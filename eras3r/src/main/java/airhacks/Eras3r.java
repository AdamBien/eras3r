package airhacks;

import airhacks.eras3r.boundary.BucketEraser;

/**
 *
 * @author airhacks.com
 */
interface Eras3r {

    static void usage(){
        System.out.println("invoke with arguments: [bucketname] [true]");
    }

    static boolean isBucketDeletion(String... args) {
        if (args.length <= 1)
            return false;
        return Boolean.parseBoolean(args[1]);
    }

    static void main(String... args) {
        var bucketName = args[0];
        var deleteBucket = isBucketDeletion(args);
        BucketEraser.eraseBucketContents(bucketName,deleteBucket);
    }
}
