package com.caipiao.common.lottery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 竞彩理论奖金计算工具类
 * Created by Kouyi on 2018/02/4.
 */
public class ScopeBonusUtil {
    private static Map<String, int[]> morePassMap = new HashMap<>();//多串过关
    private static HashMap<String,Integer> mapsEnd = new HashMap<>();//串关最大边界

    public static void main(String[] args) {
        //请注意：String数组中数据格式说明：玩法代码-购买选项|赔率
        //所有玩法格式定义 参见常量playMap
        //足球例子
        String[] s1 = new String[] {"SPF-3|2.46","RQSPF-3|5.7:","JQS-2|3.15","BQC-31|15"};
        List<String[]> list0 = new ArrayList<>();//胆场次
        //list0.add(s1);
        List<String[]> list1 = new ArrayList<>();//拖场次
        String[] s2 = new String[] {"SPF-3|2.72", "SPF-0|2.25", "RQSPF-3|1.51|1", "RQSPF-1|3.85|1"};
        String[] s3 = new String[] {"SPF-3|1.37", "SPF-0|5.65", "RQSPF-3|2.13|-1", "RQSPF-0|2.62|-1"};
        String[] s4 = new String[] {"SPF-3|1.94", "SPF-0|3.72", "RQSPF-3|4.40|-1", "RQSPF-1|3.30|-1"};
        list1.add(s2);
        list1.add(s3);
        list1.add(s4);
        System.out.println(calculateScopeBonus(list0, list1, new String[]{"2*1"}, 1));
        //篮球例子
        List<String[]> list3 = new ArrayList<>();//胆场次
        List<String[]> list4 = new ArrayList<>();//拖场次
        String[] s5 = new String[] {"SF-0|1.18","SF-3|3.35","RFSF-0|1.68|6.5","RFSF-3|1.81|6.5"};
        String[] s6 = new String[] {"SF-0|2.22","SF-3|1.43","RFSF-3|1.75|-3.5"};
        String[] s7 = new String[] {"SF-0|2.34","RFSF-3|1.75|-3.5","DXF-3|1.75"};
        list4.add(s5);
        list4.add(s6);
        list4.add(s7);
        System.out.println(calculateScopeBonus(list3, list4, new String[]{"2*1"}, 1));
    }

