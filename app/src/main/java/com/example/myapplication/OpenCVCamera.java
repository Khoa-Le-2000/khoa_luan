package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import global.Global;
import global.ReturnValue;

public class OpenCVCamera extends AppCompatActivity implements ImageAnalysis.Analyzer {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    ImageView viewImage;

    PreviewView previewView;

    ImageButton openFlashLight, btnBack;
    Boolean statusFlashLight = false;

    TextView txtViewMark;

    Camera camera = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_cvcamera);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        openFlashLight = findViewById(R.id.btnOpenFlashLight);
        previewView = findViewById(R.id.previewView);
        viewImage = findViewById(R.id.viewImage);

        viewImage.setVisibility(View.VISIBLE);

        txtViewMark = findViewById(R.id.txtMark);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OpenCVCamera.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, getExecutor());

        openFlashLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (camera == null) return;
                if (camera.getCameraInfo().hasFlashUnit()) {
                    statusFlashLight = !statusFlashLight;
//                    openFlashLight.setText(statusFlashLight ? R.string.flash_off : R.string.flash_on);
                    openFlashLight.setBackground(statusFlashLight ?
                            ContextCompat.getDrawable(OpenCVCamera.this, R.drawable.ic_baseline_flash_on_24) :
                            ContextCompat.getDrawable(OpenCVCamera.this, R.drawable.ic_baseline_flash_off_24));
                    camera.getCameraControl().enableTorch(statusFlashLight);
                }
            }
        });
    }

    private void startCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder()
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(getExecutor(), this);

        camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis);
    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }


    @Override
    public void analyze(@NonNull ImageProxy image) {
        Log.d("OpenCVCamera", "analyze: got the frame at: " + image.getImageInfo().getTimestamp());

        final Bitmap bitmap = previewView.getBitmap();


        if (bitmap == null)
            return;
        Log.i("OpenCVCamera", Integer.toString(bitmap.getWidth()));

//        final Bitmap bitmap1 = bitmap.copy(bitmap.getConfig(), true);
        image.close();


        ReturnValue returnValue = check(bitmap, Global.getCorrectAnswer());
        Log.i("OpenCVCamera", returnValue.success ? "OK - " + returnValue.mark : "NOT OK");
        Log.i("OpenCVCamera", "FPS: " + returnValue.fps);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (returnValue.success) {
                    viewImage.setVisibility(View.VISIBLE);
                    viewImage.setImageBitmap(bitmap);

                    String viewMark = returnValue.mark + "/10";
                    if (returnValue.mark < 10) viewMark = "0" + viewMark;

                    txtViewMark.setText(viewMark);
                } else {
                    viewImage.setVisibility(View.INVISIBLE);
                    txtViewMark.setText(R.string.mark_default);
                }
            }
        });
    }

    private Bitmap toGrayScale(Bitmap bitmap) {
        int width, height;
        height = bitmap.getHeight();
        width = bitmap.getWidth();

        Bitmap bmpGrayScale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmpGrayScale);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bmpGrayScale;
    }

    public native ReturnValue check(Bitmap frame, int[] correctAnswer);
}
