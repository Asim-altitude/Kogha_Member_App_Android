package asim.tgs_member_app.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

/**
 * Created by Asim Shahzad on 12/29/2017.
 */
public class ImageRotation
{


    public static Bitmap getProperBitmap(String path, Bitmap bmp)
    {
        try {
            ExifInterface ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(bmp, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(bmp, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(bmp, 270);
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    return bmp;
            }

        }
        catch (Exception e)
        {
            Log.e("rotation error",""+e.getMessage());
            return null;
        }


    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

}
