package com.caipiao.admin.service.ticket;

import com.caipiao.common.constants.Constants;
import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.plugin.InitPlugin;
import com.caipiao.common.scheme.SchemeUtils;
import com.caipiao.common.util.CalculationUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.lottery.PeriodMapper;
import com.caipiao.dao.match.MatchBasketBallSpMapper;
import com.caipiao.dao.match.MatchFootBallSpMapper;
import com.caipiao.dao.match.MatchGyjMapper;
import com.caipiao.dao.scheme.SchemeMapper;
import com.caipiao.dao.ticket.TicketMapper;
import com.caipiao.dao.ticket.TicketVoteMapper;
import com.caipiao.dao.user.UserMapper;
import com.caipiao.domain.base.SchemeBean;
import com.caipiao.domain.common.Task;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.lottery.Period;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.domain.user.User;
import com.caipiao.memcache.MemCached;
import com.caipiao.plugin.helper.PluginUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 后台出票相关服务
 * Created by kouyi on 2017/12/01.
 */
@Service("ticketService")
public class TicketService
{
    private final static String matchKey = "ticket_match_";
    private HashMap<String,LotteryUtils> lotteryUtilsMap = new HashMap<String, LotteryUtils>();//彩种工具类集合
    @Autowired
    private TicketVoteMapper ticketVoteMapper;
    @Autowired
    private TicketMapper ticketMapper;
    @Autowired
    private MatchFootBallSpMapper footBallSpMapper;
    @Autowired
    private MatchBasketBallSpMapper basketBallSpMapper;
    @Autowired
    private SchemeMapper schemeMapper;
    @Autowired
    private PeriodMapper periodMapper;
    @Autowired
    private MemCached memcached;
    @Autowired
    private MatchGyjMapper gyjMapper;

    /**
     * 查询出票商列表（后台管理）
     * @author kouyi
     */
    public List<Dto> queryTicketVoteList(Dto params)
    {
        return ticketVoteMapper.queryTicketVoteList(params);
    }

    /**
     * 新增出票商（后台管理）
     * @author kouyi
     */
    public int saveTicketVote(Dto params)
    {
        return ticketVoteMapper.saveTicketVote(params);
    }

    /**
     * 删除出票商（后台管理）
     * @author kouyi
     */
    public int deleteTicketVote(Dto params)
    {
        return ticketVoteMapper.deleteTicketVote(params);
    }

    /**
     * 修改出票商（后台管理）
     * @author kouyi
     */
    public int updateTicketVote(Dto params) {
        return ticketVoteMapper.updateTicketVote(params);
    }

    /**
     * 查询出票商分票规则列表（后台管理）
     * @author kouyi
     */
    public List<Dto> queryTicketRuleList(Dto params)
    {
        return ticketVoteMapper.queryTicketRuleList(params);
    }

    /**
     * 新增出票商分票规则（后台管理）
     * @author kouyi
     */
    public int saveTicketRule(Dto params)
    {
        return ticketVoteMapper.saveTicketRule(params);
    }

    /**
     * 删除出票商分票规则（后台管理）
     * @author kouyi
     */
    public int deleteTicketRule(Dto params)
    {
        return ticketVoteMapper.deleteTicketRule(params);
    }

    /**
     * 修改出票商分票规则（后台管理）
     * @author kouyi
     */
    public int updateTicketRule(Dto params) {
        return ticketVoteMapper.updateTicketRule(params);
    }

    /**
     * 查询出票控制参数列表（后台管理）
     * @author kouyi
     */
    public List<Dto> queryTicketConfigList(Dto params)
    {
        return ticketVoteMapper.queryTicketConfigList(params);
    }

    /**
     * 修改出票控制参数（后台管理）
     * @author kouyi
     */
    public int updateTicketConfig(Dto params) {
        return ticketVoteMapper.updateTicketConfig(params);
    }

    /**
     * 查询出票列表（后台管理）
     * @author kouyi
     */
    public List<Dto> queryTicketList(Dto params) throws Exception
    {
        return ticketMapper.queryTicketList(params);
    }

