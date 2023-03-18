package airhacks;

import java.util.Optional;

import airhacks.eras3r.boundary.BucketEraser;

/**
 *
 * @author airhacks.com
 */
interface Eras3r {

    static boolean invalidArguments(String... args) {
        if (args.length == 0 || args.length > 2) {
            System.out.println("invoke with arguments: [bucketname] [true]");
            return true;
        }
        return false;
    }

    static boolean isBucketDeletion(String... args) {
        if (args.length <= 1)
            return false;
        return Boolean.parseBoolean(args[1]);
    }

    static String bucketName(String... args) {
        return args[0];
    }

    static void main(String... args) {
        if (invalidArguments(args))
            return;
        var bucketName = bucketName(args);
        var deleteBucket = isBucketDeletion(args);
        BucketEraser.eraseBucketContents(bucketName, deleteBucket);
    }
}
