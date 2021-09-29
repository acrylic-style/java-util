package test.util.rest;

import org.json.JSONObject;
import org.junit.Test;
import util.http.RESTAPI;

public class RESTAPITest {
    @Test(timeout = 5000)
    public void jsonAPI() {
        String s = new RESTAPI("https://next.acrylicstyle.xyz/api/ezpp-changelog.json")
                .call()
                .then(res -> res.getResponse(new JSONObject()))
                .then(obj -> obj.getJSONArray("groups"))
                .then(arr -> arr.getJSONObject(0))
                .then(obj -> obj.getString("display_name"))
                .complete();
        assert "ezpp!".equals(s);
    }

    @Test(timeout = 5000)
    public void emptyBody() {
        JSONObject obj = new RESTAPI(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", "00000000-0000-0000-0000-000000000000"))
                .call()
                .then(RESTAPI.Response::getResponse)
                .complete();
        assert obj == null;
    }
}
