# 拼图游戏 (JigsawPuzzles)

## 📖 项目简介

这是一个高完成度的Java拼图小游戏项目，源自黑马程序员的教学案例，经过深度优化和功能扩展。项目采用MVC分层架构，实现了完整的用户系统、多难度拼图、存档读档等功能，是一个适合Java初学者学习GUI开发和项目实践的练手作品。

### 项目特色
- 🎯 **完整的项目架构**：采用MVC设计模式，代码结构清晰，易于维护和扩展
- 🎨 **精美的游戏界面**：使用Swing框架构建，界面美观，交互流畅
- 🔒 **安全的数据存储**：用户密码采用MD5加密，数据使用JSON格式持久化
- 📦 **灵活的配置管理**：支持开发/生产模式切换，便于调试和部署
- 🛠️ **便捷的工具支持**：内置图片切割工具，轻松添加自定义拼图图片

## ✨ 核心特性

### 🎮 游戏功能
- **多难度支持**：2x2、3x3、4x4、5x5 四种难度级别
- **多主题图片**：动物、美女、运动、人物四大主题，每个主题包含多张图片随机切换
- **智能图片加载**：自动扫描图片目录，根据实际图片数量动态生成图片序列，无需手动维护
- **智能打乱算法**：采用Fisher-Yates洗牌算法，并验证拼图可解性（逆序数检测）
- **操作方式**：支持键盘方向键和鼠标点击两种操作方式
- **按键提示**：按住A键可查看完整图片

### 👤 用户系统
- **登录注册**：完整的用户注册和登录功能
- **验证码验证**：登录时需输入验证码防止恶意操作
- **密码加密**：使用MD5加密存储用户密码
- **用户数据持久化**：基于JSON格式存储用户信息

### 💾 存档系统
- **游戏存档**：支持保存当前游戏进度（拼图状态、步数、难度等）
- **存档读取**：可从存档恢复游戏状态继续游玩
- **用户独立存档**：每个用户拥有独立的存档文件

### 🎯 特权功能
- **VIP一键通关**：特定账号（Narylr）可使用W键直接完成拼图，用于调试和演示

## 🛠 技术栈

| 技术 | 说明 |
|------|------|
| **语言** | Java 21 |
| **GUI框架** | Swing |
| **构建工具** | Maven |
| **工具库** | Hutool 5.8.43 (JSON处理、加密、文件操作) |
| **测试框架** | JUnit 5.10.2 |
| **打包方式** | JAR / EXE |

## 📁 项目结构

```
Game/
├── src/main/
│   ├── java/
│   │   ├── controller/           # 控制器层
│   │   │   ├── AuthController.java      # 用户认证控制器（登录/注册）
│   │   │   └── SaveController.java      # 存档控制器（保存/读取）
│   │   ├── model/                # 数据模型层
│   │   │   ├── User.java                # 用户实体类
│   │   │   └── GameSave.java            # 游戏存档实体类
│   │   ├── ui/                   # 界面层
│   │   │   ├── BaseFrame.java           # 窗口基类
│   │   │   ├── LoginFrame.java          # 登录界面
│   │   │   ├── RegisterFrame.java       # 注册界面
│   │   │   ├── GameFrame.java           # 游戏主界面
│   │   │   └── showDialog.java          # 对话框组件
│   │   ├── util/                 # 工具类层
│   │   │   ├── ConfigUtil.java          # 配置文件读取工具
│   │   │   ├── GetCodeUtil.java         # 验证码生成工具
│   │   │   ├── ImageUtil.java           # 图片处理工具
│   │   │   ├── ImageScanner.java        # 图片扫描工具（自动识别图片数量）
│   │   │   ├── ResourcePathUtil.java    # 资源路径工具
│   │   │   ├── SplitToolUI.java         # 图片切割工具界面
│   │   │   └── splitUtil.java           # 图片切割核心逻辑
│   │   └── App.java              # 程序入口
│   ├── resources/                # 资源文件
│   │   ├── config.properties            # 主配置（模式切换）
│   │   ├── config-dev.properties        # 开发环境配置
│   │   ├── config-prod.properties       # 生产环境配置
│   │   └── image/                       # 游戏图片资源
│   └── data/                     # 数据文件
│       ├── userinfo.json                # 用户数据
│       └── save/                        # 用户存档目录
└── pom.xml                       # Maven配置文件
```

## 🎯 核心算法

### 1. 拼图可解性验证
拼图游戏并非所有随机排列都能解开，项目实现了基于逆序数的可解性检测：

- **奇数网格**（如3x3、5x5）：逆序数必须为偶数
- **偶数网格**（如2x2、4x4）：逆序数 + 空白块从底部数的行数必须为奇数

