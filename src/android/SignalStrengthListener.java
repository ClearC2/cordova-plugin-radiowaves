package com.clearc2.cordova.radiowaves;

import android.telephony.SignalStrength;

public interface SignalStrengthListener {
    void signalUpdated(SignalStrength signalStrength);
}
