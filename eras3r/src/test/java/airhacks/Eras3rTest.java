package airhacks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import airhacks.Eras3r.Mode;

public class Eras3rTest {
    @Test
    void isBucketDeletion() {
        assertThat(Eras3r.deletionMode("bucketname","--remove-bucket")).isEqualTo(Mode.DELETE_BUCKET);
        assertThat(Eras3r.deletionMode("--remove-bucket","bucketname")).isEqualTo(Mode.DELETE_BUCKET);
        assertThat(Eras3r.deletionMode("bucketname","false")).isEqualTo(Mode.DELETE_CONTENTS);
        assertThat(Eras3r.deletionMode("bucketname","anything")).isEqualTo(Mode.DELETE_CONTENTS);
        assertThat(Eras3r.deletionMode("bucketname","anything")).isEqualTo(Mode.DELETE_CONTENTS);
    }

    @Test
    void isExpiration() {
        assertThat(Eras3r.deletionMode("bucketname","--expire-contents")).isEqualTo(Mode.EXPIRE_CONTENTS);

    }

    @Test
    void bucketName() {
        var expected = "duke";
        var actual = Eras3r.bucketName(expected,"something else");
        assertThat(actual).isNotEmpty();
        assertThat(actual.get()).isEqualTo(expected);
    }

}
