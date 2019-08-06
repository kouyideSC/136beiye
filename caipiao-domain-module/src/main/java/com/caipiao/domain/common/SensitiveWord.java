package com.caipiao.domain.common;

import java.io.Serializable;
import java.util.Date;

/**
 * 敏感词对象
 * Created by kouyi on 2017/11/04.
 */
public class SensitiveWord implements Serializable {
    private static final long serialVersionUID = -6918239852609577619L;
    private Long id;
    private String word;//敏感词
    private Date createTime;//录入时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}