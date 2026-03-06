package ui;

import controller.GameController;
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

// 游戏主界面：仅负责渲染与事件转发，不承载业务规则。
public class GameFrame extends BaseFrame implements KeyListener, ActionListener {
    private static final String VIP_USERNAME = "Narylr";

    private final String currentUsername;
    private final GameController gameController;

    private ImageIcon[][] imageCache;
    private ImageIcon allImageCache;

    private final JLabel background = new JLabel(new ImageIcon(getResourceUrl("image/background.png")));
    private final JLabel win = new JLabel(new ImageIcon(getResourceUrl("image/win.png")));
    private final JLabel about = new JLabel(new ImageIcon(getResourceUrl("image/about.png")));

    private final JMenu changeImage = new JMenu("更换图片");
    private final JMenuItem girl = new JMenuItem("美女");
    private final JMenuItem animal = new JMenuItem("动物");
    private final JMenuItem sport = new JMenuItem("运动");
    private final JMenuItem person = new JMenuItem("人物");
    private final JMenuItem pokemon = new JMenuItem("pokemon");

    private final JLabel stepLabel = new JLabel("步数: 0");

    private final JMenu funcJMenu = new JMenu("功能");
    private final JMenu aboutJMenu = new JMenu("关于我们");

    private final JMenuItem replayItem = new JMenuItem("重新开始");
    private final JMenuItem reLoginItem = new JMenuItem("重新登录");
    private final JMenuItem closeItem = new JMenuItem("关闭游戏");
    private final JMenuItem accountItem = new JMenuItem("公众号");

    private final JMenu difficultyMenu = new JMenu("难度选择");
    private final JMenuItem diff2x2 = new JMenuItem("2x2 非常简单");
    private final JMenuItem diff3x3 = new JMenuItem("3x3 轻松");
    private final JMenuItem diff4x4 = new JMenuItem("4x4 难");
    private final JMenuItem diff5x5 = new JMenuItem("5x5 非常困难");

    private final JMenuItem saveItem = new JMenuItem("保存进度");
    private final JMenuItem loadItem = new JMenuItem("读取存档");

    public GameFrame() {
        this(null);
    }

    public GameFrame(String username) {
        this.currentUsername = username;
        this.gameController = new GameController();

        initJFrame();
        initJMenuBar();
        loadImageCache();
        initImage();
        setVisible(true);
    }

    private void initJFrame() {
        super.initJFrame(603, 680, "拼图游戏 v1.0");
        addKeyListener(this);
        enableInputMethods(false);
    }

    private URL getResourceUrl(String path) {
        return ResourcePathUtil.getResourceUrl(path);
    }

    private void loadImageCache() {
        // 根据当前主题、图片编号和难度预加载切图资源。
        int gridSize = gameController.getGridSize();
        int total = gridSize * gridSize;

        imageCache = new ImageIcon[gridSize][gridSize];
        ImageIcon defaultImage = ImageUtil.createDefaultPuzzleImage();

        String difficultyPath = gameController.getImagePath() + gameController.getImageNum() + "/" + gridSize + "x" + gridSize + "/";

        for (int num = 0; num < total; num++) {
            URL imageUrl = getResourceUrl(difficultyPath + num + ".jpg");
            imageCache[num / gridSize][num % gridSize] = imageUrl != null ? new ImageIcon(imageUrl) : defaultImage;
        }

        URL allImageUrl = getResourceUrl(difficultyPath + "all.jpg");
        allImageCache = allImageUrl != null ? new ImageIcon(allImageUrl) : defaultImage;
    }

