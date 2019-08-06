package com.caipiao.common.util;

import sun.org.mozilla.javascript.internal.UniqueTag;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * 金额计算类
 */
public class CalculationUtils {
    private static DecimalFormat df = new DecimalFormat("#.00");

    /**
     * 提供精确的加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    /**
     * 提供精确的减法运算。
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static String mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return df.format(b1.multiply(b2).doubleValue());
    }

    /**
     * 精确乘法运算
     * @param value1 被乘数
     * @param value2 乘数
     * @return
     */

    public static Double muld(Number value1, Number value2) {
        BigDecimal b1 = new BigDecimal(Double.toString(value1.doubleValue()));
        BigDecimal b2 = new BigDecimal(Double.toString(value2.doubleValue()));
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static String div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return df.format(b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue());
    }

    /**
     * 将字符串转化为double类型的金额
     *
     * @param amt
     * @return
     */
    public static double fomatAmt(String amt) {
        return Double.parseDouble(amt);
    }

    public static String formatString(double amount) {
      //  java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#.00");

        return  rd(amount);
    }

    /**
     * 格式化带逗号的金额字符串(逢千进一)
     *  例如: 1,510,366.0000
     *
     * @param money
     * @return
     */
    public static Number parseMoneyForThousandIntoOne(String money){
        DecimalFormat decimalFormat = new DecimalFormat();
        String style = "0,000.0#";
        decimalFormat.applyPattern(style);
        try {
            Number number = decimalFormat.parse(money);
            return number;
        } catch (ParseException e) {
        }
        return new BigDecimal(0);
    }

    public static String getMoney(String money){
        if(StringUtil.isEmpty(money)){
            return money;
        }
        //是否带有逗号,需要特殊处理转换为数字
        if(money.contains(",")){
            Number number =  parseMoneyForThousandIntoOne(money);
            if(null != number){
                BigDecimal bigDecimal = new BigDecimal(number.toString());//防止超大的数
                return bigDecimal.toString();
            }
        }else if(money.contains(".")){
            Number number = parseMoney(money);
            if(null != number){
                BigDecimal bigDecimal = new BigDecimal(number.toString());//防止超大的数
                return bigDecimal.toString();
            }
        }
        return money;
    }

    /**
     * 格式化数字金额为带逗号的字符串
     *  例如:1510366
     *
     * @param money
     * @return
     */
    public static String getMoneyDouHao(String money){
        try {
            BigDecimal decimal = new BigDecimal(money);
            DecimalFormat format = new DecimalFormat(",###,##0"); //没有小数
            return format.format(decimal);
        } catch (Exception e) {
            return null;
        }
    }

    public static double spValue(double d) {
        return com.util.math.MathUtil.round(d, 2);
    }

    /**
     * 银行家算法
     * 舍去位的数值小于5时，直接舍去
     * 舍去位的数值大于等于6时，进位后舍去
     * 当舍去位的数值等于5时，分两种情况：5后面还有其他数字（非0），则进位后舍去；若5后面是0（即5是最后一个数字）
     * 则根据5前一位数的奇偶性来判断是否需要进位，奇数进位，偶数舍去
     * round(10.5551) = 10.56
     * round(10.555)  = 10.56
     * round(10.545)  = 10.54
     * @param d
     * @return
     */
    public static double bankerAlgoNum(double d){
        BigDecimal bd = new BigDecimal("" + d);
        bd = bd.setScale(3, BigDecimal.ROUND_DOWN);
        bd = bd.setScale(3,BigDecimal.ROUND_HALF_EVEN);
        NumberFormat numberFormat = new DecimalFormat("#0.##");
        String dStr = numberFormat.format(bd.doubleValue());
        return Double.parseDouble(dStr);
    }

    /**
     * 将带小数的金额格式化为千分位整数字符串
     * @param money
     * @return
     */
    public static String getMoneyFormat(String money) {
        return getMoneyDouHao(getMoney(money));
    }

