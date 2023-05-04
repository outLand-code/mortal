#ifndef HUF_H
#define HUF_H

#include <string>

struct HufEncode {
    HufEncode();
    void initEncodeDic();
    void encode(std::string path);
};

struct HufDecode {
    HufDecode();
    void initDecodeDic();
    void decode(std::string path);
};

#endif
