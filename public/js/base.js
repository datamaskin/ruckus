// Author: Scott Gay
define([
        "postal",
        "moment",
        "moment-timezone",
        "moment-timezone-data",
        "jquery",
        "assets/js/definition.js"
], function (postal, moment) {
        ruckus.base = function (parameters) {
                this.init = function () {
                        this.parameters = parameters;
                        this.msgBus = postal.channel();
                };
                this.init();
        };

        // cleans text prior to passing through as querystring parameter in ajax call
        ruckus.base.prototype.cleantext = function (val) {
                return encodeURIComponent(this.quote(val));
        };

        // implements cleantext above
        ruckus.base.prototype.quote = function (string) {
                var escapable = /[\\\"\u0000-\u001f\u007f-\u009f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g;
                var meta = { // table of character substitutions
                        '\b': '\\b',
                        '\t': '\\t',
                        '\n': '\\n',
                        '\f': '\\f',
                        '\r': '\\r',
                        '"': '\\"',
                        '\\': '\\\\'
                };

                escapable.lastIndex = 0;
                return escapable.test(string) ? string.replace(escapable, function (a) {
                        var c = meta[a];
                        return typeof c === 'string' ? c : '\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
                }) : string;
        };

        // pass any user generated text, querystring parameters, or potentially data from backend through before displaying.  Goal is to strip tags.
        ruckus.base.prototype.cleanxss = function (val) {
                // -fixme- this likely isn't enough.  Look at strip_tags port from php
                val = val.replace('&lt;', '<');
                val = val.replace('&gt;', '>');
                val = val.replace('&amp;quot;', '"');
                return val;
        };

        ruckus.base.prototype.formatPlace = function (i) {
                // FIXME - this should take a position like 1, 2, 3 and return 1st, 2nd, 3rd, 14th, 21st, etc
                var j = i % 10,
                        k = i % 100;
                if (j == 1 && k != 11) {
                        return i + "st";
                }
                if (j == 2 && k != 12) {
                        return i + "nd";
                }
                if (j == 3 && k != 13) {
                        return i + "rd";
                }
                return i + "th";
        };

        Number.prototype.formatInteger = function () {
                var val = this.toString();
                return val.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
        };
        Number.prototype.formatCurrency = function () {
                var val = this;
                var out = val / 100;
                if (out % 1 === 0) {
                        //FIXME:  Learn how to regex.
                        out = out.toString() + ".";
                        out = "$" + out.replace(/\d(?=(\d{3})+\.)/g, '$&,');
                        return out.substring(0, out.length - 1);
                }
                else {
                        return "$" + out.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
                }
        };

        ruckus.base.prototype.formatMoney = function (val) {
                var out = val / 100;
                if (out % 1 === 0) {
                        //FIXME:  Learn how to regex.
                        out = out.toString() + ".";
                        out = "$" + out.replace(/\d(?=(\d{3})+\.)/g, '$&,');
                        return out.substring(0, out.length - 1);
                }
                else {
                        return "$" + out.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
                }
        };

        ruckus.base.prototype.formatTimeActual = function (val) {
                var myDate = new Date(val);
//		return moment(myDate).tz("America/New_York").format("ddd, h:mma z");
                return moment(myDate).tz("America/New_York").format("ddd, h:mma");
        };

        ruckus.base.prototype.formatTimePercentage = function (league, unitsRemaining) {
                return Math.floor(((ruckus.definition[league.toLowerCase()].timeUnits.number - unitsRemaining) / ruckus.definition[league.toLowerCase()].timeUnits.number) * 100);
        };

        ruckus.base.prototype.formatTimePercentageTeam = function (league, unitsRemaining) {
                return Math.floor(((ruckus.definition[league.toLowerCase()].timeUnits.number * ruckus.definition[league.toLowerCase()].players.number - unitsRemaining) / (ruckus.definition[league.toLowerCase()].timeUnits.number * ruckus.definition[league.toLowerCase()].players.number)) * 100);
        };

        ruckus.base.prototype.formatContestName = function (contest, variation) {
                var _this = this;
                var output = {
                        line1: undefined,
                        line2: undefined
                };

                switch (variation) {
                        case '1linesimple' :
                                switch (contest.contestType.abbr) {
                                        case 'H2H' :
                                                if (contest.opp != 'H2H') {
//							output.line1 = contest.league + ' ' + _this.formatMoney(contest.entryFee).replace('$','') + ' vs. ' + contest.opp;
                                                        output.line1 = contest.league + ' vs. ' + contest.opp;
                                                } else {
//							output.line1 = contest.league + ' ' + _this.formatMoney(contest.entryFee).replace('$','') + ' - H2H';
                                                        output.line1 = contest.league + ' - H2H';
                                                }
                                                break;
                                        case 'NRM' :
                                                output.line1 = contest.league + ' - ' + contest.contestType.name;
                                                break;
                                        case 'GPP' :
                                                output.line1 = contest.league + ' - ' + _this.formatMoney(contest.prizePool) + ' ' + contest.displayName;
                                                break;
                                        case 'DU' :
                                                output.line1 = contest.league + ' - ' + contest.contestType.name;
                                                break;
                                        case 'SAT' :
                                                output.line1 = contest.league + ' ' + _this.formatMoney(contest.prizePool) + ' ' + contest.contestType.name;
                                                break;
                                        default :
                                                output.line1 = contest.league + ' ' + contest.contestType.name;
                                                break;
                                }
                                break;
                        case '1linefull' :
                                switch (contest.contestType.abbr) {
                                        case 'H2H' :
                                                if (contest.opp != 'H2H')
                                                //output.line1 = contest.league + ' ' + _this.formatMoney(contest.entryFee).replace('$','') + ' vs. ' + contest.opp;
                                                        output.line1 = contest.league + ' ' + _this.formatMoney(contest.entryFee) + ' vs. ' + contest.opp;
                                                else
                                                //output.line1 = contest.league + ' ' + _this.formatMoney(contest.entryFee).replace('$','') + ' - H2H';
                                                        output.line1 = contest.league + ' ' + _this.formatMoney(contest.entryFee) + ' - H2H';
                                                break;
                                        case 'NRM' :
                                                output.line1 = contest.league + ' - ' + contest.contestType.name;
                                                break;
                                        case 'GPP' :
                                                output.line1 = contest.league + ' ' + contest.displayName;
                                                break;
                                        case 'DU' :
                                                output.line1 = contest.league + ' - ' + contest.contestType.name;
                                                break;
                                        case 'SAT' :
                                                output.line1 = contest.league + ' - ' + contest.contestType.name;
                                                break;
                                        default :
                                                output.line1 = contest.league + ' ' + contest.contestType.name;
                                                break;
                                }
                                break;
                        case '2line' :
                                switch (contest.contestType.abbr) {
                                        case 'H2H' :
                                                output.line1 = contest.league + ' ' + _this.formatMoney(contest.entryFee) + ' - H2H';
                                                if (contest.opp != 'H2H' && contest.opp != undefined)
                                                        output.line2 = 'vs. ' + contest.opp;
                                                else
                                                        output.line2 = '';
                                                break;
                                        case 'NRM' :
                                                output.line1 = contest.league + ' ' + _this.formatMoney(contest.entryFee) + ' - ' + contest.contestType.name;
                                                output.line2 = contest.currentEntries.formatInteger() + ' vs. ' + contest.capacity.formatInteger();
                                                break;
                                        case 'GPP' :
                                                output.line1 = contest.league + ' ' + _this.formatMoney(contest.prizePool) + ' ';
                                                output.line2 = contest.displayName;
                                                break;
                                        case 'DU' :
                                                output.line1 = contest.league + ' ' + _this.formatMoney(contest.entryFee) + ' - ' + contest.contestType.name;
                                                output.line2 = contest.currentEntries.formatInteger() + ' vs. ' + contest.capacity.formatInteger();
                                                break;
                                        case 'SAT' :
                                                output.line1 = contest.league + ' ' + _this.formatMoney(contest.prizePool);
                                                output.line2 = contest.contestType.name;
                                                break;
                                        default :
                                                output.line1 = contest.league + ' ' + _this.formatMoney(contest.entryFee) + ' - ' + contest.contestType.name;
                                                output.line2 = contest.currentEntries.formatInteger() + ' vs. ' + contest.capacity.formatInteger();
                                                break;
                                }
                                break;
                }
                return output;
        };

        ruckus.base.prototype.log = function (logData) {
                if (!logData.level) logData.level = 4;

//                if(logData.level < 4) {
                /*                        switch (logData.type) {

                 case 'api' :
                 this.consolelog('API -> ' + logData.msg);
                 this.consolelog(logData.data);
                 break;
                 case 'pageload' :
                 this.consolelog('PAGE LOAD -> ' + logData.msg);
                 this.consolelog(logData.data);
                 break;
                 case 'event' :
                 this.consolelog('EVENT -> ' + logData.msg);
                 this.consolelog(logData.data);
                 break;
                 case 'unload' :
                 this.consolelog('UNLOAD -> ' + logData.msg);
                 this.consolelog(logData.data);
                 break;

                 default :
                 this.consolelog('GENERAL -> ' + logData.msg);
                 this.consolelog(logData.data);
                 break;

                 }*/
//                }
        };
        ruckus.base.prototype.consolelog = function (val) {
                console.log(val);
        };

        ruckus.base.prototype.evalArray = function (arrOriginal, arrNew) {
                // FIXME - there is probably a better solution to this than to loop through both arrays twice.  Looked on underscore, nothing seemed to workout upon initial glance.
                var output = {
                        add: [],
                        remove: []
                };

                $.each(arrOriginal, function (k1, v1) {
                        var found = false;
                        $.each(arrNew, function (k2, v2) {
                                if (v1.id == v2.id)
                                        found = true;
                        });
                        if (!found)
                                output.remove.push(v1);
                });

                $.each(arrNew, function (k1, v1) {
                        var found = false;
                        $.each(arrOriginal, function (k2, v2) {
                                if (v1.id == v2.id)
                                        found = true;
                        });
                        if (!found)
                                output.add.push(v1);
                });

                return output;
        };

        // note : this is a duplication of sendRequest which also exists in models/base.
        // - it is being left here in the event that you wish to call api directly outside of a model
        ruckus.base.prototype.sendRequest = function (request, loadingContainer) {
                // the structure of this api call can obviously change based on your needs.
                // - action and collection might change to apiName and method for example
                // - change your apiURL
                // - adjust the jsonp call as necessary

                // http://www.ajaxload.info/ is a good resource for generating your own loading gif
                var ajaxLoader = undefined;
                if (loadingContainer != undefined)
                        ajaxLoader = $('<img>', {'src': 'assets/images/ajax-loader.gif'}).appendTo(loadingContainer);

                var action = request.action;
                var collection = request.collection;
                var callback = request.callback;
                var failcallback = request.failcallback;

                var apiURL = "http://96.126.120.64:8126";

                $.ajax({
                        type: "POST",
                        url: apiURL + "?random=" + this.getRandomNumber(),
                        data: "action=" + action + "&collection=" + collection,
                        dataType: "jsonp",
                        success: function (data, status) {
                                if (ajaxLoader != undefined)
                                        ajaxLoader.remove();
                                if (callback != undefined)
                                        callback(data);
                        },
                        error: function (data, e1, e2) {
                                if (ajaxLoader != undefined)
                                        ajaxLoader.remove();
                                var errorInfo = { 'data': data, 'e1': e1, 'e2': e2 };
                                if (failcallback != undefined)
                                        failcallback(errorInfo);
                        }
                });
        };

        ruckus.base.prototype.getRandomNumber = function () {
                var randomnumber = Math.floor(Math.random() * 10000);
                return randomnumber;
        };

        // USED WITH DUST TEMPLATING
        ruckus.base.prototype.require_template = function (templateName) {
                var template = templateName;
                var tmpl_dir = 'assets/dusttemplates';
                var tmpl_url = tmpl_dir + '/' + templateName + '.js';
                var tmpl_string = '';
                $.ajax({
                        url: tmpl_url,
                        method: 'GET',
                        async: false,
                        contentType: 'text',
                        success: function (data) {
                                tmpl_string = data;
                        }
                });

        };

        ruckus.base.prototype.tablestripe = function (selector) {
                //$('.conr_visible').each(function (key, val) {
                $(selector).each(function (key, val) {
                        var row = $('#' + val.id);
                        if (key % 2 == 0) {
                                row.addClass('table-stripe-on');
                                row.removeClass('table-stripe-off');
                        } else {
                                row.addClass('table-stripe-off');
                                row.removeClass('table-stripe-on');
                        }
                });
        };

        ruckus.base.prototype.addScrollBars = function (options) {

                if (options === undefined)
                        options = { horizrailenabled: false, cursordragontouch: true, railvalign: "top", hidecursordelay: 1000 };

                var nicescroll_container = $(".niceScroll");//select all elements with a class of niceScroll

                if (nicescroll_container.hasClass('niceScroll-horiz')) {
                        options.horizrailenabled = true;
                        options.railvalign = "bottom";

                        /*
                         * Fix for quick lineups based on contestresultslineups-tpl.tl file in /app/assets/dusttemplates
                         * For each element with niceScroll-horiz class, find number of li elements (quick lineups)
                         *  then caluculate the total width of each such element,
                         *  then multiply number of elements by element width to get width to assign to container.
                         */
                        $('.niceScroll-horiz').each(function () {
                                var numberOfElements = $(this).find('li').size();
                                var elemWidth = $(this).find('li').outerWidth(true) + 5;
                                var elementWidth = numberOfElements * elemWidth;
                                $(this).children("div").width(elementWidth);
                        });

                }
                if (nicescroll_container.hasClass('niceScroll-vert')) {
                        options.horizrailenabled = false;
                        options.railvalign = "top";
                }
                //add nice scroll to element but immediately hide it so it doesn't show up if not navigating to another page     
                nicescroll_container.niceScroll(options);

                //bind hover event to elements with class niceScroll so they create, show and hide scrollbars based on hover, not automatically
                $('.niceScroll').bind({
                        mouseenter: function () {
                                $(this).niceScroll(options);
                                $(this).getNiceScroll().resize();
                                //$(this).getNiceScroll().show();
                        },
                        mouseleave: function () {
                                //$(this).getNiceScroll().hide();
                        }
                });
        };

        ruckus.base.prototype.unsubscribeFrom = function (channel, topic) {
                var subscriptions = postal.getSubscribersFor({
                        channel: channel,
                        topic: topic
                });
                _.each(subscriptions, function (sub) {
                        sub.unsubscribe();
                });
        };

        return ruckus.base;
});


