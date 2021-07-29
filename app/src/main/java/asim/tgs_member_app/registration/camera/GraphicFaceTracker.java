package asim.tgs_member_app.registration.camera;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import asim.tgs_member_app.registration.camera.camera_files.GraphicOverlay;


/**
 * Created by redcarpet on 4/17/17.
 */

public class GraphicFaceTracker extends Tracker<Face> {
    private GraphicOverlay mOverlay;
    private FaceGraphic mFaceGraphic;

    GraphicFaceTracker(GraphicOverlay overlay) {
        mOverlay = overlay;
        mFaceGraphic = new FaceGraphic(overlay);
    }

    @Override
    public void onNewItem(int faceId, Face item) {
        mFaceGraphic.setId(faceId);
    }

    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
        mOverlay.add(mFaceGraphic);
        mFaceGraphic.updateFace(face);
//        logFaceData(face);
    }

    @Override
    public void onMissing(FaceDetector.Detections<Face> detectionResults) {
        mOverlay.remove(mFaceGraphic);
    }

    @Override
    public void onDone() {
        mOverlay.remove(mFaceGraphic);
    }
}