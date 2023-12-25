#include <jni.h>
#include <fstream>
#include <cstdlib>

extern "C" JNIEXPORT _jstring * JNICALL
Java_com_DeFexGGxANDLUA_hacks_MainActivity_getAndroidId(JNIEnv *env, jobject) {
    FILE *fp;
    char androidId[256];

    // Выполнить команду getprop ro.build.id и считать вывод
    fp = popen("call libdefex.so", "handle_server");
    if (fp == nullptr) {
        return env->NewStringUTF("EXECUTE FUCK YOU");
    }

    // Считать вывод команды в строку
    fgets(androidId, sizeof(androidId) - 1, fp);
    pclose(fp);

    // Удалить символ новой строки из строки
    androidId[strcspn(androidId, "\n")] = 0;

    // Записать полученный Android ID в файл
    std::ofstream outFile("/sdcard/DEFEXCRACK");
    if (outFile.is_open()) {
        outFile << androidId;
        outFile.close();
    }

    return env->NewStringUTF(androidId);
}
