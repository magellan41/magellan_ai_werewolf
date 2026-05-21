package xyz.syyrjx.aiwerewolf.player;


import lombok.Getter;
import lombok.Setter;
import xyz.syyrjx.aiwerewolf.agent.DashScopeAgent;
import xyz.syyrjx.aiwerewolf.utils.SystemPrompt;

/**
 * @Classname Wolf
 * @Description 狼玩家
 * @Date 2026/5/20 14:59
 * @Created by magellan
 */
public class Wolf extends Player{
    @Getter
    private String role = "狼人";
    // 队友编号
    @Getter
    @Setter
    private int teammateNumber;


    public String getSystemPrompt() {
        if (super.isPersonFlay()) {
            String systemPrompt = SystemPrompt.HUMAN_ASSISTANT_SYSTEM_PROMPT
                    .replace("{role}", this.getRole() + ", 用户的队友的编号为" + this.teammateNumber)
                    .replace("{number}", super.getNumber()+"");
            return systemPrompt;
        }
        String systemPrompt = SystemPrompt.SYSTEM_PROMPT
                .replace("{role}", this.getRole() + ", 你的队友的编号为" + this.teammateNumber)
                .replace("{number}", super.getNumber()+"");
        return systemPrompt;
    }
}
