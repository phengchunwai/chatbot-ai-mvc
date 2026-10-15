package com.pheng.chatbot.api.test;


import com.pheng.chatbot.api.domain.ai.IOllamaApi;
import com.pheng.chatbot.api.domain.ai.model.AiResp;
import com.pheng.chatbot.api.domain.chatcore.IChatApi;
import com.pheng.chatbot.api.domain.chatcore.model.aggregates.UnAnsweredQuestionsAggregates;
import com.pheng.chatbot.api.domain.chatcore.model.vo.Topics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringRunTest {
    private static final Logger logger = LoggerFactory.getLogger(SpringRunTest.class);

    @Value("${chatbot-api.groupID}")
    private String groupID;
    @Value("${chatbot-api.cookie}")
    private String cookie;

    @Resource
    private IChatApi chatbotApi;

    @Resource
    private IOllamaApi ollamaApi;


    @Test
    public void test_chatbotApi() throws IOException {
        // 调用待回答接口
        UnAnsweredQuestionsAggregates questionsAggregates = chatbotApi.queryUnAnsweredQuestionsTopicId(groupID, cookie);
        logger.info("questionsAggregates:{}", questionsAggregates);

        // 执行回答的逻辑
        List<Topics> topics = questionsAggregates.getResp_data().getTopics();

        for (Topics topic : topics) {
            String topicId = topic.getTopic_id();
            String questionText = topic.getQuestion().getText();

            logger.info("topicId:{}, questionText:{}", topicId, questionText);
            chatbotApi.answer(groupID, cookie, topicId, questionText, false);
            // 只是后续的回答问题是要交给大模型的，这里只是把主要逻辑写出来
        }
    }


    @Test
    public void test_ollamaApi() throws IOException {
        AiResp result = ollamaApi.getOllamaResult("what is your name");
        logger.info("result:{}", result.toString());
    }

}

