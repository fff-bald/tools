package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.*;

/**
 * 命令执行工具类
 *
 * @author cjl
 * @since 2024/9/23 23:33
 */
public class CmdUtil {
    /**
     * 执行一个 Windows 命令，并在 60 秒内等待其完成。
     * 如果命令在 60 秒内未完成，则强制终止该命令。
     *
     * @param command 命令字符串，例如 "cmd /c dir"
     * @return 命令执行后的结果码，0通常表示成功，-1表示超时
     */
    public static int winCommand(String command) {
        LogUtil.info("【CmdUtil】winCommand 方法被调用，调用者：{}，命令：{}", getCallerClassName(), command);
        LogUtil.info("【CmdUtil】winCommand 方法开始执行，命令：{}", command);

        ProcessBuilder processBuilder = new ProcessBuilder(command.split("\\s+"));
        processBuilder.redirectErrorStream(true); // 将错误输出和标准输出合并

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(() -> {
            int exitCode = 0;
            Process process = null;
            try {
                LogUtil.info("【CmdUtil】启动进程...");
                process = processBuilder.start();

                // 读取命令输出
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // 处理或记录命令的输出
                        LogUtil.info("【CmdUtil】命令输出：{}", line);
                    }
                }

                // 等待命令执行完成
                exitCode = process.waitFor();
                LogUtil.info("【CmdUtil】命令执行完成，退出码：{}", exitCode);
            } catch (IOException e) {
                LogUtil.error("【CmdUtil】Win命令执行异常：{}"
                        , ExceptionUtil.getStackTraceAsString(e));
            } catch (InterruptedException e) {
                LogUtil.error("【CmdUtil】Win命令执行异常：{}"
                        , ExceptionUtil.getStackTraceAsString(e));
                Thread.currentThread().interrupt(); // 恢复中断状态
                if (process != null) {
                    process.destroy(); // 强制终止进程
                    LogUtil.info("【CmdUtil】进程被中断，已强制终止");
                }
            } catch (Exception e) {
                LogUtil.error("【CmdUtil】Win命令执行异常：{}"
                        , ExceptionUtil.getStackTraceAsString(e));
                if (process != null) {
                    process.destroy(); // 强制终止进程
                    LogUtil.info("【CmdUtil】进程发生异常，已强制终止");
                }
            }
            return exitCode;
        });

        int exitCode = -1;
        try {
            exitCode = future.get(60, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            LogUtil.error("【CmdUtil】Win命令执行超时，强制终止进程：{}"
                    , ExceptionUtil.getStackTraceAsString(e));
            future.cancel(true); // 取消任务
        } catch (InterruptedException | ExecutionException e) {
            LogUtil.error("【CmdUtil】Win命令执行异常：{}"
                    , ExceptionUtil.getStackTraceAsString(e));
        } finally {
            executor.shutdownNow(); // 立即关闭 ExecutorService
        }

        LogUtil.info("【CmdUtil】winCommand 方法执行结束，退出码：{}", exitCode);
        return exitCode;
    }

    // ---------- private ----------

    // 获取调用者的类名
    private static String getCallerClassName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        // 栈帧索引 3 是调用者的方法
        return stackTrace.length > 3 ? stackTrace[3].getClassName() : "Unknown";
    }

    // ---------- main ----------

    public static void main(String[] args) {
        winCommand("shutdown -s -t 60");
    }
}
