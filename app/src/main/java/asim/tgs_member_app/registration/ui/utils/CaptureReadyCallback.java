package asim.tgs_member_app.registration.ui.utils;

import android.graphics.Rect;
import android.graphics.RectF;

public interface CaptureReadyCallback
{
    void onFaceReadyInCenter(String path);

    void onFaceReadyInCenter(RectF rectF);
    void onFaceReadyInCenter(Rect rectF);
    void onFaceNoReady();
    void onFaceQuickReady();
}
