package com.caipiao.domain.base;

import java.io.Serializable;

/**
 * 场次业务标准bean
 * @user kouyi
 * @date 2017-11-07
 */
public class MatchBean extends BaseBean implements Serializable {
    private static final long serialVersionUID = 1156975739021908091L;
    private String matchCode;//场次编号

    public String getMatchCode() {
        return matchCode;
    }

    public void setMatchCode(String matchCode) {
        this.matchCode = matchCode;
    }
}
