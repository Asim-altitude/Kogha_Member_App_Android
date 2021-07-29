package asim.tgs_member_app.registration.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.vision.face.Face;

import java.io.File;
import java.io.FileOutputStream;

import asim.tgs_member_app.R;
import asim.tgs_member_app.registration.camera.camera_files.FaceTrackingListener;
import asim.tgs_member_app.registration.camera.camera_files.GraphicOverlay;
import asim.tgs_member_app.registration.ui.utils.CaptureReadyCallback;


class BodyFaceGraphic extends GraphicOverlay.Graphic {
    private static final String TAG = "FaceGraphic";
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;
    private static final int COLOR_CHOICES[] = {
        Color.BLUE,
        Color.CYAN,
        Color.GREEN,
        Color.MAGENTA,
        Color.RED,
        Color.WHITE,
        Color.YELLOW
    };
    private Bitmap posture;
    private static int mCurrentColorIndex = 0;
    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;
    private volatile Face mFace;
    private int mFaceId;
    private float mFaceHappiness;
    private CaptureReadyCallback captureReadyCallback;

    public void setCaptureReadyCallback(CaptureReadyCallback captureReadyCallback) {
        this.captureReadyCallback = captureReadyCallback;

    }

    BodyFaceGraphic(GraphicOverlay overlay) {
        super(overlay);

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];
        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);
        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);
        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);


    }

    public void setBitmap(Context context){
        posture = BitmapFactory.decodeResource(context.getResources(), R.drawable.posture);
        postInvalidate();
    }

    void setId(int id) {
        mFaceId = id;
    }

    void updateFace(Face face) {
        mFace = face;
        postInvalidate();
        logFaceData(mFace, new FaceTrackingListener() {
            @Override
            public void onFaceLeftMove() {
                Log.e(TAG, "onFaceLeftMove: ");
            }

            @Override
            public void onFaceRightMove() {
                Log.e(TAG, "onFaceRightMove: ");
            }

            @Override
            public void onFaceUpMove() {
                Log.e(TAG, "onFaceUpMove: ");
            }

            @Override
            public void onFaceDownMove() {

            }

            @Override
            public void onGoodSmile() {
                Log.e(TAG, "onGoodSmile: ");
            }

            @Override
            public void onEyeCloseError() {

            }

            @Override
            public void onMouthOpenError() {

            }

            @Override
            public void onMultipleFaceError() {

            }
        });
    }
    @Override
    public void draw(Canvas canvas) {

        //createWindowFrame(canvas);
        Log.e(TAG, "draw: ");
        Face face = mFace;
        if (face == null) {
            return;
        }
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        Log.e("Y",""+y);
      /*  canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
        canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);
     */  /* canvas.drawText("happiness: " + String.format("%.2f", face.getIsSmilingProbability()), x - ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint);
        canvas.drawText("right eye: " + String.format("%.2f", face.getIsRightEyeOpenProbability()), x + ID_X_OFFSET * 2, y + ID_Y_OFFSET * 2, mIdPaint);
       */// canvas.drawText("left eye: " + String.format("%.2f", face.getIsLeftEyeOpenProbability()), x - ID_X_OFFSET*2, y - ID_Y_OFFSET*2, mIdPaint);
        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        float EulerY=mFace.getEulerY();
        float EulerZ=mFace.getEulerZ();
      /*  canvas.drawText("Euler Y: " + String.format("%.2f", EulerY), x - ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint);
        canvas.drawText("Euler Z: " + String.format("%.2f", EulerZ), x + ID_X_OFFSET * 2, y + ID_Y_OFFSET * 2, mIdPaint);
     */   Log.e("Right",""+right);
        Log.e("Left",""+left);
        Log.e("Top",""+top);
        Log.e("Bottom",""+bottom);

        RectF rectF = new RectF();
        rectF.left = left;
        rectF.right = right;
        rectF.top = top;
        rectF.bottom = bottom;

       // detectIfReady(canvas,rectF);

       // canvas.drawRect(left, top, right, bottom, mBoxPaint);

        Log.e(TAG, "draw: END");
    }
    private void logFaceData(Face mFaces, FaceTrackingListener listener) {
        float smilingProbability;
        float leftEyeOpenProbability;
        float rightEyeOpenProbability;
        float eulerY;
        float eulerZ;

        smilingProbability = mFaces.getIsSmilingProbability();
        leftEyeOpenProbability = mFaces.getIsLeftEyeOpenProbability();
        rightEyeOpenProbability = mFaces.getIsRightEyeOpenProbability();
        eulerY = mFaces.getEulerY();
        eulerZ = mFaces.getEulerZ();
           /* Log.e( "Tuts+ Face Detection", "Smiling: " + smilingProbability );
            Log.e( "Tuts+ Face Detection", "Left eye open: " + leftEyeOpenProbability );
            Log.e( "Tuts+ Face Detection", "Right eye open: " + rightEyeOpenProbability );
            Log.e( "Tuts+ Face Detection", "Euler Y: " + eulerY );*/
        Log.e( "Tuts+ Face Detection", "Euler Z: " + eulerZ );

    }

    int side_margin = 100;
    boolean isFramCreated = false;
    protected void createWindowFrame(Canvas osCanvas) {

        if (true) {


            drawBodyFrame(osCanvas);

      /* *//* if (posture!=null){
            int width = posture.getWidth();
            int height = posture.getHeight();

            if (width < osCanvas.getWidth() && height < osCanvas.getHeight()){

            }

        }*//*
            int width = osCanvas.getWidth();
            int height = osCanvas.getHeight();

            if (width < height) {
                width = width - side_margin;
                height = osCanvas.getWidth();
            } else {
                height = height - side_margin;
                width = osCanvas.getHeight();
            }

            RectF outerRectangle = new RectF(0, 0, osCanvas.getWidth(), osCanvas.getHeight());

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.argb(150, 0, 0, 0));
           // osCanvas.drawRect(outerRectangle, paint);


            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
            int canvasW = osCanvas.getWidth();
            int canvasH = osCanvas.getHeight();
            Point centerOfCanvas = new Point(canvasW / 2, canvasH / 2);

            int left = centerOfCanvas.x - (width / 3);
            int top = centerOfCanvas.y - (height / 3);
            int right = centerOfCanvas.x + (width /3);
            int bottom = centerOfCanvas.y + (height / 3);

            RectF rect = new RectF(0 , top , right , bottom );

            paint.setStrokeWidth(5);
           // osCanvas.drawOval(rect, paint);
           // osCanvas.drawRect(rect, paint);

            int circle = osCanvas.getWidth() - side_margin;

            isFramCreated = true;

            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStrokeWidth(5);
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);*/

        }
       // osCanvas.drawRect(rect, paint);

    }

    RectF customface = null;
    private void drawBodyFrame(Canvas osCanvas) {
        try {

            Paint mBoxPaint1 = new Paint();
            mBoxPaint1.setColor(Color.WHITE);
            mBoxPaint1.setStyle(Paint.Style.STROKE);
            mBoxPaint1.setStrokeWidth(5);
            if (posture!=null){
                int marginTop = (osCanvas.getHeight() - osCanvas.getWidth())/2;
                Bitmap postureScaled = Bitmap.createScaledBitmap(posture,osCanvas.getWidth(),osCanvas.getWidth(),false);
                osCanvas.drawBitmap(postureScaled,0,marginTop,mBoxPaint1);




                RectF rect = new RectF(0 , 0 , osCanvas.getWidth() , marginTop );
                Paint blackPaint = new Paint();
                blackPaint.setColor(Color.BLACK);
                blackPaint.setStyle(Paint.Style.FILL);

                osCanvas.drawRect(rect,blackPaint);

                RectF rect2 = new RectF(0 , osCanvas.getWidth()+marginTop , osCanvas.getWidth() , osCanvas.getHeight());

                osCanvas.drawRect(rect2,blackPaint);


                mBoxPaint.setColor(Color.BLACK);
                mBoxPaint.setStrokeWidth(5);
                int part1 =osCanvas.getWidth()/3;

                PointF centerOfCanvas = new PointF((osCanvas.getWidth()/3),marginTop);
                int left = (int) centerOfCanvas.x - 30;
                int top = (int) centerOfCanvas.y + 100;
                int right = (int) (centerOfCanvas.x + (osCanvas.getWidth()/3)) + 30;
                int bottom = (int) (centerOfCanvas.y + (osCanvas.getWidth()/2)) + 30;

                bottom = bottom + marginTop/2;
                customface = new RectF( left, top , right , bottom);
               // osCanvas.drawRect(customface,mBoxPaint);
                // osCanvas.drawOval(rect, paint);

            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    int time = 0;
    boolean isCapturing = false;

    public void setCapturing(boolean capturing) {
        isCapturing = capturing;

    }

    protected void detectIfReady(Canvas canvas, RectF faceRect){

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        if (width < height) {
            width = width - side_margin;
            height = canvas.getWidth();
        }else {
            height = height - side_margin;
            width = canvas.getHeight();
        }

        int canvasW = canvas.getWidth();
        int canvasH = canvas.getHeight();
        Point centerOfCanvas = new Point(canvasW / 2, canvasH / 2);

        int left = centerOfCanvas.x - (width / 3);
        int top = centerOfCanvas.y - (height / 3);
        int right = centerOfCanvas.x + (width / 3);
        int bottom = centerOfCanvas.y + (height /3);
        Rect rect = new Rect(left, top, right, bottom);


        if (customface.top < faceRect.top
                && customface.left < faceRect.left
                && customface.bottom > faceRect.bottom
                && customface.right > faceRect.right

                ){

          /*  if (isVerSmall(rect,faceRect)) {
                Log.e(TAG, "detectIfReady: Small Face");
                return;
            }*/

            Log.e(TAG, " READY_TO_CAPTURE....." );
            captureReadyCallback.onFaceQuickReady();
            time++;

            if (time >= 5 && !isCapturing){
                if (captureReadyCallback!=null)
                {

                    captureReadyCallback.onFaceReadyInCenter(rect);
                    time = 0;
                }
            }

        } else {
            if (!isCapturing && captureReadyCallback!=null)
            {
                captureReadyCallback.onFaceNoReady();
            }
        }
    }

    private boolean isVerSmall(Rect rect, RectF faceRect) {

        try {
            double topMargin = faceRect.top - rect.top;
            double leftMargin = faceRect.left - rect.left;
            double bottomMargin = rect.bottom - faceRect.bottom;
            double rightMargin = rect.right - faceRect.right;

            Log.e(TAG, "isVerSmall: "+rightMargin+" "+bottomMargin+" "+topMargin+" "+leftMargin);
            if (topMargin > 100 || leftMargin > 100 || bottomMargin > 100 || rightMargin > 100)
                return true;
            else
                return false;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }


    private String createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory() + "/KOGHA");

        if (!direct.exists()) {
            direct.mkdirs();
        }

        File file = new File(Environment.getExternalStorageDirectory() + "/KOGHA", fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            return file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
