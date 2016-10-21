package xc.spaceyinvaders;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class FullscreenActivity extends Activity {

    TextView highScore;
    SharedPreferences mPrefs;
    int hsi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_fullscreen);
        mPrefs= PreferenceManager.getDefaultSharedPreferences(this);
        highScore=(TextView)findViewById(R.id.highscore);
        hsi = mPrefs.getInt("high", 0);     //saved high score as variable "high", default value 0
        String string = "Best: "+hsi;
        highScore.setText(string);
    }

    public void startGame(View view){
        Intent intent = new Intent(this, InGame.class);
        startActivity(intent);
    }
}
