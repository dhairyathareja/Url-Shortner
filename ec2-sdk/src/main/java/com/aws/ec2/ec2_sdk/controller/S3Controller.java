package com.aws.ec2.ec2_sdk.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@RestController
@RequestMapping("/s3")
public class S3Controller {

    private final S3Client s3Client;
    private final Ec2Client ec2Client;
    private final IamClient iamClient;

    public S3Controller() {
        this.s3Client = S3Client.builder().build();   
        this.ec2Client = Ec2Client.builder().build(); 
        this.iamClient = IamClient.builder().build();
    }

    @PostMapping("/create-attach")
    public ResponseEntity<String> createS3AndAttachToEC2(
            @RequestParam String bucketName,
            @RequestParam String instanceId,
            @RequestParam String roleName
    ) {
        try {
            // Step 1: Create an S3 bucket
            s3Client.createBucket(CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build());

            // Step 2: Create IAM Role for EC2 Instance
            CreateRoleResponse roleResponse = iamClient.createRole(CreateRoleRequest.builder()
                    .roleName(roleName)
                    .assumeRolePolicyDocument("{\"Version\": \"2012-10-17\", \"Statement\": [{\"Effect\": \"Allow\", \"Principal\": {\"Service\": \"ec2.amazonaws.com\"}, \"Action\": \"sts:AssumeRole\"}]}")
                    .build());

            // Step 3: Attach S3 Policy to the Role
            String policyArn = "arn:aws:iam::aws:policy/AmazonS3FullAccess";
            iamClient.attachRolePolicy(AttachRolePolicyRequest.builder()
                    .roleName(roleName)
                    .policyArn(policyArn)
                    .build());

            // Step 4: Create Instance Profile & Attach Role
            iamClient.createInstanceProfile(CreateInstanceProfileRequest.builder()
                    .instanceProfileName(roleName)
                    .build());

            iamClient.addRoleToInstanceProfile(AddRoleToInstanceProfileRequest.builder()
                    .instanceProfileName(roleName)
                    .roleName(roleName)
                    .build());

            // Step 5: Attach IAM Role to EC2 Instance
            ec2Client.associateIamInstanceProfile(AssociateIamInstanceProfileRequest.builder()
                    .iamInstanceProfile(IamInstanceProfileSpecification.builder().name(roleName).build())
                    .instanceId(instanceId)
                    .build());

            return ResponseEntity.ok("S3 bucket " + bucketName + " created and attached to EC2 instance " + instanceId);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
