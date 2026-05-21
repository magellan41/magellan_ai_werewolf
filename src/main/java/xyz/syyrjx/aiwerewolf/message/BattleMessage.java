package xyz.syyrjx.aiwerewolf.message;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.syyrjx.aiwerewolf.player.Player;

/**
 * @Classname BattleMessage
 * @Description 对决发言阶段消息类
 * @Date 2026/5/20 20:39
 * @Created by magel
 */
@Data
@NoArgsConstructor
public class BattleMessage {
    private String speech = "";
    private String monologue = "";

    public String info(Player player) {
        return player.identity() + "发言：" + speech + "\n" + "心理活动：" + monologue;
    }
}
