var electron = require('electron');
var remote = electron.remote;
var ipcRenderer = electron.ipcRenderer;
var Menu = electron.Menu;

var thumbnail_size = {
    width: (420 * 9 / 20).toFixed(0),
    height: (215 * 9 / 20).toFixed(0)
};

var games = remote.getGlobal('games');
var config = remote.getGlobal('config');

var locals = remote.getGlobal('locals');
var currencies = remote.getGlobal('currencies');
var markers = remote.getGlobal('markers');

var loaded = false;

ipcRenderer.on('load', function(event) {
    loaded = true;

    updateSummaries();
    sort();
});

ipcRenderer.on('toggle_search', function(event) {
    var div = $('.searchbar');
    var style = div.css('display');
    div.css('display', style == 'none' ? 'block': 'none');

    $('.search_textfield').focus();
});

ipcRenderer.on('update_game', function(event, list, game, status) {
    log(status + ' ' + game.id);
    games = list;

    if (loaded) {
        if (status == 'update') {
            $("#game_id_" + game.id).replaceWith(dom(game));

            updateSummaries();
            sort();
        }
        else if (status == 'create') {
            createGame(game);

            updateSummaries();
            sort();
        }
        else if (status == 'delete') {
            $("#game_id_" + game.id).remove();

            updateSummaries();
        }
    }
});

ipcRenderer.on('update_lang', function(event, code) {
    config.lang = code;

    $('.summary_games').html('&#127918 ' + local('summary_games', games.length));
    $('.game_button_play').html('&#127918&nbsp;' + local('button_play'));

    $('.search_textfield').attr('placeholder', localM('filters', 'search'));

    for (var i = 0; i < games.length; i++) {
        createMenu(games[i]);
    }

    sort();
    updateHeader();
});

ipcRenderer.on('update_filter', function(event, cfg) {
    config = cfg;

    sort();
});

