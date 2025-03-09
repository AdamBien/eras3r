package airhacks.eras3r.boundary;

import java.util.List;

import airhacks.Eras3r.Mode;
import airhacks.eras3r.control.BucketRemover;
import airhacks.eras3r.control.BucketsDiscoverer;
import airhacks.eras3r.control.Log;
import airhacks.eras3r.control.ObjectsRemover;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketLifecycleConfiguration;
import software.amazon.awssdk.services.s3.model.ExpirationStatus;
import software.amazon.awssdk.services.s3.model.LifecycleExpiration;
import software.amazon.awssdk.services.s3.model.LifecycleRule;
import software.amazon.awssdk.services.s3.model.LifecycleRuleFilter;
import software.amazon.awssdk.services.s3.model.NoncurrentVersionExpiration;

public interface BucketEraser {
    static boolean isDeleteBucketsWithNameContaining(String bucketName) {
        return bucketName.startsWith("**") && bucketName.endsWith("**");
    }

    public static void expireObjectsWithLifecycleRule(S3Client client, String bucketName, int expirationDays) {
        var lifecycleRule = LifecycleRule.builder()
                .id("ExpirationRule")
                .status(ExpirationStatus.ENABLED)
                .filter(LifecycleRuleFilter.builder().build())
                .expiration(LifecycleExpiration.builder()
                        .days(expirationDays)
                        .build())
                .noncurrentVersionExpiration(NoncurrentVersionExpiration.builder()
                        .noncurrentDays(expirationDays)
                        .build())
                .build();

        var lifecycleConfig = BucketLifecycleConfiguration.builder()
                .rules(List.of(lifecycleRule))
                .build();

        client.putBucketLifecycleConfiguration(req -> req
                .bucket(bucketName)
                .lifecycleConfiguration(lifecycleConfig));
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
            Log.INFO.out("expiring contents of bucket " + bucketName);
            expireObjectsWithLifecycleRule(client, bucketName, 1);
            return;
        }
        if (mode.equals(Mode.DELETE_CONTENTS)) {
            Log.INFO.out("deleting contents of bucket " + bucketName);
            ObjectsRemover.eraseBucketContents(client, bucketName);
        }
        if (mode.equals(Mode.DELETE_BUCKET)) {
            Log.INFO.out("deleting bucket " + bucketName);
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
