package asim.tgs_member_app.registration.camera;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import asim.tgs_member_app.R;
import asim.tgs_member_app.registration.camera.camera_files.CameraSourcePreview;
import asim.tgs_member_app.registration.camera.camera_files.GraphicOverlay;
import asim.tgs_member_app.registration.ui.utils.CaptureReadyCallback;

public class CaptureFrontBodyScreen extends AppCompatActivity implements CaptureReadyCallback {
    private static final String TAG = "FaceTracker";
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;


    private ImageView capture_image;
    FaceDetector faceDetector;
    ImageView manual_capture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_face_screen);

        faceDetector = new
                FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(false)
                .build();

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);
        capture_image = findViewById(R.id.capture);
        manual_capture = findViewById(R.id.capture_manual);
        manual_capture.setVisibility(View.VISIBLE);

        manual_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] bytes) {

                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        try {
                            String mainpath = Environment.getExternalStorageDirectory() + "/Profile_";
                            File basePath = new File(mainpath);
                            if (!basePath.exists())
                                Log.d("CAPTURE_BASE_PATH", basePath.mkdirs() ? "Success" : "Failed");
                            final File captureFile = new File(mainpath + ".jpg");
                            if (!captureFile.exists())
                                Log.d("CAPTURE_FILE_PATH", captureFile.createNewFile() ? "Success" : "Failed");
                            FileOutputStream stream = new FileOutputStream(captureFile);
                            stream.write(bytes);
                            stream.flush();
                            stream.close();

                            mPreview.stop();

                            getCameraPhotoOrientation(captureFile.getAbsolutePath());
                            ((ImageView)findViewById(R.id.capturedImageView)).setImageURI(Uri.parse(captureFile.getAbsolutePath()));
                            findViewById(R.id.capturedLayout).setVisibility(View.VISIBLE);

                            findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setData(Uri.parse(captureFile.getAbsolutePath()));
                                    setResult(RESULT_OK,intent);

                                    finish();
                                }
                            });
                            findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            });
                            findViewById(R.id.retake).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    isTaken = false;
                                    manual_capture.setVisibility(View.VISIBLE);
                                    findViewById(R.id.capturedLayout).setVisibility(View.GONE);
                                    ((ImageView)findViewById(R.id.capturedImageView)).setImageDrawable(null);
                                    startCameraSource();
                                }
                            });

                            manual_capture.setVisibility(View.GONE);
                       /* Intent intent = new Intent();
                        intent.setData(Uri.parse(path));
                        setResult(RESULT_OK,intent);

                        finish();*/
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        capture_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");
        final String[] permissions = new String[]{Manifest.permission.CAMERA};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, "Provide Camera Permission",
                Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", listener)
                .show();
    }

    boolean mIsFacingFront = false;

    private void createCameraSource() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_LANDMARKS)
                .setTrackingEnabled(true)
                .setMode(FaceDetector.FAST_MODE)
                .setProminentFaceOnly(true)
                .setMinFaceSize(mIsFacingFront ? 0.35f : 0.15f)
                .build();
        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }
        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mPreview.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage("No Camera Permission")
                .setPositiveButton("Ok", listener)
                .show();
    }


    private void startCameraSource() {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }


    @Override
    public void onFaceReadyInCenter(String path) {
        Log.e(TAG, "onFaceReadyInCenter: "+path);
        isTaken = true;

    }

    @Override
    public void onFaceQuickReady() {
        try {
            ((ImageView)findViewById(R.id.capture)).setImageResource(R.drawable.ico_tick);
            //((ImageView)findViewById(R.id.capture)).setColorFilter(Color.GREEN);
            findViewById(R.id.capture).setVisibility(View.VISIBLE);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onFaceNoReady() {
        try {
            ((ImageView)findViewById(R.id.capture)).setVisibility(View.GONE);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    boolean isTaken = false;
    @Override
    public void onFaceReadyInCenter(final Rect rectF) {
        try {
            if (isTaken)
                return;

            isTaken = true;


            findViewById(R.id.capture).setVisibility(View.VISIBLE);
            mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] bytes) {

                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    try {
                        String mainpath = Environment.getExternalStorageDirectory() + "/Profile_";
                        File basePath = new File(mainpath);
                        if (!basePath.exists())
                            Log.d("CAPTURE_BASE_PATH", basePath.mkdirs() ? "Success" : "Failed");
                        final File captureFile = new File(mainpath + ".jpg");
                        if (!captureFile.exists())
                            Log.d("CAPTURE_FILE_PATH", captureFile.createNewFile() ? "Success" : "Failed");
                        FileOutputStream stream = new FileOutputStream(captureFile);
                        stream.write(bytes);
                        stream.flush();
                        stream.close();

                        mPreview.stop();

                        getCameraPhotoOrientation(captureFile.getAbsolutePath());
                        ((ImageView)findViewById(R.id.capturedImageView)).setImageURI(Uri.parse(captureFile.getAbsolutePath()));
                        findViewById(R.id.capturedLayout).setVisibility(View.VISIBLE);

                        findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setData(Uri.parse(captureFile.getAbsolutePath()));
                                setResult(RESULT_OK,intent);

                                finish();
                            }
                        });
                        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                        findViewById(R.id.retake).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isTaken = false;
                                findViewById(R.id.capturedLayout).setVisibility(View.GONE);
                                ((ImageView)findViewById(R.id.capturedImageView)).setImageDrawable(null);
                                startCameraSource();
                            }
                        });

                       /* Intent intent = new Intent();
                        intent.setData(Uri.parse(path));
                        setResult(RESULT_OK,intent);

                        finish();*/
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Bitmap createFlippedBitmap(Bitmap source, boolean xFlip, boolean yFlip) {
        Matrix matrix = new Matrix();
        matrix.postScale(xFlip ? -1 : 1, yFlip ? -1 : 1, source.getWidth() / 2f, source.getHeight() / 2f);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    public String getCameraPhotoOrientation(String imagePath){
        int rotate = 0;
        try {
            //context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Matrix matrix = new Matrix();

            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            matrix.postRotate(rotate);

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

            Bitmap fliped = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

            Bitmap rotatedBitmap = createFlippedBitmap(fliped,true,false);

          /*  Frame frame = new Frame.Builder().setBitmap(rotatedBitmap).build();
            SparseArray<Face> faces = faceDetector.detect(frame);
            float x1 = faces.get(0).getPosition().x;
            float y1 = faces.get(0).getPosition().y;
            float x2 = x1 + faces.get(0).getWidth();
            float y2 = y1 + faces.get(0).getHeight();*/

           int width = rotatedBitmap.getWidth();
           int height = rotatedBitmap.getHeight();
           Log.e(TAG, "getCameraPhotoOrientation: w "+width+" h"+height);

           Bitmap dstBmp = null;

            if (rotatedBitmap.getWidth() >= rotatedBitmap.getHeight()){

                dstBmp = Bitmap.createBitmap(
                        rotatedBitmap,
                        rotatedBitmap.getWidth()/2 - rotatedBitmap.getHeight()/2,
                        0,
                        rotatedBitmap.getHeight(),
                        rotatedBitmap.getHeight()
                );

            }else{

                dstBmp = Bitmap.createBitmap(
                        rotatedBitmap,
                        0,
                        rotatedBitmap.getHeight()/2 - rotatedBitmap.getWidth()/2,
                        rotatedBitmap.getWidth(),
                        rotatedBitmap.getWidth()
                );
            }

            int d_width = dstBmp.getWidth();
            int margin = d_width/30;

            d_width = d_width - (margin*2);
            Bitmap crpod =  Bitmap.createBitmap(dstBmp, margin, margin, d_width, d_width);


            FileOutputStream out = new FileOutputStream(imagePath);
            crpod.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;
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

        return file.getAbsolutePath();
    }


    @Override
    public void onFaceReadyInCenter(RectF rectF) {

    }

    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face>{
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    private GraphicOverlay mOverlay;
    private BodyFaceGraphic mFaceGraphic;
    private class GraphicFaceTracker extends Tracker<Face>  {

        private boolean mPreviousIsLeftEyeOpen = true;
        private boolean mPreviousIsRightEyeOpen = true;
        private volatile FaceData mFaceData;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new BodyFaceGraphic(overlay);

        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int id, Face face) {
            mFaceGraphic = new BodyFaceGraphic(mOverlay);
            mFaceGraphic.setCaptureReadyCallback(CaptureFrontBodyScreen.this);
            mFaceGraphic.setBitmap(CaptureFrontBodyScreen.this);
            mFaceGraphic.setCapturing(isTaken);

        }



        // 2
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            if (mOverlay!=null && mFaceGraphic!=null)
                mOverlay.remove(mFaceGraphic);

            mOverlay.add(mFaceGraphic);



            if (mFaceData == null)
                mFaceData = new FaceData();


            if (face != null) {

                mFaceGraphic.updateFace(face);
            }
        }

        // 3
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mFaceGraphic = new BodyFaceGraphic(mOverlay);
            mFaceGraphic.setCaptureReadyCallback(CaptureFrontBodyScreen.this);
            mFaceGraphic.setBitmap(CaptureFrontBodyScreen.this);
            mFaceGraphic.isCapturing = isTaken;
        }

        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }

        private PointF getLandmarkPosition(Face face, int landmark) {
            PointF myLandmark = null;
            for (Landmark landmark1 : face.getLandmarks()) {
                if (landmark1.getType() == landmark)
                    myLandmark = landmark1.getPosition();
            }

            return myLandmark;
        }
    }


}
