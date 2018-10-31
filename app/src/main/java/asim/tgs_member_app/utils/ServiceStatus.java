package asim.tgs_member_app.utils;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by PC-GetRanked on 9/18/2018.
 */

public class ServiceStatus
{
    public static boolean isServiceRunning(Context context,Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
