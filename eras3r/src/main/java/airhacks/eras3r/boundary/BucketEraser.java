package airhacks.eras3r.boundary;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Object;

public class BucketEraser {

    S3Client client;

    public BucketEraser() {
        this.client = S3Client.create();
    }

    public void eraseBucketContents(String bucketName) {
        var listRequest = ListObjectsV2Request.builder().bucket(bucketName).build();
        var s3Objects = this.client.listObjectsV2(listRequest).contents();

        var s3Keys = s3Objects
                .stream()
                .limit(1000)
                .map(S3Object::key)
                .peek(this::log)
                .map(this::toIdentifier)
                .toList();
        if(s3Keys.isEmpty()){
            System.out.println("bucket %s is empty".formatted(bucketName));
            return;
        }

        var delete = Delete.builder()
                .objects(s3Keys)
                .build();
                
        var deleteRequest = DeleteObjectsRequest
                .builder()
                .bucket(bucketName)
                .delete(delete)
                .build();

        this.client.deleteObjects(deleteRequest);
    }

    ObjectIdentifier toIdentifier(String objectName) {
        return ObjectIdentifier.builder()
                .key(objectName)
                .build();
    }

    public void deleteObject(String bucketName, String key) {
        var deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        this.client.deleteObject(deleteRequest);
    }

    void log(String message){
        System.out.println(message);
    }

}
