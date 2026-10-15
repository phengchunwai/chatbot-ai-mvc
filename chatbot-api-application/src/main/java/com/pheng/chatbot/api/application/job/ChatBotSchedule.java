package com.pheng.chatbot.api.application.job;

import com.pheng.chatbot.api.domain.ai.IOllamaApi;
import com.pheng.chatbot.api.domain.ai.model.AiResp;
import com.pheng.chatbot.api.domain.chatcore.IChatApi;
import com.pheng.chatbot.api.domain.chatcore.model.aggregates.UnAnsweredQuestionsAggregates;
import com.pheng.chatbot.api.domain.chatcore.model.vo.Topics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;


/**
 * 把domain的零散的服务整合起来
 * 另外就是还可以把一些定时任务放到这里面还有可以处理mq信息，比如发送mq信息
 * 另外就是还可以对外提供一些接口
 *
 */
@Configuration
@EnableScheduling
public class ChatBotSchedule {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ChatBotSchedule.class);
    @Value("${chatbot-api.groupID}")
    private String groupID;
    @Value("${chatbot-api.cookie}")
    private String cookie;

    @Resource
    private IChatApi chatbotApi;

    @Resource
    private IOllamaApi ollamaApi;

    @Scheduled(cron = "0 0/3 * * *  ?")
    public void run() {
        if (new Random().nextBoolean()){
            log.info("随机打烊");
            return;
        }
        // 22点之后不回答
        GregorianCalendar calendar=new GregorianCalendar();
        int hour = calendar.get(GregorianCalendar.HOUR_OF_DAY);
        if (hour >= 22) {
            log.info("22点之后不回答");
            return;
        }
        try {
            // 调用待回答接口
            UnAnsweredQuestionsAggregates questionsAggregates = chatbotApi.queryUnAnsweredQuestionsTopicId(groupID, cookie);

            // 执行回答的逻辑
            List<Topics> topics = questionsAggregates.getResp_data().getTopics();
            if (null == topics || topics.isEmpty()) {
                log.info("没有待回答的问题");
                return;
            }
            for (Topics topic : topics) {
                String topicId = topic.getTopic_id();
                String questionText = topic.getQuestion().getText();

                AiResp result = ollamaApi.getOllamaResult(questionText);
                String response = result.getResponse();

                chatbotApi.answer(groupID, cookie, topicId, response, false);

                log.info("编号：{}，回答问题：{}", topic, questionText);
            }
        } catch (Exception e) {
            log.error("错误：{}", e.getMessage(), e);
        }
    }


}
