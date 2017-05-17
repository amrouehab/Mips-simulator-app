package com.example.amr.mipssimulator;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText MipsCodeText ;
    InstructionExcutor instructionExcutor;
    LayoutFragments  registerFragment, memoryFragment,cycleDataFragment;
    Register_Memory_CycleData_Handler registerMemoryCycleDataHandler;
    InstructionsHandler instructionsHandler;
    private boolean isFirstBackPress=true;
    private FrameLayout container;
    private Fragment CurrentFragment;
     int ProgramCounter =0;
    int pos1=0,pos2=0;
     boolean ExcutionIsInProgress=false;
     boolean BuildIsDone=false;
     TextView MessageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MipsCodeText=(EditText)findViewById(R.id.editText2);
        MessageView=(TextView) findViewById(R.id.messageView);
        registerMemoryCycleDataHandler =new Register_Memory_CycleData_Handler(this);
        instructionsHandler=new InstructionsHandler(registerMemoryCycleDataHandler,this);
        container=(FrameLayout)findViewById(R.id.container);
        initialiseLayoutFragments();
        instructionExcutor=new InstructionExcutor(instructionsHandler);
        TextView notes=(TextView)findViewById(R.id.notes);
        notes.setText("ImportantNotes\n*the code is case sensitive\n*every instruction should be written line by line without any spaces \n" +
                "*the pointer of any instruction that used for jumping should be at a seperate line before its  instruction\n" +
                "*you could adjust the Register values from register Table the same as for MemoryValues\n" +
                "* the PC is zero By Default\n* register $ra is initially set to -1 which is the address of the system caller\nwhich means that execution will be finished when ra is -1 when using Jal");


    }



    private void initialiseLayoutFragments() {
        registerFragment=new LayoutFragments();
        Bundle bundle=new Bundle();
        bundle.putInt("LayoutID",R.layout.reg_ly);
        registerFragment.setArguments(bundle);
        memoryFragment =new LayoutFragments();
        bundle=new Bundle();
        bundle.putInt("LayoutID",R.layout.memory_ly);
        memoryFragment.setArguments(bundle);
        cycleDataFragment =new LayoutFragments();
        bundle=new Bundle();
        bundle.putInt("LayoutID",R.layout.cycle_layout);
        cycleDataFragment.setArguments(bundle);



    }
    public void showDataCycleLayout(View v){
        hideBotomLy();
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.enter_r,R.anim.exit_r);
        ft.replace(R.id.container, cycleDataFragment,"t");
        ft.commit();
        CurrentFragment=cycleDataFragment;

    }

    public void showRegisterLayout(View v){
       hideBotomLy();
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.enter_r,R.anim.exit_r);
        ft.replace(R.id.container, registerFragment,"t");
        ft.commit();
        CurrentFragment=registerFragment;

    }

    private void hideBotomLy() {
        LinearLayout rl=(LinearLayout) findViewById(R.id.v);
        rl.setVisibility(View.GONE);
    }

    public void Build(View v){
        try {
            if(ExcutionIsInProgress){
               showMessage("please click stop before starting another build");
            }
            else {
                instructionsHandler.MipsCode = MipsCodeText.getText().toString();
                if(instructionsHandler.obtainInstFromCode(0)) {
                    showMessage("Done\nyou can run now");
                    registerMemoryCycleDataHandler.addInstToMemory(instructionsHandler);
                    BuildIsDone = true;
                }

            }
        }catch (Exception e){
            MessageView = (TextView) findViewById(R.id.messageView);
           showMessage("Error");
        }

    }
    public void Excode(View v){
        if(!ExcutionIsInProgress){
            MessageView.setText("this is a fibonacci sequence code Example \n please set the required value of $t0 which is F0\n and $t1 which is F1\n and $a0 which is N");
            MipsCodeText.setText("fib:\n" +
                    "addi $t0,$0,0\n" +
                    "addi $t1,$0,1\n" +
                    "beq $a0,$t0,l1\n" +
                    "beq $a0,$t1,l2\n" +
                    "addi $sp,$sp,-12\n" +
                    "sw $ra,8($sp)\n" +
                    "sw $a0,4($sp)\n" +
                    "sw $s0,0($sp)\n" +
                    "addi $a0,$a0,-1\n" +
                    "jal fib\n" +
                    "addi $s0,$v0,0\n" +
                    "addi $a0,$a0,-1\n" +
                    "jal fib\n" +
                    "add $v0,$v0,$s0\n" +
                    "lw $s0,0($sp)\n" +
                    "lw $a0,4($sp)\n" +
                    "lw $ra,8($sp)\n" +
                    "addi $sp,$sp,12\n" +
                    "jr $ra\n" +
                    "l1:\n" +
                    "addi $v0,$0,0\n" +
                    "jr $ra\n" +
                    "l2:\n" +
                    "addi $v0,$0,1\n" +
                    "jr $ra\n");}
        else showMessage("Execution is in progress stop it first");



    }
    public void Run(View v){
        try {
            ExcutionIsInProgress = ProgramCounter <= instructionsHandler.DataAndInstMemory.size() - 1;
            if (BuildIsDone) {
                if (ExcutionIsInProgress) {
                    highliteCurrntInstruction();
                    instructionExcutor.Excute(instructionsHandler.DataAndInstMemory.get(ProgramCounter));
                    MessageView.setText("Execution is in progress\nPress the Run button to execute the next Instruction");

                } else {
                    showMessage("Execution is finished");
                    Stop(null);
                }
            } else {
                showMessage("Please Build Before Run");

            }
        }catch (Exception e){
            showMessage("Error please check your code\nHints: 1- check the notes before build\n2- click on Example code to show an Example of a valid code");
        }
    }

    private void highliteCurrntInstruction() {
        SpannableString text = new SpannableString(instructionsHandler.MipsCode);
        pos2=MipsCodeText.getText().toString().indexOf("\n",pos1)+1;
        if(MipsCodeText.getText().toString().charAt(pos2-2)==':')pos2=MipsCodeText.getText().toString().indexOf("\n",pos2)+1;
        text.setSpan(new TextAppearanceSpan(this, R.style.style1), pos1, pos2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        MipsCodeText.setText(text, TextView.BufferType.SPANNABLE);
        pos1=pos2;
    }


    public void Stop(View v){
        ProgramCounter =0;
        pos1=0;pos2=0;
        MipsCodeText.setText(instructionsHandler.MipsCode);
        ExcutionIsInProgress=false;
        BuildIsDone=false;
        showMessage("Execution is finished");

    }
    public void showMemoryLayout(View v){
        hideBotomLy();
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.enter_r,R.anim.exit_r);
        ft.add(R.id.container, memoryFragment,"t");
        ft.commit();
        CurrentFragment= memoryFragment;

    }
    @Override
    //this method handles on back key click to exit the app
    public void onBackPressed() {
        if (!isFirstBackPress) {
            onDestroy();
            System.exit(0);
        } else {
            if (container.getChildAt(0)!=null){
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.enter_r,R.anim.exit_r,R.anim.enter,R.anim.exit);
                ft.remove(CurrentFragment);
                ft.commit();
                LinearLayout rl=(LinearLayout) findViewById(R.id.v);
                rl.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(MainActivity.this, "Press again to Exit", Toast.LENGTH_SHORT).show();
                isFirstBackPress = false;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isFirstBackPress = true;

                    }
                }, 3 * 1000);

            }
        }
    }

    public void showMessage(String Message) {
        MessageView.setText(Message);
        Toast.makeText(this, Message, Toast.LENGTH_SHORT).show();
    }
}