    /**
     * 实体店查询出票列表（后台管理）
     * @author kouyi
     */
    public List<Dto> queryTicketListShop(Dto params) throws Exception
    {
        return ticketMapper.queryTicketListShop(params);
    }

    /**
     * 查询票列表-总条数（后台管理）
     * @author kouyi
     */
    public int queryTicketListCount(Dto params) throws Exception
    {
        return ticketMapper.queryTicketListCount(params);
    }

    /**
     * 查询票信息（后台管理）
     * @author kouyi
     */
    public Dto queryTicketInfoById(Dto params) throws Exception
    {
        return ticketMapper.queryTicketInfoById(params.getAsLong("id"));
    }

    /**
     * 实体店手动设置票状态（后台管理）
     * @author kouyi
     */
    public int setTicketStatus(Dto params) throws Exception {
        int result = 0;
        if(StringUtil.isNotEmpty(params.getAsString("datas")))
        {
            JSONArray jsonArray = JSONArray.fromObject(params.getAsString("datas"));//解析参数串
            if(jsonArray != null && jsonArray.size() > 0)
            {
                for(int i = 0; i < jsonArray.size(); i ++)
                {
                    String codeSP = "";
                    JSONObject obj = jsonArray.getJSONObject(i);
                    int status = obj.getInt("optype");
                    Long id = obj.getLong("id");
                    //查询最新票状态
                    Dto ticket = ticketMapper.queryTicketInfoById(id);
                    if(ticket.getAsInteger("ticketStatus") != 0) {//只处理待出票状态的票
                        continue;
                    }
                    if(status == 1) {
                        status = SchemeConstants.TICKET_STATUS_OUTED;
                        //竞彩手动设置打票成功-取当前sp 组装出票赔率串
                        if(LotteryUtils.isJc(ticket.getAsString("lotteryId"))) {
                            codeSP = settingTzContentSpAndLose(ticket.getAsString("lotteryId"), ticket.getAsString("codes"));
                            if(StringUtil.isEmpty(codeSP)) {
                                params.put("dmsg","解析生成出票sp失败,请联系技术人员处理");
                                return 0;
                            }
                        }
                    } else {
                        status = SchemeConstants.TICKET_STATUS_FAIL;
                    }
                    String ticketId = "";
                    Random rd = new Random();
                    for (int x=0; x < 18; x++)
                    {
                        ticketId += rd.nextInt(10);
                    }
                    int random = (int)((Math.random() * 90) + 40);
                    ticketMapper.updateShopOutTicketStatus(status, SchemeConstants.ticketStatusMap.get(status), ticketId, random, codeSP, id);
                    result++;
                }
            }
            else
            {
                params.put("dmsg","无法解析参数串datas");
            }
        }
        else
        {
            params.put("dmsg","请至少选择一张票进行确认");
        }
        return result;
    }

    /**
     * 手动批量切票（后台管理）
     * @author kouyi
     */
    public int changeTicket(Dto params) throws Exception {
        int result = 0;
        if(StringUtil.isNotEmpty(params.getAsString("datas")))
        {
            JSONArray jsonArray = JSONArray.fromObject(params.getAsString("datas"));//解析参数串
            String voteId = params.getAsString("voteId");
            if(jsonArray != null && jsonArray.size() > 0)
            {
                for(int i = 0; i < jsonArray.size(); i ++)
                {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    Dto ticket = ticketMapper.queryTicketInfoById(obj.getLong("id"));
                    //状态为出票失败的票 才能进行切票
                    if(StringUtil.isNotEmpty(ticket)) {
                        if(ticket.getAsInteger("ticketStatus") == SchemeConstants.TICKET_STATUS_FAIL) {
                            Dto paramsDto = new BaseDto("id", obj.get("id"));
                            paramsDto.put("voteId", voteId);
                            int tempResult = ticketMapper.changeTicket(paramsDto);//执行删除
                            result += tempResult;
                        } else {
                            result += 1;
                        }
                    }
                }
            }
            else
            {
                params.put("dmsg","无法解析参数串datas");
            }
        }
        else
        {
            params.put("dmsg","请至少选择一条记录进行删除");
        }
        return result;
    }

