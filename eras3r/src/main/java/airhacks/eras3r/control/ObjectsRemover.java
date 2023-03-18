package airhacks.eras3r.control;

import airhacks.eras3r.boundary.BucketEraser;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeletedObject;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Object;

public interface ObjectsRemover {

    static void eraseBucketContents(S3Client client, String bucketName, boolean deleteBucket) {
        Logging.log("deleting contents and %s bucket?: %b".formatted(bucketName, deleteBucket));
        var listRequest = ListObjectsV2Request
                .builder()
                .bucket(bucketName)
                .build();
        var s3Objects = client
                .listObjectsV2(listRequest)
                .contents();

        var s3Keys = s3Objects
                .stream()
                .limit(1000)
                .map(S3Object::key)
                .peek(Logging::log)
                .map(ObjectsRemover::toIdentifier)
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
        Logging.log("objects deleted");

        response.deleted()
                .stream()
                .map(DeletedObject::key)
                .forEach(Logging::log);
    }

    private static ObjectIdentifier toIdentifier(String objectName) {
        return ObjectIdentifier.builder()
                .key(objectName)
                .build();
    }

}
