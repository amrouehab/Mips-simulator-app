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
    private boolean ExcutionIsInProgress=false;
    private boolean BuildIsDone=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MipsCodeText=(EditText)findViewById(R.id.editText2);
        registerMemoryCycleDataHandler =new Register_Memory_CycleData_Handler(this);
        instructionsHandler=new InstructionsHandler(registerMemoryCycleDataHandler,this);
        container=(FrameLayout)findViewById(R.id.container);
        initialiseLayoutFragments();
        instructionExcutor=new InstructionExcutor(registerMemoryCycleDataHandler,instructionsHandler);
        TextView notes=(TextView)findViewById(R.id.notes);
        notes.setText("ImportantNotes\n*the code is case sensitive\n*every instruction should be written line by line without any spaces \n" +
                "*the pointer of any instruction that used for jumping should be at a seperate line before its  instruction\n" +
                "*you could adjust the Register values from register Table the same as for MemoryValues\n" +
                "* the PC is zero By Default");


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
                TextView ErrorView = (TextView) findViewById(R.id.messageView);
                ErrorView.setText("please click stop before starting another build");
            }
            else {
                instructionsHandler.MipsCode = MipsCodeText.getText().toString();
                instructionsHandler.obtainInstFromCode(0);
                registerMemoryCycleDataHandler.addInstToMemory(instructionsHandler);
                BuildIsDone=true;
            }
        }catch (Exception e){
            TextView ErrorView = (TextView) findViewById(R.id.messageView);
            ErrorView.setText("Error");
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
        }

    }
    public void Run(View v){
        TextView ErrorView = (TextView) findViewById(R.id.messageView);
        ExcutionIsInProgress = ProgramCounter <= instructionsHandler.DataAndInstMemory.size() - 1;
        if(BuildIsDone) {
            if (ExcutionIsInProgress) {
                highliteCurrntInstruction();
                instructionExcutor.Excute(instructionsHandler.DataAndInstMemory.get(ProgramCounter), ProgramCounter);
                ErrorView.setText("Excution is in progress");

            } else {

                ErrorView.setText("Excution is finished");
                Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
                Stop(null);
            }
        }else {
            ErrorView.setText("Please Build Before Run");
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
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
}
