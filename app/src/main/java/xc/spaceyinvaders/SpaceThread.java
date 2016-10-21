package xc.spaceyinvaders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.app.Activity;

/**
 * Created by Aaron on 2016-04-05.
 */
public class SpaceThread extends Thread {
    SpaceView sv;
    boolean gameLoaded;
    int gameState;
    final int INIT = -1;
    final int LOADING = 0;
    final int RUNNING = 1;
    final int PAUSED = 2;
    final int OVER = 3;
    final int END = 4;

    public SpaceThread(SpaceView sv) {
        this.sv=sv;
        gameLoaded = false;
    }
    public void run() {
        SurfaceHolder sh = sv.getHolder();
        Canvas c;
        gameState = INIT;
        // Main game loop.
        while( !Thread.interrupted() ) {
            switch (gameState) {
                case INIT:
                    c = sh.lockCanvas(null);
                    sv.draw(c);
                    sh.unlockCanvasAndPost(c);
                    sv.loadGame();
                    gameLoaded = true;
                    gameState = RUNNING;
                    break;
                case LOADING:
                    c = sh.lockCanvas(null);
                    sv.draw(c);
                    sh.unlockCanvasAndPost(c);
                    sv.createInvaders(sv.getLevel());
                    break;
                case OVER:
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // Thread was interrupted while sleeping.
                        return;
                    }
                    //intentional roll over to case RUNNING
                case RUNNING:
                    c = sh.lockCanvas(null);
                    try {
                        synchronized (sh) {
                            sv.draw(c);
                            Log.d("Draw", "Frame Complete");
                            sv.update();
                        }
                    } catch (Exception e) {
                    } finally {
                        if (c != null) {
                            sh.unlockCanvasAndPost(c);
                        }
                    }
                    // Set the frame rate by setting this delay
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        // Thread was interrupted while sleeping.
                        return;
                    }
                    break;
                case PAUSED:
                    //not yet implemented
                    break;
                case END:
                    SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(sv.getContext());
                    SharedPreferences.Editor editor = mPrefs.edit();
                    if (mPrefs.getInt("high", 0) < sv.getScore()) {
                        editor.putInt("high", sv.getScore());
                        editor.apply();
                    }
                    interrupt();
                    break;
            }

        }
        Log.d("SpaceThread", "Game Over!");
    }
    public void setGameState(int gameState){
        this.gameState = gameState;
    }
}