    private void initImage() {
        // 按当前棋盘状态重绘整个拼图区域。
        int gridSize = gameController.getGridSize();
        int pieceSize = gameController.getPieceSize();
        int[][] board = gameController.getBoard();

        int totalSize = gridSize * pieceSize;
        int offsetX = (420 - totalSize) / 2 + 84;
        int offsetY = (420 - totalSize) / 2 + 134;

        getContentPane().removeAll();

        if (gameController.isWin()) {
            initWin();
        }

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                int num = board[i][j];
                JLabel jLabel = new JLabel(imageCache[num / gridSize][num % gridSize]);
                jLabel.setBounds(pieceSize * j + offsetX, pieceSize * i + offsetY, pieceSize, pieceSize);
                jLabel.setBorder(BorderFactory.createLineBorder(Color.black));

                final int row = i;
                final int col = j;
                jLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameController.isWin()) {
                            return;
                        }
                        if (gameController.clickTile(row, col)) {
                            initImage();
                        }
                    }
                });

                getContentPane().add(jLabel);
            }
        }

        initBackground();
        initStepLabel();
        getContentPane().repaint();
    }

    private void initWin() {
        win.setBounds(150, 200, 300, 300);
        getContentPane().add(win);
    }

    private void initBackground() {
        background.setBounds(40, 40, 508, 560);
        getContentPane().add(background);
    }

    private void initStepLabel() {
        stepLabel.setText("步数: " + gameController.getStepCount());
        stepLabel.setBounds(400, 40, 150, 30);
        getContentPane().add(stepLabel);
    }

    private void initJMenuBar() {
        JMenuBar jMenuBar = new JMenuBar();

        changeImage.add(girl);
        changeImage.add(animal);
        changeImage.add(sport);
        changeImage.add(person);
        changeImage.add(pokemon);

        difficultyMenu.add(diff2x2);
        difficultyMenu.add(diff3x3);
        difficultyMenu.add(diff4x4);
        difficultyMenu.add(diff5x5);

        funcJMenu.add(changeImage);
        funcJMenu.add(difficultyMenu);
        funcJMenu.add(saveItem);
        funcJMenu.add(loadItem);
        funcJMenu.add(replayItem);
        funcJMenu.add(reLoginItem);
        funcJMenu.add(closeItem);
        aboutJMenu.add(accountItem);

        jMenuBar.add(funcJMenu);
        jMenuBar.add(aboutJMenu);
        setJMenuBar(jMenuBar);

        girl.addActionListener(this);
        animal.addActionListener(this);
        sport.addActionListener(this);
        person.addActionListener(this);
        pokemon.addActionListener(this);

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

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameController.isWin()) {
            return;
        }

        if (e.getKeyCode() == 65) {
            getContentPane().removeAll();
            JLabel allJLabel = new JLabel(allImageCache);
            allJLabel.setBounds(84, 134, 420, 420);
            add(allJLabel);
            add(background);
            getContentPane().repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameController.isWin()) {
            return;
        }

        int keyCode = e.getKeyCode();
        if (keyCode == 87 && VIP_USERNAME.equals(currentUsername)) {
            gameController.forceSolved();
            initImage();
            return;
        }

        if (keyCode == 65) {
            initImage();
            return;
        }

        if (gameController.moveByKeyCode(keyCode)) {
            initImage();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == diff2x2) {
            gameController.changeDifficulty(2);
            loadImageCache();
            initImage();
        } else if (source == diff3x3) {
            gameController.changeDifficulty(3);
            loadImageCache();
            initImage();
        } else if (source == diff4x4) {
            gameController.changeDifficulty(4);
            loadImageCache();
            initImage();
        } else if (source == diff5x5) {
            gameController.changeDifficulty(5);
            loadImageCache();
            initImage();
        }

        if (source == animal) {
            gameController.changeTheme(GameController.Theme.ANIMAL);
            loadImageCache();
            initImage();
        } else if (source == girl) {
            gameController.changeTheme(GameController.Theme.GIRL);
            loadImageCache();
            initImage();
        } else if (source == sport) {
            gameController.changeTheme(GameController.Theme.SPORT);
            loadImageCache();
            initImage();
        } else if (source == person) {
            gameController.changeTheme(GameController.Theme.PERSON);
            loadImageCache();
            initImage();
        } else if (source == pokemon) {
            gameController.changeTheme(GameController.Theme.POKEMON);
            loadImageCache();
            initImage();
        }

        if (source == replayItem) {
            gameController.restart();
            initImage();
        } else if (source == reLoginItem) {
            dispose();
            new LoginFrame();
        } else if (source == closeItem) {
            System.exit(0);
        } else if (source == accountItem) {
            new showDialog("公众号", about);
        } else if (source == saveItem) {
            saveGame();
        } else if (source == loadItem) {
            loadGame();
        }
    }

    private void saveGame() {
        if (currentUsername == null || currentUsername.isEmpty()) {
            new showDialog("未登录，无法保存");
            return;
        }

        if (gameController.saveCurrentGame(currentUsername)) {
            new showDialog("保存成功！");
        } else {
            new showDialog("保存失败");
        }
    }

    private void loadGame() {
        if (currentUsername == null || currentUsername.isEmpty()) {
            new showDialog("未登录，无法读取");
            return;
        }

        if (!gameController.loadGame(currentUsername)) {
            new showDialog("没有找到存档。");
            return;
        }

        loadImageCache();
        initImage();
        String loadTime = gameController.getLastLoadTime();
        if (loadTime == null || loadTime.isEmpty()) {
            new showDialog("读取成功！");
        } else {
            new showDialog("读取成功！\n存档时间: " + loadTime);
        }
    }
}
