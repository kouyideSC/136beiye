package com.caipiao.admin.util;

import com.caipiao.common.util.convert.TypeCaseHelper;
import com.caipiao.domain.cpadmin.Dto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.CodeSource;
import java.security.Key;
import java.security.MessageDigest;
import java.security.ProtectionDomain;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SystemUtils {
    public static final int LAST_DAY = 0;
    public static final int LAST_WEEK = 1;
    public static final int LAST_MONTH = 2;
    public static final int LAST_7_DAYS = 3;
    public static final int LAST_30_DAYS = 4;
    public static final int LAST_1_DAYS = 5;
    /**
     * 7位ASCII字符，也叫作ISO646-US、Unicode字符集的基本拉丁块
     */
    public static final String US_ASCII = "US-ASCII";
    /**
     * ISO 拉丁字母表 No.1，也叫作 ISO-LATIN-1
     */
    public static final String ISO_8859_1 = "ISO-8859-1";
    /**
     * 8 位 UCS 转换格式
     */
    public static final String UTF_8 = "UTF-8";
    /**
     * 16 位 UCS 转换格式，Big Endian（最低地址存放高位字节）字节顺序
     */
    public static final String UTF_16BE = "UTF-16BE";
    /**
     * 16 位 UCS 转换格式，Little-endian（最高地址存放低位字节）字节顺序
     */
    public static final String UTF_16LE = "UTF-16LE";
    /**
     * 16 位 UCS 转换格式，字节顺序由可选的字节顺序标记来标识
     */
    public static final String UTF_16 = "UTF-16";
    /**
     * 中文超大字符集
     */
    public static final String GBK = "GBK";
    private static final String key = "%$^**##@@!#$^%&*()())_)_()*&^%%#@$$#$$$#$@";
    private static Log log = LogFactory.getLog(SystemUtils.class);
    private static String HanDigiStr[] = new String[]{"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    private static String HanDiviStr[] = new String[]{"", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "万",
            "拾", "佰", "仟", "亿", "拾", "佰", "仟", "万", "拾", "佰", "仟"};
    private static String DEFAULT_FORMART = "yyyy-MM-dd HH:mm:ss";
    private static Key mKey;
    private static Cipher mDecryptCipher;
    private static Cipher mEncryptCipher;

    public static boolean isEmpty(Object pObj) {
        if (pObj == null)
            return true;
        if (pObj == "")
            return true;
        if (pObj instanceof String) {
            if (((String) pObj).length() == 0) {
                return true;
            }
        } else if (pObj instanceof Collection) {
            if (((Collection) pObj).size() == 0) {
                return true;
            }
        } else if (pObj instanceof Map) {
            if (((Map) pObj).size() == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 把list转换为一个用逗号分隔的字符串
     */
    public static String listToString(List list) {
        StringBuilder sb = new StringBuilder();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (i < list.size() - 1) {
                    sb.append(list.get(i) + ",");
                } else {
                    sb.append(list.get(i));
                }
            }
        }
        return sb.toString();
    }

    public static boolean isNotEmpty(Object pObj) {
        if (pObj == null)
            return false;
        if (pObj == "")
            return false;
        if (pObj instanceof String) {
            if (((String) pObj).length() == 0) {
                return false;
            }
        } else if (pObj instanceof Collection) {
            if (((Collection) pObj).size() == 0) {
                return false;
            }
        } else if (pObj instanceof Map) {
            if (((Map) pObj).size() == 0) {
                return false;
            }
        }
        return true;
    }

    public static String getFixedPersonIDCode(String personIDCode) throws Exception {
        if (personIDCode == null)
            throw new Exception("输入的身份证号无效，请检查");

        if (personIDCode.length() == 18) {
            if (isIdentity(personIDCode))
                return personIDCode;
            else
                throw new Exception("输入的身份证号无效，请检查");
        } else if (personIDCode.length() == 15)
            return fixPersonIDCodeWithCheck(personIDCode);
        else
            throw new Exception("输入的身份证号无效，请检查");
    }

    public static String fixPersonIDCodeWithCheck(String personIDCode) throws Exception {
        if (personIDCode == null || personIDCode.trim().length() != 15)
            throw new Exception("输入的身份证号不足15位，请检查");

        if (!isIdentity(personIDCode))
            throw new Exception("输入的身份证号无效，请检查");

        return fixPersonIDCodeWithoutCheck(personIDCode);
    }

    public static String fixPersonIDCodeWithoutCheck(String personIDCode) throws Exception {
        if (personIDCode == null || personIDCode.trim().length() != 15)
            throw new Exception("输入的身份证号不足15位，请检查");

        String id17 = personIDCode.substring(0, 6) + "19" + personIDCode.substring(6, 15); // 15位身份证补'19'

        char[] code = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'}; // 11个校验码字符
        int[] factor = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1}; // 18个加权因子
        int[] idcd = new int[18];
        int sum; // 根据公式 ∑(ai×Wi) 计算
        int remainder; // 第18位校验码
        for (int i = 0; i < 17; i++) {
            idcd[i] = Integer.parseInt(id17.substring(i, i + 1));
        }
        sum = 0;
        for (int i = 0; i < 17; i++) {
            sum = sum + idcd[i] * factor[i];
        }
        remainder = sum % 11;
        String lastCheckBit = String.valueOf(code[remainder]);
        return id17 + lastCheckBit;
    }

    public static boolean isIdentity(String identity) {
        if (identity == null)
            return false;
        if (identity.length() == 18 || identity.length() == 15) {
            String id15 = null;
            if (identity.length() == 18)
                id15 = identity.substring(0, 6) + identity.substring(8, 17);
            else
                id15 = identity;
            try {
                Long.parseLong(id15); // 校验是否为数字字符串

                String birthday = "19" + id15.substring(6, 12);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                sdf.parse(birthday); // 校验出生日期
                if (identity.length() == 18 && !fixPersonIDCodeWithoutCheck(id15).equals(identity))
                    return false; // 校验18位身份证
            } catch (Exception e) {
                return false;
            }
            return true;
        } else
            return false;
    }

    public static Timestamp getBirthdayFromPersonIDCode(String identity) throws Exception {
        String id = getFixedPersonIDCode(identity);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try {
            Timestamp birthday = new Timestamp(sdf.parse(id.substring(6, 14)).getTime());
            return birthday;
        } catch (ParseException e) {
            throw new Exception("不是有效的身份证号，请检查");
        }
    }

    public static String getGenderFromPersonIDCode(String identity) throws Exception {
        String id = getFixedPersonIDCode(identity);
        char sex = id.charAt(16);
        return sex % 2 == 0 ? "2" : "1";
    }

    private static String PositiveIntegerToHanStr(String NumStr) {
        // 输入字符串必须正整数，只允许前导空格(必须右对齐)，不宜有前导零
        String RMBStr = "";
        boolean lastzero = false;
        boolean hasvalue = false; // 亿、万进位前有数值标记
        int len, n;
        len = NumStr.length();
        if (len > 15)
            return "数值过大!";
        for (int i = len - 1; i >= 0; i--) {
            if (NumStr.charAt(len - i - 1) == ' ')
                continue;
            n = NumStr.charAt(len - i - 1) - '0';
            if (n < 0 || n > 9)
                return "输入含非数字字符!";

            if (n != 0) {
                if (lastzero)
                    RMBStr += HanDigiStr[0]; // 若干零后若跟非零值，只显示一个零
                // 除了亿万前的零不带到后面
                // if( !( n==1 && (i%4)==1 && (lastzero || i==len-1) ) )
                // 如十进位前有零也不发壹音用此行
                if (!(n == 1 && (i % 4) == 1 && i == len - 1)) // 十进位处于第一位不发壹音
                    RMBStr += HanDigiStr[n];
                RMBStr += HanDiviStr[i]; // 非零值后加进位，个位为空
                hasvalue = true; // 置万进位前有值标记

            } else {
                if ((i % 8) == 0 || ((i % 8) == 4 && hasvalue)) // 亿万之间必须有非零值方显示万
                    RMBStr += HanDiviStr[i]; // “亿”或“万”
            }
            if (i % 8 == 0)
                hasvalue = false; // 万进位前有值标记逢亿复位
            lastzero = (n == 0) && (i % 4 != 0);
        }

        if (RMBStr.length() == 0)
            return HanDigiStr[0]; // 输入空字符或"0"，返回"零"
        return RMBStr;
    }

    /**
     * 将货币转换为大写形式
     *
     * @param val 传入的数据
     * @return String 返回的人民币大写形式字符串
     */
    public static String numToRMBStr(double val) {
        String SignStr = "";
        String TailStr = "";
        long fraction, integer;
        int jiao, fen;

        if (val < 0) {
            val = -val;
            SignStr = "负";
        }
        if (val > 99999999999999.999 || val < -99999999999999.999)
            return "数值位数过大!";
        // 四舍五入到分
        long temp = Math.round(val * 100);
        integer = temp / 100;
        fraction = temp % 100;
        jiao = (int) fraction / 10;
        fen = (int) fraction % 10;
        if (jiao == 0 && fen == 0) {
            TailStr = "整";
        } else {
            TailStr = HanDigiStr[jiao];
            if (jiao != 0)
                TailStr += "角";
            // 零元后不写零几分
            if (integer == 0 && jiao == 0)
                TailStr = "";
            if (fen != 0)
                TailStr += HanDigiStr[fen] + "分";
        }
        // 下一行可用于非正规金融场合，0.03只显示“叁分”而不是“零元叁分”
        // if( !integer ) return SignStr+TailStr;
        return SignStr + PositiveIntegerToHanStr(String.valueOf(integer)) + "元" + TailStr;
    }

    public static int getDaysInMonth(int year, int month) {
        if ((month == 1) || (month == 3) || (month == 5) || (month == 7) || (month == 8) || (month == 10)
                || (month == 12)) {
            return 31;
        } else if ((month == 4) || (month == 6) || (month == 9) || (month == 11)) {
            return 30;
        } else {
            if (((year % 4) == 0) && ((year % 100) != 0) || ((year % 400) == 0)) {
                return 29;
            } else {
                return 28;
            }
        }
    }

    /**
     * 根据所给的起止时间来计算间隔的天数
     *
     * @param startDate 起始时间
     * @param endDate   结束时间
     * @return int 返回间隔天数
     */
    public static int getIntervalDays(java.sql.Date startDate, java.sql.Date endDate) {
        long startdate = startDate.getTime();
        long enddate = endDate.getTime();
        long interval = enddate - startdate;
        int intervalday = (int) (interval / (1000 * 60 * 60 * 24));
        return intervalday;
    }

    public static int getIntervalDays(Date startDate, Date endDate) {
        long startdate = startDate.getTime();
        long enddate = endDate.getTime();
        long interval = enddate - startdate;
        int intervalday = (int) (interval / (1000 * 60 * 60 * 24));
        return intervalday;
    }

    /**
     * 根据所给的起止时间来计算间隔的月数
     *
     * @param startDate 起始时间
     * @param endDate   结束时间
     * @return int 返回间隔月数
     */
    public static int getIntervalMonths(java.sql.Date startDate, java.sql.Date endDate) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        int startDateM = startCal.MONTH;
        int startDateY = startCal.YEAR;
        int enddatem = endCal.MONTH;
        int enddatey = endCal.YEAR;
        int interval = (enddatey * 12 + enddatem) - (startDateY * 12 + startDateM);
        return interval;
    }

    /**
     * 返回当前日期时间字符串<br>
     * 默认格式:yyyy-mm-dd hh:mm:ss
     *
     * @return String 返回当前字符串型日期时间
     */
    public static String getCurrentTime() {
        String returnStr = null;
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        returnStr = f.format(date);
        return returnStr;
    }

    /**
     * 返回自定义格式的当前日期时间字符串
     *
     * @param format 格式规则
     * @return String 返回当前字符串型日期时间
     */
    public static String getCurrentTime(String format) {
        String returnStr = null;
        SimpleDateFormat f = null != format && !"".equals(format) ?
                new SimpleDateFormat(format) : new SimpleDateFormat(DEFAULT_FORMART);
        Date date = new Date();
        returnStr = f.format(date);
        return returnStr;
    }

    public static String getCustomerTime(String format, int dayNumber) {
        String returnStr = null;
        SimpleDateFormat f = null != format && !"".equals(format) ?
                new SimpleDateFormat(format) : new SimpleDateFormat(DEFAULT_FORMART);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, dayNumber);
        returnStr = f.format(cal.getTime());
        return returnStr;
    }

    /**
     * 返回当前字符串型日期
     *
     * @return String 返回的字符串型日期
     */
    public static String getCurDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = simpledateformat.format(calendar.getTime());
        return strDate;
    }

    /**
     * 返回TimeStamp对象
     *
     * @return
     */
    public static Timestamp getCurrentTimestamp() {
        Object obj = TypeCaseHelper.convert(getCurrentTime(), "Timestamp", "yyyy-MM-dd HH:mm:ss");
        if (obj != null)
            return (Timestamp) obj;
        else
            return null;
    }

    /**
     * 将字符串型日期转换为日期型
     *
     * @param strDate       字符串型日期
     * @param srcDateFormat 源日期格式
     * @param dstDateFormat 目标日期格式
     * @return Date 返回的util.Date型日期
     */
    public static Date stringToDate(String strDate, String srcDateFormat, String dstDateFormat) {
        Date rtDate = null;
        Date tmpDate = (new SimpleDateFormat(srcDateFormat)).parse(strDate, new ParsePosition(0));
        String tmpString = null;
        if (tmpDate != null)
            tmpString = (new SimpleDateFormat(dstDateFormat)).format(tmpDate);
        if (tmpString != null)
            rtDate = (new SimpleDateFormat(dstDateFormat)).parse(tmpString, new ParsePosition(0));
        return rtDate;
    }

    /**
     * 合并字符串数组
     *
     * @param a 字符串数组0
     * @param b 字符串数组1
     * @return 返回合并后的字符串数组
     */
    public static String[] mergeStringArray(String[] a, String[] b) {
        if (a.length == 0 || isEmpty(a))
            return b;
        if (b.length == 0 || isEmpty(b))
            return a;
        String[] c = new String[a.length + b.length];
        for (int m = 0; m < a.length; m++) {
            c[m] = a[m];
        }
        for (int i = 0; i < b.length; i++) {
            c[a.length + i] = b[i];
        }
        return c;
    }

    /**
     * 对文件流输出下载的中文文件名进行编码 屏蔽各种浏览器版本的差异性
     */
    public static String encodeChineseDownloadFileName(HttpServletRequest request, String pFileName) {
        String agent = request.getHeader("USER-AGENT");
        try {
            if (null != agent && -1 != agent.indexOf("MSIE")) {
                pFileName = URLEncoder.encode(pFileName, "utf-8");
            } else {
                pFileName = new String(pFileName.getBytes("utf-8"), "iso8859-1");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return pFileName;
    }

    /**
     * 判断是否是IE浏览器
     *
     * @param
     * @return
     */
    public static boolean isIE(HttpServletRequest request) {
        String userAgent = request.getHeader("USER-AGENT").toLowerCase();
        boolean isIe = true;
        int index = userAgent.indexOf("msie");
        if (index == -1) {
            isIe = false;
        }
        return isIe;
    }

    /**
     * 判断是否是Chrome浏览器
     *
     * @param
     * @return
     */
    public static boolean isChrome(HttpServletRequest request) {
        String userAgent = request.getHeader("USER-AGENT").toLowerCase();
        boolean isChrome = true;
        int index = userAgent.indexOf("chrome");
        if (index == -1) {
            isChrome = false;
        }
        return isChrome;
    }

    /**
     * 判断是否是Firefox浏览器
     *
     * @param
     * @return
     */
    public static boolean isFirefox(HttpServletRequest request) {
        String userAgent = request.getHeader("USER-AGENT").toLowerCase();
        boolean isFirefox = true;
        int index = userAgent.indexOf("firefox");
        if (index == -1) {
            isFirefox = false;
        }
        return isFirefox;
    }

    /**
     * 获取客户端类型
     *
     * @param
     * @return
     */
    public static String getClientExplorerType(HttpServletRequest request) {
        String userAgent = request.getHeader("USER-AGENT").toLowerCase();
        String explorer = "非主流浏览器";
        if (isIE(request)) {
            int index = userAgent.indexOf("msie");
            explorer = userAgent.substring(index, index + 8);
        } else if (isChrome(request)) {
            int index = userAgent.indexOf("chrome");
            explorer = userAgent.substring(index, index + 12);
        } else if (isFirefox(request)) {
            int index = userAgent.indexOf("firefox");
            explorer = userAgent.substring(index, index + 11);
        }
        return explorer.toUpperCase();
    }

    /**
     * 基于MD5算法的单向加密
     *
     * @param strSrc 明文
     * @return 返回密文
     */
    public static String encryptBasedMd5(String strSrc) {
        String outString = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] outByte = md5.digest(strSrc.getBytes("UTF-8"));
            outString = outByte.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outString;
    }

    /**
     * 判断缺省数据源的JDBC类型：Oracle
     *
     * @return
     */
    public static boolean defaultJdbcTypeOracle() {
        boolean out = false;
        String jdbcType = System.getProperty("g4.JdbcType");
        if (jdbcType.equalsIgnoreCase("oracle")) {
            out = true;
        }
        return out;
    }

    /**
     * 判断缺省数据源的JDBC类型：Mysql
     *
     * @return
     */
    public static boolean defaultJdbcTypeMysql() {
        boolean out = false;
        String jdbcType = System.getProperty("g4.JdbcType");
        if (jdbcType.equalsIgnoreCase("mysql")) {
            out = true;
        }
        return out;
    }

    /**
     * JS输出含有\n的特殊处理
     *
     * @param pStr
     * @return
     */
    public static String replace4JsOutput(String pStr) {
        pStr = pStr.replace("\r\n", "<br/>&nbsp;&nbsp;");
        pStr = pStr.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
        pStr = pStr.replace(" ", "&nbsp;");
        return pStr;
    }

    /**
     * 获取class文件所在绝对路径
     *
     * @param cls
     * @return
     * @throws IOException
     */
    public static String getPathFromClass(Class cls) {
        String path = null;
        if (cls == null) {
            throw new NullPointerException();
        }
        URL url = getClassLocationURL(cls);
        if (url != null) {
            path = url.getPath();
            if ("jar".equalsIgnoreCase(url.getProtocol())) {
                try {
                    path = new URL(path).getPath();
                } catch (MalformedURLException e) {
                }
                int location = path.indexOf("!/");
                if (location != -1) {
                    path = path.substring(0, location);
                }
            }
            File file = new File(path);
            try {
                path = file.getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    /**
     * 这个方法可以通过与某个类的class文件的相对路径来获取文件或目录的绝对路径。 通常在程序中很难定位某个相对路径，特别是在B/S应用中。
     * 通过这个方法，我们可以根据我们程序自身的类文件的位置来定位某个相对路径。
     * 比如：某个txt文件相对于程序的Test类文件的路径是../../system/test.txt，
     * 那么使用本方法Path.getFullPathRelateClass("../../system/test.txt",Test.class)
     * 得到的结果是txt文件的在系统中的绝对路径。
     *
     * @param relatedPath 相对路径
     * @param cls         用来定位的类
     * @return 相对路径所对应的绝对路径
     * @throws IOException 因为本方法将查询文件系统，所以可能抛出IO异常
     */
    public static String getFullPathRelateClass(String relatedPath, Class cls) {
        String path = null;
        if (relatedPath == null) {
            throw new NullPointerException();
        }
        String clsPath = getPathFromClass(cls);
        File clsFile = new File(clsPath);
        String tempPath = clsFile.getParent() + File.separator + relatedPath;
        File file = new File(tempPath);
        try {
            path = file.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 获取类的class文件位置的URL
     *
     * @param cls
     * @return
     */
    private static URL getClassLocationURL(final Class cls) {
        if (cls == null)
            throw new IllegalArgumentException("null input: cls");
        URL result = null;
        final String clsAsResource = cls.getName().replace('.', '/').concat(".class");
        final ProtectionDomain pd = cls.getProtectionDomain();
        if (pd != null) {
            final CodeSource cs = pd.getCodeSource();
            if (cs != null)
                result = cs.getLocation();
            if (result != null) {
                if ("file".equals(result.getProtocol())) {
                    try {
                        if (result.toExternalForm().endsWith(".jar") || result.toExternalForm().endsWith(".zip"))
                            result = new URL("jar:".concat(result.toExternalForm()).concat("!/").concat(clsAsResource));
                        else if (new File(result.getFile()).isDirectory())
                            result = new URL(result, clsAsResource);
                    } catch (MalformedURLException ignore) {
                    }
                }
            }
        }
        if (result == null) {
            final ClassLoader clsLoader = cls.getClassLoader();
            result = clsLoader != null ? clsLoader.getResource(clsAsResource) : ClassLoader
                    .getSystemResource(clsAsResource);
        }
        return result;
    }

    /**
     * 获取start到end区间的随机数,不包含start+end
     *
     * @param start
     * @param end
     * @return
     */
    public static BigDecimal getRandom(int start, int end) {
        return new BigDecimal(start + Math.random() * end);
    }

    /**
     * 根据前缀获取编号
     *
     * @param start 前缀
     * @return 编号
     */
    public static String generateNewId(String start) {
        Date nowTime = new Date();
        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddhhmmss");
        return start + formater.format(nowTime);
    }

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 文件拷贝
     *
     * @param filePath1 源文件路径
     * @param filePath2 目标文件路径
     * @return
     * @throws Exception
     */
    public static long copyFile(String filePath1, String filePath2) {
        File srcFile = new File(filePath1);
        File destDir = new File(filePath2);
        long copySizes = 0;
        if (!srcFile.exists()) {
            copySizes = -1;
        } else if (!destDir.exists()) {
            copySizes = -1;
        } else {
            try {
                initKey(key);
                initCipher();
                doEncryptFile(new FileInputStream(filePath1), filePath2);
                copySizes = 1;
            } catch (Exception e) {
                log.error("copyFile error:" + e.toString());
            }

        }
        return copySizes;
    }

    /**
     * 给定文件名称创建文件，有文件则删除后创建文件
     *
     * @param filePath 文件路径
     * @param context  写入内容
     * @return
     */
    public static String writeNewFile(String filePath, List<String> context) {
        File file = new File(filePath);
        try {
            if (!file.exists())
                file.createNewFile();
            else
                deleteFile(filePath);
            //对该文件加锁
            RandomAccessFile out = new RandomAccessFile(file, "rw");
            for (String line : context) {
                out.write(line.getBytes("utf-8"));
            }
            out.close();
            out = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 给定文件名追加文件内容
     *
     * @param filePath 追加文件路径
     * @param context  写入内容
     * @return
     */
    public static String appendWriteFile(String filePath, List<String> context) {
        File file = new File(filePath);
        try {
            if (!file.exists())
                file.createNewFile();
            //对该文件加锁
            RandomAccessFile out = new RandomAccessFile(file, "rw");
            long fileLength = out.length();
            out.seek(fileLength);
            for (String line : context) {
                out.write(line.getBytes("utf-8"));
            }
            out.close();
            out = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void initKey(String keyRule) {
        byte[] keyByte = keyRule.getBytes();
        byte[] byteTemp = new byte[8];
        for (int i = 0; i < byteTemp.length && i < keyByte.length; i++) {
            byteTemp[i] = keyByte[i];
        }
        mKey = new SecretKeySpec(byteTemp, "DES");
    }

    private static void initCipher() throws Exception {
        mEncryptCipher = Cipher.getInstance("DES");
        mEncryptCipher.init(Cipher.ENCRYPT_MODE, mKey);

        mDecryptCipher = Cipher.getInstance("DES");
        mDecryptCipher.init(Cipher.DECRYPT_MODE, mKey);
    }

    private static void doEncryptFile(InputStream in, String savePath) {
        try {
            CipherInputStream cin = new CipherInputStream(in, mEncryptCipher);
            OutputStream os = new FileOutputStream(savePath);
            byte[] bytes = new byte[1024];
            int len = -1;
            while ((len = cin.read(bytes)) > 0) {
                os.write(bytes, 0, len);
                os.flush();
            }
            os.close();
            cin.close();
            in.close();
        } catch (Exception e) {
            log.error("doEncryptFile error:" + e.toString());
        }
    }

    public static int compare_date(String DATE1, String DATE2) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2 + " 23:59:59");
            if (dt1.getTime() > dt2.getTime()) {
                //System.out.println("dt1 在dt2前");
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                // System.out.println("dt1在dt2后");
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    /**
     * 字符串编码转换的实现方法
     *
     * @param str        待转换编码的字符串
     * @param newCharset 目标编码
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String changeCharset(String str, String newCharset)
            throws UnsupportedEncodingException {
        if (str != null) {
            //用默认字符编码解码字符串。
            byte[] bs = str.getBytes();
            //用新的字符编码生成字符串
            return new String(bs, newCharset);
        }
        return null;
    }

    /**
     * 将字符编码转换成UTF-8码
     */
    public static String toUTF_8(String str) throws UnsupportedEncodingException {
        return changeCharset(str, UTF_8);
    }

    /**
     * 将字符编码转换成GBK码
     */
    public static String toGBK(String str) throws UnsupportedEncodingException {
        return changeCharset(str, GBK);
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public static boolean arrayContains(String[] array, String item) {
        for (String str : array) {
            if (str.equals(item)) {
                return true;
            }
        }
        return false;
    }


    /**
     * list去重复
     *
     * @param list 需要去重复的list
     * @return 排列后的list
     */
    public static List<Dto> removeDuplicateObj(List<Dto> list) {
        List<Dto> results = new ArrayList<Dto>();
        Set<String> set = new HashSet<String>();
        for (Dto dto : list) {
            if (set.add(dto.getAsString("moduleCode")))
                results.add(dto);
        }
        return results;
    }

    public static List<String> getDateList(SimpleDateFormat f, int type) {
        List<String> result = new ArrayList<String>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        switch (type) {
            case LAST_DAY:
                calendar.add(Calendar.DATE, -1);
                result.add(f.format(calendar.getTime()));
                break;
            case LAST_WEEK:
                calendar.add(Calendar.WEEK_OF_YEAR, -1);
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                result.add(f.format(calendar.getTime()));
                calendar.add(Calendar.DATE, 6);
                result.add(f.format(calendar.getTime()));
                break;
            case LAST_MONTH:
                calendar.add(Calendar.MONTH, -1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                result.add(f.format(calendar.getTime()));
                calendar.add(Calendar.MONTH, 1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.add(Calendar.DATE, -1);
                result.add(f.format(calendar.getTime()));
                break;
            case LAST_1_DAYS:
                calendar.add(Calendar.DATE, -1);
                result.add(f.format(calendar.getTime()));
                calendar.setTime(new Date());
                result.add(f.format(calendar.getTime()));
                break;
            case LAST_7_DAYS:
                calendar.add(Calendar.DATE, -6);
                result.add(f.format(calendar.getTime()));
                calendar.setTime(new Date());
                result.add(f.format(calendar.getTime()));
                break;
            case LAST_30_DAYS:
                calendar.add(Calendar.DATE, -29);
                result.add(f.format(calendar.getTime()));
                calendar.setTime(new Date());
                result.add(f.format(calendar.getTime()));
                break;
        }
        return result;
    }

    public static List<String> getDateList(Date date, SimpleDateFormat f, int type) {
        List<String> result = new ArrayList<String>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        switch (type) {
            case LAST_DAY:
                calendar.add(Calendar.DATE, -1);
                result.add(f.format(calendar.getTime()));
                break;
            case LAST_WEEK:
                calendar.add(Calendar.WEEK_OF_YEAR, -1);
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                result.add(f.format(calendar.getTime()));
                calendar.add(Calendar.DATE, 6);
                result.add(f.format(calendar.getTime()));
                break;
            case LAST_MONTH:
                calendar.add(Calendar.MONTH, -1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                result.add(f.format(calendar.getTime()));
                calendar.add(Calendar.MONTH, 1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.add(Calendar.DATE, -1);
                result.add(f.format(calendar.getTime()));
                break;
            case LAST_1_DAYS:
                calendar.add(Calendar.DATE, -1);
                result.add(f.format(calendar.getTime()));
                calendar.setTime(date);
                result.add(f.format(calendar.getTime()));
                break;
            case LAST_7_DAYS:
                calendar.add(Calendar.DATE, -6);
                result.add(f.format(calendar.getTime()));
                calendar.setTime(date);
                result.add(f.format(calendar.getTime()));
                break;
            case LAST_30_DAYS:
                calendar.add(Calendar.DATE, -29);
                result.add(f.format(calendar.getTime()));
                calendar.setTime(date);
                result.add(f.format(calendar.getTime()));
                break;
        }
        return result;
    }
    
    /*
	 * 16进制数字字符集
	 */
	private static String hexString = "0123456789ABCDEF";

	/**
	 * 字符串转16进制
	 * @param	str	待转换的字符串
	 */
	public static String stringToHex(String str) 
	{
		//根据默认编码获取字节数组,并将字节数组中每个字节拆解成2位16进制整数
		byte[] bytes = str.getBytes();
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for(int i = 0; i < bytes.length; i++) 
		{
			sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
			sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
		}
		return sb.toString();
	}

	/**
	 * 16进制转字符串
	 * @param	str	待转换的16进制字符
	 */
	public static String hexToString(String str) 
	{
		String s = str;
		if(StringUtils.isNotEmpty(str))
		{
			//将每2位16进制整数组装成一个字节
			ByteArrayOutputStream baos = new ByteArrayOutputStream(str.length() / 2);
			for(int i = 0; i < str.length(); i += 2)
			{
				baos.write((hexString.indexOf(str.charAt(i)) << 4 | hexString.indexOf(str.charAt(i + 1))));
			}
			s = new String(baos.toByteArray());
		}
		return s;
	}

    /**
     * 将字符串转为16进制数字
     * @param str 字符串路径
     * @return 16进制字符串路径
     */
    public static String StringToHexPath(String str) 
    {
    	//转化字符串为十六进制编码
        String s = "";
        for (int i = 0; i < str.length(); i++) {
            int ch = (int) str.charAt(i);
            String s4 = Integer.toHexString(ch);
            s = s + s4;
        }
        return s;
    }
}