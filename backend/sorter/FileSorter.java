package sorter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileSorter {
    private Config config;

    public FileSorter() {
        this.config = new Config();
    }
    
    public void execute(String targetPath) {
        File directory = new File(targetPath);
        
        // ログパスを固定
        String logFilePath = new File(System.getProperty("user.home"), "整理ログ.txt").getAbsolutePath();

        try (PrintWriter out = new PrintWriter(new FileWriter(logFilePath, true))) {
            out.println("--- 処理開始: " + targetPath + " ---");
            
            if (!directory.exists()) {
                out.println("エラー: フォルダが存在しません");
                return;
            }
            
            // listFiles() の結果を詳細に確認する
            File[] files = directory.listFiles();
            if (files == null) {
                out.println("重大エラー: listFiles()がnullを返しました。OSによるアクセス拒否の可能性大。");
            } else if (files.length == 0) {
                out.println("情報: フォルダは空です。");
            } else {
                out.println("情報: " + files.length + " 個のファイルを発見。");
                int movedCount = 0;
                for (File file : files) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        FileRule rule = config.findRule(fileName);
                        if (rule != null) {
                            String destDirPath = rule.getFolderName();
                            Files.createDirectories(Paths.get(destDirPath));
                            Path source = file.toPath();
                            Path target = Paths.get(destDirPath, fileName);
                            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
                            out.println("移動: " + fileName);
                            movedCount++;
                        }
                    }
                }
                out.println("完了: " + movedCount + " 個移動。");
            }
        } catch (IOException e) {
            System.out.println("エラー発生: " + e.getMessage());
        }
    }
}