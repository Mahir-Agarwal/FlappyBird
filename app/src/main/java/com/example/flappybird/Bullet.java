package com.example.flappybird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

import static com.example.flappybird.GameView.screenRatioX;
import static com.example.flappybird.GameView.screenRatioY;

public class Bullet {
    private static final String TAG = "Bullet";
    public boolean isActive = false;
    public int x, y;
    public Bitmap bullet;

    Bullet(Context context) {
        try {
            bullet = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet);
            if (bullet == null) {
                Log.e(TAG, "Failed to load bullet image");
                return;
            }

            int width = bullet.getWidth();
            int height = bullet.getHeight();
            width /= 4;
            height /= 4;

            width = (int) (width * screenRatioX);
            height = (int) (height * screenRatioY);

            bullet = Bitmap.createScaledBitmap(bullet, width, height, true);
        } catch (Exception e) {
            Log.e(TAG, "Error creating bullet: " + e.getMessage());
        }
    }

    public void recycle() {
        if (bullet != null) {
            bullet.recycle();
            bullet = null;
        }
    }

    public void getCollisionShape(Rect rect) {
        rect.set(x, y, x + bullet.getWidth(), y + bullet.getHeight());
    }
}