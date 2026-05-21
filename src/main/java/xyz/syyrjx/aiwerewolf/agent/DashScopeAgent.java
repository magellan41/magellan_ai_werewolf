package xyz.syyrjx.aiwerewolf.agent;


import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;

/**
 * @Classname DashScopeAgent
 * @Description DashScope Agent
 * @Date 2026/5/20 14:31
 * @Created by magellan
 */
public class DashScopeAgent {

    private static final DashScopeApi DASH_SCOPE_API = DashScopeApi.builder()
            .apiKey(System.getenv("DASHSCOPE_API_KEY"))
            .build();

    private ChatModel chatModel;

    private ReactAgent agent;

    private DashScopeAgent() {}

    public static DashScopeAgent createAgent(String agentName,String modelType, String systemPrompt) {
//        System.out.println(systemPrompt);
        DashScopeAgent dashScopeAgent = new DashScopeAgent();
        dashScopeAgent.chatModel = DashScopeChatModel.builder()
                .dashScopeApi(DASH_SCOPE_API)
                .defaultOptions(DashScopeChatOptions.builder()
                        .model(modelType)
                        .build())
                .build();

        dashScopeAgent.agent = ReactAgent.builder()
                .name(agentName)
                .model(dashScopeAgent.chatModel)
                .systemPrompt(systemPrompt)
                .saver(new MemorySaver())
                .build();
        return dashScopeAgent;
    }

    public String ask(String question) throws GraphRunnerException {
        AssistantMessage call = agent.call(question);
        return call.getText();

    }
}
