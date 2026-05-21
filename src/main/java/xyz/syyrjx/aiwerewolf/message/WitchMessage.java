package xyz.syyrjx.aiwerewolf.message;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.syyrjx.aiwerewolf.player.Player;

import java.util.List;

/**
 * @Classname WitchMessage
 * @Description 女巫消息
 * @Date 2026/5/20 17:29
 * @Created by magellang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WitchMessage {
    private Integer potion;
    private Integer target;
    private String monologue = "";

    public String info(Player player, List<Player> playerList) {
        StringBuilder sb = new StringBuilder();
        if (potion == -1) {
            sb.append(player.identity() + "没有使用药水");
        } else if (potion == 0) {
            sb.append(player.identity() + "对" + playerList.get(target).identity() + "使用了解药");
        } else if (potion == 1) {
            sb.append(player.identity() + "对" + playerList.get(target).identity() + "使用毒药");
        }
        sb.append("\n").append("心理活动：").append(monologue);
        return sb.toString();
    }
}
