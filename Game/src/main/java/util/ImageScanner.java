package util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// 图片扫描工具类
// 用于扫描指定目录下的图片文件夹，自动生成图片编号序列
public class ImageScanner {

    // 扫描指定主题目录下的所有图片文件夹
    // themePath: 主题路径（如 "image/animal"）
    public static List<Integer> scanImageNumbers(String themePath) {
        List<Integer> numbers = new ArrayList<>();
        String imageDir = ConfigUtil.getImageDir();

        // 构建完整的主题目录路径
        File themeDir = new File(imageDir, themePath.replace("image/", ""));

        if (!themeDir.exists() || !themeDir.isDirectory()) {
            System.err.println("[图片扫描] 主题目录不存在: " + themeDir.getAbsolutePath());
            return numbers;
        }

        // 遍历主题目录下的所有子文件夹
        File[] imageFolders = themeDir.listFiles(File::isDirectory);

        if (imageFolders == null || imageFolders.length == 0) {
            System.err.println("[图片扫描] 未找到图片文件夹: " + themeDir.getAbsolutePath());
            return numbers;
        }

        // 提取图片编号
        for (File folder : imageFolders) {
            try {
                String folderName = folder.getName();
                // 提取数字部分（如 "animal1" -> 1）
                String numStr = folderName.replaceAll("[^0-9]", "");
                if (!numStr.isEmpty()) {
                    int num = Integer.parseInt(numStr);
                    numbers.add(num);
                }
            } catch (NumberFormatException e) {
                System.err.println("[图片扫描] 无法解析文件夹名称: " + folder.getName());
            }
        }

        return numbers;
    }

    // 扫描所有主题的图片编号
    public static ImageNumbers scanAllThemes() {
        return new ImageNumbers(
                scanImageNumbers("image/animal"),
                scanImageNumbers("image/girl"),
                scanImageNumbers("image/sport"),
                scanImageNumbers("image/person")
        );
    }

    // 图片编号容器类
    public static class ImageNumbers {
        private final List<Integer> animalNumbers;
        private final List<Integer> girlNumbers;
        private final List<Integer> sportNumbers;
        private final List<Integer> personNumbers;

        public ImageNumbers(List<Integer> animalNumbers, List<Integer> girlNumbers,
                            List<Integer> sportNumbers, List<Integer> personNumbers) {
            this.animalNumbers = animalNumbers;
            this.girlNumbers = girlNumbers;
            this.sportNumbers = sportNumbers;
            this.personNumbers = personNumbers;
        }

        public List<Integer> getAnimalNumbers() {
            return animalNumbers;
        }

        public List<Integer> getGirlNumbers() {
            return girlNumbers;
        }

        public List<Integer> getSportNumbers() {
            return sportNumbers;
        }

        public List<Integer> getPersonNumbers() {
            return personNumbers;
        }
    }
}
