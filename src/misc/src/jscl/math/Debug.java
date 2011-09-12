package jscl.math;

import java.io.PrintStream;

public class Debug {
    static PrintStream out;
    static int indentation;

    private Debug() {}

    public static void println(Object x) {
        if(out==null);
        else {
            for(int i=0;i<indentation;i++) out.print(' ');
            out.println(x);
        }
    }

    public static void setOutputStream(PrintStream out) {
        Debug.out=out;
    }

    public static PrintStream getOutputStream() {
        return out;
    }

    public static void increment() {
        indentation++;
    }

    public static void decrement() {
        indentation--;
    }

    public static void reset() {
        indentation=0;
    }
}
