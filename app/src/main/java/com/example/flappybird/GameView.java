package com.example.flappybird;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.util.Log;

public class GameView extends SurfaceView implements Runnable {
    private static final String TAG = "GameView";
    private static final String PREF_NAME = "game";
    private static final int MAX_BULLETS = 5;
    private static final int MAX_BIRDS = 3;
    private static final int BULLET_SPEED = 30;
    private static final int BIRD_SPEED = 20;
    private static final int BIRD_SPAWN_INTERVAL = 1500;
    private static final int BACKGROUND_SPEED = 8;
    private static final int FPS = 60;
    private static final long FRAME_PERIOD = 1000 / FPS;

    private Thread gameThread;
    private boolean isPlaying;
    private boolean isGameOver;
    private SurfaceHolder holder;
    private Paint paint;
    private Context context;
    private Flight flight;
    private Bullet[] bullets;
    private Birds[] birds;
    private Background background1, background2;
    private int screenX, screenY;
    private int score;
    private int highScore;
    private int birdCount;
    private long lastBirdSpawnTime;
    private SoundPool soundPool;
    private int shootSound;
    private boolean isMute;
    private SharedPreferences preferences;
    private Rect flightRect;
    private Rect birdRect;
    private Rect bulletRect;

    public static float screenRatioX;
    public static float screenRatioY;

    public GameView(Context context) {
        super(context);
        this.context = context;
        holder = getHolder();
        paint = new Paint();
        
        // Get screen dimensions
        screenX = getResources().getDisplayMetrics().widthPixels;
        screenY = getResources().getDisplayMetrics().heightPixels;
        screenRatioX = screenX / 1920f;
        screenRatioY = screenY / 1080f;
        
        // Initialize collision rects
        flightRect = new Rect();
        birdRect = new Rect();
        bulletRect = new Rect();
        
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        isMute = preferences.getBoolean("isMute", false);
        highScore = preferences.getInt("highscore", 0);

        // Initialize sound
        if (!isMute) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
            shootSound = soundPool.load(context, R.raw.shoot, 1);
        }

        // Initialize game objects
        bullets = new Bullet[MAX_BULLETS];
        birds = new Birds[MAX_BIRDS];
        for (int i = 0; i < MAX_BULLETS; i++) {
            bullets[i] = new Bullet(context);
        }
        for (int i = 0; i < MAX_BIRDS; i++) {
            birds[i] = new Birds(context.getResources());
        }

        // Initialize backgrounds
        background1 = new Background(screenX, screenY, context.getResources());
        background2 = new Background(screenX, screenY, context.getResources());
        background2.x = screenX;

        // Initialize flight
        flight = new Flight(this, screenY, context.getResources());
        lastBirdSpawnTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;
        long targetTime = 1000 / FPS;

