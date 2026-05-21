package xyz.syyrjx.aiwerewolf.message;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.syyrjx.aiwerewolf.player.Player;

import java.util.List;

/**
 * @Classname VoteMessage
 * @Description 投票阶段消息类
 * @Date 2026/5/20 20:21
 * @Created by magel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteMessage {
    private Integer vote;
    private String monologue = "";

    public String info(Player player, List<Player> playerList) {
        return player.identity() + (vote == -1 ? "弃票" :("投票" + playerList.get(vote).identity())) + "\n" + "心理活动：" + monologue;
    }
}
