package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 命令执行工具类
 *
 * @author cjl
 * @since 2024/9/23 23:33
 */
public class CmdUtil {
    /**
     * 执行命令行命令并返回结果码。
     *
     * @param command 命令字符串，例如 "cmd /c dir"
     * @return 命令执行后的结果码，0通常表示成功
     * @throws IOException          如果命令执行过程中发生I/O异常
     * @throws InterruptedException 如果等待命令执行完成时线程被中断
     */
    public static int winCommmand(String command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command.split("\\s+"));
        processBuilder.redirectErrorStream(true); // 将错误输出和标准输出合并

        int exitCode = 0;

        try {
            Process process = processBuilder.start();

            // 读取命令输出（如果需要的话，这里只是读取并丢弃）
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 这里可以处理或记录命令的输出，或者简单地忽略它
                    System.out.println(line);
                }
            }

            // 等待命令执行完成
            exitCode = process.waitFor();
        } catch (Exception e) {
            LogUtil.error("【CmdUtil】Win命令执行异常：{}", ExceptionUtil.getStackTraceAsString(e));
        }

        return exitCode;
    }

    // ---------- main ----------

    public static void main(String[] args) {
        winCommmand("shutdown -s -t 60");
    }
}
