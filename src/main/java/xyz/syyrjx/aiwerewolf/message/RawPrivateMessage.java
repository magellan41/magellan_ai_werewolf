package xyz.syyrjx.aiwerewolf.message;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname RawPrivateMessage
 * @Description 原始私发消息
 * @Date 2026/5/20 20:12
 * @Created by magel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RawPrivateMessage {
    private Integer id;
    private String message = "";
}
