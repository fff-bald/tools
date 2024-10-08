package model;

import com.alibaba.excel.annotation.ExcelProperty;

/**
 * @author cjl
 * @since 2024/8/31 16:00
 */
public class CommonExcelModel {

    @ExcelProperty(value = "一")
    private String str1;

    @ExcelProperty(value = "二")
    private String str2;

    @ExcelProperty(value = "三")
    private String str3;

    @ExcelProperty(value = "四")
    private String str4;

    @ExcelProperty(value = "五")
    private String str5;

    @ExcelProperty(value = "刘")
    private String str6;

    @ExcelProperty(value = "七")
    private String str7;

    @ExcelProperty(value = "八")
    private String str8;

    @ExcelProperty(value = "九")
    private String str9;

    @ExcelProperty(value = "十")
    private String str10;

    // ---------- valueOf ----------

    public static CommonExcelModel valueOf(String str1, String str2, String str3) {
        CommonExcelModel res = new CommonExcelModel();
        res.str1 = str1;
        res.str2 = str2;
        res.str3 = str3;
        return res;
    }

    public static CommonExcelModel valueOf(String str1, String str2, String str3, String str4, String str5) {
        CommonExcelModel res = valueOf(str1, str2, str3);
        res.str4 = str4;
        res.str5 = str5;
        return res;
    }

    // ---------- get & set ----------

    public String getStr1() {
        return str1;
    }

    public void setStr1(String str1) {
        this.str1 = str1;
    }

    public String getStr2() {
        return str2;
    }

    public void setStr2(String str2) {
        this.str2 = str2;
    }

    public String getStr3() {
        return str3;
    }

    public void setStr3(String str3) {
        this.str3 = str3;
    }

    public String getStr4() {
        return str4;
    }

    public void setStr4(String str4) {
        this.str4 = str4;
    }

    public String getStr5() {
        return str5;
    }

    public void setStr5(String str5) {
        this.str5 = str5;
    }

    public String getStr6() {
        return str6;
    }

    public void setStr6(String str6) {
        this.str6 = str6;
    }

    public String getStr7() {
        return str7;
    }

    public void setStr7(String str7) {
        this.str7 = str7;
    }

    public String getStr8() {
        return str8;
    }

    public void setStr8(String str8) {
        this.str8 = str8;
    }

    public String getStr9() {
        return str9;
    }

    public void setStr9(String str9) {
        this.str9 = str9;
    }

    public String getStr10() {
        return str10;
    }

    public void setStr10(String str10) {
        this.str10 = str10;
    }
}
