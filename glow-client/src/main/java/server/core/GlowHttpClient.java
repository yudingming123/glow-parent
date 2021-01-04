package server.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import server.base.ReqInfo;
import server.base.RspInfo;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Author yudm
 * @Date 2021/1/2 15:20
 * @Desc
 */
public class GlowHttpClient {
    /*private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Integer.class, new TypeAdapter<RspInfo<Object>>() {
                @Override
                public void write(JsonWriter jsonWriter, RspInfo<Object> objectRspInfo) throws IOException {

                }

                @Override
                public RspInfo<Object> read(JsonReader jsonReader) throws IOException {
                    return null;
                }
            }).create();*/
    private final Gson gson = new GsonBuilder().create();
    private final CloseableHttpClient httpClient;
    private final HttpPost httpPost;

    public GlowHttpClient(PoolingHttpClientConnectionManager phccm, CloseableHttpClient httpClient, HttpPost httpPost, int period) {
        this.httpClient = httpClient;
        this.httpPost = httpPost;
        if (period < 10000) {
            period = 10 * 1000;
        }
        // 定时回滚并清理过期的事务
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(phccm::closeExpiredConnections, 20, period, TimeUnit.MILLISECONDS);
    }

    public <T> RspInfo<T> doPost(ReqInfo req) {
        CloseableHttpResponse rsp = null;
        try {
            httpPost.setEntity(new StringEntity(gson.toJson(req)));
            rsp = httpClient.execute(httpPost);
            if (null == rsp) {
                throw new GlowClientException("GlowHttp no response");
            }
            int code = rsp.getStatusLine().getStatusCode();
            if (200 != code) {
                throw new GlowClientException("GlowHttp status code:" + code);
            }

            return parsEntity(rsp.getEntity());
        } catch (IOException e) {
            throw new GlowClientException(e);
        } finally {
            //终止继续读取response
            httpPost.abort();
            //关闭response
            if (null != rsp) {
                try {
                    rsp.close();
                } catch (IOException e) {
                    throw new GlowClientException(e);
                }
            }
        }
    }

    private <T> RspInfo<T> parsEntity(HttpEntity entity) {
        String str;
        try {
            str = EntityUtils.toString(entity);
        } catch (IOException e) {
            throw new GlowClientException(e);
        }
        return gson.fromJson(str, new TypeToken<RspInfo<T>>() {
        }.getType());
    }
}