    /**
     * 设置投注项赔率/让球/让分/大小分盘口(竞彩)
     * @param lotteryId
     * @param contents
     */
    public String settingTzContentSpAndLose(String lotteryId, String contents) {
        if(!LotteryUtils.isGyj(lotteryId)) {
            String[] splitTzcodes = contents.split("\\|");//提取投注内容
            boolean ishh = contents.startsWith(LotteryConstants.JCWF_PREFIX_HH) ? true : false;//是否为混投 true-是
            Dto spDto = null;

            //解析投注内容
            String[] tzcodes = splitTzcodes[1].split(",");
            StringBuilder tempBuilder = new StringBuilder();
            for (int index = 0; index < tzcodes.length; index++) {
                //解析单场次投注内容
                String[] tempCodes = tzcodes[index].split("=");//默认按单个玩法解析
                if (ishh) {
                    tempCodes = tzcodes[index].split(">");//竞彩混投解析
                }
                //查询竞彩足球赔率
                if (LotteryUtils.isJczq(lotteryId)) {
                    spDto = (Dto) memcached.get(LotteryConstants.jczqSpPrefix + tempCodes[0]);//从缓存中获取赔率信息
                    if (spDto == null) {
                        spDto = footBallSpMapper.queryFootBallSp(tempCodes[0]);//从数据库中查询赔率信息
                        memcached.set(LotteryConstants.jczqSpPrefix + tempCodes[0], spDto, 60);//在缓存中保留1分钟
                    }
                }
                //查询竞彩篮球赔率
                else if (LotteryUtils.isJclq(lotteryId)) {
                    spDto = (Dto) memcached.get(LotteryConstants.jclqSpPrefix + tempCodes[0]);//从缓存中获取赔率信息
                    if (spDto == null) {
                        spDto = basketBallSpMapper.queryBasketBallSp(tempCodes[0]);//从数据库中查询赔率信息
                        memcached.set(LotteryConstants.jclqSpPrefix + tempCodes[0], spDto, 60);//在缓存中保留1分钟
                    }
                }
                if (spDto != null) {
                    //根据投注项去匹配盘口和赔率信息
                    StringBuilder tempBuilder2 = new StringBuilder();
                    if (ishh) {
                        tempBuilder2.append(tempCodes[0] + "->");//拼装场次信息
                        //判断玩法类型,如果是让球胜平负/让分胜负,则设置让球/让分盘口
                        String[] xxs = tempCodes[1].split("=");
                        tempBuilder2.append(xxs[0]);
                        tempBuilder2.append("=");
                        //设置选项赔率
                        String[] xs = xxs[1].split("\\/");
                        for (int n = 0; n < xs.length; n++) {
                            if (LotteryConstants.JCWF_PREFIX_RFSF.equals(xxs[0])) {
                                String lose = spDto.getAsString("lose");
                                if (StringUtil.isEmpty(lose)) {
                                    return "";
                                }
                                tempBuilder2.append(xs[n].replace("-", "").replace(":", "") + "&" + lose + "@" + spDto.get(xxs[0] + xs[n]));
                            }
                            //如果玩法类型为大小分,则设置大小分盘口
                            else if (LotteryConstants.JCWF_PREFIX_DXF.equals(xxs[0])) {
                                String dxf = spDto.getAsString("dxf");
                                if (StringUtil.isEmpty(dxf)) {
                                    return "";
                                }
                                tempBuilder2.append(xs[n].replace("-", "").replace(":", "") + "&" + dxf + "@" + spDto.get(xxs[0] + xs[n]));
                            } else {
                                tempBuilder2.append(xs[n].replace("-", "").replace(":", "") + "@" + spDto.get(xxs[0] + xs[n]));
                            }
                            if (n != xs.length - 1) {
                                tempBuilder2.append("/");
                            }
                        }
                    } else {
                        //判断玩法类型,如果是让球胜平负/让分胜负,则设置让球/让分盘口
                        tempBuilder2.append(tempCodes[0]);
                        tempBuilder2.append("=");
                        String[] xxs = tempCodes[1].split("\\/");
                        for (int m = 0; m < xxs.length; m++) {
                            if (LotteryConstants.JCWF_PREFIX_RFSF.equals(splitTzcodes[0])) {
                                String lose = spDto.getAsString("lose");
                                if (StringUtil.isEmpty(lose)) {
                                    return "";
                                }
                                tempBuilder2.append(xxs[m].replace("-", "").replace(":", "") + "&" + lose + "@" + spDto.get(splitTzcodes[0] + xxs[m]));
                            }
                            //如果玩法类型为大小分,则设置大小分盘口
                            else if (LotteryConstants.JCWF_PREFIX_DXF.equals(splitTzcodes[0])) {
                                String dxf = spDto.getAsString("dxf");
                                if (StringUtil.isEmpty(dxf)) {
                                    return "";
                                }
                                tempBuilder2.append(xxs[m].replace("-", "").replace(":", "") + "&" + dxf + "@" + spDto.get(splitTzcodes[0] + xxs[m]));
                            } else {
                                tempBuilder2.append(xxs[m].replace("-", "").replace(":", "") + "@" + spDto.get(splitTzcodes[0] + xxs[m]));
                            }
                            if (m != xxs.length - 1) {
                                tempBuilder2.append("/");
                            }
                        }
                    }
                    tempBuilder.append(tempBuilder2.toString());
                    if (index != tzcodes.length - 1) {
                        tempBuilder.append(",");
                    }
                } else {
                    return "";
                }
            }
            return tempBuilder.toString();
        } else {
            Map<String, Double> gyjMatch = (Map<String, Double>) memcached.get("gyjspkey" + lotteryId);
            if(StringUtil.isEmpty(gyjMatch)) {
                Dto query = new BaseDto();
                query.put("lotteryId", lotteryId);
                List<Dto> matchList = gyjMapper.queryGyjMatchInfos(query);//查询对阵信息
                if(matchList != null && matchList.size() > 0) {
                    gyjMatch = new HashMap<>();
                    //封装对阵信息
                    for(Dto matchDto : matchList) {
                        gyjMatch.put(matchDto.getAsString("matchCode"), matchDto.getAsDoubleValue("sp"));
                    }
                }
                memcached.set("gyjspkey" + lotteryId, gyjMatch, 60);//缓存一分钟
            }
            String[] splitTzcodes = contents.split("\\|")[1].split("\\=");//提取投注内容
            StringBuffer spcodes = new StringBuffer();
            spcodes.append(splitTzcodes[0]).append("=");
            String[] choose = splitTzcodes[1].split("\\/");
            for(int n=0; n<choose.length; n++) {
                String sp = CalculationUtils.rd(gyjMatch.get(choose[n]));
                spcodes.append(choose[n]);
                spcodes.append("@");
                spcodes.append(sp);
                if(n != choose.length -1) {
                    spcodes.append("/");
                }
            }
            return spcodes.toString();
        }
    }

