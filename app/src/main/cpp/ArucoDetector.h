#include <opencv2/core.hpp>
#include <opencv2/aruco.hpp>
#include <opencv2/imgproc.hpp>

using namespace std;
using namespace cv;

const int WIDTH_SHEET = 310;
const int HEIGHT_SHEET = 437;

Point2f getCenterFromCorner(vector<Point2f> corner) {
    float x = corner[0].x + corner[2].x;
    float y = corner[0].y + corner[2].y;
    return Point2f(x / 2, y / 2);
}

bool drawBorder(Mat& img, vector<Point2f> listCenter) {
    if (listCenter[0].x > 0 && listCenter[1].x > 0 && listCenter[2].x > 0 && listCenter[3].x > 0
        && listCenter[0].y > 0 && listCenter[1].y > 0 && listCenter[2].y > 0 && listCenter[3].y > 0) {
        line(img, listCenter[0], listCenter[1], Scalar(0, 255, 0));
        line(img, listCenter[0], listCenter[2], Scalar(0, 255, 0));
        line(img, listCenter[2], listCenter[3], Scalar(0, 255, 0));
        line(img, listCenter[1], listCenter[3], Scalar(0, 255, 0));
        return true;
    }
    return false;
}

//void getHeight(vector<Point2f> listCenter) {
//	return
//}

float fx(float a, float b, float x, Point2f A) {
    return -(a * x - a * A.x - b * A.y) / b;
}

void drawPoint(Mat& img, Point2f pointCenter, int r, int g, int b, double radius = 5) {
    circle(img, pointCenter, radius, Scalar(b, g, r), -1);
}

void drawPoint(Mat& img, Point2i pointCenter, int r, int g, int b, double radius = 5) {
    circle(img, pointCenter, radius, Scalar(b, g, r), -1);
}

void drawPoint(Mat& img, Point2d pointCenter, int r, int g, int b, double radius = 5) {
    circle(img, pointCenter, radius, Scalar(b, g, r), -1);
}

vector<vector<Point2f>> drawQuestionNumber(Mat& img, float gapx, float gapy) {
    vector<vector<Point2f>> result(10);
    float x, y;
    for (int i = 0, y = gapy; i < 10; i++, y += gapy) {
        for (int j = 0, x = gapx; j < 4; j++, x += gapx) {
            result[i].push_back(Point2f(x, y));
            drawPoint(img, Point2f(x, y), 255, 0, 0);
            circle(img, Point2f(x, y), 13, Scalar(0, 255, 255));
        }
    }
    return result;
}

bool checkInCircle(Point2d center, double r, Point2d point) {
    return (point.x - center.x) * (point.x - center.x) + (point.y - center.y) * (point.y - center.y) < r * r;
}

vector<Point2d> getListInCircle(Point2f center, double radius) {
    vector<Point2d> result;
    for (double x = (double)center.x - radius; x < (double)center.x + radius; x++) {
        for (double y = (double)center.y - radius; y < (double)center.y + radius; y++) {
            if (checkInCircle(Point2d((double)center.x, (double)center.y), radius, Point2d(x, y))) {
                result.push_back(Point2d(x, y));
            }
        }
    }
    return result;
}

vector<Point2f> drawCenter(Mat& img, vector<vector<Point2f>> corners, vector<int> ids) {
    int length = ids.size();
    Point2f centerPoint = Point2f(-1, -1);
    vector<Point2f> listCenter = { centerPoint,centerPoint ,centerPoint ,centerPoint };
    for (int i = 0; i < length; i++) {
        centerPoint = getCenterFromCorner(corners[i]);
        circle(img, centerPoint, 5, Scalar(0, 0, 255), -1);
        //putText(img, "id=" + to_string(ids[i]), centerPoint, FONT_HERSHEY_SIMPLEX, 1, Scalar(255, 0, 0));
        listCenter[ids[i] - 1] = centerPoint;
    }
    Point2f temp = listCenter[3];
    listCenter[3] = listCenter[2];
    listCenter[2] = temp;
    return listCenter;
}

Mat warpSheet(Mat& img, vector<Point2f> listCenter) {
    Mat result;
    vector<Point2f> listCornerSheet = { Point2f(0,0),Point2f(WIDTH_SHEET,0),
                                        Point2f(0,HEIGHT_SHEET),Point2f(WIDTH_SHEET,HEIGHT_SHEET) };

    Size sizeSheet = Size(WIDTH_SHEET, HEIGHT_SHEET);

    Mat matrix = getPerspectiveTransform(listCenter, listCornerSheet);
    warpPerspective(img, result, matrix, sizeSheet);
    return result;
}

Mat warpSheetInvert(Mat& img, vector<Point2f> listCenter, Size size) {
    Mat result;
    vector<Point2f> listCornerSheet = { Point2f(0,0),Point2f(WIDTH_SHEET,0),
                                        Point2f(0,HEIGHT_SHEET),Point2f(WIDTH_SHEET,HEIGHT_SHEET) };

    Mat matrix = getPerspectiveTransform(listCenter, listCornerSheet);
    warpPerspective(img, result, matrix, size, WARP_INVERSE_MAP);
    return result;
}

bool checkAnswer(Mat sheet, vector<Point2d> listPoint) {
    int b, w;
    b = w = 0;
    for (Point2d point : listPoint) {
        //cout << point << " " << (int)sheetGray.at<uchar>(point.x, point.y) << endl;
        int value = (int)sheet.at<uchar>(point.y, point.x);
        if (value == 255) {
            //drawPoint(sheet, point, 0, 255, 0, 1);
            //Vec3b & color = sheet.at<Vec3b>()
            w++;
        }
        else {
            //drawPoint(sheet, point, 0, 0, 255, 1);
            b++;
        }
    }
    return b > w;
}

bool checkAnswer(Mat sheet, vector<Point2d> listPoint, Mat& sheetRGB) {
    int b, w;
    b = w = 0;
    for (Point2d point : listPoint) {
        //cout << point << " " << (int)sheetGray.at<uchar>(point.x, point.y) << endl;
        int value = (int)sheet.at<uchar>(point.y, point.x);
        if (value == 255) {
            drawPoint(sheetRGB, point, 0, 0, 255, 1);
            //Vec3b & color = sheet.at<Vec3b>()
            w++;
        }
        else {
            drawPoint(sheetRGB, point, 0, 255, 0, 1);
            b++;
        }
    }
    return b > w;
}

Mat overlayImage(Mat srcImg, Mat overlayImg) {
    Mat result = srcImg.clone();
    for (int i = 0; i < result.rows; i++) {
        for (int j = 0; j < result.cols; j++)
        {
            if (!(overlayImg.at<Vec3b>(i, j)[0] == 0 && overlayImg.at<Vec3b>(i, j)[1] == 0 && overlayImg.at<Vec3b>(i, j)[2] == 0)) {
                result.at<Vec3b>(i, j) = overlayImg.at<Vec3b>(i, j);
            }
        }
    }
    //cout << overlayImg.at<Vec3b>(0, 0) << endl;
    return result;
}

Mat autoContrastBrightness(Mat srcImg) {
    Mat thresh;

    adaptiveThreshold(srcImg, thresh, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 29, 15);

    return thresh;
}