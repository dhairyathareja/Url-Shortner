package com.aws.ec2.ec2_sdk.controller;

import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

@RestController
@RequestMapping("/ec2")
@CrossOrigin(origins = "*")  // Allow access from Postman and browser
public class EC2Controller {

    private final Ec2Client ec2Client;

    public EC2Controller() {
        this.ec2Client = Ec2Client.builder()
                .region(Region.AP_SOUTH_1) // Mumbai region
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
    }

    @PostMapping("/create")
    public String createInstance() {
        try {
            RunInstancesRequest runRequest = RunInstancesRequest.builder()
                    .imageId("ami-0e35ddab05955cf57") // Replace with valid Mumbai AMI
                    .instanceType(InstanceType.T2_MICRO)
                    .maxCount(1)
                    .minCount(1)
                    .build();

            RunInstancesResponse response = ec2Client.runInstances(runRequest);
            String instanceId = response.instances().get(0).instanceId();
            return "Instance created with ID: " + instanceId;
        } catch (Exception e) {
            return "Error creating instance: " + e.getMessage();
        }
    }

    @PostMapping("/stop/{instanceId}")
    public String stopInstance(@PathVariable String instanceId) {
        try {
            StopInstancesRequest stopRequest = StopInstancesRequest.builder()
                    .instanceIds(instanceId)
                    .build();
            ec2Client.stopInstances(stopRequest);
            return "Instance stopped: " + instanceId;
        } catch (Exception e) {
            return "Error stopping instance: " + e.getMessage();
        }
    }

    @DeleteMapping("/terminate/{instanceId}")
    public String terminateInstance(@PathVariable String instanceId) {
        try {
            TerminateInstancesRequest terminateRequest = TerminateInstancesRequest.builder()
                    .instanceIds(instanceId)
                    .build();
            ec2Client.terminateInstances(terminateRequest);
            return "Instance terminated: " + instanceId;
        } catch (Exception e) {
            return "Error terminating instance: " + e.getMessage();
        }
    }
}
