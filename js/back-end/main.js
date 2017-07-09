// Electron Initialization
const electron = require('electron');
const app = electron.app;
const Menu = electron.Menu;
const BrowserWindow = electron.BrowserWindow;

// Other Modules
const url = require('url');
const FileSystem = require('fs');
const LibraryReader = require('./LibraryReader.min.js');
const MenuBar = require('./MenuBar.min.js');
const TrayIcon = require('./TrayIcon.min.js');
const PreInstall = require('./PreInstall.min.js');

var libraries = ["E:/Software/Steam/steamapps"];

var window;
var stage = 0, stages = 6;

app.on('ready', function() {
    window = new BrowserWindow({
        width: 1000,
        height: 760,
        show: false,
        icon: './assets/icon.png',
        title: 'CompanionLauncher v2.0'
    });

    PreInstall.update(bake);
    loadConfig();
    loadLocals();
    loadResources();

    // Wait for Baking...
});

function bake() {
    stage++;

    log('Baking... Stage: ' + stage);

    if (stage == stages) {
        window.loadURL(url.format({
            pathname: 'index.html',
            protocol: 'file:',
            slashes: true
        }));

        TrayIcon.init();
        global.updateUI();

        LibraryReader.setWindow(window);
        LibraryReader.loadSteamLibraries(libraries);

        global.games = LibraryReader.listGames();

        window.webContents.on('did-finish-load', function() {
            window.show();
        });

        setInterval(function() {
            LibraryReader.ping(libraries);
        }, 30000);
    }
}

function loadConfig() {
    var path = app.getPath("userData") + "/config.json";

    if (!FileSystem.existsSync(path)) {
        FileSystem.writeFile(path, "{}", function(err) {
            log(err);
        });
    }

    FileSystem.readFile(path, 'UTF-8', function(err, data) {
        if (!err) {
            global.config = JSON.parse(data);
            bake(); // 1

            var modified = setupConfigValues([["currency", "USD"], ["lang", "en_US"], ["sorting", "name"]]);

            if (modified) {
                FileSystem.writeFile(path, JSON.stringify(global.config), function(err) {
                    if (err) log(err);
                });
            }
        }
        else {
            log(err);
        }
    });
}

function setupConfigValues(array) {
    var modified = false;

    for (var i = 0; i < array.length; i++) {
        var key = array[i][0];

        if (!global.config[key]) {
            global.config[key] = array[i][1];
            modified = true;
        }
    }

    return modified;
}

function loadResources() {
    FileSystem.readFile("./assets/features.min.json", 'UTF-8', function(err, data) {
        if (!err) {
            global.features = JSON.parse(data);
            bake(); // 2
        }
        else {
            log(err);
        }
    });

    FileSystem.readFile("./assets/currencies.min.json", 'UTF-8', function(err, data) {
        if (!err) {
            global.currencies = JSON.parse(data);
            bake(); // 3
        }
        else {
            log(err);
        }
    });

    FileSystem.readFile("./LICENSE", 'UTF-8', function(err, data) {
        if (!err) {
            global.license = data;
            bake(); // 4
        }
        else {
            log(err);
        }
    });
}

function loadLocals() {
    global.locals = {};

    FileSystem.readdir("./lang", function(err, files) {
        if (!err) {
            for (var i = 0; i < files.length; i++) {
                if (files[i].match('.*_.*\.min\.json')) {
                    stages++;

                    FileSystem.readFile("./lang/" + files[i], 'UTF-8', function(err, data) {
                        if (!err) {
                            var json = JSON.parse(data);
                            global.locals[json.code] = json;
                            bake() // 5+n
                        }
                        else {
                            log(err);
                        }
                    });
                }
            }

            bake(); // 5
        }
        else {
            log(err);
        }
    });
}

global.saveConfig = function(cfg) {
    log('Saving Config...')
    global.config = cfg;
    FileSystem.writeFile(app.getPath('userData') + '/config.json', JSON.stringify(cfg), function(err) {
        if (err) log(err);
    });
}

global.isDevMode = function() {
    // Set via a batch script.
    return process.env.ELECTRON_DEVELOPMENT_ENVIRONMENT == 'true';
}

function log(message) {
    global.log('Main', message);
}

global.log = function(thread, message) {
    console.log(' (' + thread + '): ' + message);
}

global.updateUI = function() {
    TrayIcon.build(window);

    var menu = MenuBar.build(window);

    Menu.setApplicationMenu(menu);
    window.setMenu(menu);
}
