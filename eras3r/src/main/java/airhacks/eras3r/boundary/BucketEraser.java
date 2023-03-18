package airhacks.eras3r.boundary;

import airhacks.eras3r.control.BucketRemover;
import airhacks.eras3r.control.BucketsDiscoverer;
import airhacks.eras3r.control.Logging;
import airhacks.eras3r.control.ObjectsRemover;
import software.amazon.awssdk.services.s3.S3Client;

public interface BucketEraser {

    static void eraseBucketContents(String bucketName, boolean deleteBucket) {
        try (var client = S3Client.create()) {
            if(!BucketsDiscoverer.bucketExists(client, bucketName)){
                Logging.log("bucket %s does not exist".formatted(bucketName));
                return;
            }
            ObjectsRemover.eraseBucketContents(client, bucketName, deleteBucket);
            if (deleteBucket){
                Logging.log("deleting bucket "+ bucketName);
                BucketRemover.removeBucket(client, bucketName);
            }
        }
    }

}
