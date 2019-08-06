package com.caipiao.common.lottery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 排列组合计算工具类
 * Created by Kouyi on 2017/12/8.
 */
public class CombineUtil {
    private static Map<String, int[]> morePassMap = new HashMap<>();//多串过关
    private static HashMap<String,Integer> mapsEnd = new HashMap<>();//串关最大边界

    public static void main(String[] args) {
        int sm = combine(null, new int[]{2,1,1,1,1,1,1,1,3,2}, 9);
        System.out.println(sm);

        List<int[]> list2 = new ArrayList<>();//设胆场次
        //int[] s1 = new int[]{2,2};//一场比赛两种玩法，分别选2个选项
        //list2.add(s1);
        int[] s11 = new int[]{2,4,1,3};//一场比赛四种玩法，分别选2,4,1,3个选项
        list2.add(s11);

        List<int[]> list = new ArrayList<>();//设拖场次
        int[] s1 = new int[]{2,2};
        int[] s2 = new int[]{2,4,1,3};
        int[] s3 = new int[]{2,4};//一场比赛两种玩法，分别选2,4个选项
        int[] s4 = new int[]{2,2,3,3,1};//一场比赛五种玩法，分别选2,2,3,3,1个选项
        list.add(s1);
        list.add(s2);
        list.add(s3);
        list.add(s4);

        int zhuSum11 = combine(null, list, new String[]{"3*1"});
        System.out.println(zhuSum11);
        int zhuSum = combine(null, list, new String[]{"3*3"});
        System.out.println(zhuSum);
        int zhuSum0 = combine(null, list, new String[]{"3*4"});
        System.out.println(zhuSum0);
        int zhuSum2 = combine(null, list, new String[]{"4*4"});
        System.out.println(zhuSum2);
        int zhuSum3 = combine(null, list, new String[]{"4*5"});
        System.out.println(zhuSum3);
        int zhuSum4 = combine(null, list, new String[]{"4*6"});
        System.out.println(zhuSum4);
        int zhuSum5 = combine(null, list, new String[]{"4*11"});
        System.out.println(zhuSum5);
    }

    /**
     * 计算竞彩注数
     * * @param d_choose
     *      胆场次-不设胆 则传空
     * @param t_choose
     *      拖场次-不能为空 购买的场次和选项数
     * @param pass
     *      串关方式-如3*1,3*3等
     * @return
     *      -99：表示参数错误
     */
    public static int combine(List<int[]> d_choose, List<int[]> t_choose, String[] pass) {
        int code = -99;
        if(t_choose == null || t_choose.size() == 0 || pass == null || pass.length == 0) {
            return code;
        }
        int maxMulPass = 0;//最大串关方式
        int minPass = 99;//最小串关方式需要的最低场次数
        int maxPass = 0;//最大串关方式需要的最高场次数
        for(String ps : pass) {
            if(ps.length() < 3) {
                return code;
            }
            int max = Integer.parseInt(ps.substring(0,1));
            if(maxPass < max) {
                maxPass = max;
            }
            if(minPass > max) {
                minPass = max;
            }
            int min = Integer.parseInt(ps.substring(2));
            if(maxMulPass < min) {
                maxMulPass = min;
            }
        }
        if((maxMulPass > 1 && pass.length > 1) || (maxMulPass > 1 && d_choose != null && d_choose.size() > 0)) {//多串过关只能单选|多串不能设胆
            return code;
        }
        if(d_choose != null && d_choose.size() >= minPass) {//最低串关方式不能小于胆的场次数
            return code;
        }

        int[] d_choo = null;//拖胆处理
        if(d_choose == null) {
            d_choose = new ArrayList<>();
        } else {
            d_choo = new int[d_choose.size()];
            for(int ch=0; ch <d_choose.size(); ch++) {
                int ix = 0;
                for(int c : d_choose.get(ch)) {
                    ix+=c;
                }
                d_choo[ch] = ix;
            }
        }
        if(maxPass > d_choose.size() + t_choose.size()) {//串关必须的最高场次数不能大于所选(胆场次数+拖场次数)
            return code;
        }

        int sumZhuShu = 0;
        for(String p : pass) {
            if (!morePassMap.containsKey(p)) {//只要有一个串关方式不正确，则不能再进行计算，因为结果一定错误，返回-99
                return code;
            }
            int[] passCombine = morePassMap.get(p);//获取串关组合
            int maxMnumber = getMaxPassType(p);
            int[] tchoose = new int[t_choose.size()];
            for (int x = 0; x < t_choose.size(); x++) {
                tchoose[x] = x;
            }
            List<int[]> chaiMax = combine(tchoose, maxMnumber - d_choose.size());
            if (chaiMax == null || chaiMax.size() == 0) {
                return code;
            }

            for (int y = 0; y < chaiMax.size(); y++) {
                List<int[]> chaiZhu = new ArrayList<>();
                int[] max = chaiMax.get(y);
                for (int z = 0; z < max.length; z++) {
                    chaiZhu.add(t_choose.get(max[z]));
                }
                List<int[]> resultList = new ArrayList<>();
                combineMulPlay(chaiZhu, new int[chaiZhu.size()], 0, resultList);
                for (int[] choose : resultList) {
                    for (int cb : passCombine) {
                        if(d_choose != null && d_choose.size() > 0) {
                            sumZhuShu += combine(d_choo, choose, cb);
                        } else {
                            sumZhuShu += combine(null, choose, cb);
                        }
                    }
                }
            }
        }
        return sumZhuShu;
    }