    /**
     * 设置方案详情-出票管理
     * @author  kouyi
     * @param   scheme    方案对象(源数据)
     * @param   params   方案详情对象(详情保存在该对象中)
     */
    public void settingSchemeDetailForTicket(Dto scheme, Dto params)
    {
        String lotteryId = scheme.getAsString("lotteryId");//彩种id
        if(LotteryUtils.isGyj(lotteryId))
        {
            settingGyjSchemeDetailForTicket(scheme, params);
        }
        else if(LotteryUtils.isJc(lotteryId))
        {
            settingJcSchemeDetailForTicket(scheme, params);//设置竞彩方案详情
        }
        else if(LotteryUtils.isZC(lotteryId))
        {
            settingZcSchemeDetailForTicket(scheme, params);//设置足彩方案详情
        }
        else if(LotteryUtils.isSzc(lotteryId))
        {
            settingSzcSchemeDetailForTicket(scheme, params);//设置数字彩方案详情
        }
    }

    /**
     * 设置冠亚军方案详情
     * @author  kouyi
     * @param   scheme    方案对象(源数据)
     * @param   params   方案详情对象(详情保存在该对象中)
     */
    private void settingGyjSchemeDetailForTicket(Dto scheme, Dto params)
    {
        String lotteryId = scheme.getAsString("lotteryId");
        params.put("ggfs", "单关");//设置过关方式

        Map<String, Dto> gyjMatch = (Map<String, Dto>) memcached.get("gyjkey" + lotteryId);
        if(StringUtil.isEmpty(gyjMatch)) {
            Dto query = new BaseDto();
            query.put("lotteryId", lotteryId);
            List<Dto> matchList = gyjMapper.queryGyjMatchInfos(query);//查询对阵信息
            if(matchList != null && matchList.size() > 0) {
                gyjMatch = new HashMap<>();
                //封装对阵信息
                for(Dto matchDto : matchList) {
                    gyjMatch.put(matchDto.getAsString("matchCode"), matchDto);
                }
            }
            memcached.set("gyjkey" + lotteryId, gyjMatch, 24 * 60 * 60);
        }

        StringBuffer choose = new StringBuffer();
        StringBuffer choosecode = new StringBuffer();
        String tzspContent = scheme.getAsString("codes").split("\\=")[1];//提取投注内容
        String[] codes = tzspContent.split("\\/");
        for(int m=0; m<codes.length; m++) {
            Dto match = gyjMatch.get(codes[m]);
            if(lotteryId.equals(LotteryConstants.GJ)) {
                choose.append(match.getAsString("teamName"));
            } else {
                choose.append(match.getAsString("teamName")).append("vs").append(match.getAsString("guestTeamName"));
            }
            choosecode.append(Integer.parseInt(codes[m]) < 10 ? ("0" + codes[m]) : codes[m]);
            if(m != codes.length-1) {
                choose.append("/");
                choosecode.append("/");
            }
        }
        params.put("ttxxcode", choosecode);
        params.put("ttxx", choose.toString());
    }

