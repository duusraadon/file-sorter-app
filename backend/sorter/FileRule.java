package sorter;

public class FileRule {
    private String extension;
    private String folderName;

    // コンストラクタ。拡張子はすべて小文字に統一して保持
    public FileRule(String extension, String folderName) {
        this.extension = extension.toLowerCase();
        this.folderName = folderName;
    }

    // 対象ファイル名が、定義された拡張子と一致するかを判定
    public boolean matches(String fileName) {
        // 拡張子にドットを付けて小文字で比較することで、「.cab」や「.CAB」も拾えるようにする
        String extensionWithDot = "." + this.extension.toLowerCase();
        return fileName.toLowerCase().endsWith(extensionWithDot);
    }

    public String getFolderName() {
        return this.folderName;
    }
}
