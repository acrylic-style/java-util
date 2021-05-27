package test.util.rest;

import org.json.JSONObject;
import util.rest.RESTAPI;

// this isn't unit test because it requires network connectivity.
public class RESTAPITest {
    public static void main(String[] args) {
        new RESTAPI("https://next.acrylicstyle.xyz/api/ezpp-changelog.json")
            .call()
            .then(res -> res.getResponse(new JSONObject()))
            .then(obj -> obj.getJSONArray("groups"))
            .then(arr -> arr.getJSONObject(0))
            .thenDo(obj -> System.out.println(obj.getString("display_name")));
        // output: ezpp!
    }
}
