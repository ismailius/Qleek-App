    package android.qleek;

    import android.app.Activity;
    import android.content.BroadcastReceiver;
    import android.content.ComponentName;
    import android.content.Context;
    import android.content.Intent;
    import android.content.IntentFilter;
    import android.content.ServiceConnection;
    import android.content.SharedPreferences;
    import android.graphics.Typeface;
    import android.net.NetworkInfo;
    import android.net.wifi.ScanResult;
    import android.net.wifi.SupplicantState;
    import android.net.wifi.WifiConfiguration;
    import android.net.wifi.WifiInfo;
    import android.net.wifi.WifiManager;
    import android.os.Bundle;
    import android.os.IBinder;
    import android.qleek.MainActivityr.R;
    import android.util.Log;
    import android.view.View;
    import android.widget.TextView;

    import org.json.JSONArray;

    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.Collections;
    import java.util.HashSet;
    import java.util.List;

    import static android.qleek.MainActivity.hideSystemUI;

    /**
     * Created by ismailsalhi on 4/23/15.
     */
    public class ApWebServerActivity extends Activity {

        private IntentFilter intentFilter;
        public IntentReceiver wifiReciever;
        public WifiManager mWifiManager;

        boolean mBound = false;

        private int port = 8000;
        public WebServer server;

        public static List<wifiNetwork> networks = new ArrayList<wifiNetwork>();

        public static boolean AP;
        public static TextView myTextView2;
        String wifis[];

        public static final int WIFI_AP_STATE_DISABLING = 10;
        public static final int WIFI_AP_STATE_DISABLED = 11;
        public static final int WIFI_AP_STATE_ENABLING = 12;
        public static final int WIFI_AP_STATE_ENABLED = 13;
        public static final int WIFI_AP_STATE_FAILED = 14;

        class wifiNetwork {
            String ssid;
            String security;
            int rssi;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.ap_webserver);

            View mMainView  = findViewById(R.id.ap_layout);
            hideSystemUI(mMainView);

            TextView myTextView = (TextView) findViewById(R.id.textView2);
            myTextView.setText(Constants.QLEEK_SETUP_HELLO_MESSAGE);

            myTextView=(TextView)findViewById(R.id.textView2);
            myTextView2=(TextView)findViewById(R.id.textView3);
            Typeface typeFace= Typeface.createFromAsset(getAssets(), "futura.ttf");

//            this.getAssets().
            myTextView.setTypeface(typeFace);
            myTextView2.setTypeface(typeFace);

            AP = false;
            turnOnOffHotspot(this, false);
            mWifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

            // Removing all previous Wifi Configs on the device
            List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
            if(list != null) {
                for (WifiConfiguration k : list) {
                    mWifiManager.removeNetwork(k.networkId);
                }
                mWifiManager.saveConfiguration();
            }

            // Turning the Wifi on if it's off
            if(!isWifiOn(this)) {
                mWifiManager.setWifiEnabled(true);
            }

            wifiReciever = new IntentReceiver();
            intentFilter = new IntentFilter();
            intentFilter.addAction(WebServer.WEB_SERVER_NEW_REQUEST);
            intentFilter.addAction(WebServer.WEB_SERVER_WIFI_ATTEMPT);
            intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
            intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
            intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            intentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
            registerReceiver(wifiReciever, intentFilter);

            // Start scanning the network
            mWifiManager.startScan();
        }

        @Override
        protected void onPause() {
            unregisterReceiver(wifiReciever);
            super.onPause();
        }

        @Override
        protected void onResume() {
            registerReceiver(wifiReciever, intentFilter);
            super.onResume();
        }

        @Override
        protected void onStop() {
            super.onStop();
            // Unbind from the service
            if (mBound) {
                unbindService(mConnection);
                mBound = false;
            }
        }

        public class IntentReceiver extends BroadcastReceiver {

            public void onReceive(Context c, Intent intent) {
                if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(intent.getAction())) {
                    int state = intent.getIntExtra("wifi_state", 0);
                    setWifiApStateText(state);
                } else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                    handleWifiStateChanged(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                            WifiManager.WIFI_STATE_UNKNOWN));
                } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                    handleNetworkStateChanged(
                            (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO));
                } else if (intent.getAction().equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
                /* TODO: handle supplicant connection change later */
                } else if (intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
                    handleSupplicantStateChanged(
                            (SupplicantState) intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE),
                            intent.hasExtra(WifiManager.EXTRA_SUPPLICANT_ERROR),
                            intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 0));
                } else if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
