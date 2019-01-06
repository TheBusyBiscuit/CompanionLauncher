var electron = require('electron');
var remote = electron.remote;
var ipcRenderer = electron.ipcRenderer;

var string_similarity = require('string-similarity');

var thumbnail_size = {
    width: (420 * 9 / 20).toFixed(0),
    height: (215 * 9 / 20).toFixed(0)
};

var games = remote.getGlobal('games');
var config = remote.getGlobal('config');

var locals = remote.getGlobal('locals');

var max_games = 4;

$(function() {
    $("head").append('<style type="text/css"></style>');
    updateCSS();

    $.fn.sortChildren = function (sortingFunction) {

        return this.each(function () {
            var children = $(this).children().get();
            children.sort(sortingFunction);
            $(this).append(children);
        });

    };

    for (var i = 0; i < games.length; i++) {
        var game = games[i];
        createGame(game);
    }

    $('#quick-search').attr("placeholder", locals[config.lang].search["quick-launch"]);

    var search = $("#quick-search")
    var container = $(".container_games");

    search.focus();
    search.on('blur',function () {
        setTimeout(function() {
            search.focus()
        }, 10);
    });

    var evaluate = function(element, query) {
        var data = element.attr('search-data');
        var points = 100 * string_similarity.compareTwoStrings(query, data);

        if (!data.includes(query)) points -= 25;

        return points;
    }

    search.bind("propertychange change keyup input paste", function() {
        var value = search.val().toLowerCase();

        if (value.length == 0) {
            log("Empty Query: Hiding Everything!");
            container.children().hide();

            remote.getGlobal('setQuickLauncherHeight')(52);
        }
        else {
            container.sortChildren(function(a, b) {
                a = $(a);
                b = $(b);

                var points1 = evaluate(a, value);
                var points2 = evaluate(b, value);

                a.attr("title", points1 + "%");
                b.attr("title", points2 + "%");

                if (points1 < 8) {
                    a.hide();
                }
                else {
                    a.show();
                }

                if (points2 < 8) {
                    b.hide();
                }
                else {
                    b.show();
                }

                return points2 - points1;
            });

            remote.getGlobal('setQuickLauncherHeight')(52 + max_games * thumbnail_size.height);
        }
    });

    $(document).on({
        dragstart: function(event) {
            event.preventDefault();
        },
        dragover: function(event) {
            event.preventDefault();
        },
        drop: function(event) {
            event.preventDefault();
        }
    });

    $(document).on({
        mouseenter: function() {
            $($(this).children()[1]).addClass('game_image_hover').attr('id', '0');
            $($(this).children()[2]).css('display', 'inline');
        },
        mouseleave: function() {
            $($(this).children()[2]).css('display', 'none');
        },
        contextmenu: function() {
            var id = $(this).attr('id').replace('game_image_', '');
            remote.getGlobal('openMenu')(id);
        },
        click: function() {
            var id = $(this).attr('id').replace('game_image_', '');
            remote.getGlobal('launchGame')(id);
            remote.BrowserWindow.getFocusedWindow().minimize();
        }
    }, '.game_thumbnail');
});

setInterval(tick(), 60);

function tick() {
    var code = 128348;
    return function() {
        $('.loading').html('&#' + code);

        if (code < 128359) code++;
        else code = 128348;

        $('.game_image_hover').each(function() {
            var element = $(this);

            var i = Number(element.attr('id'));

            if ($(element.parent()).is(':hover')) {
                if (i < 6) {
                    i += 0.7;
                }
            }
            else {
                i -= 0.7;
            }

            if (i > 0) {
                element.css('filter', 'blur(' + i + 'px) brightness(' + (100 - i * 6) + '%)');
                element.css('clip', 'rect(' + (i / 6) + 'px, ' + (thumbnail_size.width - (i / 6)) + 'px, ' + (thumbnail_size.height - (i / 6)) + 'px, ' + (i / 6) + 'px)');
                element.attr('id', i);
            }
            else {
                element.removeClass('game_image_hover');
                element.removeAttr('id');
                element.css('filter', '');
                element.css('clip', '');
            }
        });
    };
}

ipcRenderer.on('update_game', function(event, list, game, status) {
    games = list;

    if (status == 'update') {
        $("#game_id_" + game.id).replaceWith(dom(game));
    }
    else if (status == 'create') {
        createGame(game);
    }
    else if (status == 'delete') {
        $("#game_id_" + game.id).remove();
    }
});

ipcRenderer.on('update_lang', function(event, code) {
    config.lang = code;

    $('.game_button_play').html('&#127918&nbsp;' + locals[config.lang]['button_play']);
    $('#quick-search').attr("placeholder", locals[config.lang].search["quick-launch"]);
});

function createGame(game) {
    $(".container_games").append(dom(game));
}

function dom(game) {
    return '' +
    '<div id ="game_id_' + game.id + '" class="game_object" search-data="' + str(game.name).toLowerCase().replace(/\"/g, "")  + '">' +
        '<div id="game_image_' + game.id + '" class="game_component game_thumbnail clickable">' +
            '<div class="game_spacer"></div>' +
            '<img class="game_image_component game_image" src="' + game.thumbnail + '"/>' +
            '<div class="game_button_component" style="display: none;">' +
                '<div class="game_image_component game_button_play">&#127918&nbsp;' + locals[config.lang]['button_play'] +
                '</div>' +
            '</div>' +
        '</div>' +
        '<div class="game_component game_info_column game_label">' +
            '<p class="game_title">' +
                str(game.name) +
            '</p>' +
        '</div>' +
    '</div>';
}

function str(obj) {
    return obj != undefined ? obj: '<p class="loading"></p>';
}

function updateCSS() {
    var css = $("head").children(":last");
    css.html('' +
        '.quick-search { background: #323232; border-color: #1e1e1e; color: #f0f0f0; }' +
        '.contaner_games { max-height: ' + (thumbnail_size.height * max_games) + ' }' +
        '.game_thumbnail { width: ' + thumbnail_size.width + 'px !important; }' +
        '.game_thumbnail { height: ' + thumbnail_size.height + 'px !important; }' +
        '.game_spacer { width: ' + thumbnail_size.width + 'px !important; }' +
        '.game_spacer { height: ' + thumbnail_size.height + 'px !important; }' +
        '.sorting_thumbnail { width: ' + thumbnail_size.width + 'px !important; }' +
        '.game_image { width: ' + thumbnail_size.width + 'px !important; }' +
        '.game_object { height: ' + thumbnail_size.height + 'px !important; }' +
        '.game_object { background: #323232; }' +
        '.game_title { color: #f0f0f0; }' +
        '.game_size { color: #f0f0f0; }' +
        '.game_object { border: 1px solid #242424; }' +
        '::-webkit-scrollbar { display: none; }' +
        '::-webkit-scrollbar-thumb:window-inactive, ::-webkit-scrollbar-thumb { display: none; }' +
        'body { background: #1e1e1e; overflow: hidden; }'
    );
}

function log(message) {
    remote.getGlobal('log')('Quick-Launch', message);
}
