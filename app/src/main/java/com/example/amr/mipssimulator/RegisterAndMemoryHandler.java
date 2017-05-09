package com.example.amr.mipssimulator;


import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

public class RegisterAndMemoryHandler {
    public String[] RegistersCode =new String[]{"$0","$s0","$s1","$s2","$s3","$s4","$s5","$s6","$s7","$vo","$sv1","$a0","$a1","$a2","$a3",
            "$t0","$t1","$t2","$t3","$t4","$t5","$t6","$t7","$t8","$t9","$sp","$ra"};
public LinearLayout RegisterName,RegisterValue,MemoryAddress,MemoryValue;
    private Activity Main;

    public RegisterAndMemoryHandler(Activity main) {

        Main=main;

    }



    void buildRegistersLayout() {
        String Text ;
        for(int i=0;i<27;i++){

            for(int p=0;p<2;p++){
                Text=RegistersCode[i];
                if(p==0){
                    if(i==0) Text = "RegisterName";
                       RegisterName.addView(addRegisterNameView(Text,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,Color.BLACK));
                }
                else{
                    if(i==0){
                        Text="RegisterValue";
                        RegisterValue.addView(addRegisterNameView(Text,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,Color.BLACK));
                    }else {
                        Text="0";
                        RegisterValue.addView(addRegisterNameView(Text,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Color.BLACK));
                    }

                }


            }


        }
    }
    void buildMemorlayoutWithInst(InstructionsHandler instructionsHandler) {
        String adressText = "0", ValueText = "";
        MemoryAddress.removeAllViews();
        MemoryValue.removeAllViews();
        HashMap<String, String> instructionData = null;
        for (int i = -1; i < instructionsHandler.DataAndInstMemory.size(); i++) {
            if (i >= 0) {
                adressText= String.valueOf(i*4);

                instructionData = instructionsHandler.DataAndInstMemory.get(i);
            }
            for (int p = 0; p < 2; p++) {
                if (i >= 0) {
                    if (instructionData.get("format") == "R") {
                        ValueText = instructionData.get("inst") + instructionData.get("R0") + " " + instructionData.get("R1") + " " + instructionData.get("R2");
                    }
                    if (instructionData.get("format") == "I") {
                        ValueText = instructionData.get("inst") + instructionData.get("R0") + " " + instructionData.get("R1") + " " + instructionData.get("R2");

                    }
                    if (instructionData.get("format") == "J") {
                        ValueText = instructionData.get("inst") + instructionData.get("R0") + " " + instructionData.get("R1") + " " + instructionData.get("R2");

                    }
                }
                if (p == 0) {
                    if (i == -1) adressText = "Address";
                    MemoryAddress.addView(addRegisterNameView(adressText, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Color.BLACK));

                } else {
                    if (i == -1) ValueText = "Value";
                    MemoryValue.addView(addRegisterNameView(ValueText, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Color.BLACK));

                }


            }

        }
        addSomeMemoryBlock(instructionsHandler.DataAndInstMemory.size(),instructionsHandler.DataAndInstMemory.size()+50);
    }

    private void addSomeMemoryBlock(int x, int size) {

        for (int i = x; i < size; i++) {
            for (int p = 0; p < 2; p++) {
                if (p == 0) {
                    MemoryAddress.addView(addRegisterNameView(String.valueOf(i * 4), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Color.BLACK));

                } else {

                    MemoryValue.addView(addRegisterNameView("0", ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Color.BLACK));

                }


            }


        }
        }


    private TextView addRegisterNameView(String text, int Height, int Width, int TextColor) {

        TextView tv=new TextView(Main);
        tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(Width, Height,2));
        tv.setTextSize(23);
        tv.setSingleLine();
        tv.setTextColor(TextColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        tv.setText(text);
        tv.setBackgroundResource(R.drawable.rect_shape);
        return tv;

    }


    public void assignLayouts(View layout) {
        if (RegisterName == null) {
            RegisterName = (LinearLayout) layout.findViewById(R.id.regName);
            RegisterValue = (LinearLayout) layout.findViewById(R.id.regValue);

        }
        if (MemoryAddress == null) {
            MemoryAddress = (LinearLayout) layout.findViewById(R.id.memoryname);
            MemoryValue = (LinearLayout) layout.findViewById(R.id.memoryvalue);
        }
    }

}
