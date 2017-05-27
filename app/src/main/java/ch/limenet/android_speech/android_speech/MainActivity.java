package ch.limenet.android_speech.android_speech;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.HashMap;
import java.util.Locale;

import android.speech.tts.TextToSpeech;
import android.widget.TextView;

import java.io.IOException;

import android.net.wifi.WifiManager;

public class MainActivity extends AppCompatActivity {
    public static final int PORT = 8765;
    private Server server;
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

        try {
            server = new Server(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ttsEngines = new HashMap<>();
        ttsEngines.put("en", new LocalizedTTS(Locale.US, this.getApplicationContext()).tts);
        ttsEngines.put("de", new LocalizedTTS(Locale.GERMAN, this.getApplicationContext()).tts);
    }

    public void speak(String loc, String text) {
        setTextOutputLanguage(loc);
        setTextOutputText(text);
        if (Build.VERSION.SDK_INT >= 21) {
            ttsEngines.get(loc).speak(text, TextToSpeech.QUEUE_ADD, null, loc + text);
        } else {
            ttsEngines.get(loc).speak(text, TextToSpeech.QUEUE_ADD, null);
        }
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

}
