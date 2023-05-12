package airhacks.eras3r.control;

import java.util.List;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteMarkerEntry;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeletedObject;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsResponse;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.ObjectVersion;
import software.amazon.awssdk.services.s3.paginators.ListObjectVersionsIterable;

public interface ObjectsRemover {

    static void eraseBucketContents(S3Client client, String bucketName, boolean deleteBucket) {
        Logging.log("deleting contents and %s bucket?: %b".formatted(bucketName, deleteBucket));
        var listRequest = ListObjectVersionsRequest
                .builder()
                .bucket(bucketName)
                .build();
        var listObjectResponse = client.listObjectVersionsPaginator(listRequest);

        listObjectResponse.stream()
                .map(ListObjectVersionsResponse::versions)
                .map(ObjectsRemover::versionsToIdentifier)
                .forEach(batch -> ObjectsRemover.deleteBatch(client, listObjectResponse, bucketName, batch));
        
    }

    private static List<ObjectIdentifier> versionsToIdentifier(List<ObjectVersion> versions){
        return versions.stream()
        .map(ObjectsRemover::toIdentifier)
        .toList();
    }

    private static void deleteBatch(S3Client client, ListObjectVersionsIterable listObjectResponse, String bucketName,List<ObjectIdentifier> s3Keys){
        Logging.info("deleting next %d objects".formatted(s3Keys.size()));
        if (s3Keys.isEmpty()) {
            Logging.info("bucket %s is empty".formatted(bucketName));
        } else {
            deleteObjects(client, bucketName, s3Keys);
        }

        var deleteMarkerKeys = listObjectResponse.deleteMarkers()
                .stream()
                .map(ObjectsRemover::toIdentifier)
                .toList();

        if (deleteMarkerKeys.isEmpty()) {
            Logging.info("no delete markers are available in bucket: %s".formatted(bucketName));
        } else {
            Logging.info("removing %d delete marker keys".formatted(deleteMarkerKeys.size()));
            deleteObjects(client, bucketName, deleteMarkerKeys);
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
