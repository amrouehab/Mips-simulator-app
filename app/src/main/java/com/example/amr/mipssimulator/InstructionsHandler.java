package com.example.amr.mipssimulator;


import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class InstructionsHandler {
    private final MainActivity mainActivity;
    public String MipsCode;
    private String[] Instructions =new String[]{"add","and","or","nor","slt","sll","jr","beq","addi","andi","ori","lw","sw","j","jal"};
    private static final int LastRFormatInInstArray =6;
    private static final int LastIFormatInInstArray =12;
    private static final int LastJFormatInInstArray =14;
    private int InstFormat=0;
    int SearchCursorPos=0;
    public static final int R_FORMAT =10;
    public static final int I_FORMAT =20;
    public static final int J_FORMAT =30;
    boolean InstructionNameISObtained=false;
    RegisterAndMemoryHandler registers;
    ArrayList<HashMap<String,String>> DataAndInstMemory =new ArrayList<>();
    private String ObtainedInst;
    private int LineCounter=0;

    public InstructionsHandler( RegisterAndMemoryHandler registers,MainActivity mainActivity) {
        this.mainActivity=mainActivity;
        this.registers=registers;
    }

    //momken necheck el asm be anna necheck fel line kolo men awel char le7d el /n function bta5od men awel 0 l7d /n
    //y3ny men awel el line le a5ro
    //we na2s n3ml ba2y el inst we handler to excute the inst one  by one

    //this obatines the inst from user code  #param c >. the starting Char pos to begin  with
    public void obtainInstFromCode(int c) {
        //freeThe memory
        DataAndInstMemory.clear();
        LineCounter=0;
        HashMap<String, String> ObtainedInstr = new HashMap<>();
        String ObtainedCode ;
        for (int i = c; i < MipsCode.length(); i++) {//add $s1,$s2,$s3 //
            if (checkIsItaName(MipsCode.substring(i, MipsCode.indexOf('\n', i)))) {
                i = i+MipsCode.indexOf('\n', i);
                ObtainedCode = MipsCode.substring(i, MipsCode.indexOf('\n', i));
                ObtainedInstr.put("name", ObtainedCode);

            } else {
                if (isValidMiPsCodeObtained(MipsCode.substring(i, MipsCode.indexOf(' ', i)))) {
                    ObtainedCode = MipsCode.substring(i, MipsCode.indexOf(' ', i));
                    i = MipsCode.indexOf(' ', i)+1;
                    ObtainedInstr.put("inst", ObtainedCode);
                    InstructionNameISObtained = true;
                    String ExpectedCodePattern = MipsCode.substring(i , MipsCode.indexOf("\n", i ));
                    int positions[] = getInstContentsP0sFromExpectedCodePattern(ExpectedCodePattern,ObtainedInstr);
                    obtainIformatInstContents(positions, ObtainedInstr, ExpectedCodePattern);
                    i = i+ExpectedCodePattern.length()+1;
                    DataAndInstMemory.add(ObtainedInstr);
                    InstructionNameISObtained = false;
                    ObtainedInstr = new HashMap<>();
                }
                else {
                    sendErrorToUser(MipsCode.substring(i, MipsCode.indexOf(' ', i)));
                }

LineCounter++;

            }
        }
        TextView mesage= (TextView) mainActivity.findViewById(R.id.messageView);
        mesage.setText("Done!\nYou can run now");
    }


    private boolean checkIsItaName(String substring) {
        return substring.charAt(substring.length() - 1) == ':';

    }


    private int[] getInstContentsP0sFromExpectedCodePattern(String expectedCodePattern, HashMap<String, String> obtainedInst) {
        int From1=0,to1=0,From2=0,to2=0,From3=0,to3=0;
        switch(InstFormat){
            case R_FORMAT:{
                From1=0;to1=expectedCodePattern.indexOf(',',From1);
                From2=to1+1;to2=expectedCodePattern.indexOf(',',From2);
                From3=to2+1;to3=expectedCodePattern.length();
                obtainedInst.put("format","R");
                break;
            }
            case I_FORMAT: { //sw $s1,8($s2);  //addi $s1,$s2,8
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

            case J_FORMAT:{
                From1=0;to1=expectedCodePattern.length();
                to2=to1;to3=to1;
                obtainedInst.put("format","J");
                break;
            }
        }




        return new int[]{From1,From2,From3,to1,to2,to3};
    }


//this function gets the sw and lw instructions contents from the first char $ to the last char )
    private void obtainIformatInstContents(int positions[], HashMap<String, String> ObtainedInstr,String ExpectedCode) {
        boolean isValidMipsCode =false;
            int counter = 0;
            String ObtainedCode = "";
            for (int i = 0; i <=2; i++) {
                switch (i) {
                    case 0: {
                        ObtainedCode = ExpectedCode.substring(positions[0], positions[3]);
                        isValidMipsCode = isValidMiPsCodeObtained(ObtainedCode);break;
                    }
                    case 1: {
                        ObtainedCode = ExpectedCode.substring(positions[1], positions[4]);
                        isValidMipsCode = isValidMiPsCodeObtained(ObtainedCode);break;
                    }
                    case 2: {
                        ObtainedCode = ExpectedCode.substring(positions[2], positions[5]);
                        isValidMipsCode = isValidMiPsCodeObtained(ObtainedCode);break;
                    }
                }
                if (isValidMipsCode) {
                    ObtainedInstr.put("R" + i, ObtainedCode);
                    ObtainedCode = "";

                }
                else {
                    sendErrorToUser(ExpectedCode);break;
                }
            }

    }




    //gets the current RInstruction registers to deal with

    private boolean isAnImmediateValue(String obtainedCode) {

        try {
            int i = Integer.parseInt(obtainedCode + 1);

            return true;


        } catch (Exception e) {
            return false;
        }
    }

    private void obtainInstRegisters(int i0, int i1, HashMap<String, String> ObtainedInstr) {
        try {
            int counter = 0;
            String ObtainedCode = "";
            char Char = ' ';
            for (int i = i0; i <= i1; i++) {
                if (!isValidMiPsCodeObtained(ObtainedCode)) {
                    Char = MipsCode.charAt(i);
                    ObtainedCode = ObtainedCode + String.valueOf(Char);
                    if (ObtainedCode.length() == 4) {
                        sendErrorToUser(ObtainedCode);
                        break;
                    }
                } else {
                    counter++;//to know the sequence of the obtained register R1,R2,... and so on
                    ObtainedInstr.put("R" + counter, ObtainedCode);
                    ObtainedCode = "";
                    if (i >= i1) {
                        DataAndInstMemory.add(ObtainedInstr);

                    }
                }

            }
        }catch (Exception e){
            e.getMessage();
        }


    }

    //this sends Shows error to user due to wrong syntax or InvalidInst

    private void sendErrorToUser(String ErrorCode) {
        TextView ErrorView = (TextView) mainActivity.findViewById(R.id.messageView);
        ErrorView.setText("Error in code"+ErrorCode+"\n"+"at Line"+LineCounter);

    }

    //responsible for checking the obtained string is it a valid or not
    private boolean isValidMiPsCodeObtained(String MipsCode) {
        boolean CodeObtained = false;
        for (int i = 0; i <= 26; i++) {
            if (!InstructionNameISObtained) {
                if (MipsCode.equals(Instructions[i])) {
                    InstFormat = getInstFormat(i);
                    CodeObtained = true;
                    InstructionNameISObtained=true;
                    ObtainedInst=MipsCode;
                    break;
                }
                if (i >=14) break;
            } else {
                if (MipsCode.equals(registers.RegistersCode[i])) {
                    CodeObtained = true;
                    break;

                }
            }

        }
        if(isAnImmediateValue(MipsCode))CodeObtained=true;
        if(ObtainedInst.equals("j")||ObtainedInst.equals("jal")||ObtainedInst.equals("beq"))CodeObtained=true;

        return CodeObtained;

    }
    //gets the Format of the inst #param i>. the index of the inst In the Instruction array
    private int getInstFormat(int i) {
        if(i<=LastRFormatInInstArray) return R_FORMAT;
        if(i>LastRFormatInInstArray&&i<=LastIFormatInInstArray) return I_FORMAT;
        if(i>LastIFormatInInstArray&&i<=LastJFormatInInstArray) return J_FORMAT;


        return -1;
    }
}
