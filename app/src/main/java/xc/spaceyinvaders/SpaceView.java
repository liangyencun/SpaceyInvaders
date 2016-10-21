package xc.spaceyinvaders;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Jimmy on 2016/4/5.
 */
public class SpaceView extends SurfaceView implements SurfaceHolder. Callback{


    public SpaceView(Context context) { super(context) ;
        this.context = context;
        getHolder (). addCallback(this);
        setFocusable(true);
    }

    Context context;
    Ship ship;
    Bullet[] bullet = new Bullet[200];
    int maxNumOfBullet = 5; // you can set the maximum number of bullets
    int numOfBullet = 0;
    Invaders[] invaders = new Invaders[100];
    int numOfInvaders = 0;
    int numOfInvadersAlive = 0;
    float level = 1;
    //1-(1/sqrt(2)) is used instead of 0.5 to put 'hitbox' inside the circle rather than circumscribing it
    float touchDistanceY;
    float touchDistanceX;
    float touchDistanceXforUFO;
    float touchDistanceYforUFO;
    boolean bounded;
    Ufo ufo;
    SpaceThread st;
    boolean gameLoaded = false;

    boolean highScoreDisplayed; //to keep "New High Score" dialog on screen
    Paint p = new Paint(); //used for drawing text on canvas

    int score;
    String scoreString = "Score: 0";
    String levelString = "Level: 1";

    SoundPool soundPool;
    int soundLaser, soundBomb, soundShotUFO, soundGameOver;
    boolean loaded = false;

