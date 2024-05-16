package com.os.mmu;


public class Main {
    public static void main(String[] args) {

        // long size = Long.parseLong(args[0]), unit = Long.parseLong(args[1]);
        long size = 10, unit = 1;
        int strategy = 2;
        REPL repl = new REPL(size, unit, strategy);

        repl.run();

    }
}