$(function() {
    log('Loading...');

    $("head").append('<style type="text/css"></style>');
    updateCSS();

    for (var i = 0; i < games.length; i++) {
        var game = games[i];
        createGame(game);
    }

    addSorter('name');
    addSorter('features');
    addSorter('price');
    addSorter('size');

    $('.search_textfield').attr('placeholder', localM('filters', 'search'));
    $('.search_textfield').on('input', function() {
        var value = $('.search_textfield').val();
        var allowed = games.filter(search(value));

        for (var i = 0; i < games.length; i++) {
            $('#game_id_' + games[i].id).css('display', (allowed.indexOf(games[i]) == -1 ? 'none': ''));
        }
    });

    updateHeader();

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

    $(document).on({
        mouseenter: function() {
            $(this).css('font-size', '160%');
        },
        mouseleave: function() {
            $(this).css('font-size', '110%');
        }
    }, '.features_icon');
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

function createGame(game) {
    $(".container_games").append(dom(game));
    createMenu(game);
}

function createMenu(game) {
    var template = [
        {
            label: localM('game', 'play'),
            click: function() {
                remote.getGlobal('launchGame')(game.id);
            }
        },
        {
            type: 'separator'
        },
        {
            label: localM('game', 'mark'),
            type: 'submenu',
            submenu: []
        },
        {
            type: 'separator'
        },
        {
            label: localM('game', 'hide'),
            click: mark(game, 'hidden')
        }
    ];

    var activeMarkers = config.markers[game.id];

    for (var key in markers) {
        template[2].submenu.push({
            label: (locals[config.lang].markers[key]),
            type: 'checkbox',
            checked: (activeMarkers && activeMarkers.indexOf(key) > -1),
            click: mark(game, key)
        });
    }

    remote.getGlobal('createMenu')(game.id, template);
}

function mark(game, key) {
    return function() {
        var array = config.markers[game.id];
        if (array == undefined) array = [];

        var index = array.indexOf(key);

        if (index == -1) array.push(key);
        else array.splice(index, 1);

        config.markers[game.id] = array;

        saveConfig();
        sort();

        createMenu(game);

        remote.getGlobal('updateUI')();
    }
}

function dom(game) {
    return '' +
    '<div id ="game_id_' + game.id + '" class="game_object"' + (isFiltered(game) ? '': ' style="display: none') + '">' +
        '<div id="game_image_' + game.id + '" class="game_component game_thumbnail clickable">' +
            '<div class="game_spacer"></div>' +
            '<img class="game_image_component game_image" src="' + game.thumbnail + '"/>' +
            '<div class="game_button_component" style="display: none;">' +
                '<div class="game_image_component game_button_play">&#127918&nbsp;' + local('button_play') +
                '</div>' +
            '</div>' +
            '<span id="favourite_"' + game.id + ' class="favourite">' +
            addMarkers(game.id) +
            '</span>' +
        '</div>' +
        '<div class="game_component game_info_column game_label">' +
            '<p class="game_title">' +
                str(game.name) +
            '</p>' +
            '<p class="game_developers">' +
                str(game.developers) +
            '</p>' +
        '</div>' +
        '<div class="game_component game_info_column game_features">' +
            '<p class="game_cell features_cell">' +
                str(game.features) +
            '</p>' +
        '</div>' +
        '<div class="game_component game_info_column game_price">' +
            '<p class="game_cell price_cell' + (game.priceInfo && game.priceInfo.discount_percent > 0  ? ' discount': '') + '">' +
                priceLabel(game.priceInfo) +
            '</p>' +
        '</div>' +
        '<div class="game_component game_info_column game_size">' +
            '<p class="game_cell">' +
                '&#128190 ' + formatBytes(game.bytes) +
            '</p>' +
        '</div>' +
    '</div>';
}

function updateSummaries() {
    $('.summary_games').html('&#127918 ' + local('summary_games', games.length));

    var bytes = 0;
    var price = 0;

    for (var i = 0; i < games.length; i++) {
        bytes = +bytes + +games[i].bytes;

        if (games[i].price != undefined) price = +price + +games[i].price;
    }

    $('.summary_price').html('&#128181 ' + priceInfo(price));
    $('.summary_size').html('&#128190 ' + formatBytes(bytes));
}

function sort() {
    var array = games.slice();
    array.sort(sorter(config.sorting));

    for (var i = 0; i < array.length; i++) {
        $($('.container_games').children()[i]).replaceWith(dom(array[i]));
    }

    for (var key in remote.getGlobal('features')) {
        $('.feature_' + key).prop('title', locals[config.lang].features[key]);
    }
}

function sorter(criteria) {
    var sign = 1;
    var key = criteria;

    if (criteria.startsWith('-')) {
        sign = -1;
        key = criteria.substring(1);
    }

    if (key == 'features') key = 'featuresCount';

    return function compare(game1, game2) {
        var val1 = property(game1, key);
        var val2 = property(game2, key);
        return sign * (key == 'name' ? val1.localeCompare(val2): val2 - val1);
    }
}

function isFiltered(game) {
    var filters = config.filters;
    var activeMarkers = config.markers[game.id];
    var input = $('.search_textfield').val();

    if (!search(input)(game)) return false;
    if (activeMarkers && activeMarkers.indexOf('hidden') != -1) return false;
    if (filters.length == 0) return true;
    if (activeMarkers == undefined) return false;

    for (var i = 0; i < filters.length; i++) {
        var filter = filters[i];

        if (activeMarkers.indexOf(filter) != -1) {
            return true;
        }
    }

    return false;
}

function property(game, property) {
    var value = game[property];

    if (value != undefined) {
        return value;
    }
    else {
        return 0;
    }
}

function str(obj) {
    return obj != undefined ? obj: '<p class="loading"></p>';
}

function local(key, variable) {
    return locals[config.lang][key].replace('%X', variable);
}

function localM(section, key) {
    return locals[config.lang].menu[section][key];
}

function addMarkers(id) {
    var text = '';

    for (var key in markers) {
        var array = config.markers[id];
        if (array && array.indexOf(key) > -1) {
            text += markers[key];
        }
    }

    return text;
}

function formatBytes(bytes) {
    if (bytes < 1) return "0 B";

    var tera = bytes / 1099511627776.0;

    if (tera > 1) return tera.toFixed(2) + " TB";
    else {
        var giga = bytes / 1073741824.0;

        if (giga > 1) return giga.toFixed(2) + " GB";
        else {
            var mega = bytes / 1048576.0;

            if (mega > 1) return mega.toFixed(2) + " MB";
            else {
                var kilo = bytes / 1024.0;
                if (kilo > 1) return kilo.toFixed(2) + " KB";
                else return bytes.toFixed(2) + " B";
            }
        }
    }
}

function priceLabel(json) {
    if (json == undefined) return '<p class="loading"></p>';
    if (json.final < 0) return '';

    var price = '&#128181 ';

    if (json.final > 0) price += currencies[config.currency].char + ' ' + (json.final / currencies[config.currency].rate).toFixed(2);
    else price += local('price_none');

    if (json.discount_percent > 0) {
        price += ' (- ' + json.discount_percent + "%)";
    }

    return price;
}

function priceInfo(price) {
    return currencies[config.currency].char + ' ' + (price / currencies[config.currency].rate).toFixed(2);
}

function saveConfig() {
    remote.getGlobal('saveConfig')(JSON.parse(JSON.stringify(config)));
}

function updateHeader() {
    var sorting = config.sorting;

    $('.sorting_thumbnail').html(local('column_icon'));
    $('.sorting_name').html((sorting == 'name' ? '&#11015 ': sorting == '-name' ? '&#11014 ': '&nbsp;&nbsp; ') + local('column_name'));
    $('.sorting_features').html((sorting == 'features' ? '&#11015 ': sorting == '-features' ? '&#11014 ': '&nbsp;&nbsp; ') + local('column_features'));
    $('.sorting_price').html((sorting == 'price' ? '&#11015 ': sorting == '-price' ? '&#11014 ': '&nbsp;&nbsp; ') + local('column_price'));
    $('.sorting_size').html((sorting == 'size' ? '&#11015 ': sorting == '-size' ? '&#11014 ': '&nbsp;&nbsp; ') + local('column_size'));
}

function addSorter(type) {
    $('.sorting_' + type).click(function() {
        if (config.sorting == type) config.sorting = '-' + type;
        else config.sorting = type;

        updateHeader();
        saveConfig();

        sort();
    })
}

function search(input) {
    if (input == '') return function() { return true; }

    var query = input.toLowerCase();

    return function(game) {
        if (game.name && game.name.toLowerCase().indexOf(query) != -1) return true;
        if (game.developers && game.developers.toLowerCase().indexOf(query) != -1) return true;

        if (game.featuresTooltip) {
            for (var i in game.featuresTooltip) {
                if (game.featuresTooltip[i].toLowerCase().indexOf(query) != -1) return true;
            }
        }

        return false;
    }
}

function updateCSS() {
    var css = $("head").children(":last");
    css.html('' +
        '.game_thumbnail { width: ' + thumbnail_size.width + 'px !important; }' +
        '.game_thumbnail { height: ' + thumbnail_size.height + 'px !important; }' +
        '.game_spacer { width: ' + thumbnail_size.width + 'px !important; }' +
        '.game_spacer { height: ' + thumbnail_size.height + 'px !important; }' +
        '.summary_games { width: ' + thumbnail_size.width + 'px !important; }' +
        '.sorting_thumbnail { width: ' + thumbnail_size.width + 'px !important; }' +
        '.game_image { width: ' + thumbnail_size.width + 'px !important; }' +
        '.game_object { height: ' + thumbnail_size.height + 'px !important; }' +
        '.game_object { background: #323232; }' +
        '.game_title { color: #f0f0f0; }' +
        '.game_developers { color: #8c8c8c; }' +
        '.game_features { color: #f0f0f0; }' +
        '.game_price { color: #f0f0f0; }' +
        '.discount { color: #c8ffc8; }' +
        '.game_size { color: #f0f0f0; }' +
        '.game_object { border: 1px solid #242424; }' +
        '.summary_cell, .sorting_cell { color: #f0f0f0; }' +
        '.container_summary, .container_sorting { background: #1e1e1e; }' +
        '::-webkit-scrollbar { background: #282828; }' +
        '::-webkit-scrollbar-thumb:window-inactive, ::-webkit-scrollbar-thumb { background: #111111 }' +
        'body { background: #1e1e1e; }'
    );
}

function log(message) {
    remote.getGlobal('log')('Renderer', message);
}
