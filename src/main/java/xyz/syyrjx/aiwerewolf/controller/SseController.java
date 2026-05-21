package xyz.syyrjx.aiwerewolf.controller;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import xyz.syyrjx.aiwerewolf.utils.SseUtil;

import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Classname SseController
 * @Description Sse
 * @Date 2026/5/21 15:12
 * @Created by magel
 */
@RestController

public class SseController {
    @CrossOrigin
    @GetMapping(value = "/sse/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamTime(@RequestParam String userId) {
        // 设置超时时间为0表示永不超时（生产环境建议设置合理的超时时间，如30000L）
        SseEmitter emitter = new SseEmitter(0L);
        SseUtil.addSseEmitter(userId, emitter);

//        // 使用定时任务模拟持续推送数据
//        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
//        scheduler.scheduleAtFixedRate(() -> {
//            try {
//                String data = "当前服务器时间：" + LocalTime.now();
//                // 推送数据给客户端
//                emitter.send(data);
//            } catch (IOException e) {
//                // 推送失败（通常是因为客户端断开了连接），结束推送并关闭线程池
//                emitter.completeWithError(e);
//                scheduler.shutdown();
//            }
//        }, 0, 1, TimeUnit.SECONDS);

        // 连接完成或超时时，清理资源
        emitter.onCompletion(() -> {
            SseUtil.removeSseEmitter(userId);
            System.out.println("SSE连接已结束");
//            scheduler.shutdown();
        });
        emitter.onTimeout(() -> {
            SseUtil.removeSseEmitter(userId);
            System.out.println("SSE连接超时");
//            scheduler.shutdown();
        });

        return emitter;
    }
}
