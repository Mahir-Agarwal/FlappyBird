package com.example.flappybird;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

import static com.example.flappybird.GameView.screenRatioX;
import static com.example.flappybird.GameView.screenRatioY;

public class Birds {
    private static final String TAG = "Birds";
    public boolean wasShoot = false;
    public int x, y, width, height;
    public Bitmap bird1, bird2, bird3, bird4;

    Birds(Resources res) {
        try {
            bird1 = BitmapFactory.decodeResource(res, R.drawable.bird1);
            bird2 = BitmapFactory.decodeResource(res, R.drawable.bird2);
            bird3 = BitmapFactory.decodeResource(res, R.drawable.bird3);
            bird4 = BitmapFactory.decodeResource(res, R.drawable.bird4);

            if (bird1 == null || bird2 == null || bird3 == null || bird4 == null) {
                Log.e(TAG, "Failed to load bird images");
                return;
            }

            width = bird1.getWidth();
            height = bird1.getHeight();
            width /= 4;
            height /= 4;

            width = (int) (width * screenRatioX);
            height = (int) (height * screenRatioY);

            bird1 = Bitmap.createScaledBitmap(bird1, width, height, true);
            bird2 = Bitmap.createScaledBitmap(bird2, width, height, true);
            bird3 = Bitmap.createScaledBitmap(bird3, width, height, true);
            bird4 = Bitmap.createScaledBitmap(bird4, width, height, true);
        } catch (Exception e) {
            Log.e(TAG, "Error creating birds: " + e.getMessage());
        }
    }

    public Bitmap bird() {
        if (bird1 == null || bird2 == null || bird3 == null || bird4 == null) {
            Log.e(TAG, "Bird images not loaded properly");
            return null;
        }

        if (x < 0) {
            x = 0;
            return bird1;
        }
        if (x < 100) {
            return bird2;
        }
        if (x < 200) {
            return bird3;
        }
        return bird4;
    }

    public void getCollisionShape(Rect rect) {
        rect.set(x, y, x + width, y + height);
    }

    public void recycle() {
        if (bird1 != null) {
            bird1.recycle();
            bird1 = null;
        }
        if (bird2 != null) {
            bird2.recycle();
            bird2 = null;
        }
        if (bird3 != null) {
            bird3.recycle();
            bird3 = null;
        }
        if (bird4 != null) {
            bird4.recycle();
            bird4 = null;
        }
    }
}