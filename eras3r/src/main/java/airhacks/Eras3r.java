package airhacks;

import java.util.Optional;
import java.util.stream.Stream;

import airhacks.eras3r.boundary.BucketEraser;
import airhacks.eras3r.control.Log;

/**
 *
 * @author airhacks.com
 */
public interface Eras3r {

    enum Mode{
        DELETE_CONTENTS,
        DELETE_BUCKET,
        EXPIRE_CONTENTS
    }
    int PARALLELISM = 10;
    String REMOVE_BUCKET = "--remove-bucket";
    String EXPIRE_CONTENTS = "--expire-contents";

    static boolean invalidArguments(String... args) {
        if (args.length == 0 || args.length > 2) {
            Log.INFO.out(
            """
                invoke with arguments: [bucketname] [%s] [%s]
                use "**[partial bucket name]**" to delete multiple buckets
            """.formatted(REMOVE_BUCKET,EXPIRE_CONTENTS));
            return true;
        }
        return false;
    }


    static Eras3r.Mode deletionMode(String... args) {
        if (args.length <= 1)
            return Mode.DELETE_CONTENTS;
        var command = args[1];
        Log.INFO.out("parameter for bucket removal: " + command);
        var expiration = Stream
                .of(args)
                .filter(arg -> arg.equals(EXPIRE_CONTENTS))
                .findAny()
                .isPresent();
        if (expiration) {
            Log.WARNING.out("deleting bucket's contents");
            return Mode.EXPIRE_CONTENTS;

        }
        var removal = Stream
                .of(args)
                .filter(arg -> arg.equals(REMOVE_BUCKET))
                .findAny()
                .isPresent();
        if (removal) {
            Log.WARNING.out("bucket is going to be deleted");
            return Mode.DELETE_BUCKET;
        } 
        return Mode.DELETE_CONTENTS;
    }

    static Optional<String> bucketName(String... args) {
        return Stream
                .of(args)
                .filter(arg -> !arg.startsWith("--"))
                .findFirst();
    }

    static void main(String... args) {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", String.valueOf(PARALLELISM));
        Log.INFO.out("eras3r v0.0.16");
        if (invalidArguments(args))
            return;
        var bucketName = bucketName(args);
        if (bucketName.isEmpty()) {
            Log.WARNING.out("bucket name is not specified");
            return;
        }
        var deleteBucket = deletionMode(args);
        BucketEraser.eraseBucketContents(bucketName.get(), deleteBucket);
    }
}
