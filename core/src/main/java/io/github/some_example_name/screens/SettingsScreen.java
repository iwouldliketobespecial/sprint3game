package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.some_example_name.GameResources;
import io.github.some_example_name.GameSettings;
import io.github.some_example_name.MyGdxGame;
import io.github.some_example_name.components.MovingBackgroundView;
import io.github.some_example_name.components.TextView;
import io.github.some_example_name.components.ButtonView;
import io.github.some_example_name.components.ImageView;
import io.github.some_example_name.managers.MemoryManager;

import java.util.ArrayList;

public class SettingsScreen extends ScreenAdapter {
    MyGdxGame myGdxGame;
    MovingBackgroundView backgroundView;
    TextView titleTextView;
    ImageView blackoutImageView;
    ButtonView returnButton;
    TextView musicSettingView;
    TextView soundSettingView;
    TextView clearSettingView;

    public SettingsScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;

        backgroundView = new MovingBackgroundView(GameResources.BACKGROUND_IMG_PATH);

        int centerX = GameSettings.SCREEN_WIDTH / 2;
        int centerY = GameSettings.SCREEN_HEIGHT / 2;

        titleTextView = new TextView(myGdxGame.largeWhiteFont, centerX - 100, 1450, "Settings");
        blackoutImageView = new ImageView(centerX - 350, centerY - 430, 700, 600, GameResources.BLACKOUT_MIDDLE_IMG_PATH);
        musicSettingView = new TextView(myGdxGame.commonWhiteFont, centerX - 200, 1250, "music: ON");
        soundSettingView = new TextView(myGdxGame.commonWhiteFont, centerX - 200, 1130, "sound: ON");
        clearSettingView = new TextView(myGdxGame.commonWhiteFont, centerX - 200, 1010, "clear records");
        returnButton = new ButtonView(centerX - 80, 800, 160, 70, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "return");
    }

    @Override
    public void render(float delta) {
        handleInput();
        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);
        myGdxGame.batch.begin();
        backgroundView.draw(myGdxGame.batch);
        titleTextView.draw(myGdxGame.batch);
        blackoutImageView.draw(myGdxGame.batch);
        returnButton.draw(myGdxGame.batch);
        musicSettingView.draw(myGdxGame.batch);
        soundSettingView.draw(myGdxGame.batch);
        clearSettingView.draw(myGdxGame.batch);
        myGdxGame.batch.end();
    }

    private String translateStateToText(boolean state) {
        return state ? "ON" : "OFF";
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            myGdxGame.touch = myGdxGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (returnButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.setScreen(myGdxGame.menuScreen);
            }
            if (clearSettingView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                MemoryManager.saveTableOfRecords(new ArrayList<>());
                clearSettingView.setText("clear records (cleared)");
            }
            if (musicSettingView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                MemoryManager.saveMusicSettings(!MemoryManager.loadIsMusicOn());
                myGdxGame.audioManager.updateMusicFlag();
                musicSettingView.setText("music: " + translateStateToText(MemoryManager.loadIsMusicOn()));
            }
            if (soundSettingView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                MemoryManager.saveSoundSettings(!MemoryManager.loadIsSoundOn());
                myGdxGame.audioManager.updateSoundFlag();
                soundSettingView.setText("sound: " + translateStateToText(MemoryManager.loadIsSoundOn()));
            }
        }
    }

    @Override
    public void dispose() {
        backgroundView.dispose();
        titleTextView.dispose();
        blackoutImageView.dispose();
        returnButton.dispose();
        musicSettingView.dispose();
        soundSettingView.dispose();
        clearSettingView.dispose();
    }
}
