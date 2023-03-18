package airhacks.eras3r.control;

import java.util.List;
import java.util.stream.Stream;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

public interface BucketsDiscoverer {

    static boolean bucketExists(S3Client client, String bucketName) {
        return listBuckets(client)
                .filter(currentName -> currentName.equals(bucketName))
                .findAny()
                .isPresent();
    }

    static boolean checkExistence(S3Client client, String bucketName) {
        var headBucketRequest = HeadBucketRequest.builder().bucket(bucketName).build();
        try {
            var response = client.headBucket(headBucketRequest);
            return response.sdkHttpResponse().statusCode() == 200;
        } catch (NoSuchBucketException ex) {
            return false;
        }
    }

    static Stream<String> listBuckets(S3Client client) {
        return client.listBuckets()
                .buckets()
                .stream()
                .map(Bucket::name);
    }

    static Stream<String> listBucketsContaining(S3Client client, String bucketName) {
        return listBuckets(client)
                .peek(Logging::debug)
                .filter(currentName -> currentName.contains(bucketName));
    }
}
