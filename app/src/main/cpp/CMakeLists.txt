cmake_minimum_required(VERSION 3.4.1)

# Ваши существующие настройки cmake

add_library(
        # Название вашей библиотеки
        inject

        # Тип библиотеки (SHARED или STATIC)
        SHARED

        # Пути к исходным файлам C++
        main.cpp
)

# Включаем поддержку C++11
set_target_properties(inject PROPERTIES
        CXX_STANDARD 11
        CXX_EXTENSIONS OFF
        CXX_STANDARD_REQUIRED ON
        )

# Добавляем необходимые библиотеки (если есть)
# target_link_libraries(inject some_library)

# Путь к NDK исходникам

# Включаем logcat вывод в Android
find_library(
        log-lib
        log
)

# Связываем вашу библиотеку с библиотекой log
target_link_libraries(
        inject
        ${log-lib}
)

# Добавляем разрешение на запись во внешнее хранилище
target_include_directories(inject PRIVATE
        ${CMAKE_SOURCE_DIR}/src/main/cpp
        )

# Включаем исключения
target_compile_options(inject PRIVATE -fexceptions)
