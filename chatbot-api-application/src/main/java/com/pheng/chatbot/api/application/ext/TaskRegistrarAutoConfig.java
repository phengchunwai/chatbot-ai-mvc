package com.pheng.chatbot.api.application.ext;

import com.pheng.chatbot.api.application.job.ChatBotSchedule;
import com.pheng.chatbot.api.common.PropertyUtil;
import com.pheng.chatbot.api.domain.ai.IOllamaApi;
import com.pheng.chatbot.api.domain.chatcore.IChatApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 这个类的目的是去通过读取配置文件的里面的多组配置，然后执行定时任务来返回问题
 * 原来的定时任务类做了改造，去掉了固定的任务配置，改为动态的从配置文件中读取任务配置
 * 原来的定时任务类需要实现Runnable接口，这个类负责来读取配置文件然后解析参数，定时任务的类负责接收参数并执行任务
 *
 */
@Configuration
@EnableScheduling
public class TaskRegistrarAutoConfig implements EnvironmentAware, SchedulingConfigurer {

    private Logger logger = LoggerFactory.getLogger(TaskRegistrarAutoConfig.class);


    /**
     * 任务配置组
     */
    private Map<String, Map<String, Object>> taskGroupMap = new HashMap<>();

    @Resource
    private IChatApi chatbotApi;

    @Resource
    private IOllamaApi ollamaApi;



    @Override
    public void setEnvironment(Environment environment) {
        String prefix="chatbot-api";
        String launchList = environment.getProperty(prefix + ".launchList");
        if (StringUtils.isEmpty(launchList))return;
        for (String groupKey : launchList.split(",")) {
            Map<String,Object> taskGrouprops = PropertyUtil.handle(environment, prefix + groupKey, Map.class);
            taskGroupMap.put("groupKey", taskGrouprops);
        }

    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        Set<String> taskGroups = taskGroupMap.keySet();
        for (String groupKey : taskGroups) {
            Map<String, Object> taskGroup = taskGroupMap.get(groupKey);
            String groupName = taskGroup.get("groupName").toString();
            String groupId = taskGroup.get("groupId").toString();
            String cookie = taskGroup.get("cookie").toString();
            String openAiKey = taskGroup.get("openAiKey").toString();
            String cronExpressionBase64 = taskGroup.get("cronExpression").toString();
            String cronExpression = new String(Base64.getDecoder().decode(cronExpressionBase64), StandardCharsets.UTF_8);
            boolean silenced = Boolean.parseBoolean(taskGroup.get("silenced").toString());

            taskRegistrar.addCronTask(new ChatBotSchedule(groupName,groupId,cookie,openAiKey,chatbotApi,ollamaApi,silenced), cronExpression);
        }

    }
}
