package com.caipiao.taskcenter.task;

import com.caipiao.common.constants.Constants;
import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.lottery.LotteryGrade;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.DoubleUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.lottery.Lottery;
import com.caipiao.domain.vo.PeriodVo;
import com.caipiao.service.common.TaskService;
import com.caipiao.service.lottery.PeriodService;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.xpath.SourceTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

/**
 * 在售期次文件更新任务
 * @author 	mcdog
 */
@Component("createUpdatePeriodTask")
public class CreateUpdatePeriodTask
{
	private static Logger logger = LoggerFactory.getLogger(CreateUpdateHistoryPeriodTask.class);

	@Value("${api.period.filepath}")
	private String filepath;

	@Value("${api.period.tz.history.readSize}")
	private int historyReadSize;//每次读取历史期次条数(app投注界面)

	@Autowired
	private TaskService taskService;

	@Autowired
	private PeriodService periodService;

	private static final Map<String,String> lotteryMaps = new HashMap<String,String>();//彩种集合(以彩种id为key,彩种id为值)

	private static final Map<String,String> kplotteryMaps = new HashMap<String,String>();//快频彩种集合(以彩种id为key,彩种id为值)

	static
	{
		lotteryMaps.put(LotteryConstants.SSQ, LotteryConstants.SSQ);
		lotteryMaps.put(LotteryConstants.FC3D, LotteryConstants.FC3D);
		lotteryMaps.put(LotteryConstants.QLC, LotteryConstants.QLC);
		lotteryMaps.put(LotteryConstants.DLT, LotteryConstants.DLT);
		lotteryMaps.put(LotteryConstants.QXC, LotteryConstants.QXC);
		lotteryMaps.put(LotteryConstants.PL5, LotteryConstants.PL5);
		lotteryMaps.put(LotteryConstants.PL3, LotteryConstants.PL3);
		lotteryMaps.put(LotteryConstants.K3_JL, LotteryConstants.K3_JL);
		lotteryMaps.put(LotteryConstants.K3_AH, LotteryConstants.K3_AH);
		lotteryMaps.put(LotteryConstants.K3_JS, LotteryConstants.K3_JS);
		lotteryMaps.put(LotteryConstants.SSC_CQ, LotteryConstants.SSC_CQ);
		lotteryMaps.put(LotteryConstants.SSC_JX, LotteryConstants.SSC_JX);
		lotteryMaps.put(LotteryConstants.X511_GD, LotteryConstants.X511_GD);
		lotteryMaps.put(LotteryConstants.X511_SD, LotteryConstants.X511_SD);
		lotteryMaps.put(LotteryConstants.X511_SH, LotteryConstants.X511_SH);
		lotteryMaps.put(LotteryConstants.SFC, LotteryConstants.SFC);
		lotteryMaps.put(LotteryConstants.RXJ, LotteryConstants.RXJ);
		lotteryMaps.put(LotteryConstants.JQC, LotteryConstants.JQC);
		lotteryMaps.put(LotteryConstants.BQC, LotteryConstants.BQC);

		kplotteryMaps.put(LotteryConstants.K3_JL, LotteryConstants.K3_JL);
		kplotteryMaps.put(LotteryConstants.K3_AH, LotteryConstants.K3_AH);
		kplotteryMaps.put(LotteryConstants.K3_JS, LotteryConstants.K3_JS);
	}

