package airhacks;

import airhacks.eras3r.boundary.BucketEraser;

/**
 *
 * @author airhacks.com
 */
interface App {

    static void main(String... args) {
        var bucketName = args[0];
        var eraser = new BucketEraser();
        eraser.eraseBucketContents(bucketName);
    }
}