    /**
     * 计算理论奖金范围
     * @param d_list
     * @param t_list
     * @param pass
     */
    private static String calculateScopeBonus(List<String[]> d_list, List<String[]> t_list, String[] pass, int multiple) {
        try {
            List<double[]> dminDoubleList = null;
            Map<String, List<double[]>> dmap = getMinAndMaxSpCombine(d_list);
            if (dmap != null && dmap.size() > 0) {
                dminDoubleList = dmap.get("min");
            }
            Map<String, List<double[]>> tmap = getMinAndMaxSpCombine(t_list);
            if (tmap == null || tmap.size() == 0) {
                return "";
            }

            List<double[]> minDoubleList = tmap.get("min");
            List<double[]> minCombline = combineBonus(dminDoubleList, minDoubleList, pass);
            double minBonus = 0;
            if (minCombline != null && minCombline.size() > 0) {
                for (double[] mc : minCombline) {
                    double temp = 1;
                    for (double c : mc) {
                        temp *= c;
                    }
                    if (minBonus == 0) {
                        minBonus = temp;
                    } else {
                        if (minBonus > temp) {
                            minBonus = temp;
                        }
                    }
                }
            }
            String min = "";
            if(minBonus > 0) {
                min = String.format("%.2f", minBonus * 2 * multiple);
            }

            List<double[]> dmaxDoubleList = dmap.get("max");
            List<double[]> maxDoubleList = tmap.get("max");
            List<double[]> maxCombline = combineBonus(dmaxDoubleList, maxDoubleList, pass);
            double maxBonus = 0;
            if (maxCombline != null && maxCombline.size() > 0) {
                for (double[] mc : maxCombline) {
                    double temp = 1;
                    for (double c : mc) {
                        temp *= c;
                    }
                    maxBonus += temp;
                }
            }
            String max = "";
            if(maxBonus > 0) {
                max = String.format("%.2f", maxBonus * 2 * multiple);
            }
            StringBuffer buffer = new StringBuffer();
            if(min.equals(max)) {
                buffer.append(max);
            } else {
                if(min.equals("")) {
                    buffer.append(max);
                } else if(max.equals("")) {
                    buffer.append(min);
                } else {
                    buffer.append(min).append("~").append(max);
                }
            }
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取场次最小和最大赔率
     * @param list
     * @throws Exception
     */
    private static Map<String, List<double[]>> getMinAndMaxSpCombine(List<String[]> list) throws Exception {
        Map<String, List<double[]>> resultMap = new HashMap<>();
        if(list == null || list.size() == 0) {
            return resultMap;
        }
        List<double[]> minDoubleList = new ArrayList<>();
        List<double[]> maxDoubleList = new ArrayList<>();
        for(String[] matchStr : list) {
            if(matchStr.length == 1) {
                double[] doubles = new double[1];
                String[] mch = matchStr[0].split("\\|");
                if(mch.length < 2) {
                    return null;
                }
                doubles[0] = Double.parseDouble(mch[1]);
                minDoubleList.add(doubles);
                maxDoubleList.add(doubles);
                continue;
            }
            List<String> sp3 = new ArrayList<>();
            List<String> sp1 = new ArrayList<>();
            List<String> sp0 = new ArrayList<>();
            double minSp = 0;
            for(String match : matchStr) {
                String[] mch = match.split("\\|");
                if(mch.length < 2) {
                    return null;
                }

                double ms = Double.parseDouble(mch[1]);
                if(minSp == 0) {
                    minSp = ms;
                } else {
                    if(minSp > ms) {
                        minSp = ms;
                    }
                }
                String rqs = "";
                if((mch[0].startsWith("RQSPF") || mch[0].startsWith("RFSF")) && mch.length == 3) {
                    if(Double.parseDouble(mch[2]) > 0) {
                        rqs = "+";
                    } else {
                        rqs = "-";
                    }
                }
                String key = mch[0] + rqs;
                if(!playMap.containsKey(key)) {
                    return null;
                }
                String vl = playMap.get(key);
                if(vl.indexOf("3") > -1) {
                    sp3.add(match);
                }
                if(vl.indexOf("1") > -1) {
                    sp1.add(match);
                }
                if(vl.indexOf("0") > -1) {
                    sp0.add(match);
                }
            }
            minDoubleList.add(new double[] {minSp});
            filterSamePlayMaxSp(sp3);
            filterSamePlayMaxSp(sp1);
            filterSamePlayMaxSp(sp0);
            double sumMax = 0;
            List<String> maxList = null;
            if(sp3.size() > 0) {
                double sum3 = 1;
                for(String s3 : sp3) {
                    String[] mch = s3.split("\\|");
                    sum3*= Double.parseDouble(mch[1]);
                }
                if(sumMax < sum3) {
                    sumMax = sum3;
                    maxList = sp3;
                }
            }
            if(sp1.size() > 0) {
                double sum1 = 1;
                for(String s1 : sp1) {
                    String[] mch = s1.split("\\|");
                    sum1*= Double.parseDouble(mch[1]);
                }
                if(sumMax < sum1) {
                    sumMax = sum1;
                    maxList = sp1;
                }
            }
            if(sp0.size() > 0) {
                double sum0 = 1;
                for(String s0 : sp0) {
                    String[] mch = s0.split("\\|");
                    sum0*= Double.parseDouble(mch[1]);
                }
                if(sumMax < sum0) {
                    sumMax = sum0;
                    maxList = sp0;
                }
            }
            if(maxList != null && maxList.size() > 0) {
                double[] doubles = new double[maxList.size()];
                int index = 0;
                for (String str : maxList) {
                    String[] mch = str.split("\\|");
                    if(mch.length < 2) {
                        return null;
                    }
                    doubles[index] = Double.parseDouble(mch[1]);
                    index++;
                }
                maxDoubleList.add(doubles);
            }
        }
        resultMap.put("min", minDoubleList);
        resultMap.put("max", maxDoubleList);
        return resultMap;
    }

    /**
     * 过滤同一玩法中最大的SP值
     * @param sp
     * @return
     */
    private static void filterSamePlayMaxSp(List<String> sp) {
        if(sp == null || sp.size() == 0) {
            return;
        }

        Map<String, String> resultMap = new HashMap<>();//存放过滤结果
        Map<String, Double> tempMap = new HashMap<>();//存放比较数字
        for(String mt : sp) {
            String[] mch = mt.split("\\|");
            String tempKey = mch[0].split("\\-")[0];
            if(tempMap.containsKey(tempKey)) {
                double tempValue = tempMap.get(tempKey);
                if(tempValue < Double.parseDouble(mch[1])) {
                    tempMap.put(tempKey, Double.parseDouble(mch[1]));
                    resultMap.put(tempKey, mt);
                }
            } else {
                tempMap.put(tempKey, Double.parseDouble(mch[1]));
                resultMap.put(tempKey, mt);
            }
        }
        tempMap.clear();
        sp.clear();
        sp.addAll(resultMap.values());
    }

    /**
     * 奖金范围-返回组合列表
     * * @param d_choose
     *      胆场次-不设胆 则传空
     * @param t_choose
     *      拖场次-不能为空 购买的场次和选项数
     * @param pass
     *      串关方式-如3*1,3*3等
     * @return
     *      -99：表示参数错误
     */
    private static List<double[]> combineBonus(List<double[]> d_choose, List<double[]> t_choose, String[] pass) {
        int code = -99;
        if(t_choose == null || t_choose.size() == 0 || pass == null || pass.length == 0) {
            return null;
        }

        if(d_choose == null) {
            d_choose = new ArrayList<>();
        }

        List<double[]> resultList = new ArrayList<>();
        for(String p : pass) {
            if (!morePassMap.containsKey(p)) {//只要有一个串关方式不正确，则不能再进行计算，因为结果一定错误，返回-99
                return null;
            }
            int[] passCombine = morePassMap.get(p);//获取串关组合
            int maxMnumber = getMaxPassType(p);
            int[] tchoose = new int[t_choose.size()];
            for (int x = 0; x < t_choose.size(); x++) {
                tchoose[x] = x;
            }
            List<int[]> chaiMax = combine(tchoose, maxMnumber - d_choose.size());
            if (chaiMax == null || chaiMax.size() == 0) {
                return null;
            }
            for (int y = 0; y < chaiMax.size(); y++) {
                List<double[]> chaiZhu = new ArrayList<>();
                int[] max = chaiMax.get(y);
                for (int z = 0; z < max.length; z++) {
                    chaiZhu.add(t_choose.get(max[z]));
                }
                List<double[]> tempList = new ArrayList<>();
                combineMulPlayDouble(chaiZhu, new double[chaiZhu.size()], 0, tempList);
                for (double[] choose : tempList) {
                    for (int cb : passCombine) {
                        if(d_choose != null && d_choose.size() > 0) {
                            resultList.addAll(combineDouble(d_choose, choose, cb));
                        } else {
                            resultList.addAll(combineDouble(null, choose, cb));
                        }
                    }
                }
            }
        }
        return resultList;
    }

    /**
     * 奖金范围-多元素排列组合-类似拆票算法-返回组合列表
     * @param targetList 目标数据列表
     * @param combineTempList 组合临时数据列表
     * @param combineTempIndex 组合结果数据开始索引下标
     * @param resultList
     */
    private static void combineMulPlayDouble(List<double[]> targetList, double[] combineTempList, int combineTempIndex, List<double[]> resultList) {
        if (targetList.size() == 1) {
            double[] end = targetList.get(0);
            for (int i = 0; i < end.length; i++) {
                combineTempList[combineTempIndex] = end[i];
                double[] tempList = new double[combineTempList.length];
                for(int c=0; c<combineTempList.length; c++) {
                    tempList[c] = combineTempList[c];
                }
                resultList.add(tempList);
            }
        } else {
            double[] first = targetList.get(0);
            List<double[]> tempList = new ArrayList<double[]>(targetList);
            tempList.remove(0);
            for (int i = 0; i < first.length; i++) {
                combineTempList[combineTempIndex] = first[i];
                combineMulPlayDouble(tempList, combineTempList, combineTempIndex + 1, resultList);
            }
        }
    }

    /**
     * 奖金范围-从d+t[d|t=选号个数]中取n个数组合-玩法计算总注数
     * @param d 可以不设胆 则传空
     * @param t 必须有拖码场次
     * @param n
     * @return
     */
    private static List<double[]> combineDouble(List<double[]> d, double[] t, int n) {
        List<double[]> resultList = new ArrayList<>();
        if(t == null || t.length == 0) {
            return resultList;
        }
        if(d == null || d.size() == 0) {
            sequenceDouble(t, 0, new double[n], 0, resultList);
        }
        else {
            int dn = d.size();
            int tn = t.length;
            if(dn > 8 || dn + tn < n) {//胆码个数范围0~8个|胆和拖不能低于n个
                return resultList;
            }

            List<double[]> temp_t = new ArrayList<>();
            int nd = n - d.size();
            sequenceDouble(t, 0, new double[nd], 0, temp_t);
            List<double[]> temp_d = new ArrayList<>();
            combineMulPlayDouble(d, new double[d.size()], 0, temp_d);
            for(double[] lr : temp_t) {
                for(double[] ld : temp_d) {
                    double[] temp = new double[lr.length+ld.length];
                    System.arraycopy(lr, 0, temp, 0, lr.length);
                    System.arraycopy(ld, 0, temp, lr.length, ld.length);
                    resultList.add(temp);
                }
            }
        }
        return resultList;
    }

    /**
     * 奖金范围-列举排列组合-返回注数列表
     * @param targetList 目标数据列表
     * @param targetIndex 目标数据开始索引下标
     * @param combineTempList 组合临时数据列表
     * @param combineTempIndex 组合结果数据开始索引下标
     * @return resultList
     */
    private static void sequenceDouble(double[] targetList, int targetIndex, double[] combineTempList, int combineTempIndex, List<double[]> resultList) {
        int size = combineTempList.length;
        int count = combineTempIndex + 1;
        if (count > size) {//结束
            double[] tempList = new double[combineTempList.length];
            for(int a = 0; a < combineTempList.length; a++) {
                tempList[a] = combineTempList[a];
            }
            resultList.add(tempList);
            return;
        }
        //递归遍历
        for (int i = targetIndex; i < targetList.length + count - size; i++) {
            combineTempList[combineTempIndex] = targetList[i];
            sequenceDouble(targetList, i + 1, combineTempList, combineTempIndex + 1, resultList);
        }
    }

    /**
     * 竞彩投注过关方式至少需要投注场次数
     * @param passType
     * @return
     */
    private static int getMaxPassType(String passType){
        int pass = getPassType(passType);
        if(pass != -1){
            return Integer.parseInt(passType.substring(0, passType.lastIndexOf("*")));
        }
        return -1;
    }

    /**
     * 竞彩投注最大边界值
     * @param passType
     * @return
     */
    private static int getPassType(String passType){
        Integer obj = mapsEnd.get(passType);
        if ( obj != null ) {
            return obj.intValue();
        } else {
            return -1;
        }
    }

    /**
     * 排列组合-从m[m=选号个数]中取n个数组合
     * @param m
     * @param n
     */
    private static List<int[]> combine(int[] m, int n) {
        List<int[]> resultList = new ArrayList<>();
        sequenceCombine(m, 0, new int[n], 0, resultList);
        return resultList;
    }

    /**
     * 奖金范围-列举排列组合-返回组合列表
     * @param targetList 目标数据列表
     * @param targetIndex 目标数据开始索引下标
     * @param combineTempList 组合临时数据列表
     * @param combineTempIndex 组合结果数据开始索引下标
     * @return
     */
    private static void sequenceCombine(int[] targetList, int targetIndex, int[] combineTempList, int combineTempIndex, List<int[]> resultList) {
        int size = combineTempList.length;
        int count = combineTempIndex + 1;
        if (count > size) {//结束
            int[] tempList = new int[combineTempList.length];
            for(int c=0; c<combineTempList.length; c++) {
                tempList[c] = combineTempList[c];
            }
            resultList.add(tempList);
            return;
        }
        //递归遍历
        for (int i = targetIndex; i < targetList.length + count - size; i++) {
            combineTempList[combineTempIndex] = targetList[i];
            sequenceCombine(targetList, i + 1, combineTempList, combineTempIndex + 1, resultList);
        }
    }

    private static Map<String, String> playMap = new HashMap<>();
    static {
        //足球定义
        playMap.put("SPF-3", "3");//胜
        playMap.put("SPF-1", "1");//平
        playMap.put("SPF-0", "0");//负
        playMap.put("RQSPF-3-", "3");//让球胜
        playMap.put("RQSPF-1-", "3");//让球平
        playMap.put("RQSPF-0-", "10");//让球负
        playMap.put("RQSPF-3+", "31");//受让球胜
        playMap.put("RQSPF-1+", "0");//受让球平
        playMap.put("RQSPF-0+", "0");//受让球负
        playMap.put("JQS-0", "1");//进球数0
        playMap.put("JQS-1", "30");//进球数1
        playMap.put("JQS-2", "310");//进球数2
        playMap.put("JQS-3", "30");//进球数3
        playMap.put("JQS-4", "310");//进球数4
        playMap.put("JQS-5", "30");//进球数5
        playMap.put("JQS-6", "310");//进球数6
        playMap.put("JQS-7", "30");//进球数7+
        playMap.put("BQC-33", "3");//半全场胜胜
        playMap.put("BQC-31", "1");//半全场胜平
        playMap.put("BQC-30", "0");//半全场胜负
        playMap.put("BQC-13", "3");//半全场负胜
        playMap.put("BQC-11", "1");//半全场平平
        playMap.put("BQC-10", "0");//半全场平负
        playMap.put("BQC-03", "3");//半全场负胜
        playMap.put("BQC-01", "1");//半全场负平
        playMap.put("BQC-00", "0");//半全场负负
        playMap.put("CBF-10", "3");//比分1:0
        playMap.put("CBF-20", "3");//比分2:0
        playMap.put("CBF-21", "3");//比分2:1
        playMap.put("CBF-30", "3");//比分3:0
        playMap.put("CBF-31", "3");//比分3:1
        playMap.put("CBF-32", "3");//比分3:2
        playMap.put("CBF-40", "3");//比分4:0
        playMap.put("CBF-41", "3");//比分4:1
        playMap.put("CBF-42", "3");//比分4:2
        playMap.put("CBF-50", "3");//比分5:0
        playMap.put("CBF-51", "3");//比分5:1
        playMap.put("CBF-52", "3");//比分5:2
        playMap.put("CBF-90", "3");//比分胜其他
        playMap.put("CBF-00", "1");//比分0:0
        playMap.put("CBF-11", "1");//比分1:1
        playMap.put("CBF-22", "1");//比分2:2
        playMap.put("CBF-33", "1");//比分3:3
        playMap.put("CBF-99", "1");//比分平其他
        playMap.put("CBF-01", "0");//比分0:1
        playMap.put("CBF-02", "0");//比分0:2
        playMap.put("CBF-12", "0");//比分1:2
        playMap.put("CBF-03", "0");//比分0:3
        playMap.put("CBF-13", "0");//比分1:3
        playMap.put("CBF-23", "0");//比分2:3
        playMap.put("CBF-04", "0");//比分0:4
        playMap.put("CBF-14", "0");//比分1:4
        playMap.put("CBF-24", "0");//比分2:4
        playMap.put("CBF-05", "0");//比分0:5
        playMap.put("CBF-15", "0");//比分1:5
        playMap.put("CBF-25", "0");//比分2:5
        playMap.put("CBF-09", "0");//比分负其他
        //篮球定义
        playMap.put("SF-3", "3");//胜
        playMap.put("SF-0", "0");//负
        playMap.put("RFSF-3-", "3");//主让分胜
        playMap.put("RFSF-0-", "30");//主让分负
        playMap.put("RFSF-3+", "3");//主受让分胜
        playMap.put("RFSF-0+", "30");//主受让分负
        playMap.put("DXF-3", "30");//大分
        playMap.put("DXF-0", "30");//小分
        playMap.put("SFC-Z15", "3");//主胜1-5分
        playMap.put("SFC-Z610", "3");//主胜6-10分
        playMap.put("SFC-Z1115", "3");//主胜11-15分
        playMap.put("SFC-Z1620", "3");//主胜16-20分
        playMap.put("SFC-Z2125", "3");//主胜21-25分
        playMap.put("SFC-Z26", "3");//主胜26+分
        playMap.put("SFC-K15", "0");//客胜1-5分
        playMap.put("SFC-K610", "0");//客胜6-10分
        playMap.put("SFC-K1115", "0");//客胜11-15分
        playMap.put("SFC-K1620", "0");//客胜16-20分
        playMap.put("SFC-K2125", "0");//客胜21-25分
        playMap.put("SFC-K26", "0");//客胜26+分

        morePassMap.put("1*1", new int[]{1});
        morePassMap.put("2*1", new int[]{2});
        morePassMap.put("3*1", new int[]{3});
        morePassMap.put("4*1", new int[]{4});
        morePassMap.put("5*1", new int[]{5});
        morePassMap.put("6*1", new int[]{6});
        morePassMap.put("7*1", new int[]{7});
        morePassMap.put("8*1", new int[]{8});
        morePassMap.put("2*3", new int[]{1, 2});
        morePassMap.put("3*6", new int[]{1, 2});
        morePassMap.put("3*7", new int[]{1, 2, 3});
        morePassMap.put("4*10", new int[]{1, 2});
        morePassMap.put("4*14", new int[]{1, 2, 3});
        morePassMap.put("4*15", new int[]{1, 2, 3, 4});
        morePassMap.put("5*15", new int[]{1, 2});
        morePassMap.put("5*25", new int[]{1, 2, 3});
        morePassMap.put("5*30", new int[]{1, 2, 3, 4});
        morePassMap.put("5*31", new int[]{1, 2, 3, 4, 5});
        morePassMap.put("6*21", new int[]{1, 2});
        morePassMap.put("6*41", new int[]{1, 2, 3});
        morePassMap.put("6*56", new int[]{1, 2, 3, 4});
        morePassMap.put("6*62", new int[]{1, 2, 3, 4, 5});
        morePassMap.put("6*63", new int[]{1, 2, 3, 4, 5, 6});
        morePassMap.put("7*127", new int[]{1, 2, 3, 4, 5, 6, 7});
        morePassMap.put("8*255", new int[]{1, 2, 3, 4, 5, 6, 7, 8});
        morePassMap.put("3*3", new int[]{2});
        morePassMap.put("3*4", new int[]{2, 3});
        morePassMap.put("4*6", new int[]{2});
        morePassMap.put("4*11", new int[]{2, 3, 4});
        morePassMap.put("5*10", new int[]{2});
        morePassMap.put("5*20", new int[]{2, 3});
        morePassMap.put("5*26", new int[]{2, 3, 4, 5});
        morePassMap.put("6*15", new int[]{2});
        morePassMap.put("6*35", new int[]{2, 3});
        morePassMap.put("6*50", new int[]{2, 3, 4});
        morePassMap.put("6*57", new int[]{2, 3, 4, 5, 6});
        morePassMap.put("7*120", new int[]{2, 3, 4, 5, 6, 7});
        morePassMap.put("8*247", new int[]{2, 3, 4, 5, 6, 7, 8});
        morePassMap.put("4*4", new int[]{3});
        morePassMap.put("4*5", new int[]{3, 4});
        morePassMap.put("5*16", new int[]{3, 4, 5});
        morePassMap.put("6*20", new int[]{3});
        morePassMap.put("6*42", new int[]{3, 4, 5, 6});
        morePassMap.put("5*5", new int[]{4});
        morePassMap.put("5*6", new int[]{4, 5});
        morePassMap.put("6*22", new int[]{4, 5, 6});
        morePassMap.put("7*35", new int[]{4});
        morePassMap.put("8*70", new int[]{4});
        morePassMap.put("6*6", new int[]{5});
        morePassMap.put("6*7", new int[]{5, 6});
        morePassMap.put("7*21", new int[]{5});
        morePassMap.put("8*56", new int[]{5});
        morePassMap.put("7*7", new int[]{6});
        morePassMap.put("7*8", new int[]{6, 7});
        morePassMap.put("8*28", new int[]{6});
        morePassMap.put("8*8", new int[]{7});
        morePassMap.put("8*9", new int[]{7, 8});

        mapsEnd.put("1*1", 1);
        mapsEnd.put("2*1", 2);
        mapsEnd.put("3*3", 2);
        mapsEnd.put("4*6", 2);
        mapsEnd.put("5*10", 2);
        mapsEnd.put("6*15", 2);
        mapsEnd.put("3*1", 3);
        mapsEnd.put("3*4", 3);
        mapsEnd.put("4*4", 3);
        mapsEnd.put("5*20", 3);
        mapsEnd.put("6*20", 3);
        mapsEnd.put("6*35", 3);
        mapsEnd.put("4*1", 4);
        mapsEnd.put("4*5", 4);
        mapsEnd.put("4*11", 4);
        mapsEnd.put("5*5", 4);
        mapsEnd.put("6*50", 4);
        mapsEnd.put("5*1", 5);
        mapsEnd.put("5*6", 5);
        mapsEnd.put("5*16", 5);
        mapsEnd.put("5*26", 5);
        mapsEnd.put("6*6", 5);
        mapsEnd.put("6*1", 6);
        mapsEnd.put("6*7", 6);
        mapsEnd.put("6*22", 6);
        mapsEnd.put("6*42", 6);
        mapsEnd.put("6*57", 6);
        mapsEnd.put("7*1", 7);
        mapsEnd.put("7*7", 6);
        mapsEnd.put("7*8", 7);
        mapsEnd.put("7*21", 5);
        mapsEnd.put("7*35", 4);
        mapsEnd.put("7*120", 7);
        mapsEnd.put("8*1", 8);
        mapsEnd.put("8*8", 7);
        mapsEnd.put("8*9", 8);
        mapsEnd.put("8*28", 6);
        mapsEnd.put("8*56", 5);
        mapsEnd.put("8*70", 4);
        mapsEnd.put("8*247", 8);
    }
}
