package com.bodhitech.it.lib_base.lib_base.modules.loggers;

import androidx.annotation.Nullable;

public interface BaseLogger {

    void d(@Nullable Object tag, String msg);

    void e(@Nullable Object tag, String msg);

    void i(@Nullable Object tag, String msg);

    void v(@Nullable Object tag, String msg);

    void w(@Nullable Object tag, String msg);

    void net(@Nullable Object tag, String msg);

}
