package model.service;

import model.GameSave;
import model.repository.GameSaveRepository;
import model.repository.json.JsonGameSaveRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// 存档业务服务：负责构建存档对象并委托仓储持久化。
public class SaveService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final GameSaveRepository gameSaveRepository;

    public SaveService() {
        this(new JsonGameSaveRepository());
    }

    public SaveService(GameSaveRepository gameSaveRepository) {
        this.gameSaveRepository = gameSaveRepository;
    }

    public boolean saveGame(String username, int gridSize, int[][] puzzleState,
                            int emptyX, int emptyY, int stepCount,
                            String imagePath, int imageNum) {
        String saveTime = LocalDateTime.now().format(FORMATTER);
        GameSave save = new GameSave(
                username,
                gridSize,
                copyBoard(puzzleState),
                emptyX,
                emptyY,
                stepCount,
                imagePath,
                imageNum,
                saveTime
        );
        return gameSaveRepository.save(save);
    }

    public boolean saveGame(GameSave gameSave) {
        return gameSaveRepository.save(gameSave);
    }

    public GameSave loadGame(String username) {
        return gameSaveRepository.load(username);
    }

    public boolean hasSave(String username) {
        return gameSaveRepository.hasSave(username);
    }

    public boolean deleteSave(String username) {
        return gameSaveRepository.delete(username);
    }

    // 防御性拷贝，避免上层继续修改原棋盘数组。
    private int[][] copyBoard(int[][] board) {
        int[][] copy = new int[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, board[i].length);
        }
        return copy;
    }
}