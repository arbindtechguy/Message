package com.example.arbindtechguy.message;
        import android.annotation.SuppressLint;
        import android.content.Intent;
        import android.support.v7.app.ActionBar;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.os.Handler;
        import android.view.MotionEvent;
        import android.view.View;
        import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class StartPageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Thread thread = new Thread(){
            public void run(){
                try{
                    sleep(300);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    startActivity(new Intent(StartPageActivity.this, WelcomeActivity.class));
                }
            }
        };
        thread.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}