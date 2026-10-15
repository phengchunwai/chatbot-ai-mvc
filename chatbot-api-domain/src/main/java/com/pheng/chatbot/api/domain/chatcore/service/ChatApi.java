package com.pheng.chatbot.api.domain.chatcore.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pheng.chatbot.api.domain.chatcore.IChatApi;
import com.pheng.chatbot.api.domain.chatcore.model.aggregates.UnAnsweredQuestionsAggregates;
import com.pheng.chatbot.api.domain.chatcore.model.req.AnswerReq;
import com.pheng.chatbot.api.domain.chatcore.model.req.ReqData;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * 这里就是把 interface里面写的测试方法迁移过来，然后把需要请求接口的地址的相关参数作为参数传入
 * 返回的数据对象就是领域对象的值对象，然后利用json工具进行反序列化成模型层的数据对象
 */
@Service
public class ChatApi implements IChatApi {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ChatApi.class);
    @Override
    public UnAnsweredQuestionsAggregates queryUnAnsweredQuestionsTopicId(String groupId, String cookie) throws IOException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet("提问地址"+groupId);
        get.addHeader("cookie", "cookie信息"+cookie);
        get.addHeader("Content-Type", "application/json;charset=utf8");

        CloseableHttpResponse response = client.execute(get);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String res = EntityUtils.toString(response.getEntity());
            UnAnsweredQuestionsAggregates aggregates = JSON.parseObject(res, UnAnsweredQuestionsAggregates.class);
            return aggregates;
        } else {
            throw new RemoteException("queryUnAnsweredQuestionsTopicId err code is"+response.getStatusLine().getStatusCode());

        }

    }

    @Override
    public boolean answer(String groupId, String cookie, String topicId, String text, boolean silenced) throws IOException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost("回答地址"+topicId);
        post.addHeader("cookie", "cookie信息"+cookie);
        post.addHeader("Content-Type", "application/json;charset=utf8");
        post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
//        String json = "{\"question\":\"问题内容\"\"}";

        AnswerReq answerReq=new AnswerReq(new ReqData(text, silenced)) ;
        JSONObject jsonObject = JSONObject.parseObject(answerReq.toString());
        String json = jsonObject.toString();

        StringEntity entity = new StringEntity(json, ContentType.create("text/json", "utf-8"));
        post.setEntity(entity);
        CloseableHttpResponse response = client.execute(post);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String resJson = EntityUtils.toString(response.getEntity());
            AnswerReq parsed = JSON.parseObject(resJson, AnswerReq.class);
            return parsed.getReq_data().isSilenced();
        } else {

            throw new RemoteException("answer err code is"+response.getStatusLine().getStatusCode());
        }

    }
}
