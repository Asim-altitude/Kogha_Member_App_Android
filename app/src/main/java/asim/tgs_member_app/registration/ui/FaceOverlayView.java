// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package asim.tgs_member_app.registration.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;


import java.text.DecimalFormat;

import asim.tgs_member_app.registration.ui.model.FaceResult;
import asim.tgs_member_app.registration.ui.utils.CaptureReadyCallback;


/**
 * Created by Nguyen on 5/20/2016.
 */

/**
 * This class is a simple View to display the faces.
 */
public class FaceOverlayView extends View {
    private static final String TAG = "FaceOverlayView";

    private Paint mPaint,fixedRectPaint;
    private Paint mTextPaint;
    private int mDisplayOrientation;
    private int mOrientation;
    private int previewWidth;
    private int previewHeight;
    private FaceResult[] mFaces;
    private double fps;
    private boolean isFront = false;
    private CaptureReadyCallback captureReadyCallback;

    public FaceOverlayView(Context context) {
        super(context);
        initialize();
    }

    private void initialize() {
        // We want a green box around the face:
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        int stroke = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, metrics);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(stroke);
        mPaint.setStyle(Paint.Style.STROKE);

        int stroke2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, metrics);
        fixedRectPaint = new Paint();
        fixedRectPaint.setAntiAlias(true);
        fixedRectPaint.setDither(true);
        fixedRectPaint.setColor(Color.BLACK);
        fixedRectPaint.setStrokeWidth(stroke2);
        fixedRectPaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, metrics);
        mTextPaint.setTextSize(size);
        mTextPaint.setColor(Color.GREEN);
        mTextPaint.setStyle(Paint.Style.FILL);
    }

    public void setFPS(double fps) {
        this.fps = fps;
    }

    public void setCaptureReadyCallback(CaptureReadyCallback captureReadyCallback) {
        this.captureReadyCallback = captureReadyCallback;
    }

    public void setFaces(FaceResult[] faces) {
        mFaces = faces;
        invalidate();
    }

    public void setOrientation(int orientation) {
        mOrientation = orientation;
    }

    public void setDisplayOrientation(int displayOrientation) {
        mDisplayOrientation = displayOrientation;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        createWindowFrame(canvas);
        if (mFaces != null && mFaces.length > 0) {

            float scaleX = (float) getWidth() / (float) previewWidth;
            float scaleY = (float) getHeight() / (float) previewHeight;

            switch (mDisplayOrientation) {
                case 90:
                case 270:
                    scaleX = (float) getWidth() / (float) previewHeight;
                    scaleY = (float) getHeight() / (float) previewWidth;
                    break;
            }



            canvas.save();
            canvas.rotate(-mOrientation);



            RectF rectF = new RectF();
            for (FaceResult face : mFaces) {
                PointF mid = new PointF();
                face.getMidPoint(mid);

                if (mid.x != 0.0f && mid.y != 0.0f) {
                    float eyesDis = face.eyesDistance();

                    rectF.set(new RectF(
                            (mid.x - eyesDis * 1.2f) * scaleX,
                            (mid.y - eyesDis * 0.65f) * scaleY,
                            (mid.x + eyesDis * 1.2f) * scaleX,
                            (mid.y + eyesDis * 1.75f) * scaleY));
                    if (isFront) {
                        float left = rectF.left;
                        float right = rectF.right;
                        rectF.left = getWidth() - right;
                        rectF.right = getWidth() - left;
                    }
                    rectF.top = rectF.top - 70;
                    rectF.bottom = rectF.bottom + 10;
                    rectF.left = rectF.left - 10;
                    rectF.right = rectF.right + 10;
                    Log.e(TAG, "onDraw:confidence "+face.getConfidence()+" left "+rectF.left+" right "+rectF.right+" top "+rectF.top+" bottom "+rectF.bottom);


                    detectIfReady(rectF);
                    //canvas.drawRect(rectF, mPaint);
                   /* canvas.drawText("ID " + face.getId(), rectF.left, rectF.bottom + mTextPaint.getTextSize(), mTextPaint);
                    canvas.drawText("Confidence " + face.getConfidence(), rectF.left, rectF.bottom + mTextPaint.getTextSize() * 2, mTextPaint);
                    canvas.drawText("EyesDistance " + face.eyesDistance(), rectF.left, rectF.bottom + mTextPaint.getTextSize() * 3, mTextPaint);
          */      }
            }
            canvas.restore();
        }

        DecimalFormat df2 = new DecimalFormat(".##");
       // canvas.drawText("Detected_Frame/s: " + df2.format(fps) + " @ " + previewWidth + "x" + previewHeight, mTextPaint.getTextSize(), mTextPaint.getTextSize(), mTextPaint);
    }

    int time = 0;
    boolean isCapturing = false;
    protected void detectIfReady(RectF faceRect){

        int width = getWidth();
        int height = getHeight();

        if (width < height) {
            width = width - side_margin;
            height = getWidth();
        }else {
            height = height - side_margin;
            width = getHeight();
        }

        int canvasW = getWidth();
        int canvasH = getHeight();
        Point centerOfCanvas = new Point(canvasW / 2, canvasH / 2);

        int left = centerOfCanvas.x - (width / 2);
        int top = centerOfCanvas.y - (height / 2);
        int right = centerOfCanvas.x + (width / 2);
        int bottom = centerOfCanvas.y + (height / 2);
        Rect rect = new Rect(left, top, right, bottom);


        if (rect.top < faceRect.top
                && rect.left < faceRect.left
                && rect.bottom > faceRect.bottom
                && rect.right > faceRect.right){

            Log.e(TAG, " READY_TO_CAPTURE....." );
            time++;
            if (time >= 3 && !isCapturing){
                if (captureReadyCallback!=null) {
                    captureReadyCallback.onFaceReadyInCenter(rect);
                    isCapturing = true;
                }

            }
        }
    }

    int side_margin = 100;
    protected void createWindowFrame(Canvas osCanvas) {

        int width = getWidth();
        int height = getHeight();

        if (width < height) {
            width = width - side_margin;
            height = getWidth();
        }else {
            height = height - side_margin;
            width = getHeight();
        }

        RectF outerRectangle = new RectF(0, 0, getWidth(), getHeight());

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.argb(150, 0, 0, 0));
        osCanvas.drawRect(outerRectangle, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        int canvasW = getWidth();
        int canvasH = getHeight();
        Point centerOfCanvas = new Point(canvasW / 2, canvasH / 2);

        int left = centerOfCanvas.x - (width / 2);
        int top = centerOfCanvas.y - (height / 3) + 50;
        int right = centerOfCanvas.x + (width / 2);
        int bottom = centerOfCanvas.y + (height / 3) - 50;
        Rect rect = new Rect(left, top, right, bottom);
        osCanvas.drawRect(rect, paint);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(5);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        osCanvas.drawRect(rect, paint);

        if (captureReadyCallback!=null)
            captureReadyCallback.onFaceReadyInCenter(rect);

    }


    public void setPreviewWidth(int previewWidth) {
        this.previewWidth = previewWidth;
    }

    public void setPreviewHeight(int previewHeight) {
        this.previewHeight = previewHeight;
    }

    public void setFront(boolean front) {
        isFront = front;
    }
}