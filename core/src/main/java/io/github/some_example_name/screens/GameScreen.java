package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.some_example_name.GameResources;
import io.github.some_example_name.GameSettings;
import io.github.some_example_name.GameSession;
import io.github.some_example_name.MyGdxGame;
import io.github.some_example_name.GameState;
import io.github.some_example_name.components.MovingBackgroundView;
import io.github.some_example_name.components.LiveView;
import io.github.some_example_name.components.TextView;
import io.github.some_example_name.components.ButtonView;
import io.github.some_example_name.components.ImageView;
import io.github.some_example_name.components.RecordsListView;
import io.github.some_example_name.managers.ContactManager;
import io.github.some_example_name.managers.MemoryManager;
import io.github.some_example_name.objects.BulletObject;
import io.github.some_example_name.objects.ShipObject;
import io.github.some_example_name.objects.TrashObject;
import io.github.some_example_name.objects.GoldenTrashObject;

import java.util.ArrayList;
import java.util.Random;

public class GameScreen extends ScreenAdapter {

    MyGdxGame myGdxGame;
    GameSession gameSession;
    ShipObject shipObject;

    ArrayList<TrashObject> trashArray;
    ArrayList<GoldenTrashObject> goldenTrashArray;
    ArrayList<BulletObject> bulletArray;

    ContactManager contactManager;

    MovingBackgroundView backgroundView;
    ImageView topBlackoutView;
    LiveView liveView;
    TextView scoreTextView;
    ButtonView pauseButton;

    ImageView fullBlackoutView;
    TextView pauseTextView;
    ButtonView homeButton;
    ButtonView continueButton;

    TextView recordsTextView;
    RecordsListView recordsListView;
    ButtonView homeButton2;

    public GameScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;
        gameSession = new GameSession();

        contactManager = new ContactManager(myGdxGame.world);

        trashArray = new ArrayList<>();
        goldenTrashArray = new ArrayList<>();
        bulletArray = new ArrayList<>();

        shipObject = new ShipObject(
            GameSettings.SCREEN_WIDTH / 2, 150,
            GameSettings.SHIP_WIDTH, GameSettings.SHIP_HEIGHT,
            GameResources.SHIP_IMG_PATHS,
            myGdxGame.world
        );

        backgroundView = new MovingBackgroundView(GameResources.BACKGROUND_IMG_PATH);

        int topBarY = GameSettings.SCREEN_HEIGHT - 160;
        int centerX = GameSettings.SCREEN_WIDTH / 2;

        topBlackoutView = new ImageView(0, topBarY, GameSettings.SCREEN_WIDTH, 160, GameResources.BLACKOUT_TOP_IMG_PATH);
        liveView = new LiveView(centerX - 45, topBarY + 25);
        scoreTextView = new TextView(myGdxGame.commonWhiteFont, 50, topBarY + 25);
        pauseButton = new ButtonView(GameSettings.SCREEN_WIDTH - 100, topBarY + 10, 46, 54, GameResources.PAUSE_IMG_PATH);

