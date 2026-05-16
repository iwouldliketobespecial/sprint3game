package io.github.some_example_name.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import io.github.some_example_name.GameSettings;

public class ShipObject extends GameObject {
    long lastShotTime;
    int livesLeft;
    private Animation<TextureRegion> animation;
    private float animationTime;
    private TextureRegion currentFrame;

    public ShipObject(int x, int y, int width, int height, Array<String> texturePaths, World world) {
        super(texturePaths.first(), x, y, width, height, GameSettings.SHIP_BIT, world);
        Array<TextureRegion> frames = new Array<>();
        for (String path : texturePaths) {
            frames.add(new TextureRegion(new Texture(path)));
        }
        animation = new Animation<>(0.1f, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        animationTime = 0f;
        body.setLinearDamping(10);
        livesLeft = 3;
    }

    public void updateAnimation(float delta) {
        animationTime += delta;
        currentFrame = animation.getKeyFrame(animationTime);
    }

    public int getLiveLeft() {
        return livesLeft;
    }

    public void addLife() {
        if (livesLeft < 3) {
            livesLeft++;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        putInFrame();
        if (currentFrame != null) {
            batch.draw(currentFrame, getX() - (width / 2f), getY() - (height / 2f), width, height);
        } else {
            super.draw(batch);
        }
    }

    public void move(Vector3 vector3) {
        body.applyForceToCenter(new Vector2(
                (vector3.x - getX()) * GameSettings.SHIP_FORCE_RATIO,
                (vector3.y - getY()) * GameSettings.SHIP_FORCE_RATIO),
            true
        );
    }

    private void putInFrame() {
        if (getY() > (GameSettings.SCREEN_HEIGHT / 2f - height / 2f)) {
            setY((int) (GameSettings.SCREEN_HEIGHT / 2f - height / 2f));
        }
        if (getY() <= (height / 2f)) {
            setY(height / 2);
        }
        if (getX() < (-width / 2f)) {
            setX(GameSettings.SCREEN_WIDTH);
        }
        if (getX() > (GameSettings.SCREEN_WIDTH + width / 2f)) {
            setX(0);
        }
    }

    public boolean needToShoot() {
        if (TimeUtils.millis() - lastShotTime >= GameSettings.SHOOTING_COOL_DOWN) {
            lastShotTime = TimeUtils.millis();
            return true;
        }
        return false;
    }

    @Override
    public void hit() {
        livesLeft -= 1;
    }

    public boolean isAlive() {
        return livesLeft > 0;
    }
}
