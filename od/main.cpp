#include <iostream>
#include <opencv2/opencv.hpp>

int main() {
    std::cout << "OpenCV Version: " << CV_VERSION << std::endl;
    cv::Mat image = cv::imread("lena.jpg");

    if (!image.empty()) {
        cv::imshow("Lena", image);
        cv::waitKey(0);
    } else {
        std::cerr << "Error: Image not found or unable to open." << std::endl;
    }

    return 0;
}
