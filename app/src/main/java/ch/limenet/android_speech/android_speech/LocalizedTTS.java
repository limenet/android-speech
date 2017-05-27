package ch.limenet.android_speech.android_speech;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

class LocalizedTTS implements TextToSpeech.OnInitListener {
    private Locale loc;
    TextToSpeech tts;

    LocalizedTTS(Locale loc, Context context) {
        this.loc = loc;
        tts = new TextToSpeech(context, this);
    }

    public void onInit(int initStatus) {
        if (initStatus == TextToSpeech.SUCCESS) {
            tts.setLanguage(loc);
        }
    }
}
