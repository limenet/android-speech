package ch.limenet.android_speech.android_speech;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

class Server extends NanoHTTPD {
    private MainActivity mainActivity;

    Server(MainActivity mainActivity) throws IOException {
        super(MainActivity.PORT);
        this.mainActivity = mainActivity;
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
        mainActivity.speak(pLoc, pText);
        return new Response(Response.Status.OK, "text/plain", pText);
    }
}
