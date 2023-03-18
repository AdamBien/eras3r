package airhacks;

import airhacks.eras3r.boundary.BucketEraser;

/**
 *
 * @author airhacks.com
 */
interface App {


    static void main(String... args) {
        var bucketName = args[0];
        BucketEraser.eraseBucketContents(bucketName);
    }
}
