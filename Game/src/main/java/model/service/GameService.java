package model.service;

import model.GameSave;
import model.domain.PuzzleState;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

// 游戏业务服务：维护当前局面并提供游戏流程相关操作。
public class GameService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Random random;
    private PuzzleState state;

    public GameService() {
        this(new Random());
    }

    public GameService(Random random) {
        this.random = random;
        this.state = PuzzleState.random(4, random);
    }

    public void startNewGame(int gridSize) {
        this.state = PuzzleState.random(gridSize, random);
    }

    public void restart() {
        startNewGame(state.getGridSize());
    }

    public boolean moveByKeyCode(int keyCode) {
        return state.moveByKeyCode(keyCode);
    }

    public boolean moveTile(int row, int col) {
        return state.moveTile(row, col);
    }

    public boolean isSolved() {
        return state.isSolved();
    }

    public void forceSolved() {
        state.forceSolved();
    }

    public int getGridSize() {
        return state.getGridSize();
    }

    public int getStepCount() {
        return state.getStepCount();
    }

    public int[][] getBoard() {
        return state.getBoardCopy();
    }

    public int getEmptyX() {
        return state.getEmptyX();
    }

    public int getEmptyY() {
        return state.getEmptyY();
    }

    public GameSave buildSave(String username, String imagePath, int imageNum) {
        // 将当前运行态转成可持久化的存档对象。
        return new GameSave(
                username,
                getGridSize(),
                getBoard(),
                getEmptyX(),
                getEmptyY(),
                getStepCount(),
                imagePath,
                imageNum,
                LocalDateTime.now().format(FORMATTER)
        );
    }

    public void loadFromSave(GameSave save) {
        // 从存档完整恢复局面与步数。
        this.state = PuzzleState.fromState(
                save.getGridSize(),
                save.getPuzzleState(),
                save.getEmptyX(),
                save.getEmptyY(),
                save.getStepCount()
        );
    }

    void setState(PuzzleState state) {
        this.state = state;
    }
}
