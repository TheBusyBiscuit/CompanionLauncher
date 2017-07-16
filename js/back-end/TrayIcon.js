const electron = require('electron');
const app = electron.app;
const Menu = electron.Menu;
const Tray = electron.Tray;

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
    }
}

function local(key) {
    return global.locals[global.config.lang].menu.tray[key];
}
