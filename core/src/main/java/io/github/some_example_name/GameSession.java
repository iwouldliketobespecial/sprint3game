package io.github.some_example_name;

import com.badlogic.gdx.utils.TimeUtils;
import io.github.some_example_name.managers.MemoryManager;
import java.util.ArrayList;

public class GameSession {
    long nextTrashSpawnTime;
    long sessionStartTime;
    long pauseStartTime;
    long pauseDuration;
    public GameState state;
    private int score;
    int destructedTrashNumber;
    int goldenDestructedTrashNumber;

    public void startGame() {
        sessionStartTime = TimeUtils.millis();
        nextTrashSpawnTime = sessionStartTime + (long) (GameSettings.STARTING_TRASH_APPEARANCE_COOL_DOWN
            * getTrashPeriodCoolDown());
        state = GameState.PLAYING;
        pauseDuration = 0;
        score = 0;
        destructedTrashNumber = 0;
        goldenDestructedTrashNumber = 0;
    }

    public void pauseGame() {
        if (state == GameState.PLAYING) {
            state = GameState.PAUSED;
            pauseStartTime = TimeUtils.millis();
        }
    }

    public void resumeGame() {
        if (state == GameState.PAUSED) {
            state = GameState.PLAYING;
            pauseDuration += TimeUtils.millis() - pauseStartTime;
        }
    }

    public void destructionRegistration() {
        destructedTrashNumber += 1;
    }

    public void destructionRegistrationGolden() {
        goldenDestructedTrashNumber += 1;
    }

    public void updateScore() {
        score = (int) ((TimeUtils.millis() - sessionStartTime - pauseDuration) / 100) + destructedTrashNumber * 10 + goldenDestructedTrashNumber * 50;
    }

    public int getScore() {
        return score;
    }

    public void endGame() {
        updateScore();
        state = GameState.ENDED;
        ArrayList<Integer> recordsTable = MemoryManager.loadRecordsTable();
        if (recordsTable == null) {
            recordsTable = new ArrayList<>();
        }
        int foundIdx = 0;
        for (; foundIdx < recordsTable.size(); foundIdx++) {
            if (recordsTable.get(foundIdx) < getScore()) break;
        }
        recordsTable.add(foundIdx, getScore());
        MemoryManager.saveTableOfRecords(recordsTable);
    }

    public boolean shouldSpawnTrash() {
        if (nextTrashSpawnTime <= getCurrentTime()) {
            nextTrashSpawnTime = getCurrentTime() + (long) (GameSettings.STARTING_TRASH_APPEARANCE_COOL_DOWN
                * getTrashPeriodCoolDown());
            return true;
        }
        return false;
    }

    private long getCurrentTime() {
        return TimeUtils.millis() - pauseDuration;
    }

    private float getTrashPeriodCoolDown() {
        return (float) Math.exp(-0.001 * (getCurrentTime() - sessionStartTime) / 1000);
    }
}
