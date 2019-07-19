// Author: Scott Gay
define([
//	"assets/js/base.js",
	"assets/js/models/basews.js",
	"assets/js/libraries/jquery.min.js",
	"assets/js/libraries/jquery.atmosphere.js"
], function(Base){
	// PARAMETERS

	ruckus.models.depositbitcoin = function(parameters){
		Base.call(this);
		this.init = function(){
			this.parameters = parameters;
			// set parameter defaults
			this.modelData = undefined;
		};
		this.init();
	};

	ruckus.models.depositbitcoin.prototype = Object.create(Base.prototype);

	ruckus.models.depositbitcoin.prototype.fetch = function(fParams){
		var _this = this;

//		// RESTFUL
//		var request = {
//			'type' : 'POST',
//			'url' : '/walet/bitcoinReceive', // /depositbitcoin.js (static file)
//			'data' : fParams,
//			'contentType' : "application/json; charset=utf-8",
//			'callback' : function(data){
//				_this.modelData = JSON.parse(data);
//				_this.log({type:'api',data:_this.modelData,msg:"depositbitcoin DATA MODEL"});
//				_this.msgBus.publish("model.depositbitcoin.retrieve", {data : _this.modelData});
//			},
//			'failcallback' : function(data){
//				_this.log({type:'api',data:data,msg:'depositbitcoin AJAX CALL FAILED'});
//			}
//		};
//		this.sendRequest(request);
	
	};

	ruckus.models.depositbitcoin.prototype.staticData = function(){
		return {};
	};

	return ruckus.models.depositbitcoin;
});
