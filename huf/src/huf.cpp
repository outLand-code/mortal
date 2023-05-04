#include "huf.h"
#include <fstream>
#include <iostream>
#include <sstream>
#include <map>
#include <vector>

std::vector<char> content;
std::map<std::string,int> encodeDic;
std::vector<int> encodeOutput;

int encodeIndex=0;
std::vector<std::string> decodeDic;
std::vector<std::string> decodeOutput;

HufEncode::HufEncode() {
    initEncodeDic();
}

HufDecode::HufDecode() {
    initDecodeDic();
}

void HufEncode::initEncodeDic() {
    for (int i = 0; i < 256; i++) {
        char c = static_cast<char>(i);
        std::string str;
        str += c;
        encodeDic[str] = i;
        encodeIndex = i;
    }
}

void HufDecode::initDecodeDic() {
    for (int i = 0; i < 256; i++) {
        char c = static_cast<char>(i);
        std::string str;
        str += c;
        decodeDic.push_back(str);
    }
}

int readEncodeFile(std::string path){
    std::ifstream in(path);

    if (!in.is_open())
    {
        std::cout<<" open file faild from path:"<<path<<std::endl;
        return 1;
    }
    // 定位文件末尾
    in.seekg(0, std::ios::end);
    // 获取文件大小
    std::streamsize fileSize = in.tellg();
    // 回到文件开头
    in.seekg(0, std::ios::beg);
    content.resize(fileSize);
    in.read(content.data(),fileSize);
    in.close();
    return 0;
}

void enOutPut(std::string path){
    std::ofstream out ;
    out.open(path+".dat", std::ios::out | std::ios::trunc );
    for (size_t i = 0; i < encodeOutput.size(); ++i) {
        out << encodeOutput[i];
        if (i != encodeOutput.size() - 1) {
            out << ",";
        }
    }
    
    out.close();
}

void HufEncode::encode(std::string path){
    readEncodeFile(path);
    char ch;
    std::string s;
    for (char c :content)
    {
        ch=c;
        auto it =encodeDic.find(s+ch);
        if (it !=encodeDic.end())
        {
            s+=ch;
        }else{
            //encode s to output file
            encodeOutput.push_back(encodeDic[s]);
            encodeIndex++;
            encodeDic[s+ch]=encodeIndex;
            s=ch;
        }
    }
    encodeOutput.push_back(encodeDic[s]);

    enOutPut(path);
}

void deOutPut(std::string path){
    std::size_t pos =path.find_last_of(".");
    if(pos !=std::string::npos){
        path=path.substr(0,pos);
        std::ofstream out ;
        out.open(path, std::ios::out | std::ios::trunc );
        for (const std::string &str : decodeOutput) {
            out << str ;
        }
        out.close();
    }else{
        std::cout << "No extension found." << std::endl;
    }
    
}

int readDecodeFile(std::string path){
    std::ifstream in(path);
     if (!in.is_open()) {
        std::cerr << "Error: Could not open the file." << std::endl;
        return 1;
    }

    std::string line;
    while (std::getline(in, line, ',')) {
        std::istringstream iss(line);
        int num;
        iss >> num;
        encodeOutput.push_back(num);
    }

    in.close();
    return 0;
}

void HufDecode::decode(std::string path){
    readDecodeFile(path);
    std::string entry;
    char ch;
    int prevcode=encodeOutput[0],currcode;
    decodeOutput.push_back(decodeDic[prevcode]);
    for(int i=1;i<encodeOutput.size();i++){
        currcode=encodeOutput[i];
        entry=decodeDic[currcode];
        decodeOutput.push_back(entry);
        ch=entry[0];
        decodeDic.push_back(decodeDic[prevcode]+ch);
        prevcode=currcode;
    }
    deOutPut(path);
}

