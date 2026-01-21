# JigsawPuzzles - 拼图游戏

一个基于 Java Swing 的拼图游戏，采用 MVC 架构设计，实现了完整的用户系统、多难度拼图和存档功能。

## 功能特性

### 游戏功能
- **多难度支持**：2x2、3x3、4x4、5x5 四种难度
- **多主题图片**：动物、美女、运动、人物四大主题
- **智能图片加载**：自动扫描图片目录，动态识别可用图片
- **可解性保证**：基于逆序数算法验证拼图可解性
- **双操作模式**：支持键盘方向键和鼠标点击

### 用户系统
- 注册/登录功能
- 验证码验证
- MD5 密码加密
- JSON 数据持久化

### 存档系统
- 保存/读取游戏进度
- 每用户独立存档
- 记录拼图状态、步数、时间戳

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 | 编程语言 |
| Swing | - | GUI 框架 |
| Maven | 3.6+ | 构建工具 |
| Hutool | 5.8.43 | JSON/加密/文件操作 |
| JUnit | 5.10.2 | 单元测试 |

## 项目结构

```
Game/
├── src/main/java/
│   ├── App.java                    # 程序入口
│   ├── controller/
│   │   ├── AuthController.java     # 用户认证
│   │   └── SaveController.java     # 存档管理
│   ├── model/
│   │   ├── User.java               # 用户实体
│   │   └── GameSave.java           # 存档实体
│   ├── ui/
│   │   ├── BaseFrame.java          # 窗口基类
│   │   ├── LoginFrame.java         # 登录界面
│   │   ├── RegisterFrame.java      # 注册界面
│   │   ├── GameFrame.java          # 游戏主界面
│   │   └── showDialog.java         # 对话框组件
│   └── util/
│       ├── ConfigUtil.java         # 配置管理
│       ├── GetCodeUtil.java        # 验证码生成
│       ├── ImageScanner.java       # 图片扫描
│       ├── ImageUtil.java          # 图片处理
│       ├── ResourcePathUtil.java   # 资源路径
│       ├── SplitToolUI.java        # 图片切割工具界面
│       └── splitUtil.java          # 图片切割核心
├── src/main/resources/
│   ├── config.properties           # 模式配置
│   ├── config-dev.properties       # 开发环境配置
│   ├── config-prod.properties      # 生产环境配置
│   └── image/                      # 游戏图片资源
├── src/main/data/
│   ├── userinfo.json               # 用户数据
│   └── save/                       # 存档目录
└── pom.xml
```

## 快速开始

### 环境要求
- JDK 21+
- Maven 3.6+

### 运行方式

**IDE 运行**
```bash
# 克隆项目后，使用 IDE 打开，运行 App.java
```

**命令行运行**
```bash
cd Game
mvn clean package
java -jar target/Game-1.0-SNAPSHOT.jar
```

### 首次使用
1. 启动后进入登录界面
2. 点击"注册"创建账号（用户名 3-16 字符，密码 8-16 位含字母数字）
3. 登录后选择难度和图片主题开始游戏

## 游戏操作

| 操作 | 功能 |
|------|------|
| 方向键 ↑↓←→ | 移动拼图块 |
| 鼠标点击 | 点击相邻块移动 |
| A 键 | 按住显示完整图片 |
| W 键 | VIP 一键通关（仅 Narylr 账号） |

### 菜单功能
- 更换图片（动物/美女/运动/人物）
- 难度选择（2x2 ~ 5x5）
- 保存/读取进度
- 重新开始/重新登录

## 核心算法

### 拼图可解性验证

基于逆序数理论判断拼图是否有解：

```java
private boolean isSolvable() {
    int inversions = 0;
    for (int i = 0; i < arr.length; i++) {
        if (arr[i] == 0) continue;
        for (int j = i + 1; j < arr.length; j++) {
            if (arr[j] == 0) continue;
            if (arr[i] > arr[j]) inversions++;
        }
    }
    int blankRowFromBottom = gridSize - emptyX;

    if (gridSize % 2 == 1) {
        // 奇数网格：逆序数为偶数
        return inversions % 2 == 0;
    } else {
        // 偶数网格：逆序数 + 空白行数为奇数
        return (inversions + blankRowFromBottom) % 2 == 1;
    }
}
```

### Fisher-Yates 洗牌算法

保证打乱的随机性和均匀分布：

```java
Random r = new Random();
for (int i = arr.length - 1; i > 0; i--) {
    int index = r.nextInt(i + 1);
    int t = arr[i];
    arr[i] = arr[index];
    arr[index] = t;
}
```

## 配置说明

### 模式切换

编辑 `config.properties`：

```properties
# dev = 开发模式（相对路径，IDE 调试）
# prod = 生产模式（绝对路径，打包后运行）
mode=dev
```

### 添加自定义图片

1. 准备 420x420 像素的正方形图片
2. 运行 `SplitToolUI` 切割工具
3. 选择源图片，设置输出目录和文件夹名（如 `animal/animal1`）
4. 选择需要的难度，点击切割

切割规格：
- 2x2：4 块，每块 210x210px
- 3x3：9 块，每块 140x140px
- 4x4：16 块，每块 105x105px
- 5x5：25 块，每块 84x84px

图片目录结构：
```
image/
├── animal/
│   ├── animal1/
│   │   ├── 2x2/  (1.jpg ~ 4.jpg, all.jpg)
│   │   ├── 3x3/  (1.jpg ~ 9.jpg, all.jpg)
│   │   ├── 4x4/  (1.jpg ~ 16.jpg, all.jpg)
│   │   └── 5x5/  (1.jpg ~ 25.jpg, all.jpg)
│   └── animal2/
├── girl/
├── sport/
└── person/
```

## 数据存储

### 用户数据 (userinfo.json)
```json
[
  {"username": "user1", "password": "md5_hash"}
]
```

### 存档数据 (save/{username}.json)
```json
{
  "username": "user1",
  "gridSize": 4,
  "puzzleState": [[1,2,3,4], [5,6,7,8], [9,10,11,12], [13,14,15,0]],
  "emptyX": 3,
  "emptyY": 3,
  "stepCount": 152,
  "imagePath": "image/animal/animal",
  "imageNum": 1,
  "saveTime": "2026-01-21 14:30:45"
}
```

## 注意事项

1. **图片资源**：发布版本需确保图片资源正确打包或与 JAR 同目录放置
2. **存档兼容性**：不同难度的存档不通用，切换难度后需重新开始
3. **文件权限**：生产环境需确保数据目录有读写权限

## 学习要点

本项目涵盖以下 Java 知识点：

- Swing GUI 开发（JFrame、JPanel、JLabel、JMenu 等）
- 事件处理（KeyListener、ActionListener、MouseListener）
- MVC 分层架构
- 文件 I/O 与 JSON 读写
- MD5 加密
- 洗牌算法与可解性验证
- Maven 项目管理
- 第三方库使用（Hutool）
- 配置文件与资源路径管理

## 开发者

**Narylr**

---

基于黑马程序员教学案例优化扩展
