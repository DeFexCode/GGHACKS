#include <jni.h>
#include <cstdlib>
#include <string>
#include <iostream>
#include <fstream>
#include <sstream>
#include <stdio.h>
#include "stddef.h"
#include <android/log.h>
#include "Component/ENCRYPTION/OBFUSCATION.h"
#include "Component/HTTP/httplib.h"
#include <curl/curl.h>
#include <openssl/rsa.h>
#include <openssl/pem.h>
#include <sys/system_properties.h>

#define LOG_TAG "[@AMGx0]"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)

// Callback-функция для записи данных, полученных от сервера
size_t WriteCallback(void* contents, size_t size, size_t nmemb, std::string* userp) {
    size_t total_size = size * nmemb;
    userp->append((char*)contents, total_size);
    return total_size;
}

std::string performHttpPost(const std::string& url, const std::string& postData) {
    CURL* curl;
    CURLcode res;
    std::string readBuffer;

    // Инициализация curl
    curl = curl_easy_init();

    if (curl) {
        // Установка URL
        curl_easy_setopt(curl, CURLOPT_URL, url.c_str());

        // Установка метода запроса POST
        curl_easy_setopt(curl, CURLOPT_POST, 1L);

        // Установка данных для отправки
        curl_easy_setopt(curl, CURLOPT_POSTFIELDS, postData.c_str());

        // Установка Content-Type
        curl_slist* headers = nullptr;
        headers = curl_slist_append(headers, "Content-Type: application/x-www-form-urlencoded");
        curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);

        // Отключение проверки SSL-сертификата (только для тестирования)
        curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 0L);

        // Установка пути к файлу сертификата
        curl_easy_setopt(curl, CURLOPT_CAINFO, "/sdcard/cacert.pem");

        // Установка callback-функции для записи данных
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteCallback);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, &readBuffer);

        // Выполнение запроса
        res = curl_easy_perform(curl);

        // Проверка на ошибки
        if (res != CURLE_OK) {
            LOGE("curl_easy_perform() failed: %s\n", curl_easy_strerror(res));
            LOGE("Failed URL: %s\n", url.c_str());
            LOGE("Post Data: %s\n", postData.c_str());
        }

        // Освобождение ресурсов curl
        curl_easy_cleanup(curl);
        curl_slist_free_all(headers);
    }

    return readBuffer;
}


extern "C"
JNIEXPORT jint JNICALL Java_com_DeFexGGxANDLUA_hacks_MainActivity_executeScript(JNIEnv* env, jobject thiz) {
    // Получаем значения из системных свойств
    char prop_build_id[PROP_VALUE_MAX];
    char prop_uuid[PROP_VALUE_MAX];
    char prop_name[PROP_VALUE_MAX];
    char prop_manufacture[PROP_VALUE_MAX];
    __system_property_get("ro.build.id", prop_build_id);
    __system_property_get("ro.build.uuid", prop_uuid);
    __system_property_get("ro.product.name", prop_name);
    __system_property_get("ro.product.manufacturer", prop_manufacture);

    // Формируем URL для запроса
    std::string url = "https://defexggxhuligan.000webhostapp.com";

    // Формируем запрос вручную
    std::string request = "/fuckraphael.php?";
    request += "username=andlua&password=";
    request += prop_build_id;
    request += "&uuid=";
    request += prop_uuid;
    request += "&manufacturer=";
    request += prop_manufacture;
    request += "&model=";
    request += prop_name;

    // Инициализируем curl
    CURL *curl = curl_easy_init();

    // Проверяем успешность инициализации
    if (curl) {
        // Создаем структуру для хранения ответа
        std::string response;

        // Устанавливаем URL
        curl_easy_setopt(curl, CURLOPT_URL, (url + request).c_str());

        // Отключаем проверку SSL-сертификата (только для тестирования)
        curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 0L);

        // Включаем verbose mode для логгирования
        curl_easy_setopt(curl, CURLOPT_VERBOSE, 1L);

        // Устанавливаем буфер для получения сообщения об ошибке
        char errorBuffer[CURL_ERROR_SIZE];
        curl_easy_setopt(curl, CURLOPT_ERRORBUFFER, errorBuffer);

        // Устанавливаем callback-функцию для записи данных
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteCallback);

        // Передаем данные запроса
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, &response);

        // Выполняем запрос
        CURLcode res = curl_easy_perform(curl);

        // Получаем HTTP-код ответа
        long http_code = 0;
        curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &http_code);
        LOGD("HTTP Response Code: %ld", http_code);

        // Проверяем успешность выполнения запроса
        if (res != CURLE_OK) {
            // Логируем ошибку
            LOGE("curl_easy_perform() failed: %s\n", curl_easy_strerror(res));
            // Выводим сообщение об ошибке из буфера
            LOGE("Error Buffer: %s\n", errorBuffer);
        }




        // Освобождаем ресурсы curl
        curl_easy_cleanup(curl);

        // Логируем ответ
        LOGD("Response: %s", response.c_str());
    }

    return 0;
}
