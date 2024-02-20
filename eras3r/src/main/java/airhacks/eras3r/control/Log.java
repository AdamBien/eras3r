package airhacks.eras3r.control;



public enum Log {

    DEBUG("\033[0;90m"),
    INFO("\033[0;33m"),
    TRACE("\033[2;34m"),
    WARNING("\033[0;97m"), 
    ERROR("\033[0;41m");

    private final String value;
    private final static String RESET = "\u001B[0m";

    private Log(String value) {
        this.value = (value + "%s" + RESET);
    }

    public String formatted(String raw) {
        return this.value.formatted(raw);
    }

    public void out(String message) {
        stdout(formatted(message));
    }

    private void stdout(String message) {
        System.out.println(message);
    }
}
