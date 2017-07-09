const electron = require('electron');
const dialog = electron.dialog;
const Menu = electron.Menu;
const shell = electron.shell;

var layout = [
    {
        label: 'General',
        submenu: [
            {
                label: 'About',
                click: function() {
                    // TODO
                }
            },
            {
                type: 'separator'
            },
            {
                label: 'Bug Tracker',
                click: function() {
                    shell.openExternal('https://github.com/TheBusyBiscuit/CompanionLauncher/issues');
                }
            },
            {
                label: 'Help us translate!',
                click: function() {
                    shell.openExternal('https://github.com/TheBusyBiscuit/CompanionLauncher/tree/Electron/lang');
                }
            },
            {
                label: 'Source Code (GitHub)',
                click: function() {
                    shell.openExternal('https://github.com/TheBusyBiscuit/CompanionLauncher');
                }
            }
        ]
    },
    {
        label: 'Settings',
        submenu: [
            {
                label: 'Language',
                type: 'submenu',
                submenu: []
            },
            {
                label: 'Currency',
                type: 'submenu',
                submenu: []
            }
        ]
    }
];

var map = {
    languages: {},
    currencies: {}
};

module.exports = {
    build: function(window) {

        // Developer Tools
        if (global.isDevMode()) {
            layout.push({
                label: '* DEV TOOLS *',
                submenu: [
                    {
                        label: 'Chromium Debugger',
                        accelerator: 'Ctrl+Shift+I',
                        click: function(item, window) {
                            if (window) window.webContents.toggleDevTools()
                        }
                    }
                ]
            });
        }

        for (var key in global.currencies) {
            var currency = global.currencies[key];
            map.currencies[currency.name] = key;

            layout[1].submenu[1].submenu.push({
                label: currency.name,
                type: 'radio',
                checked: (global.config.currency == key),
                click: function(item) {
                    global.config.currency = map.currencies[item.label];
                    global.saveConfig(global.config);

                    dialog.showMessageBox({
                        type: 'warning',
                        message: 'Your changes will be applied after the next Restart.'
                    })
                }
            });
        }

        for (var code in global.locals) {
            var language = global.locals[code];
            map.languages[language.name] = code;

            layout[1].submenu[0].submenu.push({
                label: language.name,
                type: 'radio',
                checked: (global.config.lang == language.code),
                click: function(item) {
                    global.config.lang = map.languages[item.label];
                    global.saveConfig(global.config);

                    window.webContents.send('update_lang', map.languages[item.label]);
                }
            });

            layout[1].submenu[0].submenu.sort(function(a,b) {
                return a.label.localeCompare(b.label);
            })
        }

        layout[1].submenu[1].submenu.sort(function(a,b) {
            return a.label.localeCompare(b.label);
        })

        return Menu.buildFromTemplate(layout);
    }
}