    /**
     * 设置竞彩方案详情
     * @author  kouyi
     * @param   scheme    方案对象(源数据)
     * @param   params   方案详情对象(详情保存在该对象中)
     */
    private void settingJcSchemeDetailForTicket(Dto scheme, Dto params)
    {
        String[] tzspContent = scheme.getAsString("codes").split("\\|");//提取投注内容
        /**
         * 设置投注选项
         */
        Dto tzxxDto = null;//投注选项对象
        List<Dto> tzxxList = new ArrayList<Dto>();//投注选项集合
        Dto ccxxDto = null;//场次选项对象
        List<Dto> ccxxList = null;//场次选项集合
        boolean ishh = tzspContent[0].indexOf(LotteryConstants.JCWF_PREFIX_HH) > -1? true : false;//是否混投,true表示是混投

        String tzwfname = LotteryConstants.playMethodMaps.get(tzspContent[0]);//根据玩法前缀获取玩法名称
        params.put("wfType", tzwfname);
        params.put("ggfs", tzspContent[2].replace("1*1","单关").replace("*","串").replace(",","，"));//设置过关方式

        String schemeOrderId = scheme.getAsString("schemeId");
        String schemeSp = (String) memcached.get("stdkey" + schemeOrderId);
        if(StringUtil.isEmpty(schemeSp)) {
            Scheme schemeInfo = schemeMapper.querySchemeInfoBySchemeOrderId(schemeOrderId);
            if(StringUtil.isEmpty(schemeInfo)) {
                return;
            }
            schemeSp = schemeInfo.getSchemeSpContent();
            memcached.set("stdkey" + schemeOrderId, schemeSp, 24 * 60 * 60);
        }
        Map<String, String> spMap = getSchemeCodeSp(schemeSp);
        //重新排序,按场次号的大小从小到大排序(场次选项也需要按从小到大排序)
        Map<String,Dto> ccxxMaps = new TreeMap<String,Dto>();
        Dto dccxxDto = null;
        String[] ccxxcodes = tzspContent[1].split(",");
        if(ishh)
        {//HH|20181102002>SPF=3/0,20181102003>SPF=3/1,20181102004>SPF=3/1,20181102018>RQSPF=3|4*1
            for(String ccxxcode : ccxxcodes)
            {
                String[] tempcodes = ccxxcode.split("\\>");//提取场次投注选项
                dccxxDto = new BaseDto("mcode", tempcodes[0]);
                dccxxDto.put("tzcodes",ccxxcode);//设置场次选项
                ccxxMaps.put(tempcodes[0],dccxxDto);
            }
        }
        else
        {//CBF|20181102001=1:1,20181102002=1:2,20181102009=1:1,20181102011=1:1|4*1
            for(String ccxxcode : ccxxcodes)
            {
                String[] tempcodes = ccxxcode.split("\\=");//提取场次投注选项
                dccxxDto = new BaseDto("mcode", tempcodes[0]);
                dccxxDto.put("tzcodes", ccxxcode);//设置场次选项
                ccxxMaps.put(dccxxDto.getAsString("mcode"), dccxxDto);
            }
        }

        String lotteryId = scheme.getAsString("lotteryId");
        if(LotteryUtils.isJczq(lotteryId)){
            params.put("jctype", 1);
        } else if(LotteryUtils.isJclq(lotteryId)) {
            params.put("jctype", 2);
        }
        //解析投注选项
        for(String key : ccxxMaps.keySet())
        {
            //设置投注场次信息
            tzxxDto = new BaseDto();
            //获取单场次投注数据对象
            Dto ccxxData = ccxxMaps.get(key);
            Dto matchInfo = (Dto) memcached.get(matchKey + lotteryId + key);
            if(StringUtil.isEmpty(matchInfo)) {
                //缓存中获取比赛数据
                if(LotteryUtils.isJczq(lotteryId))
                {
                    List<Dto> listMatchs = schemeMapper.queryJczqMatchesForTicket(schemeOrderId);
                    if(StringUtil.isNotEmpty(listMatchs)) {
                        for(Dto match : listMatchs) {
                            if(match.getAsString("matchCode").equals(key)) {
                                matchInfo = match;
                            }
                            memcached.set(matchKey + lotteryId + match.getAsString("matchCode"), match, 24 * 60 * 60);
                        }
                    }
                }
                else if(LotteryUtils.isJclq(lotteryId)) {
                    List<Dto> listMatchs = schemeMapper.queryJclqMatchesForTicket(schemeOrderId);
                    if (StringUtil.isNotEmpty(listMatchs)) {
                        for (Dto match : listMatchs) {
                            if (match.getAsString("matchCode").equals(key)) {
                                matchInfo = match;
                            }
                            memcached.set(matchKey + lotteryId + match.getAsString("matchCode"), match, 24 * 60 * 60);
                        }
                    }
                }
            }

            tzxxDto.put("week",matchInfo.getAsString("weekday") + matchInfo.getAsString("jcId"));//设置周信息
            tzxxDto.put("lname", matchInfo.getAsString("leagueName"));//设置联赛名称
            String hname = matchInfo.getAsString("hostName");
            String gname = matchInfo.getAsString("guestName");
            hname = StringUtil.isEmpty(hname)? "" : (hname.length() > 5? hname.substring(0,5) : hname);
            gname = StringUtil.isEmpty(gname)? "" : (gname.length() > 5? gname.substring(0,5) : gname);
            tzxxDto.put("hname",hname);//设置主队名
            tzxxDto.put("gname",gname);//设置客队名
            //设置比赛时间
            tzxxDto.put("mtime", matchInfo.getAsString("matchTime"));

            /**
             * 设置投注选项信息
             */
            //设置混投投注选项信息
            StringBuffer choose = new StringBuffer();
            if(ishh)
            {
                //20181102002>SPF=3/0,20181102003>SPF=3/1,20181102004>SPF=3/1,20181102018>RQSPF=3
                String[] ccxxs = ccxxData.getAsString("tzcodes").split("\\>");//获取单场次投注选项
                String[] xxs = ccxxs[1].split("\\=");
                String[] tempxxs = xxs[1].split("\\/");
                String pan = "";
                for(String xx : tempxxs)
                {
                    choose.append(LotteryConstants.jcXxNameMaps.get(xxs[0]+xx));
                    String sps = spMap.get(key + xxs[0] + xx);
                    if(StringUtil.isNotEmpty(sps)) {
                        String[] ss = sps.split("\\|");
                        pan = ss[0];
                        choose.append(ss[1]);
                    }
                    choose.append("/");
                }
                tzxxDto.put("xwf", LotteryConstants.playMethodMaps.get(xxs[0]) + pan);
            }
            //设置非混投投注选项信息
            else
            {
                //20181102001=1:1,20181102002=1:2,20181102009=1:1,20181102011=1:1
                String[] xxs = ccxxData.getAsString("tzcodes").split("\\=");
                String[] tempxxs = xxs[1].split("\\/");
                String pan = "";
                for(String xx : tempxxs)
                {
                    choose.append(LotteryConstants.jcXxNameMaps.get(tzspContent[0]+xx));
                    String sps = spMap.get(key + tzspContent[0] + xx);
                    if(StringUtil.isNotEmpty(sps)) {
                        String[] ss = sps.split("\\|");
                        pan = ss[0];
                        choose.append(ss[1]);
                    }
                    choose.append("/");
                }
                tzxxDto.put("xwf", LotteryConstants.playMethodMaps.get(tzspContent[0]) + pan);
            }
            tzxxDto.put("ccxxs", choose.toString().substring(0, choose.length()-1));//设置场次选项信息
            tzxxList.add(tzxxDto);
        }
        params.put("match", tzxxList);
    }

