package xyz.syyrjx.aiwerewolf;


import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import xyz.syyrjx.aiwerewolf.message.*;
import xyz.syyrjx.aiwerewolf.player.*;
import xyz.syyrjx.aiwerewolf.utils.JsonPrompt;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @Classname Game
 * @Description 控制台游戏类
 * @Date 2026/5/20 15:09
 * @Created by magellan
 */
public class GameConsole {
    public static void main(String[] args) throws IOException {
        // 角色位置洗牌
        List<Player> tempPlayerList = List.of(
                new Commoner(),
                new Commoner(),
                new Commoner(),
                new Wolf(),
                new Wolf(),
                new Witch(),
                new Seer()
        );

        List<Player> playerList = new ArrayList<>(tempPlayerList);
        Collections.shuffle(playerList);

        // 统计狼玩家位置
        List<Integer> wolfNumberList = new ArrayList<>();
        List<Player> wolfPlayerList = new ArrayList<>();
        // 统计平民玩家位置
        List<Player> commonerPlayerList = new ArrayList<>();
        // 统计女巫位置
        List<Player> witchPlayerList = new ArrayList<>();
        // 统计预言家位置
        List<Player> seerPlayerList = new ArrayList<>();
        for (int i = 0; i < playerList.size(); i++) {
            if ("狼人".equals(playerList.get(i).getRole())) {
                wolfNumberList.add(i);
                wolfPlayerList.add(playerList.get(i));
                continue;
            }
            if ("平民".equals(playerList.get(i).getRole())) {
                commonerPlayerList.add(playerList.get(i));
                continue;
            }
            if ("女巫".equals(playerList.get(i).getRole())) {
                witchPlayerList.add(playerList.get(i));
                continue;
            }
            if ("预言家".equals(playerList.get(i).getRole())) {
                seerPlayerList.add(playerList.get(i));
            }
        }

//        boolean isWolfHuman = false;
//        for (Player player : wolfPlayerList) {
//            if (player.isPersonFlay()) {
//                isWolfHuman = true;
//            }
//        }
//        if (!isWolfHuman) {
//            System.exit(0);
//        }

        ((Wolf)playerList.get(wolfNumberList.get(0))).setTeammateNumber(wolfNumberList.get(1));
        ((Wolf)playerList.get(wolfNumberList.get(1))).setTeammateNumber(wolfNumberList.get(0));

        Map<String, List<Player>> rolePlayerMap = Map.of(
                "狼人", wolfPlayerList,
                "平民", commonerPlayerList,
                "女巫", witchPlayerList,
                "预言家", seerPlayerList
        );

        // 创建agent和人类玩家
        int humanIndex = new Random().nextInt(playerList.size());
//        int humanIndex = 0;
//        for (int i = 0; i < playerList.size(); i++) {
//            if (playerList.get(i).getRole().equals("狼人")) {
//                humanIndex = i;
//                break;
//            }
//        }
        for (int i = 0; i < playerList.size(); i++) {
            playerList.get(i).setNumber(i);
            if (i == humanIndex) {
                playerList.get(i).setPersonFlay(true);
                if (playerList.get(i).getRole().equals("狼人")) {
                    System.out.println("您是" + i + "号玩家,身份是" + playerList.get(i).getRole() + "你的队友是" + ((Wolf)playerList.get(i)).getTeammateNumber());
                }else {
                    System.out.println("您是" + i + "号玩家,身份是" + playerList.get(i).getRole());
                }

            }
            playerList.get(i).createAgent("model" + i, "deepseek-v4-pro");
        }

        try (BufferedWriter logger = new BufferedWriter(new FileWriter("D:\\temp\\drama.txt", true))) {

            int turn = 0;
            String stage = "";
            while(true) {
                Map<String, Integer> roleAliveMap = aliveRole(playerList);
                turn++;
                Set<Player> dieInCurrentTurnList = new HashSet<>();

                GameConsole.log(logger, "+++++++++++++++++++++++++++++++++++++++第" + turn + "天++++++++++++++++++++++++++++++++++++++++", true);
                // 狼人阶段
                stage = "第" + turn + "天狼人行动阶段";
                GameConsole.log(logger, "==========================================狼人阶段==========================================", true);
                List<Player> wolfList = rolePlayerMap.get("狼人");
                int killNumber = -1;
                if (roleAliveMap.get("狼人") > 1) {
                    int[] killNumberList = new int[]{-2, -2};
                    String[] killDialogList = new String[]{"你的队友还没说话", "你的队友还没说话"};
                    int wolfIndex = 0;
                    while (true) {
                        wolfIndex = wolfIndex % 2;
                        int teammateIndex = (wolfIndex + 1) % 2;
                        Player wolf = wolfList.get(wolfIndex);
                        String dialog = killDialogList[teammateIndex];

                        String text = wolf.chat(JsonPrompt.WOLF_SPECIAL_OUTPUT, "现在是" + stage + "，你的行动阶段, 你的队友对你说:" + dialog + ", 请选择一名玩家击杀或选择空刀");
                        WolfMessage wolfMessage = text2Obj(text, WolfMessage.class);
                        log(logger, wolf.identity() + "行动阶段");
                        log(logger, wolfMessage.info(wolf, playerList));
                        killNumberList[wolfIndex] = wolfMessage.getVote();
                        killDialogList[wolfIndex] = wolfMessage.getPrivateMessage();


                        if ((killNumberList[0] == killNumberList[1]) ) {
                            // 第一轮不允许空刀
                            if (turn == 1 && killNumberList[0] == -1 && killNumberList[1] == -1) {
                                continue;
                            }
                            killNumber = killNumberList[0];
                            break;
                        }
                        wolfIndex++;
                    }
                    log(logger, wolfList.get(0).identity() + "和" +
                            wolfList.get(1).identity() + "投票击杀: " +
                            playerList.get(killNumber).identity());
                } else {
                    Player wolf = wolfList.get(0);
                    if (!wolf.isAlive()) {
                        wolf = wolfList.get(1);
                    }
                    String text = wolf.chat(JsonPrompt.WOLF_SPECIAL_OUTPUT, "现在是" + stage + "，你的行动阶段, 现在只有你一个狼人了，请选择一名玩家击杀或选择空刀");
                    WolfMessage wolfMessage = text2Obj(text, WolfMessage.class);
                    log(logger, wolf.identity() + "行动阶段");
                    log(logger, wolfMessage.info(wolf, playerList));
                    killNumber = wolfMessage.getVote();
                    log(logger, wolf.identity() + "击杀: " + playerList.get(killNumber).identity());
                }

                // 记录死亡玩家
                if (killNumber != -1) {
                    Player diePlayer = playerList.get(killNumber);
                    diePlayer.setAlive(false);
                    dieInCurrentTurnList.add(diePlayer);
                }

                // 女巫阶段
                stage = "第" + turn + "天女巫行动阶段";
                GameConsole.log(logger, "=========================================女巫阶段==========================================", true);
                Player witch = rolePlayerMap.get("女巫").get(0);
                if (witch.isAlive() || dieInCurrentTurnList.contains(witch)) {
                    String text = witch.chat(JsonPrompt.WITCH_SPECIAL_OUTPUT, "现在是" + stage + "，你的行动阶段, 本回合被狼人击杀的玩家编号为" + killNumber);
                    WitchMessage witchMessage = text2Obj(text, WitchMessage.class);
                    log(logger, witch.identity() + "行动阶段");
                    log(logger, witchMessage.info(witch, playerList));
                    if (witchMessage.getPotion() == 1) {
                        dieInCurrentTurnList.add(playerList.get(witchMessage.getTarget()));
                        playerList.get(witchMessage.getTarget()).setAlive(false);
                    }
                    if (witchMessage.getPotion() == 0) {
                        dieInCurrentTurnList.remove(playerList.get(witchMessage.getTarget()));
                        playerList.get(witchMessage.getTarget()).setAlive(true);
                    }
                }else {
                    log(logger, witch.identity() + "已死亡");
                }

                // 预言家阶段
                stage = "第" + turn + "天预言家行动阶段";
                GameConsole.log(logger, "=========================================预言家阶段==========================================", true);
                Player seer = rolePlayerMap.get("预言家").get(0);
                if (seer.isAlive() || dieInCurrentTurnList.contains(seer)) {
                    String text = seer.chat(JsonPrompt.SEER_SPECIAL_OUTPUT, "现在是" + stage + "，你的行动阶段, 请选择想要查验的玩家编号");
                    SeerMessage seerMessage = text2Obj(text, SeerMessage.class);
                    log(logger, seer.identity() + "行动阶段");
                    log(logger, seerMessage.info(seer, playerList));
                    stage = "无需格式化阶段";
                    Player target = playerList.get(seerMessage.getVote());
                    String resp = seer.chat("", "您选择的玩家" + target.getNumber() + "号玩家是" + target.getRole());
                    log(logger, resp);
                }else {
                    log(logger, seer.identity() + "已死亡");
                }

                //判断是否有玩家胜利
                int whoWin = whoWin(playerList);
                if (whoWin != 0) {
                    log(logger, "游戏结束，" + (whoWin == 1?"好人阵营胜利":"狼人阵营胜利"), true);
                    break;
                }

                // 发言阶段
                stage = "第" + turn + "天发言阶段";
                GameConsole.log(logger, "=========================================发言阶段==========================================", true);
                StringBuilder nightDie = new StringBuilder();
                StringBuilder historySpeech = new StringBuilder();
                Map<Integer, List<PrivateMessage>> privateMessageMap = new HashMap<>();
                if (dieInCurrentTurnList.isEmpty()) {
                    nightDie = new StringBuilder("今晚是个平安夜");
                } else {
                    nightDie = new StringBuilder("今晚有" + dieInCurrentTurnList.size() + "人死亡");
                    for (Player player : dieInCurrentTurnList) {
                        nightDie.append("，").append(player.getNumber()).append("号玩家死亡");
                    }
                }
                for (Player player : playerList) {
                    if (!player.isAlive()) {
                        continue;
                    }
                    log(logger, player.identity() + "发言阶段");
                    String text = player.chat(JsonPrompt.SPEECH_OUTPUT, nightDie + "。现在是" + stage + "，已发言的玩家说过：" + historySpeech + "，请你发言");
                    SpeechMessage speechMessage = text2Obj(text, SpeechMessage.class);
                    log(logger, speechMessage.info(player));
                    System.out.println(player.getNumber() + "号玩家说" + speechMessage.getSpeech());
                    historySpeech.append("(").append(player.getNumber()).append("号玩家说").append(speechMessage.getSpeech()).append(")");
                    for (RawPrivateMessage privateMessage : speechMessage.getPrivateMessage()) {
                        PrivateMessage privateMessage1 = new PrivateMessage();
                        privateMessage1.setFrom(player.getNumber());
                        privateMessage1.setTo(privateMessage.getId());
                        privateMessage1.setMessage(privateMessage.getMessage());
                        if (!privateMessageMap.containsKey(privateMessage1.getTo())) {
                            privateMessageMap.put(privateMessage1.getTo(), new ArrayList<>());
                        }
                        privateMessageMap.get(privateMessage1.getTo()).add(privateMessage1);
                    }
                }

                // 投票阶段
                stage = "第" + turn + "天投票阶段";
                StringBuilder voteSummary = new StringBuilder("本轮第一次投票总结：");
                int[] voteArray = new int[] {0, 0, 0, 0, 0, 0, 0};
                GameConsole.log(logger, "=========================================投票阶段==========================================", true);
                for (Player player : playerList) {
                    if (!player.isAlive()) {
                        continue;
                    }
                    log(logger, player.identity() + "投票阶段");
                    StringBuilder privateMessageToPlayer = new StringBuilder();
                    List<PrivateMessage> privateMessages = privateMessageMap.get(player.getNumber());
                    if (privateMessages != null) {
                        for (PrivateMessage privateMessage : privateMessages) {
                            privateMessageToPlayer.append("(").append(privateMessage.getFrom()).append("号玩家对你说").append(privateMessage.getMessage()).append(")");
                        }
                    }else {
                        privateMessageToPlayer.append("你收到的私信为空");
                    }
                    String text = player.chat(JsonPrompt.VOTE_OUTPUT, "现在是" + stage + "，历史发言记录：" + historySpeech + "，你收到的私信：" + privateMessageToPlayer + "。请你投票");
                    VoteMessage voteMessage = text2Obj(text, VoteMessage.class);
                    log(logger, voteMessage.info(player, playerList));
                    if (voteMessage.getVote() > -1) {
                        voteArray[voteMessage.getVote()]++;
                        voteSummary.append("(").append(player.getNumber()).append("号玩家投票").append(voteMessage.getVote()).append("号玩家").append(")");
                    }else {
                        voteSummary.append("(").append(player.getNumber()).append("号玩家投票弃票");
                    }

                }
                voteSummary.append("\n");

                StringBuilder voteResult = new StringBuilder();
                for (int i = 0; i < voteArray.length; i++) {
                    voteResult.append(playerList.get(i).identity()).append(":").append(voteArray[i]).append("票").append("\n");
                }
                log(logger, "投票结果：" + voteResult);
                int max = -1;
                List<Integer> maxIndex = new ArrayList<>();
                for (int i = 0; i < voteArray.length; i++) {
                    if (voteArray[i] > max) {
                        max = voteArray[i];
                        maxIndex.clear();
                        maxIndex.add(i);
                    } else if (voteArray[i] == max) {
                        maxIndex.add(i);
                    }
                }
                if (max != 0) {
                    // 平票决斗阶段
                    int battleCount = 1;
                    while (maxIndex.size() > 1) {
                        battleCount++;
                        stage = "第" + turn + "天第" + (battleCount - 1) + "次决斗发言阶段";
                        log(logger, "=========================================决斗阶段==========================================", true);
                        // 决斗者和投票者
                        List<Player> battlePlayerList = new ArrayList<>();
                        List<Player> votePlayerList = new ArrayList<>();
                        for (int i = 0; i < playerList.size(); i++) {
                            if (maxIndex.contains(i)) {
                                battlePlayerList.add(playerList.get(i));
                            } else {
                                if (playerList.get(i).isAlive()) {
                                    votePlayerList.add(playerList.get(i));
                                }
                            }
                        }
                        historySpeech = new StringBuilder();
                        List<Integer> battlePlayerNumberList = battlePlayerList.stream().map(Player::getNumber).toList();
                        for (Player player : battlePlayerList) {
                            log(logger, player.identity() + "决斗发言");
                            String text = player.chat(stage, "由于出现了平票情况，现在是" + stage + "，参加决斗的玩家编号为" + battlePlayerNumberList + "，现在轮到你发言， 已发言的玩家说过：" + historySpeech);
                            BattleMessage battleMessage = text2Obj(text, BattleMessage.class);
                            log(logger, battleMessage.info(player));
                            log(logger, player.getNumber() + "玩家发言：" + battleMessage.getSpeech());
                            historySpeech.append("(").append(player.getNumber()).append("号玩家说").append(battleMessage.getSpeech()).append(")");
                        }

                        stage = "第" + turn + "天第" + (battleCount - 1) + "次决斗投票阶段";
                        voteSummary.append("本轮第").append(battleCount).append("次投票总结：");
                        voteArray = new int[]{0, 0, 0, 0, 0, 0, 0};
                        log(logger, "=========================================决斗投票阶段==========================================", true);
                        for (Player player : votePlayerList) {
                            log(logger, player.identity() + "决斗投票");
                            String text = player.chat(stage, "现在是" + stage + "，请你在决斗玩家中间投出你认为是狼人的玩家，决斗玩家发言记录：" + historySpeech);
                            VoteMessage voteMessage = text2Obj(text, VoteMessage.class);
                            log(logger, voteMessage.info(player, playerList));
                            if (voteMessage.getVote() > -1) {
                                voteArray[voteMessage.getVote()]++;
                                voteSummary.append("(").append(player.getNumber()).append("号玩家投票").append(voteMessage.getVote()).append("号玩家").append(")");
                            } else {
                                voteSummary.append("(").append(player.getNumber()).append("号玩家投票弃票");
                            }

                            voteSummary.append("\n");
                            voteResult = new StringBuilder();
                            for (int i = 0; i < voteArray.length; i++) {
                                voteResult.append(playerList.get(i).identity()).append(":").append(voteArray[i]).append("票").append("\n");
                            }
                        }

                        log(logger, "投票结果：" + voteResult);
                        max = -1;
                        maxIndex = new ArrayList<>();
                        for (int i = 0; i < voteArray.length; i++) {
                            if (voteArray[i] > max) {
                                max = voteArray[i];
                                maxIndex.clear();
                                maxIndex.add(i);
                            } else if (voteArray[i] == max) {
                                maxIndex.add(i);
                            }
                        }

                    }
                    // 票出玩家
                    stage = "第" + turn + "投票总结阶段";
                    playerList.get(maxIndex.get(0)).setAlive(false);
                    stage = "";
                    for (Player player : playerList) {
                        if (!player.isAlive()) {
                            continue;
                        }
                        String text = player.chat("", maxIndex.get(0) + "号玩家被票出, 投票情况如下（本轮被每次投票情况单独成一行）：" + voteSummary);
                        log(logger, player.identity() + "收到本轮结果说：" + text);
                    }
                } else {
                    stage = "";
                    for (Player player : playerList) {
                        if (!player.isAlive()) {
                            continue;
                        }
                        String text = player.chat(stage,"所有玩家弃票");
                        log(logger, player.identity() + "收到本轮结果说：" + text);
                    }
                }



                //判断是否有玩家胜利
                whoWin = whoWin(playerList);
                if (whoWin != 0) {
                    log(logger, "游戏结束，" + (whoWin == 1?"好人阵营胜利":"狼人阵营胜利"), true);
                    break;
                }


            }

            GameConsole.log(logger, "游戏角色分配:", true);
            for (Player player : playerList) {
                GameConsole.log(logger, player.getNumber() + "号玩家: " + player.getRole() + (player.isPersonFlay()?"(人类)":"(困难的敌人)"), true);
            }
        } catch (GraphRunnerException e) {
            throw new RuntimeException(e);
        }

    }

