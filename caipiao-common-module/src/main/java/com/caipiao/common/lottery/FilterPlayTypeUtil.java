package com.caipiao.common.lottery;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.jjyh.MatchInfo;
import com.caipiao.domain.jsbf.Schedule;

import java.util.*;

/**
 * 竞彩过滤互斥玩法工具类
 * Created by Kouyi on 2018/05/15.
 */
public class FilterPlayTypeUtil {
    public static void main(String[] args) throws Exception {
        //请注意：String数组中数据格式说明：玩法代码-购买选项|赔率
        //所有玩法格式定义 参见常量playMap
        //足球例子
        String[] choose = new String[] {"SPF=1|5.40", "RQSPF=1|4.15|-1", "RQSPF=3|2.88|-1", "RQSPF=0|1.66|-1"};
        List<String> tmap = getFilterPlay(choose);
        tmap = getFilterPlaySame(choose, "SPF=1|5.40");
        //篮球例子
        choose = new String[] {"SF=0|1.75", "RFSF=3|1.87|-1.5"};
        tmap = getFilterPlay(choose);
    }

    /**
     * 获取过滤玩法后的选项结果
     * @param choose
     * @throws Exception
     */
    public static List<String> getFilterPlay(String[] choose) throws Exception {
        List<String> result = new ArrayList<>();
        if (choose == null || choose.length < 2) {
            return result;
        }
        List<String> sp3 = new ArrayList<>();
        List<String> sp1 = new ArrayList<>();
        List<String> sp0 = new ArrayList<>();
        for (String entry : choose) {
            String vl = getSPFChoose(entry);
            if (vl.indexOf("3") > -1) {
                sp3.add(entry);
            }
            if (vl.indexOf("1") > -1) {
                sp1.add(entry);
            }
            if (vl.indexOf("0") > -1) {
                sp0.add(entry);
            }
        }
        filterSamePlayMaxSp(sp3);
        filterSamePlayMaxSp(sp1);
        filterSamePlayMaxSp(sp0);
        double sumMax = 0;
        List<String> maxList = new ArrayList<>();
        if (sp3.size() > 0) {
            double sum3 = 1;
            for (String s3 : sp3) {
                String[] mch = s3.split("\\|");
                sum3 *= Double.parseDouble(mch[1]);
            }
            if (sumMax < sum3) {
                sumMax = sum3;
                maxList = sp3;
            }
        }
        if (sp1.size() > 0) {
            double sum1 = 1;
            for (String s1 : sp1) {
                String[] mch = s1.split("\\|");
                sum1 *= Double.parseDouble(mch[1]);
            }
            if (sumMax < sum1) {
                sumMax = sum1;
                maxList = sp1;
            }
        }
        if (sp0.size() > 0) {
            double sum0 = 1;
            for (String s0 : sp0) {
                String[] mch = s0.split("\\|");
                sum0 *= Double.parseDouble(mch[1]);
            }
            if (sumMax < sum0) {
                maxList = sp0;
            }
        }

        for(String cos : choose) {
            if(!maxList.contains(cos)) {
                result.add(cos.split("\\|")[0]);
            }
        }
        return result;
    }

    /**
     * 过滤与指定玩法不相同的选项结果
     * @param choose
     * @param str
     * @throws Exception
     */
    public static List<String> getFilterPlaySame(String[] choose, String str) throws Exception {
        List<String> result = new ArrayList<>();
        if (choose == null || choose.length < 1 || str == null || str.equals("")) {
            return result;
        }
        List<String> sp3 = new ArrayList<>();
        List<String> sp1 = new ArrayList<>();
        List<String> sp0 = new ArrayList<>();
        for (String entry : choose) {
            String[] playType = entry.split("\\=");
            if(str.startsWith(playType[0])) {//过滤和str相同的玩法
                continue;
            }
            String vl = getSPFChoose(entry);
            if (vl.indexOf("3") > -1) {
                sp3.add(entry);
            }
            if (vl.indexOf("1") > -1) {
                sp1.add(entry);
            }
            if (vl.indexOf("0") > -1) {
                sp0.add(entry);
            }
        }

        List<String> maxList = new ArrayList<>();
        String v0 = getSPFChoose(str);
        if (v0.indexOf("3") == -1) {
            sp3.clear();
        }
        if (v0.indexOf("1") == -1) {
            sp1.clear();
        }
        if (v0.indexOf("0") == -1) {
            sp0.clear();
        }
        filterSamePlayMaxSpSame(sp3, str);
        filterSamePlayMaxSpSame(sp1, str);
        filterSamePlayMaxSpSame(sp0, str);

        double sumMax = 0;
        if (sp3.size() > 0) {
            double sum3 = 1;
            for (String s3 : sp3) {
                String[] mch = s3.split("\\|");
                sum3 *= Double.parseDouble(mch[1]);
            }
            if (sumMax < sum3) {
                sumMax = sum3;
                maxList = sp3;
            }
        }
        if (sp1.size() > 0) {
            double sum1 = 1;
            for (String s1 : sp1) {
                String[] mch = s1.split("\\|");
                sum1 *= Double.parseDouble(mch[1]);
            }
            if (sumMax < sum1) {
                sumMax = sum1;
                maxList = sp1;
            }
        }
        if (sp0.size() > 0) {
            double sum0 = 1;
            for (String s0 : sp0) {
                String[] mch = s0.split("\\|");
                sum0 *= Double.parseDouble(mch[1]);
            }
            if (sumMax < sum0) {
                maxList = sp0;
            }
        }

        for(String cos : choose) {
            if(!maxList.contains(cos)) {
                result.add(cos.split("\\|")[0]);
            }
        }
        return result;
    }

