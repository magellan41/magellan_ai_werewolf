package xyz.syyrjx.aiwerewolf.message;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.syyrjx.aiwerewolf.player.Player;

import java.util.List;

/**
 * @Classname SeerMessage
 * @Description 预言家阶段消息
 * @Date 2026/5/20 18:14
 * @Created by magellan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeerMessage {
    private Integer vote;
    private String monologue = "";

    public String info(Player seer, List<Player> playerList) {
        StringBuilder sb = new StringBuilder();
        sb.append(seer.identity()).append("选择查验").append(playerList.get(vote).identity()).append("的身份\n")
                .append("心理活动：").append(monologue);
        return sb.toString();
    }
}
