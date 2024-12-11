package com.bodhitech.it.lib_base.lib_base.modules.shared_preferences;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bodhitech.it.lib_base.lib_base.BaseConstants;

import net.grandcentrix.tray.TrayPreferences;

public class BaseSessionPreferences extends TrayPreferences {

    protected static final String SESSION = BaseConstants.BASE_TAG_EXTRA + ".session";
    // Module Name
    private static final String MODULE_NAME_SESSION_PREFERENCES = "session-preferences";

    // Constants Values
    public static final int INT_UNDEFINED = -0x1;

    public BaseSessionPreferences(@NonNull Context context) {
        super(context, MODULE_NAME_SESSION_PREFERENCES, 0x1);
    }

}
