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

int flush_index = 4095; 


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
    
    content.clear();
    std::string line;
    while (std::getline(in, line)) {
        content.insert(content.end(), line.begin(), line.end());
        if (in.peek() != EOF) {
            content.push_back('\n');
        }
    }

    in.close();
    return 0;
}

void enOutPut(std::string path){
    std::ofstream out(path+".dat",std::ios::binary) ;
    unsigned char buffer = 0;
    int bit_position = 0;
    for (const size_t &elem: encodeOutput) 
    {
        // std::cout<< "test:"<<elem<<std::endl;
        for(int i=2048;i>0;i=i>>1){
            if ((elem&i)==i) {
                buffer |= (1 << (7 - bit_position));
            }
            ++bit_position;
            if (bit_position == 8) {
                out.write(reinterpret_cast<char*>(&buffer), 1);
                bit_position = 0;
                buffer = 0;
            }
        }
    }
    if (bit_position > 0) {
        out.write(reinterpret_cast<char*>(&buffer), 1);
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
            if(encodeDic.size()>=4094){
                encodeOutput.push_back(flush_index);
                initEncodeDic();
            }else{
                encodeDic[s+ch]=encodeIndex;
            }
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
    std::ifstream in(path,std::ios::binary);
     if (!in.is_open()) {
        std::cerr << "Error: Could not open the file." << std::endl;
        return 1;
    }
    char byte;
    int bit_position=0,read_int=0;
    while (in.read(&byte,1))
    {
            char c=byte;
            for(int i=128;i>0;i=i>>1){
                if((c&i)==i){
                    read_int|=1<<(11-bit_position);
                }
                ++bit_position;
                if (bit_position==12)
                {
                    // std::cout<<"test1:"<<read_int<<std::endl;
                    encodeOutput.push_back(read_int);
                    bit_position=0;
                    read_int=0;
                }
                
            }
        
    }
    // if(bit_position>0){
    //     encodeOutput.push_back(read_int);
    // }


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
        if (currcode == flush_index) {
            initDecodeDic();
            prevcode = encodeOutput[++i];
            decodeOutput.push_back(decodeDic[prevcode]);
            continue;
        }
        if (currcode >= decodeDic.size()) {
            entry = decodeDic[prevcode];
            entry += entry[0];
        } else {
            entry = decodeDic[currcode];
        }
        decodeOutput.push_back(entry);
        ch=entry[0];
        decodeDic.push_back(decodeDic[prevcode]+ch);
        if(decodeDic.size()>=4094){
            initDecodeDic();
        }
        prevcode=currcode;
    }
    
    deOutPut(path);
}