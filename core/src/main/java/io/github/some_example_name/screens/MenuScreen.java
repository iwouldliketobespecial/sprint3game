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

public class MenuScreen extends ScreenAdapter {
    MyGdxGame myGdxGame;
    MovingBackgroundView backgroundView;
    TextView titleView;
    ButtonView startButtonView;
    ButtonView settingsButtonView;
    ButtonView exitButtonView;

    public MenuScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;
        backgroundView = new MovingBackgroundView(GameResources.MENU_BACKGROUND_IMG_PATH);

        int centerX = GameSettings.SCREEN_WIDTH / 2;
        int buttonWidth = 620;
        int buttonHeight = 90;
        int buttonX = centerX - buttonWidth / 2;

        titleView = new TextView(myGdxGame.largeWhiteFont, centerX - 180, 1550, "Space Cleaner");
        startButtonView = new ButtonView(buttonX, 1250, buttonWidth, buttonHeight, myGdxGame.commonBlackFont, GameResources.BUTTON_LONG_BG_IMG_PATH, "start");
        settingsButtonView = new ButtonView(buttonX, 1120, buttonWidth, buttonHeight, myGdxGame.commonBlackFont, GameResources.BUTTON_LONG_BG_IMG_PATH, "settings");
        exitButtonView = new ButtonView(buttonX, 990, buttonWidth, buttonHeight, myGdxGame.commonBlackFont, GameResources.BUTTON_LONG_BG_IMG_PATH, "exit");
    }

    @Override
    public void render(float delta) {
        handleInput();
        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.BLACK);
        myGdxGame.batch.begin();
        backgroundView.draw(myGdxGame.batch);
        titleView.draw(myGdxGame.batch);
        startButtonView.draw(myGdxGame.batch);
        settingsButtonView.draw(myGdxGame.batch);
        exitButtonView.draw(myGdxGame.batch);
        myGdxGame.batch.end();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            myGdxGame.touch = myGdxGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (startButtonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.setScreen(myGdxGame.gameScreen);
            }
            if (exitButtonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                Gdx.app.exit();
            }
            if (settingsButtonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.setScreen(myGdxGame.settingsScreen);
            }
        }
    }

    @Override
    public void dispose() {
        backgroundView.dispose();
        titleView.dispose();
        startButtonView.dispose();
        settingsButtonView.dispose();
        exitButtonView.dispose();
    }
}
