package com.example.amr.mipssimulator;

import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;


//this class is responsible for execution of each instruction
//this is behaves like the control Unit
class InstructionExecutor {
    private final InstructionsHandler instructionsHandler;
    private ArrayList<Integer> StackMemoryForHighlightingTextFirstPos =new ArrayList<>();


   InstructionExecutor(InstructionsHandler instructionsHandler) {
        this.instructionsHandler=instructionsHandler;
    }
// this function takes the inst and its data and execute it
    void Execute(HashMap<String, String> inst){
            // this switch takes the inst name and execute it Just like The controller in the dataPath
            switch (inst.get("inst")) {//gets the current instruction to deal with (here we get it by name not by op code and Func Just for simplicity)
                // execute the inst by passing its Register address  or offsets if its an i format or instLabel if is J format
                case "add":
                    executeAdd(inst.get("R0Address"), inst.get("R1Address"), inst.get("R2Address"));
                    break;
                case "addi":
                    executeAddi(inst.get("R0Address"), inst.get("R1Address"), Integer.parseInt(inst.get("R2")));
                    break;
                case "and":
                    excuteAnd(inst.get("R0Address"), inst.get("R1Address"), inst.get("R2Address"));
                    break;
                case "andi":
                    executeAndi(inst.get("R0Address"), inst.get("R1Address"), Integer.parseInt(inst.get("R2")));
                    break;
                case "sll":
                    executeSll(inst.get("R0Address"), inst.get("R1Address"), inst.get("R2"));
                    break;
                case "beq":
                    executeBeq(inst.get("R0Address"), inst.get("R1Address"), inst.get("R2"));
                    break;
                case "or":
                    executeOr(inst.get("R0Address"), inst.get("R1Address"), inst.get("R2Address"));
                    break;
                case "ori":
                    executeOri(inst.get("R0Address"), inst.get("R1Address"), Integer.parseInt(inst.get("R2")));
                    break;
                case "sw":
                    executeSw(inst.get("R0Address"), inst.get("R2Address"), Integer.parseInt(inst.get("R1")));
                    break;
                case "slt":
                    executeSlt(inst.get("R0Address"), inst.get("R1Address"), inst.get("R2Address"));
                    break;
                case "nor":
                    executeNor(inst.get("R0Address"), inst.get("R1Address"), inst.get("R2Address"));
                    break;
                case "j":
                    executeJFormat(inst.get("R0"));
                    break;
                case "jal":
                    executeJal(inst.get("R0"));
                    break;
                case "jr":
                    executeJr(inst.get("R0Address"));
                    break;
                case "lw":
                    executeLw(inst.get("R0Address"), inst.get("R2Address"), Integer.parseInt(inst.get("R1")));
                    break;
                case "bne":
                    executeBne(inst.get("R0Address"), inst.get("R1Address"), inst.get("R2"));
                    break;
            }

        }

    private void executeBne(String r0Address, String r1Address, String r2) {
        int r1Value,r2Value;
        r1Value= Integer.parseInt(((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).getText().toString());
        r2Value= Integer.parseInt(((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r1Address))).getText().toString());
        if(r1Value!=r2Value){

            executeJFormat(r2);
        }
        else instructionsHandler.mainActivity.ProgramCounter++;
        String CycleDataValue[] = new String[]{"5", r0Address, r1Address, "no rd", "0", "0 ->noFunc", "X", "1", "0", "0",
                "X", "5", "0", "0","0", r2,"0"};
        fillCycleDataViewsWithData(CycleDataValue);


    }

    // this makes sure that the destination register is not $0or $ra
  private void checkIFitsNotRegZeroOrRegRa(String DestinationAddress){
      if(DestinationAddress.equals("$0")||DestinationAddress.equals("$ra")) {
          instructionsHandler.mainActivity.showMessage("Error Register{" + DestinationAddress + "}cannot be modified ");
          instructionsHandler.mainActivity.Stop(null);
      }
  }

