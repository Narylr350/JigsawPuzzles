package ui;

import controller.SaveController;
import model.GameSave;
import util.ImageScanner;
import util.ImageUtil;
import util.ResourcePathUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// 游戏主界面
public class GameFrame extends BaseFrame implements KeyListener, ActionListener {
    // 难度配置：网格大小 -> 块尺寸
    private static final int[][] DIFFICULTY_CONFIG = {
        {2, 210},  // 2x2 非常简单
        {3, 140},  // 3x3 轻松
        {4, 105},  // 4x4 难
        {5, 84}    // 5x5 非常困难
    };

    // 当前难度
    private int gridSize = 4;      // 网格大小
    private int pieceSize = 105;   // 拼图块尺寸

    // VIP用户名
    private String currentUsername;
    private static final String VIP_USERNAME = "Narylr";

    // 拼图数据（动态数组）
    private int[] arr;
    private int[][] newArr;
    private int[][] finalArr;

    // 空白位置
    private int emptyX = 0;
    private int emptyY = 0;

    // 图片缓存（动态大小）
    private ImageIcon[][] imageCache;
    private ImageIcon allImageCache = null;

    // 步数
    int stepCount = 0;

    // 图片路径
    String pathAnimal = "image/animal/animal";
    String pathGirl = "image/girl/girl";
    String pathSport = "image/sport/sport";
    String pathPerson = "image/person/person";
    String path = pathAnimal;

    // 图片组件
    JLabel background = new JLabel(new ImageIcon(getResourceUrl("image/background.png")));
    JLabel win = new JLabel(new ImageIcon(getResourceUrl("image/win.png")));
    JLabel about = new JLabel(new ImageIcon(getResourceUrl("image/about.png")));

    // 更换图片菜单
    JMenu changeImage = new JMenu("更换图片");

    // 图片选项
    JMenuItem girl = new JMenuItem("美女");
    JMenuItem animal = new JMenuItem("动物");
    JMenuItem sport = new JMenuItem("运动");
    JMenuItem person = new JMenuItem("人");
    // 步数标签
    JLabel stepLabel = new JLabel("步数: 0");

    // 菜单选项
    JMenu funcJMenu = new JMenu("功能");
    JMenu aboutJMenu = new JMenu("关于我们");

    // 菜单条目
    JMenuItem replayItem = new JMenuItem("重新开始");
    JMenuItem reLoginItem = new JMenuItem("重新登录");
    JMenuItem closeItem = new JMenuItem("关闭游戏");
    JMenuItem accountItem = new JMenuItem("公众号");

    // 难度菜单
    JMenu difficultyMenu = new JMenu("难度选择");
    JMenuItem diff2x2 = new JMenuItem("2x2 非常简单");
    JMenuItem diff3x3 = new JMenuItem("3x3 轻松");
    JMenuItem diff4x4 = new JMenuItem("4x4 难");
    JMenuItem diff5x5 = new JMenuItem("5x5 非常困难");

    // 存档读档菜单
    JMenuItem saveItem = new JMenuItem("保存进度");
    JMenuItem loadItem = new JMenuItem("读取存档");

    // 随机图片编号
    int randomNum = 1;

    // 随机图片序列
    private List<Integer> animalNumbers;
    private List<Integer> girlNumbers;
    private List<Integer> sportNumbers;
    private List<Integer> personNumbers;
    private int personIndex = 0;
    private int animalIndex = 0;
    private int girlIndex = 0;
    private int sportIndex = 0;

    // 默认构造方法
    public GameFrame() {
        this(null);
    }

    // 带用户名的构造方法
    public GameFrame(String username) {
        this.currentUsername = username;
        initArrays();
        initRandomImageNumbers();
        initJFrame();
        initJMenuBar();
        initData();
        loadImageCache();
        initImage();
        setVisible(true);
    }

