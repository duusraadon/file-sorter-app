const { app, BrowserWindow, ipcMain, dialog } = require('electron');
const { spawn } = require('child_process');
const path = require('path');
const os = require('os');
const fs = require('fs');

require('@electron/remote/main').initialize();

let mainWindow;

function createWindow() {
    mainWindow = new BrowserWindow({
        width: 800,
        height: 600,
        webPreferences: {
            nodeIntegration: true,
            contextIsolation: false,
            enableRemoteModule: true,
            webSecurity: false
        }
    });

    require("@electron/remote/main").enable(mainWindow.webContents);

    mainWindow.loadFile(path.join(__dirname, 'frontend', 'index.html'));

    mainWindow.on('closed', function () {
        mainWindow = null;
    });
}

function getTrashPaths() {
    return [path.join(os.homedir(), '.Trash')];
}

ipcMain.handle('select-folder', async () => {
    const result = await dialog.showOpenDialog(mainWindow, {
        properties: ['openDirectory']
    });
    return result.canceled ? null : result.filePaths[0];
});

ipcMain.handle('start-sorting', async (event, folderPath) => {
    if (!folderPath) return "フォルダが選択されていません。";

    // ゴミ箱判定（フロントから送られてきたパスに.Trashが含まれる場合）
    const targetPath = (folderPath.includes('.Trash')) ? getTrashPaths()[0] : folderPath;
    
    console.log("【デバッグ】Javaに渡すパス:", targetPath);

    const backendDir = path.join(__dirname, 'backend');
    const jarPath = path.join(backendDir, 'FileSorter.jar');
    
    return new Promise((resolve, reject) => {
        const javaProcess = spawn('java', ['-jar', jarPath, targetPath], {
            cwd: backendDir
        });

        let output = '';
        javaProcess.stdout.on('data', (data) => { output += data.toString(); });
        javaProcess.stderr.on('data', (data) => { 
            const errorMsg = data.toString();
            console.error(`Java Error Detail: ${errorMsg}`);
            output += errorMsg;
        });

        javaProcess.on('close', (code) => {
            if (code === 0) {
                resolve(output || "仕分け完了しました。");
            } else {
                reject(`Java実行エラー (コード: ${code})。出力: ${output}`);
            }
        });
    });
});

app.on('ready', createWindow);
app.on('window-all-closed', function () {
    if (process.platform !== 'darwin') app.quit();
});
app.on('activate', function () {
    if (mainWindow === null) createWindow();
});