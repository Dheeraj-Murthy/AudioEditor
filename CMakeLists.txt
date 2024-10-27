cmake_minimum_required(VERSION 3.10)
project(NativeLibrary)

set(CMAKE_CXX_STANDARD 11)

add_library(sayHello SHARED ./Native/Main.cpp)
# sayHello is the name of the .dylib file and which must be loaded
# ./Native/Main.cpp is the file to be compiled
# adjust both to suit your requirements


# Set the JNI include directories
find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})