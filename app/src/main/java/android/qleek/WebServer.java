package android.qleek;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

public class WebServer extends NanoHTTPD
{
    public boolean connected = false;

    public static final String HOME_DIRECTORY = "data/www";
    public static final String WEB_SERVER_WIFI_ATTEMPT = "com.mikhaellopez.webserver.WebServer.WEB_SERVER_WIFI_ATTEMPT";
    public static final String WEB_SERVER_NEW_REQUEST = "com.mikhaellopez.webserver.WebServer.WEB_SERVER_NEW_REQUEST";

    public static final String
            MIME_PLAINTEXT = "text/plain",
            MIME_HTML = "text/html",
            MIME_JS = "application/javascript",
            MIME_MAP = "application/octet-stream",
            MIME_JSON = "application/json",
            MIME_ICON = "image/x-icon",
            MIME_FONT = "font/opentype",
            MIME_CSS = "text/css",
            MIME_PNG = "image/png",
            MIME_DEFAULT_BINARY = "application/octet-stream",
            MIME_XML = "text/xml";

    Context context;
    File f;
	public WebServer(int port, JSONArray wifiNetworks, Context c) throws IOException {

	    super(port, new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
//        super(port, )

//        f = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "wifi_list.json");


        context = c;

        Log.i("CACHE", context.getCacheDir().getAbsolutePath());

        f = new File(c.getCacheDir().getAbsolutePath(), "/wifi_list.json");

        try {
            FileWriter writer = new FileWriter(f);
            writer.append(wifiNetworks.toString());
            writer.flush();
            writer.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
	}

    private File myRootDir;

    @Override
    public Response serve (String uri, String method, Properties header, Properties parms, Properties files) {


        final StringBuilder buf = new StringBuilder();
        for (Map.Entry<Object, Object> kv : header.entrySet())
            buf.append(kv.getKey() + " : " + kv.getValue() + "\n");
        InputStream mbuffer = null;

      if(!connected) {
            Intent intent = new Intent(WEB_SERVER_NEW_REQUEST);
            context.sendBroadcast(intent);
            connected = true;
        }

        if(method.equalsIgnoreCase( "POST" ))
        {
            if(uri.equalsIgnoreCase("/wifi"))
            {
                Intent attemptIntent = new Intent(WEB_SERVER_WIFI_ATTEMPT);
                attemptIntent.putExtra("ssid", parms.getProperty("wifi"));
                attemptIntent.putExtra("pwd", parms.getProperty("password"));
                context.sendBroadcast(attemptIntent);
            }
        }

        try {
            if(uri!=null){
                if(uri.contains(".js")){
                    mbuffer = context.getAssets().open(uri.substring(1));
                    return new Response(HTTP_OK, MIME_JS, mbuffer);
                } else if(uri.contains(".map")){
                    mbuffer = context.getAssets().open(uri.substring(1));
                    return new Response(HTTP_OK, MIME_MAP, mbuffer);
                } else if (uri.contains(".ico")) {
                    mbuffer = context.getAssets().open(uri.substring(1));
                    return new Response(HTTP_OK, MIME_PLAINTEXT, mbuffer);
                } else if(uri.contains(".css")){
                    mbuffer = context.getAssets().open(uri.substring(1));
                    return new Response(HTTP_OK, MIME_CSS, mbuffer);

                } else if(uri.contains(".png")){
                    mbuffer = context.getAssets().open(uri.substring(1));
                    // HTTP_OK = "200 OK" or HTTP_OK = Status.OK;(check comments)
                    return new Response(HTTP_OK, MIME_PNG, mbuffer);
                } else if (uri.contains("/mnt/sdcard")){
                    Log.d("SERVEASSET", "request for media on sdCard " + uri);
                    File request = new File(uri);
                    mbuffer = new FileInputStream(request);
                    FileNameMap fileNameMap = URLConnection.getFileNameMap();
                    String mimeType = fileNameMap.getContentTypeFor(uri);

                    Response streamResponse = new Response(HTTP_OK, mimeType, mbuffer);
                    Random rnd = new Random();
                    String etag = Integer.toHexString(rnd.nextInt());
                    streamResponse.addHeader( "ETag", etag);
                    streamResponse.addHeader( "Connection", "Keep-alive");

                    return streamResponse;
                } else if (uri.contains("eot") || uri.contains("svg") || uri.contains("ttf") || uri.contains("woff") || uri.contains("woff2")) {
                    mbuffer = context.getAssets().open(uri.substring(1));
                    return new Response(HTTP_OK, MIME_FONT, mbuffer);
                } else if (uri.contains("/wifi_list")) {
                    File request = new File(context.getCacheDir() + "/wifi_list.json");
                    mbuffer = new FileInputStream(request);
                    return new Response(HTTP_OK, MIME_JSON, mbuffer);
                } else if (uri.contains("html")) {
//                    Log.d("SERVEASSET", "SERVE ::  URI " + uri);
                    mbuffer = context.getAssets().open(uri.substring(1));
                    return new Response(HTTP_OK, MIME_HTML, mbuffer);
                }
                else {
//                    Log.d("SERVEASSET", "SERVE ::  URI " + uri);
                    mbuffer = context.getAssets().open("index.html");
                    return new Response(HTTP_OK, MIME_HTML, mbuffer);
                }
            }
        } catch (IOException e) {
            Log.d("SERVEASSET", "Error opening file" + uri.substring(1));
            e.printStackTrace();
        }
        return null;
    }
}
