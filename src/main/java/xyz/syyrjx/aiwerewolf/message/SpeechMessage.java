package xyz.syyrjx.aiwerewolf.message;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.syyrjx.aiwerewolf.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @Classname SpeechMessage
 * @Description 发言阶段消息
 * @Date 2026/5/20 18:35
 * @Created by magellan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpeechMessage {
    private String speech = "";
    private String monologue = "";
    @JsonProperty("private_message")
    private List<RawPrivateMessage> privateMessage = new ArrayList<>();

    public String info(Player player) {
        StringBuilder sb = new StringBuilder();
        sb.append(player.identity());
        sb.append("发言：").append(speech);
        sb.append("\n").append("心理活动：").append(monologue);
        sb.append("\n").append("私发消息：").append(privateMessage);
        return sb.toString();
    }
}
