package xc.spaceyinvaders;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class Ship {

    int width; //screenWidth
    int height; //screenHeight
    int shipWidth;
    int shipHeight;
    int margin; //ship will be within the margin on left, right, and bottom
    float x,y;
    float vx; // speed of ship in x direction
    Bitmap bitmapShip;

    final int STOPPED = 0;
    final int LEFT = 1;
    final int RIGHT = 2;
    int shipMoving  =STOPPED;

    public Ship(){}

    public Ship(Context context, int width, int height) {
        Bitmap tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship);
        shipWidth = width/6;
        shipHeight = height/8;
        bitmapShip = Bitmap.createScaledBitmap(tmp, shipWidth, shipHeight, false);
        margin = shipWidth/2;
        x = width/2 - shipWidth/2; //bottom center of screen
        y = height - shipHeight - margin;
        vx = 10;
        this.width = width;
        this.height = height;
        Log.d("Load", "Ship");
    }

    void draw(Canvas c) {
        Paint p = new Paint();
        p.setColor(Color.RED);

        c.drawBitmap(bitmapShip, x, y, p);
    }

    void update() {
        if(shipMoving == LEFT) {
            x = x - vx;
            if (x <= 0 ) {
                x = 0;
            }
        }

        if(shipMoving == RIGHT){
            x = x + vx;
            if (x >= (width-shipWidth)) {
                x = width - shipWidth;
            }
        }
    }

    public void setMovementState(int state){
        shipMoving = state;
    }

    public int getMovementState(){
        return shipMoving;
    }

    public float getX(){ return x; }

    public float getY(){ return y; }

    public boolean getIsStopped(){ return shipMoving == STOPPED; }
}