    public void loadGame(){
        //Sound handling
        if (Build.VERSION.SDK_INT < 21) { //deprecated for API level 21+
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        }
        else{
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(10);
            soundPool = builder.build();
        }
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });
        soundLaser = soundPool.load(this.context, R.raw.laser, 1);
        soundBomb = soundPool.load(this.context, R.raw.bomb, 1);
        soundShotUFO = soundPool.load(this.context, R.raw.shotufo, 1);
        soundGameOver = soundPool.load(this.context, R.raw.gameover, 1);
        //ship creation
        ship=new Ship(this.context,getWidth(), getHeight());
        //bullet creation
        for(int i=0; i<maxNumOfBullet; i++) {
            bullet[numOfBullet] = new Bullet(this.context, getWidth(), getHeight(), ship.getX(), ship.getY());
            numOfBullet++;
        }
        //invader creation
        createInvaders(1);
        touchDistanceY = (float)(invaders[0].invadersHight*0.293 + bullet[0].bulletHeight*0.293);
        touchDistanceX = (float)(invaders[0].invadersWidth*0.293 + bullet[0].bulletWidth*0.293);
        //ufo creation
        ufo = new Ufo(this.context, getWidth(), getHeight());
        touchDistanceYforUFO = (float)(ufo.shipHeight*0.5 + bullet[0].bulletHeight*0.293);
        touchDistanceXforUFO = (float)(ufo.shipWidth*0.293 + bullet[0].bulletWidth*0.5);
        //touch handling
        loadTouchHandler();
        Log.d("Init", "Loading Complete");
    }

    @Override
    public void surfaceCreated ( SurfaceHolder holder ) {
        // Launch animator thread
        if (!gameLoaded) {
            st = new SpaceThread(this);
            st.start();
            gameLoaded = true;
        }
        Log.d("Load", "surfaceView/Thread");
    }

    @Override
    public void draw(Canvas c) {
        super.draw(c);
        switch(st.gameState) {
            case -1: //INIT
                c.drawColor(Color.BLACK);
                p.setColor(Color.WHITE);
                p.setTextSize(getHeight()/10);
                p.setAntiAlias(true);
                p.setFakeBoldText(true);
                p.setStyle(Paint.Style.FILL);
                p.setTextAlign(Paint.Align.CENTER);
                c.drawText("LEVEL 1", getWidth()/2, getHeight()/3, p);
                Log.d("Draw", "Initialization Loading Frame Complete");
                break;
            case 0: //LOADING
                setOnTouchListener(null);
                c.drawColor(Color.BLACK);
                p.setColor(Color.WHITE);
                p.setTextSize(getHeight()/10);
                p.setAntiAlias(true);
                p.setFakeBoldText(true);
                p.setStyle(Paint.Style.FILL);
                p.setTextAlign(Paint.Align.CENTER);
                c.drawText("LEVEL "+(int)level, getWidth()/2, getHeight()/3, p);
                Log.d("Draw", "Loading Frame Complete");
                st.setGameState(st.RUNNING);
                loadTouchHandler();
                scoreString = "Score: "+score;
                levelString = "Level: "+(int)level;
                break;
            case 1: //RUNNING
                c.drawColor(Color.BLACK);
                Log.i("Draw", "Background");

                p.setColor(Color.WHITE);
                p.setTextSize(getHeight() / 25);
                p.setStyle(Paint.Style.FILL);
                p.setAntiAlias(true);

                ufo.draw(c);
                Log.i("Draw", "Ufo");
                ship.draw(c);
                Log.i("Draw", "Ship");

                for (int i = 0; i < numOfBullet; i++) {
                    bullet[i].draw(c);
                }
                Log.i("Draw", "Bullet");
                for (int i = 0; i < numOfInvaders; i++) {
                    invaders[i].draw(c);
                }
                Log.i("Draw", "Invaders");

                c.drawLine(0, getHeight() - getHeight() / 4, getWidth(), getHeight() - getHeight() / 4, p);
                Log.i("Draw", "Line");

                p.setTextAlign(Paint.Align.LEFT);
                c.drawText(scoreString, 10, getHeight() * 3 / 4 - 5, p);
                p.setTextAlign(Paint.Align.RIGHT);
                c.drawText(levelString, getWidth() - 10, getHeight() * 3 / 4 - 5, p);
            case 2: //PAUSED
                break;
            case 3: //OVER
                c.drawColor(Color.BLACK);
                p.setColor(Color.WHITE);
                p.setTextSize(getHeight() / 15);
                p.setStyle(Paint.Style.FILL);
                p.setAntiAlias(true);
                p.setTextAlign(Paint.Align.CENTER);

                c.drawText("Game Over!", getWidth() / 2, getHeight() / 2 - 2 * getHeight() / 15, p);

                p.setTextSize(getHeight() / 20);
                c.drawText(scoreString, getWidth() / 2, getHeight() / 2 - getHeight() / 15, p);

                SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = mPrefs.edit();
                if (!highScoreDisplayed && mPrefs.getInt("high", 0) < score) {
                    editor.putInt("high", score);
                    c.drawText("New High Score!", getWidth() / 2, getHeight() / 2, p);
                    editor.apply();
                    highScoreDisplayed = true;
                }
                p.setTextSize(getHeight() / 30);
                c.drawText("Tap anywhere to play again", getWidth() / 2, getHeight() / 2 + getHeight() / 15, p);

                setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                            Activity tmp = (Activity) context;
                            tmp.finish();
                            Intent intent = new Intent(context, InGame.class);
                            tmp.startActivity(intent);
                            return true;
                        }
                        return false;
                    }
                });
                break;
        }
    }

    public void update(){
        if (st.gameState == st.RUNNING) {
            ship.update();
            ufo.update();
            Log.d("score", "" + scoreString);

            collisionDetection();

            //bullet[] update
            for (int i = 0; i < numOfBullet; i++) {
                bullet[i].update(ship.getX());
                //Log.d("DEBUG","bullet[" +i+"].getX()="+bullet[i].getX());
            }
            //invaders update
            for (int i = 0; i < numOfInvaders; i++) {
                //Log.d("DEBUG","invaders[" +i+"].getX()="+invaders[i].getX());
                invaders[i].update();
                if (invaders[i].isAlive) {
                    if (invaders[i].getX() + invaders[i].getWidth() > getWidth() || invaders[i].getX() < 0) {
                        bounded = true; //invader is at the bound of screen
                    }
                }
            }

            //if one invader touched the bound of screen, all invaders go down and reverse
            if (bounded) {
                for (int i = 0; i < numOfInvaders; i++) {
                    invaders[i].goDownAndReverse();
                    //game over when any live invader crosses the line (3/4 down)
                    if (invaders[i].isAlive && (invaders[i].getY() + invaders[i].getHeight() > getHeight() * 3 / 4)) {
                        Log.d("Status", "Game Over");
                        soundPool.play(soundGameOver,0.2f,0.2f,1,0,1);
                        st.setGameState(st.OVER);
                        break;
                    }
                }
                bounded = false;
            }
        }
    }

    public void collisionDetection(){
        //if a bullet collides with an invader, invaders[i].isAlive = false
        outerLoop:
        for(int j=0; j<numOfBullet; j++){
            if (bullet[j].isShooting) {
                float bCenterX = bullet[j].getX() + bullet[j].bulletWidth / 2;
                float bCenterY = bullet[j].getY() - bullet[j].bulletHeight / 2;
                float uCenterX = ufo.getX() + ufo.shipWidth / 2;
                float uCenterY = ufo.getY() - ufo.shipWidth / 2;
                if((Math.abs(uCenterY - bCenterY) <= touchDistanceYforUFO ) && (Math.abs(uCenterX - bCenterX) <= touchDistanceXforUFO)){
                    soundPool.play(soundShotUFO,1,1,1,0,1);
                    bullet[j].isAlive = false;
                    bullet[j].update(ship.getX()); //to prevent multi-kill with one bullet
                    score += 20*level;
                    scoreString = "Score: "+score;
                }
                for(int i=0; i<numOfInvaders; i++){
                    if (invaders[i].isAlive) {
                        float iCenterX = invaders[i].getX() + invaders[i].invadersWidth / 2;
                        float iCenterY = invaders[i].getY() - invaders[i].invadersHight / 2;
                        if ((Math.abs(iCenterY - bCenterY) <= touchDistanceY) && (Math.abs(iCenterX - bCenterX) <= touchDistanceX)) {
                            soundPool.play(soundBomb,0.1f,0.1f,1,0,1);
                            invaders[i].isAlive = false;
                            bullet[j].isAlive = false;
                            bullet[j].update(ship.getX()); //to prevent multi-kill with one bullet
                            score+=level;
                            if (--numOfInvadersAlive == 0) {
                                //disable all touch handling while next level is loading
                                setOnTouchListener(null);
                                //remove all bullets in the air before loading next level
                                for (int k = 0; k < numOfBullet; k++){
                                    if (bullet[k].isShooting){
                                        bullet[k].setShooting(false);
                                        bullet[k].resetY();
                                        bullet[k].update(ship.getX());
                                    }
                                }
                                score+=level*level;
                                level++;
                                st.setGameState(st.LOADING);
                                break outerLoop;
                            }
                            scoreString = "Score: "+score;
                            levelString = "Level: "+(int)level;
                            Log.d("collisionDetection", "invaders[" + i + "] removed by bullet["+j+"]");
                        }
                    }
                }
            }
        }
    }

    public void createInvaders(float level){
        numOfInvaders = 0;
        for(int column=1; column<=6; column++){
            for(int row=1; row<=4; row++ ){
                invaders[numOfInvaders] = new Invaders(this.context, getWidth(), getWidth(), row, column, level);
                numOfInvaders++;
                numOfInvadersAlive++;
            }
        }
        Log.d("Load", "Level "+(int)level);
    }

    public void loadTouchHandler(){
        setOnTouchListener(new OnTouchListener() {
            int lastAction = -1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //for now, player can only shoot when stopped and multiple bullets can be on screen at a time
                if (event.getY() < getHeight() * 3 / 4) {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        for (int x = 0; x < maxNumOfBullet; x++) {
                            if (!bullet[x].isShooting) {
                                bullet[x].setShooting(true);
                                soundPool.play(soundLaser, 0.05f, 0.05f, 0, 0, 1);
                                break;
                            }
                        }
                        /*bullet[numOfShoot].setShooting(true);
                        if(numOfShoot < maxNumOfBullet-1) {
                            numOfShoot++;
                        }else{
                            numOfShoot = 0;
                        }*/
                    }
                }
                switch (event.getActionIndex()) {
                    case 0:
                        switch (event.getActionMasked()) {
                            case MotionEvent.ACTION_DOWN:
                                Log.d("Log.debug", "ACTION_DOWN at X=" + Float.toString(event.getX()) + ", Y=" + Float.toString(event.getY()));
                                if (event.getY() > getHeight() * 3 / 4) {
                                    if (event.getX() > getWidth() / 2) {
                                        ship.setMovementState(ship.RIGHT);
                                        lastAction = 0;
                                    } else {
                                        ship.setMovementState(ship.LEFT);
                                        lastAction = 1;
                                    }
                                }
                                break;
                            case MotionEvent.ACTION_UP:
                                ship.setMovementState(ship.STOPPED);
                                lastAction = -1;
                                break;
                            case MotionEvent.ACTION_POINTER_UP: //two buttons pressed, first one released
                                Log.d("Log.debug", "First button released");
                                if (event.getY(1) > getHeight() * 3 / 4) {
                                    if (event.getX(1) > getWidth() / 2) {
                                        ship.setMovementState(ship.RIGHT);
                                    } else if (event.getX(1) < getWidth() / 2) {
                                        ship.setMovementState(ship.LEFT);
                                    }
                                }
                                break;
                        }
                        return true;
                    case 1: //for handling two-touch events
                        switch (event.getActionMasked()) {
                            case MotionEvent.ACTION_POINTER_DOWN:
                                Log.d("Log.debug", "ACTION_POINTER_DOWN at X= " + Float.toString(event.getX(1)));
                                if (event.getY(1) > getHeight() * 3 / 4) {
                                    if (event.getX(1) < getWidth() / 2 && ship.getMovementState() == ship.RIGHT) { //cancel right movement
                                        ship.setMovementState(ship.STOPPED);
                                    } else if (event.getX(1) > getWidth() / 2 && ship.getMovementState() == ship.LEFT) { //cancel left movement
                                        ship.setMovementState(ship.STOPPED);
                                    }
                                }
                                break;
                            case MotionEvent.ACTION_POINTER_UP:
                                switch (lastAction) {
                                    case 0:
                                        ship.setMovementState(ship.RIGHT);
                                        break;
                                    case 1:
                                        ship.setMovementState(ship.LEFT);
                                }
                                break;
                        }
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void surfaceChanged ( SurfaceHolder holder,
                                 int format , int width , int height ) { // Respond to surface changes , e.g. ,
    }
    @Override
    public void surfaceDestroyed ( SurfaceHolder holder ) {
        // The cleanest way to stop a thread is by interrupting it. // SpaceThread regularly checks its interrupt flag. st.interrupt();
    }

    public int getScore(){
        return score;
    }

    public int getLevel(){
        return (int)level;
    }

}
