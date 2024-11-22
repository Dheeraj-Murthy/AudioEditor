package com.meenigam.Utils;

public class callNative {
    static {
        System.loadLibrary("native-lib");
    }

    public static void main(String[] args) {
        new callNative().giveVector();
    }

    public native int[] giveVector();
}
