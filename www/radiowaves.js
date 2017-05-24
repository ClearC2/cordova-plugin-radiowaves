var argscheck = require('cordova/argscheck'),
    channel = require('cordova/channel'),
    utils = require('cordova/utils'),
    exec = require('cordova/exec'),
    cordova = require('cordova');

var PLUGIN_NAME = "radiowaves";

/**
 * RadioWaves:
 */
function RadioWaves() {
}

RadioWaves.prototype.watchSignal = function(successCallback, errorCallback) {
  basicExec("watchSignal", successCallback, errorCallback);
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