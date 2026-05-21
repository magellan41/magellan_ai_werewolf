package xyz.syyrjx.aiwerewolf.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import xyz.syyrjx.aiwerewolf.entity.GameMessage;
import xyz.syyrjx.aiwerewolf.player.Player;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Classname GameUtil
 * @Description 游戏工具类
 * @Date 2026/5/21 15:01
 * @Created by magel
 */
public class GameUtil {

    private static ObjectMapper mapper = new ObjectMapper();
    static {
        // 缺少 Creator 属性时不报错
        mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
        // 忽略未知属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许未转义的控制字符
        mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
    }
    public static <T> T text2Obj(String text, Class<T> clazz) throws JsonProcessingException {
        String cleanJsonStr = text.replace("```json","").replace("```","");
        return mapper.readValue(cleanJsonStr, clazz);
    }

    public static String obj2Text(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    public static void log(BufferedWriter logger, String message, boolean console, String id) throws IOException {
        if (console) {
            System.out.println(message);
        }
        if (id != null) {
            SseUtil.send(id, GameMessage.system(message));
        }
        logger.newLine();
        logger.write(message);
    }
    public static void log(BufferedWriter logger, String message) throws IOException {
        log(logger, message, false, null);
    }

    /**
     * @param playerList
     * @return -1: 狼人胜利， 0：继续游戏， 1：好人阵营胜利
     */
    public static int whoWin(List<Player> playerList) {
        Map<String, Integer> aliveRole = aliveRole(playerList);
        return whoWin(aliveRole);
    }

    public static int whoWin(Map<String, Integer> aliveRole) {
//        System.out.println(aliveRole);
        if (!aliveRole.containsKey("狼人")) {
            return 1;
        }
        if (!aliveRole.containsKey("平民") || !aliveRole.containsKey("神职")) {
            return -1;
        }
        return 0;
    }


    public static Map<String, Integer> aliveRole(List<Player> playerList) {
        // 狼人 神职 平民
        Map<String, Integer> aliveRole = new HashMap<>();

        for (Player player : playerList) {
            if (player.isAlive() && "狼人".equals(player.getRole())) {
                aliveRole.put("狼人", aliveRole.getOrDefault("狼人", 0) + 1);
            } else if (player.isAlive() && "女巫".equals(player.getRole())) {
                aliveRole.put("神职", aliveRole.getOrDefault("神职", 0) + 1);
            } else if (player.isAlive() && "预言家".equals(player.getRole())) {
                aliveRole.put("神职", aliveRole.getOrDefault("神职", 0) + 1);
            } else if (player.isAlive() && "平民".equals(player.getRole())) {
                aliveRole.put("平民", aliveRole.getOrDefault("平民", 0) + 1);
            }
        }
        return aliveRole;
    }
}