//                handleSignalChanged(intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, 0));
                } else if (intent.getAction().equals(WifiManager.NETWORK_IDS_CHANGED_ACTION)) {
                /* TODO: handle network id change info later */
                } else if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    List<ScanResult> wifiScanList = mWifiManager.getScanResults();
                    wifis = new String[wifiScanList.size()];

                    wifiNetwork net;
                    ArrayList ssids = new ArrayList();

                    for (int i = 0; i < wifiScanList.size(); i++) {
                        ssids.add(wifiScanList.get(i).SSID.toString());
                    }

                    HashSet<String> hs = new HashSet<String>();
                    hs.addAll(ssids);
                    ssids.clear();
                    ssids.addAll(hs);
                    Collections.sort(ssids);

                    String[][] networksList = new String[ssids.size()][2];

                    for (int i = 0; i < ssids.size(); i++) {

                        for (int j = 0; j < wifiScanList.size(); j++)
                        {
                            if (ssids.toArray()[i].equals(wifiScanList.get(j).SSID))
                            {
                                net = new wifiNetwork();
                                net.ssid = (String) ssids.get(i);
                                net.security = wifiScanList.get(j).capabilities;
                                networks.add(net);

                                String[] network = new String[2];
                                network[0] = (String) ssids.get(i);
                                network[1] = getSecurity(wifiScanList.get(j).capabilities);

                                networksList[i][0] = network[0];
                                networksList[i][1] = network[1];
                            }
                        }
                    }

                    if (!AP) {
                        turnOnOffHotspot(c, true);
                        AP = true;

                        JSONArray ssidJsonArray = new JSONArray(Arrays.asList(networksList));
                        try {
                            server = new WebServer(port, ssidJsonArray, c);
                        } catch (IOException e) {
                            e.printStackTrace();
                            TextView myTextView = (TextView) findViewById(R.id.textView2);
                            myTextView.setText(Constants.QLEEK_SETUP_WEB_SERVER_PROBLEM);
                        }
                    }
                }
                else {
                    if (intent.getAction().equals(WebServer.WEB_SERVER_NEW_REQUEST)){
                        TextView myTextView = (TextView) findViewById(R.id.textView2);
                        myTextView.setText(Constants.QLEEK_SETUP_SELECT_HOME_WIFI);
                    }
                    else {

                        if (intent.getAction().equals(WebServer.WEB_SERVER_WIFI_ATTEMPT)){
                            TextView myTextView = (TextView) findViewById(R.id.textView2);
                            String serviceJsonString = intent.getStringExtra("ssid");
                            myTextView.setText("I am trying to connect to " + serviceJsonString + ".\n It will take a few seconds...");
                            connectWifi(intent.getStringExtra("ssid"), intent.getStringExtra("pwd"));
                        }
                    }
                }
            }
        }

        private void connectWifi(final String ssid, final String pwd) {
            new Thread() {
                @Override
                public void run() {
                    SetWifiConfiguration(ssid, pwd);
                }
            }.start();

        }

        private void handleWifiStateChanged(int wifiState) {
            setWifiStateText(wifiState);
        }

        private void setWifiStateText(int wifiState) {
            String wifiStateString;
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLING:
                    wifiStateString = "Disabling";
                    break;
                case WifiManager.WIFI_STATE_DISABLED:{
                    wifiStateString = "Disabled";
                    break;
                }
                case WifiManager.WIFI_STATE_ENABLING:
                    wifiStateString = "Enabling";
                    break;
                case WifiManager.WIFI_STATE_ENABLED: {
                    wifiStateString = "Enabled";
                    break;
                }
                case WifiManager.WIFI_STATE_UNKNOWN:
                    wifiStateString = "Unknown";
                    break;
                default:
                    wifiStateString = "BAD";
                    TextView myTextView = (TextView) findViewById(R.id.textView2);
                    myTextView.setText(Constants.QLEEK_SETUP_INTERNET_CONNECTION_PROBLEM);
                    break;
            }
