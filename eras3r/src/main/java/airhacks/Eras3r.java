package airhacks;

import java.util.Optional;
import java.util.stream.Stream;

import airhacks.eras3r.boundary.BucketEraser;
import airhacks.eras3r.control.Log;

/**
 *
 * @author airhacks.com
 */
interface Eras3r {
    static final String REMOVE_BUCKET = "--remove-bucket";

    static boolean invalidArguments(String... args) {
        if (args.length == 0 || args.length > 2) {
            Log.INFO.out(
            """
                invoke with arguments: [bucketname] [%s]
                use "**[partial bucket name]**" to delete multiple buckets
            """.formatted(REMOVE_BUCKET));
            return true;
        }
        return false;
    }

    static boolean isBucketDeletion(String... args) {
        if (args.length <= 1)
            return false;
        var command = args[1];
        Log.INFO.out("parameter for bucket removal: " + command);
        var removal = Stream
                .of(args)
                .filter(arg -> arg.equals(REMOVE_BUCKET))
                .findAny()
                .isPresent();
        if (removal) {
            Log.WARNING.out("bucket is going to be deleted");
        } else {
            Log.WARNING.out("deleting bucket's contents");
        }
        return removal;
    }

    static Optional<String> bucketName(String... args) {
        return Stream
                .of(args)
                .filter(arg -> !arg.startsWith("--"))
                .findFirst();
    }

    static void main(String... args) {
        Log.INFO.out("eras3r v0.0.11");
        if (invalidArguments(args))
            return;
        var bucketName = bucketName(args);
        if (bucketName.isEmpty()) {
            Log.WARNING.out("bucket name is not specified");
            return;
        }
        var deleteBucket = isBucketDeletion(args);
        BucketEraser.eraseBucketContents(bucketName.get(), deleteBucket);
    }
}
