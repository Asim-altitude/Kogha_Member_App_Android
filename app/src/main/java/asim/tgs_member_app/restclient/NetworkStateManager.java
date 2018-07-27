package asim.tgs_member_app.restclient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by sohaibkhalid on 11/1/16.
 *
 */

public class NetworkStateManager {

    public boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if((networkInfo != null && networkInfo.isConnected())) {
            return true;
        }
        else return false;
    }

}
