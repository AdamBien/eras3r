package airhacks;

import java.util.Optional;
import java.util.stream.Stream;

import airhacks.eras3r.boundary.BucketEraser;
import airhacks.eras3r.control.Logging;

/**
 *
 * @author airhacks.com
 */
interface Eras3r {
    static final String REMOVE_BUCKET = "--remove-bucket";

    static boolean invalidArguments(String... args) {
        if (args.length == 0 || args.length > 2) {
            System.out.println("invoke with arguments: [bucketname] [%s]".formatted(REMOVE_BUCKET));
            return true;
        }
        return false;
    }

    static boolean isBucketDeletion(String... args) {
        if (args.length <= 1)
            return false;
        var command = args[1];
        Logging.info("parameter for bucket removal: " + command);
        var removal = Stream
                .of(args)
                .filter(arg -> arg.equals(REMOVE_BUCKET))
                .findAny()
                .isPresent();
        if (removal) {
            Logging.warning("bucket is going to be deleted");
        } else {
            Logging.warning("deleting bucket's contents");
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
        Logging.info("eras3r v0.0.6");
        if (invalidArguments(args))
            return;
        var bucketName = bucketName(args);
        if (bucketName.isEmpty()) {
            Logging.warning("bucket name is not specified");
            return;
        }
        var deleteBucket = isBucketDeletion(args);
        BucketEraser.eraseBucketContents(bucketName.get(), deleteBucket);
    }
}
