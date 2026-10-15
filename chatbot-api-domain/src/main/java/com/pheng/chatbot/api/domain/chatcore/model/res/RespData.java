package com.pheng.chatbot.api.domain.chatcore.model.res;



import com.pheng.chatbot.api.domain.chatcore.model.vo.Topics;

import java.util.List;

/**

 * @description 结果数据

 */
public class RespData {

    private List<Topics> topics;

    public List<Topics> getTopics() {
        return topics;
    }

    public void setTopics(List<Topics> topics) {
        this.topics = topics;
    }

}
