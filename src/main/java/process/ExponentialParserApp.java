package process;

import utils.CollectionUtil;
import utils.ExcelUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 中证指数xlsx解析器
 */
public class ExponentialParserApp {

    private static final String FILE_PATH = "";

    public static void main(String[] args) {
        List<Map<Integer, String>> list = ExcelUtil.readExcelToMapList(FILE_PATH, 0, 1);
        List<Map<Integer, String>> calTmpList = CollectionUtil.arrayList();
        for (Map<Integer, String> tableMap : list) {
            String s = tableMap.get(2);
            if (s.contains("全收益")) {
                continue;
            }
            calTmpList.add(tableMap);
        }
        // 计算每年统计信息
        Map<Integer, YearlyStats> result = calculateYearlyStats(calTmpList);
        // 输出结果
        printResults(result);
    }

    // ---------- private ----------

    /**
     * 计算每年的最大回撤、发生日期以及收益率
     *
     * @param dataList 包含指数数据的List，每个元素是Map<Integer, String>
     * @return 返回每年的最大回撤信息和收益率，key为年份，value为年度统计信息
     */
    public static Map<Integer, YearlyStats> calculateYearlyStats(List<Map<Integer, String>> dataList) {
        // 按年份分组数据
        Map<Integer, List<Map<Integer, String>>> yearlyData = dataList.stream()
                .filter(map -> map.get(0) != null && map.get(9) != null &&
                        !map.get(9).equals("null")) // 过滤掉日期或收盘价为null的数据
                .collect(Collectors.groupingBy(map -> {
                    String dateStr = map.get(0);
                    return Integer.parseInt(dateStr.substring(0, 4)); // 提取年份
                }));

        Map<Integer, YearlyStats> yearlyStats = CollectionUtil.hashMap();

        // 计算每年的最大回撤和收益率
        for (Map.Entry<Integer, List<Map<Integer, String>>> entry : yearlyData.entrySet()) {
            Integer year = entry.getKey();
            List<Map<Integer, String>> yearData = entry.getValue();

            // 按日期排序，确保时间顺序正确
            yearData.sort((a, b) -> a.get(0).compareTo(b.get(0)));

            // 计算最大回撤
            MaxDrawdownInfo maxDrawdownInfo = calculateMaxDrawdown(yearData);

            // 计算收益率
            double yearlyReturn = calculateYearlyReturn(yearData);

            // 保存年度统计信息
            yearlyStats.put(year, new YearlyStats(maxDrawdownInfo, yearlyReturn));
        }

        return yearlyStats;
    }

    /**
     * 计算单年度的最大回撤以及发生的日期
     *
     * @param sortedData 按日期排序的年度数据
     * @return 最大回撤信息，包括回撤比例和发生日期
     */
    private static MaxDrawdownInfo calculateMaxDrawdown(List<Map<Integer, String>> sortedData) {
        if (sortedData.isEmpty()) {
            return new MaxDrawdownInfo(0.0, null);
        }

        double maxDrawdown = 0.0;  // 最大回撤
        double runningHigh = 0.0;  // 运行过程中的最高点
        String maxDrawdownDate = null; // 最大回撤发生的日期

        for (Map<Integer, String> record : sortedData) {
            String date = record.get(0); // 获取日期
            String closePriceStr = record.get(9); // 获取收盘点位
            if (closePriceStr == null || closePriceStr.equals("null")) {
                continue;
            }

            try {
                double closePrice = Double.parseDouble(closePriceStr);

                // 更新运行最高点
                if (closePrice > runningHigh) {
                    runningHigh = closePrice;
                }

                // 计算当前回撤 = (最高点 - 当前点位) / 最高点
                if (runningHigh > 0) {
                    double currentDrawdown = (runningHigh - closePrice) / runningHigh;
                    if (currentDrawdown > maxDrawdown) {
                        maxDrawdown = currentDrawdown;
                        maxDrawdownDate = date; // 更新最大回撤发生的日期
                    }
                }
            } catch (NumberFormatException e) {
                // 忽略无法解析的数据
                System.err.println("无法解析收盘价: " + closePriceStr);
                continue;
            }
        }

        return new MaxDrawdownInfo(maxDrawdown, maxDrawdownDate);
    }

    /**
     * 计算单年度的收益率
     *
     * @param sortedData 按日期排序的年度数据
     * @return 年度收益率
     */
    private static double calculateYearlyReturn(List<Map<Integer, String>> sortedData) {
        if (sortedData.isEmpty()) {
            return 0.0;
        }

        try {
            String startPriceStr = sortedData.get(0).get(9); // 年初收盘点位
            String endPriceStr = sortedData.get(sortedData.size() - 1).get(9); // 年末收盘点位

            double startPrice = Double.parseDouble(startPriceStr);
            double endPrice = Double.parseDouble(endPriceStr);

            return (endPrice - startPrice) / startPrice;
        } catch (NumberFormatException e) {
            System.err.println("无法解析收盘价");
            return 0.0;
        }
    }

    /**
     * 格式化输出结果
     */
    public static void printResults(Map<Integer, YearlyStats> yearlyStats) {
        System.out.println("=== 各年度统计信息 ===");

        // 按年份排序输出
        yearlyStats.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    Integer year = entry.getKey();
                    YearlyStats stats = entry.getValue();
                    System.out.printf("%d年最大回撤: %.2f%%，发生日期: %s，年度收益率: %.2f%%\n",
                            year, stats.getMaxDrawdownInfo().getMaxDrawdown() * 100,
                            stats.getMaxDrawdownInfo().getDate(),
                            stats.getYearlyReturn() * 100);
                });

        // 计算总体统计
        if (!yearlyStats.isEmpty()) {
            double avgDrawdown = yearlyStats.values().stream()
                    .mapToDouble(stats -> stats.getMaxDrawdownInfo().getMaxDrawdown())
                    .average()
                    .orElse(0.0);

            double avgReturn = yearlyStats.values().stream()
                    .mapToDouble(YearlyStats::getYearlyReturn)
                    .average()
                    .orElse(0.0);

            System.out.println("\n=== 统计摘要 ===");
            System.out.printf("平均年度最大回撤: %.2f%%\n", avgDrawdown * 100);
            System.out.printf("平均年度收益率: %.2f%%\n", avgReturn * 100);
        }
    }


    /**
     * 最大回撤信息类
     */
    static class MaxDrawdownInfo {
        private double maxDrawdown; // 最大回撤比例
        private String date;       // 最大回撤发生的日期

        public MaxDrawdownInfo(double maxDrawdown, String date) {
            this.maxDrawdown = maxDrawdown;
            this.date = date;
        }

        public double getMaxDrawdown() {
            return maxDrawdown;
        }

        public String getDate() {
            return date;
        }
    }

    /**
     * 年度统计信息类
     */
    static class YearlyStats {
        private MaxDrawdownInfo maxDrawdownInfo; // 最大回撤信息
        private double yearlyReturn;            // 年度收益率

        public YearlyStats(MaxDrawdownInfo maxDrawdownInfo, double yearlyReturn) {
            this.maxDrawdownInfo = maxDrawdownInfo;
            this.yearlyReturn = yearlyReturn;
        }

        public MaxDrawdownInfo getMaxDrawdownInfo() {
            return maxDrawdownInfo;
        }

        public double getYearlyReturn() {
            return yearlyReturn;
        }
    }
}
