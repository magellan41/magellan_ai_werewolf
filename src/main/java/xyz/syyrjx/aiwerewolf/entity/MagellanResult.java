package xyz.syyrjx.aiwerewolf.entity;


import lombok.Data;

/**
 * @Classname MagellanResult
 * @Description 一个通用的返回结果类
 * @Date 2026/5/21 16:52
 * @Created by magellan
 */
@Data
public class MagellanResult {
    private int code;
    private String message;
    private Object data;

    private MagellanResult() {}

    public static MagellanResult success(Object data) {
        MagellanResult magellanResult = new MagellanResult();
        magellanResult.setCode(1);
        magellanResult.setMessage("success");
        magellanResult.setData(data);
        return magellanResult;
    }

    public static MagellanResult fail(String message) {
        MagellanResult magellanResult = new MagellanResult();
        magellanResult.setCode(0);
        magellanResult.setMessage(message);
        return magellanResult;
    }
}
