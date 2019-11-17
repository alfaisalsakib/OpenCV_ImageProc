package com.example.sakib.cameraimageprocess;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2
{

    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;

    Mat mRgba,imgGray,imgCanny,mat2,mat3,imgHSV,threshHoledHSV;

    Scalar sc1,sc2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        String s = "CAMERA";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                }

        }

        setContentView(R.layout.activity_main);

        sc1 = new Scalar(45,20,10);
        sc2 = new Scalar(75,255,255);

        cameraBridgeViewBase = (JavaCameraView) findViewById(R.id.cameraView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);
        cameraBridgeViewBase.enableView();


        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {


                switch (status)
                {
                    case BaseLoaderCallback.SUCCESS:
                        cameraBridgeViewBase.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }

            }
        };
    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(width,height,CvType.CV_8UC4);
        imgGray = new Mat(width,height,CvType.CV_8UC4);
        imgCanny = new Mat(width,height,CvType.CV_8UC4);
        imgHSV = new Mat(width,height,CvType.CV_8UC4);
        threshHoledHSV = new Mat(width,height,CvType.CV_8UC4);

        mat2 = new Mat(width,height,CvType.CV_8UC4);
        mat3 = new Mat(width,height,CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {


        mRgba = inputFrame.rgba();
        Mat tempRgba = mRgba.t();
        Core.flip(mRgba.t(),tempRgba,1);
        Imgproc.resize(tempRgba,tempRgba,mRgba.size());

        //Core.transpose(mRgba,mat2);
        //Imgproc.resize(mat2,mat3,mat3.size(),0,0,0);


        Imgproc.cvtColor(tempRgba,imgGray,Imgproc.COLOR_RGB2GRAY);
        Imgproc.Canny(imgGray,imgCanny,200,80);

        //Imgproc.cvtColor(mRgba,imgHSV,Imgproc.COLOR_BGR2HSV);
        //Core.inRange(imgHSV,sc1,sc2,threshHoledHSV);

        return imgCanny;
        //return threshHoledHSV;

    }

    @Override
    protected void onPause() {
        super.onPause();

        if(cameraBridgeViewBase!=null)
        {
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!OpenCVLoader.initDebug())
        {
            Toast.makeText(this,"problem in openCv",Toast.LENGTH_LONG).show();
        }
        else
        {
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(cameraBridgeViewBase!=null)
        {
            cameraBridgeViewBase.disableView();
        }
    }


}