        while (isPlaying) {
            startTime = System.nanoTime();
            update();
            draw();
            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - timeMillis;

            if (waitTime > 0) {
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Error in sleep: " + e.getMessage());
                }
            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;

            if (frameCount == FPS) {
                float averageFPS = 1000 / ((totalTime / frameCount) / 1000000f);
                frameCount = 0;
                totalTime = 0;
                if (averageFPS < FPS - 5) {
                    Log.w(TAG, "Low FPS: " + averageFPS);
                }
            }
        }
    }

    private void update() {
        if (isGameOver) return;

        // Update backgrounds
        background1.x -= BACKGROUND_SPEED;
        background2.x -= BACKGROUND_SPEED;

        if (background1.x + background1.background.getWidth() < 0) {
            background1.x = screenX;
        }
        if (background2.x + background2.background.getWidth() < 0) {
            background2.x = screenX;
        }

        // Update flight
        flight.update();

        // Update bullets
        for (Bullet bullet : bullets) {
            if (bullet.isActive) {
                bullet.x += BULLET_SPEED;
                if (bullet.x > screenX) {
                    bullet.isActive = false;
                }
            }
        }

        // Spawn new birds
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBirdSpawnTime > BIRD_SPAWN_INTERVAL && birdCount < MAX_BIRDS) {
            for (int i = 0; i < MAX_BIRDS; i++) {
                if (!birds[i].wasShoot) {
                    birds[i].x = screenX;
                    birds[i].y = (int) (Math.random() * (screenY - birds[i].height));
                    birds[i].wasShoot = true;
                    birdCount++;
                    lastBirdSpawnTime = currentTime;
                    break;
                }
            }
        }

        // Update birds
        for (Birds bird : birds) {
            if (bird.wasShoot) {
                bird.x -= BIRD_SPEED;
                if (bird.x < -bird.width) {
                    bird.wasShoot = false;
                    birdCount--;
                }
            }
        }

        // Check collisions
        checkCollisions();
    }

    private void checkCollisions() {
        // Get flight collision shape
        flight.getCollisionShape(flightRect);
        
        // Adjust collision box to be slightly smaller for better gameplay
        int collisionMargin = 10;
        flightRect.inset(collisionMargin, collisionMargin);
        
        // Check bird collisions
        for (Birds bird : birds) {
            if (bird.wasShoot) {
                bird.getCollisionShape(birdRect);
                // Make bird collision box slightly smaller
                birdRect.inset(collisionMargin, collisionMargin);
                
                if (Rect.intersects(flightRect, birdRect)) {
                    gameOver();
                    return;
                }
            }
        }

        // Check bullet collisions
        for (Bullet bullet : bullets) {
            if (bullet.isActive) {
                bullet.getCollisionShape(bulletRect);
                for (Birds bird : birds) {
                    if (bird.wasShoot) {
                        bird.getCollisionShape(birdRect);
                        if (Rect.intersects(bulletRect, birdRect)) {
                            bullet.isActive = false;
                            bird.wasShoot = false;
                            birdCount--;
                            score++;
                            if (score > highScore) {
                                highScore = score;
                                preferences.edit().putInt("highscore", highScore).apply();
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private void draw() {
        if (holder.getSurface().isValid()) {
            Canvas canvas = holder.lockCanvas();
            canvas.drawColor(Color.WHITE);

            // Draw backgrounds
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);

            // Draw game objects
            canvas.drawBitmap(flight.getFlight(), flight.x, flight.y, paint);
            
            for (Bullet bullet : bullets) {
                if (bullet.isActive) {
                    canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint);
                }
            }

            for (Birds bird : birds) {
                if (bird.wasShoot) {
                    canvas.drawBitmap(bird.bird(), bird.x, bird.y, paint);
                }
            }

            // Draw score
            paint.setColor(Color.BLACK);
            paint.setTextSize(64 * screenRatioX);
            canvas.drawText(score + "", screenX / 2, 64 * screenRatioY, paint);

            // Draw game over
            if (isGameOver) {
                paint.setTextSize(128 * screenRatioX);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Game Over", screenX / 2, screenY / 2, paint);
                paint.setTextSize(64 * screenRatioX);
                canvas.drawText("Tap to restart", screenX / 2, screenY / 2 + 100 * screenRatioY, paint);
            }

            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void resume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void pause() {
        isPlaying = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e(TAG, "Error in pause: " + e.getMessage());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isGameOver) {
                    // Reset game
                    isGameOver = false;
                    score = 0;
                    birdCount = 0;
                    lastBirdSpawnTime = System.currentTimeMillis();
                    
                    // Reset birds
                    for (Birds bird : birds) {
                        bird.wasShoot = false;
                    }
                    
                    // Reset bullets
                    for (Bullet bullet : bullets) {
                        bullet.isActive = false;
                    }
                    
                    // Reset flight
                    flight.reset();
                    
                    // Restart game
                    resume();
                } else {
                    // Left side of screen for jump, right side for shoot
                    if (event.getX() < screenX / 2) {
                        flight.jump();
                    } else {
                        flight.toShoot++;
                        if (!isMute && soundPool != null) {
                            soundPool.play(shootSound, 1, 1, 1, 0, 1);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (event.getX() < screenX / 2) {
                    flight.stopJump();
                }
                break;
        }
        return true;
    }

    public void newBullet() {
        for (Bullet bullet : bullets) {
            if (!bullet.isActive) {
                bullet.x = flight.x + flight.width / 2;
                bullet.y = flight.y + flight.height / 2;
                bullet.isActive = true;
                break;
            }
        }
    }

    private void gameOver() {
        isGameOver = true;
        isPlaying = false;
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        preferences.edit().putInt("highscore", highScore).apply();
    }

    public int getScreenX() {
        return screenX;
    }

    public int getScreenY() {
        return screenY;
    }
}