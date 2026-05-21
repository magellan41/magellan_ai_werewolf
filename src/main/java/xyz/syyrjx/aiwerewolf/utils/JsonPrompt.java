package xyz.syyrjx.aiwerewolf.utils;


/**
 * @Classname JsonPrompt
 * @Description 返回的json格式提示词
 * @Date 2026/5/21 12:04
 * @Created by magellan
 */
public class JsonPrompt {
    public static final String SPEECH_OUTPUT = """
    请你严格按照如下json格式返回：
    {
        "speech": "发言的内容",
        "monologue": "你个人的内心活动，这部分不会被其它玩家看到"
        "private_message": [{{"id":被私信的玩家a号码, "message":"私信内容"}},{{"id":被私信的玩家b号码, "message":"私信内容"}}...]  如果没有要私信的玩家则返回空列表[]
    }
""";

    public static final String VOTE_OUTPUT = """
    请你严格按照如下json格式返回：
    {
        "vote": 一个数字表示你要票出的玩家编号，弃票返回-1
        "monologue": "你个人的内心活动，这部分不会被其它玩家看到"
    }
""";



    public static final String WOLF_SPECIAL_OUTPUT = """
    请你严格按照如下json格式返回：
    {
        "vote": 一个数字，如果决定了投某位玩家则返回该玩家的编号，如果决定不投则返回-1,
        "private_message": "发送给队友的消息" 如果队友已经死亡你可以返回空字符串
    }
    这个对话流程会被反复执行，直到你和队友达成一致，狼人行动阶段与队友交流阶段保持使用这个json格式。
    如果场上只有你一个狼人，你不能选择不投，只能在存活玩家中选择一位杀掉
""";

    public static final String  WITCH_SPECIAL_OUTPUT = """
    请你严格按照如下json格式返回：
    {
        "potion": 一个数字可选值为-1,0,1, -1代表不使用药，0代表救人，1代表毒人,
        "target": 一个数字可选值为当前存活的玩家编号，表示你希望使用的目标玩家，如果不使用药则返回-1,
        "monologue": "你个人的内心活动，这部分不会被其它玩家看到"
    }
    注意：整局游戏你就只有1次救人和1次毒人机会，请谨慎使用。
""";

    public static final String SEER_SPECIAL_OUTPUT = """
请你严格按照如下json格式返回：
    {
        "vote": 一个数字表示你希望查看身份的玩家编号
        "monologue": "你个人的内心活动，这部分不会被其它玩家看到"
    }
    当你获知玩家身份后只需要回复：收到,并重复收到的信息
""";
}
