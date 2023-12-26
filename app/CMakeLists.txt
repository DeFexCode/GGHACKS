# CMakeLists.txt в кореневій папці проекту

cmake_minimum_required(VERSION 3.10)

# Set the project name
project(AndKittyInjector)

# Set the path to KittyMemoryEx
set(KITTYMEMORY_PATH src/main/cpp/KittyMemoryEx)

# Set the path to XDL
set(XDL_PATH src/main/cpp/src/xdl)

# Add the source files for the library
file(GLOB_RECURSE LIB_SRC src/*.cpp src/Injector/*.cpp)

# Create a static library for AndKittyInjector
add_library(AndKittyInjectorLib STATIC ${LIB_SRC})

# Include directories in the header file
target_include_directories(AndKittyInjectorLib PUBLIC ${KITTYMEMORY_PATH} ${XDL_PATH})

# Link libraries for the library
target_link_libraries(AndKittyInjectorLib PUBLIC log)

# Add the source files for the executable
file(GLOB_RECURSE PROJ_SRC src/main/cpp/src/main.cpp)

# Create the executable file for the Android app
add_executable(AndKittyInjector ${PROJ_SRC})

# Link the executable file with the static library
target_link_libraries(AndKittyInjector AndKittyInjectorLib)

# Додайте цей код для app модулю
set(APP_PATH ${CMAKE_CURRENT_SOURCE_DIR}/app)
file(GLOB APP_SRC ${APP_PATH}/*.cpp)
add_executable(AndKittyInjectorApp ${APP_SRC})
target_link_libraries(AndKittyInjectorApp AndKittyInjectorLib)
