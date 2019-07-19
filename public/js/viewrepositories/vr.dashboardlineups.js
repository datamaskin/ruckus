define([
        'rg_viewrepo_base',
        'rg_pubsub',
        'assets/js/models/timestamp.js',
], function (Base) {
        // SETUP
        ruckus.views.repositories.dashboardlineups = function (parameters) {
                var _this = this;
                Base.call(_this);
                _this.parameters = parameters;
                _this.pageloadcomplete = false;
		if (parameters.mockdata) {
                        _this.mockdata = parameters.mockdata;
                } else {
                        _this.mockdata = false;
                }
                if(parameters.interval) {
                        _this.interval = parameters.interval;
                }
                else {
                        _this.interval = 5000;
                }
                _this.local_data = {
                        servertime: null,
                        servertime_updated: null
                };
                _.bindAll(_this,
                        "fetch",
                        "fetchServerTime",
                        "pushServerTimeUpdate");

                // Initialize data repositories
                _this.dataRepos.timeStampRepo = new ruckus.models.timestamp(parameters);
        };
        ruckus.views.repositories.dashboardlineups.prototype = Object.create(Base.prototype);

        // FETCH TRIGGERS
        ruckus.views.repositories.dashboardlineups.prototype.fetch = function () {
                var _this = this;
                _this.log({type: 'general', data: undefined, msg: 'DASHBOARD VIEW REPO > FETCH'});

                // Initialise listeners
                _this.registerDataSubscriptions();

                // Load initial data sets
                _this.dataRepos.timeStampRepo.fetch();
        };
        ruckus.views.repositories.dashboardlineups.prototype.fetchServerTime = function () {
                var _this = this;
                _this.log({type: 'general', data: undefined, msg: 'DASHBOARD VIEW REPO > FETCH SERVER TIME'});
                _this.dataRepos.timeStampRepo.fetch();
        };

        // SUBSCRIPTION REGISTRY
        ruckus.views.repositories.dashboardlineups.prototype.registerDataSubscriptions = function () {
                this.__addsubscription(ruckus.pubsub.subscriptions.models.data.timestamp.servertime, this.pushServerTimeUpdate);
        };

        // RESPONDERS - Server Time
        ruckus.views.repositories.dashboardlineups.prototype.pushServerTimeUpdate = function (data) {
                var _this = this;
                _this.log({type: 'general', data: data, msg: 'DASHBOARD VIEW REPO > pushServerTimeUpdate'});

                _this.local_data.servertime = data;
                _this.local_data.servertime_updated = new Date();
                _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboard.servertime, data);
        };

        // TERMINATION
        ruckus.views.repositories.dashboardlineups.prototype.unload = function () {
                delete this.local_data;
                this.__destroy();
        };

        return ruckus.views.repositories.dashboardlineups;
});
