package xyz.syyrjx.aiwerewolf.message;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.syyrjx.aiwerewolf.player.Player;

import java.util.List;

/**
 * @Classname WolfMessage
 * @Description 狼人阶段消息
 * @Date 2026/5/20 16:59
 * @Created by magellan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WolfMessage {
    private Integer vote;
    @JsonProperty("private_message")
    private String privateMessage = "";

    public String info(Player wolf, List<Player> playerList) {
        StringBuilder sb = new StringBuilder();
        if (vote >= 0 ){
            sb.append(wolf.identity() + "投票给" + playerList.get(vote).identity());
        } else {
          sb.append(wolf.identity() + "没有投票");
        }
        sb.append("\n").append("心理活动：").append(privateMessage);
        return sb.toString();
    }
}
