var argscheck = require('cordova/argscheck'),
    channel = require('cordova/channel'),
    utils = require('cordova/utils'),
    exec = require('cordova/exec'),
    cordova = require('cordova');

var PLUGIN_NAME = "radiowaves";

// channel.createSticky('onCordovaInfoReady');
// channel.waitForInitialization('onCordovaInfoReady');

/**
 * RadioWaves:
 */
function RadioWaves() {
  this.signal = {}
  this.cdma = null;
  this.lte = null;
  this.gsm = null;
  
  // var self = this;
  // channel.onCordovaReady.subscribe(function() {
  //   self.getInfo(function(data) {
  //     this.cdma = data.cdma;
  //     this.lte = data.lte;
  //     this.gsm = data.gsm;
  //   });
  // })
}

RadioWaves.prototype.watchSignal = function(successCallback, errorCallback) {
  var success = function(signalData) {
    this.signal = signalData
    successCallback(signalData);
  }

  basicExec("watchSignal", success, errorCallback);
}

RadioWaves.prototype.stopWatchSignal = function(successCallback, errorCallback) {
  basicExec("stopWatchSignal", successCallback, errorCallback);
}

RadioWaves.prototype.getInfo = function(successCallback, errorCallback) {
  basicExec("getInfo", successCallback, errorCallback);
}

function basicExec(action, successCallback, errorCallback) {
  exec(
    successCallback || function() {},
    errorCallback || function() {},
    PLUGIN_NAME,
    action
  );
}

var obj = new RadioWaves();
module.exports = obj;