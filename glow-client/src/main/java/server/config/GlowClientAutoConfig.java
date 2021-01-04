package server.config;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.core.GlowHttpClient;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author yudm
 * @Date 2020/12/12 14:05
 * @Desc
 */
@Configuration
@EnableConfigurationProperties(GlowClientProperty.class)
@ConditionalOnClass(value = {CloseableHttpClient.class})
@ConditionalOnProperty(prefix = "spring.datasource", name = "glow-client", matchIfMissing = false)
public class GlowClientAutoConfig {
    @Resource
    private GlowClientProperty gcp;

    @Bean
    public GlowHttpClient glowHttpClient() {
        //使用Httpclient连接池的方式配置(推荐)，同时支持netty，okHttp以及其他http框架
        PoolingHttpClientConnectionManager phccm = new PoolingHttpClientConnectionManager();
        // 最大连接数和同路由并发数一样
        phccm.setMaxTotal(gcp.getMaxConnectPerRoute());
        // 同路由并发数
        phccm.setDefaultMaxPerRoute(gcp.getMaxConnectPerRoute());

        HttpClientBuilder hcb = HttpClientBuilder.create();
        //配置连接池
        hcb.setConnectionManager(phccm);
        //重试次数
        hcb.setRetryHandler(new DefaultHttpRequestRetryHandler(gcp.getRetryTimes(), true));
        //设置默认请求头
        hcb.setDefaultHeaders(getDefaultHeaders());
        //设置长连接保持策略
        hcb.setKeepAliveStrategy(connectionKeepAliveStrategy());

        RequestConfig.Builder rcb = RequestConfig.custom();
        rcb.setConnectionRequestTimeout(gcp.getConnectionRequestTimout());
        rcb.setSocketTimeout(gcp.getSocketTimeout());
        rcb.setConnectTimeout(gcp.getConnectTimeout());

        HttpPost hp = new HttpPost(gcp.getServerUrl());
        hp.setConfig(rcb.build());

        return new GlowHttpClient(phccm, hcb.build(), hp, gcp.getKeepAliveTime());
    }

    /**
     * 长连接保持策略
     */
    private ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
        return (response, context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && "timeout".equalsIgnoreCase(param)) {
                    try {
                        return Long.parseLong(value) * 1000;
                    } catch (NumberFormatException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            //否则使用默认长连接保持时间
            return gcp.getHttpPool().getKeepAliveTime();
        };
    }

    /**
     * 设置请求头
     */
    private List<Header> getDefaultHeaders() {
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Connection", "Keep-Alive"));
        headers.add(new BasicHeader("Content-Type", "application/json;charset=" + gcp.getHttpPool().getCharset()));
        return headers;
    }

}