    private void executeJal(String r0) {
        //saves the next inst to $ra register
        ((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(27)).setText(String.valueOf((instructionsHandler.mainActivity.ProgramCounter+1)*4));
        StackMemoryForHighlightingTextFirstPos.add(instructionsHandler.mainActivity.pos1);
        executeJFormat(r0);
        String CycleDataValue[] = new String[]{"3", "0 -> no rs", "0 -> no rt", "0 -> no rd", "0", "0 ->noFunc", "2", "X", "0"
                ,"0","2","X", "X", "1","1", "0 no immediate","0"};
        fillCycleDataViewsWithData(CycleDataValue);


    }

    private void executeBeq(String r0Address, String r1Address, String r2) {
        int r1Value,r2Value;
        r1Value= Integer.parseInt(((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).getText().toString());
        r2Value= Integer.parseInt(((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r1Address))).getText().toString());
        if(r1Value==r2Value){

executeJFormat(r2);
        }
        else instructionsHandler.mainActivity.ProgramCounter++;
        String CycleDataValue[] = new String[]{"4", r0Address, r1Address, "no rd", "0", "0 ->noFunc", "X", "1", "0", "0",
                "X", "4", "0", "0","0", r2,"0"};
        fillCycleDataViewsWithData(CycleDataValue);


    }


    private void executeJr(String r0Address) {
        //get the $ra value
        int raValue= Integer.parseInt(((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).getText().toString());
        //check if its the last return
        // if false pass it to PC else stop excution
        if(raValue==-1)instructionsHandler.mainActivity.Stop(null);
        else {
            // pass to PC
            instructionsHandler.mainActivity.ProgramCounter=raValue/4;
            instructionsHandler.mainActivity.pos1 = StackMemoryForHighlightingTextFirstPos.get(StackMemoryForHighlightingTextFirstPos.size() - 1);
            // now remove the last pos
            StackMemoryForHighlightingTextFirstPos.remove(StackMemoryForHighlightingTextFirstPos.size() - 1);
        }

        String CycleDataValue[] = new String[]{"0", r0Address , "0", "0", "0","8","1","0","0","0",
                "0","8","0","1","X","no imm","1"};
        fillCycleDataViewsWithData(CycleDataValue);

    }

    private void executeSlt(String r0Address, String r1Address, String r2Address) {
        int r1Value,r2Value;
        checkIFitsNotRegZeroOrRegRa(r0Address);
        // get the Register 1 value and Register 2 Value from the Register table
        r1Value= Integer.parseInt(((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r1Address))).getText().toString());
        r2Value= Integer.parseInt(((TextView)instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r2Address))).getText().toString());
if(r1Value<r2Value)((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).setText("1");//sets 1 to the destination address
else ((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).setText("0");//sets 0 to the destination address

        String CycleDataValue[] = new String[]{"0", "0" , r1Address, r0Address, "0","42","1","0","0","0",
                "0","4","0","1","0","no imm","0"};
        fillCycleDataViewsWithData(CycleDataValue);
        instructionsHandler.mainActivity.ProgramCounter++;
    }

    private void executeSll(String r0Address, String r1Address, String shiftAmount) {
        int r1Value;
        int ShiftAmount= Integer.parseInt(shiftAmount);
        checkIFitsNotRegZeroOrRegRa(r0Address);
        r1Value= Integer.parseInt(((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r1Address))).getText().toString());
        ((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).setText(String.valueOf(r1Value<<ShiftAmount));
        String CycleDataValue[] = new String[]{"0", "0" , r1Address, "0", shiftAmount,"0","1","0","0","0",
                "0","2","0","1","0","no imm","0"};
        fillCycleDataViewsWithData(CycleDataValue);
        instructionsHandler.mainActivity.ProgramCounter++;
    }



    private void executeJFormat(String r0) {
        int branchingToInstPos=0;
        int startPos=0;
        for(int i = 0; i<instructionsHandler.InstLabelsPos.size(); i++){
            if(instructionsHandler.InstLabelsPos.get(i).containsKey(r0+':')){
                branchingToInstPos=instructionsHandler.InstLabelsPos.get(i).get(r0+':');
                startPos=instructionsHandler.InstLabelsPos.get(i).get("startPos");
                break;
            }
        }
    instructionsHandler.mainActivity.ProgramCounter =branchingToInstPos;
        instructionsHandler.mainActivity.pos1=startPos;
        String CycleDataValue[] = new String[]{"2", "0 -> no rs", "0 -> no rt", "0 -> no rd", "0", "0 ->noFunc", "X ->Don't care", "X", "0"
                ,"0","X","X", "X", "0","1", "0 no immediate","0"};
        fillCycleDataViewsWithData(CycleDataValue);



    }

    private void executeLw(String r0Address, String r2Address, int r1) {
        int address;
        checkIFitsNotRegZeroOrRegRa(r0Address);
        address= Integer.parseInt(((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r2Address))).getText().toString())+r1;
        String r1Value=((TextView) instructionsHandler.registerMemoryCycleDataHandler.MemoryValue.getChildAt(address/4)).getText().toString();
        ((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).setText(r1Value);
        String CycleDataValue[] = new String[]{"35", r2Address, r0Address, "0 -> no rd", "0", "0 ->noFunc", "0", "0", "1"
                ,"0","1","2", "1", "1","0", String.valueOf(r1),"0"};
        fillCycleDataViewsWithData(CycleDataValue);
        instructionsHandler.mainActivity.ProgramCounter++;
    }

    private void executeSw(String r0Address, String r2Address, int r1) {
        int address;
        address= Integer.parseInt(((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r2Address))).getText().toString())+r1;
        String r1Value=((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).getText().toString();

        ((TextView) instructionsHandler.registerMemoryCycleDataHandler.MemoryValue.getChildAt(address/4)).setText(r1Value);
        String CycleDataValue[] = new String[]{"43", r2Address, r0Address, "0 -> no rd", "0", "0 ->noFunc", "X", "0", "0"
                ,"1","XX ->Don't care","2", "1", "0","0", String.valueOf(r1),"0"};
        fillCycleDataViewsWithData(CycleDataValue);
        instructionsHandler.mainActivity.ProgramCounter++;

    }

    private void executeNor(String r0Address, String r1Address, String r2Address) {
        int r1Value,r2Value,res;
        checkIFitsNotRegZeroOrRegRa(r0Address);
        r1Value= Integer.parseInt(((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r1Address))).getText().toString());
        r2Value= Integer.parseInt(((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r2Address))).getText().toString());
        res=~(r1Value|r2Value);
        ((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).setText(String.valueOf(res));
        String CycleDataValue[]=new String[]{"0", String.valueOf(r1Value), String.valueOf(r2Value),String.valueOf(r1Value+r2Value),"0","39","1","0","0","0",
                "0","5","1","0","0","0"};
        fillCycleDataViewsWithData(CycleDataValue);
        instructionsHandler.mainActivity.ProgramCounter++;

    }

