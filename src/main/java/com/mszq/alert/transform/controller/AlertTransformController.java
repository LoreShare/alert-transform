package com.mszq.alert.transform.controller;

import com.google.gson.Gson;
import com.mszq.alert.transform.bean.AlertOutput;
import com.mszq.alert.transform.bean.InputLog;
import com.mszq.alert.transform.config.CommonConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class AlertTransformController {

    private final CommonConfig commonConfig;

    public AlertTransformController(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    private static final OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();

    private static final Gson gson = new Gson();

    @PostMapping("/transform")
    public void transform(@RequestBody(required = false) List<InputLog> inputLogs){
        for (InputLog inputLog : inputLogs) {
            AlertOutput alertOutput = inputLog.toAlertOutput(commonConfig.getCluster(), commonConfig.getSeverity());

            log.info("告警日志内容: {}", alertOutput.getCommonAnnotations().getLog());

            String json = gson.toJson(alertOutput);
            okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(json, MediaType.parse("application/json"));
            Request request = new Request.Builder().url(commonConfig.getUrl()).post(requestBody).build();
            try(Response response = client.newCall(request).execute()){
                if (response.isSuccessful()){
                    log.info("告警发送成功, {}", alertOutput.getCommonAnnotations().getLog());
                } else {
                    log.error("UniOps响应状态码异常,响应为 {}" , response);
                }
            } catch (IOException e) {
                log.error("UniOps请求异常,异常为 {}" , e.getMessage());
            }
        }
    }

    @PostMapping("/alert")
    public void alert(@RequestBody(required = false) Object body, @RequestParam Map<String,String> params){
        if (body != null){
            String jsonBody = gson.toJson(body);
            log.info("body: {}", jsonBody);
        }
        if (!params.isEmpty()){
            String jsonParam = gson.toJson(params);
            log.info("params: {}", jsonParam);
        }
    }
}
