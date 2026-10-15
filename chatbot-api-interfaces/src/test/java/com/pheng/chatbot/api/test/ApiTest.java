package com.pheng.chatbot.api.test;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.hc.core5.http.ParseException;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 现在企业开放api都要钱，只能模拟步骤看不见实际效果，但是可以本地搭建一套这样的小型服务来测试
 */
public class ApiTest {
    /**
     * mock 网页上的提问接口
     */
    @Test
    public void query_unanswered_questions() throws IOException {

        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet("提问地址");
        get.addHeader("cookie", "cookie信息");
        get.addHeader("Content-Type", "application/json;charset=utf8");

        CloseableHttpResponse response = client.execute(get);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String res = EntityUtils.toString(response.getEntity());
            System.out.println(res);
        } else {
            System.out.println("请求失败");
            System.out.println(response.getStatusLine().getStatusCode());
        }

    }


    @Test
    public void answer() throws IOException, ParseException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost("回答地址");
        post.addHeader("cookie", "cookie信息");
        post.addHeader("Content-Type", "application/json;charset=utf8");

        String json = "{\"question\":\"问题内容\"}";

        StringEntity entity = new StringEntity(json, ContentType.create("text/json", "utf-8"));
        post.setEntity(entity);
        CloseableHttpResponse response = client.execute(post);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String res = EntityUtils.toString(response.getEntity());
            System.out.println(res);
        } else {
            System.out.println("请求失败");
            System.out.println(response.getStatusLine().getStatusCode());
        }


    }

    /**
     * 这里直接使用本地的ollama模型来测试
     */
    @Test
    public void test_ollama() throws IOException {


//         Ollama 的本地 API 地址
        String apiUrl = "http://localhost:11434/api/generate";
        String modelName = "qwen2.5-coder:7b"; // 替换为你的模型名
        String promptText = "西南四省是哪四个，请简要回答";

        HttpURLConnection connection = null;
        try {
            URL url = new URL(apiUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // 构建 JSON 请求体（注意：简单场景下手动拼接，复杂场景建议使用 JSON 库）
            String jsonInputString = String.format(
                    "{\"model\": \"%s\", \"prompt\": \"%s\", \"stream\": false}",
                    modelName, promptText
            );

            // 发送请求
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 读取响应
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    // 简单解析返回的 JSON（实际项目中建议使用 Jackson/Gson 等库）
                    String fullResponse = response.toString();
                    System.out.println("原始响应: " + fullResponse);
                    // 你可以在这里从 fullResponse 中提取 "response" 字段的值
                }
            } else {
                System.out.println("请求失败，响应码: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    @Test
    public void test_ollama_by_hutool() {

        String apiUrl = "http://localhost:11434/api/generate";
        String modelName = "qwen2.5-coder:7b"; // 替换为你的模型名
        String promptText = "请你说一下冒牌排序的核心思想，请简要回答";

        // fastjson 构建请求体，避免手动拼字符串
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", modelName);
        requestBody.put("prompt", promptText);
        requestBody.put("stream", false);

        // Hutool 链式调用，自动处理编码、连接与资源关闭
        try (HttpResponse response = HttpRequest.post(apiUrl)
                .header("Content-Type", "application/json; utf-8")
                .header("Accept", "application/json")
                .body(requestBody.toJSONString())
                .timeout(60 * 1000) // 模型推理较慢，加大超时时间
                .execute()) {

            if (response.isOk()) {
                JSONObject result = JSON.parseObject(response.body());
                System.out.println("模型回答: " + result.getString("response"));
            } else {
                System.out.println("请求失败，响应码: " + response.getStatus());
            }
        }
    }




}