    private void executeOri(String r0Address, String r1Address, int r2) {
        int r1Value;
        checkIFitsNotRegZeroOrRegRa(r0Address);
        r1Value= Integer.parseInt(((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r1Address))).getText().toString());
        ((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).setText(String.valueOf(r1Value|r2));
        String CycleDataValue[] = new String[]{"13", r1Address, r0Address, "no rd", "0", "8", "0", "0", "0", "0",
                "0", "1", "1", "1","0", String.valueOf(r2),"0"};
        fillCycleDataViewsWithData(CycleDataValue);
        instructionsHandler.mainActivity.ProgramCounter++;
    }

    private void executeOr(String r0Address, String r1Address, String r2Address) {
        int r1Value,r2Value;
        checkIFitsNotRegZeroOrRegRa(r0Address);
        r1Value= Integer.parseInt(((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r1Address))).getText().toString());
        r2Value= Integer.parseInt(((TextView)instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r2Address))).getText().toString());
        ((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).setText(String.valueOf(r1Value|r2Value));
        String CycleDataValue[]=new String[]{"0", r2Address,r1Address,r0Address,"0","35","1","0","0","0",
                "0","1","0","1","0","no imm","0"};
        fillCycleDataViewsWithData(CycleDataValue);
        instructionsHandler.mainActivity.ProgramCounter++;
    }

    private void executeAndi(String r0Address, String r1Address, int r2) {
        int r1Value;
        checkIFitsNotRegZeroOrRegRa(r0Address);
        r1Value= Integer.parseInt(((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r1Address))).getText().toString());
        ((TextView)instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).setText(String.valueOf(r1Value&r2));
        String CycleDataValue[] = new String[]{"12", r1Address, r0Address, "no rd", "0", "0", "0", "0", "0", "0",
                "0", "0", "1", "1", "0",String.valueOf(r2),"0"};
        fillCycleDataViewsWithData(CycleDataValue);
        instructionsHandler.mainActivity.ProgramCounter++;
    }

    private void excuteAnd(String r0Address, String r1Address, String r2Address) {
        int r1Value,r2Value;
        checkIFitsNotRegZeroOrRegRa(r0Address);
        r1Value= Integer.parseInt(((TextView)instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r1Address))).getText().toString());
        r2Value= Integer.parseInt(((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r2Address))).getText().toString());
        ((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).setText(String.valueOf(r1Value&r2Value));
        String CycleDataValue[]=new String[]{"0", r2Address,r1Address,r0Address,"0","36","1","0","0","0",
                "0","0","0","1","0","no imm","0"};
        fillCycleDataViewsWithData(CycleDataValue);
        instructionsHandler.mainActivity.ProgramCounter++;

    }

    private void executeAddi(String r0Address, String r1Address, int r2) {
        int r1Value;
        checkIFitsNotRegZeroOrRegRa(r0Address);
        r1Value = Integer.parseInt(((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r1Address))).getText().toString());
        ((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(r0Address))).setText(String.valueOf(r1Value + r2));
        String CycleDataValue[] = new String[]{"8", r1Address, r0Address, "no rd", "0", "0", "0", "0", "0", "0",
                "0", "2", "1", "1", "0", String.valueOf(r2),"0"};
        fillCycleDataViewsWithData(CycleDataValue);
        instructionsHandler.mainActivity.ProgramCounter++;
    }


    private void executeAdd(String R0Address, String R1Address, String R2Address) {
        int r1Value,r2Value;
        checkIFitsNotRegZeroOrRegRa(R0Address);
        r1Value= Integer.parseInt(((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(R1Address))).getText().toString());
        r2Value= Integer.parseInt(((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(R2Address))).getText().toString());
        ((TextView) instructionsHandler.registerMemoryCycleDataHandler.RegisterValue.getChildAt(Integer.parseInt(R0Address))).setText(String.valueOf(r1Value+r2Value));
        String CycleDataValue[]=new String[]{"0", R2Address,R1Address,R0Address,"0","32","1","0","0","0",
                "0","2","0","1","0","no imm","0"};
        fillCycleDataViewsWithData(CycleDataValue);
        instructionsHandler.mainActivity.ProgramCounter++;
    }

    private void fillCycleDataViewsWithData(String[] cycleDataValue) {

        for(int i=0;i<cycleDataValue.length;i++){
            ((TextView)instructionsHandler.registerMemoryCycleDataHandler.DataValue.getChildAt(i+1)).setText(cycleDataValue[i]);



        }
    }

}
