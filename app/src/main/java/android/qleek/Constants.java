package android.qleek;

/**
 * Created by ismailsalhi on 4/23/15.
 */
public class Constants {
    // The state of the device -1 = not setup
    public static final String QLEEK_SPOTIFY_TOKEN_URL = "http://qleek-backend-staging.herokuapp.com/api/v1/player/554d5c724569bff3e425fff6/setting/spotify_token";
    public static final String SPOTIFY_TOKEN = "android.qleek.SPOTIFY_TOKEN";
    public static final String SPOTIFY_CLIENT_ID = "a25c3469a92648eab6ff4d41c4658562";
    public static final String DEVICE_STATE = " android.qleek.DEVICE_STATE";

    public static final String DEVICE_STATE_SETUP = "android.qleek.setup";
    public static final String DEVICE_STATE_PLAYER = "android.qleek.player";
    public static final String DEVICE_STATE_DISCONNECTED = "android.qleek.disconnected";

    public static final Object BACKSLASH = "\"";
    public static final String NETWROK_ADDITIONAL_SECURITY_TKIP = "TKIP";
    public static final String NETWROK_ADDITIONAL_SECURITY_AES = "AES";
    public static final String NETWROK_ADDITIONAL_SECURITY_WEP = "WEP";
    public static final String NETWROK_ADDITIONAL_SECURITY_NONE = "NONE";

    public static final String QLEEK_SETUP_HELLO_MESSAGE = "Hi I am your Qleek Player. \n Let me help you set things up. I'm preparing everything, please wait...";
    public static final String QLEEK_SETUP_HOTSPOT_INFO = "Go to Qleek.me/setup \n And follow the instructions to connect Qleek to the Web";
    public static final String QLEEK_SETUP_SELECT_HOME_WIFI = "Please select your home Wifi Network in the list.";
    public static final String QLEEK_SETUP_WEB_SERVER_PROBLEM = "It looks like we are having a web server problem. Please reboot your player. \n If the problem persists please contact us at Qleek.me/contact";
    public static final String QLEEK_SETUP_INTERNET_CONNECTION_PROBLEM = "Snap! I seem to have a problem connecting to the Internet. Please check your home Wifi. \n If the problem persists please contact us at Qleek.me/contact";
    public static final String QLEEK_SETUP_ACCESS_POINT_PROBLEM = "It looks like we are having an Access Point problem. Please reboot your player.\n If the problem persists please contact us at Qleek.me/contact";
    public static final String QLEEK_SETUP_INCORRECT_PASSWORD = "The password seems incorrect.\n Please reconnect and try again.";

    public static final String PLAYER_SETTINGS = "android.qleek.PLAYER_SETTINGS";

}
