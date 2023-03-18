package airhacks.eras3r.boundary;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketVersioningStatus;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.PutBucketVersioningRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.VersioningConfiguration;

public class BucketEraserIT {

    static S3Client client = S3Client.create();
    static String bucketName;

    BucketEraser eraser;

    @BeforeAll
    static void createBucket() {
        bucketName = "airhacks-" + System.currentTimeMillis();

        var response = client
                .createBucket(CreateBucketRequest
                        .builder()
                        .bucket(bucketName)
                        .build());

        var status = response.sdkHttpResponse().statusCode();
        System.out.println(status);
        var versioningRequest = PutBucketVersioningRequest
                .builder()
                .bucket(bucketName)
                .versioningConfiguration(VersioningConfiguration
                        .builder()
                        .status(BucketVersioningStatus.ENABLED)
                        .build())
                .build();

        client.putBucketVersioning(versioningRequest);

        var request = PutObjectRequest
                .builder()
                .bucket(bucketName)
                .key("duke")
                .build();
        client.putObject(request, RequestBody.fromBytes("java".getBytes()));
        client.putObject(request, RequestBody.fromBytes("java 2".getBytes()));
    }

    @BeforeEach
    void init() {
        eraser = new BucketEraser();
    }

    @Test
    void testEraseBucketContents() {
        this.eraser.eraseBucketContents(bucketName);
    }

    @AfterAll
    static void deleteBucket(){
        var deleteRequest = DeleteBucketRequest.builder().bucket(bucketName).build();
        client.deleteBucket(deleteRequest);
    }
}
