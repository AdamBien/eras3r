package airhacks;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class AppTest {
    @Test
    void isBucketDeletion() {
        assertTrue(App.isBucketDeletion("bucketname","true"));
        assertFalse(App.isBucketDeletion("bucketname","false"));
        assertFalse(App.isBucketDeletion("bucketname","anything"));
    }
}
