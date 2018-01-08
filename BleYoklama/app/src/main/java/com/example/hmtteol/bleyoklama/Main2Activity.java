package com.example.hmtteol.bleyoklama;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Main2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Thread mSplashThread;//thread classdan obje olustrduk uygulamann 3 saniye uyutulmasi icin
        mSplashThread = new Thread(){
            @Override public void run(){
                try {
                    synchronized(this){
                        wait(3000);
                    }
                }catch(InterruptedException ex){

                }
                finally{

                    Intent i=new Intent(Main2Activity.this,Login.class);
                    startActivity(i);
                    finish();
                }

            }
        };//thread objesini olustrduk ve istedmz sekilde sekillendrdik
        mSplashThread.start();// thread objesini calistriyoruz
    }
}
