package process;

import utils.CollectionUtil;
import utils.ConfigUtil;
import utils.ExcelUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 中证指数xlsx解析器
 */
public class ExponentialParserApp {

    private static final String FILE_PATH = ConfigUtil.loadConfig().getProperty("process.exponential_parser.filepath");

    public static void main(String[] args) {
        analyzeData(FILE_PATH, false);
    }

    // ---------- private ----------

    private static void analyzeData(String filePath, boolean isAll) {
        List<Map<Integer, String>> list = ExcelUtil.readExcelToMapList(filePath, 0, 1);
        List<Map<Integer, String>> calTmpList = CollectionUtil.arrayList();
        for (Map<Integer, String> tableMap : list) {
            String s = tableMap.get(2);
            if (isAll && !s.contains("全收益")) {
                // 是分析全收益 && 这条数据不是全收益数据
                continue;
            }
            if (!isAll && s.contains("全收益")) {
                // 不是分析全收益 && 这条数据是全收益数据
                continue;
            }
            calTmpList.add(tableMap);
        }
        // 计算每年统计信息
        Map<Integer, YearlyStats> result = calculateYearlyStats(calTmpList);
        // 输出结果
        printResults(result, isAll);
    }

    /**
     * 计算每年的最大回撤、发生日期以及收益率
     *
     * @param dataList 包含指数数据的List，每个元素是Map<Integer, String>
     * @return 返回每年的最大回撤信息和收益率，key为年份，value为年度统计信息
     */
    public static Map<Integer, YearlyStats> calculateYearlyStats(List<Map<Integer, String>> dataList) {
        // 先对所有数据按日期排序
        List<Map<Integer, String>> sortedAllData = dataList.stream()
                .filter(map -> map.get(0) != null && map.get(9) != null &&
                        !map.get(9).equals("null")) // 过滤掉日期或收盘价为null的数据
                .sorted((a, b) -> a.get(0).compareTo(b.get(0))) // 按日期排序
                .collect(Collectors.toList());

        // 按年份分组数据
        Map<Integer, List<Map<Integer, String>>> yearlyData = sortedAllData.stream()
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

            // 计算最大回撤（传入完整的排序数据用于跨年修复计算）
            MaxDrawdownInfo maxDrawdownInfo = calculateMaxDrawdown(yearData, sortedAllData);

            // 计算收益率
            double yearlyReturn = calculateYearlyReturn(yearData);

            // 保存年度统计信息
            yearlyStats.put(year, new YearlyStats(maxDrawdownInfo, yearlyReturn));
        }

