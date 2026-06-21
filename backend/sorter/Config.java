package sorter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
    private List<FileRule> rules = new ArrayList<>();

    public Config() {
        loadRulesFromCsv();
    }
    
    private void loadRulesFromCsv() {
        String userHome = System.getProperty("user.home");
        
        // フォルダパスの定義
        Map<String, String> paths = new HashMap<>();
        paths.put("DOCS", userHome + "/Documents");
        paths.put("PICS", userHome + "/Pictures");
        paths.put("MUSIC", userHome + "/Music");
        paths.put("TRASH", userHome + "/.Trash");
        paths.put("AUP_DIR", userHome + "/Documents/AudacityProjects");

        // 【修正】絶対パスで指定して迷子を防ぐ
        String csvPath = userHome + "/Desktop/FileSorterApp/backend/config.csv";
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String ext = parts[0].trim();
                    String key = parts[1].trim();
                    String path = paths.getOrDefault(key, key);
                    rules.add(new FileRule(ext, path));
                }
            }
        } catch (IOException e) {
            System.err.println("設定ファイルが見つかりません: " + csvPath);
        }
    }

    public FileRule findRule(String fileName) {
        for (FileRule rule : rules) {
            if (rule.matches(fileName)) return rule;
        }
        return null;
    }
}