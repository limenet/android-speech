package ch.limenet.android_speech.android_speech;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.HashMap;
import java.util.Locale;

import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.TextView;

import java.io.IOException;
import java.util.Map;

import android.net.wifi.WifiManager;

import fi.iki.elonen.NanoHTTPD;

public class MainActivity extends AppCompatActivity {
    private static final int PORT = 8765;
    private TtsHTTP server;
    private WifiManager.WifiLock wifiLock;
    private HashMap<String, TextToSpeech> ttsEngines;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        wifiLock = wifiManager.createWifiLock("lock");
        wifiLock.acquire();

        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        final String formattedIpAddress = String.format(Locale.US, "%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        setTextStatus("Please access http://" + formattedIpAddress + ":" + PORT);

        try {
            server = new TtsHTTP();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ttsEngines = new HashMap<>();
        ttsEngines.put("en", new TtsLocale(Locale.US, this.getApplicationContext()).tts);
        ttsEngines.put("de", new TtsLocale(Locale.GERMAN, this.getApplicationContext()).tts);

    }

    private void speak(String loc, String text) {
        setTextOutputLanguage(loc);
        setTextOutputText(text);
        if (Build.VERSION.SDK_INT >= 21) {
            ttsEngines.get(loc).speak(text, TextToSpeech.QUEUE_ADD, null, loc + text);
        } else {
            ttsEngines.get(loc).speak(text, TextToSpeech.QUEUE_ADD, null);
        }
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

        if (ttsEngines != null) {
            for (TextToSpeech ttsEngine : ttsEngines.values()) {
                ttsEngine.stop();
                ttsEngine.shutdown();
            }
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

    public void setTextStatus(final String t) {
        TextView tv = (TextView) findViewById(R.id.status);
        tv.setText(t);
    }

    public void setTextOutputText(final String t) {
        TextView tv = (TextView) findViewById(R.id.outputText);
        tv.setText(t);
    }

    public void setTextOutputLanguage(final String t) {
        TextView tv = (TextView) findViewById(R.id.outputLanguage);
        tv.setText(t);
    }

    public void onClickFooter(View v) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://limenet.ch"));
        startActivity(intent);
    }


    private class TtsHTTP extends NanoHTTPD {
        TtsHTTP() throws IOException {
            super(PORT);
            start();
        }

        @Override
        public Response serve(IHTTPSession session) {
            try {
                session.parseBody(new HashMap<String, String>());
            } catch (IOException | ResponseException e) {
                e.printStackTrace();
            }
            Map<String, String> params = session.getParms();
            String pText = params.get("text");
            String pLoc = params.get("locale");
            speak(pLoc, pText);
            return new NanoHTTPD.Response(Response.Status.OK, "text/plain", pText);
        }
    }

    private class TtsLocale implements OnInitListener {
        private Locale loc;
        TextToSpeech tts;

        TtsLocale(Locale loc, Context context) {
            this.loc = loc;
            tts = new TextToSpeech(context, this);
        }

        public void onInit(int initStatus) {
            if (initStatus == TextToSpeech.SUCCESS) {
                tts.setLanguage(loc);
            }
        }
    }

}
