#pragma once

#include <opencv2/core.hpp>

using namespace cv;

Mat drawMarker();
void myFlip(Mat src);
void myBlur(Mat src, int sigma);