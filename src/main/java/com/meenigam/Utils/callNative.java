package com.meenigam.Utils;

public class callNative {
    static {
        try {
            System.loadLibrary("native");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Failed to load native library: " + e.getMessage());
            System.err.println("Attempting to load from native directory...");
            try {
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("mac")) {
                    System.load("native/libnative.dylib");
                } else {
                    System.load("native/libnative.so");
                }
                System.out.println("Native library loaded successfully from native directory");
            } catch (UnsatisfiedLinkError e2) {
                System.err.println("Failed to load native library from native directory: " + e2.getMessage());
                e2.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
//        new callNative().sayHello("aloo", 8, new String[]{"yo", "uo"});
//        callCode("");
        callCode("/Users/dheerajmurthy/Desktop/ProjectFiles/finalFile.wav", 8, new String[]{"aloo"});
    }

    public static void callCode(String message, int operation, String[] params) {
        new callNative().sayHello(message, operation, params);

    }

    private native void sayHello(String msg, int operation, String[] params);
}
