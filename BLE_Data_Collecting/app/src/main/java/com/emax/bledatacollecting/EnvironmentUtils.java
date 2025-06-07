package com.emax.bledatacollecting;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

public class EnvironmentUtils {

    public static boolean isPermissionGranted(@NonNull String[] grantPermissions, @NonNull int[] grantResults, @NonNull String permission) {
        for (int i = 0; i < grantPermissions.length; i++) {
            if (permission.equals(grantPermissions[i])) {
                return grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
        }
        return false;
    }

}
