package xc.spaceyinvaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by Jimmy on 2016/4/8.
 */
public class Bullet {

    int width; //screenWidth
    int height; //screenHeight
    int bulletWidth;
    int bulletHeight;
    int margin;
    float xi, yi; // initial position of bullets
    float x, y; //
    float vy; //speed of bullets in Y direction
    Bitmap bitmapBullet;
    boolean isShooting;
    boolean isAlive;

    public Bullet(Context context, int width, int height, float shipXPosition, float shipYPosition) {
        Bitmap tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet);
        bulletWidth = width/15;
        bulletHeight = height/25;
        bitmapBullet = Bitmap.createScaledBitmap(tmp, bulletWidth, bulletHeight, false);
        vy = 10;
        this.width = width;
        this.height = height;
        margin = bulletWidth/2;
        xi = (float)(shipXPosition + margin * 1.6); //+margin*1.6 so that bullet comes out of ship's cavity
        yi = shipYPosition-margin;
        x = xi;
        y = yi;
        isShooting = false;
        isAlive = true;
        Log.d("Load", "Bullet");
    }

    public void draw(Canvas c){
        Paint p = new Paint();
        p.setColor(Color.RED);
        if (isShooting) {
            c.drawBitmap(bitmapBullet, x, y, p);
        }
    }

    public void update(float shipXPosition){
        if (isShooting) {
            float tmpY;
            tmpY = y - vy;
            if (tmpY < 0 || (!isAlive)) { //isAlive is linked to collision detection, which is in SpaceView's update()
                setShooting(false);
            }
            y = tmpY;
        }
        else{
            y = yi;
            x = (float) (shipXPosition + margin * 1.6);//+margin*1.6 so that bullet comes out of ship's cavity
            isAlive = true;
        }
    }

    public void setShooting(boolean shooting){
        isShooting = shooting;
    }
    
    public float getX(){return x;}
    public float getY(){return y;}
    public void resetY(){
        y = yi;
    }
}
