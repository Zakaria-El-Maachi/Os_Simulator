package com.os.mmu;


public class Main {
    public static void main(String[] args) {

        long size = Long.parseLong(args[0]), unit = Long.parseLong(args[1]);
        int strategy = Integer.parseInt(args[2]);
        REPL repl = new REPL(size, unit, strategy);

        repl.run();

    }
}
