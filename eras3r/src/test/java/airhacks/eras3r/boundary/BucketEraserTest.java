package airhacks.eras3r.boundary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class BucketEraserTest {

    @Test
    void removeStars(){
        var input = "**airhacks-**";
        var expected = "airhacks-";
        var actual = BucketEraser.removeStars(input);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void removisDeleteBucketsWithNameeStars(){
        assertFalse(BucketEraser.isDeleteBucketsWithNameContaining("duke"));
        assertTrue(BucketEraser.isDeleteBucketsWithNameContaining("\"**duke**\""));
    }

}
