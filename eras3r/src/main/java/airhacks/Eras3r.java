package airhacks;

import airhacks.eras3r.boundary.BucketEraser;
import airhacks.eras3r.control.Logging;

/**
 *
 * @author airhacks.com
 */
interface Eras3r {
    static final String REMOVE_BUCKET = "RB!!!";

    static boolean invalidArguments(String... args) {
        if (args.length == 0 || args.length > 2) {
            System.out.println("invoke with arguments: [bucketname] [RB!!!]");
            return true;
        }
        return false;
    }

    static boolean isBucketDeletion(String... args) {
        if (args.length <= 1)
            return false;
        var command = args[1];
        Logging.info("parameter for bucket removal: " + command);
        var removal =  REMOVE_BUCKET
                .equals(args[1]);
        if(removal){
            Logging.warning("bucket is going to be deleted");
        }else{
            Logging.warning("deleting bucket's contents");
        }
        return removal;
    }

    static String bucketName(String... args) {
        return args[0];
    }

    static void main(String... args) {
        Logging.info("eras3r v0.0.2");
        if (invalidArguments(args))
            return;
        var bucketName = bucketName(args);
        var deleteBucket = isBucketDeletion(args);
        BucketEraser.eraseBucketContents(bucketName, deleteBucket);
    }
}
