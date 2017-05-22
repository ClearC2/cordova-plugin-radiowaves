package com.clearc2.cordova.radiowaves;

import android.telephony.SignalStrength;

/**
 * Created by cameronmoreau on 5/17/17.
 */

public interface SignalStrengthListener {
    void signalUpdated(SignalStrength signalStrength);
}
