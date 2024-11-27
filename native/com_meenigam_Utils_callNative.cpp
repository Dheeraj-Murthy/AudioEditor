#include "com_meenigam_Utils_callNative.h"
#include "main.h"
#include <iostream>
using namespace std;

JNIEXPORT void JNICALL Java_com_meenigam_Utils_callNative_sayHello
  (JNIEnv *env, jobject obj, jstring strArg, jint intArg, jobjectArray objArrayArg) {

    // 1. Extract the string (jstring) and convert it to a C++ string
    const char *str = env->GetStringUTFChars(strArg, 0);
    std::string extractedString(str);
    env->ReleaseStringUTFChars(strArg, str);  // Always release the string when done

    // 2. Extract the integer (jint)
    int extractedInt = intArg;

    // 3. Extract the object array (jobjectArray)
    jsize arrayLength = env->GetArrayLength(objArrayArg);
    std::vector<std::string> extractedArray;  // Assuming it's an array of strings

    for (jsize i = 0; i < arrayLength; ++i) {
        jobject element = env->GetObjectArrayElement(objArrayArg, i);
        jstring elementStr = (jstring) element;
        const char *elementChars = env->GetStringUTFChars(elementStr, 0);
        extractedArray.push_back(std::string(elementChars));
        env->ReleaseStringUTFChars(elementStr, elementChars);  // Release after use
        env->DeleteLocalRef(element);  // Delete local reference to prevent memory leak
    }

    // Now, you have the variables:
    // - extractedString (from strArg)
    // - extractedInt (from intArg)
    // - extractedArray (from objArrayArg)

    // Use these variables as needed:
    std::cout << "String: " << extractedString << std::endl;
    std::cout << "Integer: " << extractedInt << std::endl;
    std::cout << "Array: ";
    for (const auto &item : extractedArray) {
        std::cout << item << " ";
    }
    std::cout << std::endl;
    utilityBelt(extractedInt, extractedString, extractedString, extractedArray);
}

