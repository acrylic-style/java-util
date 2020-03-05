package util;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class JSONAPI {
    private URL url;
    private String method = "GET";
    private RequestBody requestBody = null;

    public JSONAPI(@NotNull String url) {
        this.url = RuntimeExceptionThrower.invoke(() -> new URL(url));
    }

    public JSONAPI(@NotNull String url, @NotNull String method) {
        this(url);
        this.method = method;
    }

    public JSONAPI(@NotNull String url, @NotNull String method, @NotNull RequestBody requestBody) {
        this(url, method);
        this.requestBody = requestBody;
    }

    public Response call() {
        try {
            HttpURLConnection conn = (HttpURLConnection) this.url.openConnection();
            conn.setRequestMethod(this.method);
            if (requestBody != null) requestBody.getMap().forEach(conn::addRequestProperty);
            conn.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getResponseCode() != 200 ? conn.getErrorStream() : conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) sb.append(output);
            return new Response(conn.getResponseCode(), new JSONObject(sb.toString()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Response {
        private int responseCode;
        private JSONObject response;

        public Response(int responseCode, JSONObject response) {
            this.responseCode = responseCode;
            this.response = response;
        }

        public int getResponseCode() {
            return responseCode;
        }

        public JSONObject getResponse() {
            return response;
        }
    }

    public static class BodyBuilder {
        private HashMap<String, String> properties = new HashMap<>();

        public BodyBuilder addRequestProperty(String key, String value) {
            properties.put(key, value);
            return this;
        }

        public RequestBody build() {
            return new RequestBody(this.properties);
        }
    }

    public static class RequestBody {
        private HashMap<String, String> map;

        private RequestBody(@NotNull HashMap<String, String> map) {
            this.map = map;
        }

        @NotNull
        private HashMap<String, String> getMap() {
            return this.map;
        }
    }
}
