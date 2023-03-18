package airhacks;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class Eras3rTest {
    @Test
    void isBucketDeletion() {
        assertTrue(Eras3r.isBucketDeletion("bucketname","true"));
        assertFalse(Eras3r.isBucketDeletion("bucketname","false"));
        assertFalse(Eras3r.isBucketDeletion("bucketname","anything"));
    }
}
