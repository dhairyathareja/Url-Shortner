package com.aws.ec2.ec2_sdk.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

@RestController
@RequestMapping("/ebs")
public class EbsController {

    private final Ec2Client ec2Client;

    public EbsController() {
        this.ec2Client = Ec2Client.create();
    }

    @PostMapping("/create-attach")
    public ResponseEntity<String> createAndAttachEBS(
            @RequestParam String instanceId,
            // @RequestParam String availabilityZone,
            @RequestParam int volumeSize
    ) {
        try {
            // Step 1: Create EBS Volume
            CreateVolumeRequest createRequest = CreateVolumeRequest.builder()
                    .availabilityZone("ap-sout-1b")
                    .size(volumeSize)
                    .volumeType(VolumeType.GP2)
                    .build();

            CreateVolumeResponse createResponse = ec2Client.createVolume(createRequest);
            String volumeId = createResponse.volumeId();

            // Step 2: Wait for Volume to be Available
            ec2Client.waiter().waitUntilVolumeAvailable(DescribeVolumesRequest.builder()
                    .volumeIds(volumeId)
                    .build());

            // Step 3: Attach Volume to EC2 Instance
            AttachVolumeRequest attachRequest = AttachVolumeRequest.builder()
                    .volumeId(volumeId)
                    .instanceId(instanceId)
                    .device("/dev/xvdf") // Change if needed
                    .build();

            ec2Client.attachVolume(attachRequest);

            return ResponseEntity.ok("EBS Volume " + volumeId + " created and attached to instance " + instanceId);

        } catch (Ec2Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
