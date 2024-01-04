#include <jni.h>
#include <cstdlib>
#include <string>
#include <iostream>
#include <fstream>
#include <sstream>
#include <android/log.h>
#include "Component/ENCRYPTION/OBFUSCATION.h"
#include "Component/HTTP/httplib.h"
#include <curl.h>

#define LOG_TAG "[@AMGx0]"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)

extern "C"
JNIEXPORT void JNICALL Java_com_DeFexGGxANDLUA_hacks_MainActivity_executeScript(JNIEnv *env, jobject thiz) {
    if (env != nullptr) {
        // Используем httplib для выполнения HTTP-запроса к серверу
        httplib::Client cli("https://defexggxhuligan.000webhostapp.com");

        auto res = cli.Get("/negrpashet.txt");

        if (res && res->status == 200) {
            // Если запрос успешен, то сохраняем файл
            std::ofstream outfile("/sdcard/download/negrpashet.txt");
            outfile << res->body;
            outfile.close();
            LOGI("File downloaded successfully.");
        } else {
            LOGE("HTTP GET request failed or no content available.");
        }
    }
}
