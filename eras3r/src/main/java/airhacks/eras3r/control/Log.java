package airhacks.eras3r.control;

import java.io.PrintStream;

public enum Log {

    DEBUG("\033[0;90m",System.out),
    INFO("\033[0;33m",System.out),
    TRACE("\033[2;34m",System.out),
    WARNING("\033[1;31m",System.out), 
    ERROR("\033[0;41m",System.err);

    private final String template;
    private PrintStream out;
    private final static String RESET = "\u001B[0m";

    private Log(String level,PrintStream out) {
        this.template = (level + "%s" + RESET);
        this.out = out;

    }

    public void out(String message) {
        var colored = this.template.formatted(message);
        out.println(colored);
    }

}
