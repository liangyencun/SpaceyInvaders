package xc.spaceyinvaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.widget.Switch;

/**
 * Created by Jimmy on 2016/4/8.
 */
public class Invaders {

    int width; //screenWidth
    int height; //screenHeight
    int invadersWidth;
    int invadersHight;
    int margin;
    int row;
    int column;
    float x, y; //
    final int LEFT = 1;
    final int RIGHT = 2;
    int shipMoving;
    float vx; //speed of invaders in Y direction
    Bitmap bitmapInvaders;
    boolean isAlive;

    public Invaders(Context context, int width, int height, int row, int column, float level) {
        this.width = width;
        this.height = height;
        this.row = row;
        this.column = column;
        Bitmap tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.spacey);
        invadersWidth = width/10;
        invadersHight = height/10;
        bitmapInvaders = Bitmap.createScaledBitmap(tmp, invadersWidth, invadersHight, false);
        margin = invadersWidth/4;
        x = column * (invadersWidth + margin);
        y = row * (invadersHight + margin/2);
        vx = (float)(level*1.2);
        shipMoving = RIGHT;
        isAlive = true;
    }

    public void draw(Canvas c){
        Paint p = new Paint();
        p.setColor(Color.RED);

        if(isAlive) {
            c.drawBitmap(bitmapInvaders, x, y, p);
        }
    }

    public void update(){
        if (shipMoving == LEFT) {
            x = x - vx;
        }
        if (shipMoving == RIGHT) {
            x = x + vx;
        }

    }

    public void goDownAndReverse(){
        y = y + 2*margin;
        switch (shipMoving){
            case LEFT:
                shipMoving = RIGHT;
                break;
            case RIGHT:
                shipMoving = LEFT;
                break;
        }
    }

    public float getX(){ return x; }
    public float getY(){ return y; }
    public float getWidth(){ return (float)invadersWidth + margin; }
    public float getHeight(){
        return (float)(invadersHight);
    }

}
