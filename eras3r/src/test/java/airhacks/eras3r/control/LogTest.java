package airhacks.eras3r.control;

import org.junit.jupiter.api.Test;

public class LogTest {

    @Test
    public void allColors() {
        Log.DEBUG.out("debug");
        Log.TRACE.out("trace");
        Log.INFO.out("info");
        Log.WARNING.out("warning");
        Log.ERROR.out("error");
    }
}
