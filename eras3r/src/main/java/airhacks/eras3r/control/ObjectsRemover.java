package airhacks.eras3r.control;

import java.util.List;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteMarkerEntry;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeletedObject;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.ObjectVersion;

public interface ObjectsRemover {

    static void eraseBucketContents(S3Client client, String bucketName, boolean deleteBucket) {
        Logging.log("deleting contents and %s bucket?: %b".formatted(bucketName, deleteBucket));
        var listRequest = ListObjectVersionsRequest
                .builder()
                .bucket(bucketName)
                .build();
        var listObjectResponse = client.listObjectVersions(listRequest);
        var s3Keys = listObjectResponse
                .versions()
                .stream()
                .limit(1000)
                .map(ObjectsRemover::toIdentifier)
                .toList();

        if (s3Keys.isEmpty()) {
            System.out.println("bucket %s is empty".formatted(bucketName));
        } else {
            deleteObjects(client, bucketName, s3Keys);
        }

        s3Keys = listObjectResponse.deleteMarkers()
                .stream()
                .map(ObjectsRemover::toIdentifier)
                .peek(Logging::log)
                .toList();

        if (s3Keys.isEmpty()) {
            System.out.println("no delete markers are available in bucket: %s".formatted(bucketName));
        } else {
            deleteObjects(client, bucketName, s3Keys);
        }

    }

    private static void deleteObjects(S3Client client, String bucketName, List<ObjectIdentifier> s3Keys) {
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

    private static ObjectIdentifier toIdentifier(ObjectVersion version) {
        return ObjectIdentifier.builder()
                .key(version.key())
                .versionId(version.versionId())
                .build();
    }

    private static ObjectIdentifier toIdentifier(DeleteMarkerEntry version) {
        return ObjectIdentifier.builder()
                .key(version.key())
                .versionId(version.versionId())
                .build();
    }

}
