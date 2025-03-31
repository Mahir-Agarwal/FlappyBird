package com.example.flappybird;

import static com.example.flappybird.GameView.screenRatioX;
import static com.example.flappybird.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

public class Flight {
    private static final String TAG = "Flight";
    private static final float GRAVITY = 0.8f;  // Reduced gravity
    private static final float JUMP_FORCE = -15f;
    private static final float MAX_SPEED = 15f;
    private static final float MOVEMENT_SMOOTHING = 0.85f;

    int toShoot = 0;
    boolean isGoingUp = false;
    int x, y, width, height, wingCount = 0, shootCount = 0;
    Bitmap flight1, flight2, shoot1, shoot2, shoot3, shoot4, shoot5, dead;
    private GameView gameView;
    private float velocity = 0;
    private float targetVelocity = 0;

    Flight(GameView gameView, int screenY, Resources res) {
        try {
            this.gameView = gameView;
            loadFlightImages(res);
            loadShootingImages(res);
            loadDeadImage(res);

            // Initialize position
            y = screenY / 2;
            x = (int) (64 * screenRatioX);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing flight: " + e.getMessage());
        }
    }

    private void loadFlightImages(Resources res) {
        flight1 = BitmapFactory.decodeResource(res, R.drawable.fly1);
        flight2 = BitmapFactory.decodeResource(res, R.drawable.fly2);

        if (flight1 == null || flight2 == null) {
            Log.e(TAG, "Failed to load flight images");
            return;
        }

        // Calculate dimensions
        width = flight1.getWidth();
        height = flight1.getHeight();
        width /= 4;
        height /= 4;

        // Scale according to screen size
        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        // Ensure minimum size
        width = Math.max(width, 1);
        height = Math.max(height, 1);

        // Create scaled bitmaps
        flight1 = Bitmap.createScaledBitmap(flight1, width, height, true);
        flight2 = Bitmap.createScaledBitmap(flight2, width, height, true);
    }

    private void loadShootingImages(Resources res) {
        shoot1 = BitmapFactory.decodeResource(res, R.drawable.shoot1);
        shoot2 = BitmapFactory.decodeResource(res, R.drawable.shoot2);
        shoot3 = BitmapFactory.decodeResource(res, R.drawable.shoot3);
        shoot4 = BitmapFactory.decodeResource(res, R.drawable.shoot4);
        shoot5 = BitmapFactory.decodeResource(res, R.drawable.shoot5);

        if (shoot1 == null || shoot2 == null || shoot3 == null || shoot4 == null || shoot5 == null) {
            Log.e(TAG, "Failed to load shooting animation images");
            return;
        }

        shoot1 = Bitmap.createScaledBitmap(shoot1, width, height, true);
        shoot2 = Bitmap.createScaledBitmap(shoot2, width, height, true);
        shoot3 = Bitmap.createScaledBitmap(shoot3, width, height, true);
        shoot4 = Bitmap.createScaledBitmap(shoot4, width, height, true);
        shoot5 = Bitmap.createScaledBitmap(shoot5, width, height, true);
    }

    private void loadDeadImage(Resources res) {
        dead = BitmapFactory.decodeResource(res, R.drawable.dead);
        if (dead == null) {
            Log.e(TAG, "Failed to load dead image");
            return;
        }
        dead = Bitmap.createScaledBitmap(dead, width, height, true);
    }

    void update() {
        // Calculate target velocity based on input
        if (isGoingUp) {
            targetVelocity = JUMP_FORCE;
        } else {
            targetVelocity += GRAVITY;
        }

        // Clamp target velocity
        targetVelocity = Math.max(-MAX_SPEED, Math.min(targetVelocity, MAX_SPEED));

        // Smoothly interpolate current velocity towards target
        velocity = velocity * MOVEMENT_SMOOTHING + targetVelocity * (1 - MOVEMENT_SMOOTHING);

        // Update position
        y += velocity;

        // Keep flight within screen bounds with bounce effect
        int maxY = gameView.getScreenY() - height;
        if (y < 0) {
            y = 0;
            velocity = Math.abs(velocity) * 0.5f; // Bounce with reduced velocity
        } else if (y > maxY) {
            y = maxY;
            velocity = -Math.abs(velocity) * 0.5f; // Bounce with reduced velocity
        }

        // Update animation
        if (velocity < 0) {
            wingCount = 1; // Wings up when moving up
        } else {
            wingCount = 0; // Wings down when moving down
        }
    }

    void jump() {
        isGoingUp = true;
        targetVelocity = JUMP_FORCE;
    }

    void stopJump() {
        isGoingUp = false;
    }

    Bitmap getFlight() {
        if (flight1 == null || flight2 == null) {
            Log.e(TAG, "Flight images not loaded properly");
            return null;
        }

        if (toShoot > 0) {
            return handleShootingAnimation();
        }

        return handleNormalFlight();
    }

    private Bitmap handleShootingAnimation() {
        if (shoot1 == null || shoot2 == null || shoot3 == null || shoot4 == null || shoot5 == null) {
            return flight1; // Fallback to normal flight if shooting animation failed to load
        }

        switch (shootCount) {
            case 1:
                shootCount++;
                return shoot1;
            case 2:
                shootCount++;
                return shoot2;
            case 3:
                shootCount++;
                return shoot3;
            case 4:
                shootCount++;
                return shoot4;
            default:
                shootCount = 1;
                toShoot--;
                gameView.newBullet();
                return shoot5;
        }
    }

    private Bitmap handleNormalFlight() {
        if (wingCount == 0) {
            wingCount++;
            return flight1;
        }
        wingCount--;
        return flight2;
    }

    public void recycle() {
        if (flight1 != null) {
            flight1.recycle();
            flight1 = null;
        }
        if (flight2 != null) {
            flight2.recycle();
            flight2 = null;
        }
        if (shoot1 != null) {
            shoot1.recycle();
            shoot1 = null;
        }
        if (shoot2 != null) {
            shoot2.recycle();
            shoot2 = null;
        }
        if (shoot3 != null) {
            shoot3.recycle();
            shoot3 = null;
        }
        if (shoot4 != null) {
            shoot4.recycle();
            shoot4 = null;
        }
        if (shoot5 != null) {
            shoot5.recycle();
            shoot5 = null;
        }
        if (dead != null) {
            dead.recycle();
            dead = null;
        }
    }

    public void getCollisionShape(Rect rect) {
        rect.set(x, y, x + width, y + height);
    }

    Bitmap getDead() {
        return dead;
    }

    void reset() {
        y = gameView.getScreenY() / 2;
        velocity = 0;
        isGoingUp = false;
        toShoot = 0;
        shootCount = 1;
        wingCount = 0;
    }
}