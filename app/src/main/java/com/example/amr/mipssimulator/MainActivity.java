package com.example.amr.mipssimulator;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class MainActivity extends AppCompatActivity {
    EditText MipsCodeText ;
    LayoutFragments  registerFragment,memoryFragments;
    RegisterAndMemoryHandler registerAndMemoryHandler;
    InstructionsHandler instructionsHandler;
    private boolean isFirstBackPress=true;
    private FrameLayout container;
    private Fragment CurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MipsCodeText=(EditText)findViewById(R.id.editText2);
        initialiseLayoutFragments();

        registerAndMemoryHandler =new RegisterAndMemoryHandler(this);
        instructionsHandler=new InstructionsHandler(registerAndMemoryHandler,this);
        container=(FrameLayout)findViewById(R.id.container);

//momken n7ot we ncreater el layouts hena we nnadeha men getActivity fel fragment
    }

    private void initialiseLayoutFragments() {
        registerFragment=new LayoutFragments();
        Bundle bundle=new Bundle();
        bundle.putInt("LayoutID",R.layout.reg_ly);
        registerFragment.setArguments(bundle);
        memoryFragments=new LayoutFragments();
        bundle=new Bundle();
        bundle.putInt("LayoutID",R.layout.memory_ly);
        memoryFragments.setArguments(bundle);


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
            instructionsHandler.MipsCode = MipsCodeText.getText().toString();
            instructionsHandler.obtainInstFromCode(0);
        }catch (Exception e){
            TextView ErrorView = (TextView) findViewById(R.id.messageView);
            ErrorView.setText("Error");
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
        }

    }
    public void Run(View v){


    }
    public void runNext(View v){


    }
    public void Stop(View v){


    }
    public void showMemoryLayout(View v){
        hideBotomLy();
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.enter_r,R.anim.exit_r);
        ft.add(R.id.container, memoryFragments,"t");
        ft.commit();
        CurrentFragment=memoryFragments;

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
