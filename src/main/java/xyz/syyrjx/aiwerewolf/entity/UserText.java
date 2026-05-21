package xyz.syyrjx.aiwerewolf.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname UserText
 * @Description 用户发言实体类
 * @Date 2026/5/21 16:03
 * @Created by magel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserText {
    private String userId;
    private String text;
}