	/**
	 * 创建/更新在售期次文件
	 * @author	sjq
	 */
	public void createUpdatePeriodFile()
	{
		try
		{
			//定义在售期次查询参数
			long start = System.currentTimeMillis();
			Dto zsParams = new BaseDto("sellStatus",1);//销售状态为销售中
			zsParams.put("minSellEndTime", DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//销售截止时间必须晚于当前时间
			zsParams.put("orderBySorts","period asc");//按期次升序排列
			zsParams.put("pstart",0);
			zsParams.put("psize",10);//默认取最近10期

			//定义历史期次查询参数
			Dto lsParams = new BaseDto("sellStatus",2);//已截止的期次
			lsParams.put("minState",1);//计奖状态值>=1
			lsParams.put("maxSellEndTime", DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//销售截止时间必须早于当前时间
			lsParams.put("orderBySorts","period desc");//按期次降序排列
			lsParams.put("pstart",0);
			lsParams.put("psize",5);//只读取最近5期

			//循环彩种集合,根据条件依次更新期次文件
			for(Map.Entry<String,String> entry : lotteryMaps.entrySet())
			{
				try
				{
					//查询待执行的任务
					logger.debug("[期次文件-" + (entry.getKey()) + "]更新开始");
					String taskName = Constants.periodUpdateTaskMaps.get(entry.getKey());
					boolean hasTask = taskService.isHasTask(taskName);
					if(hasTask)
					{
                        boolean isszc = LotteryUtils.isSzc(entry.getKey())? true : false;//是否为数字彩
                        boolean isk3 = LotteryUtils.isK3(entry.getKey())? true : false;//是否为快三
                        boolean ismp = LotteryUtils.isMp(entry.getKey())? true : false;//是否为慢频
                        boolean iszc = LotteryUtils.isZC(entry.getKey())? true : false;//是否为足彩
                        boolean showGcAttr = (ismp || iszc)? true : false;//慢频/足彩,则显示滚存
						boolean showDxJoAttr = (isszc && !isk3)? true : false;//数字彩(不包含快三),则显示大小比例和奇偶比例
                        boolean showXtHzDxDsAttr = isk3? true : false;//快三,则显示开奖形态/和值/大小/单双
						boolean showMatchAttr = iszc? true : false;//足彩,则显示对阵信息
						boolean showLsPeriod = iszc? false : true;//足彩,则不显示历史期次

						JSONObject jsonData = new JSONObject();//文件数据对象
						List<Dto> zsPeriodList = new ArrayList<Dto>();//在售期次
						List<Dto> lsPeriodList = new ArrayList<Dto>();//历史期次
						zsParams.put("lotteryId",entry.getKey());
						lsParams.put("lotteryId",entry.getKey());

						//查询在售期次信息
						if(iszc)
						{
							zsParams.put("psize",3);//足彩,只取最近3期在售
						}
						else if(isk3)
						{
							zsParams.put("psize",20);//快三,取最近20期在售
							lsParams.put("psize",10);//快三,取最近10期历史
						}
						List<Dto> dataList = periodService.queryPeriods(zsParams);
						Dto periodDto = null;
						if(dataList != null && dataList.size() > 0)
						{
							//转换为前端展示的对象
							for(Dto data : dataList)
							{
								periodDto = new BaseDto();
								periodDto.put("lid",entry.getKey());//设置彩种id
								periodDto.put("lname",data.getAsString("lotteryName"));//设置彩种名称
								periodDto.put("pid",data.getAsString("period"));//设置期次号
								periodDto.put("pname",periodDto.getAsString("pid") + "期");//设置期次名称

								//设置期次号简称
								periodDto.put("spid",periodDto.getAsString("pid").substring(4));
								if(isk3)
								{
									periodDto.put("spid",periodDto.getAsString("pid").substring(8));//设置快三期次号简称
									periodDto.put("pname",periodDto.getAsString("spid") + "期");//设置快三期次名称
								}

								periodDto.put("stime",data.getAsString("sellStartTime"));//设置开始销售时间
								periodDto.put("etime",data.getAsString("sellEndTime"));//设置销售截止时间

								//设置销售状态,如果是足彩,则还需要判断当前时间是否已经到了开售时间
								periodDto.put("status",1);//设置销售状态
								periodDto.put("sdesc","在售");
								if(LotteryUtils.isZC(entry.getKey()))
								{
									if(DateUtil.parseDate(data.getAsString("sellStartTime"),DateUtil.DEFAULT_DATE_TIME).after(new Date()))
									{
										periodDto.put("status",0);//设置销售状态
										periodDto.put("sdesc","当前对阵尚未确定！请稍后购买！");
									}
								}
								//设置截止时间简称
								if(StringUtil.isNotEmpty(periodDto.get("etime")) && periodDto.getAsString("etime").length() >= 16)
								{
									periodDto.put("setime",periodDto.getAsString("etime").substring(5,16));//去掉年,去掉秒
								}
								//如果需要显示滚存字段,则设置滚存
								if(showGcAttr)
								{
									setGc(data,periodDto);
								}
								//如果需要显示对阵信息,则设置对阵信息
								if(showMatchAttr)
								{
									List<Map<String,Object>> matchList = new ArrayList<Map<String,Object>>();
									if(StringUtils.isNotEmpty(data.getAsString("matches")))
									{
										Map<String,Object> matchMaps = JsonUtil.jsonToMap(data.getAsString("matches"));
										if(matchMaps != null && matchMaps.size() > 0)
										{

											Map<String,Object> matchMap = null;
											for(int i = 1; i <= 14; i ++)
											{
												matchMap = (Map<String,Object>)matchMaps.get("" + i);
												Map<String,Object> dataMap = new HashMap<String,Object>();
												dataMap.put("xh",matchMap.get("index"));//赛事序号
												dataMap.put("mname",matchMap.get("matchname"));//赛事名称

												//设置主客队名称(2个字的名称,则添加空格)
												String hname = StringUtil.isEmpty(matchMap.get("homeTeamView"))? "" : matchMap.get("homeTeamView").toString();
												String gname = StringUtil.isEmpty(matchMap.get("awayTeamView"))? "" : matchMap.get("awayTeamView").toString();
												dataMap.put("hname",hname.length() == 2? (hname.substring(0,1) + "　" + hname.substring(1,2)) : hname);//设置主队名
												dataMap.put("gname",gname.length() == 2? (gname.substring(0,1) + "　" + gname.substring(1,2)) : gname);//设置客队名

												dataMap.put("sp3",matchMap.get("sheng") == null? "" : matchMap.get("sheng"));//胜赔
												dataMap.put("sp1",matchMap.get("ping") == null? "" : matchMap.get("ping"));//平赔
												dataMap.put("sp0",matchMap.get("fu") == null? "" : matchMap.get("fu"));//负赔
												dataMap.put("mtime",matchMap.get("matchTime"));
												if(StringUtil.isNotEmpty(dataMap.get("mtime")) && dataMap.get("mtime").toString().length() >= 16)
												{
													dataMap.put("mdate",dataMap.get("mtime").toString().substring(5,10));//只取月日
													dataMap.put("mhour",dataMap.get("mtime").toString().substring(11,16) + " 开赛");//只取时分
													dataMap.put("mtime",dataMap.get("mtime").toString().substring(0,16));//去掉秒
												}
												matchList.add(dataMap);
											}
										}
									}
									periodDto.put("matches",matchList);//设置对阵信息
								}
								zsPeriodList.add(periodDto);
							}
						}
						//查询历史期次信息
						dataList = periodService.queryPeriods(lsParams);
						if(dataList != null && dataList.size() > 0)
						{
							for(Dto data : dataList)
							{
								//只保留已经开奖且有开奖号的期次
								if(data.getAsInteger("state") >= 3 && StringUtil.isNotEmpty(data.get("drawNumber")))
								{
									//转换为前端展示的对象
									periodDto = new BaseDto();
									periodDto.put("lid",entry.getKey());//设置彩种id
									periodDto.put("lname",data.getAsString("lotteryName"));//设置彩种名称
									periodDto.put("pid",data.getAsString("period"));//设置期次号
									periodDto.put("pname",periodDto.getAsString("pid") + "期");//设置期次名称

									//设置期次号简称
									periodDto.put("spid",periodDto.getAsString("pid").substring(4));//设置期次号简称
									if(isk3)
									{
										periodDto.put("spid",periodDto.getAsString("pid").substring(8));//设置快三期次号简称
										periodDto.put("pname",periodDto.getAsString("spid") + "期");//设置快三期次名称
									}

									periodDto.put("kstatus",data.getAsInteger("state") < 3? 0 : 1);//设置开奖状态
									periodDto.put("kcode",data.getAsString("drawNumber"));//设置开奖号码
									periodDto.put("ktime",data.getAsString("drawNumberTime"));//设置开奖时间

									//设置开奖时间简称
									if(StringUtil.isNotEmpty(periodDto.get("ktime")) && periodDto.getAsString("ktime").length() >= 16)
									{
										periodDto.put("sktime",periodDto.getAsString("ktime").substring(5,16));//(去掉年,去掉秒
									}
									//如果需要显示滚存字段,则设置滚存
									if(showGcAttr)
									{
										setGc(data,periodDto);
									}
									//如果需要显示大小比/奇偶比例字段,则设置大小/奇偶比例
									if(showDxJoAttr)
									{
										periodDto.put("dxScale",CreateUpdateHistoryPeriodTask.getDxScale(entry.getKey(),periodDto.getAsString("kcode").split("\\|")[0]));//大小比例
										periodDto.put("joScale",CreateUpdateHistoryPeriodTask.getOddEvenScale(periodDto.getAsString("kcode").split("\\|")[0]));//奇偶比例
									}
									//如果需要显示开奖形态/和值/单双字段,则设置
                                    if(showXtHzDxDsAttr)
                                    {
										periodDto.put("kjxt",CreateUpdateHistoryPeriodTask.getXt(entry.getKey(),periodDto.getAsString("kcode")));//设置开奖号形态
										periodDto.put("kjhz",CreateUpdateHistoryPeriodTask.getHz(entry.getKey(),periodDto.getAsString("kcode")));//设置开奖号和值
										periodDto.put("kjdx",CreateUpdateHistoryPeriodTask.getDx(entry.getKey(),periodDto.getAsString("kcode")));//设置开奖号大小
										periodDto.put("kjds",CreateUpdateHistoryPeriodTask.getDs(entry.getKey(),periodDto.getAsString("kcode")));//设置开奖号单双
                                    }
									//如果需要显示对阵信息字段,则设置对阵信息
									if(showMatchAttr)
									{
										List<Map<String,Object>> matchList = new ArrayList<Map<String,Object>>();
										if(StringUtils.isNotEmpty(data.getAsString("matches")))
										{
											Map<String,Object> matchMaps = JsonUtil.jsonToMap(data.getAsString("matches"));
											if(matchMaps != null && matchMaps.size() > 0)
											{
												Map<String,Object> matchMap = null;
												for(int i = 1; i <= 14; i ++)
												{
													matchMap = (Map<String,Object>)matchMaps.get("" + i);
													Map<String,Object> dataMap = new HashMap<String,Object>();
													dataMap.put("xh",matchMap.get("index"));
													dataMap.put("mname",matchMap.get("matchname"));

													//设置主客队名称(2个字的名称,则添加空格)
													String hname = StringUtil.isEmpty(matchMap.get("homeTeamView"))? "" : matchMap.get("homeTeamView").toString();
													String gname = StringUtil.isEmpty(matchMap.get("awayTeamView"))? "" : matchMap.get("awayTeamView").toString();
													dataMap.put("hname",hname.length() == 2? (hname.substring(0,1) + "　" + hname.substring(1,2)) : hname);//设置主队名
													dataMap.put("gname",gname.length() == 2? (gname.substring(0,1) + "　" + gname.substring(1,2)) : gname);//设置客队名

													dataMap.put("sp3",matchMap.get("sheng") == null? "" : matchMap.get("sheng"));//胜赔
													dataMap.put("sp1",matchMap.get("ping") == null? "" : matchMap.get("ping"));//平赔
													dataMap.put("sp0",matchMap.get("fu") == null? "" : matchMap.get("fu"));//负赔
													dataMap.put("score",matchMap.get("score"));
													dataMap.put("mtime",matchMap.get("matchTime"));
													if(StringUtil.isNotEmpty(dataMap.get("mtime")) && dataMap.get("mtime").toString().length() >= 16)
													{
														dataMap.put("mdate",dataMap.get("mtime").toString().substring(5,10));//只取月日
														dataMap.put("mhour",dataMap.get("mtime").toString().substring(11,16) + " 开赛");//只取时分
														dataMap.put("mtime",dataMap.get("mtime").toString().substring(0,16));//去掉秒
													}
													matchList.add(dataMap);
												}
											}
										}
										periodDto.put("matches",matchList);
									}
									lsPeriodList.add(periodDto);
								}
							}
						}
						//取最近一期的历史期次作为最近一期在售期次的滚存
						if(zsPeriodList.size() > 0 && lsPeriodList.size() > 0 && showGcAttr)
						{
							zsPeriodList.get(0).put("gc",lsPeriodList.get(0).getAsString("gc"));
						}
						//生成期次文件
						jsonData.put("zs",JsonUtil.JsonArray(zsPeriodList));//设置在售期次数据
						if(showLsPeriod)
						{
							jsonData.put("ls",JsonUtil.JsonArray(lsPeriodList));//设置历史期次数据
							jsonData.put("hnum",historyReadSize);//设置每次读取历史期次条数(app投注界面)
						}
						if(zsPeriodList.size() > 0 || lsPeriodList.size() > 0)
						{
							FileUtils.write(new File(filepath + (entry.getKey()) + ".json"),jsonData.toString(), "UTF-8");
						}
						//更新期次文件任务
						int spendTime = (int) (System.currentTimeMillis() - start) / 1000;
						taskService.updateTask(taskName, spendTime);
						logger.info("[期次文件-" + (entry.getKey()) + "]更新完毕");
					}
				}
				catch (Exception e)
				{
					logger.error("[期次文件-" + (entry.getKey()) + "]更新发生异常,异常信息：", e);
				}
			}
		}
		catch (Exception e)
		{
			logger.error("[期次文件]更新发生异常,异常信息：", e);
		}
	}

	/**
	 * 创建/更新在售期次文件(快三)
	 * @author	sjq
	 */
	public void createUpdateK3PeriodFile()
	{
		try
		{
			//定义在售期次查询参数
			long start = System.currentTimeMillis();
			Dto zsParams = new BaseDto("sellStatus",1);//销售状态为销售中
			zsParams.put("minSellEndTime", DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//销售截止时间必须晚于当前时间
			zsParams.put("orderBySorts","period asc");//按期次升序排列
			zsParams.put("pstart",0);
			zsParams.put("psize",20);//默认读取最近20期

			//定义历史期次查询参数
			Dto lsParams = new BaseDto("sellStatus",2);//已截止的期次
			lsParams.put("minState",1);//计奖状态值>=1
			lsParams.put("maxSellEndTime", DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//销售截止时间必须早于当前时间
			lsParams.put("orderBySorts","period desc");//按期次降序排列
			lsParams.put("pstart",0);
			lsParams.put("psize",10);//默认读取最近10期

			//循环彩种集合,根据条件依次更新期次文件
			for(Map.Entry<String,String> entry : kplotteryMaps.entrySet())
			{
				try
				{
					logger.debug("[创建/更新在售期次文件(快三)]期次文件-" + (entry.getKey()) + "更新开始");
					JSONObject jsonData = new JSONObject();//文件数据对象
					List<Dto> zsPeriodList = new ArrayList<Dto>();//在售期次
					List<Dto> lsPeriodList = new ArrayList<Dto>();//历史期次
					zsParams.put("lotteryId",entry.getKey());
					lsParams.put("lotteryId",entry.getKey());
					List<Dto> dataList = periodService.queryPeriods(zsParams);//查询期次数据
					Dto periodDto = null;
					if(dataList != null && dataList.size() > 0)
					{
						//转换为前端展示的对象
						for(Dto data : dataList)
						{
							periodDto = new BaseDto();
							periodDto.put("lid",entry.getKey());//设置彩种id
							periodDto.put("lname",data.getAsString("lotteryName"));//设置彩种名称
							periodDto.put("pid",data.getAsString("period"));//设置期次号
							periodDto.put("spid",periodDto.getAsString("pid").substring(8));//设置快三期次号简称
							periodDto.put("pname",periodDto.getAsString("spid") + "期");//设置快三期次名称
							periodDto.put("stime",data.getAsString("sellStartTime"));//设置开始销售时间
							periodDto.put("etime",data.getAsString("sellEndTime"));//设置销售截止时间

							//设置销售状态,如果是足彩,则还需要判断当前时间是否已经到了开售时间
							periodDto.put("status",1);//设置销售状态
							periodDto.put("sdesc","在售");
							if(LotteryUtils.isZC(entry.getKey()))

							//设置截止时间简称
							if(StringUtil.isNotEmpty(periodDto.get("etime")) && periodDto.getAsString("etime").length() >= 16)
							{
								periodDto.put("setime",periodDto.getAsString("etime").substring(5,16));//去掉年,去掉秒
							}
							zsPeriodList.add(periodDto);
						}
					}
					//查询历史期次信息
					dataList = periodService.queryPeriods(lsParams);
					if(dataList != null && dataList.size() > 0)
					{
						for(Dto data : dataList)
						{
							//只保留已经开奖且有开奖号的期次
							if(data.getAsInteger("state") >= 3 && StringUtil.isNotEmpty(data.get("drawNumber")))
							{
								//转换为前端展示的对象
								periodDto = new BaseDto();
								periodDto.put("lid",entry.getKey());//设置彩种id
								periodDto.put("lname",data.getAsString("lotteryName"));//设置彩种名称
								periodDto.put("pid",data.getAsString("period"));//设置期次号
								periodDto.put("spid",periodDto.getAsString("pid").substring(8));//设置快三期次号简称
								periodDto.put("pname",periodDto.getAsString("spid") + "期");//设置快三期次名称
								periodDto.put("kstatus",data.getAsInteger("state") < 3? 0 : 1);//设置开奖状态
								periodDto.put("kcode",data.getAsString("drawNumber"));//设置开奖号码
								periodDto.put("ktime",data.getAsString("drawNumberTime"));//设置开奖时间

								//设置开奖时间简称
								if(StringUtil.isNotEmpty(periodDto.get("ktime")) && periodDto.getAsString("ktime").length() >= 16)
								{
									periodDto.put("sktime",periodDto.getAsString("ktime").substring(5,16));//(去掉年,去掉秒
								}
								//设置开奖形态/和值/大小/单双
								periodDto.put("kjxt",CreateUpdateHistoryPeriodTask.getXt(entry.getKey(),periodDto.getAsString("kcode")));//设置开奖号形态
								periodDto.put("kjhz",CreateUpdateHistoryPeriodTask.getHz(entry.getKey(),periodDto.getAsString("kcode")));//设置开奖号和值
								periodDto.put("kjdx",CreateUpdateHistoryPeriodTask.getDx(entry.getKey(),periodDto.getAsString("kcode")));//设置开奖号大小
								periodDto.put("kjds",CreateUpdateHistoryPeriodTask.getDs(entry.getKey(),periodDto.getAsString("kcode")));//设置开奖号单双

								lsPeriodList.add(periodDto);
							}
						}
					}
					//生成期次文件
					jsonData.put("zs",JsonUtil.JsonArray(zsPeriodList));//设置在售期次数据
					jsonData.put("ls",JsonUtil.JsonArray(lsPeriodList));//设置历史期次数据
					jsonData.put("hnum",historyReadSize);//设置每次读取历史期次条数(app投注界面)
					if(zsPeriodList.size() > 0 || lsPeriodList.size() > 0)
					{
						FileUtils.write(new File(filepath + (entry.getKey()) + ".json"),jsonData.toString(), "UTF-8");
					}
					//更新期次文件任务
					logger.info("[创建/更新在售期次文件(快三)]期次文件-" + (entry.getKey()) + "更新完毕.");
				}
				catch (Exception e)
				{
					logger.error("[创建/更新在售期次文件(快三)]期次文件-" + (entry.getKey()) + "更新发生异常!异常信息：", e);
				}
			}
		}
		catch (Exception e)
		{
			logger.error("[创建/更新在售期次文件(快三)]更新发生异常!异常信息：", e);
		}
	}

	/**
	 * 根据奖级设置滚存
	 * @author	sjq
	 * @param 	data		源数据
	 * @param 	periodDto	期次对象(解析后的滚存设置到该对象中)
	 */
	public static void setGc(Dto data,Dto periodDto)
	{
		try
		{
			JSONObject jsonObject = JSONObject.fromObject(data.get("prizeGrade"));
			Iterator<String> iterator = jsonObject.keys();
			while(iterator.hasNext())
			{
				String key = iterator.next();
				if(key.equals(LotteryGrade.jclj))
				{
					String gc = jsonObject.getString(key);
					String newgc = "";
					if(StringUtil.isNotEmpty(gc))
					{
						String moneyStr = gc.indexOf("元") > -1? gc.substring(0,gc.indexOf("元")) : gc;
						moneyStr = moneyStr.replace(",","");//去掉千分位

						//根据金额的大小转换为中文描述
						double maxUnitValue = Math.pow(10,8);//最大单位,从亿开始
						double monery = Double.parseDouble(moneyStr);
						double remainder = 0;//求余
						if(monery >= maxUnitValue)
						{
							newgc += (int)(monery / maxUnitValue);//计算亿
							newgc += "亿";
							remainder = monery % maxUnitValue;
							if(remainder > 0)
							{
								maxUnitValue = Math.pow(10,4);
								newgc += (int)(remainder / maxUnitValue);//计算万
								newgc += "万";
							}
						}
						else if(monery >= Math.pow(10,4))
						{
							maxUnitValue = Math.pow(10,4);
							newgc += (int)(monery / maxUnitValue);//计算万
							newgc += "万";
							remainder = monery % maxUnitValue;
							if(remainder > 0)
							{
								maxUnitValue = Math.pow(10,3);
								newgc += (int)(remainder / maxUnitValue);//计算千
								newgc += "千";
							}
						}
						else
						{
							newgc = gc;
						}
					}
					periodDto.put("gc",StringUtil.isEmpty(gc)? "" : ("0.00".equals(newgc)? "0" : newgc));
					break;
				}
			}
		}
		catch (Exception e)
		{
			periodDto.put("gc","--");
		}
	}
}