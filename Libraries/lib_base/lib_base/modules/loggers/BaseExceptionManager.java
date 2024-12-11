package com.bodhitech.it.lib_base.lib_base.modules.loggers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface BaseExceptionManager {

    // Exceptions Importance Levels
    int EXCEPTION_LEVEL_LOW = 0x1;
    int EXCEPTION_LEVEL_MEDIUM = 0x2;
    int EXCEPTION_LEVEL_HIGH = 0x3;
    // Exceptions
    String EXCEPTION_SOCKET_TIMEOUT = "SocketTimeoutException";
    String EXCEPTION_UNKNOWN_HOST = "UnknownHostException";
    String EXCEPTION_TIMEOUT = "Timeout";

    <T extends Exception> void onException(@Nullable Object tag, @NonNull T exception, int lvl);

    <T extends Exception> void onNetworkException(@Nullable Object tag, @NonNull T exception);

}
