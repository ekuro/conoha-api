import com.google.gson.GsonBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

/**
 * ConoHaのサーバを自動で起動するために作りました。
 * TENANT_ID, USER_NAME, PASSWORDは起動時にSystemPropertyとして指定してください。
 *
 * Created by ekuro on 2017/12/24.
 */
public class ConohaApi {

    private static final String[] END_POINTS = {"tyo1", "sin1", "sjc1"};
    private static final String APP_JSON = "application/json";
    private static final String ACCEPT_LABEL = "Accept";
    private static final String AUTH_TOKEN_LABEL = "X-Auth-Token";
    private static String TENANT_ID;
    private static String USER_NAME;
    private static String PASSWORD;
    private static GsonBuilder gsonBuilder = new GsonBuilder();

    static {
        TENANT_ID = System.getProperty("tenant");
        USER_NAME = System.getProperty("username");
        PASSWORD = System.getProperty("password");
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
    }

    public static void main(String[] args) {
        Arrays.asList(END_POINTS)
                .forEach(ConohaApi::startServerAt);
    }

    private static void startServerAt(String endPoint) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            AccessToken token = authorize(endPoint);
            Servers all = fetchServers(endPoint, token.getTokenId(), null);
            Servers actives = fetchServers(endPoint, token.getTokenId(), "active");
            Servers inActives = all.remove(actives);
            inActives.stream().forEach(server -> {
                boolean result = start(client, endPoint, token.getTokenId(), server.getId());
                System.out.println(result);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static AccessToken authorize(String endPoint) {
        Tenant tenant = new Tenant(new ConohaUser(USER_NAME, PASSWORD, TENANT_ID));
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpResponse response = executePost(client, generateAuthPath(endPoint), tenant, null);
            String responseData =
                    EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            return gsonBuilder.create().fromJson(responseData, AccessToken.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Servers fetchServers(String endPoint, String tokenId, String status) {
        String url = generateServersPath(endPoint, TENANT_ID);
        if (Objects.nonNull(status)) {
            url += "?status=" + status;
        }
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpResponse response = executeGet(client, url, tokenId);
            String responseData =
                    EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            return gsonBuilder.create().fromJson(responseData, Servers.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean start(HttpClient client, String endPoint, String tokenId, String serverId) {
        String url = generateServersPath(endPoint, TENANT_ID) + "/" + serverId + "/action";
        String json = "{\"os-start\": null}";
        HttpResponse response = executePost(client, url, json, tokenId);
        return response.getStatusLine().getStatusCode() == 202;
    }

    private static HttpResponse executeGet(HttpClient client, String url, String tokenId) {
        HttpGet request = new HttpGet(url);
        request.setHeader(ACCEPT_LABEL, APP_JSON);
        if (Objects.nonNull(tokenId)) request.setHeader(AUTH_TOKEN_LABEL, tokenId);
        try {
            return client.execute(request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static HttpResponse executePost(HttpClient client, String url, Object body, String tokenId) {
        String json = gsonBuilder.create().toJson(body);
        return executePost(client, url, json, tokenId);
    }

    private static HttpResponse executePost(HttpClient client, String url, String body, String tokenId) {
        HttpPost request = new HttpPost(url);
        try {
            StringEntity entity = new StringEntity(body);
            entity.setContentType(new BasicHeader(ACCEPT_LABEL, APP_JSON));
            request.setEntity(entity);
            if (Objects.nonNull(tokenId)) request.setHeader(AUTH_TOKEN_LABEL, tokenId);
            return client.execute(request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String generateAuthPath(String endPoint) {
        return "https://identity." + endPoint + ".conoha.io/v2.0/tokens";
    }

    private static String generateServersPath(String endPoint, String tenantId) {
        return "https://compute." + endPoint + ".conoha.io/v2/" + tenantId + "/servers";
    }
}
