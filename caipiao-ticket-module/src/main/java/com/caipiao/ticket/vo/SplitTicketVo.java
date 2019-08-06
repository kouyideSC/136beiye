package com.caipiao.ticket.vo;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.plugin.helper.PluginUtil;

import java.io.Serializable;
import java.util.*;

/**
 * 拆票结果对象
 * Created by Kouyi on 2017/12/04.
 */
public class SplitTicketVo implements Serializable {
    private static final long serialVersionUID = -3242722132769291701L;

    private Map<String, List<String>> mapOrder;//玩法对应所有方案编号
    private Map<String, List<SchemeTicket>> mapTicket;//拆票完成后小票汇总
    private List<SchemeTicket> ticketList;//混投中包含的单一玩法-独立出来继续分票

    public Map<String, List<String>> getMapOrder() {
        return mapOrder;
    }

    public void setMapOrder(Map<String, List<String>> mapOrder) {
        this.mapOrder = mapOrder;
    }

    public Map<String, List<SchemeTicket>> getMapTicket() {
        return mapTicket;
    }

    public void setMapTicket(Map<String, List<SchemeTicket>> mapTicket) {
        this.mapTicket = mapTicket;
    }

    public List<SchemeTicket> getTicketList() {
        return ticketList;
    }

    public void setTicketList(List<SchemeTicket> ticketList) {
        this.ticketList = ticketList;
    }

    /**
     * 保存方案拆好的单一玩法小票
     * @param tickets
     */
    public void addAllTicket(List<SchemeTicket> tickets) {
        if(StringUtil.isEmpty(tickets)) {
            return;
        }
        if(StringUtil.isEmpty(ticketList)) {
            ticketList = new ArrayList<>();
        }
        ticketList.addAll(tickets);
    }

    /**
     * 保存方案拆好的非单一玩法小票
     * @param orderId
     * @param tickets
     */
    public void putTicket(String orderId, List<SchemeTicket> tickets) {
        if(StringUtil.isEmpty(tickets)) {
            return;
        }
        if(StringUtil.isEmpty(mapTicket)) {
            mapTicket = new HashMap<>();
        }

        List<SchemeTicket> list = null;
        if(mapTicket.containsKey(orderId)) {
            list = mapTicket.get(orderId);
        } else {
            list = new ArrayList<>();
        }
        list.addAll(tickets);
        mapTicket.put(orderId, list);
    }

    /**
     * 保存方案编号
     * @param playTypeId
     * @param orderId
     */
    public void putOrder(String playTypeId, String orderId) {
        if(StringUtil.isEmpty(playTypeId) || StringUtil.isEmpty(orderId)) {
            return;
        }
        if(StringUtil.isEmpty(mapOrder)) {
            mapOrder = new HashMap<>();
        }

        List<String> list = null;
        if(mapOrder.containsKey(playTypeId)) {
            list = mapOrder.get(playTypeId);
        } else {
            list = new ArrayList<>();
        }
        if(!list.contains(orderId)) {
            list.add(orderId);
            mapOrder.put(playTypeId, list);
        }
    }
}