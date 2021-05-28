package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import util.promise.Promise;
import util.reflect.Ref;
import util.reflect.RefField;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * @deprecated uses broken promise, please use {@link util.rest.RESTAPI}.
 */
@Deprecated
@SuppressWarnings({ "unused", "RedundantSuppression" })
public class RESTAPI extends TypedEventEmitter<RESTAPI.RESTEvent> implements Chain<RESTAPI> {
    public final URL url;
    public final String method;
    public final RequestBody requestBody;
    private boolean doOutputError = false;

    public RESTAPI(@NotNull String url) {
        this(url, "GET", null);
    }

    public RESTAPI(@NotNull String url, @NotNull String method) {
        this(url, method, null);
    }

    public RESTAPI(@NotNull String url, @NotNull String method, @Nullable RequestBody requestBody) {
        this.url = Thrower.invoke(() -> new URL(url));
        this.method = method;
        this.requestBody = requestBody;
    }

    @Contract(pure = true)
    public boolean isDoOutputError() { return doOutputError; }

    public void setDoOutputError(boolean doOutputError) { this.doOutputError = doOutputError; }

    /**
     * @see #call(Class)
     */
    public Promise<Response<JSONObject>> call() { return call(JSONObject.class); }

    /**
     * JsonClass must have string constructor to deserialize the json.
     * @see #call()
     */
    public final <T> Promise<Response<T>> call(Class<T> jsonClass) {
        return new Promise<Response<T>>() {
            @Override
            public Response<T> apply(Object o) throws Throwable {
                URLConnection urlConnection = RESTAPI.this.url.openConnection();
                if (!(urlConnection instanceof HttpURLConnection)) {
                    throw new RuntimeException("Cannot open connection to " + urlConnection.getClass().getCanonicalName());
                }
                HttpURLConnection conn = (HttpURLConnection) urlConnection;
                new RefField<>(HttpURLConnection.class.getDeclaredField("method")).accessible(true).set(conn, RESTAPI.this.method);
                // conn.setRequestMethod(RESTAPI.this.method);
                RESTAPI.this.emit(RESTEvent.PRE_CONNECTION, conn);
                if (requestBody != null && requestBody.getMap().size() != 0)
                    requestBody.getMap().forEach(conn::addRequestProperty);
                if (requestBody != null && requestBody.getRawBody() != null) {
                    conn.setDoOutput(true);
                    byte[] bytes = requestBody.getRawBody().getBytes(StandardCharsets.UTF_8);
                    OutputStream os = conn.getOutputStream();
                    os.write(bytes);
                    os.close();
                }
                conn.connect();
                RESTAPI.this.emit(RESTEvent.POST_CONNECTION, conn);
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getResponseCode() != 200 ? conn.getErrorStream() : conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                RESTAPI.this.emit(RESTEvent.PRE_APPEND, sb);
                String output;
                while ((output = br.readLine()) != null) sb.append(output);
                RESTAPI.this.emit(RESTEvent.POST_APPEND, sb);
                try {
                    return new Response<>(conn.getResponseCode(), jsonClass.getConstructor(String.class).newInstance(sb.toString()), sb.toString());
                } catch (ReflectiveOperationException | RuntimeException e) {
                    e.printStackTrace();
                    return new Response<>(conn.getResponseCode(), null, sb.toString());
                }
            }
        };
    }

    /**
     * @deprecated Breaks on Java 12+, and not needed anymore when using {@link RESTAPI}
     */
    @Deprecated
    public static void addCustomMethods(String... methods) {
        RefField<HttpURLConnection> methodsField = Ref.getClass(HttpURLConnection.class).getDeclaredField("methods").accessible(true).removeFinal();
        String[] oldMethods = (String[]) methodsField.get(null);
        methodsField.set(null, ICollectionList.asList(oldMethods).addAll(methods).toArray(new String[0]));
    }

    public static final class Response<T> {
        private final int responseCode;
        private final T response;
        @NotNull private final String rawResponse;

        public Response(int responseCode, T response, @NotNull String rawResponse) {
            this.responseCode = responseCode;
            this.response = response;
            this.rawResponse = rawResponse;
        }

