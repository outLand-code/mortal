#include<iostream>
#include<huf.h>

int main(int argc, char *argv[]){
    
    // HufEncode en;
    // en.encode("test.txt");

    // HufDecode de;
    // de.decode("test.txt.dat");

    // Compression:     huf test.txt -encode
    //Uncompression:    huf test.txt -decode
    

    // 遍历参数并输出
    for (int i = 0; i < argc; ++i) {
        std::cout << "Argument " << i << ": " << argv[i] << std::endl;
    }

    std::string path = argv[1];
    std::string action=argv[2];
    if (action=="-encode")
    {
        HufEncode huf;
        huf.encode(path);
    }else if(action=="-decode"){
        HufDecode huf;
        huf.decode(path);
    }else{
        std::cout << "param is wrong!" << std::endl;
    }
    return 0;
    
}