//            Log.e("INTENT MBR", wifiStateString);
        }

        private void setWifiApStateText(int wifiState) {
            String wifiStateString;
            switch (wifiState) {
                case WIFI_AP_STATE_DISABLING:
                    wifiStateString = "Disabling";
                    break;
                case WIFI_AP_STATE_DISABLED:{
                    wifiStateString = "Disabled";
                    break;
                }
                case WIFI_AP_STATE_ENABLING:
                    wifiStateString = "Enabling";
                    break;
                case WIFI_AP_STATE_ENABLED:
                {
                        TextView myTextView = (TextView) findViewById(R.id.textView2);
                        myTextView.setText(Constants.QLEEK_SETUP_HOTSPOT_INFO);
                        wifiStateString = "Enabled";
                    break;
                }
                case WIFI_AP_STATE_FAILED:
                    wifiStateString = "Failed";
                    TextView myTextView = (TextView) findViewById(R.id.textView2);
                    myTextView.setText(Constants.QLEEK_SETUP_ACCESS_POINT_PROBLEM);
                    break;
                default:
                    wifiStateString = "BAD";
                    break;
            }
            Log.e("INTENT AP", wifiStateString);
        }

        private void handleSupplicantStateChanged(SupplicantState state, boolean hasError, int error) {
            if (hasError) {
                TextView myTextView = (TextView) findViewById(R.id.textView2);
                myTextView.setText(Constants.QLEEK_SETUP_INCORRECT_PASSWORD);
                server.connected = false;
                turnOnOffHotspot(this, true);
            } else {
                Log.e("INTENT SUPPLICANT", "" + state.toString());
            }
        }

        private void handleNetworkStateChanged(NetworkInfo networkInfo) {
            if (mWifiManager.isWifiEnabled()) {
                WifiInfo info = mWifiManager.getConnectionInfo();
                if(networkInfo.getDetailedState().toString().equals("CONNECTED")) {
                    SharedPreferences settings = this.getSharedPreferences(Constants.DEVICE_STATE, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(Constants.DEVICE_STATE_SETUP, false);
                    editor.commit();
//                    Intent i = new Intent(this, PlayerActivity.class);
//                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    this.startActivity(i);
                }
                if(networkInfo.getDetailedState().toString().contains("DISCONNECTED")) {
                    SharedPreferences settings = this.getSharedPreferences(Constants.DEVICE_STATE, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(Constants.DEVICE_STATE_SETUP, true);
                    editor.commit();
                }
            }
        }

        public static boolean isWifiOn (Context context) {
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            if (wifiManager.isWifiEnabled() == false)
                return false;
            else return true;
        }

        public static void turnOnOffWifi(Context context, boolean isTurnToOn) {
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);

            wifiManager.setWifiEnabled(isTurnToOn);
        }

        public static void turnOnOffHotspot(Context context, boolean isTurnToOn) {
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiApControl apControl = WifiApControl.getApControl(wifiManager);

            if (apControl != null) {
                if(isTurnToOn) {
                    // Turning AP On
                    if(apControl.isWifiApEnabled())
                    {

                    }
                    else
                    {
                        if (isWifiOn(context) && isTurnToOn) {
                            turnOnOffWifi(context, false);
                        }
                        apControl.setWifiApEnabled(apControl.getWifiApConfiguration(),
                                true);
                    }
                }
                else {
                    if(!apControl.isWifiApEnabled())
                    {
                    }
                    else
                    {
                        apControl.setWifiApEnabled(apControl.getWifiApConfiguration(),
                                false);
                    }
                }
            }
        }

        public static void getWifiStatus(Context context){
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo();
        }

        public void SetWifiConfiguration(String ssid, String pwd) {
            AP = true;
            turnOnOffHotspot(this, false);
            for (int i = 0; i < networks.size(); i++) {
                wifiNetwork net = (wifiNetwork) networks.toArray()[i];
                if(ssid.equalsIgnoreCase(net.ssid))
                {
                    if (getSecurity(net.security).equalsIgnoreCase("WEP")) {
                        addWifiConfig(ssid, pwd, "WEP", Constants.NETWROK_ADDITIONAL_SECURITY_WEP);
                    } else if (getSecurity(net.security).equalsIgnoreCase("NONE")) {
                        addWifiConfig(ssid, "", "NONE", Constants.NETWROK_ADDITIONAL_SECURITY_NONE);
                    } else if (getSecurity(net.security).equalsIgnoreCase("PSK")) {
                        addWifiConfig(ssid, pwd, "WPA2", "");
                    } else if (getSecurity(net.security).equalsIgnoreCase("EAP")) {
                        addWifiConfig(ssid, pwd, "WPA2", "");
                    }
                }
            }
        }

        static String getSecurity(String security) {
            if (security.contains("WEP")) {
                return "WEP";
            } else if (security.contains("PSK")) {
                return "PSK";
            } else if (security.contains("EAP")) {
                return "EAP";
            }
            return "NONE";
        }

        public void addWifiConfig(String ssid, String password, String securityParam, String securityDetailParam) {
            if (ssid == null) {
                throw new IllegalArgumentException(
                        "Required parameters can not be NULL #");
            }

            String wifiName = ssid;
            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = Constants.BACKSLASH + wifiName + Constants.BACKSLASH;

            String security = securityParam;

            if (security.equalsIgnoreCase("WEP")) {
                conf.wepKeys[0] = password;
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            } else if (security
                    .equalsIgnoreCase("NONE")) {
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            } else if ("WPA"
                    .equalsIgnoreCase(security)
                    || "WPA2"
                    .equalsIgnoreCase(security)
                    || "WPA/WPA2 PSK"
                    .equalsIgnoreCase(security)) {
                // appropriate ciper is need to set according to security type used,
                // ifcase of not added it will not be able to connect
                conf.preSharedKey = Constants.BACKSLASH
                        + password + Constants.BACKSLASH;
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                conf.status = WifiConfiguration.Status.ENABLED;
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                conf.allowedPairwiseCiphers
                        .set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedPairwiseCiphers
                        .set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            }

            String securityDetails = securityDetailParam;

            if (securityDetails
                    .equalsIgnoreCase(Constants.NETWROK_ADDITIONAL_SECURITY_TKIP)) {
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                conf.allowedPairwiseCiphers
                        .set(WifiConfiguration.PairwiseCipher.TKIP);
            } else if (securityDetails
                    .equalsIgnoreCase(Constants.NETWROK_ADDITIONAL_SECURITY_AES)) {
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedPairwiseCiphers
                        .set(WifiConfiguration.PairwiseCipher.CCMP);
            } else if (securityDetails
                    .equalsIgnoreCase(Constants.NETWROK_ADDITIONAL_SECURITY_WEP)) {
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            } else if (securityDetails
                    .equalsIgnoreCase(Constants.NETWROK_ADDITIONAL_SECURITY_NONE)) {
                conf.allowedPairwiseCiphers
                        .set(WifiConfiguration.PairwiseCipher.NONE);
            }

            int newNetworkId =  mWifiManager.addNetwork(conf);
            mWifiManager.saveConfiguration();
            mWifiManager.enableNetwork(newNetworkId, true);

            mWifiManager.setWifiEnabled(true);
        }

        private ServiceConnection mConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
//                WebServerService.LocalBinder binder = (WebServerService.LocalBinder) service;
//                wsService = binder.getService();
                mBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mBound = false;
            }
        };
    }


