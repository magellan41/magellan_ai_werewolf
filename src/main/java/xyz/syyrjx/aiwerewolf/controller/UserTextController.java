package xyz.syyrjx.aiwerewolf.controller;


import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import xyz.syyrjx.aiwerewolf.entity.UserText;
import xyz.syyrjx.aiwerewolf.game.GameSse;
import xyz.syyrjx.aiwerewolf.utils.SseUtil;

/**
 * @Classname UserTextController
 * @Description 用户交互控制层
 * @Date 2026/5/21 16:03
 * @Created by magel
 */
@RestController
public class UserTextController {
    @CrossOrigin
    @PostMapping("/user/text")
    public ResponseEntity<String> userText(@RequestBody UserText userText) {
        System.out.println(userText);
        SseUtil.put(userText.getUserId(), userText.getText());
        return ResponseEntity.ok("success");
    }

    @CrossOrigin
    @SneakyThrows
    @PostMapping("/user/start")
    public ResponseEntity<String> userStart(@RequestBody UserText userText) {
        System.out.println(userText);
        GameSse gameSse = new GameSse(userText.getUserId());
        new Thread(gameSse::run).start();
        return ResponseEntity.ok("success");
    }
}
