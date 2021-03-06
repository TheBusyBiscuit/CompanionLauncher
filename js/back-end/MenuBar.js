const electron = require('electron');
const dialog = electron.dialog;
const Menu = electron.Menu;
const shell = electron.shell;
const app = electron.app;

var browserWindow;

function layout() {
    return [
        {
            label: local('general', 'title'),
            submenu: [
                {
                    label: local('general', 'about'),
                    click: function() {
                        dialog.showMessageBox({
                            type: 'info',
                            title: 'About CompanionLauncher',
                            buttons: [
                                local('general', 'license'),
                                'Close'
                            ],
                            message: '\nVersion: v' + app.getVersion() +
                            '\n' +
                            '\nAuthor(s): ' + global.authors +
                            '\n' +
                            '\nElectron v' + process.versions.electron +
                            '\nChromium v' + process.versions.chrome +
                            '\nnode.js v' + process.versions.node
                            ,
                        },
                        function(response) {
                            if (response == 0) {
                                dialog.showMessageBox({
                                    type: 'info',
                                    title: 'License',
                                    buttons: [
                                        'Close'
                                    ],
                                    message: global.license
                                });
                            }
                        });
                    }
                },
                {
                    type: 'separator'
                },
                {
                    label: local('general', 'bugs'),
                    click: function() {
                        shell.openExternal('https://github.com/TheBusyBiscuit/CompanionLauncher/issues');
                    }
                },
                {
                    label: local('general', 'translations'),
                    click: function() {
                        shell.openExternal('https://github.com/TheBusyBiscuit/CompanionLauncher/tree/Electron/lang');
                    }
                },
                {
                    label: local('general', 'source') + ' (GitHub)',
                    click: function() {
                        shell.openExternal('https://github.com/TheBusyBiscuit/CompanionLauncher');
                    }
                },
                {
                    type: 'separator'
                },
                {
                    label: local('general', 'quit'),
                    accelerator: 'Ctrl+Q',
                    click: function() {
                        app.quit();
                    }
                }
            ]
        },
        {
            label: local('settings', 'title'),
            submenu: [
                {
                    label: local('settings', 'language'),
                    type: 'submenu',
                    submenu: []
                },
                {
                    label: local('settings', 'currency'),
                    type: 'submenu',
                    submenu: []
                },
                {
                    label: local('settings', 'hidden'),
                    type: 'submenu',
                    submenu: []
                }
            ]
        },
        {
            label: local('filters', 'title'),
            submenu: [
                {
                    label: local('filters', 'search'),
                    accelerator: 'Ctrl+F',
                    click: function(item, window) {
                        if (window) window.webContents.send('toggle_search');
                    }
                }
            ]
        }
    ];
}

var map = {
    languages: {},
    currencies: {}
};

module.exports = {
    build: function(window) {
        browserWindow = window;

        var template = layout();

        // Developer Tools
        if (global.isDevMode()) {
            template.push({
                label: '* DEV TOOLS *',
                submenu: [
                    {
                        label: 'Chromium Debugger',
                        accelerator: 'Ctrl+Shift+I',
                        click: function(item, window) {
                            if (window) window.webContents.toggleDevTools()
                        }
                    },
                    {
                        label: 'Reload Page',
                        accelerator: 'CmdOrCtrl+R',
                        click: function(item, window) {
                            if (window) window.reload();
                        }
                    },
                    {
                        label: 'Open config.json',
                        accelerator: 'Ctrl+Shift+S',
                        click: function(item, window) {
                            shell.openItem(app.getPath("userData") + "/config.json");
                        }
                    }
                ]
            });
        }

        for (var key in global.currencies) {
            var currency = global.currencies[key];
            map.currencies[currency.name] = key;

            template[1].submenu[1].submenu.push({
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


        for (var id in global.config.markers) {
            if (global.config.markers[id].indexOf('hidden') != -1) {
                template[1].submenu[2].submenu.push({
                    label: global.lookup[id] ? global.lookup[id].name: 'Loading...',
                    click: unhide(id)
                });
            }
        }

        for (var code in global.locals) {
            var language = global.locals[code];
            map.languages[language.name] = code;

            template[1].submenu[0].submenu.push({
                label: language.name,
                type: 'radio',
                checked: (global.config.lang == language.code),
                click: function(item) {
                    global.config.lang = map.languages[item.label];
                    global.saveConfig(global.config);
                    global.updateUI();

                    window.webContents.send('update_lang', map.languages[item.label]);
                }
            });

            template[1].submenu[0].submenu.sort(function(a,b) {
                return a.label.localeCompare(b.label);
            })
        }

        for (var filter in global.markers) {
            template[2].submenu.push({
                label: global.locals[global.config.lang].markers[filter],
                type: 'checkbox',
                checked: (global.config.filters.indexOf(filter) > -1),
                click: changeFilter(filter)
            });
        }

        template[1].submenu[1].submenu.sort(function(a,b) {
            return a.label.localeCompare(b.label);
        })

        return Menu.buildFromTemplate(template);
    }
}

function unhide(id) {
    return function() {
        global.config.markers[id].splice(global.config.markers[id].indexOf('hidden'), 1);

        global.saveConfig(global.config);
        global.updateUI();

        browserWindow.webContents.send('update_filter', global.config);
    }
}

function changeFilter(filter) {
    return function() {
        var index = global.config.filters.indexOf(filter);

        if (index == -1) global.config.filters.push(filter);
        else global.config.filters.splice(index, 1);

        global.saveConfig(global.config);
        global.updateUI();

        browserWindow.webContents.send('update_filter', global.config);
    }
}

function local(parent, key) {
    return global.locals[global.config.lang].menu[parent][key];
}

function log(message) {
    global.log('Main', message);
}

var menus = {};

global.createMenu = function(id, template) {
    menus[id] = Menu.buildFromTemplate(template);
}

global.openMenu = function(id) {
    menus[id].popup(browserWindow);
}
