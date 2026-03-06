package controller;

import model.GameSave;
import model.service.GameService;
import model.service.SaveService;
import util.ImageScanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 游戏控制器：负责衔接 UI 事件与游戏/存档业务服务。
public class GameController {
    public enum Theme {
        ANIMAL,
        GIRL,
        SPORT,
        PERSON,
        POKEMON
    }

    private static final String PATH_ANIMAL = "image/animal/animal";
    private static final String PATH_GIRL = "image/girl/girl";
    private static final String PATH_SPORT = "image/sport/sport";
    private static final String PATH_PERSON = "image/person/person";
    private static final String PATH_POKEMON = "image/pokemon/pokemon";

    private final GameService gameService;
    private final SaveService saveService;

    private String imagePath = PATH_ANIMAL;
    private int imageNum = 1;
    private String lastLoadTime;

    private List<Integer> animalNumbers = new ArrayList<>();
    private List<Integer> girlNumbers = new ArrayList<>();
    private List<Integer> sportNumbers = new ArrayList<>();
    private List<Integer> personNumbers = new ArrayList<>();
    private List<Integer> pokemonNumbers = new ArrayList<>();

    private int animalIndex = 0;
    private int girlIndex = 0;
    private int sportIndex = 0;
    private int personIndex = 0;
    private int pokemonIndex = 0;

    public GameController() {
        this(new GameService(), new SaveService(), true);
    }

    public GameController(GameService gameService, SaveService saveService) {
        this(gameService, saveService, true);
    }

    public GameController(GameService gameService, SaveService saveService, boolean initThemes) {
        this.gameService = gameService;
        this.saveService = saveService;
        if (initThemes) {
            initRandomImageNumbers();
        } else {
            animalNumbers = normalizeNumbers(new ArrayList<>());
            girlNumbers = normalizeNumbers(new ArrayList<>());
            sportNumbers = normalizeNumbers(new ArrayList<>());
            personNumbers = normalizeNumbers(new ArrayList<>());
            pokemonNumbers = normalizeNumbers(new ArrayList<>());
        }
    }

    public int getGridSize() {
        return gameService.getGridSize();
    }

    public int getPieceSize() {
        return switch (getGridSize()) {
            case 2 -> 210;
            case 3 -> 140;
            case 5 -> 84;
            default -> 105;
        };
    }

    public int[][] getBoard() {
        return gameService.getBoard();
    }

    public int getStepCount() {
        return gameService.getStepCount();
    }

    public boolean isWin() {
        return gameService.isSolved();
    }

    public boolean moveByKeyCode(int keyCode) {
        return gameService.moveByKeyCode(keyCode);
    }

    public boolean clickTile(int row, int col) {
        return gameService.moveTile(row, col);
    }

    public void forceSolved() {
        gameService.forceSolved();
    }

    public void restart() {
        gameService.restart();
    }

    public void changeDifficulty(int newGridSize) {
        if (getGridSize() != newGridSize) {
            gameService.startNewGame(newGridSize);
        }
    }

    public void changeTheme(Theme theme) {
        // 切换主题时同时切换图片编号，并重开当前难度棋盘。
        switch (theme) {
            case ANIMAL -> {
                imagePath = PATH_ANIMAL;
                imageNum = getNextAnimalNumber();
            }
            case GIRL -> {
                imagePath = PATH_GIRL;
                imageNum = getNextGirlNumber();
            }
            case SPORT -> {
                imagePath = PATH_SPORT;
                imageNum = getNextSportNumber();
            }
            case PERSON -> {
                imagePath = PATH_PERSON;
                imageNum = getNextPersonNumber();
            }
            case POKEMON -> {
                imagePath = PATH_POKEMON;
                imageNum = getNextPokemonNumber();
            }
            default -> throw new IllegalStateException("Unexpected theme: " + theme);
        }
        restart();
    }

    public boolean saveCurrentGame(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        return saveService.saveGame(gameService.buildSave(username, imagePath, imageNum));
    }

    public boolean loadGame(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        GameSave save = saveService.loadGame(username);
        if (save == null) {
            return false;
        }
        gameService.loadFromSave(save);
        imagePath = save.getImagePath();
        imageNum = save.getImageNum();
        lastLoadTime = save.getSaveTime();
        return true;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getImageNum() {
        return imageNum;
    }

    public String getLastLoadTime() {
        return lastLoadTime;
    }

    public void setImage(String imagePath, int imageNum) {
        this.imagePath = imagePath;
        this.imageNum = imageNum;
    }

    private void initRandomImageNumbers() {
        // 扫描资源目录，动态获取每个主题下可用图片编号。
        ImageScanner.ImageNumbers imageNumbers = ImageScanner.scanAllThemes();
        animalNumbers = normalizeNumbers(imageNumbers.getAnimalNumbers());
        girlNumbers = normalizeNumbers(imageNumbers.getGirlNumbers());
        sportNumbers = normalizeNumbers(imageNumbers.getSportNumbers());
        personNumbers = normalizeNumbers(imageNumbers.getPersonNumbers());
        pokemonNumbers = normalizeNumbers(imageNumbers.getPokemonNumbers());
    }

    private List<Integer> normalizeNumbers(List<Integer> numbers) {
        List<Integer> result = new ArrayList<>(numbers);
        if (result.isEmpty()) {
            // 兜底编号，避免空主题导致 UI 崩溃。
            result.add(1);
        }
        Collections.shuffle(result);
        return result;
    }

    private int getNextAnimalNumber() {
        if (animalIndex >= animalNumbers.size()) {
            Collections.shuffle(animalNumbers);
            animalIndex = 0;
        }
        return animalNumbers.get(animalIndex++);
    }

    private int getNextGirlNumber() {
        if (girlIndex >= girlNumbers.size()) {
            Collections.shuffle(girlNumbers);
            girlIndex = 0;
        }
        return girlNumbers.get(girlIndex++);
    }

    private int getNextSportNumber() {
        if (sportIndex >= sportNumbers.size()) {
            Collections.shuffle(sportNumbers);
            sportIndex = 0;
        }
        return sportNumbers.get(sportIndex++);
    }

    private int getNextPersonNumber() {
        if (personIndex >= personNumbers.size()) {
            Collections.shuffle(personNumbers);
            personIndex = 0;
        }
        return personNumbers.get(personIndex++);
    }

    private int getNextPokemonNumber() {
        if (pokemonIndex >= pokemonNumbers.size()) {
            Collections.shuffle(pokemonNumbers);
            pokemonIndex = 0;
        }
        return pokemonNumbers.get(pokemonIndex++);
    }
}
