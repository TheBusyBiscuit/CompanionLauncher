const https = require('https');
const FileSystem = require('fs');

module.exports = {
    update: function(callback) {
        if (global.isDevMode()) {
            var list = [];

            var request = https.get({
                host: 'api.github.com',
                path: '/repos/TheBusyBiscuit/CompanionLauncher/contributors',
                headers: {
                    'User-Agent': 'CompanionLauncher'
                }
            } , function(response) {
                var code = response.statusCode;
                log('contributors.json (' + code + ')');
                if (code == 200) {
                    var body = '';

                    response.on('data', function(data) {
                        body += data;
                    });

                    response.on('end', function() {
                        var contributors = JSON.parse(body);

                        log('Contributors');

                        for (var i = 0; i < contributors.length; i++) {
                            log('  ' + contributors[i].login + ' (' + contributors[i].contributions + ')');
                            list.push({
                                name: contributors[i].login,
                                amount: contributors[i].contributions
                            });
                        }

                        list.sort(function(a,b) {
                            return b.amount - a.amount;
                        })

                        FileSystem.writeFile(__dirname + '/../../assets/contributors.json', JSON.stringify(list, null, 2), function(err) {
                            if (err) log(err);
                            else {
                                var text = '';

                                for (var i = 0; i < list.length; i++) {
                                    if (text == '') text = list[i].name;
                                    else text += ', ' + list[i].name;
                                }

                                global.authors = text;

                                callback();
                            }
                        });
                    });
                }
            }).on('error', function(err) {
                log(err);
            });
        }
        else {
            FileSystem.readFile(__dirname + '/../../assets/contributors.min.json', function(err, data) {
                if (err) log(err);
                else {
                    var list = JSON.parse(data);
                    var text = '';

                    for (var i = 0; i < list.length; i++) {
                        if (text == '') text = list[i].name;
                        else text += ', ' + list[i].name;
                    }

                    global.authors = text;

                    callback();
                }
            });
        }
    }
}

function log(message) {
    global.log('Main', message);
}
