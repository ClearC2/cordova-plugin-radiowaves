# cordova-plugin-radiowaves

Add to your project with

```cordova plugin add https://github.com/ClearC2/cordova-plugin-radiowaves```

## Usage

### getInfo(successCallback, failureCallback)
This function will read the phone's Cell Info and report the LTE, CMDA and GSM info. This method triggers a permission request on Android M devices and will throw an error if location is denied
```
window.radiowaves.getInfo(data => {
  console.log(data);
}, err => {
  console.log('There was an error: ' + err)
})
```
A response would look like this. If a key is missing, the device does not have that specific capability.
```
{
  lte: {
    mcc: ...,
    mnc: ...,
    pci: ...,
    tac: ...,
    ci: ...,
  },
  cmda: {
    bsid: ...,
    sid: ...,
    nid: ...
  },
  gsm: {
    arfcn: ...,
    bsic: ...,
    cid: ...,
    lac: ...,
    mcc: ...,
    mnc: ...
  }
}
```

### watchSignal(successCallback, failureCallback)
This function will listen for signal changes and report back strength, error rate, cdma and evdo values. **IT IS IMPORTANT TO CALL stopWatchSignal() WHEN YOU ARE DONE**.
```
window.radiowaves.watchSignal(signalUpdate => {
  console.log(signalUpdate);
})
```

A signalUpdate response looks like
```
  {"strength":12,"errorRate":4,"cdma":{"dbm":-90,"ecio":-120},"evdo":{"dbm":-80,"ecio":-120,"snr":4}}
```


### stopWatchSignal()
This function will stop listening to signal updates. Call this method to not waste battery life.
