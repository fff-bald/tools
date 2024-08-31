package utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;

import java.util.List;
import java.util.Map;

public class ExcelUtil {
    /**
     * 将数据列表写入到指定的Excel文件中。
     *
     * <p>该方法使用EasyExcel库将数据列表写入到指定路径的Excel文件中。数据列表中的每个对象
     * 都应与指定的数据模型类匹配。</p>
     *
     * @param filePath 要写入的Excel文件路径
     * @param dataList 要写入的数据列表
     * @param <T>      数据模型的类型
     */
    public static <T> void writeDataToExcel(String filePath, List<T> dataList) {
        // 检查数据列表是否为空
        if (dataList == null || dataList.isEmpty()) {
            System.out.println("数据列表为空，无法写入文件！");
            return;
        }

        T t = dataList.get(0);

        // 使用EasyExcel的write方法开始写入操作
        // 第一个参数是文件路径，第二个参数是数据模型类，用于指定写入时的数据类型
        EasyExcel.write(filePath, t.getClass())
                // 调用sheet方法创建一个写入的sheet，可以指定sheet的名字
                .sheet("Sheet1")
                // 调用doWrite方法开始写入数据，传入数据列表
                .doWrite(dataList);

        // 注意：doWrite方法执行后，文件写入操作就完成了，不需要显式关闭流或连接
        System.out.println("数据写入完成，文件路径：" + filePath);
    }

    /**
     * 将数据写入到Excel文件的不同Sheet中。
     *
     * @param filePath Excel文件的路径。
     * @param dataMap  包含多个Sheet名称和数据列表的映射。每个Sheet名称对应一个数据列表，列表中的元素可以是相同类型的对象或基本数据类型。
     *                 注意：由于EasyExcel在写入时需要知道确切的数据类型，这里假设所有列表中的元素都是相同类型的对象（或基本数据类型包装类）。
     *                 如果列表中混合了不同类型的数据，可能会导致错误。
     */
    public static void writeDataToExcel(String filePath, Map<String, List<Object>> dataMap) {
        // 检查数据列表是否为空
        if (dataMap == null || dataMap.isEmpty()) {
            System.out.println("数据列表为空，无法写入文件！");
            return;
        }

        try (ExcelWriter excelWriter = EasyExcel.write(filePath).build()) {
            for (Map.Entry<String, List<Object>> entry : dataMap.entrySet()) {
                String sheetName = entry.getKey();
                List<Object> dataList = entry.getValue();

                if (dataList.isEmpty()) {
                    System.out.println("Sheet '" + sheetName + "' 为空，跳过该Sheet！");
                    continue;
                }

                // 获取数据列表的第一个元素，假设所有元素都是相同类型的
                Class<?> clazz = dataList.get(0).getClass();

                // 注意：EasyExcel的WriteSheet并没有直接提供setClazz方法。这里我们通过构建时传入Class类型信息（如果有需要的话）
                // 或者通过数据模型类的注解（如@ExcelProperty）来指定列和属性的映射。
                // 如果数据是简单类型或基本数据类型的包装类，并且不需要额外的处理（如自定义转换器），则通常不需要显式设置Class。

                // 创建并配置WriteSheet
                WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).head(clazz).build(); // 假设clazz是包含@ExcelProperty注解的类，这里仅为示例

                // 如果clazz不是一个带有@ExcelProperty注解的类，则head(clazz)可能不适用，应该省略或使用其他方式指定表头

                // 写入数据到对应的Sheet
                excelWriter.write(dataList, writeSheet);

                // 如果需要，可以在这里添加额外的Sheet配置，如列宽、行高等
                // 例如：writeSheet.setColumnWidth(...);
            }

            // ExcelWriter在try-with-resources块中自动关闭，无需显式关闭

        } catch (Exception e) {
            e.printStackTrace();
            // 可以选择抛出自定义异常或进行其他错误处理
        }

        // 注意：doWrite方法（虽然在这个例子中未直接调用，但ExcelWriter的write方法内部会执行写入逻辑）执行后，
        // 文件写入操作就完成了，try-with-resources块确保了ExcelWriter的自动关闭。
        System.out.println("数据写入完成，文件路径：" + filePath);
    }
}
