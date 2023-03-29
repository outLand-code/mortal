cmake_minimum_required(VERSION 3.1)

project(od)

set(CMAKE_CXX_STANDARD 11)

find_package(OpenCV REQUIRED)
include_directories(${OpenCV_INCLUDE_DIRS})

add_executable(od main.cpp)
target_link_libraries(od ${OpenCV_LIBS})