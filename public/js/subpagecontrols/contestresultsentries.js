// Author: Scott Gay
define([
        "assets/js/subpagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
//	"assets/js/libraries/underscore-min.js"
        "assets/js/libraries/dust-core.min.js",
        "assets/js/models/contestentries.js"
], function (Base) {
        ruckus.subpagecontrols.contestresultsentries = function (parameters) {
                Base.call(this);
		var _this = this;
                this.init = function () {
                        this.parameters = parameters;

			var subReload = this.msgBus.subscribe("controls.quicklineup.reloadentries",function(){ _this.load(); });
			_this.subscriptions.push(subReload);


                };
                this.init();
        };

        ruckus.subpagecontrols.contestresultsentries.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.contestresultsentries.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-spc-contestresultsentries');
	
                this.contestEntries = new ruckus.models.contestentries({});
                this.models.push(this.contestEntries);
                var sub = this.msgBus.subscribe("model.contestentries.retrieve", function (data) {
                        sub.unsubscribe();
                        _this.require_template('contestresultsentries-tpl');
                        dust.render('dusttemplates/contestresultsentries-tpl', {entries: data.data}, function (err, out) {
                                _this.container.html(out);
				_this.addScrollBars();
                                if (_this.parameters.tab != 'entries')
                                        _this.hidePage();
                                $('#entrysearch').bind('keyup', function (evt) {
                                        evt.stopPropagation();
                                        var val = $('#entrysearch').val();
                                        $.each(data.data, function (key, value) {
                                                if (value.userName.toLowerCase().indexOf(val.toLowerCase()) == -1) {
                                                        $('.entry_' + value.userId).hide();
                                                } else {
                                                        $('.entry_' + value.userId).show();
                                                }
                                        });
                                });
                                $('#entrysearch').bind('focus', function (evt) {
                                        evt.stopPropagation();
                                        if ($('#entrysearch').val() == 'Search_') {
                                                $('#entrysearch').val('');
                                                $('#entrysearch_blink').hide();
                                        }
                                });
                                $('#entrysearch').bind('blur', function (evt) {
                                        evt.stopPropagation();
                                        if ($('#entrysearch').val() == '') {
                                                $('#entrysearch').val('Search_');
                                                $('#entrysearch_blink').show();
                                        }
                                });

                        });
                });
                this.contestEntries.fetch(this.parameters.contest);
        };

        ruckus.subpagecontrols.contestresultsentries.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.contestresultsentries;
});
	

