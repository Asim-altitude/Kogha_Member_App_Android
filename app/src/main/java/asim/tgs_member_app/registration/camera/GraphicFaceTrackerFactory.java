package asim.tgs_member_app.registration.camera;


import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;

import asim.tgs_member_app.registration.camera.camera_files.GraphicOverlay;


/**
 * Created by redcarpet on 4/18/17.
 */

public class GraphicFaceTrackerFactory  implements MultiProcessor.Factory<Face> {
    GraphicOverlay overlay;

    public GraphicFaceTrackerFactory(GraphicOverlay graphicOverlay)
    {
        this.overlay = graphicOverlay;

    }
    @Override
    public Tracker<Face> create(Face face) {
        return new GraphicFaceTracker(overlay);
    }
}