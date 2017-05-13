package com.example.amr.mipssimulator;

import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

public class InstructionExcutor {
    private final Register_Memory_CycleData_Handler registerMemoryCycleDataHandler;
    private final InstructionsHandler instructionsHandler;



    public InstructionExcutor(Register_Memory_CycleData_Handler registerMemoryCycleDataHandler, InstructionsHandler instructionsHandler) {
        this.registerMemoryCycleDataHandler = registerMemoryCycleDataHandler;
        this.instructionsHandler=instructionsHandler;
    }

    void Excute(HashMap<String,String> inst,int instOrder){
      switch(inst.get("inst")){
          case "add":executeAdd(inst.get("R0Address"),inst.get("R1Address"),inst.get("R2Address"));break;
          case "addi":excuteAddi(inst.get("R0Address"),inst.get("R1Address"), Integer.parseInt(inst.get("R2")));break;
          case "and":excuteAnd(inst.get("R0Address"),inst.get("R1Address"),inst.get("R2Address"));break;
          case "andi":excuteAndi(inst.get("R0Address"),inst.get("R1Address"), Integer.parseInt(inst.get("R2")));break;
          case "sll":excuteSll(inst.get("R0Address"),inst.get("R1Address"),inst.get("R2Address"));break;
          case "beq":excuteBeq(inst.get("R0Address"),inst.get("R1Address"),inst.get("R2"));break;
          case "or":executeOr(inst.get("R0Address"),inst.get("R1Address"),inst.get("R2Address"));break;
          case "ori":excuteOri(inst.get("R0Address"),inst.get("R1Address"), Integer.parseInt(inst.get("R2")));break;
          case "sw":
              executeSw(inst.get("R0Address"),inst.get("R2Address"), Integer.parseInt(inst.get("R1")));break;
          case "slt":excuteSlt(inst.get("R0Address"),inst.get("R1Address"),inst.get("R2Address"));break;
          case "nor":executeNor(inst.get("R0Address"),inst.get("R1Address"),inst.get("R2Address"));break;
          case "j":executeJFormat(inst.get("R0"));break;
          case "jal": executeJFormat(inst.get("R0"));break;
          case "jr":excuteJr(inst.get("R0Address"));break;
          case "lw":executeLw(inst.get("R0Address"),inst.get("R2Address"), Integer.parseInt(inst.get("R1")));break;







      }


  }

    private void excuteBeq(String r0Address, String r1Address, String r2) {
        int r1Value,r2Value;
        r1Value= Integer.parseInt(((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).getText().toString());
        r2Value= Integer.parseInt(((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r1Address))).getText().toString());
        if(r1Value==r2Value){

executeJFormat(r2);
        }

    }

    private void excuteJr(String r0Address) {


    }

    private void excuteSlt(String r0Address, String r1Address, String r2Address) {
        int r1Value,r2Value;
        r1Value= Integer.parseInt(((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r1Address))).getText().toString());
        r2Value= Integer.parseInt(((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r2Address))).getText().toString());
