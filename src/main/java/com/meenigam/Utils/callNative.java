package com.meenigam.Utils;

public class callNative {
    static {
        System.loadLibrary("native-lib");
    }
    public native int[] giveVector();
    public static void main(String[] args) {
        new callNative().giveVector();
    }
}
