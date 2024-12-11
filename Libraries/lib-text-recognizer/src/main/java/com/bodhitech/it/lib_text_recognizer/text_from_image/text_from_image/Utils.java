package com.bodhitech.it.lib_text_recognizer.text_from_image.text_from_image;

import android.util.Log;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static boolean checkIsDouble(String str){
        try{
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException nfE){
            Log.e(TAG, nfE.getMessage());
        }
        return false;
    }

}
