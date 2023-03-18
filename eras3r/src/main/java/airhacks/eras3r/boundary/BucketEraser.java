package airhacks.eras3r.boundary;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeletedObject;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Object;

public interface BucketEraser {

    static void eraseBucketContents(String bucketName,boolean deleteBucket) {
        try (var client = S3Client.create()) {
            var listRequest = ListObjectsV2Request.builder().bucket(bucketName).build();
            var s3Objects = client.listObjectsV2(listRequest).contents();

            var s3Keys = s3Objects
                    .stream()
                    .limit(1000)
                    .map(S3Object::key)
                    .peek(BucketEraser::log)
                    .map(BucketEraser::toIdentifier)
                    .toList();
            if (s3Keys.isEmpty()) {
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

            var response = client.deleteObjects(deleteRequest);
            log("deleted objects");

            response.deleted()
                    .stream()
                    .map(DeletedObject::key)
                    .forEach(BucketEraser::log);
        }
    }

    private static ObjectIdentifier toIdentifier(String objectName) {
        return ObjectIdentifier.builder()
                .key(objectName)
                .build();
    }

    private static void log(String message) {
        System.out.println(message);
    }

}
