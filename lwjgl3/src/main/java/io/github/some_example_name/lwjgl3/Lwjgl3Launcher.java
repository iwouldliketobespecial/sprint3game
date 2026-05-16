package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.some_example_name.MyGdxGame;
import io.github.some_example_name.GameSettings;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(GameSettings.SCREEN_WIDTH / 2, GameSettings.SCREEN_HEIGHT / 2);
        config.setTitle("Space Cleaner");
        new Lwjgl3Application(new MyGdxGame(), config);
    }
}
