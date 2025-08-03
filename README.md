<h1>🐦 Flappy World</h1>

<p><strong>FlappyWorld</strong> (or FlappyBird) is a lightweight Android game built using <strong>Java</strong> in <strong>Android Studio</strong>. It was developed as a learning project to explore 2D game development on Android, including Canvas drawing, game loops, and touch interactions.</p>

<h2>🎯 Game Objective & Mechanics</h2>
<p>The aim is simple: tap the screen to keep the bird flying through gaps between moving pipes. Avoid hitting obstacles—otherwise the game ends. Each successful pass increases your score.</p>

<h2>🛠️ How It’s Built</h2>
<ul>
  <li>
    <strong>Game Architecture</strong><br/>
    Uses a custom <code>GameView</code> class extending <code>SurfaceView</code> to handle the game loop, rendering, and user input.
  </li>
  <li>
    <strong>Bitmap & Canvas Drawing</strong><br/>
    All sprites like the bird, pipes and background are drawn using Bitmap images on a Canvas for smooth 2D rendering.
  </li>
  <li>
    <strong>Collision & Scoring</strong><br/>
    <code>Bird</code> and <code>Pipe</code> objects have bounding logic to detect collisions and update the score when the player passes through pipes.
  </li>
  <li>
    <strong>Game Loop</strong><br/>
    A continuously running loop updates physics, redraws frames, and listens for taps for bird movement.
  </li>
</ul>

<h2>📷 Screenshots</h2>
<p align="center">
  <img src="assets/start_screen.png" width="30%" alt="Start Screen">
  <img src="assets/gameplay.png" width="30%" alt="Gameplay">
  <img src="assets/gameover_screen.png" width="30%" alt="Game Over">
</p>
<p align="center">
  <em>Start</em> &nbsp;&nbsp;&nbsp;&nbsp; <em>Gameplay</em> &nbsp;&nbsp;&nbsp;&nbsp; <em>Game Over</em>
</p>

<h2>📚 Learning & Takeaways</h2>
<p>This project helped me learn:</p>
<ul>
  <li>How to use <code>SurfaceView</code> and <code>Canvas</code> in Android for custom animations</li>
  <li>Basic game loop structure and frame updates</li>
  <li>Touch event handling for controlling character movement</li>
</ul>

<h2>📂 Project Structure</h2>
<ul>
  <li>📁 <strong>app/src/main/java/<em>yourpackage</em></strong>
    <ul>
      <li><code>GameActivity.java</code> – Launches the game view</li>
      <li><code>GameView.java</code> – Core render/update loop class</li>
      <li><code>Bird.java</code> – Manages bird's physics and tapping</li>
      <li><code>Pipe.java</code> – Generates and moves pipes</li>
    </ul>
  </li>
  <li>📁 <strong>app/src/main/res/drawable</strong> – All game images (bird, pipe, background, etc.)</li>
  <li>📁 <strong>app/src/main/res/layout</strong> – Optional XML screens (start/game-over layout)</li>
  <li><code>AndroidManifest.xml</code></li>
</ul>

<h2>🚀 Getting Started</h2>
<ol>
  <li>Clone the repository</li>
  <li>Open it using Android Studio</li>
  <li>Run on an emulator or real Android device</li>
</ol>

<h2>🙋‍♂️ About Me</h2>
<p>This is my learning project built while practicing Android game development. Created by Mahir Agarwal using Java in Android Studio. Feedback and pull requests are welcome!</p>