    /**
     * 从d+t[d|t=选号个数]中取n个数组合-玩法计算总注数
     * @param d 可以不设胆 则传空
     * @param t 必须有拖码场次
     * @param n
     * @return
     */
    public static int combine(int[] d, int[] t, int n) {
        int num = 0;
        if(t == null || t.length == 0) {
            return num;
        }

        List<Integer> resultList = new ArrayList<>();
        if(d == null || d.length == 0) {
            sequence(t, 0, new int[n], 0, resultList);
            for(Integer s : resultList) {
                num += s.intValue();
            }
        }
        else {
            int dn = d.length;
            int tn = t.length;
            if(dn > 8 || dn + tn < n) {//胆码个数范围0~8个|胆和拖不能低于n个
                return num;
            }
            int nd = n - d.length;
            sequence(t, 0, new int[nd], 0, resultList);
            for(Integer s : resultList) {
                int sd = 1;
                for(int dm : d) {
                    sd *= dm;
                }
                num += (sd * s);
            }
        }
        return num;
    }

    /**
     * 排列组合-从m[m=选号个数]中取n个数组合-玩法计算总注数
     * @param m
     * @param n
     */
    public static int combineNum(int[] m, int n) {
        List<Integer> resultList = new ArrayList<>();
        sequence(m, 0, new int[n], 0, resultList);
        int num = 0;
        for(Integer s : resultList) {
            num += s.intValue();
        }
        return num;
    }

    /**
     * 排列组合-从m[m=选号个数]中取n个数组合
     * @param m
     * @param n
     */
    public static List<int[]> combine(int[] m, int n) {
        List<int[]> resultList = new ArrayList<>();
        sequenceCombine(m, 0, new int[n], 0, resultList);
        return resultList;
    }

    /**
     * 多元素排列组合-类似拆票算法-返回组合列表
     * @param targetList 目标数据列表
     * @param combineTempList 组合临时数据列表
     * @param combineTempIndex 组合结果数据开始索引下标
     * @param resultList
     */
    public static void combineMulPlay(List<int[]> targetList, int[] combineTempList, int combineTempIndex, List<int[]> resultList) {
        if (targetList.size() == 1) {
            int[] end = targetList.get(0);
            for (int i = 0; i < end.length; i++) {
                combineTempList[combineTempIndex] = end[i];
                int[] tempList = new int[combineTempList.length];
                for(int c=0; c<combineTempList.length; c++) {
                    tempList[c] = combineTempList[c];
                }
                resultList.add(tempList);
            }
        } else {
            int[] first = targetList.get(0);
            List<int[]> tempList = new ArrayList<int[]>(targetList);
            tempList.remove(0);
            for (int i = 0; i < first.length; i++) {
                combineTempList[combineTempIndex] = first[i];
                combineMulPlay(tempList, combineTempList, combineTempIndex + 1, resultList);
            }
        }
    }
    
    /**
     * 列举排列组合-返回组合列表
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

    /**
     * 列举排列组合-返回注数列表
     * @param targetList 目标数据列表
     * @param targetIndex 目标数据开始索引下标
     * @param combineTempList 组合临时数据列表
     * @param combineTempIndex 组合结果数据开始索引下标
     * @return resultList
     */
    private static void sequence(int[] targetList, int targetIndex, int[] combineTempList, int combineTempIndex, List<Integer> resultList) {
        int size = combineTempList.length;
        int count = combineTempIndex + 1;
        if (count > size) {//结束
            int sum = 1;
            for(int nm : combineTempList) {
                sum*=nm;
            }
            resultList.add(sum);
            return;
        }
        //递归遍历
        for (int i = targetIndex; i < targetList.length + count - size; i++) {
            combineTempList[combineTempIndex] = targetList[i];
            sequence(targetList, i + 1, combineTempList, combineTempIndex + 1, resultList);
        }
    }

    /**
     * 竞彩投注过关方式至少需要投注场次数
     * @param passType
     * @return
     */
    public static int getMaxPassType(String passType){
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
    public static int getPassType(String passType){
        Integer obj = mapsEnd.get(passType);
        if ( obj != null ) {
            return obj.intValue();
        } else {
            return -1;
        }
    }

    static {
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
