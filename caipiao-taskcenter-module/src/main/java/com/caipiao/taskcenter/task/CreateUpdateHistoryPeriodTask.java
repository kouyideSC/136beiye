package com.caipiao.taskcenter.task;

import com.caipiao.common.constants.Constants;
import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.vo.PeriodVo;
import com.caipiao.service.common.TaskService;
import com.caipiao.service.lottery.PeriodService;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

/**
 * 历史期次文件更新任务
 * @author 	mcdog
 */
@Component("createUpdateHistoryPeriodTask")
public class CreateUpdateHistoryPeriodTask
{
	private static Logger logger = LoggerFactory.getLogger(CreateUpdateHistoryPeriodTask.class);

	@Value("${api.period.filepath}")
	private String filepath;

	@Autowired
	private TaskService taskService;

	@Autowired
	private PeriodService periodService;

	private static final Map<String,String> lotteryTaskMaps = new HashMap<String,String>();//彩种任务集合(以彩种id为key,彩种id为值)

	private static final Map<String,String> pcMaps = new HashMap<String,String>();//奖池key映射(将中文key映射为英文或字母key)

	static
	{
		lotteryTaskMaps.put(LotteryConstants.SSQ, LotteryConstants.SSQ);
		lotteryTaskMaps.put(LotteryConstants.FC3D, LotteryConstants.FC3D);
		lotteryTaskMaps.put(LotteryConstants.QLC, LotteryConstants.QLC);
		lotteryTaskMaps.put(LotteryConstants.DLT, LotteryConstants.DLT);
		lotteryTaskMaps.put(LotteryConstants.QXC, LotteryConstants.QXC);
		lotteryTaskMaps.put(LotteryConstants.PL5, LotteryConstants.PL5);
		lotteryTaskMaps.put(LotteryConstants.PL3, LotteryConstants.PL3);
		lotteryTaskMaps.put(LotteryConstants.K3_JL, LotteryConstants.K3_JL);
		lotteryTaskMaps.put(LotteryConstants.K3_AH, LotteryConstants.K3_AH);
		lotteryTaskMaps.put(LotteryConstants.K3_JS, LotteryConstants.K3_JS);
		lotteryTaskMaps.put(LotteryConstants.SSC_CQ, LotteryConstants.SSC_CQ);
		lotteryTaskMaps.put(LotteryConstants.SSC_JX, LotteryConstants.SSC_JX);
		lotteryTaskMaps.put(LotteryConstants.X511_GD, LotteryConstants.X511_GD);
		lotteryTaskMaps.put(LotteryConstants.X511_SD, LotteryConstants.X511_SD);
		lotteryTaskMaps.put(LotteryConstants.X511_SH, LotteryConstants.X511_SH);
		lotteryTaskMaps.put(LotteryConstants.SFC, LotteryConstants.SFC);//胜负彩和任九
		//lotteryTaskMaps.put(LotteryConstants.RXJ, LotteryConstants.RXJ);
		lotteryTaskMaps.put(LotteryConstants.JQC, LotteryConstants.JQC);
		lotteryTaskMaps.put(LotteryConstants.BQC, LotteryConstants.BQC);

		pcMaps.put("投注总金额","xl");
		pcMaps.put("奖池累计金额","gc");
	}