    /**
     * 获取选项归的胜平负归属-如BF=1:0归属于胜3
     * @param entry
     * @return
     */
    private static String getSPFChoose(String entry) {
        String[] mch = entry.split("\\|");
        if (mch.length < 2) {
            return null;
        }

        String rqs = "";
        if ((mch[0].startsWith("RQSPF") || mch[0].startsWith("RFSF")) && mch.length == 3) {
            if (Double.parseDouble(mch[2]) > 0) {
                rqs = "+";
            } else {
                rqs = "-";
            }
        }
        String key = mch[0] + rqs;
        if (!playMap.containsKey(key)) {
            return null;
        }
        return playMap.get(key);
    }

    /**
     * 过滤同一玩法中最大的SP值
     * @param spChoose
     * @return
     */
    private static void filterSamePlayMaxSp(List<String> spChoose) {
        if(spChoose == null || spChoose.size() == 0) {
            return;
        }
        Map<String, String> resultMap = new HashMap<>();//存放过滤结果
        Map<String, Double> tempMap = new HashMap<>();//存放比较数字
        //过滤相同玩法
        for(String spStr : spChoose) {
            String[] mch = spStr.split("\\|");
            String tempKey = mch[0].split("\\=")[0];
            if(tempMap.containsKey(tempKey)) {
                double tempValue = tempMap.get(tempKey);
                if(tempValue < Double.parseDouble(mch[1])) {
                    tempMap.put(tempKey, Double.parseDouble(mch[1]));
                    resultMap.put(tempKey, spStr);
                }
            } else {
                tempMap.put(tempKey, Double.parseDouble(mch[1]));
                resultMap.put(tempKey, spStr);
            }
        }

        //过滤互斥玩法
        Map<String, String> playMap = new HashMap<>();//存放玩法赔率
        for(Map.Entry<String, String> entry : resultMap.entrySet()) {
            String[] value = entry.getValue().split("\\|");
            String[] xuan = value[0].split("\\=");
            value[1] = xuan[1] + "|" + Double.parseDouble(value[1]);
            if (entry.getKey().equals(LotteryConstants.JCWF_PREFIX_RQSPF) || entry.getKey().equals(LotteryConstants.JCWF_PREFIX_RFSF)) {
                value[1] = value[1] + "|" + value[2];
            }
            playMap.put(entry.getKey(), value[1]);
        }

        //让球玩法和其他玩法互斥
        if(playMap.containsKey(LotteryConstants.JCWF_PREFIX_RQSPF)) {
            String[] rqsp = playMap.get(LotteryConstants.JCWF_PREFIX_RQSPF).split("\\|");
            if(rqsp[0].equals("3") && StringUtil.parseDouble(rqsp[2]) == -1) {//主-1让球胜
                if(playMap.containsKey(LotteryConstants.JCWF_PREFIX_CBF)) {//猜比分
                    String[] cbfsp = playMap.get(LotteryConstants.JCWF_PREFIX_CBF).split("\\|");
                    if(cbfsp[0].equals("1:0") || cbfsp[0].equals("2:1") || cbfsp[0].equals("3:2")) {
                        if (StringUtil.parseDouble(rqsp[1]) > StringUtil.parseDouble(cbfsp[1])) {
                            playMap.remove(LotteryConstants.JCWF_PREFIX_CBF);
                        } else {
                            playMap.remove(LotteryConstants.JCWF_PREFIX_RQSPF);
                        }
                    }
                }
                if(playMap.containsKey(LotteryConstants.JCWF_PREFIX_JQS)) {//进球数
                    String[] jqssp = playMap.get(LotteryConstants.JCWF_PREFIX_JQS).split("\\|");
                    if(jqssp[0].equals("1")) {
                        if (StringUtil.parseDouble(rqsp[1]) > StringUtil.parseDouble(jqssp[1])) {
                            playMap.remove(LotteryConstants.JCWF_PREFIX_JQS);
                        } else {
                            playMap.remove(LotteryConstants.JCWF_PREFIX_RQSPF);
                        }
                    }
                }
            }
            if(rqsp[0].equals("1") && StringUtil.parseDouble(rqsp[2]) == -1) {//主-1让球平
                if(playMap.containsKey(LotteryConstants.JCWF_PREFIX_CBF)) {//猜比分
                    String[] cbfsp = playMap.get(LotteryConstants.JCWF_PREFIX_CBF).split("\\|");
                    if(cbfsp[0].equals("2:0") || cbfsp[0].equals("3:0") || cbfsp[0].equals("3:1")
                            || cbfsp[0].equals("4:0") || cbfsp[0].equals("4:1") || cbfsp[0].equals("4:2")
                            || cbfsp[0].equals("5:0") || cbfsp[0].equals("5:1") || cbfsp[0].equals("5:2")) {
                        if (StringUtil.parseDouble(rqsp[1]) > StringUtil.parseDouble(cbfsp[1])) {
                            playMap.remove(LotteryConstants.JCWF_PREFIX_CBF);
                        } else {
                            playMap.remove(LotteryConstants.JCWF_PREFIX_RQSPF);
                        }
                    }
                }
                if(playMap.containsKey(LotteryConstants.JCWF_PREFIX_JQS)) {//进球数
                    String[] jqssp = playMap.get(LotteryConstants.JCWF_PREFIX_JQS).split("\\|");
                    if(jqssp[0].equals("2") || jqssp[0].equals("4") || jqssp[0].equals("6")) {
                        if (StringUtil.parseDouble(rqsp[1]) > StringUtil.parseDouble(jqssp[1])) {
                            playMap.remove(LotteryConstants.JCWF_PREFIX_JQS);
                        } else {
                            playMap.remove(LotteryConstants.JCWF_PREFIX_RQSPF);
                        }
                    }
                }
            }
            if(rqsp[0].equals("0") && StringUtil.parseDouble(rqsp[2]) == 1) {//主+1让球负
                if(playMap.containsKey(LotteryConstants.JCWF_PREFIX_CBF)) {//猜比分
                    String[] cbfsp = playMap.get(LotteryConstants.JCWF_PREFIX_CBF).split("\\|");
                    if(cbfsp[0].equals("0:1") || cbfsp[0].equals("1:2") || cbfsp[0].equals("2:3")) {
                        if (StringUtil.parseDouble(rqsp[1]) > StringUtil.parseDouble(cbfsp[1])) {
                            playMap.remove(LotteryConstants.JCWF_PREFIX_CBF);
                        } else {
                            playMap.remove(LotteryConstants.JCWF_PREFIX_RQSPF);
                        }
                    }
                }
                if(playMap.containsKey(LotteryConstants.JCWF_PREFIX_JQS)) {//进球数
                    String[] jqssp = playMap.get(LotteryConstants.JCWF_PREFIX_JQS).split("\\|");
                    if(jqssp[0].equals("1")) {
                        if (StringUtil.parseDouble(rqsp[1]) > StringUtil.parseDouble(jqssp[1])) {
                            playMap.remove(LotteryConstants.JCWF_PREFIX_JQS);
                        } else {
                            playMap.remove(LotteryConstants.JCWF_PREFIX_RQSPF);
                        }
                    }
                }
            }
            if(rqsp[0].equals("1") && StringUtil.parseDouble(rqsp[2]) == 1) {//主+1让球平
                if(playMap.containsKey(LotteryConstants.JCWF_PREFIX_CBF)) {//猜比分
                    String[] cbfsp = playMap.get(LotteryConstants.JCWF_PREFIX_CBF).split("\\|");
                    if(cbfsp[0].equals("0:2") || cbfsp[0].equals("0:3") || cbfsp[0].equals("1:3")
                            || cbfsp[0].equals("0:4") || cbfsp[0].equals("1:4") || cbfsp[0].equals("2:4")
                            || cbfsp[0].equals("0:5") || cbfsp[0].equals("1:5") || cbfsp[0].equals("2:5")) {
                        if (StringUtil.parseDouble(rqsp[1]) > StringUtil.parseDouble(cbfsp[1])) {
                            playMap.remove(LotteryConstants.JCWF_PREFIX_CBF);
                        } else {
                            playMap.remove(LotteryConstants.JCWF_PREFIX_RQSPF);
                        }
                    }
                }
                if(playMap.containsKey(LotteryConstants.JCWF_PREFIX_JQS)) {//进球数
                    String[] jqssp = playMap.get(LotteryConstants.JCWF_PREFIX_JQS).split("\\|");
                    if(jqssp[0].equals("2") || jqssp[0].equals("4") || jqssp[0].equals("6")) {
                        if (StringUtil.parseDouble(rqsp[1]) > StringUtil.parseDouble(jqssp[1])) {
                            playMap.remove(LotteryConstants.JCWF_PREFIX_JQS);
                        } else {
                            playMap.remove(LotteryConstants.JCWF_PREFIX_RQSPF);
                        }
                    }
                }
            }
        }

        //比分玩法和其他玩法互斥
        if(playMap.containsKey(LotteryConstants.JCWF_PREFIX_CBF)) {
            String[] bfsp = playMap.get(LotteryConstants.JCWF_PREFIX_CBF).split("\\|");
            String[] bf = bfsp[0].split("\\:");
            if(playMap.containsKey(LotteryConstants.JCWF_PREFIX_JQS)) {//进球数
                String[] jqssp = playMap.get(LotteryConstants.JCWF_PREFIX_JQS).split("\\|");
                if(!jqssp[0].equals((StringUtil.parseInt(bf[0])+StringUtil.parseInt(bf[1]))+"")) {
                    if (StringUtil.parseDouble(bfsp[1]) > StringUtil.parseDouble(jqssp[1])) {
                        playMap.remove(LotteryConstants.JCWF_PREFIX_JQS);
                    } else {
                        playMap.remove(LotteryConstants.JCWF_PREFIX_CBF);
                    }
                }
            }
            if(playMap.containsKey(LotteryConstants.JCWF_PREFIX_BQC)) {//半全场
                String[] bqcsp = playMap.get(LotteryConstants.JCWF_PREFIX_BQC).split("\\|");
                if(((bfsp[0].equals("1:0") || bfsp[0].equals("2:0") || bfsp[0].equals("3:0")
                        || bfsp[0].equals("4:0") || bfsp[0].equals("5:0")) && bqcsp[0].equals("0-3"))
                        || ((bfsp[0].equals("0:1") || bfsp[0].equals("0:2") || bfsp[0].equals("0:3")
                        || bfsp[0].equals("0:4") || bfsp[0].equals("0:5")) && bqcsp[0].equals("3-0"))) {
                    if (StringUtil.parseDouble(bfsp[1]) > StringUtil.parseDouble(bqcsp[1])) {
                        playMap.remove(LotteryConstants.JCWF_PREFIX_BQC);
                    } else {
                        playMap.remove(LotteryConstants.JCWF_PREFIX_CBF);
                    }
                }
            }
        }

        //进球数玩法和其他玩法互斥
        if(playMap.containsKey(LotteryConstants.JCWF_PREFIX_JQS)) {
            String[] jqsp = playMap.get(LotteryConstants.JCWF_PREFIX_JQS).split("\\|");
            if(playMap.containsKey(LotteryConstants.JCWF_PREFIX_BQC)) {//半全场
                String[] bqcsp = playMap.get(LotteryConstants.JCWF_PREFIX_BQC).split("\\|");
                if((jqsp[0].equals("1") || jqsp[0].equals("2")) && (bqcsp[0].equals("0-3") || bqcsp[0].equals("3-0"))
                        || (jqsp[0].equals("0") && (bqcsp[0].equals("0-1") || bqcsp[0].equals("3-1")))) {
                    if (StringUtil.parseDouble(jqsp[1]) > StringUtil.parseDouble(bqcsp[1])) {
                        playMap.remove(LotteryConstants.JCWF_PREFIX_BQC);
                    } else {
                        playMap.remove(LotteryConstants.JCWF_PREFIX_JQS);
                    }
                }
            }
        }
        //让分胜负玩法和其他玩法互斥
        if(playMap.containsKey(LotteryConstants.JCWF_PREFIX_RFSF)) {
            String[] rfsfsp = playMap.get(LotteryConstants.JCWF_PREFIX_RFSF).split("\\|");
            if(playMap.containsKey(LotteryConstants.JCWF_PREFIX_SFC)) {//胜分差
                String[] sfcsp = playMap.get(LotteryConstants.JCWF_PREFIX_SFC).split("\\|");
                String middle = sfcMap.get(StringUtil.parseInt(sfcsp[0]));
                if(StringUtil.isNotEmpty(middle)) {
                    String[] md = middle.split("\\-");
                    Double rf = StringUtil.parseDouble(rfsfsp[2]);
                    if(rfsfsp[0].equals("3") && rf > 0 && StringUtil.parseInt(sfcsp[0]) > 10 && rf < StringUtil.parseInt(md[0])) {//受让分胜和客胜分差
                        if (StringUtil.parseDouble(rfsfsp[1]) > StringUtil.parseDouble(sfcsp[1])) {
                            playMap.remove(LotteryConstants.JCWF_PREFIX_SFC);
                        } else {
                            playMap.remove(LotteryConstants.JCWF_PREFIX_RFSF);
                        }
                    }
                    if(rfsfsp[0].equals("0") && rf > 0 && StringUtil.parseInt(sfcsp[0]) > 10 && (rf > StringUtil.parseInt(md[1]))) {//受让分负和客胜分差
                        if (StringUtil.parseDouble(rfsfsp[1]) > StringUtil.parseDouble(sfcsp[1])) {
                            playMap.remove(LotteryConstants.JCWF_PREFIX_SFC);
                        } else {
                            playMap.remove(LotteryConstants.JCWF_PREFIX_RFSF);
                        }
                    }
                    if(rfsfsp[0].equals("3") && rf < 0 && StringUtil.parseInt(sfcsp[0]) < 7 && Math.abs(rf) > StringUtil.parseInt(md[1])) {//让分胜和客胜分差
                        if (StringUtil.parseDouble(rfsfsp[1]) > StringUtil.parseDouble(sfcsp[1])) {
                            playMap.remove(LotteryConstants.JCWF_PREFIX_SFC);
                        } else {
                            playMap.remove(LotteryConstants.JCWF_PREFIX_RFSF);
                        }
                    }
                    if(rfsfsp[0].equals("0") && rf < 0 && StringUtil.parseInt(sfcsp[0]) < 7 && (Math.abs(rf) < StringUtil.parseInt(md[0]))) {//让分负和客胜分差
                        if (StringUtil.parseDouble(rfsfsp[1]) > StringUtil.parseDouble(sfcsp[1])) {
                            playMap.remove(LotteryConstants.JCWF_PREFIX_SFC);
                        } else {
                            playMap.remove(LotteryConstants.JCWF_PREFIX_RFSF);
                        }
                    }
                }
            }
        }
        resultMap.clear();
        for(Map.Entry<String, String> entry : playMap.entrySet()) {
            resultMap.put(entry.getKey(), entry.getKey()+"="+entry.getValue());
        }
        tempMap.clear();
        spChoose.clear();
        spChoose.addAll(resultMap.values());
    }

