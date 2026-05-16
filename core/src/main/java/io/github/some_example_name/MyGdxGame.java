package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import io.github.some_example_name.managers.AudioManager;
import io.github.some_example_name.screens.GameScreen;
import io.github.some_example_name.screens.MenuScreen;
import io.github.some_example_name.screens.SettingsScreen;
import static io.github.some_example_name.GameSettings.*;

public class MyGdxGame extends Game {
    public World world;
    public Vector3 touch;
    public SpriteBatch batch;
    public OrthographicCamera camera;
    public GameScreen gameScreen;
    public MenuScreen menuScreen;
    public SettingsScreen settingsScreen;
    public AudioManager audioManager;
    public BitmapFont commonWhiteFont;
    public BitmapFont commonBlackFont;
    public BitmapFont largeWhiteFont;
    public Texture whiteTexture;
    float accumulator = 0;

    @Override
    public void create() {
        Box2D.init();
        world = new World(new Vector2(0, 0), true);
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        commonWhiteFont = FontBuilder.generate(30, Color.WHITE, GameResources.FONT_PATH);
        commonBlackFont = FontBuilder.generate(30, Color.BLACK, GameResources.FONT_PATH);
        largeWhiteFont = FontBuilder.generate(48, Color.WHITE, GameResources.FONT_PATH);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whiteTexture = new Texture(pixmap);
        pixmap.dispose();

        audioManager = new AudioManager();

        gameScreen = new GameScreen(this);
        menuScreen = new MenuScreen(this);
        settingsScreen = new SettingsScreen(this);

        setScreen(menuScreen);
    }

    @Override
    public void dispose() {
        batch.dispose();
        world.dispose();
        commonWhiteFont.dispose();
        commonBlackFont.dispose();
        largeWhiteFont.dispose();
        whiteTexture.dispose();
        audioManager.backgroundMusic.dispose();
        audioManager.shootSound.dispose();
        audioManager.explosionSound.dispose();
    }

    public void stepWorld() {
        float delta = Gdx.graphics.getDeltaTime();
        accumulator += Math.min(delta, 0.25f);
        if (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;
            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }
    }
}