if(r1Value<r2Value)((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).setText("1");
else ((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).setText("0");

        ((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).setText(String.valueOf(r1Value+r2Value));
        instructionsHandler.mainActivity.ProgramCounter++;
    }

    private void excuteSll(String r0Address, String r1Address, String r2Address) {

    }



    private void executeJFormat(String r0) {
        int branchingToInstPos=0;
        int startPos=0;
        for(int i = 0; i<instructionsHandler.InstNamesPos.size(); i++){
            if(instructionsHandler.InstNamesPos.get(i).containsKey(r0+':')){
                branchingToInstPos=instructionsHandler.InstNamesPos.get(i).get(r0+':');
                startPos=instructionsHandler.InstNamesPos.get(i).get("startPos");
                break;
            }
        }
    instructionsHandler.mainActivity.ProgramCounter =branchingToInstPos;
        instructionsHandler.mainActivity.pos1=startPos;

    }

    private void executeLw(String r0Address, String r2Address, int r1) {
        int address;
        address= Integer.parseInt(((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r2Address))).getText().toString())+r1;
        String r1Value=((TextView) registerMemoryCycleDataHandler.MemoryValue.getChildAt(address/4)).getText().toString();
        ((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).setText(r1Value);

        instructionsHandler.mainActivity.ProgramCounter++;
    }

    private void executeSw(String r0Address, String r2Address, int r1) {
        int address;
        address= Integer.parseInt(((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r2Address))).getText().toString())+r1;
        String r1Value=((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).getText().toString();

        ((TextView) registerMemoryCycleDataHandler.MemoryValue.getChildAt(address/4)).setText(r1Value);
        instructionsHandler.mainActivity.ProgramCounter++;

    }

    private void executeNor(String r0Address, String r1Address, String r2Address) {
        int r1Value,r2Value;
        r1Value= Integer.parseInt(((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r1Address))).getText().toString());
        r2Value= Integer.parseInt(((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r2Address))).getText().toString());
        ((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).setText(String.valueOf(r1Value|r2Value));
        String CycleDataValue[]=new String[]{"0", String.valueOf(r1Value), String.valueOf(r2Value),String.valueOf(r1Value+r2Value),"0","0","1","0","0","0",
                "0","1","1","0","0"};
        fillCycleDataViewsWithData(CycleDataValue);
        instructionsHandler.mainActivity.ProgramCounter++;

    }

    private void excuteOri(String r0Address, String r1Address, int r2) {
        int r1Value;
        r1Value= Integer.parseInt(((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r1Address))).getText().toString());
        ((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).setText(String.valueOf(r1Value|r2));
        instructionsHandler.mainActivity.ProgramCounter++;
    }

    private void executeOr(String r0Address, String r1Address, String r2Address) {
        int r1Value,r2Value;
        r1Value= Integer.parseInt(((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r1Address))).getText().toString());
        r2Value= Integer.parseInt(((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r2Address))).getText().toString());
        ((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).setText(String.valueOf(r1Value|r2Value));
        String CycleDataValue[]=new String[]{"0", String.valueOf(r1Value), String.valueOf(r2Value),String.valueOf(r1Value+r2Value),"0","0","1","0","0","0",
                "0","1","1","0","0"};
        fillCycleDataViewsWithData(CycleDataValue);
        instructionsHandler.mainActivity.ProgramCounter++;
    }

    private void excuteAndi(String r0Address, String r1Address, int r2) {
        int r1Value;
        r1Value= Integer.parseInt(((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r1Address))).getText().toString());
        ((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).setText(String.valueOf(r1Value&r2));
        instructionsHandler.mainActivity.ProgramCounter++;
    }

    private void excuteAnd(String r0Address, String r1Address, String r2Address) {
        int r1Value,r2Value;
        r1Value= Integer.parseInt(((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r1Address))).getText().toString());
        r2Value= Integer.parseInt(((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r2Address))).getText().toString());
        ((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).setText(String.valueOf(r1Value&r2Value));
        String CycleDataValue[]=new String[]{"0", String.valueOf(r1Value), String.valueOf(r2Value),String.valueOf(r1Value+r2Value),"0","0","1","0","0","0",
                "0","1","1","0","0"};
        fillCycleDataViewsWithData(CycleDataValue);
        instructionsHandler.mainActivity.ProgramCounter++;

    }

    private void excuteAddi(String r0Address, String r1Address, int r2) {
        int r1Value;
        r1Value= Integer.parseInt(((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r1Address))).getText().toString());
        ((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).setText(String.valueOf(r1Value+r2));
        instructionsHandler.mainActivity.ProgramCounter++;
    }

    private void executeAdd(String R0Address, String R1Address, String R2Address) {
        int r1Value,r2Value;
        r1Value= Integer.parseInt(((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(R1Address))).getText().toString());
        r2Value= Integer.parseInt(((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(R2Address))).getText().toString());
        ((TextView) registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(R0Address))).setText(String.valueOf(r1Value+r2Value));
        String CycleDataValue[]=new String[]{"0", String.valueOf(r1Value), String.valueOf(r2Value),String.valueOf(r1Value+r2Value),"0","0","1","0","0","0",
                "0","1","1","0","0"};
        fillCycleDataViewsWithData(CycleDataValue);
        instructionsHandler.mainActivity.ProgramCounter++;
    }

    private void fillCycleDataViewsWithData(String[] cycleDataValue) {

        for(int i=0;i<cycleDataValue.length;i++){
            ((TextView)registerMemoryCycleDataHandler.DataValue.getChildAt(i)).setText(cycleDataValue[i]);



        }
    }

}
