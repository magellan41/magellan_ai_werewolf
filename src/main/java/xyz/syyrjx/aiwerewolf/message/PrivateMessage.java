package xyz.syyrjx.aiwerewolf.message;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname PrivateMessage
 * @Description 私发消息
 * @Date 2026/5/20 18:30
 * @Created by magellan
 */
@Data
@NoArgsConstructor
public class PrivateMessage {
    private Integer to;
    private Integer from;
    private String message = "";
}
