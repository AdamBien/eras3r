package airhacks.eras3r.boundary;

import airhacks.Eras3r.Mode;
import airhacks.eras3r.control.BucketRemover;
import airhacks.eras3r.control.BucketsDiscoverer;
import airhacks.eras3r.control.DataExpirer;
import airhacks.eras3r.control.Log;
import airhacks.eras3r.control.ObjectsRemover;
import software.amazon.awssdk.services.s3.S3Client;

public interface BucketEraser {
    static boolean isDeleteBucketsWithNameContaining(String bucketName) {
        return bucketName.startsWith("**") && bucketName.endsWith("**");
    }

   

    static void eraseBucketContents(String bucketName, Mode mode) {
        try (var client = S3Client.create()) {
            if (isDeleteBucketsWithNameContaining(bucketName)) {
                var bucketNameFragment = removeStars(bucketName);
                Log.WARNING.out("deleting process buckets matching %s".formatted(bucketNameFragment));
                deleteMultipleBucketsMatching(client, bucketNameFragment, mode);
            } else {
                Log.WARNING.out("deleting single bucket: %s".formatted(bucketName));
                deleteSingleBucket(client, bucketName, mode);
            }
        }
    }

    static void deleteSingleBucket(S3Client client, String bucketName, Mode mode) {
        if (!BucketsDiscoverer.checkExistence(client, bucketName)) {
            Log.INFO.out("bucket %s does not exist".formatted(bucketName));
            return;
        }

        if (mode.equals(Mode.EXPIRE_CONTENTS)) {
            Log.WARNING.out("expiring contents of bucket " + bucketName);
            DataExpirer.expireObjectsWithLifecycleRule(client, bucketName, 1);
            return;
        }
        if (mode.equals(Mode.DELETE_CONTENTS)) {
            Log.WARNING.out("deleting contents of bucket " + bucketName);
            ObjectsRemover.eraseBucketContents(client, bucketName);
        }
        if (mode.equals(Mode.DELETE_BUCKET)) {
            Log.WARNING.out("deleting bucket " + bucketName);
            BucketRemover.removeBucket(client, bucketName);
        }
    }

    static void deleteMultipleBucketsMatching(S3Client client, String bucketName, Mode mode) {
        BucketsDiscoverer
                .listBucketsContaining(client, bucketName)
                .forEach(currentName -> deleteSingleBucket(client, currentName, mode));
    }

    static String removeStars(String placeHolder) {
        return placeHolder
                .replace("**", "")
                .replace("\"", "");
    }

}
