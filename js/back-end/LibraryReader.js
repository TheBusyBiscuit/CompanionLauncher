const electron = require('electron');
const app = electron.app;
const shell = electron.shell;

const FileSystem = require('fs');
const http = require('http');

var window;
var games = [];
var idlist = [];

prepareDirectory("thumbnails");
prepareDirectory("details");

global.lookup = {};

module.exports = {

    setWindow: function(obj) {
        window = obj;
    },

    listGames: function() {
        return games;
    },

    ping: function(libraries) {

        for (var i = 0; i < idlist.length; i++) {
            var id = idlist[i];
            var exists = false;

            for (var j = 0; j < libraries.length; j++) {
                if (FileSystem.existsSync(libraries[j] + '/appmanifest_' + id + ".acf")) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                log('REMOVED ' + id);
                sendUpdate(games[i], 'delete');

                idlist.splice(i, 1);
                games.splice(i, 1);
            }
        }

        for (var i = 0; i < libraries.length; i++) {
            var path = libraries[i];
            log('Reading ' + path);

            FileSystem.readdir(path, function(err, files) {
                if (!err) {
                    for (var i = 0; i < files.length; i++) {
                        var file = files[i];

                        if (file.match('appmanifest_[0-9]+\.acf')) {
                            var id = file.replace('appmanifest_', '').replace('.acf', '');
                            if (idlist.indexOf(id) == -1) {
                                log('ADDED ' + id);
                                loadGame(path + '/' + file);

                                sendUpdate(games[games.length - 1], 'create');
                            }
                        }
                    }
                }
                else {
                    log(err);
                }
            });
        }
    },

    loadSteamLibraries: function(libraries) {
        for (var i = 0; i < libraries.length; i++) {
            var path = libraries[i];
            log('Reading ' + path);

            FileSystem.readdir(path, function(err, files) {
                if (!err) {
                    for (var i = 0; i < files.length; i++) {
                        var file = files[i];

                        if (file.match('appmanifest_[0-9]+\.acf')) {
                            loadGame(path + '/' + file);
                        }
                    }
                }
                else {
                    log(err);
                }
            });
        }
    }

};

function loadGame(file) {
    FileSystem.readFile(file, 'UTF-8', function(err, data) {
        if (!err) {
            var json = data.replace('"AppState"\n', "");

            json = replaceAll(json, '"\n\t+(?=")', '",');
            json = replaceAll(json, '"\t+(?=")', '":');
            json = replaceAll(json, '"\n\t+(?={)', '":');
            json = replaceAll(json, '}\n\t+(?=")', '},');

            json = JSON.parse(json);

            var game = {};
            game.name = json.name;

            game.bytes = game.size = json.SizeOnDisk;

            var id = -1;

            if (json.appid != undefined) id = json.appid;
            else if (json.appID != undefined) id = json.appID;

            if (id != -1) {
                idlist.push(id);
                game.id = id;
                log(game.name + " (" + game.id + ")");

                global.lookup[id] = game;

                if (!FileSystem.existsSync(app.getPath("userData") + "/thumbnails/" + game.id + ".jpg")) {
                    log("  Downloading header.jpg");
                    var request = http.get("http://cdn.akamai.steamstatic.com/steam/apps/" + id + "/header.jpg", function(response) {
                        var code = response.statusCode;
                        if (code == 200) {
                            var stream = FileSystem.createWriteStream(app.getPath("userData") + "/thumbnails/" + game.id + ".jpg");
                            response.pipe(stream);
                        }
                        game.thumbnail = app.getPath("userData") + '/thumbnails/' + game.id + '.jpg';
                        log("   " + game.id + ".jpg (" + code + ")");
                        sendUpdate(game, 'update');
                    });
                }
                else {
                    game.thumbnail = app.getPath("userData") + '/thumbnails/' + game.id + '.jpg';
                }

                {
                    log("  Downloading details.json");
                    var request = http.get('http://store.steampowered.com/api/appdetails/?filters=price_overview,developers,categories&appids=' + id + '&cc=' + global.currencies[global.config.currency].cc, function(response) {
                        var code = response.statusCode;
                        if (code == 200) {
                            var body = '';

                            var stream = FileSystem.createWriteStream(app.getPath("userData") + "/details/" + game.id + ".json");
                            response.pipe(stream);

                            response.on('data', function(data) {
                                body += data;
                            });

                            response.on('end', function() {
                                var details = JSON.parse(body);
                                readDetails(game, details);

                                log("   " + game.id + ".json (" + code + ")");
                            });
                        }
                    }).on('error', function(err) {
                        log(err);
                        if (FileSystem.existsSync(app.getPath("userData") + "/details/" + game.id + ".json")) {
                            FileSystem.readFile(app.getPath("userData") + "/details/" + game.id + ".json", 'UTF-8', function(err, data) {
                                if (!err) {
                                    readDetails(game, JSON.parse(data));
                                }
                            });
                        }
                    });
                }

                games.push(game);
            }
        }
    });
}

function formatList(jsonList) {
    var str = '';
    for (var i = 0; i < jsonList.length; i++) {
        var element = jsonList[i];

        if (str == '') str = element;
        else str += ', ' + element;
    }

    return str;
}

function formatFeatures(game, json, callback) {
    var str = '';
    var count = 0;
    var array = [];

    json.sort(function(a,b) { return a.id - b.id});

    for (var i = 0; i < json.length; i++) {
        var element = json[i].id;

        if (global.features[element]) {
            array.push(element);
            if (str == '') str = '<span class="features_icon feature_' + element + '">' + global.features[element].char + '</span>';
            else str += ' ' + '<span class="features_icon feature_' + element + '">' + global.features[element].char + '</span>';

            count++;
        }
    }

    game.featuresCount = count;
    game.features = str;
    game.featuresTooltip = array;
}

function readDetails(game, json) {
    if (json[game.id].success) {
        game.details = json;
        game.developers = formatList(json[game.id].data.developers);

        formatFeatures(game, json[game.id].data.categories);

        if (json[game.id].data.price_overview) {
            game.priceInfo = json[game.id].data.price_overview;
            game.price = json[game.id].data.price_overview.final;
        }
        else {
            game.priceInfo = {final: 0, discount_percent: 0};
            game.price = 0;
        }
    }
    else {
        game.developers = '';
        game.features = '';
        game.featuresCount = 0;
        game.priceInfo = {final: -1, discount_percent: 0};
        game.price = -1;
    }

    sendUpdate(game, 'update');
}

function sendUpdate(game, status) {
    if (status == 'update') {
        games[idlist.indexOf(game.id)] = game;
    }
    window.webContents.send('update_game', games, game, status);
}

function replaceAll(str, find, replace) {
    return str.replace(new RegExp(find, 'g'), replace);
}

function prepareDirectory(dir) {
    if (!FileSystem.existsSync(app.getPath("userData") + "/" + dir)) {
        FileSystem.mkdirSync(app.getPath("userData") + "/" + dir);
    }
}

global.launchGame = function(id) {
    log('Launching Game ID "' + id + '"');
    shell.openExternal('steam://rungameid/' + id);
}

function log(message) {
    global.log('Main', message);
}