```java
private boolean isSolvable() {
    int inversions = 0;
    // 计算逆序数
    for (int i = 0; i < arr.length; i++) {
        if (arr[i] == 0) continue;
        for (int j = i + 1; j < arr.length; j++) {
            if (arr[j] == 0) continue;
            if (arr[i] > arr[j]) inversions++;
        }
    }
    int blankRowFromBottom = gridSize - emptyX;
    
    if (gridSize % 2 == 1) {
        return inversions % 2 == 0;
    } else {
        return (inversions + blankRowFromBottom) % 2 == 1;
    }
}
```

### 2. Fisher-Yates洗牌算法
保证拼图打乱的随机性和均匀分布：

```java
Random r = new Random();
for (int i = arr.length - 1; i > 0; i--) {
    int index = r.nextInt(i + 1);
    int t = arr[i];
    arr[i] = arr[index];
    arr[index] = t;
}
```

## 🎮 游戏操作

| 按键/操作 | 功能 |
|----------|------|
| **方向键** ↑↓←→ | 移动拼图块 |
| **鼠标点击** | 点击拼图块进行移动（需与空白块相邻） |
| **A键** | 按住显示完整图片，松开恢复拼图 |
| **W键** | VIP专属：一键通关（需Narylr账号） |

### 菜单功能
- **功能菜单**
    - 更换图片（动物/美女/运动/人物）
    - 难度选择（2x2 ~ 5x5）
    - 保存进度
    - 读取存档
    - 重新开始
    - 重新登录
    - 关闭游戏
- **关于我们**：显示公众号信息

## 🚀 快速开始

### 环境要求
- JDK 21 或更高版本
- Maven 3.6+

### 运行项目

#### 方式1：IDE运行
```bash
# 1. 克隆项目
git clone <repository-url>

# 2. 使用IDE（如IntelliJ IDEA）打开项目

# 3. 运行 App.java 主类
```

#### 方式2：Maven命令
```bash
# 1. 编译打包
cd Game
mvn clean package

# 2. 运行JAR文件
java -jar target/Game-1.0-SNAPSHOT.jar
```

### 打包为可执行文件
项目支持打包为JAR和EXE格式：

```bash
# 打包JAR（已配置maven-shade-plugin）
mvn clean package

# 打包EXE（需要额外工具，如launch4j或jpackage）
```

## 📝 配置说明

### 环境模式切换
编辑 `config.properties` 切换开发/生产模式：

```properties
# dev = 开发模式（使用相对路径）
# prod = 发布模式（使用绝对路径）
mode=dev
```

### 配置文件说明
- **config-dev.properties**：开发环境配置（相对路径，用于IDE调试）
- **config-prod.properties**：生产环境配置（绝对路径，用于打包后使用）

## 🎨 添加自定义图片

### 智能图片加载
系统会自动扫描 `image` 目录下的所有图片文件夹，根据实际文件数量动态生成图片序列。无需手动修改代码中的图片编号。

### 图片文件夹命名规则
- 格式：`主题类别 + 数字`（如 `animal1`、`girl5`、`sport3`、`person2`）
- 系统会自动从文件夹名称中提取数字作为图片编号
- 支持任意数量的图片，系统会自动识别

### 图片准备
1. 准备420x420像素的正方形图片
2. 使用内置的图片切割工具（`SplitToolUI`）生成拼图素材
3. 切割后会生成对应难度的文件夹（如 `2x2/`, `3x3/`, `4x4/`, `5x5/`）
4. 将文件夹重命名为主题类别+数字格式（例如：`animal1`、`girl5`）

### 切割规格
- **2x2**：4块，每块 210x210px
- **3x3**：9块，每块 140x140px
- **4x4**：16块，每块 105x105px
- **5x5**：25块，每块 84x84px

## 🔐 特权账号

项目内置特权账号用于调试：

| 账号 | 密码 | 权限 |
|------|------|------|
| Narylr | (自行注册) | W键一键通关 |

## 🐛 已知问题与注意事项

1. **图片资源路径**：发布版本需确保图片资源正确打包到JAR中
2. **存档兼容性**：不同难度之间的存档不通用
3. **文件权限**：生产环境需确保data目录有读写权限

## 📚 学习要点

本项目涵盖以下Java知识点：

- ✅ Swing GUI开发（JFrame、JPanel、JLabel、JMenu等）
- ✅ 事件处理（KeyListener、ActionListener、MouseListener）
- ✅ 面向对象设计（MVC分层架构）
- ✅ 文件I/O操作（JSON读写）
- ✅ 加密算法应用（MD5密码加密）
- ✅ 算法实现（洗牌算法、可解性验证）
- ✅ Maven项目管理
- ✅ 第三方库使用（Hutool工具库）
- ✅ 资源路径管理
- ✅ 配置文件处理

## 👨‍💻 开发者

**Narylr**

## 📄 许可证

本项目仅供学习交流使用。

---

**项目版本**：v1.0  
**最后更新**：2026-01
