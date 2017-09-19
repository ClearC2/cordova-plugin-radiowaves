package com.clearc2.cordova.radiowaves;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.CellSignalStrengthLte;
import android.telephony.TelephonyManager;
import android.Manifest;
import android.os.Build;
import android.util.Log;

/**
 * Created by cameronmoreau on 5/17/17.
 */

public class RadioWaves extends CordovaPlugin implements SignalStrengthListener {

	private static final String LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;

	// Plugin Context
	Activity activity;

	// Save callback to hit multiple times as signal changes
	CallbackContext watchCallback;
	CallbackContext infoCallback;

	// Radio wave signal variables
	TelephonyManager tm;
	CustomPhoneStateListener phoneStateListener;

	CellInfoCdma cellInfoCdma;
	CellInfoLte cellInfoLte;
	CellInfoGsm cellInfoGsm;

	public RadioWaves() {
	}
	
	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		
		this.activity = this.cordova.getActivity();
		
		// Setup signal listener
		this.tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
		this.phoneStateListener = new CustomPhoneStateListener(this);
	}

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

		// Listen to signal strength changes
		if ("watchSignal".equals(action)) {
			startWatchSignal(callbackContext);
			return true;
		}

		// Stop listening to signal updates
		if ("stopWatchSignal".equals(action)) {
			stopWatchSignal(callbackContext);
			return true;
		}

		// Fetch all CellInfo data
		if ("getInfo".equals(action)) {

			// Request location permission if needed
			if(PermissionHelper.hasPermission(this, LOCATION_PERMISSION)) {
				getCellInfo();
				getInfo(callbackContext);
			} else {
				this.infoCallback = callbackContext;
				PermissionHelper.requestPermission(this, 0, LOCATION_PERMISSION);
			}
			
			return true;
		}

		// Returning false results in a "MethodNotFound" error.
		return false;
	}

	@Override
	public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
		// If allowed, fetch cell info
		if (grantResults[0] != PackageManager.PERMISSION_DENIED) {
			getCellInfo();
			
			// Finish getting info
			if (infoCallback != null) getInfo(infoCallback);
		} else {
			// Info failed
			if (infoCallback != null) infoCallback.error("Permission denied");
		}
	}

	private void getCellInfo() {
		// Get cell info data
		if (Build.VERSION.SDK_INT >= 17) {
			for(CellInfo i : tm.getAllCellInfo()) {
				if (i instanceof CellInfoCdma) cellInfoCdma = (CellInfoCdma) i;
				else if (i instanceof CellInfoLte) cellInfoLte = (CellInfoLte) i;
				else if (i instanceof CellInfoGsm) cellInfoGsm = (CellInfoGsm) i;
			}
		}
	}

	/**
	 * Private cordova executable functions
	 * These are called from `execute`
	 */

	// Begin listening to signal and store callback
	private void startWatchSignal(final CallbackContext callbackContext) {
		this.watchCallback = callbackContext;
		tm.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}

	// Stop watching signal and discard resources
	private void stopWatchSignal(final CallbackContext callbackContext) {
		tm.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
		callbackContext.success();
		this.watchCallback = null;
	}

	// Return back LTE, CMDA and GSM data
	private void getInfo(final CallbackContext callbackContext) {
		JSONObject cellInfo = new JSONObject();
		JSONObject lteInfo = new JSONObject();
		JSONObject cdmaInfo = new JSONObject();
		JSONObject gsmInfo = new JSONObject();

		try {
			// LTE
			if (cellInfoLte != null) {
				CellIdentityLte lteIdentity = cellInfoLte.getCellIdentity();

				int mcc = (lteIdentity.getMcc() == Integer.MAX_VALUE) ? -1 : lteIdentity.getMcc();
				int mnc = (lteIdentity.getMnc() == Integer.MAX_VALUE) ? -1 : lteIdentity.getMnc();
				int pci = (lteIdentity.getPci() == Integer.MAX_VALUE) ? -1 : lteIdentity.getPci();
				int tac = (lteIdentity.getTac() == Integer.MAX_VALUE) ? -1 : lteIdentity.getTac();
				int ci = (lteIdentity.getCi() == Integer.MAX_VALUE) ? -1 : lteIdentity.getCi();
				int sector = (ci == -1) ? -1 : (ci & 0xFF);
				int CI = ci >> 8;
				CI &= 0xFFFFFF;
				int market = (ci == -1) ? -1 : CI / 1000;
				int enodeb = (ci == -1) ? -1 : CI % 1000;

				lteInfo.put("mcc", mcc);
				lteInfo.put("mnc", mnc);
				lteInfo.put("pci", pci);
				lteInfo.put("tac", tac);
				lteInfo.put("ci", ci);
				lteInfo.put("sector", sector);
				lteInfo.put("market", market);
				lteInfo.put("enodeb", enodeb);

				cellInfo.put("lte", lteInfo);
			}

			// CDMA
			if (cellInfoCdma != null) {
				CellIdentityCdma cdmaIdentity = cellInfoCdma.getCellIdentity();

				cdmaInfo.put("bsid", cdmaIdentity.getBasestationId());
				cdmaInfo.put("sid", cdmaIdentity.getSystemId());
				cdmaInfo.put("nid", cdmaIdentity.getNetworkId());

				cellInfo.put("cdma", cdmaInfo);
			}

			// GSM
			if (cellInfoGsm != null) {
				CellIdentityGsm gsmIdentity = cellInfoGsm.getCellIdentity();

				gsmInfo.put("arfcn", gsmIdentity.getArfcn());
				gsmInfo.put("bsic", gsmIdentity.getBsic());
				gsmInfo.put("cid", gsmIdentity.getCid());
				gsmInfo.put("lac", gsmIdentity.getLac());
				gsmInfo.put("mcc", gsmIdentity.getMcc());
				gsmInfo.put("mnc", gsmIdentity.getMnc());

				cellInfo.put("gsm", gsmInfo);
			}

		} catch(JSONException e) {
			e.printStackTrace();
		}

		// Send back the result
		PluginResult result = new PluginResult(PluginResult.Status.OK, cellInfo);
		callbackContext.sendPluginResult(result);
	}

	/**
	 * Implemented signal listener
	 */
	public void signalUpdated(SignalStrength signalStrength) {
		JSONObject signalData = new JSONObject();
		JSONObject cdmaData = new JSONObject();
		JSONObject evdoData = new JSONObject();
		JSONObject lteData = new JSONObject();

		try {
			// CDMA
			cdmaData.put("rssi", signalStrength.getCdmaDbm());
			cdmaData.put("ecio", signalStrength.getCdmaEcio());

			// EVDO
			evdoData.put("rssi", signalStrength.getEvdoDbm());
			evdoData.put("ecio", signalStrength.getEvdoEcio());
			evdoData.put("snr", signalStrength.getEvdoSnr());

			// LTE
			if (cellInfoLte != null) {
				CellSignalStrengthLte signalStrengthLte = cellInfoLte.getCellSignalStrength();
				lteData.put("dbm", signalStrengthLte.getDbm());
				String[] LTEData = signalStrengthLte.toString().split(" ");
				for (int i = 0; i < LTEData.length; i++) {
					String[] data = LTEData[i].split("=");
					if (data.length == 2) {
						int value = (Integer.parseInt(data[1]) == Integer.MAX_VALUE) ? -1 : data[1];
						lteData.put(data[0], value);
					}
				}
			}

			// Signal
			signalData.put("strength", signalStrength.getGsmSignalStrength());
			signalData.put("errorRate", signalStrength.getGsmBitErrorRate());
			signalData.put("cdma", cdmaData);
			signalData.put("evdo", evdoData);
			signalData.put("lte", lteData);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		// If callback is present, return signal object and keep callback alive
		if (watchCallback != null) {
			PluginResult result = new PluginResult(PluginResult.Status.OK, signalData);
			result.setKeepCallback(true);
			watchCallback.sendPluginResult(result);
		}
	}
}