package com.example.amr.mipssimulator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class LayoutFragments extends Fragment {
    private boolean FirstCreate=true;
    public View LayoutView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(FirstCreate) {

            FirstCreate=false;

            if(getArguments().getInt("LayoutID")==R.layout.reg_ly) LayoutView =((MainActivity)getActivity()).registerMemoryCycleDataHandler.RegMainLy;

        }
        if(getArguments().getInt("LayoutID")==R.layout.memory_ly) LayoutView =((MainActivity)getActivity()).registerMemoryCycleDataHandler.MemoryMainLy;
        if(getArguments().getInt("LayoutID")==R.layout.cycle_layout) LayoutView =((MainActivity)getActivity()).registerMemoryCycleDataHandler.CycleDataLy;


        return LayoutView;
    }
}
