// Author: Scott Gay
define([
        "assets/js/viewrepositories/base.js",
        "rg_pubsub",
        "assets/js/models/contest.js",
        "assets/js/models/contestscoring.js",
        "assets/js/models/timestamp.js"
], function (Base) {
        // SETUP
        ruckus.views.repositories.contestentry = function (parameters) {
                var _this = this;

                Base.call(_this);
                _this.init = function () {
                        _this.parameters = parameters;
                        _this.listeners = [];
                        _this.data_repos = [];
                        _this.data = {
                                contests: null,
                                scoring: null,
                                servertime: null
                        };
                        _this.pageloadcomplete = false;
                };
                _this.init();
                _.bindAll(_this,
                        "fetchServerTime",
                        "pushServerTimeUpdate");

                // Initialise listeners
                _this.listenServerTimeUpdate();

                _this.data_repos.timeStampRepo = new ruckus.models.timestamp({});

        };
        ruckus.views.repositories.contestentry.prototype = Object.create(Base.prototype);

        // FETCH TRIGGERS - Server Time
        ruckus.views.repositories.contestentry.prototype.fetchServerTime = function() {
                var _this = this;
                _this.log({type: 'general', data: undefined, msg: 'CONTEST ENTRY VIEW REPO > FETCH SERVER TIME'});

                _this.data_repos.timeStampRepo.fetch();
        };

        // LISTENERS - Server Time
        ruckus.views.repositories.contestentry.prototype.listenServerTimeUpdate = function () {
                var _this = this;
                var sub = _this.msgBus.subscribe(ruckus.pubsub.subscriptions.models.data.timestamp.servertime, _this.pushServerTimeUpdate);
                this.listeners.push(sub);
        };

        // RESPONDERS - Server Time
        ruckus.views.repositories.contestentry.prototype.pushServerTimeUpdate = function (data) {
                var _this = this;
                _this.log({type: 'general', data: data, msg: 'VIEW REPO > UPDATE SERVER TIME'});
                _this.data.servertime = data;
                _this.data.servertime_updated = new Date();
                _this.msgBus.publish(ruckus.pubsub.subscriptions.view.contestentry.contestrow.servertime, data);
        };

        // TERMINATION
        ruckus.views.repositories.contestentry.prototype.unload = function () {
                var _this = this;
                for(var i=0;i<_this.listeners.length;i++){
                        var l =_this.listeners.pop();
                        l.unsubscribe();
                }

                // Destroy data repos
                for (var key in _this.data_repos) {
                        if (_this.data_repos.hasOwnProperty(key)) {
                                _this.data_repos[key].unload();
                        }
                }
                //_.unbindAll(this, "pushAdd", "pushAll", "pushUpdate");
        };

        return ruckus.views.repositories.contestentry;
});