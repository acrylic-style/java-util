package util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class JSONAPI extends EventEmitter {
    private final URL url;
    private String method = "GET";
    private RequestBody requestBody = null;
    private Consumer<HttpURLConnection> postConnection = c -> {};

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

    public JSONAPI(@NotNull String url, @NotNull String method, @NotNull BodyBuilder bodyBuilder) {
        this(url, method, bodyBuilder.build());
    }

    public JSONAPI setPostConnection(@NotNull Consumer<HttpURLConnection> consumer) {
        postConnection = consumer;
        return this;
    }

    /**
     * This method will try to cast to JSONObject.<br />
     * Following events will be emitted during this call:<br />
     * <ul>
     *     <li>postConnection - before connect</li>
     *     <li>connection - after connection</li>
     * </ul>
     * @see #call(Class)
     */
    public Response call() {
        return call(JSONObject.class);
    }

    /**
     * Following events will be emitted during this call:<br />
     * <ul>
     *     <li>postConnection - before connect</li>
     *     <li>connection - after connection</li>
     * </ul>
     * @see #call()
     */
    public Response call(Class<?> jsonClass) {
        try {
            HttpURLConnection conn = (HttpURLConnection) this.url.openConnection();
            conn.setRequestMethod(this.method);
            postConnection.accept(conn);
            this.emit("postConnection", conn);
            if (requestBody != null && requestBody.getMap().size() != 0) requestBody.getMap().forEach(conn::addRequestProperty);
            if (requestBody != null && requestBody.getRawBody() != null) {
                byte[] bytes = requestBody.getRawBody().getBytes(StandardCharsets.UTF_8);
                OutputStream os = conn.getOutputStream();
                os.write(bytes);
                os.close();
            }
            conn.connect();
            this.emit("connection", conn);
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getResponseCode() != 200 ? conn.getErrorStream() : conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) sb.append(output);
            try {
                return new Response(conn.getResponseCode(), jsonClass.getConstructor(String.class).newInstance(sb.toString()), sb.toString());
            } catch (JSONException | ReflectiveOperationException ignored) {
                return new Response(conn.getResponseCode(), null, sb.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Following events will be emitted during this call:<br />
     * <ul>
     *     <li>postConnection - before connect</li>
     *     <li>connection - after connection</li>
     * </ul>
     */
    public Response callWithoutException(Class<?> jsonClass) {
        try {
            return call(jsonClass);
        } catch (RuntimeException e) { // may happen if json is written in invalid format
            e.printStackTrace();
            return null;
        }
    }

    public static class Response {
        private final int responseCode;
        private final Object response;
        private final String rawResponse;

        public Response(int responseCode, Object response, String rawResponse) {
            this.responseCode = responseCode;
            this.response = response;
            this.rawResponse = rawResponse;
        }

        public int getResponseCode() {
            return responseCode;
        }

        public Object getResponse() {
            return response;
        }

        public String getRawResponse() {
            return rawResponse;
        }
    }

    public static class BodyBuilder {
        private final HashMap<String, String> properties = new HashMap<>();
        private String rawBody = null;

        public BodyBuilder addRequestProperty(String key, String value) {
            properties.put(key, value);
            return this;
        }

        /**
         * Sets json object as body. This method conflicts with {@link BodyBuilder#setJSON(JSONArray)} and {@link BodyBuilder#setRawBody(String)}.
         */
        public BodyBuilder setJSON(@NotNull JSONObject json) {
            properties.put("Content-Type", "application/json");
            this.rawBody = json.toString();
            return this;
        }

        /**
         * Sets json array as body. This method conflicts with {@link BodyBuilder#setJSON(JSONObject)} and {@link BodyBuilder#setRawBody(String)}.
         */
        public BodyBuilder setJSON(@NotNull JSONArray json) {
            properties.put("Content-Type", "application/json");
            this.rawBody = json.toString();
            return this;
        }

        /**
         * Sets json object as body. This method conflicts with {@link BodyBuilder#setJSON(JSONArray)} and {@link BodyBuilder#setJSON(JSONObject)}.
         */
        public BodyBuilder setRawBody(@Nullable String body) {
            properties.remove("Content-Type");
            this.rawBody = body;
            return this;
        }

        public RequestBody build() {
            return new RequestBody(this.properties, this.rawBody);
        }
    }

    public static class RequestBody {
        private final HashMap<String, String> map;
        private final String rawBody;

        private RequestBody(@NotNull HashMap<String, String> map, @Nullable String rawBody) {
            this.map = map;
            this.rawBody = rawBody;
        }

        @NotNull
        private HashMap<String, String> getMap() {
            return this.map;
        }

        @Nullable
        private String getRawBody() {
            return this.rawBody;
        }
    }
}