    private static ObjectMapper mapper = new ObjectMapper();
    static {
        // 缺少 Creator 属性时不报错
        mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
        // 忽略未知属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许未转义的控制字符
        mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
    }
    private static <T> T text2Obj(String text, Class<T> clazz) throws JsonProcessingException {
        String cleanJsonStr = text.replace("```json","").replace("```","");
        return mapper.readValue(cleanJsonStr, clazz);
    }

    private static void log(BufferedWriter logger, String message, boolean console) throws IOException {
        if (console) {
            System.out.println(message);
        }
        logger.newLine();
        logger.write(message);
    }
    private static void log(BufferedWriter logger, String message) throws IOException {
        log(logger, message, false);
    }

    /**
     * @param playerList
     * @return -1: 狼人胜利， 0：继续游戏， 1：好人阵营胜利
     */
    private static int whoWin(List<Player> playerList) {
        Map<String, Integer> aliveRole = aliveRole(playerList);
        return whoWin(aliveRole);
    }

    private static int whoWin(Map<String, Integer> aliveRole) {
//        System.out.println(aliveRole);
        if (!aliveRole.containsKey("狼人")) {
            return 1;
        }
        if (!aliveRole.containsKey("平民") || !aliveRole.containsKey("神职")) {
            return -1;
        }
        return 0;
    }


    private static Map<String, Integer> aliveRole(List<Player> playerList) {
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
