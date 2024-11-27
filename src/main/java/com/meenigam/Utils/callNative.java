package com.meenigam.Utils;

public class callNative {
    static {
        System.loadLibrary("native");
    }

    public static void main(String[] args) {
        new callNative().sayHello("woow");
        callCode("/home/mjthegreat/IIITB/Sem_3/JNI/test.wav", "8");
    }
    public static void callCode(String message, String operation) {
        new callNative().sayHello(message + operation);

    }

    private native void sayHello(String woow);
}