	/**
	 * 创建/更新历史期次文件
	 * @author	sjq
	 */
	public void createUpdateHistoryPeriodFile()
	{
		try
		{
			//循环彩种集合,根据条件依次更新历史期次文件
			Dto params = new BaseDto("sellStatus",2);//已截止的期次
			params.put("minState",4);//计奖状态值>=4(开奖号审核成功)
			params.put("maxSellEndTime", DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//销售截止时间必须早于当前时间
			params.put("orderBySorts","period desc");//按期次降序排列
			params.put("pstart",0);
			params.put("psize",15);//只读取最近15条
			for(Map.Entry<String,String> entry : lotteryTaskMaps.entrySet())
			{
				try
				{
					//查询待执行的任务
					logger.debug("[历史期次文件-" + (entry.getKey()) + "]更新开始");
					params.put("lotteryId",entry.getKey());
					String taskName = Constants.periodHistoryUpdateTaskMaps.get(entry.getKey());
					boolean hasTask = taskService.isHasTask(taskName);
					if(hasTask)
					{
						boolean isszc = LotteryUtils.isSzc(entry.getKey())? true : false;//是否为数字彩
						boolean isk3 = LotteryUtils.isK3(entry.getKey())? true : false;//是否为快三
						boolean ismp = LotteryUtils.isMp(entry.getKey())? true : false;//是否为慢频
						boolean iszc = LotteryUtils.isZC(entry.getKey())? true : false;//是否为足彩
						boolean showMatchAttr = iszc? true : false;//足彩,则显示对阵信息
						boolean showDxjoAttr = (isszc && !isk3)? true : false;//数字彩(不包含快三),则显示大小比例和奇偶比例
						boolean showpgpcAttr = LotteryUtils.isKp(entry.getKey())? false : true;//快频,不显示奖级/奖池
						boolean showXtHzDxDsAttr = isk3? true : false;//快三,则显示开奖形态/和值/大小/单双

						//查询相关业务数据
						long start = System.currentTimeMillis();
						List<Dto> dataList = periodService.queryPeriods(params);//查询期次信息
						if(dataList != null && dataList.size() > 0)
						{
							List<Dto> periodList = new ArrayList<Dto>();
							Dto periodDto = null;
							for(Dto data : dataList)
							{
								//转换为前端展示的对象
								periodDto = new BaseDto();
								periodDto.put("lid",entry.getKey());//设置彩种id

								//设置彩种名称,如果是胜负彩,则需要设置彩种名为胜负彩/任九
								periodDto.put("lname",data.getAsString("lotteryName"));//设置彩种名称
								if(LotteryConstants.SFC.equals(entry.getKey()))
								{
									periodDto.put("lname",periodDto.getAsString("lname") + "/任九");
								}

								periodDto.put("pid",data.getAsString("period"));//设置期次号
								periodDto.put("pname",periodDto.getAsString("pid") + "期");//设置期次名称

								//设置期次号简称
								periodDto.put("spid",periodDto.getAsString("pid").substring(4));
								if(isk3)
								{
									periodDto.put("spid",periodDto.getAsString("pid").substring(8));//设置快三期次号简称
								}

								periodDto.put("kstatus",data.getAsInteger("state") < 3? "0" : "1");//设置开奖状态
								periodDto.put("kcode",data.getAsString("drawNumber"));//设置开奖号码

								//设置开奖时间
								periodDto.put("ktime",data.getAsString("drawNumberTime"));//设置开奖时间
								if(StringUtil.isNotEmpty(periodDto.get("ktime")))
								{
									periodDto.put("ktime",periodDto.getAsString("ktime").substring(0,10) + " 开奖");
								}
								//获取奖级/奖池信息
								List<Map<String,Object>> pglist = new ArrayList<Map<String,Object>>();//奖级信息
								List<Map<String,Object>> pclist = new ArrayList<Map<String,Object>>();//奖池信息
								if(showpgpcAttr && StringUtil.isNotEmpty(data.get("prizeGrade")))
								{
									//获取胜负彩奖级/奖池信息
									if(LotteryConstants.SFC.equals(entry.getKey()))
									{
										setSfcPrizeGrade(data.getAsString("prizeGrade"),pglist,pclist,0);
										//获取任九奖级/奖池信息
										if(LotteryConstants.SFC.equals(entry.getKey()))
										{
											Dto queryDto = new BaseDto("lotteryId", LotteryConstants.RXJ);
											queryDto.put("period",periodDto.getAsString("pid"));
											List<Dto> rxjPeriodList = periodService.queryPeriods(queryDto);
											if(rxjPeriodList != null && rxjPeriodList.size() > 0)
											{
												setSfcPrizeGrade(rxjPeriodList.get(0).getAsString("prizeGrade"),pglist,pclist,1);
											}
										}
									}
									else
									{
										setPrizeGrade(data.getAsString("prizeGrade"),pglist,pclist);
									}
								}
								periodDto.put("pglist",pglist);//设置奖级信息
								periodDto.put("pclist",pclist);//设置奖池信息

								//如果需要显示对阵信息且对阵信息不为空,则设置对阵信息
								if(showMatchAttr && StringUtils.isNotEmpty(data.getAsString("matches")))
								{
									String matches = "";
									Map<String,Object> matchMaps = JsonUtil.jsonToMap(data.getAsString("matches"));
									if(matchMaps != null && matchMaps.size() > 0)
									{
										Map<String,String> matchMap = null;
										for(int i = 1; i <= 14; i ++)
										{
											matchMap = (Map<String,String>)matchMaps.get("" + i);
											matches += "," + (matchMap.get("homeTeamView").length() > 3? matchMap.get("homeTeamView").substring(0,3) : matchMap.get("homeTeamView"));
										}
									}
									periodDto.put("matches",matches.substring(1));
								}
								//如果需要显示大小比/奇偶比例,则设置大小/奇偶比例
								if(showDxjoAttr)
								{
									periodDto.put("dxScale",getDxScale(entry.getKey(),periodDto.getAsString("kcode").split("\\|")[0]));//大小比例
									periodDto.put("joScale",getOddEvenScale(periodDto.getAsString("kcode").split("\\|")[0]));//奇偶比例
								}
								//如果需要显示开奖形态/和值/单双字段,则设置
								if(showXtHzDxDsAttr)
								{
									periodDto.put("kjxt",CreateUpdateHistoryPeriodTask.getXt(entry.getKey(),periodDto.getAsString("kcode")));//设置开奖号形态
									periodDto.put("kjhz",CreateUpdateHistoryPeriodTask.getHz(entry.getKey(),periodDto.getAsString("kcode")));//设置开奖号和值
									periodDto.put("kjdx",CreateUpdateHistoryPeriodTask.getDx(entry.getKey(),periodDto.getAsString("kcode")));//设置开奖号大小
									periodDto.put("kjds",CreateUpdateHistoryPeriodTask.getDs(entry.getKey(),periodDto.getAsString("kcode")));//设置开奖号单双
								}
								periodList.add(periodDto);
							}
							//生成期次文件
							FileUtils.write(new File(filepath + entry.getKey() + "_h.json"), JsonUtil.JsonArray(periodList), "UTF-8");
						}
						//更新期次文件任务
						int spendTime = (int) (System.currentTimeMillis() - start) / 1000;
						taskService.updateTask(taskName, spendTime);
						logger.info("[历史期次文件-" + (entry.getKey()) + "]更新完毕");
					}
				}
				catch (Exception e)
				{
					logger.error("[历史期次文件-" + (entry.getKey()) + "]更新发生异常,异常信息：", e);
				}
			}
		}
		catch (Exception e)
		{
			logger.error("[历史期次文件]更新发生异常,异常信息：", e);
		}
	}

	/**
	 * 根据彩种id和开奖号计算大小比
	 * @author	sjq
	 * @param 	lotteryId	彩种id
	 * @param 	drawNumber	开奖号码
	 */
	public static String getDxScale(String lotteryId,String drawNumber)
	{
		String scale = "";
		if(StringUtils.isNotEmpty(drawNumber))
		{
			String[] codes = drawNumber.split(",");
			int bigThreshold = getBigThreshold(lotteryId);//获取大号的阈值
			int big = 0;
			int small = 0;
			for(String code : codes)
			{
				if(Integer.parseInt(code) >= bigThreshold)
				{
					big ++;
				}
				else
				{
					small ++;
				}
			}
			scale = big + ":" + small;
		}
		return scale;
	}

	/**
	 * 根据开奖号计算奇偶比
	 * @author	sjq
	 * @param 	drawNumber	开奖号码
	 */
	public static String getOddEvenScale(String drawNumber)
	{
		String scale = "";
		if(StringUtils.isNotEmpty(drawNumber))
		{
			String[] codes = drawNumber.split(",");
			int oddCount = 0;//奇数个数
			int evenCount = 0;//偶数个数
			for(String code : codes)
			{
				if((Integer.parseInt(code) & 1) == 1)
				{
					oddCount ++;
				}
				else
				{
					evenCount ++;
				}
			}
			scale = oddCount + ":" + evenCount;
		}
		return scale;
	}

	/**
	 * 根据彩种id和开奖号计算开奖形态
	 * @author	sjq
	 * @param 	lotteryId	彩种id
	 * @param 	drawNumber	开奖号码
	 */
	public static String getXt(String lotteryId,String drawNumber)
	{
		String xt = "";
		if(StringUtils.isNotEmpty(drawNumber))
		{
			/**
			 * 根据开奖号判断开奖形态
			 */
			//判断是否三同号
			String[] codes = drawNumber.split(",");//提取开奖号
			int first = Integer.parseInt(codes[0]);
			int second = Integer.parseInt(codes[1]);
			int third = Integer.parseInt(codes[2]);
			boolean is3th = false;//是否为3同号
			boolean is3bth = false;//是否为3不同号
			if(first == second && second == third)
			{
				xt += ",三同号";
				is3th = true;
			}
			//判断是否三不同号
			if(first != second && second != third && first != third)
			{
				xt += ",三不同号";
				is3bth = true;
			}
			//判断是否三连号
			if(first == (second - 1) && second == (third - 1))
			{
				xt += ",三连号";
			}
			//判断是否两同号
			if(!is3th && (first == second || first == third || second == third))
			{
				xt += ",两同号";
			}
			//判断是否两不同号
			if(!is3bth && (first != second || first == third || second == third))
			{
				xt += ",两不同号";
			}
			xt = xt.substring(1);
		}
		return xt;
	}

	/**
	 * 根据彩种id和开奖号计算和值
	 * @author	sjq
	 * @param 	lotteryId	彩种id
	 * @param 	drawNumber	开奖号码
	 */
	public static String getHz(String lotteryId,String drawNumber)
	{
		int hz = 0;
		if(StringUtils.isNotEmpty(drawNumber))
		{
			String[] codes = drawNumber.split(",");
			for(String code : codes)
			{
				hz += Integer.parseInt(code);
			}
		}
		return hz == 0? "" : (hz + "");
	}

	/**
	 * 根据彩种id和开奖号计算单双
	 * @author	sjq
	 * @param 	lotteryId	彩种id
	 * @param 	drawNumber	开奖号码
	 */
	public static String getDs(String lotteryId,String drawNumber)
	{
		int hz = 0;
		if(StringUtils.isNotEmpty(drawNumber))
		{
			String[] codes = drawNumber.split(",");
			for(String code : codes)
			{
				hz += Integer.parseInt(code);
			}
		}
		return hz == 0? "" : ((hz & 1) == 1? "单" : "双");
	}

	/**
	 * 根据彩种id和开奖号计算大小
	 * @author	sjq
	 * @param 	lotteryId	彩种id
	 * @param 	drawNumber	开奖号码
	 */
	public static String getDx(String lotteryId,String drawNumber)
	{
		int hz = 0;
		if(StringUtils.isNotEmpty(drawNumber))
		{
			String[] codes = drawNumber.split(",");
			for(String code : codes)
			{
				hz += Integer.parseInt(code);
			}
		}
		return hz == 0? "" : (hz > 9? "大" : "小");
	}

	/**
	 * 根据彩种获取大小号的大号阈值
	 * @author	sjq
	 * @param 	lotteryId	彩种id
	 */
	public static int getBigThreshold(String lotteryId)
	{
		//双色球
		if(LotteryConstants.SSQ.equals(lotteryId))
		{
			return 17;
		}
		//福彩3D
		else if(LotteryConstants.FC3D.equals(lotteryId))
		{
			return 5;
		}
		//重庆时时彩
		else if(LotteryConstants.SSC_CQ.equals(lotteryId))
		{
			return 5;
		}
		//吉林快三
		else if(LotteryConstants.K3_JL.equals(lotteryId))
		{
			return 4;
		}
		//安徽快三
		else if(LotteryConstants.K3_AH.equals(lotteryId))
		{
			return 4;
		}
		//七乐彩
		else if(LotteryConstants.QLC.equals(lotteryId))
		{
			return 16;
		}
		//江苏快三
		else if(LotteryConstants.K3_JS.equals(lotteryId))
		{
			return 4;
		}
		//江西时时彩
		else if(LotteryConstants.SSC_JX.equals(lotteryId))
		{
			return 5;
		}
		//大乐透
		else if(LotteryConstants.DLT.equals(lotteryId))
		{
			return 18;
		}
		//七星彩
		else if(LotteryConstants.QXC.equals(lotteryId))
		{
			return 5;
		}
		//排列5
		else if(LotteryConstants.PL5.equals(lotteryId))
		{
			return 5;
		}
		//排列3
		else if(LotteryConstants.PL3.equals(lotteryId))
		{
			return 5;
		}
		//广东11选5
		else if(LotteryConstants.X511_GD.equals(lotteryId))
		{
			return 6;
		}
		//山东11选5
		else if(LotteryConstants.X511_SD.equals(lotteryId))
		{
			return 6;
		}
		//上海11选5
		else if(LotteryConstants.X511_SH.equals(lotteryId))
		{
			return 6;
		}
		return  -1;
	}

	/**
	 * 设置奖级/奖池信息
	 * @author	sjq
	 * @param 	prizeGrade	奖级字符串
	 * @param 	pglist		用来存放奖级信息
	 * @param 	pclist		用来存放奖池信息
	 */
	public static void setPrizeGrade(String prizeGrade,List<Map<String,Object>> pglist,List<Map<String,Object>> pclist)
	{
		JSONObject jsonObject = JSONObject.fromObject(prizeGrade);
		Iterator<String> iterator = jsonObject.keys();
		Map<String,Object> pcmap = new HashMap<String,Object>();//奖池对象
		while(iterator.hasNext())
		{
			String key = iterator.next();
			if(jsonObject.get(key) instanceof JSONObject)
			{
				JSONObject dataObj = jsonObject.getJSONObject(key);
				Map<String,String> pgmap = new HashMap<String,String>();
				pgmap.put("dz",dataObj.getString("单注奖金"));
				pgmap.put("jj",dataObj.getString("加奖奖金"));
				if(dataObj.containsKey("中奖注数"))
				{
					pgmap.put("zs",dataObj.getString("中奖注数"));
				}
				Map<String,Object> dataMap = new HashMap<String,Object>();
				dataMap.put("pname",key);
				dataMap.put("items",pgmap);
				pglist.add(dataMap);

				//如果有追加投注的开奖信息(主要针对大乐透)
				if(dataObj.containsKey("追加奖金") || dataObj.containsKey("追加注数"))
				{
					Map<String,String> zjpgmap = new HashMap<String,String>();
					if(dataObj.containsKey("追加奖金"))
					{
						zjpgmap.put("dz",dataObj.getString("追加奖金"));
					}
					if(dataObj.containsKey("追加注数"))
					{
						zjpgmap.put("zs",dataObj.getString("追加注数"));
					}
					Map<String,Object> zjdataMap = new HashMap<String,Object>();
					zjdataMap.put("pname",key + "追加");
					zjdataMap.put("items",zjpgmap);
					pglist.add(zjdataMap);
				}
			}
			else
			{
				pcmap.put(pcMaps.get(key),jsonObject.getString(key));
			}
		}
		pclist.add(pcmap);
	}

	/**
	 * 设置胜负彩奖级/奖池信息
	 * @author	sjq
	 * @param 	prizeGrade	奖级字符串
	 * @param 	pglist		用来存放奖级信息
	 * @param 	pclist		用来存放奖池信息
	 * @param 	type		用来区分胜负彩和任九(0-胜负彩 1-任九)
	 */
	public static void setSfcPrizeGrade(String prizeGrade,List<Map<String,Object>> pglist,List<Map<String,Object>> pclist,int type)
	{
		JSONObject jsonObject = JSONObject.fromObject(prizeGrade);
		Iterator<String> iterator = jsonObject.keys();
		Map<String,Object> pcmap = new HashMap<String,Object>();//奖池对象
		while(iterator.hasNext())
		{
			String key = iterator.next();
			if(jsonObject.get(key) instanceof JSONObject)
			{
				JSONObject dataObj = jsonObject.getJSONObject(key);
				Map<String,String> pgmap = new HashMap<String,String>();
				pgmap.put("dz",dataObj.getString("单注奖金"));
				pgmap.put("jj",dataObj.getString("加奖奖金"));
				if(dataObj.containsKey("中奖注数"))
				{
					pgmap.put("zs",dataObj.getString("中奖注数"));
				}
				Map<String,Object> dataMap = new HashMap<String,Object>();
				dataMap.put("pname",type == 1? "任九" : key);
				dataMap.put("items",pgmap);
				pglist.add(dataMap);
			}
			else
			{
				pcmap.put(pcMaps.get(key),jsonObject.getString(key));
			}
		}
		Map<String,Object> dataMap = new HashMap<String,Object>();
		dataMap.put("pname",type == 1? "任九" : "胜负彩");
		dataMap.put("items",pcmap);
		pclist.add(dataMap);
	}
}