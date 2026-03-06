package controller;

import model.GameSave;
import model.service.SaveService;

// 存档控制器：保留实例接口并兼容旧的静态调用入口。
public class SaveController {
    private static final SaveService DEFAULT_SAVE_SERVICE = new SaveService();

    private final SaveService saveService;

    public SaveController() {
        this(new SaveService());
    }

    public SaveController(SaveService saveService) {
        this.saveService = saveService;
    }

    public boolean save(String username, int gridSize, int[][] puzzleState,
                        int emptyX, int emptyY, int stepCount,
                        String imagePath, int imageNum) {
        return saveService.saveGame(username, gridSize, puzzleState, emptyX, emptyY, stepCount, imagePath, imageNum);
    }

    public GameSave load(String username) {
        return saveService.loadGame(username);
    }

    public boolean has(String username) {
        return saveService.hasSave(username);
    }

    public boolean delete(String username) {
        return saveService.deleteSave(username);
    }

    // 向后兼容：旧 UI 仍可通过静态方法访问存档能力。
    public static boolean saveGame(String username, int gridSize, int[][] puzzleState,
                                   int emptyX, int emptyY, int stepCount,
                                   String imagePath, int imageNum) {
        return DEFAULT_SAVE_SERVICE.saveGame(username, gridSize, puzzleState, emptyX, emptyY, stepCount, imagePath, imageNum);
    }

    public static GameSave loadGame(String username) {
        return DEFAULT_SAVE_SERVICE.loadGame(username);
    }

    public static boolean hasSave(String username) {
        return DEFAULT_SAVE_SERVICE.hasSave(username);
    }

    public static boolean deleteSave(String username) {
        return DEFAULT_SAVE_SERVICE.deleteSave(username);
    }
}
