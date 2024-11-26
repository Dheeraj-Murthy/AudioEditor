#include "com_meenigam_Utils_callNative.h"
#include <iostream>
using namespace std;

JNIEXPORT void JNICALL Java_com_meenigam_Utils_callNative_sayHello
(JNIEnv *env, jobject obj, jstring javaString) {
    // Convert the jstring to a C-style string
    const char *nativeString = env->GetStringUTFChars(javaString, nullptr);
    if (nativeString == nullptr) {
        cerr << "Failed to convert jstring to native string." << endl;
        return; // OutOfMemoryError already thrown by JVM
    }

    // Use the native string
    cout << "Received string from Java: " << nativeString << endl;

    // Release the memory allocated for the native string
    env->ReleaseStringUTFChars(javaString, nativeString);
}