// Author: Scott Gay
define([
        "assets/js/subpagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
//	"assets/js/libraries/underscore-min.js"
        "assets/js/libraries/dust-core.min.js",
        "assets/js/models/chat.js",
        "nicescroll" //currently checks for character max of 140 chars.
], function (Base) {
        ruckus.subpagecontrols.dashboardcontestchat = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.subpagecontrols.dashboardcontestchat.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.dashboardcontestchat.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-spc-dashboardcontestchat');

                this.chatModel = new ruckus.models.chat({});
                this.models.push(_this.chatModel);
                var sub = _this.msgBus.subscribe("model.chat.all", function (data) {
                        sub.unsubscribe();
                        _this.require_template('dashboardcontestchat-tpl');
                        dust.render('dusttemplates/dashboardcontestchat-tpl', {}, function (err, out) {
                                _this.container.html(out);
				_this.addScrollBars();
                                $.each(data.data, function (key, value) {
                                        _this.render(value, false);
                                });
                                var dhcc_input = $('#dhcc_input');
                                dhcc_input.bind('keypress', function (evt) {
                                        if (evt.keyCode == 13) {
                                                //now additionally checks if the length of the string is less than 140 characters. no errors on the run.
                                                if (dhcc_input.val() != '' && dhcc_input.val() != 'Chat' /*&& dhcc_input.val().length() < 140*/) {
                                                        _this.chatModel.push({'message': dhcc_input.val()});
                                                        dhcc_input.val('');
                                                        $('#dhcc_input_blink').hide();
                                                }
                                        }
                                });
                                dhcc_input.bind('focus', function (evt) {
                                        evt.stopPropagation();
                                        if (dhcc_input.val() == 'Chat') {
                                                dhcc_input.val('');
                                                $('#dhcc_input_blink').hide();
                                        }
                                });
                                dhcc_input.bind('blur', function (evt) {
                                        evt.stopPropagation();
                                        if (dhcc_input.val() == '') {
                                                dhcc_input.val('Chat');
                                                $('#dhcc_input_blink').show();
                                        }
                                });
                        });
                });
                var sub2 = _this.msgBus.subscribe("model.chat.update", function (data) {
                        _this.render(data, true);
                });

                this.subscriptions.push(sub2);
                this.chatModel.fetch({id: this.parameters.contest.contestId});
        };

        ruckus.subpagecontrols.dashboardcontestchat.prototype.render = function (data, prepend) {
                var _this = this;
                var container = undefined;
                if (prepend)
                        container = $('<div>').prependTo($('#dhcc_messages'));
                else
                        container = $('<div>').appendTo($('#dhcc_messages'));

                $('<div>', {'class': 'ellipsis', 'style': 'float:left;font-size:12px;font-weight:500;color:#474747;margin-top:15px;width:100px;'}).appendTo(container).html(data.author);
                $('<div>', {'style': 'float:right;font-size:9px;color:#ff8593;margin-top:18px;'}).appendTo(container).html(_this.formatTimeActual(data.time));
                $('<div>', {'style': 'clear:both;'}).appendTo(container);
                $('<div>', {'style': 'font-size:11px;color:#474747;width:184px;overflow-x:hidden;'}).appendTo(container).html(data.message);
                $('<div>', {'style': 'padding-bottom:15px;border-bottom: 1px dashed #dddddd;width:30%;'}).appendTo(container);

                //$(".chat_container").niceScroll();
        };

        ruckus.subpagecontrols.dashboardcontestchat.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.dashboardcontestchat;
});
