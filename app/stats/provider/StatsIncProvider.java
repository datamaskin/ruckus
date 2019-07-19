package stats.provider;

import common.GlobalConstants;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import play.Play;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Created by mgiles on 5/24/14.
 */
public abstract class StatsIncProvider implements IStatProvider {
    private static final String DATATYPE = "stats";
    private static final String APIVERSION = Play.application().configuration().getString("stats.inc.api.version");
    private static final String URI = Play.application().configuration().getString("stats.inc.uri");

    protected abstract String getKey();

    protected abstract String getSecret();

    protected abstract String getSportName();

    protected abstract String getLeagueAbbreviation();

    @Override
    public String getStats(Map<String, String> params) throws Exception {
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        String sig = getSig(getKey() + getSecret() + timestamp);

        String tailArgs = "";
        String resource = null;
        String method = null;
        for (String key : params.keySet()) {
            if (key.equalsIgnoreCase(GlobalConstants.STATS_INC_KEY_RESOURCE)) {
                resource = params.get(GlobalConstants.STATS_INC_KEY_RESOURCE);
            } else if (key.equalsIgnoreCase(GlobalConstants.STATS_INC_KEY_METHOD)) {
                method = params.get(GlobalConstants.STATS_INC_KEY_METHOD);
            } else {
                tailArgs += "&" + key + "=" + params.get(key);
            }
        }

        String url = URI + "/v" + APIVERSION + "/" + DATATYPE + "/" + getSportName() + "/" + getLeagueAbbreviation().toLowerCase() + "/" + resource + "/";
        url = url + (method == null ? "" : method);
        url = url + "?api_key=" + getKey() + "&sig=" + sig + tailArgs;

        CloseableHttpClient httpclient = HttpClients.createDefault();
        final String furl = url;
        HttpGet httpget = new HttpGet(url);
        ResponseHandler<String> responseHandler = null;
        while (responseHandler == null) {
            responseHandler = new ResponseHandler<String>() {
                public String handleResponse(final HttpResponse response) throws IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        if (status == 500) {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return null;
                        } else {
                            throw new ClientProtocolException("Unexpected response status: " + status + " for URL: " + furl);
                        }
                    }
                }
            };
        }
        return httpclient.execute(httpget, responseHandler);
    }

    /**
     * @param str
     * @return
     */
    private String getSig(String str) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(str.getBytes());
            StringBuffer buffer = new StringBuffer();
            for (byte b : hash) {
                buffer.append(String.format("%02x", b));
            }
            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Your algorithm is bad, and you should feel bad.", e);
        }
    }
}
