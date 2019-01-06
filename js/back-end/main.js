// Electron Initialization
var electron = require('electron');
var app = electron.app;
var Menu = electron.Menu;
var BrowserWindow = electron.BrowserWindow;

// Other Modules
var url = require('url');
var FileSystem = require('fs');
var LibraryReader = require('./LibraryReader.min.js');
var MenuBar = require('./MenuBar.min.js');
var TrayIcon = require('./TrayIcon.min.js');
var PreInstall = require('./../deployment/PreInstall.min.js');

var libraries = ["E:/Software/Steam/steamapps"];

var window, quickWindow;
var exit = false;
var stage = 0, stages = 7;

app.on('ready', function() {
    log('DEVELOPER MODE? ' + global.isDevMode());

    window = new BrowserWindow({
        width: 1000,
        height: 760,
        show: false,
        icon: __dirname + '/../../assets/icon.png',
        title: 'CompanionLauncher v' + app.getVersion()
    });

    quickWindow = new BrowserWindow({
        width: 720,
        height: 52,
        show: false,
        frame: false,
        resizable: false,
        icon: __dirname + '/../../assets/icon.png',
        title: 'CompanionLauncher v' + app.getVersion() + ' (Quick-Launcher)'
    });

    quickWindow.setMenu(null);

    PreInstall.update(bake); // 1
    loadConfig();
    loadLocals();
    loadResources();

    // Wait for Baking...
});

app.on('before-quit', function() {
    exit = true;
})

function bake() {
    stage++;

    log('Baking... Stage: ' + stage);

    if (stage == stages) {
        window.on('close', function(event) {
            if (!exit) {
                event.preventDefault();
                window.hide();
            }
        });

        TrayIcon.init();

        LibraryReader.setWindows(window, quickWindow);
        LibraryReader.loadSteamLibraries(libraries);

        global.games = LibraryReader.listGames();

        quickWindow.loadURL(url.format({
            pathname: __dirname + '/../../quickLaunch.html',
            protocol: 'file:',
            slashes: true
        }));

        window.loadURL(url.format({
            pathname: __dirname + '/../../index.html',
            protocol: 'file:',
            slashes: true
        }));

        window.on('show', function() {
            window.webContents.send('load');
        })

        window.webContents.on('did-finish-load', function() {
            global.updateUI();
            window.show();
        });

        setInterval(function() {
            LibraryReader.ping(libraries);
        }, 30000);
    }
}

function loadConfig() {
    var path = app.getPath("userData") + "/config.json";
    console.log("Using Config: " + path);

    if (!FileSystem.existsSync(path)) {
        FileSystem.writeFile(path, "{}", function(err) {
            log(err);
        });
    }

    FileSystem.readFile(path, 'UTF-8', function(err, data) {
        if (!err) {
            global.config = JSON.parse(data);
            bake(); // 2

            var modified = setupConfigValues([["currency", "USD"], ["lang", "en_US"], ["sorting", "name"], ["markers", {}], ["filters", []], ["libraries", []]]);

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
    FileSystem.readFile(__dirname + '/../../assets/features.min.json', 'UTF-8', function(err, data) {
        if (!err) {
            global.features = JSON.parse(data);
            bake(); // 3
        }
        else {
            log(err);
        }
    });

    FileSystem.readFile(__dirname + '/../../assets/currencies.min.json', 'UTF-8', function(err, data) {
        if (!err) {
            global.currencies = JSON.parse(data);
            bake(); // 4
        }
        else {
            log(err);
        }
    });

    FileSystem.readFile(__dirname + '/../../assets/markers.min.json', 'UTF-8', function(err, data) {
        if (!err) {
            global.markers = JSON.parse(data);
            bake(); // 5
        }
        else {
            log(err);
        }
    });

    FileSystem.readFile(__dirname + '/../../LICENSE', 'UTF-8', function(err, data) {
        if (!err) {
            global.license = data;
            bake(); // 6
        }
        else {
            log(err);
        }
    });
}

function loadLocals() {
    global.locals = {};

    FileSystem.readdir(__dirname + '/../../lang', function(err, files) {
        if (!err) {
            for (var i = 0; i < files.length; i++) {
                if (files[i].match('.*_.*\.min\.json')) {
                    stages++;

                    FileSystem.readFile(__dirname + '/../../lang/' + files[i], 'UTF-8', function(err, data) {
                        if (!err) {
                            var json = JSON.parse(data);
                            global.locals[json.code] = json;
                            bake() // 7+n
                        }
                        else {
                            log(err);
                        }
                    });
                }
            }

            bake(); // 7
        }
        else {
            log(err);
        }
    });
}

global.saveConfig = function(cfg) {
    log('Saving Config...')
    global.config = cfg;
    FileSystem.writeFile(app.getPath('userData') + '/config.json', JSON.stringify(cfg, null, 2), function(err) {
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

global.openQuickLauncher = function() {
    if (quickWindow.isVisible()) quickWindow.hide();
    else quickWindow.show();
}

global.setQuickLauncherHeight = function(height) {
    quickWindow.setSize(quickWindow.getSize()[0], height);
}
