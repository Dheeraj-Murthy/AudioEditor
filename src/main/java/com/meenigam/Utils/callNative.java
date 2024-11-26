package com.meenigam.Utils;

public class callNative {
    static {
        System.loadLibrary("native");
    }

    public static void main(String[] args) {
        new callNative().sayHello("woow");
    }

    private void sayHello(String woow) {
    }
}