        return yearlyStats;
    }

    /**
     * 计算单年度的最大回撤以及发生的日期和修复天数
     *
     * @param yearData      当年的数据
     * @param allSortedData 所有按日期排序的数据（用于跨年修复计算）
     * @return 最大回撤信息，包括回撤比例、发生日期和修复天数
     */
    private static MaxDrawdownInfo calculateMaxDrawdown(List<Map<Integer, String>> yearData,
                                                        List<Map<Integer, String>> allSortedData) {
        if (yearData.isEmpty()) {
            return new MaxDrawdownInfo(0.0, null, -1);
        }

        double maxDrawdown = 0.0;  // 最大回撤
        double runningHigh = 0.0;  // 运行过程中的最高点
        String maxDrawdownDate = null; // 最大回撤发生的日期
        double maxDrawdownHigh = 0.0; // 最大回撤时的最高点价格

        // 第一遍遍历：找到最大回撤及其发生位置
        for (Map<Integer, String> record : yearData) {
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
                        maxDrawdownHigh = runningHigh; // 记录最大回撤时的最高点
                    }
                }
            } catch (NumberFormatException e) {
                // 忽略无法解析的数据
                System.err.println("无法解析收盘价: " + closePriceStr);
                continue;
            }
        }

        // 计算修复天数（使用完整数据进行跨年查找）
        int recoveryDays = calculateRecoveryDays(allSortedData, maxDrawdownHigh, maxDrawdownDate);

        return new MaxDrawdownInfo(maxDrawdown, maxDrawdownDate, recoveryDays);
    }

    /**
     * 计算最大回撤的修复天数（支持跨年查找）
     *
     * @param allSortedData   所有按日期排序的数据
     * @param maxDrawdownHigh 最大回撤时的最高点价格
     * @param maxDrawdownDate 最大回撤发生的日期
     * @return 修复天数，如果未修复则返回-1
     */
    private static int calculateRecoveryDays(List<Map<Integer, String>> allSortedData,
                                             double maxDrawdownHigh,
                                             String maxDrawdownDate) {
        if (maxDrawdownDate == null || allSortedData.isEmpty()) {
            return -1;
        }

        // 找到最大回撤发生日期在完整数据中的位置
        int maxDrawdownIndex = -1;
        for (int i = 0; i < allSortedData.size(); i++) {
            if (maxDrawdownDate.equals(allSortedData.get(i).get(0))) {
                maxDrawdownIndex = i;
                break;
            }
        }

        if (maxDrawdownIndex == -1) {
            return -1;
        }

        // 从最大回撤发生位置的下一天开始查找修复点（跨年查找）
        for (int i = maxDrawdownIndex + 1; i < allSortedData.size(); i++) {
            Map<Integer, String> record = allSortedData.get(i);
            String date = record.get(0);
            String closePriceStr = record.get(9);

            if (closePriceStr == null || closePriceStr.equals("null")) {
                continue;
            }

            try {
                double closePrice = Double.parseDouble(closePriceStr);

                // 如果价格回到或超过之前的最高点，则认为已修复
                if (closePrice >= maxDrawdownHigh) {
                    return calculateDaysBetween(maxDrawdownDate, date);
                }
            } catch (NumberFormatException e) {
                continue;
            }
        }

        // 如果在所有后续数据中都没有找到修复点，返回-1表示未修复
        return -1;
    }

    /**
     * 计算两个日期之间的天数差
     *
     * @param startDate 开始日期字符串
     * @param endDate   结束日期字符串
     * @return 天数差
     */
    private static int calculateDaysBetween(String startDate, String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate start = LocalDate.parse(startDate, formatter);
            LocalDate end = LocalDate.parse(endDate, formatter);
            return (int) ChronoUnit.DAYS.between(start, end);
        } catch (Exception e) {
            System.err.println("日期解析错误: " + startDate + " 到 " + endDate);
            return -1;
        }
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
    public static void printResults(Map<Integer, YearlyStats> yearlyStats, boolean isAll) {
        System.out.println("=== 各年度统计信息，是否为全收益：" + isAll + " ===");

        // 按年份排序输出
        yearlyStats.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    Integer year = entry.getKey();
                    YearlyStats stats = entry.getValue();
                    MaxDrawdownInfo drawdownInfo = stats.getMaxDrawdownInfo();

                    String recoveryInfo;
                    if (drawdownInfo.getRecoveryDays() == -1) {
                        recoveryInfo = "未修复";
                    } else {
                        recoveryInfo = drawdownInfo.getRecoveryDays() + "天";
                    }

                    System.out.printf("%d年最大回撤: %.2f%%，发生日期: %s，修复天数: %s，年度收益率: %.2f%%\n",
                            year,
                            drawdownInfo.getMaxDrawdown() * 100,
                            drawdownInfo.getDate(),
                            recoveryInfo,
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

            // 计算平均修复天数（只计算已修复的年份）
            double avgRecoveryDays = yearlyStats.values().stream()
                    .mapToInt(stats -> stats.getMaxDrawdownInfo().getRecoveryDays())
                    .filter(days -> days != -1)
                    .average()
                    .orElse(0.0);

            long recoveredYears = yearlyStats.values().stream()
                    .mapToInt(stats -> stats.getMaxDrawdownInfo().getRecoveryDays())
                    .filter(days -> days != -1)
                    .count();

            System.out.println("\n=== 统计摘要 ===");
            System.out.printf("平均年度最大回撤: %.2f%%\n", avgDrawdown * 100);
            System.out.printf("平均年度收益率: %.2f%%\n", avgReturn * 100);
            if (recoveredYears > 0) {
                System.out.printf("平均修复天数: %.1f天 (基于%d个已修复年份)\n", avgRecoveryDays, recoveredYears);
            } else {
                System.out.println("平均修复天数: 无已修复年份");
            }
        }
    }

    /**
     * 最大回撤信息类
     */
    static class MaxDrawdownInfo {
        private double maxDrawdown; // 最大回撤比例
        private String date;       // 最大回撤发生的日期
        private int recoveryDays;  // 修复天数，-1表示未修复

        public MaxDrawdownInfo(double maxDrawdown, String date, int recoveryDays) {
            this.maxDrawdown = maxDrawdown;
            this.date = date;
            this.recoveryDays = recoveryDays;
        }

        public double getMaxDrawdown() {
            return maxDrawdown;
        }

        public String getDate() {
            return date;
        }

        public int getRecoveryDays() {
            return recoveryDays;
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