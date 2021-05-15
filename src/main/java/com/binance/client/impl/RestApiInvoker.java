package com.binance.client.impl;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.binance.client.exception.BinanceApiException;
import com.binance.client.impl.utils.JsonWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.CookieManager;
import java.net.InetSocketAddress;
import java.net.Proxy;

@Component
public class RestApiInvoker {

    private static final Logger log = LoggerFactory.getLogger(RestApiInvoker.class);
    //设置代理
    public static String ip;
    public static  int port;
    @Value("${binance.proxy.ip}")
    public void setIp(String ip) {
        RestApiInvoker.ip = ip;
    }
    @Value("${binance.proxy.port}")
    public void setPort(int port) {
        RestApiInvoker.port = port;
    }
    private static Proxy proxy;
    private static OkHttpClient client ;

    @PostConstruct
    public void init(){
        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
        client = new OkHttpClient.Builder().proxy(proxy).build();
    }

    static void checkResponse(JsonWrapper json) {
        try {
            if (json.containKey("success")) {
                boolean success = json.getBoolean("success");
                if (!success) {
                    String err_code = json.getStringOrDefault("code", "");
                    String err_msg = json.getStringOrDefault("msg", "");
                    if ("".equals(err_code)) {
                        throw new BinanceApiException(BinanceApiException.EXEC_ERROR, "[Executing] " + err_msg);
                    } else {
                        throw new BinanceApiException(BinanceApiException.EXEC_ERROR,
                                "[Executing] " + err_code + ": " + err_msg);
                    }
                }
            } else if (json.containKey("code")) {

                int code = json.getInteger("code");
                if (code != 200) {
                    String message = json.getStringOrDefault("msg", "");
                    throw new BinanceApiException(BinanceApiException.EXEC_ERROR,
                            "[Executing] " + code + ": " + message);
                }
            }
        } catch (BinanceApiException e) {
            throw e;
        } catch (Exception e) {
            throw new BinanceApiException(BinanceApiException.RUNTIME_ERROR,
                    "[Invoking] Unexpected error: " + e.getMessage());
        }
    }

    static <T> T callSync(RestApiRequest<T> request) {
        try {
            String str;
            log.info("Request URL " + request.request.url());
            Response response = client.newCall(request.request).execute();
            // System.out.println(response.body().string());
            if (response != null && response.body() != null) {
                str = response.body().string();
                response.close();
            } else {
                throw new BinanceApiException(BinanceApiException.ENV_ERROR,
                        "[Invoking] Cannot get the response from server");
            }
            log.info("Response =====> " + str);
            JsonWrapper jsonWrapper = JsonWrapper.parseFromString(str);
            checkResponse(jsonWrapper);
            return request.jsonParser.parseJson(jsonWrapper);
        } catch (BinanceApiException e) {
            throw e;
        } catch (Exception e) {
            throw new BinanceApiException(BinanceApiException.ENV_ERROR,
                    "[Invoking] Unexpected error: " + e.getMessage());
        }
    }

    static WebSocket createWebSocket(Request request, WebSocketListener listener) {
        return client.newWebSocket(request, listener);
    }

    public static void print(){
        log.info(ip);
        log.info(port+"");
        log.info(proxy.toString());
        log.info(client.proxy().toString());

    }
}
