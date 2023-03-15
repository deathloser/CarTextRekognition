package com.example.myapp;

import java.io.File;  
import java.io.FileNotFoundException; 
import java.util.Scanner; 
import java.io.FileWriter;  
import java.io.IOException;  
import java.io.PrintWriter;


import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Label;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.rekognition.model.S3Object;
import software.amazon.awssdk.services.rekognition.model.DetectTextRequest;
import software.amazon.awssdk.services.rekognition.model.DetectTextResponse;
import software.amazon.awssdk.services.rekognition.model.TextDetection;


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
import java.util.Date;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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


        //1.get the messages (file names) via ReceiveMessage
           ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(5)
                .build();
            List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();
        for(Message message : messages){
                System.out.println("The message body is: "+message.body());
                getLabelsfromImage(rekClient,bucketname,message.body());
}


        rekClient.close();
}
    public static void getLabelsfromImage(RekognitionClient rekClient, String bucket, String image) {

        try {

            S3Object s3Object = S3Object.builder()
                .bucket(bucket)
                .name(image)
                .build();

            Image myImage = Image.builder()
                .s3Object(s3Object)
                .build();

            DetectTextRequest textRequest = DetectTextRequest.builder()
                .image(myImage)
                .build();

            DetectTextResponse textResponse = rekClient.detectText(textRequest);

            List<TextDetection> textCollection = textResponse.textDetections();
//            System.out.println("Detected lines and words for "+image);


        try{
	FileWriter writer = new FileWriter("output.txt");
        List<String> textList = new ArrayList<String>();
            for (TextDetection text: textCollection) {
                if(!text.detectedText().isEmpty()){
                        System.out.println("Detected a car and text in "+image);
                        writer.write("Detected a car and text in "+image);
                        System.out.println("Detected: " + text.detectedText());
                        System.out.println("Confidence: " + text.confidence().toString());
                        System.out.println(image+": "+text.detectedText()+"\n");

                        System.out.println(image+": "+text.detectedText()+"\n");
                        textList.add(image+": "+text.detectedText());
                        }
                if(image="-1"){
                        System.out.println("There are no more images");
        }
        
            }writer.close();

            for(String text: textList) {
                System.out.println("The array "+text);
}
}
        catch(IOException e){
                System.out.println("There was an error");e.printStackTrace();}


        }catch (RekognitionException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}