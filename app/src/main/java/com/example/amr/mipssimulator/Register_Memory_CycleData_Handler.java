package com.example.amr.mipssimulator;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

 class Register_Memory_CycleData_Handler {
     public View CycleDataLy;
     String[] RegistersCode =new String[]{"$0","$s0","$s1","$s2","$s3","$s4","$s5","$s6","$s7","$v0","$sv1","$a0","$a1","$a2","$a3",
            "$t0","$t1","$t2","$t3","$t4","$t5","$t6","$t7","$t8","$t9","$sp","$ra"};
     String[] CycleDataProbertise =new String[]{"opcode","rs","rt","rd","shamt","funct","RegDest","branch","MemRead","MemWrite","MemToReg",
             "AluOp","AluSrc","RegWrite","jump","Immediate"};
  LinearLayout RegisterName,RegisterValue,MemoryAddress,MemoryValue,DataName,DataValue;;
    View RegMainLy,MemoryMainLy;
     TextView InstViewr ;
    private MainActivity Main;


     Register_Memory_CycleData_Handler(MainActivity main) {
        Main=main;
        RegMainLy = Main.getLayoutInflater().inflate(R.layout.reg_ly,null);//bya5od el scrollview
        MemoryMainLy = Main.getLayoutInflater().inflate(R.layout.memory_ly,null);
         CycleDataLy=Main.getLayoutInflater().inflate(R.layout.cycle_layout,null);//bya5od el scrollview
         RegisterName = (LinearLayout) RegMainLy.findViewById(R.id.regName);
        RegisterName.setTag("Reg");
        RegisterValue = (LinearLayout) RegMainLy.findViewById(R.id.regValue);
        MemoryAddress = (LinearLayout) MemoryMainLy.findViewById(R.id.memoryname);
        MemoryAddress.setTag("mem");
        MemoryValue = (LinearLayout) MemoryMainLy.findViewById(R.id.memoryvalue);
         DataName = (LinearLayout) CycleDataLy.findViewById(R.id.Name);
         DataName.setTag("data");
         DataValue = (LinearLayout) CycleDataLy.findViewById(R.id.Value);
        AddViewsToLy("Address","Value",27,RegisterName,RegisterValue);
        AddViewsToLy("Address","Value",100,MemoryAddress,MemoryValue);
         AddViewsToLy("Property","Value",16,DataName,DataValue);
         InstViewr=(TextView)CycleDataLy.findViewById(R.id.instViewer);

    }







    private void AddViewsToLy(String NameHeader,String ValueHeader,int size, LinearLayout NameLy, LinearLayout ValueLy) {
        String Text ="",Tag="";
        for(int i=-1;i<size;i++){
            for(int p=0;p<2;p++){
               if(i!=-1) {
                   if (NameLy.getTag().equals("Reg")) {
                       if(i==0||i==27)Tag="NotEditable";
                       else Tag="Editable";
                      Text = RegistersCode[i];
                   }
                   if (NameLy.getTag().equals("mem")){
                       Tag="Editable";
                       Text = String.valueOf(i * 4);
                   }
                   if (NameLy.getTag().equals("data")){
                       Tag="NotEditable";
                       Text = CycleDataProbertise[i];
                   }
               }


            if(p==0){
                    if(i==-1) Text = NameHeader;
                       NameLy.addView(CreateViewFormemoryAndRegister(Text,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,Color.BLACK,"NotEditable"));
                }
                else{
                    if(i==-1){
                        Text=ValueHeader;
                        ValueLy.addView(CreateViewFormemoryAndRegister(Text,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,Color.BLACK,"NotEditable"));
                    }else {
                        if(NameLy.getTag().equals("Reg")&&i==27)Text="-1";
                        else Text="0";
                        ValueLy.addView(CreateViewFormemoryAndRegister(Text,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Color.BLACK,Tag));
                    }

                }


            }


        }
    }
    void addInstToMemory(InstructionsHandler instructionsHandler) {
        String ValueText = "";
       clearMemory();
        HashMap<String, String> instructionData ;
        for (int i = 0; i < instructionsHandler.DataAndInstMemory.size(); i++) {
                instructionData = instructionsHandler.DataAndInstMemory.get(i);

                    if (instructionData.get("format").equals("R")) {
                        ValueText = instructionData.get("inst") + " "+ instructionData.get("R0") + " " + instructionData.get("R1") + " " + instructionData.get("R2");
                    }
                    if (instructionData.get("format").equals("I")) {
                        ValueText = instructionData.get("inst") + " "+ instructionData.get("R0") + " " + instructionData.get("R1") + " " + instructionData.get("R2");

                    }
                    if (instructionData.get("format").equals("J")) {
                        ValueText = instructionData.get("inst") + " "+ instructionData.get("R0") ;

                    }

                ((TextView)MemoryValue.getChildAt(i+1)).setText(ValueText);

            }

        }

    private void clearMemory() {
        for (int i = 1; i < MemoryValue.getChildCount(); i++) {
            ((TextView)MemoryValue.getChildAt(i)).setText("0");
        }
    }


    private TextView CreateViewFormemoryAndRegister(String text, int Height, int Width, int TextColor, String tag) {
        TextView tv=new TextView(Main);
        tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(Width, Height,2));
        tv.setTextSize(23);
        tv.setSingleLine();
        tv.setTag(tag);
        tv.setTextColor(TextColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        tv.setText(text);
        tv.setBackgroundResource(R.drawable.rect_shape);
        tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
final EditText newValue=new EditText(Main);
                newValue.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                if(view.getTag().equals("Editable")){
                    if(Main.ExcutionIsInProgress)
                        Toast.makeText(Main, "Cannot changing values while Execution is in progress", Toast.LENGTH_LONG).show();
                    final android.support.v7.app.AlertDialog.Builder ad = new android.support.v7.app.AlertDialog.Builder(Main);
                    ad.setTitle("Set Value")
                            .setNeutralButton("Done", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ((TextView)view).setText(newValue.getText().toString());
                                    dialog.dismiss();


                                }
                            })
                            .setView(newValue)
                            .show();




                }
                return true;
            }
        });
        return tv;

    }




}
