package com.jimei.glow.client.core;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.jimei.glow.common.base.ReqInfo;
import com.jimei.glow.common.base.RspInfo;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Author yudm
 * @Date 2021/1/2 15:20
 * @Desc
 */
public class GlowHttpClient {
    //将日期类型自动转为Long类型的时间戳
    private final Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, (JsonSerializer<Date>) (date, type, jsonSerializationContext) -> new JsonPrimitive(date.getTime())).create();
    private final CloseableHttpClient httpClient;
    private final HttpPost httpPost;

    public GlowHttpClient(PoolingHttpClientConnectionManager phccm, CloseableHttpClient httpClient, HttpPost httpPost, int period) {
        this.httpClient = httpClient;
        this.httpPost = httpPost;
        if (period < 10000) {
            period = 10 * 1000;
        }
        // 定时回滚并清理失效的http连接
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(phccm::closeExpiredConnections, 20, period, TimeUnit.MILLISECONDS);
    }

    public <T> RspInfo<T> execute(ReqInfo req) {
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

            return parse(rsp.getEntity());
        } catch (IOException e) {
            throw new GlowClientException(e);
        } finally {
            release(rsp);
        }
    }

    private <T> RspInfo<T> parse(HttpEntity entity) {
        String str;
        try {
            str = EntityUtils.toString(entity);
        } catch (IOException e) {
            throw new GlowClientException(e);
        }
        return gson.fromJson(str, new TypeToken<RspInfo<T>>() {
        }.getType());
    }

   /* public static void main(String[] args) {
        *//*User user = new User();
        user.setAge(199);
        user.setName("xxx");
        user.setTime(new java.util.Date());
        System.out.println(user.getTime());
        System.out.println(user.getTime().getTime());
        System.out.println(new Time(user.getTime().getTime()));
        System.out.println(new Timestamp(user.getTime().getTime()).getTime());
        System.out.println(new Date(user.getTime().getTime()));*//*
        User user = test();
        System.out.println(user.toString());
    }*/

    /*private static RspInfo<Map<String, Object>> run(User t) {
        RspInfo<Object> rspInfo = new RspInfo<>(1, new Exception(new RuntimeException("ssssss")), t);
        System.out.println(new GsonBuilder().create().toJson(rspInfo));
        return test(new GsonBuilder().create().toJson(rspInfo));
    }*/

    /*private static <T> T test() {
        T.class.newInstance();
        Map<String, Object> map = new HashMap<>();
        return (T) new Object();
    }*/

    private void release(CloseableHttpResponse rsp) {
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
