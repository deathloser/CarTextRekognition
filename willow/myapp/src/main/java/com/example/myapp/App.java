package com.example.myapp;


import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Label;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.rekognition.model.S3Object;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.QueueNameExistsException;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import java.util.Date;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class App {
        private static final String QUEUE_NAME = "paul.fifo";

        private static final String queueUrl = "https://queue.amazonaws.com/140523324042/paul.fifo";


    public static void main(String args[]){
        SqsClient sqsClient = SqsClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        
       	String bucketname = "njit-cs-643";
        

       	System.out.println("Getting bucket items...");

        S3Service s3Service = new S3Service();
        Region region = Region.US_EAST_1;
        RekognitionClient rekClient = RekognitionClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        List<String> myKeys = s3Service.listBucketObjects(bucketname);

        System.out.println("Bucket items retrieved!");

        for (String key : myKeys) {
                    System.out.println(key);
                    getLabelsfromImage(rekClient, bucketname, key,sqsClient);
}
        rekClient.close();
}

     	public static void sendMessage(SqsClient sqsClient,String messageBody){
                System.out.println("Sending a message: "+messageBody);
                        try {

            sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
               // .messageDeduplicationId("example-group-1:1") // deduplication Id
                .messageGroupId("example-group-1") // message group Id
                .build());

        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

}


    public static void getLabelsfromImage(RekognitionClient rekClient, String bucket, String image,SqsClient sqsClient) {

        try {
            S3Object s3Object = S3Object.builder()
                .bucket(bucket)
                .name(image)
                .build();

            Image myImage = Image.builder()
                .s3Object(s3Object)
                .build();

            DetectLabelsRequest detectLabelsRequest = DetectLabelsRequest.builder()
                .image(myImage)
                .maxLabels(10)
                .build();

            DetectLabelsResponse labelsResponse = rekClient.detectLabels(detectLabelsRequest);
            List<Label> labels = labelsResponse.labels();
            System.out.println("Detected labels for the given photo");


            for (Label label: labels) {
                System.out.println(label.name() + ": " + label.confidence().toString());
//IF label.name contains "car" 
            if((label.name().equals("Car"))&&(label.confidence()>=90)){
                System.out.println("It's a car probably");
                sendMessage(sqsClient,image);

}

            }        //no more images left
        sendMessage(sqsClient,"-1");
        
       	}catch (RekognitionException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }



}

