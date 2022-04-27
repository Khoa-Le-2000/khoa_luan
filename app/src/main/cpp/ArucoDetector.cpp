//
// Created by admin on 029 29-Mar-2022.
//
#include "ArucoDetector.h"
#include <android/log.h>
#include <opencv2/core.hpp>
#include <opencv2/core/ocl.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/aruco.hpp>
#include <jni.h>
#include <android/bitmap.h>
#include <android/log.h>
#include <iostream>

#include <chrono>

using namespace std;
using namespace cv;
using namespace aruco;

void bitmapToMat2(JNIEnv *env, jobject bitmap, Mat &dst, jboolean needUnPremultiplyAlpha) {
    AndroidBitmapInfo info;
    void *pixels = 0;

    try {
        CV_Assert(AndroidBitmap_getInfo(env, bitmap, &info) >= 0);
        CV_Assert(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 ||
                  info.format == ANDROID_BITMAP_FORMAT_RGB_565);
        CV_Assert(AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0);
        CV_Assert(pixels);
        dst.create(info.height, info.width, CV_8UC4);
        if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
            Mat tmp(info.height, info.width, CV_8UC4, pixels);
            if (needUnPremultiplyAlpha) cvtColor(tmp, dst, COLOR_mRGBA2RGBA);
            else tmp.copyTo(dst);
        } else {
            // info.format == ANDROID_BITMAP_FORMAT_RGB_565
            Mat tmp(info.height, info.width, CV_8UC2, pixels);
            cvtColor(tmp, dst, COLOR_BGR5652RGBA);
        }
        AndroidBitmap_unlockPixels(env, bitmap);
        return;
    } catch (const cv::Exception &e) {
        AndroidBitmap_unlockPixels(env, bitmap);
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
        return;
    } catch (...) {
        AndroidBitmap_unlockPixels(env, bitmap);
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "Unknown exception in JNI code {nBitmapToMat}");
        return;
    }
}

void matToBitmap2(JNIEnv *env, Mat &src, jobject bitmap, jboolean needPremultiplyAlpha) {
    AndroidBitmapInfo info;
    void *pixels = 0;

    try {
        CV_Assert(AndroidBitmap_getInfo(env, bitmap, &info) >= 0);
        CV_Assert(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 ||
                  info.format == ANDROID_BITMAP_FORMAT_RGB_565);
        CV_Assert(src.dims == 2 && info.height == (uint32_t) src.rows &&
                  info.width == (uint32_t) src.cols);
        CV_Assert(src.type() == CV_8UC1 || src.type() == CV_8UC3 || src.type() == CV_8UC4);
        CV_Assert(AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0);
        CV_Assert(pixels);
        if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
            Mat tmp(info.height, info.width, CV_8UC4, pixels);
            if (src.type() == CV_8UC1) {
                cvtColor(src, tmp, COLOR_GRAY2RGBA);
            } else if (src.type() == CV_8UC3) {
                cvtColor(src, tmp, COLOR_RGB2RGBA);
            } else if (src.type() == CV_8UC4) {
                if (needPremultiplyAlpha) cvtColor(src, tmp, COLOR_RGBA2mRGBA);
                else src.copyTo(tmp);
            }
        } else {
            // info.format == ANDROID_BITMAP_FORMAT_RGB_565
            Mat tmp(info.height, info.width, CV_8UC2, pixels);
            if (src.type() == CV_8UC1) {
                cvtColor(src, tmp, COLOR_GRAY2BGR565);
            } else if (src.type() == CV_8UC3) {
                cvtColor(src, tmp, COLOR_RGB2BGR565);
            } else if (src.type() == CV_8UC4) {
                cvtColor(src, tmp, COLOR_RGBA2BGR565);
            }
        }
        AndroidBitmap_unlockPixels(env, bitmap);
        return;
    } catch (const cv::Exception &e) {
        AndroidBitmap_unlockPixels(env, bitmap);
        jclass je = env->FindClass("org/opencv/core/CvException");
        if (!je) je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
        return;
    } catch (...) {
        AndroidBitmap_unlockPixels(env, bitmap);
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "Unknown exception in JNI code {nMatToBitmap}");
        return;
    }
}

