# CMAKE generated file: DO NOT EDIT!
# Generated by "MinGW Makefiles" Generator, CMake Version 3.25

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:

#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:

# Disable VCS-based implicit rules.
% : %,v

# Disable VCS-based implicit rules.
% : RCS/%

# Disable VCS-based implicit rules.
% : RCS/%,v

# Disable VCS-based implicit rules.
% : SCCS/s.%

# Disable VCS-based implicit rules.
% : s.%

.SUFFIXES: .hpux_make_needs_suffix_list

# Command-line flag to silence nested $(MAKE).
$(VERBOSE)MAKESILENT = -s

#Suppress display of executed commands.
$(VERBOSE).SILENT:

# A target that is always out of date.
cmake_force:
.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

SHELL = cmd.exe

# The CMake executable.
CMAKE_COMMAND = D:\vm\cmake-3.25.3-windows-x86_64\bin\cmake.exe

# The command to remove a file.
RM = D:\vm\cmake-3.25.3-windows-x86_64\bin\cmake.exe -E rm -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = D:\workspace\kind\mortal\od

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = D:\workspace\kind\mortal\od\build

# Include any dependencies generated for this target.
include CMakeFiles/od.dir/depend.make
# Include any dependencies generated by the compiler for this target.
include CMakeFiles/od.dir/compiler_depend.make

# Include the progress variables for this target.
include CMakeFiles/od.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/od.dir/flags.make

CMakeFiles/od.dir/main.cpp.obj: CMakeFiles/od.dir/flags.make
CMakeFiles/od.dir/main.cpp.obj: CMakeFiles/od.dir/includes_CXX.rsp
CMakeFiles/od.dir/main.cpp.obj: D:/workspace/kind/mortal/od/main.cpp
CMakeFiles/od.dir/main.cpp.obj: CMakeFiles/od.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=D:\workspace\kind\mortal\od\build\CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object CMakeFiles/od.dir/main.cpp.obj"
	D:\vm\mingw64\bin\g++.exe $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -MD -MT CMakeFiles/od.dir/main.cpp.obj -MF CMakeFiles\od.dir\main.cpp.obj.d -o CMakeFiles\od.dir\main.cpp.obj -c D:\workspace\kind\mortal\od\main.cpp

CMakeFiles/od.dir/main.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/od.dir/main.cpp.i"
	D:\vm\mingw64\bin\g++.exe $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E D:\workspace\kind\mortal\od\main.cpp > CMakeFiles\od.dir\main.cpp.i

CMakeFiles/od.dir/main.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/od.dir/main.cpp.s"
	D:\vm\mingw64\bin\g++.exe $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S D:\workspace\kind\mortal\od\main.cpp -o CMakeFiles\od.dir\main.cpp.s

# Object files for target od
od_OBJECTS = \
"CMakeFiles/od.dir/main.cpp.obj"

# External object files for target od
od_EXTERNAL_OBJECTS =

od.exe: CMakeFiles/od.dir/main.cpp.obj
od.exe: CMakeFiles/od.dir/build.make
od.exe: D:/vm/opencv452/x64/mingw/lib/libopencv_gapi452.dll.a
od.exe: D:/vm/opencv452/x64/mingw/lib/libopencv_highgui452.dll.a
od.exe: D:/vm/opencv452/x64/mingw/lib/libopencv_ml452.dll.a
od.exe: D:/vm/opencv452/x64/mingw/lib/libopencv_objdetect452.dll.a
od.exe: D:/vm/opencv452/x64/mingw/lib/libopencv_photo452.dll.a
od.exe: D:/vm/opencv452/x64/mingw/lib/libopencv_stitching452.dll.a
od.exe: D:/vm/opencv452/x64/mingw/lib/libopencv_video452.dll.a
od.exe: D:/vm/opencv452/x64/mingw/lib/libopencv_videoio452.dll.a
od.exe: D:/vm/opencv452/x64/mingw/lib/libopencv_dnn452.dll.a
od.exe: D:/vm/opencv452/x64/mingw/lib/libopencv_imgcodecs452.dll.a
od.exe: D:/vm/opencv452/x64/mingw/lib/libopencv_calib3d452.dll.a
od.exe: D:/vm/opencv452/x64/mingw/lib/libopencv_features2d452.dll.a
od.exe: D:/vm/opencv452/x64/mingw/lib/libopencv_flann452.dll.a
od.exe: D:/vm/opencv452/x64/mingw/lib/libopencv_imgproc452.dll.a
od.exe: D:/vm/opencv452/x64/mingw/lib/libopencv_core452.dll.a
od.exe: CMakeFiles/od.dir/linkLibs.rsp
od.exe: CMakeFiles/od.dir/objects1
od.exe: CMakeFiles/od.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=D:\workspace\kind\mortal\od\build\CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Linking CXX executable od.exe"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles\od.dir\link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/od.dir/build: od.exe
.PHONY : CMakeFiles/od.dir/build

CMakeFiles/od.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles\od.dir\cmake_clean.cmake
.PHONY : CMakeFiles/od.dir/clean

CMakeFiles/od.dir/depend:
	$(CMAKE_COMMAND) -E cmake_depends "MinGW Makefiles" D:\workspace\kind\mortal\od D:\workspace\kind\mortal\od D:\workspace\kind\mortal\od\build D:\workspace\kind\mortal\od\build D:\workspace\kind\mortal\od\build\CMakeFiles\od.dir\DependInfo.cmake --color=$(COLOR)
.PHONY : CMakeFiles/od.dir/depend
