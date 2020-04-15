package com.google.ar.core.examples.java.sharedcamera;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.ar.core.examples.java.common.rendering.BackgroundRenderer;
import com.google.ar.core.examples.java.common.rendering.ObjectRenderer;
import com.google.ar.core.examples.java.common.rendering.PlaneRenderer;
import com.google.ar.core.examples.java.common.rendering.PointCloudRenderer;
import com.google.ar.core.exceptions.CameraNotAvailableException;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Renderer implements GLSurfaceView.Renderer {
    private static final String TAG = "DEBUGGING";

    SharedCameraActivity mActivity;

    // Whether the GL surface has been created.
    public boolean surfaceCreated;

    // Renderers, see hello_ar_java sample to learn more.
    public final BackgroundRenderer backgroundRenderer = new BackgroundRenderer();
    public final ObjectRenderer virtualObject = new ObjectRenderer();
    public final ObjectRenderer virtualObjectShadow = new ObjectRenderer();
    public final PlaneRenderer planeRenderer = new PlaneRenderer();
    public final PointCloudRenderer pointCloudRenderer = new PointCloudRenderer();

    public Renderer(SharedCameraActivity c) {
        mActivity = c;
    }
    // GL surface created callback. Will be called on the GL thread.
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d("DEBUGGING", "onSurfaceCreated()");
        surfaceCreated = true;

        // Set GL clear color to black.
        GLES20.glClearColor(0f, 0f, 1.0f, 1.0f);

        // Prepare the rendering objects. This involves reading shaders, so may throw an IOException.
        try {
            // Create the camera preview image texture. Used in non-AR and AR mode.
            backgroundRenderer.createOnGlThread(mActivity);
            Log.d("DEBUGGING", "Rendering background.");
            planeRenderer.createOnGlThread(mActivity, "models/trigrid.png");
            pointCloudRenderer.createOnGlThread(mActivity);

            virtualObject.createOnGlThread(mActivity, "models/andy.obj", "models/andy.png");
            virtualObject.setMaterialProperties(0.0f, 2.0f, 0.5f, 6.0f);

            virtualObjectShadow.createOnGlThread(
                    mActivity, "models/andy_shadow.obj", "models/andy_shadow.png");
            virtualObjectShadow.setBlendMode(ObjectRenderer.BlendMode.Shadow);
            virtualObjectShadow.setMaterialProperties(1.0f, 0.0f, 0.0f, 1.0f);

        } catch (IOException e) {
            Log.e("DEBUGGING", "Failed to read an asset file", e);
        }
        Log.d("DEBUGGING", "end of onSurfaceCreated()");
    }

    // GL surface changed callback. Will be called on the GL thread.
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

    }

    // GL draw callback. Will be called each frame on the GL thread.
    @Override
    public void onDrawFrame(GL10 gl) {
       // Log.d("DEBUGGING", "onDrawFrame()");
        // Use the cGL clear color specified in onSurfaceCreated() to erase the GL surface.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //if (!mActivity.shouldUpdateSurfaceTexture.get()) {
            //Log.d("DEBUGGING", "Not ready to draw frame.");
            // Not ready to draw.
         //   return;
       // }

        try {
           // Log.d("DEBUGGING", "Drawing ARCore Frame");
            mActivity.onDrawFrameARCore();
        } catch (CameraNotAvailableException e) {
           // Log.d("DEBUGGING", "CameraNotAvailableException");
            e.printStackTrace();
        }

    }
}