    /**
     * 设置老足彩方案详情
     * @author  kouyi
     * @param   scheme    方案对象(源数据)
     * @param   params   方案详情对象(详情保存在该对象中)
     */
    private void settingZcSchemeDetailForTicket(Dto scheme, Dto params)
    {
        //获取期次对阵
        String lotteryId = scheme.getAsString("lotteryId");
        Period period = periodMapper.queryPeriodByPerod(lotteryId, scheme.getAsString("period"));//查询期次
        Map<String,Object> matchMaps = JsonUtil.jsonToMap(period.getMatches());//提取场次信息

        StringBuffer cs = new StringBuffer();
        String[] tzContents = scheme.getAsString("codes").split("\\;");
        for(int m = 0; m < tzContents.length; m++) {
            cs.append(tzContents[m].split("\\:")[0]);
            if(m != tzContents.length-1) {
                cs.append("</br>");
            }
            /*String[] dcon = tzContents[m].split("\\:")[0].split("\\,");
            if(dcon.length == matchMaps.size()) {
                Dto tzxxDto = null;//投注选项对象
                List<Dto> tzxxList = new ArrayList<Dto>();//用来保存场次信息和投注选项
                //循环读取场次信息
                for(int i = 1; i <= matchMaps.size(); i ++)
                {
                    Map<String,String> matchMap = (Map<String,String>) matchMaps.get("" + i);//提取单场次信息
                    tzxxDto = new BaseDto();
                    tzxxDto.put("index", matchMap.get("index"));
                    tzxxDto.put("league", matchMap.get("matchname"));
                    tzxxDto.put("teamName", matchMap.get("homeTeamView") + " vs " + matchMap.get("awayTeamView"));
                    tzxxDto.put("matchTime", matchMap.get("matchTime"));

                    StringBuffer buffer = new StringBuffer();
                    for(int x = 0; x < dcon[i-1].length(); x++) {
                        String choose = String.valueOf(dcon[i-1].charAt(x));
                        if(choose.equals("3")) {
                            choose = "胜";
                        } else if(choose.equals("1")) {
                            choose = "平";
                        } else if(choose.equals("0")) {
                            choose = "负";
                        } else {
                            choose = "";
                        }
                        buffer.append(choose);
                        if(x != dcon[i-1].length() - 1) {
                            buffer.append("/");
                        }
                    }
                    tzxxDto.put("ccxxs", buffer.toString());
                    tzxxList.add(tzxxDto);
                }
                params.put("match", tzxxList);
            }*/
        }
        params.put("match", cs.toString());
    }

