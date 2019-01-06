var electron = require('electron');
var app = electron.app;
var Menu = electron.Menu;
var Tray = electron.Tray;
var GlobalShortcuts = electron.globalShortcut;

var window;

function layout() {
    return [
        {
            label: local('showHide'),
            click: function() {
                if (window.isVisible()) window.hide();
                else window.show();
            }
        },
        {
            label: local('quickLaunch'),
            accelerator: "Ctrl+Shift+L",
            click: function() {
                global.openQuickLauncher();
            }
        },
        {
            label: local('quit'),
            click: function() {
                app.quit();
            }
        }
    ];
}

module.exports = {
    init: function() {
        global.tray = new Tray(__dirname + '/../../assets/icon.png');

        global.tray.on('click', function() {
            if (window.isVisible()) window.hide();
            else window.show();
        });
    },

    build: function(instance) {
        window = instance;

        var menu = Menu.buildFromTemplate(layout());

        global.tray.setContextMenu(menu);

        GlobalShortcuts.register("Ctrl+Shift+L", function() {
            global.openQuickLauncher();
        });
    }
}

function local(key) {
    return global.locals[global.config.lang].menu.tray[key];
}
