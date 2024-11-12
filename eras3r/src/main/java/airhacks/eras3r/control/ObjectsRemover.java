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

    public static final int MAX_ELEMENTS = 999;

    static void eraseBucketContents(S3Client client, String bucketName) {
        var listRequest = ListObjectVersionsRequest
                .builder()
                .bucket(bucketName)
                .build();
        var listObjectResponse = client.listObjectVersionsPaginator(listRequest);

        listObjectResponse
                .stream()
                .parallel()
                .limit(MAX_ELEMENTS)
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
        Log.INFO.out("deleting next %d objects".formatted(s3Keys.size()));
        if (s3Keys.isEmpty()) {
            Log.INFO.out("bucket %s is empty".formatted(bucketName));
        } else {
            deleteObjects(client, bucketName, s3Keys);
        }

        var deleteMarkerKeys = listObjectResponse
                .deleteMarkers()
                .stream()
                .parallel()
                .limit(MAX_ELEMENTS)
                .map(ObjectsRemover::toIdentifier)
                .toList();

        if (deleteMarkerKeys.isEmpty()) {
            Log.INFO.out("no delete markers are available in bucket: %s".formatted(bucketName));
        } else {
            Log.INFO.out("removing %d delete marker keys".formatted(deleteMarkerKeys.size()));
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
        Log.INFO.out("objects deleted");

        response.deleted()
                .stream()
                .map(DeletedObject::key)
                .forEach(Log.INFO::out);

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
