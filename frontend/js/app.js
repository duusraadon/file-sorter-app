const { ipcRenderer } = require('electron');

document.getElementById('folder-path').addEventListener('change', async (e) => {
    if (e.target.value === 'SELECT_MANUALLY') {
        const statusArea = document.getElementById('status-area');
        statusArea.innerText = 'フォルダを選択中...';
        
        const chosenPath = await ipcRenderer.invoke('select-folder');

        if (!chosenPath) {
            e.target.value = "";
            statusArea.innerText = 'スタンバイ状態...';
            return;
        }

        const selectEl = document.getElementById('folder-path');
        Array.from(selectEl.options).forEach(opt => {
            if (opt.value === chosenPath) opt.remove();
        });

        const newOption = new Option(`選択したフォルダ: ${chosenPath}`, chosenPath);
        selectEl.add(newOption, selectEl.options[1]);
        selectEl.value = chosenPath;
        statusArea.innerText = 'フォルダが選択された。仕分けを実行可能。';
    }
});

document.getElementById('start-btn').addEventListener('click', async () => {
    const targetPath = document.getElementById('folder-path').value;
    const statusArea = document.getElementById('status-area');
    
    if (!targetPath || targetPath === 'SELECT_MANUALLY') {
        statusArea.innerText = 'エラー: フォルダを選択すること。';
        return;
    }
    
    statusArea.innerText = '仕分け処理を実行中...';
    
    try {
        const response = await fetch('http://localhost:8888/FileSorterApp/api.php', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({ 'target_path': targetPath })
        });

        const data = await response.json();
        
        if (data.status === 'success') {
            statusArea.innerText = data.message;
        } else {
            statusArea.innerText = `エラー: ${data.message}`;
        }
    } catch (err) {
        statusArea.innerText = `システムエラー: ${err}`;
    }
});