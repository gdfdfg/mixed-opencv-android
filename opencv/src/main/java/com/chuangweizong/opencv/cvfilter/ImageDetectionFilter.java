package com.chuangweizong.opencv.cvfilter;

import android.content.Context;

import org.opencv.android.Utils;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 图像检测滤镜
 */
public class ImageDetectionFilter implements Filter {

    private final Mat mReferenceImage;
    private final MatOfKeyPoint mReferenceKeypoints = new MatOfKeyPoint();
    private final Mat mReferenceDescriptors = new Mat();
    private final Mat mReferenceCorners = new Mat(4,1, CvType.CV_32FC2);
    private final MatOfKeyPoint mSceneKeypoints = new MatOfKeyPoint();
    private final Mat mSceneDescriptors = new Mat();
    private final Mat mCandidateSceneCorners = new Mat(4,1,CvType.CV_32FC2);
    private final Mat mSceneCorners = new Mat(4,1,CvType.CV_32FC2);
    private final MatOfPoint mIntSceneCorners = new MatOfPoint();

    private final Mat mGraySrc = new Mat();
    private final MatOfDMatch mMatches = new MatOfDMatch();

    private final FeatureDetector mFeatureDetecctor = FeatureDetector.create(FeatureDetector.STAR);
    private final DescriptorExtractor mDescriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.FREAK);
    private final DescriptorMatcher mDescriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

    private final Scalar mLineColor = new Scalar(0,255,0);

    public ImageDetectionFilter(final Context context,final int referenceImageResourceId) throws IOException {
        mReferenceImage = Utils.loadResource(context,referenceImageResourceId, Imgcodecs.CV_LOAD_IMAGE_COLOR);

        final Mat referenceImageGray = new Mat();
        Imgproc.cvtColor(mReferenceImage,referenceImageGray,Imgproc.COLOR_BGRA2GRAY);
        Imgproc.cvtColor(mReferenceImage,mReferenceImage,Imgproc.COLOR_BGR2RGBA);

        mReferenceCorners.put(0, 0, new double[]{0.0, 0.0});
        mReferenceCorners.put(1, 0, new double[]{referenceImageGray.cols(), 0.0});
        mReferenceCorners.put(2, 0, new double[]{referenceImageGray.cols(), referenceImageGray.rows()});
        mReferenceCorners.put(3, 0, new double[]{0.0, referenceImageGray.rows()});

        mFeatureDetecctor.detect(referenceImageGray, mReferenceKeypoints);
        mDescriptorExtractor.compute(referenceImageGray,mReferenceKeypoints,mReferenceDescriptors);

    }


    @Override
    public void apply(Mat src, Mat dst) {

        Imgproc.cvtColor(src, mGraySrc, Imgproc.COLOR_RGBA2GRAY);

        mFeatureDetecctor.detect(mGraySrc, mSceneKeypoints);
        mDescriptorExtractor.compute(mGraySrc, mSceneKeypoints, mReferenceDescriptors);
        mDescriptorMatcher.match(mSceneDescriptors, mReferenceDescriptors, mMatches);

        findSceneCorners();
        draw(src, dst);
    }

    private void findSceneCorners(){

        List<DMatch> matchList = mMatches.toList();
        if(matchList.size() < 4){
            return;
        }

        List<KeyPoint> referenceKeypointsList = mReferenceKeypoints.toList();
        List<KeyPoint> sceneKeypointsList = mSceneKeypoints.toList();

        //Calculate the max and min distances between keypoints.
        double maxDist = 0.0;
        double minDist = Double.MAX_VALUE;
        for(DMatch match : matchList){
            double dist = match.distance;
            if(dist<minDist){
                minDist = dist;
            }
            if(dist > maxDist){
                maxDist = dist;
            }

            if(minDist > 50.0){
                mSceneCorners.create(0,0,mSceneCorners.type());
                return;
            }else if(minDist > 25.0){
                return;
            }
            //Identify "good" keypoints based on match distance
            ArrayList<Point> goodReferencePointsList = new ArrayList<Point>();
            ArrayList<Point> goodScenePointsList = new ArrayList<Point>();

            double maxGoodMatchDist = 1.75*minDist;
            for (DMatch ma : matchList){
                if(ma.distance<maxGoodMatchDist){
                    goodReferencePointsList.add(referenceKeypointsList.get(ma.trainIdx).pt);
                    goodScenePointsList.add(sceneKeypointsList.get(ma.queryIdx).pt);
                }
            }

            if(goodReferencePointsList.size() < 4 || goodScenePointsList.size() < 4){
                return;
            }

            MatOfPoint2f goodReferencePoints = new MatOfPoint2f();
            goodReferencePoints.fromList(goodReferencePointsList);

            MatOfPoint2f goodScenePoints = new MatOfPoint2f();
            goodScenePoints.fromList(goodScenePointsList);

            Mat homography = Calib3d.findHomography(goodReferencePoints,goodScenePoints);
            Core.perspectiveTransform(mReferenceCorners, mCandidateSceneCorners, homography);

            mCandidateSceneCorners.convertTo(mIntSceneCorners,CvType.CV_32S);
            if(Imgproc.isContourConvex(mIntSceneCorners)){
                mCandidateSceneCorners.copyTo(mSceneCorners);
            }
        }
    }

    protected void draw(Mat src,Mat dst){

        if(dst != src){
            src.copyTo(dst);
        }

        if(mSceneCorners.height() < 4){

            int height = mReferenceImage.height();
            int width = mReferenceImage.width();

            int maxDimension = Math.min(dst.width(),dst.height())/2;
            double aspectRatio = width / (double)height;
            if(height > width){
                height = maxDimension;
                width = (int) (height * aspectRatio);
            }else {
                width = maxDimension;
                height = (int)(width / aspectRatio);
            }

            Mat dstROI = dst.submat(0,height,0,width);
            Imgproc.resize(mReferenceImage,dstROI,dstROI.size(),0.0,0.0,Imgproc.INTER_AREA);

            return;
        }

        Imgproc.line(dst,new Point(mSceneCorners.get(0,0)),new Point(mSceneCorners.get(1,0)),mLineColor,4);
        Imgproc.line(dst,new Point(mSceneCorners.get(1,0)),new Point(mSceneCorners.get(2,0)),mLineColor,4);
        Imgproc.line(dst,new Point(mSceneCorners.get(2,0)),new Point(mSceneCorners.get(3,0)),mLineColor,4);
        Imgproc.line(dst,new Point(mSceneCorners.get(3,0)),new Point(mSceneCorners.get(0,0)),mLineColor,4);


    }

}
