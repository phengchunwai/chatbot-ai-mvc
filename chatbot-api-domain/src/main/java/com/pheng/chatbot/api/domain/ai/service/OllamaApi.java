package com.pheng.chatbot.api.domain.ai.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pheng.chatbot.api.domain.ai.IOllamaApi;
import com.pheng.chatbot.api.domain.ai.model.AiResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.rmi.RemoteException;

@Service
public class OllamaApi implements IOllamaApi {
    private static final Logger logger = LoggerFactory.getLogger(OllamaApi.class);

    @Override
    public AiResp getOllamaResult(String question) throws IOException {
        String apiUrl = "http://localhost:11434/api/generate";
        String modelName = "qwen2.5-coder:7b"; // 替换为你的模型名

        // fastjson 构建请求体，避免手动拼字符串
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", modelName);
        requestBody.put("prompt", question);
        requestBody.put("stream", false);

        // Hutool 链式调用，自动处理编码、连接与资源关闭
        try (HttpResponse response = HttpRequest.post(apiUrl)
                .header("Content-Type", "application/json; utf-8")
                .header("Accept", "application/json")
                .body(requestBody.toJSONString())
                .timeout(60 * 1000) // 模型推理较慢，加大超时时间
                .execute()) {

            if (response.isOk()) {
                AiResp resp = JSON.parseObject(response.body(), AiResp.class);
                return resp;

            } else {
                throw new RemoteException("answer err code is"+response.getStatus());
            }
        }
    }
}
