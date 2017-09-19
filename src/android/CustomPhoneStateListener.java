package com.clearc2.cordova.radiowaves;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.CellSignalStrengthLte;
import android.util.Log;

/**
 * Created by cameronmoreau on 5/17/17.
 */

public class CustomPhoneStateListener extends PhoneStateListener {

  SignalStrengthListener listener;

  public CustomPhoneStateListener(SignalStrengthListener listener) {
      this.listener = listener;
  }

  @Override
  public void onSignalStrengthsChanged(SignalStrength signalStrength, CellSignalStrengthLte signalStrengthLte) {
      super.onSignalStrengthsChanged(signalStrength, signalStrengthLte);
      listener.signalUpdated(signalStrength, signalStrengthLte);
  }
}