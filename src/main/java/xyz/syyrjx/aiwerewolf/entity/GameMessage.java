package xyz.syyrjx.aiwerewolf.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname GameMessage
 * @Description 通过sse发送的游戏消息
 * @Date 2026/5/21 16:23
 * @Created by magellan
 */
@Data
public class GameMessage {
    private String type;
    private String message;

    private GameMessage() {}

    public static GameMessage system(String message) {
        GameMessage gameMessage = new GameMessage();
        gameMessage.setType("system");
        gameMessage.setMessage(message);
        return gameMessage;
    }

    public static GameMessage gamer(String  id, String message) {
        GameMessage gameMessage = new GameMessage();
        gameMessage.setType(id);
        gameMessage.setMessage(message);
        return gameMessage;
    }
}
