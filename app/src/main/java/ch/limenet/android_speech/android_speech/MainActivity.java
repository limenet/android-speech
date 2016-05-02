package ch.limenet.android_speech.android_speech;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Locale;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.TextView;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import android.net.wifi.WifiManager;
import android.os.Handler;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import fi.iki.elonen.NanoHTTPD;

public class MainActivity extends AppCompatActivity implements OnInitListener {


    public static final String EXTRA_TEXT = "text";

    private TextView textView;
    private TextToSpeech textToSpeech;

    private static final int PORT = 8765;
    private static final int HTTP_OK = 200;
    private MyHTTPD server;
    private WifiManager.WifiLock wifiLock;
    private Handler handler = new Handler();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(android.R.id.text1);
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        wifiLock = wifiManager.createWifiLock("lock");
        wifiLock.acquire();

        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        final String formattedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        setText("Please access http://" + formattedIpAddress + ":" + PORT);

        try {
            server = new MyHTTPD();
        } catch (IOException e) {
            e.printStackTrace();
        }

        textToSpeech = new TextToSpeech(this, this);
        textToSpeech.setLanguage(Locale.US);
    }

    public void onInit(int initStatus) {
    }

    private void speak(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //Close the Text to Speech Library
        if (textToSpeech != null) {

            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (server != null) {
            server.stop();
        }

        if (wifiLock != null) {
            wifiLock.release();
        }
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void setText(final String t) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(t);
            }
        });
    }


    private class MyHTTPD extends NanoHTTPD {
        public MyHTTPD() throws IOException {
            super(PORT);
            start();
        }

        @Override
        public Response serve(IHTTPSession session) {
            try {
                session.parseBody(new HashMap<String, String>());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ResponseException e) {
                e.printStackTrace();
            }
            Method method = session.getMethod();
            String uri = session.getUri();
            Map<String, String> params = session.getParms();
            String pText = params.get("text");
            setText(pText);
            speak(pText);
            return new NanoHTTPD.Response(Response.Status.OK, "text/plain", pText);
        }

    }

}
