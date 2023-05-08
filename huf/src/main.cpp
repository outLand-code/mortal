#include<iostream>
#include<huf.h>

int main(int argc, char *argv[]){

#ifdef _DEBUG
    HufEncode en;
    en.encode("test.txt");

    // HufDecode de;
    // de.decode("test.txt.dat");

#else

    // Compression:     huf test.txt -encode
    //Uncompression:    huf test.txt -decode
    

    
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
#endif    
    return 0;
    
}
