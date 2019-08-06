package com.caipiao.domain.jjyh;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Kouyi on 2018/5/8.
 */
public class JjyhOne implements Serializable {
    private static final long serialVersionUID = -220798077185991099L;
    private String minPrize;//最低奖金
    private String maxPrize;//最高奖金
    private String desc;//描述-页面使用
    private List<JjyhTwo> twoList;//优化详情

    public String getMinPrize() {
        return minPrize;
    }

    public void setMinPrize(String minPrize) {
        this.minPrize = minPrize;
    }

    public String getMaxPrize() {
        return maxPrize;
    }

    public void setMaxPrize(String maxPrize) {
        this.maxPrize = maxPrize;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<JjyhTwo> getTwoList() {
        return twoList;
    }

    public void setTwoList(List<JjyhTwo> twoList) {
        this.twoList = twoList;
    }
}
