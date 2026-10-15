package com.pheng.chatbot.api.domain.chatcore.model.aggregates;

import com.pheng.chatbot.api.domain.chatcore.model.res.RespData;

/**
 * @description 未回答问题的聚合信息
 */

public class UnAnsweredQuestionsAggregates {
    private boolean succeeded;
    private RespData resp_data;

    public boolean isSucceeded() {
        return succeeded;
    }

    public void setSucceeded(boolean succeeded) {
        this.succeeded = succeeded;
    }

    public RespData getResp_data() {
        return resp_data;
    }

    public void setResp_data(RespData resp_data) {
        this.resp_data = resp_data;
    }

}
