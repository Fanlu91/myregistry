package com.flhai.myregistry.http;


import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public interface HttpInvoker {
    Logger log = LoggerFactory.getLogger(HttpInvoker.class);

    public String post(String requestString, String url);

    public String get(String url);

    HttpInvoker Default = new OkHttpInvoker(500);

    @SneakyThrows
    static <T> T httpGet(String url, Class<T> clazz) {
        log.debug(" =====>>>> httpGet: " + url);
        String respJson = Default.get(url);
        log.debug(" =====>>>> response: " + respJson);
        return JSON.parseObject(respJson, clazz);
    }

}
