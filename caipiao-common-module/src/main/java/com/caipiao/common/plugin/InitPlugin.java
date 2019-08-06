package com.caipiao.common.plugin;

import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.plugin.helper.GamePluginAdapter;

import java.util.HashMap;

/**
 * Created by kouyi on 2017/7/24.
 */
public abstract class InitPlugin {

    /**
     * 初始化彩种插件
     * @param mapPlugin
     * @param gid
     * @return
     */
    public static GamePluginAdapter getPlugin(HashMap<String, GamePluginAdapter> mapPlugin, String gid) {
        try {
            GamePluginAdapter plugin = mapPlugin.get(gid);
            if (plugin == null) {
                plugin = (GamePluginAdapter) Thread.currentThread().getContextClassLoader().loadClass("com.caipiao.plugin.Lottery" + gid).newInstance();
                if (plugin != null) {
                    mapPlugin.put(gid, plugin);
                }
            }
            return plugin;
        } catch(Exception e) {
            return null;
        }
    }

    /**
     * 初始化彩种工具类型
     * @author  mcdog
     * @param   lotteryUtilsMap     彩种工具类型集合(以彩种id为key)
     * @param   lotteryId           彩种id
     */
    public static LotteryUtils getLotteryUtils(HashMap<String,LotteryUtils> lotteryUtilsMap,String lotteryId)
    {
        LotteryUtils lotteryUtils = null;
        try
        {
            lotteryUtils = lotteryUtilsMap.get(lotteryId);
            if(lotteryUtils == null)
            {
                lotteryUtils = (LotteryUtils)Thread.currentThread().getContextClassLoader().loadClass("com.caipiao.common.lottery.Lottery" + lotteryId + "Utils").newInstance();
                if(lotteryUtils != null)
                {
                    lotteryUtilsMap.put(lotteryId,lotteryUtils);
                }
            }
        }
        catch(Exception e)
        {
            return null;
        }
        return  lotteryUtils;
    }
}