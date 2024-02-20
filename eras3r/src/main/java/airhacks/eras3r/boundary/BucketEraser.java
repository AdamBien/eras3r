package airhacks.eras3r.boundary;

import airhacks.eras3r.control.BucketRemover;
import airhacks.eras3r.control.BucketsDiscoverer;
import airhacks.eras3r.control.Log;
import airhacks.eras3r.control.ObjectsRemover;
import software.amazon.awssdk.services.s3.S3Client;

public interface BucketEraser {
    static boolean isDeleteBucketsWithNameContaining(String bucketName) {
        return bucketName.startsWith("\"**") && bucketName.endsWith("**\"");
    }

    static void eraseBucketContents(String bucketName, boolean deleteBucket) {
        try (var client = S3Client.create()) {
            if (isDeleteBucketsWithNameContaining(bucketName)) {
                var bucketNameFragment = removeStars(bucketName);
                Log.WARNING.out("deleting multiple buckets matching %s".formatted(bucketNameFragment));
                deleteMultipleBucketsMatching(client, bucketNameFragment, deleteBucket);

            } else {
                Log.WARNING.out("deleting single bucket: %s".formatted(bucketName));
                deleteSingleBucket(client, bucketName, deleteBucket);
            }
        }
    }

    static void deleteSingleBucket(S3Client client, String bucketName, boolean deleteBucket) {
        if (!BucketsDiscoverer.checkExistence(client, bucketName)) {
            Log.INFO.out("bucket %s does not exist".formatted(bucketName));
            return;
        }
        ObjectsRemover.eraseBucketContents(client, bucketName, deleteBucket);
        if (deleteBucket) {
            Log.INFO.out("deleting bucket " + bucketName);
            BucketRemover.removeBucket(client, bucketName);
        }
    }

    static void deleteMultipleBucketsMatching(S3Client client, String bucketName, boolean deleteBucket) {
        BucketsDiscoverer
                .listBucketsContaining(client, bucketName)
                .forEach(currentName -> deleteSingleBucket(client, currentName, deleteBucket));
    }

    static String removeStars(String placeHolder) {
        return placeHolder
                .replace("**", "")
                .replace("\"", "");
    }

}
