package airhacks.eras3r.control;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;

public interface BucketRemover {

    static void removeBucket(S3Client client, String bucketName) {
        var deleteBucketRequest = DeleteBucketRequest
                .builder()
                .bucket(bucketName)
                .build();
        client.deleteBucket(deleteBucketRequest);

    }

}