    /**
     * 过滤同一玩法中最大的SP值
     * @param spChoose
     * @return
     */
    private static void filterSamePlayMaxSpSame(List<String> spChoose, String str) {
        if(spChoose == null || spChoose.size() == 0) {
            return;
        }
        Map<String, String> resultMap = new HashMap<>();//存放过滤结果
        Map<String, Double> tempMap = new HashMap<>();//存放比较数字
        //过滤相同玩法
        for(String spStr : spChoose) {
            String[] mch = spStr.split("\\|");
            String tempKey = mch[0].split("\\=")[0];
            if(tempMap.containsKey(tempKey)) {
                double tempValue = tempMap.get(tempKey);
                if(tempValue < Double.parseDouble(mch[1])) {
                    tempMap.put(tempKey, Double.parseDouble(mch[1]));
                    resultMap.put(tempKey, spStr);
                }
            } else {
                tempMap.put(tempKey, Double.parseDouble(mch[1]));
                resultMap.put(tempKey, spStr);
            }
        }

        //过滤互斥玩法
        Map<String, String> playMap = new HashMap<>();//存放玩法赔率
        for(Map.Entry<String, String> entry : resultMap.entrySet()) {
            String[] value = entry.getValue().split("\\|");
            String[] xuan = value[0].split("\\=");
            value[1] = xuan[1] + "|" + Double.parseDouble(value[1]);
            if (entry.getKey().equals(LotteryConstants.JCWF_PREFIX_RQSPF) || entry.getKey().equals(LotteryConstants.JCWF_PREFIX_RFSF)) {
                value[1] = value[1] + "|" + value[2];
            }
            playMap.put(entry.getKey(), value[1]);
        }

        Iterator<Map.Entry<String, String>> it = playMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, String> entry = it.next();
            //让球玩法和其他玩法互斥
            if(str.startsWith(LotteryConstants.JCWF_PREFIX_RQSPF)) {
                String[] xuan = str.split("\\|");
                String[] rqsp = xuan[0].split("\\=");
                if(rqsp[1].equals("3") && StringUtil.parseDouble(xuan[2]) == -1) {//主-1让球胜
                    if(entry.getKey().equals(LotteryConstants.JCWF_PREFIX_CBF)) {//猜比分
                        String[] cbfsp = entry.getValue().split("\\|");
                        if(cbfsp[0].equals("1:0") || cbfsp[0].equals("2:1") || cbfsp[0].equals("3:2")) {
                            it.remove();
                        }
                    }
                    if(entry.getKey().equals(LotteryConstants.JCWF_PREFIX_JQS)) {//进球数
                        String[] jqssp = entry.getValue().split("\\|");
                        if(jqssp[0].equals("1")) {
                            it.remove();
                        }
                    }
                }
                if(rqsp[1].equals("1") && StringUtil.parseDouble(xuan[2]) == -1) {//主-1让球平
                    if(entry.getKey().equals(LotteryConstants.JCWF_PREFIX_CBF)) {//猜比分
                        String[] cbfsp = entry.getValue().split("\\|");
                        if(cbfsp[0].equals("2:0") || cbfsp[0].equals("3:0") || cbfsp[0].equals("3:1")
                                || cbfsp[0].equals("4:0") || cbfsp[0].equals("4:1") || cbfsp[0].equals("4:2")
                                || cbfsp[0].equals("5:0") || cbfsp[0].equals("5:1") || cbfsp[0].equals("5:2")) {
                            it.remove();
                        }
                    }
                    if(entry.getKey().equals(LotteryConstants.JCWF_PREFIX_JQS)) {//进球数
                        String[] jqssp = entry.getValue().split("\\|");
                        if(jqssp[0].equals("2") || jqssp[0].equals("4") || jqssp[0].equals("6")) {
                            it.remove();
                        }
                    }
                }
                if(rqsp[1].equals("0") && StringUtil.parseDouble(xuan[2]) == 1) {//主+1让球负
                    if(entry.getKey().equals(LotteryConstants.JCWF_PREFIX_CBF)) {//猜比分
                        String[] cbfsp = entry.getValue().split("\\|");
                        if(cbfsp[0].equals("0:1") || cbfsp[0].equals("1:2") || cbfsp[0].equals("2:3")) {
                            it.remove();
                        }
                    }
                    if(entry.getKey().equals(LotteryConstants.JCWF_PREFIX_JQS)) {//进球数
                        String[] jqssp = entry.getValue().split("\\|");
                        if(jqssp[0].equals("1")) {
                            it.remove();
                        }
                    }
                }
                if(rqsp[1].equals("1") && StringUtil.parseDouble(xuan[2]) == 1) {//主+1让球平
                    if(entry.getKey().equals(LotteryConstants.JCWF_PREFIX_CBF)) {//猜比分
                        String[] cbfsp = entry.getValue().split("\\|");
                        if(cbfsp[0].equals("0:2") || cbfsp[0].equals("0:3") || cbfsp[0].equals("1:3")
                                || cbfsp[0].equals("0:4") || cbfsp[0].equals("1:4") || cbfsp[0].equals("2:4")
                                || cbfsp[0].equals("0:5") || cbfsp[0].equals("1:5") || cbfsp[0].equals("2:5")) {
                            it.remove();
                        }
                    }
                    if(entry.getKey().equals(LotteryConstants.JCWF_PREFIX_JQS)) {//进球数
                        String[] jqssp = entry.getValue().split("\\|");
                        if(jqssp[0].equals("2") || jqssp[0].equals("4") || jqssp[0].equals("6")) {
                            it.remove();
                        }
                    }
                }
            }

            //比分玩法和其他玩法互斥
            if(str.startsWith(LotteryConstants.JCWF_PREFIX_CBF)) {
                String[] xuan = str.split("\\|");
                String[] bfsp = xuan[0].split("\\=");
                String[] bf = bfsp[1].split("\\:");
                if(entry.getKey().equals(LotteryConstants.JCWF_PREFIX_JQS)) {//进球数
                    String[] jqssp = entry.getValue().split("\\|");
                    if(!jqssp[0].equals((StringUtil.parseInt(bf[0])+StringUtil.parseInt(bf[1]))+"")) {
                        it.remove();
                    }
                }
                if(entry.getKey().equals(LotteryConstants.JCWF_PREFIX_BQC)) {//半全场
                    String[] bqcsp = entry.getValue().split("\\|");
                    if(((bfsp[1].equals("1:0") || bfsp[1].equals("2:0") || bfsp[1].equals("3:0")
                            || bfsp[1].equals("4:0") || bfsp[1].equals("5:0")) && bqcsp[0].equals("0-3"))
                            || ((bfsp[1].equals("0:1") || bfsp[1].equals("0:2") || bfsp[1].equals("0:3")
                            || bfsp[1].equals("0:4") || bfsp[1].equals("0:5")) && bqcsp[0].equals("3-0"))) {
                        it.remove();
                    }
                }
            }

            //进球数玩法和其他玩法互斥
            if(str.startsWith(LotteryConstants.JCWF_PREFIX_JQS)) {
                String[] xuan = str.split("\\|");
                String[] jqsp = xuan[0].split("\\=");
                if(entry.getKey().equals(LotteryConstants.JCWF_PREFIX_BQC)) {//半全场
                    String[] bqcsp = entry.getValue().split("\\|");
                    if((jqsp[1].equals("1") || jqsp[1].equals("2")) && (bqcsp[0].equals("0-3") || bqcsp[0].equals("3-0"))
                            || (jqsp[1].equals("0") && (bqcsp[0].equals("0-1") || bqcsp[0].equals("3-1")))) {
                        it.remove();
                    }
                }
            }
            //让分胜负玩法和其他玩法互斥
            if(str.startsWith(LotteryConstants.JCWF_PREFIX_RFSF)) {
                String[] xuan = str.split("\\|");
                String[] rfsfsp = xuan[0].split("\\=");
                if(entry.getKey().equals(LotteryConstants.JCWF_PREFIX_SFC)) {//胜分差
                    String[] sfcsp = entry.getValue().split("\\|");
                    String middle = sfcMap.get(StringUtil.parseInt(sfcsp[0]));
                    if(StringUtil.isNotEmpty(middle)) {
                        String[] md = middle.split("\\-");
                        Double rf = StringUtil.parseDouble(xuan[2]);
                        if(rfsfsp[1].equals("3") && rf > 0 && StringUtil.parseInt(sfcsp[0]) > 10 && rf < StringUtil.parseInt(md[0])) {//受让分胜和客胜分差
                            it.remove();
                        }
                        if(rfsfsp[1].equals("0") && rf > 0 && StringUtil.parseInt(sfcsp[0]) > 10 && (rf > StringUtil.parseInt(md[1]))) {//受让分负和客胜分差
                            it.remove();
                        }
                        if(rfsfsp[1].equals("3") && rf < 0 && StringUtil.parseInt(sfcsp[0]) < 7 && Math.abs(rf) > StringUtil.parseInt(md[1])) {//让分胜和客胜分差
                            it.remove();
                        }
                        if(rfsfsp[1].equals("0") && rf < 0 && StringUtil.parseInt(sfcsp[0]) < 7 && (Math.abs(rf) < StringUtil.parseInt(md[0]))) {//让分负和客胜分差
                            it.remove();
                        }
                    }
                }
            }
        }

