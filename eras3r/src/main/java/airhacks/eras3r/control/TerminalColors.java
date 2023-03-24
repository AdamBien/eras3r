package airhacks.eras3r.control;

/**
 * https://en.wikipedia.org/wiki/ANSI_escape_code#Colors
 * 
 * @author airhacks.com
 */
public enum TerminalColors {

    INFO("\u001B[34m"), 
    WARNING("\033[1;90m"), 
    RESET("\u001B[0m");

    private final String code;

    private TerminalColors(String value) {
        this.code =  value;
    }

    public String code() {
        return this.code;
    }
}
