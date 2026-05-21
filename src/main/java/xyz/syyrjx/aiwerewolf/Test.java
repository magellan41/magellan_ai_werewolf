package xyz.syyrjx.aiwerewolf;


import java.util.Scanner;

/**
 * @Classname Test
 * @Description 测试
 * @Date 2026/5/20 21:13
 * @Created by magel
 */
public class Test {
    private static final Scanner scanner = new Scanner(System.in);
    private static final StringBuilder sb = new StringBuilder();
    public static void main(String[] args) {
        while (true) {
            String line = scanner.nextLine();
            if (line.isEmpty()) {
                break;
            }
            sb.append(line);
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }
}
