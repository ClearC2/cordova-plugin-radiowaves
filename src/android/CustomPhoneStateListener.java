package com.clearc2.cordova.radiowaves;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.util.Log;

public class CustomPhoneStateListener extends PhoneStateListener {

  SignalStrengthListener listener;

  public CustomPhoneStateListener(SignalStrengthListener listener) {
      this.listener = listener;
  }

  @Override
  public void onSignalStrengthsChanged(SignalStrength signalStrength) {
      super.onSignalStrengthsChanged(signalStrength);
      listener.signalUpdated(signalStrength);
  }
}