        resultMap.clear();
        for(Map.Entry<String, String> entry : playMap.entrySet()) {
            resultMap.put(entry.getKey(), entry.getKey()+"="+entry.getValue());
        }
        tempMap.clear();
        spChoose.clear();
        spChoose.addAll(resultMap.values());
    }

    private static Map<String, String> playMap = new HashMap<>();
    private static Map<Integer, String> sfcMap = new HashMap<>();
    static {
        //足球定义
        playMap.put("SPF=3", "3");//胜
        playMap.put("SPF=1", "1");//平
        playMap.put("SPF=0", "0");//负
        playMap.put("RQSPF=3-", "3");//让球胜
        playMap.put("RQSPF=1-", "3");//让球平
        playMap.put("RQSPF=0-", "10");//让球负
        playMap.put("RQSPF=3+", "31");//受让球胜
        playMap.put("RQSPF=1+", "0");//受让球平
        playMap.put("RQSPF=0+", "0");//受让球负
        playMap.put("JQS=0", "1");//进球数0
        playMap.put("JQS=1", "30");//进球数1
        playMap.put("JQS=2", "310");//进球数2
        playMap.put("JQS=3", "30");//进球数3
        playMap.put("JQS=4", "310");//进球数4
        playMap.put("JQS=5", "30");//进球数5
        playMap.put("JQS=6", "310");//进球数6
        playMap.put("JQS=7", "30");//进球数7+
        playMap.put("BQC=3-3", "3");//半全场胜胜
        playMap.put("BQC=3-1", "1");//半全场胜平
        playMap.put("BQC=3-0", "0");//半全场胜负
        playMap.put("BQC=1-3", "3");//半全场负胜
        playMap.put("BQC=1-1", "1");//半全场平平
        playMap.put("BQC=1-0", "0");//半全场平负
        playMap.put("BQC=0-3", "3");//半全场负胜
        playMap.put("BQC=0-1", "1");//半全场负平
        playMap.put("BQC=0-0", "0");//半全场负负
        playMap.put("CBF=1:0", "3");//比分1:0
        playMap.put("CBF=2:0", "3");//比分2:0
        playMap.put("CBF=2:1", "3");//比分2:1
        playMap.put("CBF=3:0", "3");//比分3:0
        playMap.put("CBF=3:1", "3");//比分3:1
        playMap.put("CBF=3:2", "3");//比分3:2
        playMap.put("CBF=4:0", "3");//比分4:0
        playMap.put("CBF=4:1", "3");//比分4:1
        playMap.put("CBF=4:2", "3");//比分4:2
        playMap.put("CBF=5:0", "3");//比分5:0
        playMap.put("CBF=5:1", "3");//比分5:1
        playMap.put("CBF=5:2", "3");//比分5:2
        playMap.put("CBF=9:0", "3");//比分胜其他
        playMap.put("CBF=0:0", "1");//比分0:0
        playMap.put("CBF=1:1", "1");//比分1:1
        playMap.put("CBF=2:2", "1");//比分2:2
        playMap.put("CBF=3:3", "1");//比分3:3
        playMap.put("CBF=9:9", "1");//比分平其他
        playMap.put("CBF=0:1", "0");//比分0:1
        playMap.put("CBF=0:2", "0");//比分0:2
        playMap.put("CBF=1:2", "0");//比分1:2
        playMap.put("CBF=0:3", "0");//比分0:3
        playMap.put("CBF=1:3", "0");//比分1:3
        playMap.put("CBF=2:3", "0");//比分2:3
        playMap.put("CBF=0:4", "0");//比分0:4
        playMap.put("CBF=1:4", "0");//比分1:4
        playMap.put("CBF=2:4", "0");//比分2:4
        playMap.put("CBF=0:5", "0");//比分0:5
        playMap.put("CBF=1:5", "0");//比分1:5
        playMap.put("CBF=2:5", "0");//比分2:5
        playMap.put("CBF=0:9", "0");//比分负其他
        //篮球定义
        playMap.put("SF=3", "3");//胜
        playMap.put("SF=0", "0");//负
        playMap.put("RFSF=3-", "3");//主让分胜
        playMap.put("RFSF=0-", "30");//主让分负
        playMap.put("RFSF=3+", "30");//主受让分胜
        playMap.put("RFSF=0+", "0");//主受让分负
        playMap.put("DXF=3", "30");//大分
        playMap.put("DXF=0", "30");//小分
        playMap.put("SFC=01", "3");//主胜1-5分
        playMap.put("SFC=02", "3");//主胜6-10分
        playMap.put("SFC=03", "3");//主胜11-15分
        playMap.put("SFC=04", "3");//主胜16-20分
        playMap.put("SFC=05", "3");//主胜21-25分
        playMap.put("SFC=06", "3");//主胜26+分
        playMap.put("SFC=11", "0");//客胜1-5分
        playMap.put("SFC=12", "0");//客胜6-10分
        playMap.put("SFC=13", "0");//客胜11-15分
        playMap.put("SFC=14", "0");//客胜16-20分
        playMap.put("SFC=15", "0");//客胜21-25分
        playMap.put("SFC=16", "0");//客胜26+分

        sfcMap.put(1, "1-5");//主胜1-5分
        sfcMap.put(2, "6-10");//主胜6-10分
        sfcMap.put(3, "11-15");//主胜11-15分
        sfcMap.put(4, "16-20");//主胜16-20分
        sfcMap.put(5, "21-25");//主胜21-25分
        sfcMap.put(6, "26");//主胜26+分
        sfcMap.put(11, "1-5");//客胜1-5分
        sfcMap.put(12, "6-10");//客胜6-10分
        sfcMap.put(13, "11-15");//客胜11-15分
        sfcMap.put(14, "16-20");//客胜16-20分
        sfcMap.put(15, "21-25");//客胜21-25分
        sfcMap.put(16, "26");//客胜26+分
    }
}
