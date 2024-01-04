cmake_minimum_required(VERSION 3.5)

project(AndKittyInjector)

# Создание цели библиотеки 'inject32' с использованием файла 'main.cpp'
add_library(inject32 SHARED main.cpp)

# Установка свойств для цели 'inject32'
set_target_properties(inject32 PROPERTIES
        CXX_STANDARD 14
        CXX_EXTENSIONS OFF
        CXX_STANDARD_REQUIRED ON
        )

# Добавление библиотеки liblog.so (предполагая, что вы используете библиотеку log)
find_library(log-lib log)
target_link_libraries(inject32 ${log-lib})

# Включение поддержки исключений
target_compile_options(inject32 PRIVATE -fexceptions)

# Общие пути к библиотекам и включаемым файлам
set(BASE_PATH ${CMAKE_SOURCE_DIR})

# Библиотеки для arm64-v8a
set(LIBCURL_PATH_ARM64 ${BASE_PATH}/curl/curl-android-arm64-v8a/lib)
set(LIBSSL_PATH_ARM64 ${BASE_PATH}/curl/openssl-android-arm64-v8a/lib)

# Библиотеки для armeabi-v7a
set(LIBCURL_PATH_ARMV7 ${BASE_PATH}/curl/curl-android-armeabi-v7a/lib)
set(LIBSSL_PATH_ARMV7 ${BASE_PATH}/curl/openssl-android-armeabi-v7a/lib)

# Включаемые файлы для arm64-v8a
set(INCLUDE_PATH_ARM64 ${BASE_PATH}/curl/curl-android-arm64-v8a/include/curl/)
set(INCLUDE_PATH_SSL_ARM64 ${BASE_PATH}/curl/openssl-android-arm64-v8a/include/curl/)

# Включаемые файлы для armeabi-v7a
set(INCLUDE_PATH_ARMV7 ${BASE_PATH}/curl/curl-android-armeabi-v7a/include/curl/)
set(INCLUDE_PATH_SSL_ARMV7 ${BASE_PATH}/curl/openssl-android-armeabi-v7a/include/curl/)

# Линковка вашей цели 'inject32' с добавленными библиотеками
target_link_libraries(inject32
        ${LIBCURL_PATH_ARM64}/libcurl.a
        ${LIBSSL_PATH_ARM64}/libssl.a
        ${LIBSSL_PATH_ARM64}/libcrypto.a
        ${LIBCURL_PATH_ARMV7}/libcurl.a
        ${LIBSSL_PATH_ARMV7}/libssl.a
        ${LIBSSL_PATH_ARMV7}/libcrypto.a
        z  # Добавляем библиотеку zlib
        )

# Добавление путей к включаемым файлам
include_directories(
        ${INCLUDE_PATH_ARM64}
        ${INCLUDE_PATH_SSL_ARM64}
        ${INCLUDE_PATH_ARMV7}
        ${INCLUDE_PATH_SSL_ARMV7}
)
