package com.meenigam.Utils;

public class callNative {
    static {
        System.loadLibrary("native");
    }

    public static void main(String[] args) {
//        new callNative().sayHello("aloo", 8, new String[]{"yo", "uo"});
//        callCode("");
        callCode("/Users/dheerajmurthy/Desktop/ProjectFiles/finalFile.wav", 8, new String[]{"aloo"});
    }

    public static void callCode(String message, int operation, String[] params) {
        new callNative().sayHello(message,operation, params);

    }

    private native void sayHello(String msg, int operation, String[] params);
}
