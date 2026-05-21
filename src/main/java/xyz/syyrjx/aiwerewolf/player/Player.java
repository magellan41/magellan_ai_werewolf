package xyz.syyrjx.aiwerewolf.player;


import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import lombok.Getter;
import lombok.Setter;
import xyz.syyrjx.aiwerewolf.agent.DashScopeAgent;
import xyz.syyrjx.aiwerewolf.entity.GameMessage;
import xyz.syyrjx.aiwerewolf.utils.GameUtil;
import xyz.syyrjx.aiwerewolf.utils.SseUtil;
import xyz.syyrjx.aiwerewolf.utils.SystemPrompt;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

/**
 * @Classname Player
 * @Description 玩家
 * @Date 2026/5/20 14:57
 * @Created by magellan
 */
public class Player {

    private static Scanner scanner = new Scanner(System.in);

    @Getter
    @Setter
    private int number;

    @Getter
    private String role;

    private DashScopeAgent agent;

    @Getter
    @Setter
    private boolean alive = true;

    @Getter
    @Setter
    private boolean personFlay = false;

//    public String getSystemPrompt() {
//        if (this.personFlay) {
//            String systemPrompt = SystemPrompt.HUMAN_ASSISTANT_SYSTEM_PROMPT
//                    .replace("{special_output}", SystemPrompt.SPECIAL_OUTPUT_FOR_HUMAN_ASSISTANT.get(this.getRole()));
//            return systemPrompt;
//        }
////        System.out.println(this.getRole());
//        String systemPrompt = SystemPrompt.SYSTEM_PROMPT
//                .replace("{special_output}", SystemPrompt.ROLE_SPECIAL_OUTPUT.get(this.getRole()))
//                .replace("{role}", this.getRole())
//                .replace("{number}", this.number+"");
//        return systemPrompt;
//    }

    public String getSystemPrompt() {
        if (this.personFlay) {
            String systemPrompt = SystemPrompt.HUMAN_ASSISTANT_SYSTEM_PROMPT
                    .replace("{role}", this.getRole())
                    .replace("{number}", this.number+"");
            return systemPrompt;
        }
    //        System.out.println(this.getRole());
        String systemPrompt = SystemPrompt.SYSTEM_PROMPT
                .replace("{role}", this.getRole())
                .replace("{number}", this.number+"");
        return systemPrompt;
    }

    public void createAgent(String agentName,String modelType) {
        this.agent = DashScopeAgent.createAgent(agentName, modelType, this.getSystemPrompt());
    }

    public String chat(String formate, String text) throws GraphRunnerException {
        if (isPersonFlay()) {
            System.out.println("您的回合：" + text);
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = scanner.nextLine();
                if (line.isEmpty()) {
                    break;
                }
                sb.append(line);
                sb.append("\n");
            }
            String userMessage =  sb.toString();
            if ("".equals(formate)) {
                return userMessage;
            }
            String json = agent.ask("用户说：" + userMessage + "。\n" + formate);
//            System.out.println(json);
            return json;
        }
        if ("".equals(formate)) {
            return agent.ask(text + "。\n请你回复收到，并重复收到的信息");
        }
        String json = agent.ask(text + "。\n" + formate);
//        System.out.println(json);
        return json;
    }

    public String chat(String formate, String text, String id) throws GraphRunnerException, IOException, ExecutionException, InterruptedException {
        if (isPersonFlay()) {
            System.out.println("您的回合：" + text);
            SseUtil.send(id, GameMessage.system(text + "请你发言"));
            String userMessage = SseUtil.get(id);
            if ("".equals(formate)) {
                return userMessage;
            }
            String json = agent.ask("用户说：" + userMessage + "。\n" + formate);
//            System.out.println(json);
            return json;
        }
        if ("".equals(formate)) {
            return agent.ask(text + "。\n请你回复收到，并重复收到的信息");
        }
        String json = agent.ask(text + "。\n" + formate);
//        System.out.println(json);
        return json;
    }

    public String identity() {
        return "玩家" + this.getNumber() + "号(" + this.getRole() + ")";
    }



}
