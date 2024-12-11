package com.bodhitech.it.lib_base.lib_base.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fondesa.lyra.Lyra;

public class BaseFragment extends Fragment {

    //region [#] Override Lifecycle Methods
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Lyra.instance().restoreState(this, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Lyra.instance().saveState(this, outState);
    }
    //endregion

}
