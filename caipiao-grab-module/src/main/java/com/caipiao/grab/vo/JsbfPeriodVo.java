package com.caipiao.grab.vo;

import java.io.Serializable;

/**
 * Created by Kouyi on 2017/11/22.
 */
public class JsbfPeriodVo implements Serializable {
    private static final long serialVersionUID = 2193449157263195693L;
    private String period;//期次
    private String title;//标题
    private boolean show;//是否显示

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

}
