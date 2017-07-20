package kyi.boost3;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Kyu on 2017-07-20.
 */

public class PermissionCheck {
    public static final int REQUEST_APP_PERMISSIONS = 1;

    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    public static boolean verifyStoragePermissions(Activity activity) {
        int fineLocation = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocation = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);

        if(fineLocation != PackageManager.PERMISSION_GRANTED || coarseLocation != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_APP_PERMISSIONS);
            return true;
        }
        return false;
    }
}
