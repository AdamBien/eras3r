package airhacks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class Eras3rTest {
    @Test
    void isBucketDeletion() {
        assertTrue(Eras3r.isBucketDeletion("bucketname","RB!!!"));
        assertFalse(Eras3r.isBucketDeletion("bucketname","false"));
        assertFalse(Eras3r.isBucketDeletion("bucketname","anything"));
    }

    @Test
    void bucketName() {
        var expected = "duke";
        var actual = Eras3r.bucketName(expected,"something else");
        assertThat(actual).isEqualTo(expected);
    }

}
