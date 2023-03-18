package airhacks.eras3r.control;

public interface Logging {
    static void log(String message) {
        System.out.println(message);
    }

    static void debug(String message) {
        log(message);
    }

    static void log(Object message) {
        System.out.println(message);
    }

    static void info(String message) {
        log(message);
    }

}
