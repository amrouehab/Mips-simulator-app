package com.example.amr.mipssimulator;


import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;

 class InstructionsHandler {
    final MainActivity mainActivity;
     String MipsCode;
    private String[] Instructions =new String[]{"add","and","or","nor","slt","sll","jr","beq","bne","addi","andi","ori","lw","sw","j","jal"};
    private static final int LastRFormatInInstArray =6;
    private static final int LastIFormatInInstArray =13;
    private static final int LastJFormatInInstArray =15;
    private int InstFormat=0;
     private static final int R_FORMAT =10;
    private static final int I_FORMAT =20;
    private static final int J_FORMAT =30;
    private boolean InstructionNameISObtained=false;
     Register_Memory_CycleData_Handler registerMemoryCycleDataHandler;
    ArrayList<HashMap<String,String>> DataAndInstMemory =new ArrayList<>();
     ArrayList<HashMap<String,Integer>> InstLabelsPos =new ArrayList<>();
    private String ObtainedInst;
    private int LineCounter=0;


     InstructionsHandler(Register_Memory_CycleData_Handler registerMemoryCycleDataHandler, MainActivity mainActivity) {
        this.mainActivity=mainActivity;
        this.registerMemoryCycleDataHandler = registerMemoryCycleDataHandler;
    }



    //this obtains the inst from user code
    // it loops the code line by line to get the mips code
    // #param c >. the starting Char pos to begin  with
     boolean obtainInstFromCode(int c) {
        //freeThe memory
        DataAndInstMemory.clear();
         InstLabelsPos.clear();
        LineCounter=0;
        HashMap<String, String> ObtainedInstr = new HashMap<>();// hashMap is like an array that u could saves data in it and puts a key to it which is used to get the data from it
        String ObtainedCode ;
         // lets  assume that the user code is loop:\nadd $s0,$s1,$s2\n
        for (int i = c; i < MipsCode.length(); i++) {
            //checks for label
            // in our assumption is loop:\n
            if (checkIsItaLabel(MipsCode.substring(i, MipsCode.indexOf('\n', i)))) {// substring the code from i to (first indexOF \n after i )
                HashMap<String, Integer> LablesPosForBranch = new HashMap<>();
                ObtainedCode = MipsCode.substring(i, MipsCode.indexOf('\n', i));// with our assumption ObtainedCode will= "loop:"
                LablesPosForBranch.put("startPos", i);//put i into the HashMap with key of "startpos"
                i = i+ObtainedCode.length();//shift the counter
                ObtainedInstr.put("name", ObtainedCode);//save the label
                LablesPosForBranch.put(ObtainedCode, DataAndInstMemory.size());
InstLabelsPos.add(LablesPosForBranch);//saves the LabelsPos to got to it  when branching
                LineCounter++; //shift the Line counter
            } else {
                if (isValidMiPsCodeObtained(MipsCode.substring(i, MipsCode.indexOf(' ', i)), ObtainedInstr)) {//check validity in our assumption the substring output will be "add"
                    ObtainedCode = MipsCode.substring(i, MipsCode.indexOf(' ', i));//saves the inst name  in our assumption its "add"
                    i = MipsCode.indexOf(' ', i)+1; //shift the counter
                    ObtainedInstr.put("inst", ObtainedCode); // save inst name on the hashmap
                    InstructionNameISObtained = true;
                    //now we will get the other data of this instruction in our assumption it will be $s0,$s1,$s2\n
                    String ExpectedCodePattern = MipsCode.substring(i , MipsCode.indexOf("\n", i )); // get the expected code in our case will be $s0,$s1,$s2
                    int positions[] = getInstContentsP0sFromExpectedCodePattern(ExpectedCodePattern,ObtainedInstr);//gets the pos of the contents in our case the pos of $s0 and $s1 and $s2
                    obtainInstContents(positions, ObtainedInstr, ExpectedCodePattern);// gets the inst contents from the expected code
                    i = i+ExpectedCodePattern.length();//shift the counter
                    DataAndInstMemory.add(ObtainedInstr);//add the inst into memory
                    InstructionNameISObtained = false;
                    ObtainedInstr = new HashMap<>();
                }
                else {
                    sendErrorToUser(MipsCode.substring(i, MipsCode.indexOf(' ', i)));
                    return false;
                }

LineCounter++;

            }
        }

         return true;
    }

// check that the string is an instruction label
    private boolean checkIsItaLabel(String substring) {
        return substring.charAt(substring.length() - 1) == ':';

    }
     //responsible for checking the obtained string is it a valid or not
     private boolean isValidMiPsCodeObtained(String MipsCode, HashMap<String, String> obtainedInstr) {
         boolean CodeObtained = false;
         for (int i = 0; i <= 26; i++) {
             if (!InstructionNameISObtained) {//check the inst name is it valid or not
                 if (MipsCode.equals(Instructions[i])) {
                     InstFormat = getInstFormat(i);
                     CodeObtained = true;
                     InstructionNameISObtained=true;
                     ObtainedInst=MipsCode;
                     break;
                 }
                 if (i >=15) break;//break if the counter exceeds the length of the supported inst
             } else {
                 if (MipsCode.equals(registerMemoryCycleDataHandler.RegistersCode[i])) {
                     CodeObtained = true;
                     obtainedInstr.put("RegAddress", String.valueOf(i+1));// saves the regAddress
                     break;

                 }
             }

         }
         if(isAnImmediateValue(MipsCode))CodeObtained=true;
         if(ObtainedInst.equals("j")||ObtainedInst.equals("jal")||ObtainedInst.equals("beq")||ObtainedInst.equals("bne"))CodeObtained=true;

         return CodeObtained;

     }

// this function gets the pos of the contents of the obtained inst from the expected code
    private int[] getInstContentsP0sFromExpectedCodePattern(String expectedCodePattern, HashMap<String, String> obtainedInst) {
        int From1=0,to1=0,From2=0,to2=0,From3=0,to3=0;
        switch(InstFormat){
            case R_FORMAT:{// ex: for jr the codePattern will be {ra};  and for add will be like this { $s1,$s2,$s3}
                if(obtainedInst.get("inst").equals("jr")){
                    From1=0;to1=expectedCodePattern.length();
                    to2=to1;to3=to1;
                    obtainedInst.put("format","R");//put the format in the HashMap
                }
                else {
                    From1 = 0;
                    to1 = expectedCodePattern.indexOf(',', From1);
                    From2 = to1 + 1;
                    to2 = expectedCodePattern.indexOf(',', From2);
                    From3 = to2 + 1;
                    to3 = expectedCodePattern.length();
                    obtainedInst.put("format", "R");
                }
                break;
            }
            case I_FORMAT: { // ex: for sw the codePattern will be like this  {$s1,8($s2)};  and for addi will be like this { $s1,$s2,8}
              if(obtainedInst.get("inst").equals("lw")||obtainedInst.get("inst").equals("sw")){
                  From1=0;to1=expectedCodePattern.indexOf(',',From1);
                  From2=to1+1;to2=expectedCodePattern.indexOf('(',From2);
                  From3=to2+1;to3=expectedCodePattern.indexOf(')',From2);

              }
              else {
                  From1=0;to1=expectedCodePattern.indexOf(',',From1);
                  From2=to1+1;to2=expectedCodePattern.indexOf(',',From2);
                  From3=to2+1;to3=expectedCodePattern.length();

              }
                obtainedInst.put("format","I");
                break;
            }

            case J_FORMAT:{//ex: for j or Jal the codePattern will be like this {loop} where loop is a label
                From1=0;to1=expectedCodePattern.length();
                to2=to1;to3=to1;
                obtainedInst.put("format","J");
                break;
            }
        }




        return new int[]{From1,From2,From3,to1,to2,to3};
    }


//this function gets the inst data from the expected code
    private void obtainInstContents(int positions[], HashMap<String, String> ObtainedInstr, String ExpectedCode) {
        boolean isValidMipsCode =false;
            String ObtainedCode = "";
            for (int i = 0; i <=2; i++) {
                switch (i) {
                    case 0: {
                        ObtainedCode = ExpectedCode.substring(positions[0], positions[3]);
                        isValidMipsCode = isValidMiPsCodeObtained(ObtainedCode, ObtainedInstr);break;
                    }
                    case 1: {
                        ObtainedCode = ExpectedCode.substring(positions[1], positions[4]);
                        isValidMipsCode = isValidMiPsCodeObtained(ObtainedCode,ObtainedInstr);break;
                    }
                    case 2: {
                        ObtainedCode = ExpectedCode.substring(positions[2], positions[5]);
                        isValidMipsCode = isValidMiPsCodeObtained(ObtainedCode, ObtainedInstr);break;
                    }
                }
                if (isValidMipsCode) {
                    ObtainedInstr.put("R" + i, ObtainedCode);
                   ObtainedInstr.put("R" + i+"Address",ObtainedInstr.get("RegAddress"));
                    ObtainedInstr.remove("RegAddress");
                    ObtainedCode = "";

                }
                else {
                    sendErrorToUser(ExpectedCode);break;
                }
            }

    }



     //check is it an immediate value by perform match operation with it
    private boolean isAnImmediateValue(String obtainedCode) {

        try {
            Integer.parseInt(obtainedCode + 1);
//check is it an IFormat
            return InstFormat == I_FORMAT||ObtainedInst.equals("sll");


        } catch (Exception e) {
            return false;
        }
    }

     //this sends Shows error to user due to wrong syntax or InvalidInst

    private void sendErrorToUser(String ErrorCode) {
        TextView ErrorView = (TextView) mainActivity.findViewById(R.id.messageView);
        ErrorView.setText("Error in code"+ErrorCode+"\n"+"at Line"+LineCounter);

    }


    //gets the Format of the inst #param i>. the index of the inst In the Instruction array
    private int getInstFormat(int i) {
        if(i<=LastRFormatInInstArray) return R_FORMAT;
        if(i>LastRFormatInInstArray&&i<=LastIFormatInInstArray) return I_FORMAT;
        if(i>LastIFormatInInstArray&&i<=LastJFormatInInstArray) return J_FORMAT;


        return -1;
    }
}
