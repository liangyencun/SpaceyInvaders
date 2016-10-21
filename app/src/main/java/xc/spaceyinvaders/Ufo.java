package xc.spaceyinvaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import java.util.Random;

/**
 * Created by Aaron on 2016-04-10.
 */
public class Ufo extends Ship{
    public Ufo(Context context, int width, int height) {
        Bitmap tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.ufo);
        shipWidth = width/8;
        shipHeight = shipWidth/2;
        bitmapShip = Bitmap.createScaledBitmap(tmp, shipWidth, shipHeight, false);
        margin = shipHeight/2;
        this.width = width;
        this.height = height;
        vx = 10;
        y = margin;
        x = -width/2; //starts off screen left
        shipMoving = RIGHT; //starts moving right
        Log.d("Load", "UFO");
    }

    @Override
    void draw(Canvas c) {
        super.draw(c);
    }

    @Override
    void update() {
        if(shipMoving == LEFT) {
            x = x - vx;
            if (x <= -width/2 ) {
                Random rng = new Random();
                    if (rng.nextInt(350) < 1) { //1 in 350 chance every time update() is called
                    shipMoving = RIGHT;
                }
            }
        }

        if(shipMoving == RIGHT){
            x = x + vx;
            if (x >= width*3/2) {
                Random rng = new Random();
                if (rng.nextInt(350) < 1) {
                    shipMoving = LEFT;
                }
            }
        }
    }
    @Override
    public float getX(){ return super.getX(); }

    @Override
    public float getY(){ return super.getY(); }
}
