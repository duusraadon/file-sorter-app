package sorter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {
    public static void main(String[] args) {
        try {
            // 引数がない場合、デスクトップの「TestFolder」をデフォルトにする
            String targetPath;
            if (args.length == 0) {
                targetPath = System.getProperty("user.home") + "/Desktop/TestFolder";
            } else {
                targetPath = args[0];
            }
            
            File targetDir = new File(targetPath);
            if (!targetDir.exists()) {
                throw new Exception("対象フォルダが見つかりません: " + targetPath);
            }
            
            FileSorter sorter = new FileSorter();
            sorter.execute(targetPath);
            
        } catch (Exception e) {
            String logPath = System.getProperty("user.home") + "/Desktop/error_log.txt";
            try (FileWriter fw = new FileWriter(logPath, true);
                 PrintWriter pw = new PrintWriter(fw)) {
                e.printStackTrace(pw);
            } catch (IOException ioEx) {
                ioEx.printStackTrace();
            }
        }
    }
}