    // 初始化动态数组
    private void initArrays() {
        int total = gridSize * gridSize;
        arr = new int[total];
        newArr = new int[gridSize][gridSize];
        finalArr = new int[gridSize][gridSize];
        imageCache = new ImageIcon[gridSize][gridSize];
        // 初始化目标状态
        int num = 1;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (i == gridSize - 1 && j == gridSize - 1) {
                    finalArr[i][j] = 0;
                } else {
                    finalArr[i][j] = num++;
                }
            }
        }
    }

    // 初始化窗口
    private void initJFrame() {
        super.initJFrame(603, 680, "拼图游戏 v1.0");
        addKeyListener(this);
        enableInputMethods(false);
    }

    // 获取资源URL
    private URL getResourceUrl(String path) {
        return ResourcePathUtil.getResourceUrl(path);
    }

    // 初始化随机图片序列
    private void initRandomImageNumbers() {
        // 使用ImageScanner自动扫描图片编号
        ImageScanner.ImageNumbers imageNumbers = ImageScanner.scanAllThemes();
        
        // 动物图片
        animalNumbers = new ArrayList<>(imageNumbers.getAnimalNumbers());
        if (animalNumbers.isEmpty()) {
            animalNumbers.add(1);
        }
        Collections.shuffle(animalNumbers);

        // 美女图片
        girlNumbers = new ArrayList<>(imageNumbers.getGirlNumbers());
        if (girlNumbers.isEmpty()) {
            girlNumbers.add(1);
        }
        Collections.shuffle(girlNumbers);

        // 运动图片
        sportNumbers = new ArrayList<>(imageNumbers.getSportNumbers());
        if (sportNumbers.isEmpty()) {
            sportNumbers.add(1);
        }
        Collections.shuffle(sportNumbers);

        // 人物图片
        personNumbers = new ArrayList<>(imageNumbers.getPersonNumbers());
        if (personNumbers.isEmpty()) {
            personNumbers.add(1);
        }
        Collections.shuffle(personNumbers);
    }

    // 获取下一个人物图片编号
    private int getNextPersonNumber() {
        if (personIndex >= personNumbers.size()) {
            Collections.shuffle(personNumbers);
            personIndex = 0;
        }
        return personNumbers.get(personIndex++);
    }

    // 获取下一个动物图片编号
    private int getNextAnimalNumber() {
        if (animalIndex >= animalNumbers.size()) {
            Collections.shuffle(animalNumbers);
            animalIndex = 0;
        }
        return animalNumbers.get(animalIndex++);
    }

    // 获取下一个美女图片编号
    private int getNextGirlNumber() {
        if (girlIndex >= girlNumbers.size()) {
            Collections.shuffle(girlNumbers);
            girlIndex = 0;
        }
        return girlNumbers.get(girlIndex++);
    }

    // 获取下一个运动图片编号
    private int getNextSportNumber() {
        if (sportIndex >= sportNumbers.size()) {
            Collections.shuffle(sportNumbers);
            sportIndex = 0;
        }
        return sportNumbers.get(sportIndex++);
    }

    // 初始化拼图数据
    private void initData() {
        do {
            // 重置数组
            for (int i = 0; i < arr.length; i++) {
                arr[i] = i;
            }
            // Fisher-Yates洗牌
            Random r = new Random();
            for (int i = arr.length - 1; i > 0; i--) {
                int index = r.nextInt(i + 1);
                int t = arr[i];
                arr[i] = arr[index];
                arr[index] = t;
            }
            // 转换为二维数组
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == 0) {
                    emptyX = i / gridSize;
                    emptyY = i % gridSize;
                }
                newArr[i / gridSize][i % gridSize] = arr[i];
            }
        } while (!isSolvable());
    }

    // 检查拼图是否可解
    private boolean isSolvable() {
        int inversions = 0;
        // 计算逆序数
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 0) {
                continue;
            }
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] == 0) {
                    continue;
                }
                if (arr[i] > arr[j]) {
                    inversions++;
                }
            }
        }
        // 空白块所在行
        int blankRowFromBottom = gridSize - emptyX;
        // 可解条件
        if (gridSize % 2 == 1) {
            // 奇数网格：逆序数必须为偶数
            return inversions % 2 == 0;
        } else {
            // 偶数网格：逆序数+空白块从底部数的行数必须为奇数
            return (inversions + blankRowFromBottom) % 2 == 1;
        }
    }

    // 加载并缓存图片
    private void loadImageCache() {
        // 创建默认白色图片
        ImageIcon defaultImage = ImageUtil.createDefaultPuzzleImage();
        int total = gridSize * gridSize;
        // 图片路径格式：image/animal/animal1/4x4/
        String difficultyPath = path + randomNum + "/" + gridSize + "x" + gridSize + "/";

        for (int num = 0; num < total; num++) {
            URL imageUrl = getResourceUrl(difficultyPath + num + ".jpg");
            if (imageUrl != null) {
                imageCache[num / gridSize][num % gridSize] = new ImageIcon(imageUrl);
            } else {
                imageCache[num / gridSize][num % gridSize] = defaultImage;
            }
        }

        URL allImageUrl = getResourceUrl(difficultyPath + "all.jpg");
        if (allImageUrl != null) {
            allImageCache = new ImageIcon(allImageUrl);
        } else {
            allImageCache = defaultImage;
        }
    }

    // 初始化拼图界面
    private void initImage() {
        // 计算居中偏移
        int totalSize = gridSize * pieceSize;
        int offsetX = (420 - totalSize) / 2 + 84;
        int offsetY = (420 - totalSize) / 2 + 134;
        
        // 清空界面
        getContentPane().removeAll();
        if (win()) {
            initWin();
        }
        // 添加拼图块
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                int num = newArr[i][j];
                JLabel jLabel = new JLabel(imageCache[num / gridSize][num % gridSize]);
                jLabel.setBounds(pieceSize * j + offsetX, pieceSize * i + offsetY, pieceSize, pieceSize);
                jLabel.setBorder(BorderFactory.createLineBorder(Color.black));

                // 点击事件
                final int row = i;
                final int col = j;
                jLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (win()) {
                            return;
                        }
                        clickPuzzle(row, col);
                    }
                });

                getContentPane().add(jLabel);
            }
        }
        // 背景
        initBackground();
        // 步数
        initStepLabel();
        // 刷新
        getContentPane().repaint();
    }

    // 点击拼图块与空白块交换
    private void clickPuzzle(int row, int col) {
        // 判断是否相邻
        if ((Math.abs(row - emptyX) == 1 && col == emptyY) || (Math.abs(col - emptyY) == 1 && row == emptyX)) {
            // 交换
            newArr[emptyX][emptyY] = newArr[row][col];
            newArr[row][col] = 0;
            emptyX = row;
            emptyY = col;
            stepCount++;
            initImage();
        }
    }

    // 显示胜利图片
    private void initWin() {
        win.setBounds(150, 200, 300, 300);
        getContentPane().add(win);
    }

    // 初始化背景
    private void initBackground() {
        background.setBounds(40, 40, 508, 560);
        getContentPane().add(background);
    }

    // 初始化步数标签
    private void initStepLabel() {
        stepLabel.setText("步数: " + stepCount);
        stepLabel.setBounds(400, 40, 150, 30);
        getContentPane().add(stepLabel);
    }

    // 初始化菜单栏
    private void initJMenuBar() {
        JMenuBar jMenuBar = new JMenuBar();
        // 添加图片选项
        changeImage.add(girl);
        changeImage.add(animal);
        changeImage.add(sport);
        changeImage.add(person);

        // 添加难度选项
        difficultyMenu.add(diff2x2);
        difficultyMenu.add(diff3x3);
        difficultyMenu.add(diff4x4);
        difficultyMenu.add(diff5x5);

        // 添加菜单条目
        funcJMenu.add(changeImage);
        funcJMenu.add(difficultyMenu);
        funcJMenu.add(saveItem);
        funcJMenu.add(loadItem);
        funcJMenu.add(replayItem);
        funcJMenu.add(reLoginItem);
        funcJMenu.add(closeItem);
        aboutJMenu.add(accountItem);

        // 添加菜单
        jMenuBar.add(funcJMenu);
        jMenuBar.add(aboutJMenu);

        setJMenuBar(jMenuBar);
        // 添加监听器
        girl.addActionListener(this);
        animal.addActionListener(this);
        sport.addActionListener(this);
        person.addActionListener(this);

        diff2x2.addActionListener(this);
        diff3x3.addActionListener(this);
        diff4x4.addActionListener(this);
        diff5x5.addActionListener(this);

        saveItem.addActionListener(this);
        loadItem.addActionListener(this);

        replayItem.addActionListener(this);
        reLoginItem.addActionListener(this);
        closeItem.addActionListener(this);
        accountItem.addActionListener(this);
    }

    // 检查是否胜利
    private boolean win() {
        for (int i = 0; i < newArr.length; i++) {
            for (int j = 0; j < newArr[i].length; j++) {
                if (newArr[i][j] != finalArr[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    // 键盘输入事件
    @Override
    public void keyTyped(KeyEvent e) {

    }

    // 键盘按下事件
    @Override
    public void keyPressed(KeyEvent e) {
        if (win()) {
            return;
        }

        int keyCode = e.getKeyCode();
        // A键显示完整图片
        if (keyCode == 65) {
            getContentPane().removeAll();
            JLabel allJLabel = new JLabel(allImageCache);
            allJLabel.setBounds(84, 134, 420, 420);
            add(allJLabel);
            add(background);
            getContentPane().repaint();
        }
    }

    // 键盘释放事件
    @Override
    public void keyReleased(KeyEvent e) {
        if (win()) {
            return;
        }

        // 方向键控制
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case 37 -> {
                // 左
                if (emptyY < gridSize - 1) {
                    newArr[emptyX][emptyY] = newArr[emptyX][emptyY + 1];
                    newArr[emptyX][emptyY + 1] = 0;
                    emptyY++;
                    stepCount++;
                    initImage();
                }
            }
            case 38 -> {
                // 上
                if (emptyX < gridSize - 1) {
                    newArr[emptyX][emptyY] = newArr[emptyX + 1][emptyY];
                    newArr[emptyX + 1][emptyY] = 0;
                    emptyX++;
                    stepCount++;
                    initImage();
                }
            }
            case 39 -> {
                // 右
                if (emptyY > 0) {
                    newArr[emptyX][emptyY] = newArr[emptyX][emptyY - 1];
                    newArr[emptyX][emptyY - 1] = 0;
                    emptyY--;
                    stepCount++;
                    initImage();
                }
            }
            case 40 -> {
                // 下
                if (emptyX > 0) {
                    newArr[emptyX][emptyY] = newArr[emptyX - 1][emptyY];
                    newArr[emptyX - 1][emptyY] = 0;
                    emptyX--;
                    stepCount++;
                    initImage();
                }
            }
            // A键恢复拼图
            case 65 -> initImage();
            case 87 -> {
                // W键一键通关(VIP专属)
                if (VIP_USERNAME.equals(currentUsername)) {
                    for (int i = 0; i < newArr.length; i++) {
                        System.arraycopy(finalArr[i], 0, newArr[i], 0, newArr[i].length);
                    }
                    emptyX = gridSize - 1;
                    emptyY = gridSize - 1;
                    initImage();
                }
            }
        }
    }

    // 菜单点击事件
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        // 难度切换
        if (source == diff2x2) {
            changeDifficulty(2, 210);
        } else if (source == diff3x3) {
            changeDifficulty(3, 140);
        } else if (source == diff4x4) {
            changeDifficulty(4, 105);
        } else if (source == diff5x5) {
            changeDifficulty(5, 84);
        }

        // 更换图片
        if (source == animal) {
            randomNum = getNextAnimalNumber();
            stepCount = 0;
            path = pathAnimal;
            initData();
            loadImageCache();
            initImage();
        } else if (source == girl) {
            randomNum = getNextGirlNumber();
            stepCount = 0;
            path = pathGirl;
            initData();
            loadImageCache();
            initImage();
        } else if (source == sport) {
            randomNum = getNextSportNumber();
            stepCount = 0;
            path = pathSport;
            initData();
            loadImageCache();
            initImage();
        } else if (source == person) {
            randomNum = getNextPersonNumber();
            stepCount = 0;
            path = pathPerson;
            initData();
            loadImageCache();
            initImage();
        }

        // 功能菜单
        if (source == replayItem) {
            // 重新开始
            stepCount = 0;
            initData();
            initImage();
        } else if (source == reLoginItem) {
            // 重新登录
            dispose();
            new LoginFrame();

        } else if (source == closeItem) {
            // 关闭游戏
            System.exit(0);

        } else if (source == accountItem) {
            // 关于我们
            new showDialog("公众号", about);
        } else if (source == saveItem) {
            // 保存进度
            saveGame();
        } else if (source == loadItem) {
            // 读取存档
            loadGame();
        }

    }

    // 保存游戏
    private void saveGame() {
        if (currentUsername == null || currentUsername.isEmpty()) {
            new showDialog("未登录，无法保存");
            return;
        }
        boolean success = SaveController.saveGame(currentUsername, gridSize, newArr, 
                                                   emptyX, emptyY, stepCount, path, randomNum);
        if (success) {
            new showDialog("保存成功！");
        } else {
            new showDialog("保存失败");
        }
    }

    // 读取存档
    private void loadGame() {
        if (currentUsername == null || currentUsername.isEmpty()) {
            new showDialog("未登录，无法读取");
            return;
        }
        GameSave save = SaveController.loadGame(currentUsername);
        if (save == null) {
            new showDialog("没有找到存档");
            return;
        }
        // 恢复游戏状态
        gridSize = save.getGridSize();
        pieceSize = getPieceSize(gridSize);
        initArrays();
        // 恢复拼图状态
        int[][] savedState = save.getPuzzleState();
        for (int i = 0; i < gridSize; i++) {
            System.arraycopy(savedState[i], 0, newArr[i], 0, gridSize);
        }
        emptyX = save.getEmptyX();
        emptyY = save.getEmptyY();
        stepCount = save.getStepCount();
        path = save.getImagePath();
        randomNum = save.getImageNum();
        // 重新加载图片并刷新界面
        loadImageCache();
        initImage();
        new showDialog("读取成功！\n存档时间: " + save.getSaveTime());
    }

    // 根据网格大小获取块尺寸
    private int getPieceSize(int size) {
        return switch (size) {
            case 2 -> 210;
            case 3 -> 140;
            case 5 -> 84;
            default -> 105;
        };
    }

    // 切换难度
    private void changeDifficulty(int newGridSize, int newPieceSize) {
        if (gridSize != newGridSize) {
            gridSize = newGridSize;
            pieceSize = newPieceSize;
            initArrays();
            stepCount = 0;
            initData();
            loadImageCache();
            initImage();
        }
    }
}