    /**
     * 格式化,例如: #.00
     * @param money
     * @return
     */
    public static Number parseMoney(String money){
        try {
            return df.parse(money);
        } catch (ParseException e) {
        }
        return new BigDecimal(0);
    }

    public static int compareDouble(double v1, double v2){
        BigDecimal data1 = new BigDecimal(v1);
        BigDecimal data2 = new BigDecimal(v2);
        return data1.compareTo(data2);
    }

    public static int compareInt(double money) {
        return (int)money;
    }

    /**
     * double类型不四舍五入取prec位小数
     * @param value
     * 		double值
     * @param prec
     * 		小数位数
     * @return
     */
    public static double roundNoDouble(double value, int prec) {
        double ret = 0.0;
        try {
            double factor = Math.pow(10, prec);
            ret = Math.floor(value * factor) / factor;
        } catch (Exception e) {
            System.out.println("double不四舍五入" + prec + "位小数异常");
        }
        return ret;
    }

    /**
     * double类型四舍五入取prec位小数
     * @param value
     * 		double值
     * @param prec
     * 		小数位数
     * @return
     */
    public static double roundDouble(double value, int prec) {
        BigDecimal bg = new BigDecimal(value);
        double ret = bg.setScale(prec, BigDecimal.ROUND_HALF_UP).doubleValue();
        return ret;
    }

    /**
     * double类型四舍五入取prec位小数
     * @param value
     * 		double值
     * @param prec
     * 		小数位数
     * @return
     */
    public static double roundDouble(String value, int prec) {
        BigDecimal bg = new BigDecimal(value);
        double ret = bg.setScale(prec, BigDecimal.ROUND_HALF_UP).doubleValue();
        return ret;
    }

    public static String formatOdds(double d){
        return String.format("%.2f", d);
    }

    /**
     * double类型四舍五入取prec位小数, 格式化为百分比
     * @param value
     * 		double值
     * @param prec
     * 		格式化后的小数位数
     * @return
     */
    public static String roundDoublePercent(double value, int prec) {
        BigDecimal bg = new BigDecimal(value * 100);
        double ret = bg.setScale(prec, BigDecimal.ROUND_HALF_UP).doubleValue();
        return ret+"%";
    }

    /**
     * 保留两位小数-不足的补0
     * @param value
     * @return
     */
    public static String rd(double value) {
        DecimalFormat format = new DecimalFormat("##0.00");
        return format.format(value);
    }

    /**
     * 保留两位小数-不足的补0
     * @param value
     * @return
     */
    public static double rdo(double value) {
        return roundDouble(value, 1);
    }

    /**
     * 将数值格式化为千分位字符串
     * @param   value   待格式化的数值
     */
    public static String formatToThousandsStr(double value)
    {
        return DecimalFormat.getNumberInstance().format(value);
    }

    protected String parseString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    protected Double parseDouble(Object obj) {
        try{
            if (UniqueTag.NOT_FOUND == obj) {
                return null;
            }
            return Double.parseDouble(obj.toString());
        }catch (Exception e) {
            return 0.0;
        }
    }

    protected Long parseLong(Object obj) {
        try{
            if (UniqueTag.NOT_FOUND == obj) {
                return null;
            }
            return Long.parseLong(obj.toString());
        } catch (Exception e) {
            return 0l;
        }
    }

    protected Integer parseInt(Object obj) {
        try{
            if (UniqueTag.NOT_FOUND == obj) {
                return null;
            }
            return Integer.parseInt(obj.toString());
        }catch (Exception e) {
            return 0;
        }
    }

    protected Boolean parseBool(Object obj) {
        try{
            if (UniqueTag.NOT_FOUND == obj) {
                return null;
            }
            return Boolean.parseBoolean(obj.toString());
        }catch (Exception e) {
            return false;
        }
    }

    protected Short parseShort(Object obj) {
        try{
            if (UniqueTag.NOT_FOUND == obj) {
                return null;
            }
            return Short.parseShort(obj.toString());
        }catch (Exception e) {
            return 0;
        }
    }


}
