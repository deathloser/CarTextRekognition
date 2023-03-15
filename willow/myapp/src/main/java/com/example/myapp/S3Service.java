 package com.example.myapp;

 import software.amazon.awssdk.core.ResponseBytes;
 import software.amazon.awssdk.regions.Region;
 import software.amazon.awssdk.services.s3.S3Client;
 import software.amazon.awssdk.services.s3.model.GetObjectRequest;
 import software.amazon.awssdk.services.s3.model.PutObjectTaggingRequest;
 import software.amazon.awssdk.services.s3.model.GetObjectResponse;
 import software.amazon.awssdk.services.s3.model.S3Exception;
 import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
 import software.amazon.awssdk.services.s3.model.S3Object;
 import software.amazon.awssdk.services.s3.model.GetObjectTaggingResponse;
 import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
 import java.util.ArrayList;
 import java.util.List;
 import software.amazon.awssdk.services.s3.model.Tagging;
 import software.amazon.awssdk.services.s3.model.Tag;
 import software.amazon.awssdk.services.s3.model.GetObjectTaggingRequest;
 import software.amazon.awssdk.services.s3.model.DeleteObjectTaggingRequest;

 public class S3Service {
      private S3Client getClient() {

    Region region = Region.US_EAST_1;
    return S3Client.builder()
            .region(region)
            .build();
 }

  // Returns the names of all images in the given bucket.
 public List<String> listBucketObjects(String bucketName) {

    S3Client s3 = getClient();
    String keyName;

    List<String> keys = new ArrayList<>();

    try {
        	ListObjectsRequest listObjects = ListObjectsRequest
                .builder()
                .bucket(bucketName)
                .build();

        ListObjectsResponse res = s3.listObjects(listObjects);
        List<S3Object> objects = res.contents();

        for (S3Object myValue: objects) {
            keyName = myValue.key();
            keys.add(keyName);
        }
	return keys;

    } catch (S3Exception e) {
        System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);
    }
    return null;
 }



 }