        /**
         * Get response code for this request.
         * See <a href=https://en.wikipedia.org/wiki/List_of_HTTP_status_codes>List of HTTP status codes</a> for
         * the full list of the status codes.
         * <table>
         *     <caption>Common response codes</caption>
         *     <tr>
         *         <td align=center><b>Status Code</b></td>
         *         <td align=center><b>Meaning</b></td>
         *     </tr>
         *     <tr>
         *         <td>200</td>
         *         <td><b>OK</b> - Request was successful, and may contains result if any.</td>
         *     </tr>
         *     <tr>
         *         <td>202</td>
         *         <td><b>Accepted</b> - The request has been accepted for processing, but the processing has not been completed.</td>
         *     </tr>
         *     <tr>
         *         <td>206</td>
         *         <td><b>Partial Content</b> - The server is delivering only part of the resource.</td>
         *     </tr>
         *     <tr>
         *         <td>400</td>
         *         <td><b>Bad Request</b> - The server cannot or will not process the request due to an apparent client error (e.g., malformed request syntax)</td>
         *     </tr>
         *     <tr>
         *         <td>401</td>
         *         <td><b>Unauthorized</b> - Similar to 403 (Forbidden), but specifically for use when authentication is required and has failed or not yet been provided.</td>
         *     </tr>
         *     <tr>
         *         <td>403</td>
         *         <td><b>Forbidden</b> - Request was denied by the server, or you need some permission to do this. This request should not be repeated.</td>
         *     </tr>
         *     <tr>
         *         <td>405</td>
         *         <td><b>Method Not Allowed</b> - A request method is not supported for the requested resource.</td>
         *     </tr>
         *     <tr>
         *         <td>429</td>
         *         <td><b>Too Many Requests</b> - Commonly used for rate-limiting, indicates you sent too many requests to the server.</td>
         *     </tr>
         *     <tr>
         *         <td>500</td>
         *         <td><b>Internal Server Error</b> - An server has encountered error and cannot process request.</td>
         *     </tr>
         *     <tr>
         *         <td>502</td>
         *         <td><b>Bad Gateway</b> - The server was action as a gateway or proxy and received an invalid response from the upstream server or backend.</td>
         *     </tr>
         *     <tr>
         *         <td>503</td>
         *         <td><b>Service Unavailable</b> - The server cannot handle the request at this time, usually this is a temporary state.</td>
         *     </tr>
         *     <tr>
         *         <td>520</td>
         *         <td><b>Web Server Returned an Unknown Error</b>(Cloudflare) - The origin server returned an empty, unknown, or unexpected response to Cloudflare.</td>
         *     </tr>
         *     <tr>
         *         <td>521</td>
         *         <td><b>Web Server Is Down</b>(Cloudflare) - The origin server has refused the connection from Cloudflare. Usually indicates the host is just down, or the webserver is down.</td>
         *     </tr>
         *     <tr>
         *         <td>522</td>
         *         <td><b>Connection Timed Out</b>(Cloudflare) - Cloudflare could not negotiate a TCP handshake with the origin server.</td>
         *     </tr>
         *     <tr>
         *         <td>523</td>
         *         <td><b>Origin Is Unreachable</b>(Cloudflare) - Cloudflare could not reach the origin server; for example, if the DNS records for the origin server are incorrect. This error might indicates the server/network is down.</td>
         *     </tr>
         *     <tr>
         *         <td>524</td>
         *         <td><b>A Timeout Occurred</b>(Cloudflare) - Cloudflare was able to complete a TCP connection to the origin server, but did not receive a timely HTTP response.</td>
         *     </tr>
         * </table>
         * @return the response code
         */
        @Contract(pure = true)
        public final int getResponseCode() { return responseCode; }

        /**
         * Get response of this response object. May be null if couldn't create the json instance.
         * (Must have constructor with exact 1 string argument)
         * @return the response
         */
        @Contract(pure = true)
        public final T getResponse() { return response; }

        /**
         * Get response of this response object. Value inside of this ActionableResult may be null
         * if couldn't create the json instance. (Must have constructor with exact 1 string argument)
         * @return the response
         */
        @Contract(value = "-> new", pure = true)
        @NotNull
        public final ActionableResult<T> getActionableResponse() { return ActionableResult.ofNullable(response); }

        /**
         * Returns a raw response data. Contains a raw data that is not parsed. This method never returns null,
         * if the response contains empty response, it will return an empty string.
         * @return a raw response
         */
        @NotNull
        public final String getRawResponse() { return rawResponse; }
    }

    public static final class BodyBuilder {
        private final HashMap<String, String> properties = new HashMap<>();
        public String rawBody = null;

        public BodyBuilder addRequestProperty(String key, String value) {
            properties.put(key, value);
            return this;
        }

        /**
         * Sets json object as body. This method conflicts with {@link BodyBuilder#setJSON(JSONArray)} and {@link BodyBuilder#setRawBody(String)}.
         */
        public final BodyBuilder setJSON(@NotNull JSONObject json) {
            properties.put("Content-Type", "application/json");
            this.rawBody = json.toString();
            return this;
        }

        /**
         * Sets json array as body. This method conflicts with {@link BodyBuilder#setJSON(JSONObject)} and {@link BodyBuilder#setRawBody(String)}.
         */
        public final BodyBuilder setJSON(@NotNull JSONArray json) {
            properties.put("Content-Type", "application/json");
            this.rawBody = json.toString();
            return this;
        }

        /**
         * Sets json object as body. This method conflicts with {@link BodyBuilder#setJSON(JSONArray)} and {@link BodyBuilder#setJSON(JSONObject)}.
         */
        public final BodyBuilder setRawBody(@Nullable String body) {
            properties.remove("Content-Type");
            this.rawBody = body;
            return this;
        }

        public final RequestBody build() { return new RequestBody(this.properties, this.rawBody); }
    }

    public static final class RequestBody {
        private final HashMap<String, String> map;
        private final String rawBody;

        private RequestBody(@NotNull HashMap<String, String> map, @Nullable String rawBody) {
            this.map = map;
            this.rawBody = rawBody;
        }

        @NotNull
        public final HashMap<String, String> getMap() { return this.map; }

        @Nullable
        public final String getRawBody() { return this.rawBody; }

        public final boolean isDoOutput() { return this.rawBody != null; }
    }

    public enum RESTEvent {
        /**
         * Emitted BEFORE the connection is opened.
         * {@link HttpURLConnection} will be passed as argument.
         */
        PRE_CONNECTION,

        /**
         * Emitted AFTER the connection is opened.
         * {@link HttpURLConnection} will be passed as argument.
         */
        POST_CONNECTION,

        /**
         * Emitted before appending the response.
         * {@link StringBuilder} will be passed as argument.
         */
        PRE_APPEND,

        /**
         * Emitted after appending the response.
         * {@link StringBuilder} will be passed as argument.
         */
        POST_APPEND,
    }
}
