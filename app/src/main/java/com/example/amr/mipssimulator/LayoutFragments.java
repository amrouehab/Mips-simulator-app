package com.example.amr.mipssimulator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class LayoutFragments extends Fragment {
    private boolean FirstCreate=true;
    public View RegisterAndMemoryTableView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(FirstCreate) {
            RegisterAndMemoryTableView =inflater.inflate(this.getArguments().getInt("LayoutID"),null);
            FirstCreate=false;
            ((MainActivity)getActivity()).registerAndMemoryHandler.assignLayouts(RegisterAndMemoryTableView);
            if(getArguments().getInt("LayoutID")==R.layout.reg_ly)((MainActivity)getActivity()).registerAndMemoryHandler.buildRegistersLayout();

        }
        if(getArguments().getInt("LayoutID")==R.layout.memory_ly)((MainActivity)getActivity()).registerAndMemoryHandler.buildMemorlayoutWithInst(((MainActivity)getActivity()).instructionsHandler);


        return RegisterAndMemoryTableView;
    }
}
