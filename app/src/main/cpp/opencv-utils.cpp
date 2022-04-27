#include "opencv-utils.h"
#include <opencv2/imgproc.hpp>
#include <opencv2/aruco.hpp>

using namespace cv;
using namespace aruco;

Mat drawMarker() {
    Mat markerImage;
    Ptr<Dictionary> dictionary = getPredefinedDictionary(DICT_6X6_100);
    drawMarker(dictionary, 33, 200, markerImage, 1);
    return markerImage;
}

void myFlip(Mat src) {
    flip(src, src, 0);
}

void myBlur(Mat src, int sigma) {
    GaussianBlur(src, src, Size(sigma, sigma), 0);
}