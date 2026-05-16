package io.github.some_example_name.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.GameSettings;

public class MovingBackgroundView extends View {
    Texture texture;
    int texture1Y;
    int texture2Y;
    int speed = 2;

    public MovingBackgroundView(String pathToTexture) {
        super(0, 0);
        texture1Y = 0;
        texture2Y = GameSettings.SCREEN_HEIGHT;
        texture = new Texture(pathToTexture);
        this.width = GameSettings.SCREEN_WIDTH;
        this.height = GameSettings.SCREEN_HEIGHT;
    }

    public void move() {
        texture1Y -= speed;
        texture2Y -= speed;
        if (texture1Y <= -GameSettings.SCREEN_HEIGHT) {
            texture1Y = GameSettings.SCREEN_HEIGHT;
        }
        if (texture2Y <= -GameSettings.SCREEN_HEIGHT) {
            texture2Y = GameSettings.SCREEN_HEIGHT;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(texture, 0, texture1Y, width, height);
        batch.draw(texture, 0, texture2Y, width, height);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
