// Author: Scott Gay
define([
        'rg_global_base',
        'rg_pubsub',
        'atmosphere'
], function (Base) {
        ruckus.models.basews = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.channels = [];
                        this.subscriptions = [];
                        this.intervals = [];
                        this.modelData = undefined;
                };
                this.init();
        };

        ruckus.models.basews.prototype = Object.create(Base.prototype);

        ruckus.models.basews.prototype.openChannel = function (endpoint) {
                var _this = this;
                this.endpoint = endpoint;

                this.log({type: 'api', data: endpoint, msg: "WEB SOCKET CHANNEL OPEN"});

                this.socket = $.atmosphere;
                this.subSocket;
                this.transport = 'websocket';
                this.request = {
                        url: endpoint,
                        contentType: "application/json",
                        logLevel: 'error', // info, error, debug
                        transport: 'websocket',
                        fallbackTransport: 'long-polling'
                };
                this.ready = false;

                this.request.onOpen = function (response) {
//		    	console.log('Atmosphere connected using ' + response.transport );
                        _this.ready = true;
                        _this.transport = response.transport;
                };

                this.request.onReopen = function (response) {
//		   	console.log('Atmosphere re-connected using ' + response.transport );
                };

                this.request.onTransportFailure = function (errorMsg, request) {
                        $.atmosphere.info(errorMsg);
                        if (window.EventSource) {
                                request.fallbackTransport = "long-polling";
                        }
//		     	console.log('Default transport is WebSocket, fallback is ' + request.fallbackTransport );
                };

                this.request.onMessage = function (response) {
                        _this.log({type: 'api', data: response.responseBody, msg: "RAW RESPONSE"});
                        if(response.responseBody.indexOf('error:') === -1) {
                                var message = JSON.parse(response.responseBody);
                                var data = message.payload;
                                _this.msgBus.publish("socket." + message.type, data);
                        }
                        else {
                                _this.log('API / SOCKET ERROR ***************');
                                _this.log(response.responseBody);
                                _this.log('API / SOCKET ERROR ***************');
                        }
                };

                this.request.onClose = function (response) {
                        _this.log({type: 'api', data: endpoint, msg: "WEB SOCKET CHANNEL CLOSED"});
                };

                this.request.onError = function (response) {
//		     	console.log('Sorry, but there\'s some problem with your socket or the server is down' );
                        location = "/app#error";
                };

                this.request.onReconnect = function (request, response) {
//		     	console.log('Connection lost, trying to reconnect. Trying to reconnect ' + request.reconnectInterval);
                        _this.log({type: 'api', data: undefined, msg: "WEB SOCKET RECONNECT"});
//			location = "/app#error";
                };
                this.subSocket = _this.socket.subscribe(this.request);
                this.channels.push(this.socket);
        };

        ruckus.models.basews.prototype.channelPush = function (fParams) {
                this.subSocket.push(JSON.stringify(fParams));
        };

        ruckus.models.basews.prototype.sendRequest = function (request) {

                var url = request.url;
                if (url.indexOf('.') != -1)
                        url += "?ver=" + ruckusVersion;
                else
                        url += "?random=" + this.getRandomNumber();
                $.ajax({
                        type: request.type,
                        url: url,
                        data: request.data,
                        contentType: request.contestType,
                        success: function (data, status) {
                                if (request.callback != undefined)
                                        request.callback(data);
                        },
                        error: function (data, e1, e2) {
                                var errorInfo = { 'data': data, 'e1': e1, 'e2': e2 };
                                if (request.failcallback != undefined)
                                        request.failcallback(errorInfo);
                        }
                });
        };

        ruckus.models.basews.prototype.__addsubscription = function (topic, callback) {
                var _this = this;
                var sub = _this.msgBus.subscribe(topic, callback);
                _this.subscriptions.push(sub);
        };


        ruckus.models.basews.prototype.unload = function () {
                var _this = this;
                $.each(this.subscriptions, function (key, value) {
                        _this.log({type: 'unload', data: value, msg: "UNLOAD MODEL SUBSCRIPTIONS"});
                        value.unsubscribe();
                });
                $.each(this.channels, function (key, value) {
                        _this.log({type: 'unload', data: value, msg: "UNLOAD MODEL CHANNELS"});
                        value.unsubscribe();
                });
                $.each(_this.intervals, function (key, value) {
                        _this.log({type: 'unload', data: value, msg: 'UNLOAD INTERVAL'});
                        clearInterval(value);
                });
                this.modelData = undefined;
        };

        return ruckus.models.basews;
});