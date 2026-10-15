package com.pheng.chatbot.api.domain.ai;

import com.pheng.chatbot.api.domain.ai.model.AiResp;

import java.io.IOException;

public interface IOllamaApi {



    AiResp getOllamaResult(String question) throws IOException;
}
