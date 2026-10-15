package com.pheng.chatbot.api.domain.chatcore;

import com.pheng.chatbot.api.domain.chatcore.model.aggregates.UnAnsweredQuestionsAggregates;

import java.io.IOException;

public interface IChatApi {

    UnAnsweredQuestionsAggregates queryUnAnsweredQuestionsTopicId(String groupId,String cookie) throws IOException;

    boolean answer(String groupId,String cookie,String topicId,String text,boolean silenced) throws IOException;
}