        fullBlackoutView = new ImageView(0, 0, GameResources.FULL_BLACKOUT_IMG_PATH);
        pauseTextView = new TextView(myGdxGame.commonWhiteFont, centerX - 30, GameSettings.SCREEN_HEIGHT / 2 + 150, "Pause");
        homeButton = new ButtonView(centerX - 250, GameSettings.SCREEN_HEIGHT / 2 - 20, 250, 120, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Home");
        continueButton = new ButtonView(centerX + 40, GameSettings.SCREEN_HEIGHT / 2 - 20, 250, 120, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Continue");

        recordsListView = new RecordsListView(myGdxGame.commonWhiteFont, GameSettings.SCREEN_HEIGHT / 2 + 100);
        recordsTextView = new TextView(myGdxGame.largeWhiteFont, centerX - 145, GameSettings.SCREEN_HEIGHT / 2 + 300, "Last records");
        homeButton2 = new ButtonView(centerX - 125, GameSettings.SCREEN_HEIGHT / 2 - 300, 250, 120, myGdxGame.commonBlackFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Home");
    }

    @Override
    public void show() {
        restartGame();
    }

    @Override
    public void render(float delta) {
        handleInput();

        if (gameSession.state == GameState.PLAYING) {
            myGdxGame.stepWorld();
            shipObject.updateAnimation(delta);

            if (gameSession.shouldSpawnTrash()) {
                Random random = new Random();
                boolean isGolden = random.nextInt(10) == 0;
                if (isGolden) {
                    GoldenTrashObject goldenTrash = new GoldenTrashObject(
                        GameSettings.TRASH_WIDTH, GameSettings.TRASH_HEIGHT,
                        GameResources.GOLDEN_TRASH_IMG_PATH,
                        myGdxGame.world
                    );
                    goldenTrashArray.add(goldenTrash);
                } else {
                    TrashObject trashObject = new TrashObject(
                        GameSettings.TRASH_WIDTH, GameSettings.TRASH_HEIGHT,
                        GameResources.TRASH_IMG_PATH,
                        myGdxGame.world
                    );
                    trashArray.add(trashObject);
                }
            }

            if (shipObject.needToShoot()) {
                BulletObject laserBullet = new BulletObject(
                    shipObject.getX(), shipObject.getY() + shipObject.height / 2,
                    GameSettings.BULLET_WIDTH, GameSettings.BULLET_HEIGHT,
                    GameResources.BULLET_IMG_PATH,
                    myGdxGame.world
                );
                bulletArray.add(laserBullet);
                if (myGdxGame.audioManager.isSoundOn) myGdxGame.audioManager.shootSound.play();
            }

            if (!shipObject.isAlive()) {
                gameSession.endGame();
                recordsListView.setRecords(MemoryManager.loadRecordsTable());
            }

            updateTrash();
            updateGoldenTrash();
            updateBullets();
            backgroundView.move();
            gameSession.updateScore();
            scoreTextView.setText("Score: " + gameSession.getScore());
            liveView.setLeftLives(shipObject.getLiveLeft());
        }

        draw();
    }

    private void handleInput() {
        if (Gdx.input.isTouched()) {
            myGdxGame.touch = myGdxGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            switch (gameSession.state) {
                case PLAYING:
                    if (pauseButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        gameSession.pauseGame();
                    } else {
                        shipObject.move(myGdxGame.touch);
                    }
                    break;

                case PAUSED:
                    if (continueButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        gameSession.resumeGame();
                    }
                    if (homeButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        myGdxGame.setScreen(myGdxGame.menuScreen);
                    }
                    break;

                case ENDED:
                    if (homeButton2.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        myGdxGame.setScreen(myGdxGame.menuScreen);
                    }
                    break;
            }
        }
    }

    private void draw() {
        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);

        myGdxGame.batch.begin();
        backgroundView.draw(myGdxGame.batch);
        for (TrashObject trash : trashArray) trash.draw(myGdxGame.batch);
        for (GoldenTrashObject goldenTrash : goldenTrashArray) goldenTrash.draw(myGdxGame.batch);
        shipObject.draw(myGdxGame.batch);
        for (BulletObject bullet : bulletArray) bullet.draw(myGdxGame.batch);
        topBlackoutView.draw(myGdxGame.batch);
        scoreTextView.draw(myGdxGame.batch);
        liveView.draw(myGdxGame.batch);
        pauseButton.draw(myGdxGame.batch);

        if (gameSession.state == GameState.PAUSED) {
            fullBlackoutView.draw(myGdxGame.batch);
            pauseTextView.draw(myGdxGame.batch);
            homeButton.draw(myGdxGame.batch);
            continueButton.draw(myGdxGame.batch);
        } else if (gameSession.state == GameState.ENDED) {
            fullBlackoutView.draw(myGdxGame.batch);
            recordsTextView.draw(myGdxGame.batch);
            recordsListView.draw(myGdxGame.batch);
            homeButton2.draw(myGdxGame.batch);
        }

        myGdxGame.batch.end();
    }

    private void updateTrash() {
        for (int i = 0; i < trashArray.size(); i++) {
            TrashObject trash = trashArray.get(i);
            boolean hasToBeDestroyed = !trash.isAlive() || !trash.isInFrame();

            if (!trash.isAlive()) {
                gameSession.destructionRegistration();
                if (myGdxGame.audioManager.isSoundOn) myGdxGame.audioManager.explosionSound.play(0.2f);
            }

            if (hasToBeDestroyed) {
                myGdxGame.world.destroyBody(trash.body);
                trashArray.remove(i--);
            }
        }
    }

    private void updateGoldenTrash() {
        for (int i = 0; i < goldenTrashArray.size(); i++) {
            GoldenTrashObject goldenTrash = goldenTrashArray.get(i);
            boolean hasToBeDestroyed = !goldenTrash.isAlive() || !goldenTrash.isInFrame();

            if (!goldenTrash.isAlive()) {
                gameSession.destructionRegistrationGolden();
                shipObject.addLife();
                if (myGdxGame.audioManager.isSoundOn) myGdxGame.audioManager.explosionSound.play(0.2f);
            }

            if (hasToBeDestroyed) {
                myGdxGame.world.destroyBody(goldenTrash.body);
                goldenTrashArray.remove(i--);
            }
        }
    }

    private void updateBullets() {
        for (int i = 0; i < bulletArray.size(); i++) {
            if (bulletArray.get(i).hasToBeDestroyed()) {
                myGdxGame.world.destroyBody(bulletArray.get(i).body);
                bulletArray.remove(i--);
            }
        }
    }

    private void restartGame() {
        for (TrashObject trash : trashArray) {
            myGdxGame.world.destroyBody(trash.body);
        }
        for (GoldenTrashObject goldenTrash : goldenTrashArray) {
            myGdxGame.world.destroyBody(goldenTrash.body);
        }
        for (BulletObject bullet : bulletArray) {
            myGdxGame.world.destroyBody(bullet.body);
        }
        trashArray.clear();
        goldenTrashArray.clear();
        bulletArray.clear();

        if (shipObject != null) {
            myGdxGame.world.destroyBody(shipObject.body);
        }

        shipObject = new ShipObject(
            GameSettings.SCREEN_WIDTH / 2, 150,
            GameSettings.SHIP_WIDTH, GameSettings.SHIP_HEIGHT,
            GameResources.SHIP_IMG_PATHS,
            myGdxGame.world
        );

        gameSession.startGame();
    }

    @Override
    public void dispose() {
        for (TrashObject trash : trashArray) {
            myGdxGame.world.destroyBody(trash.body);
        }
        for (GoldenTrashObject goldenTrash : goldenTrashArray) {
            myGdxGame.world.destroyBody(goldenTrash.body);
        }
        for (BulletObject bullet : bulletArray) {
            myGdxGame.world.destroyBody(bullet.body);
        }
        trashArray.clear();
        goldenTrashArray.clear();
        bulletArray.clear();
        backgroundView.dispose();
        liveView.dispose();
        pauseButton.dispose();
        fullBlackoutView.dispose();
        pauseTextView.dispose();
        homeButton.dispose();
        continueButton.dispose();
        recordsTextView.dispose();
        homeButton2.dispose();
    }
}
