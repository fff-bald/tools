package utils;

import com.alibaba.excel.EasyExcel;

import java.util.List;

public class ExcelUtil {
    // 泛型方法，T代表任意类型的数据模型
    public static <T> void writeDataToExcel(String filePath, List<T> dataList, Class<T> dataModelClass) {
        // 检查数据列表是否为空
        if (dataList == null || dataList.isEmpty()) {
            System.out.println("数据列表为空，无法写入文件！");
            return;
        }

        // 使用EasyExcel的write方法开始写入操作
        // 第一个参数是文件路径，第二个参数是数据模型类，用于指定写入时的数据类型
        EasyExcel.write(filePath, dataModelClass)
                // 调用sheet方法创建一个写入的sheet，可以指定sheet的名字
                .sheet("Sheet1")
                // 调用doWrite方法开始写入数据，传入数据列表
                .doWrite(dataList);

        // 注意：doWrite方法执行后，文件写入操作就完成了，不需要显式关闭流或连接
        System.out.println("数据写入完成，文件路径：" + filePath);
    }
}
