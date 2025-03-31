package com.example.flappybird;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import static com.example.flappybird.GameView.screenRatioX;
import static com.example.flappybird.GameView.screenRatioY;

public class Background {
    private static final String TAG = "Background";
    public int x = 0, y = 0;
    public Bitmap background;

    // Constructor to initialize the background and scale it to screen size
    Background(int screenX, int screenY, Resources res) {
        try {
            background = BitmapFactory.decodeResource(res, R.drawable.background);
            if (background == null) {
                Log.e(TAG, "Failed to load background image");
                return;
            }

            // Scale background to screen size
            background = Bitmap.createScaledBitmap(background, screenX, screenY, true);
        } catch (Exception e) {
            Log.e(TAG, "Error creating background: " + e.getMessage());
        }
    }

    public void recycle() {
        if (background != null) {
            background.recycle();
            background = null;
        }
    }
}