    /**
     * 设置数字彩方案详情
     * @author  kouyi
     * @param   scheme    方案对象(源数据)
     * @param   params   方案详情对象(详情保存在该对象中)
     */
    private void settingSzcSchemeDetailForTicket(Dto scheme, Dto params)
    {
        //获取期次对阵
        String lotteryId = scheme.getAsString("lotteryId");
        StringBuffer cs = new StringBuffer();
        String[] tzContents = scheme.getAsString("codes").split("\\;");
        for(int m = 0; m < tzContents.length; m++) {
            String[] tempContent = tzContents[m].split("\\:");
            cs.append(tempContent[0]);
            if(m != tzContents.length-1) {
                cs.append("</br>");
            }
            if(m == 0) {
                params.put("wfType", LotteryConstants.playMethodMaps.get(lotteryId + "-" + tempContent[1] + ":" + tempContent[2]));
            }
        }
        params.put("match", cs.toString());
    }

    /**
     * 将用户订单sp串格式化为map用来生成票对应的sp串-算奖使用
     * @param schemeCodeSp
     * @return
     */
    private static Map<String, String> getSchemeCodeSp(String schemeCodeSp) {
        Map<String, String> spMap = new HashMap<>();
        if(StringUtil.isEmpty(schemeCodeSp)) {
            return spMap;
        }
        schemeCodeSp = schemeCodeSp.replaceAll("\\(", "&").replaceAll("\\)", "");
        String[] cs = PluginUtil.splitter(schemeCodeSp, "|");
        if(cs.length != 3) {
            return null;
        }

        String[] tdan = PluginUtil.splitter(cs[1], "$");
        for(String dan : tdan) {
            String[] sps = PluginUtil.splitter(dan, ",");
            if (schemeCodeSp.indexOf(">") > -1) {//混投
                for (String sp : sps) {
                    String[] ms = PluginUtil.splitter(sp, ">");
                    String[] xs = PluginUtil.splitter(ms[1], "+");
                    for (String ch : xs) {
                        String[] alx = PluginUtil.splitter(ch, "=");
                        String[] fs = PluginUtil.splitter(alx[0], "&");//针对让球胜平负、让分胜负、大小分处理分值
                        if (alx[0].indexOf(LotteryConstants.JCWF_PREFIX_RQSPF) > -1 || alx[0].indexOf(LotteryConstants.JCWF_PREFIX_RFSF) > -1 || alx[0].indexOf(LotteryConstants.JCWF_PREFIX_DXF) > -1) {
                            alx[0] = fs[0];
                        }
                        String[] gs = PluginUtil.splitter(alx[1], "/");
                        for (String g : gs) {
                            String[] s = PluginUtil.splitter(g, "&");
                            if (alx[0].indexOf(LotteryConstants.JCWF_PREFIX_RFSF) > -1 || alx[0].indexOf(LotteryConstants.JCWF_PREFIX_DXF) > -1) {
                                spMap.put(ms[0] + alx[0] + s[0], "(" + fs[1] + ")|(" + s[1] + ")");
                            } else {
                                spMap.put(ms[0] + alx[0] + s[0], "|(" + s[1] + ")");
                            }
                        }
                    }
                }
            } else {
                for (String sp : sps) {
                    String[] ms = PluginUtil.splitter(sp, "=");
                    String[] fs = PluginUtil.splitter(ms[0], "&");//针对让球胜平负、让分胜负、大小分处理分值
                    if (cs[0].indexOf(LotteryConstants.JCWF_PREFIX_RQSPF) > -1 || cs[0].indexOf(LotteryConstants.JCWF_PREFIX_RFSF) > -1 || cs[0].indexOf(LotteryConstants.JCWF_PREFIX_DXF) > -1) {
                        ms[0] = fs[0];
                    }
                    String[] xs = PluginUtil.splitter(ms[1], "/");
                    for (String ch : xs) {
                        String[] alx = PluginUtil.splitter(ch, "&");
                        if (cs[0].indexOf(LotteryConstants.JCWF_PREFIX_RFSF) > -1 || cs[0].indexOf(LotteryConstants.JCWF_PREFIX_DXF) > -1) {
                            spMap.put(ms[0] + cs[0] + alx[0], "(" + fs[1] + ")|(" + alx[1] + ")");
                        } else {
                            spMap.put(ms[0] + cs[0]  + alx[0], "|(" + alx[1] + ")");
                        }
                    }
                }
            }
        }
        return spMap;
    }
}
