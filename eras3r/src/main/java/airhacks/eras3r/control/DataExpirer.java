package airhacks.eras3r.control;

import java.util.List;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.AbortIncompleteMultipartUpload;
import software.amazon.awssdk.services.s3.model.BucketLifecycleConfiguration;
import software.amazon.awssdk.services.s3.model.ExpirationStatus;
import software.amazon.awssdk.services.s3.model.LifecycleExpiration;
import software.amazon.awssdk.services.s3.model.LifecycleRule;
import software.amazon.awssdk.services.s3.model.LifecycleRuleFilter;
import software.amazon.awssdk.services.s3.model.NoncurrentVersionExpiration;

public interface DataExpirer {

     public static void expireObjectsWithLifecycleRule(S3Client client, String bucketName, int expirationDays) {
        var matchAllFilter = LifecycleRuleFilter.builder().build();
        var objectExpirationRule = LifecycleRule.builder()
                .id("ExpirationRule")
                .status(ExpirationStatus.ENABLED)
                .filter(matchAllFilter)
                .expiration(LifecycleExpiration.builder()
                        .days(expirationDays)
                        .build())
                .noncurrentVersionExpiration(NoncurrentVersionExpiration.builder()
                        .noncurrentDays(expirationDays)
                        .build())
                .abortIncompleteMultipartUpload(AbortIncompleteMultipartUpload.builder()
                        .daysAfterInitiation(expirationDays)
                        .build())
                .build();

        /**
         * delete markers cannot be specified with expiration period
         * therefore a standalone rule is required
         */
        var deleteMarkersRule = LifecycleRule.builder()
                .id("DeleteMarkersExpirationRule")
                .status(ExpirationStatus.ENABLED)
                .filter(LifecycleRuleFilter.builder().build())
                .expiration(LifecycleExpiration.builder()
                        .expiredObjectDeleteMarker(true)
                        .build())
                .build();

        var lifecycleConfig = BucketLifecycleConfiguration.builder()
                .rules(List.of(objectExpirationRule, deleteMarkersRule))
                .build();

        client.putBucketLifecycleConfiguration(req -> req
                .bucket(bucketName)
                .lifecycleConfiguration(lifecycleConfig));
    }
}
