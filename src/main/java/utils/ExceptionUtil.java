package utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常工具类
 */
public class ExceptionUtil {
    /**
     * 将异常堆栈信息转换为字符串
     *
     * @param e 异常对象
     * @return 异常堆栈信息的字符串表示
     */
    public static String getStackTraceAsString(Exception e) {
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
            e.printStackTrace(printWriter);
            return stringWriter.toString();
        }
    }
}
