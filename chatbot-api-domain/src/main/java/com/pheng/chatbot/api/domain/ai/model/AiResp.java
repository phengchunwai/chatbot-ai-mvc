package com.pheng.chatbot.api.domain.ai.model;


/**
 * 这里就是简单设计一下ai返回的数据接收格式 因为 ollama返回的数据太多了，所以这里只接收一个字段
 */

public class AiResp {
    private String response;

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return "AiResp{" +
                "response='" + response + '\'' +
                '}';
    }
}