Mat checkFinal(Mat frame, bool &hasData, vector<int> correctAnswer, int &numberCorrectAnswer,
               float &fps) {

//    auto start = chrono::high_resolution_clock::now();
    Ptr<Dictionary> dictionary = getPredefinedDictionary(DICT_6X6_250);

    Mat sheet, sheetGray, convertFrame, finalFrame, drawAnswer;
    vector<int> listFinalAnswer(10);
//    vector<int> correctAnswer{1, 1, 2, 2, 3, 3, 4, 4, 1, 3};
    int mark;

    vector<int> ids;
    vector<vector<Point2f>> corners;
    vector<Point2f> listCenter;
//    cvtColor(frame, frame, COLOR_);
    Mat frameGray;
    cvtColor(frame, frameGray, COLOR_BGR2GRAY);
//    auto stop = chrono::high_resolution_clock::now();
//    auto duration = chrono::duration_cast<chrono::milliseconds>(stop - start);
//    fps = (float) 1000 / duration.count();
    detectMarkers(frameGray, dictionary, corners, ids);
    if (ids.size() > 0) {
        //drawDetectedMarkers(frame, corners, ids);
        listCenter = drawCenter(frame, corners, ids);
        if (drawBorder(frame, listCenter)) {
            sheet = warpSheet(frame, listCenter);
            //cout << typeToString(sheet.type()) << endl;
            drawAnswer = Mat(HEIGHT_SHEET, WIDTH_SHEET, CV_8UC3, Scalar(0, 0, 0));
            //cout << sheet << endl;
            cvtColor(sheet, sheetGray, COLOR_BGR2GRAY);
            sheetGray = autoContrastBrightness(sheetGray);
//            threshold(sheetGray, sheetGray, 120, 255, THRESH_OTSU);

            //GaussianBlur(sheetGray, sheetGray, Size(1, 1), 0);

            //double min, max;
            //minMaxLoc(sheetGray, &min, &max);
            //double avg = (min + max) / 2;
            ////cout << min << " " << max << " " << avg << " " << mean(sheetGray) << endl;
            //sheetGray = sheetGray > avg;

            //Canny(sheetGray, sheetEdge, min, max);

            float gapx = WIDTH_SHEET / 5, gapy = HEIGHT_SHEET / 11;

            vector<vector<Point2f>> listAnswer = drawQuestionNumber(sheet, gapx, gapy);
            //cout << listAnswer[0][0] << " " << listAnswer[0][1] << " " << listAnswer[1][0] << endl;
            //Mat1b mask(sheetGray.rows, sheetGray.cols, uchar(0));
            //circle(mask, listAnswer[0][0], 13, Scalar(255), -1, 2);
            //
            //imshow("mask", mask);
            //drawPoint(sheet, Point2f(gapx, gapy), 255, 0, 0);

            bool ans1, ans2, ans3, ans4;
            double b, w;
            mark = 0;
            for (int questionNumber = 0; questionNumber < 10; questionNumber++) {
                ans1 = false;
                ans2 = false;
                ans3 = false;
                ans4 = false;
                b = w = 0;
                vector<Point2d> listPoint = getListInCircle(listAnswer[questionNumber][0], 13);
                ans1 = checkAnswer(sheetGray, listPoint, sheet);
                listPoint = getListInCircle(listAnswer[questionNumber][1], 13);
                ans2 = checkAnswer(sheetGray, listPoint, sheet);
                listPoint = getListInCircle(listAnswer[questionNumber][2], 13);
                ans3 = checkAnswer(sheetGray, listPoint, sheet);
                listPoint = getListInCircle(listAnswer[questionNumber][3], 13);
                ans4 = checkAnswer(sheetGray, listPoint, sheet);

                int check = ans1 ? 1 : 0;
                check += ans2 ? 1 : 0;
                check += ans3 ? 1 : 0;
                check += ans4 ? 1 : 0;

                if (check != 1) {
                    listFinalAnswer[questionNumber] = -1;
                    if (check > 0) {
                        if (ans1)
                            circle(drawAnswer, listAnswer[questionNumber][0],
                                   13, Scalar(0, 0, 255), 2);
                        if (ans2)
                            circle(drawAnswer, listAnswer[questionNumber][1],
                                   13, Scalar(0, 0, 255), 2);
                        if (ans3)
                            circle(drawAnswer, listAnswer[questionNumber][2],
                                   13, Scalar(0, 0, 255), 2);
                        if (ans4)
                            circle(drawAnswer, listAnswer[questionNumber][3],
                                   13, Scalar(0, 0, 255), 2);
                    }
                    circle(drawAnswer,
                           listAnswer[questionNumber][correctAnswer[questionNumber] - 1],
                           13, Scalar(255, 0, 0), 2);
                } else {
                    if (ans1) listFinalAnswer[questionNumber] = 1;
                    else if (ans2) listFinalAnswer[questionNumber] = 2;
                    else if (ans3) listFinalAnswer[questionNumber] = 3;
                    else if (ans4) listFinalAnswer[questionNumber] = 4;
                    if (listFinalAnswer[questionNumber] == correctAnswer[questionNumber]) {
                        circle(drawAnswer,
                               listAnswer[questionNumber][listFinalAnswer[questionNumber] - 1],
                               13, Scalar(0, 255, 0), 2);
                        mark++;
                    } else {
                        circle(drawAnswer,
                               listAnswer[questionNumber][correctAnswer[questionNumber] - 1],
                               13, Scalar(255, 0, 0), 2);
                        circle(drawAnswer,
                               listAnswer[questionNumber][listFinalAnswer[questionNumber] - 1],
                               13, Scalar(0, 0, 255), 2);
                    }
                }
            }
            //for (int i = 0; i < 10; i++)
            //{
            //	cout << listFinalAnswer[i] << " ";
            //}
            //cout << endl;

            convertFrame = warpSheetInvert(drawAnswer, listCenter, Size(frame.cols, frame.rows));
            hasData = true;
            numberCorrectAnswer = mark;
//            auto stop = chrono::high_resolution_clock::now();
//            auto duration = chrono::duration_cast<chrono::milliseconds>(stop - start);
//            fps = (float) 1000 / duration.count();
            return convertFrame;
//            frame = overlayImage(frame, convertFrame);
        }
    }
    hasData = false;
//    auto stop = chrono::high_resolution_clock::now();
//    auto duration = chrono::duration_cast<chrono::milliseconds>(stop - start);
//    fps = (float) 1000 / duration.count();
    return frame;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_example_myapplication_OpenCVCamera_check(JNIEnv *env, jobject thiz,
                                                  jobject bmpFrame, jintArray correctAnswer) {
    Mat frame;
    bitmapToMat2(env, bmpFrame, frame, false);

    const char *cls_name = "global/ReturnValue";
    jclass cls = env->FindClass(cls_name);
    if (env->ExceptionOccurred())
        return NULL;

    jmethodID constructorId = env->GetMethodID(cls, "<init>", "(ZIF)V");
    if (env->ExceptionOccurred())
        return NULL;

    jsize size = env->GetArrayLength(correctAnswer);
    vector<int> input(size);
    env->GetIntArrayRegion(correctAnswer, 0, size, &input[0]);

    bool check;
    int mark = 0;
    float fps = 0;
    frame = checkFinal(frame, check, input, mark, fps);
    matToBitmap2(env, frame, bmpFrame, false);

    jboolean success = check ? true : false;
    jint finalMark = mark;
    return env->NewObject(cls, constructorId, success, finalMark, fps);

//    return check ? JNI_TRUE : JNI_FALSE;
//    return JNI_TRUE;
}