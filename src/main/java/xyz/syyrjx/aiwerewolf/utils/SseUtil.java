package xyz.syyrjx.aiwerewolf.utils;


import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * @Classname SseUtil
 * @Description sse工具
 * @Date 2026/5/21 15:29
 * @Created by magellan
 */
public class SseUtil {

    private final static Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    public static void addSseEmitter(String id, SseEmitter sseEmitter) {
        sseEmitterMap.put(id, sseEmitter);
    }

    public static void removeSseEmitter(String id) {
        sseEmitterMap.remove(id);
    }

    public static void send(String id, Object message) throws IOException {
        SseEmitter sseEmitter = sseEmitterMap.get(id);
        if (sseEmitter != null) {
            sseEmitter.send(message);
        }
    }

    private final static Map<String, CompletableFuture<String>> completableFutureMap = new ConcurrentHashMap<>();

    public static String get(String id) throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        completableFutureMap.put(id, completableFuture);
        String s = completableFuture.get();
        return s;
    }

    public static void put(String id, String text) {
        CompletableFuture<String> completableFuture = completableFutureMap.get(id);
        completableFuture.complete(text);
    }
}
