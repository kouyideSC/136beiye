package com.caipiao.common.keyword;

import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.common.SensitiveWord;

import java.util.*;

/**
 * 初始化敏感词库 Created by kouyi on 2017/9/25.
 */
public class SensitiveWordUtil {

    // 敏感词库
    public static HashMap sensitiveWordMap;

    public static void main(String[] args) {
        List<SensitiveWord> fl = new ArrayList<SensitiveWord>();
        SensitiveWord wl = new SensitiveWord();
        wl.setWord("邓小平");
        fl.add(wl);
        System.out.println(isSensitiveWord("邓小平", fl));
    }

    /**
     * 检查字符串是否敏感词
     * @param text
     * @param wordList
     * @return
     */
    public static boolean isSensitiveWord(String text, List<SensitiveWord> wordList) {
        if (StringUtil.isEmpty(wordList) || StringUtil.isEmpty(text)) {
            return false;
        }
        // 初始化敏感词库对象
        SensitiveWordUtil sensitiveWordUtil = new SensitiveWordUtil();
        // 构建敏感词库
        sensitiveWordUtil.initKeyWord(wordList);
        return SensitiveWordUtil.getSensitiveWord(text);
    }

    /**
     * 获取敏感词内容
     * @param txt
     * @return
     */
    private static boolean getSensitiveWord(String txt) {
        for (int index = 0; index < txt.length(); index++) {
            int length = checkSensitiveWord(txt, index);
            if (length > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查敏感词数量
     * @param txt
     * @param beginIndex
     * @return
     */
    private static int checkSensitiveWord(String txt, int beginIndex) {
        boolean flag = false;
        int matchFlag = 0;
        char word = 0;
        Map nowMap = sensitiveWordMap;
        for (int i = beginIndex; i < txt.length(); i++) {
            word = txt.charAt(i);
            nowMap = (Map) nowMap.get(word);
            if (nowMap == null) {
                break;
            }
            matchFlag++;
            if ("1".equals(nowMap.get("isEnd").toString())) {
                flag = true;
            }
        }
        if (!flag) {
            matchFlag = 0;
        }
        return matchFlag;
    }

    /**
     * 初始化敏感词结构
     * @param words
     * @return
     */
    private Map initKeyWord(List<SensitiveWord> words) {
        try {
            // 从敏感词集合对象中取出敏感词并封装到Set集合中
            Set<String> wordSet = new HashSet<String>();
            for (SensitiveWord s : words) {
                wordSet.add(s.getWord());
            }
            // 敏感词加入到HashMap中
            addSensitiveWordToHashMap(wordSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sensitiveWordMap;
    }

    /**
     * 封装敏感词库
     * @param wordSet
     */
    private void addSensitiveWordToHashMap(Set<String> wordSet) {
        sensitiveWordMap = new HashMap(wordSet.size());
        String key = null;
        Map nowMap = null;
        Map<String, String> newWorMap = null;
        Iterator<String> it = wordSet.iterator();
        while (it.hasNext()) {
            key = it.next();
            nowMap = sensitiveWordMap;
            for (int i = 0; i < key.length(); i++) {
                char keyChar = key.charAt(i);
                Object wordMap = nowMap.get(keyChar);
                if (wordMap != null) {
                    nowMap = (Map) wordMap;
                } else {
                    newWorMap = new HashMap<String, String>();
                    newWorMap.put("isEnd", "0");
                    nowMap.put(keyChar, newWorMap);
                    nowMap = newWorMap;
                }

                // 如果该字是当前敏感词的最后一个字，则标识为结尾字
                if (i == key.length() - 1) {
                    nowMap.put("isEnd", "1");
                }
            }
        }
    }
}
