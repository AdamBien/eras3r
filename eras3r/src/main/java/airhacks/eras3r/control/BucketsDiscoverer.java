package airhacks.eras3r.control;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;

public interface BucketsDiscoverer {

    static boolean bucketExists(S3Client client, String bucketName) {
        return client.listBuckets()
                .buckets()
                .stream()
                .map(Bucket::name)
                .filter(currentName -> currentName.equals(bucketName))
                .findAny()
                .isPresent();
    }
}
