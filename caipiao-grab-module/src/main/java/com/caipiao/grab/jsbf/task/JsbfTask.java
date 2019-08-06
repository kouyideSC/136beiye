package com.caipiao.grab.jsbf.task;

import java.io.File;
import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.caipiao.common.http.Grab;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.util.CalculationUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.DoubleUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.jsbf.Schedule;
import com.caipiao.grab.jsbf.handler.GrabForChangeFile;
import com.caipiao.grab.jsbf.handler.GrabForJsbfMatch;
import com.caipiao.grab.util.JsbfUtil;
import com.caipiao.grab.vo.JsbfPeriodVo;
import com.caipiao.grab.vo.JsbfScheduleVO;
import com.caipiao.memcache.MemCached;
import com.caipiao.service.jsbf.ScheduleService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("jsbfTask")
public class JsbfTask extends JsbfUtil {
	private static Logger logger = LoggerFactory.getLogger(JsbfTask.class);
	public static BlockingQueue<Schedule> queueone = new LinkedBlockingQueue<Schedule>();// 插库
	public static BlockingQueue<Schedule> queuetwo = new LinkedBlockingQueue<Schedule>();// 更新缓存
	public static BlockingQueue<Integer> queuethree = new LinkedBlockingQueue<Integer>();// 需要更新库的比赛id
	public String curPeriod = "";

	@Autowired
	private MemCached memcache;
	@Autowired
	private GrabForJsbfMatch grabForJsbfMatch;
	@Autowired
	private GrabForChangeFile grabForChangeFile;
	@Autowired
	private ScheduleService scheduleService;

	@Value("${bf.zlk.match}")
	private String position;
	@Value("${bf.zlk.change}")
	private String change_position;
	@Value("${haocai.host.bf}")
	private String host;
	@Value("${grab.hc_match_url}")
	private String matchUrl;//即时比分对阵
	@Value("${grab.hc_change_url}")
	private String changeUrl; //即时比分直播文件

	/**
	 * 抓取比分直播change文件
	 * @author kouyi
	 */
	public void grabHaoCaiJsbfChange() {
		try {
			long start = System.currentTimeMillis();
			List<JsbfScheduleVO> voList = new ArrayList<>();
			Map<String, JsbfScheduleVO> liveMap = (Map<String, JsbfScheduleVO>) memcache.get(JSBF_LIVE_KEY);//获取直播中的场次
			if(StringUtil.isEmpty(liveMap)) {
				liveMap = new HashMap<>();
			}
			List<Schedule> list = grabForChangeFile.collect(changeUrl, null, Grab.CHARTSET_GBK, host);
			if(StringUtil.isNotEmpty(list)) {
				for (Schedule sche : list) {
					JsbfScheduleVO vo = null;
					if(liveMap.containsKey(sche.getScheduleId())) {
						vo = liveMap.get(sche.getScheduleId());
						if(StringUtil.isEmpty(vo.getWeek()) && sche.getMatchState() == -1) {//刚结束的比赛 10分钟以后从map中移除
							vo.setWeek(DateUtil.dateDefaultFormat(DateUtil.addMinute(new Date(), 10)));
						}
						setLiveSchedule(vo, sche);//格式化数据
						liveMap.put(sche.getScheduleId(), vo);
					} else {
						if(sche.getMatchState() > 0) {//进行中的比赛
							vo = new JsbfScheduleVO();
							setLiveSchedule(vo, sche);//格式化数据
							liveMap.put(sche.getScheduleId(), vo);
						}
					}
					String key = JSBF_MATCH_KEY + sche.getScheduleId();
					if (!memcache.contains(key)) {
						continue;
					}
					//更新完整赛事单个缓存
					Schedule lsche = (Schedule) memcache.get(key);
					if (!sameCh(sche).equals(sameCh(lsche))) {
						schesynchro(lsche, sche);
						memcache.set(key, lsche, JSBF_MATCH_KEY_EXPIRE);//更新完整赛事缓存
						//更新库
						scheduleService.update(lsche);
					}
				}
			}
			removeExpireKey(liveMap);
			voList.addAll(liveMap.values());
			memcache.set(JSBF_LIVE_KEY, liveMap, JSBF_CURDAY_KEY_EXPIRE);//保存直播文件缓存
			FileUtils.write(new File(change_position + "jsbf_live.json"), JsonUtil.JsonArray(voList, bfShow), Grab.CHARTSET_UTF8);
			long end = System.currentTimeMillis();
			logger.info("[即时比分直播文件抓取] 成功抓取比分直播数据 " + voList.size() + " 条,用时" + (end - start) / 1000 + "秒");
		} catch (Exception e) {
			logger.error("[即时比分直播文件抓取] 异常：", e);
		}
	}

	/**
	 * 移除map中完场过期的比赛
	 * @param liveMap
	 */
	private void removeExpireKey(Map<String, JsbfScheduleVO> liveMap) {
		if(StringUtil.isEmpty(liveMap)) {
			return;
		}
		Iterator<Map.Entry<String, JsbfScheduleVO>> iterator = liveMap.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry<String, JsbfScheduleVO> entry = iterator.next();
			JsbfScheduleVO vo = entry.getValue();
			if(StringUtil.isNotEmpty(vo.getWeek()) && DateUtil.dateDefaultFormat(vo.getWeek()).getTime() < new Date().getTime()) {
				iterator.remove();
			}
			if(StringUtil.isEmpty(vo.getWeek())) {
				if(StringUtil.parseInt(vo.getSdesc()) > 85 || StringUtil.parseInt(vo.getSdesc()) == 0) {
					vo.setWeek(DateUtil.dateDefaultFormat(DateUtil.addMinute(new Date(), 15)));//85分钟以后的比赛 默认15分钟以后从map中移除
				}
				if(DateUtil.minutesBetween(DateUtil.dateDefaultFormat(vo.getStime()), new Date()) > 150) {
					iterator.remove();
				}
			}
		}
	}

	/**
	 * 抓取好彩店竞彩赛事
 	 * @author kouyi
	 */
	public void grabHaoCaiMatch() {
		try {
			long start = System.currentTimeMillis();
			List<Schedule> list = grabForJsbfMatch.collect(matchUrl, null, Grab.CHARTSET_GBK, host);
			if(StringUtil.isEmpty(list)) {
				return;
			}
			int newMatch = 0;
			String jsbfKey = jsbfkey(0);
			Map<String, JsbfScheduleVO> liveMap = (Map<String, JsbfScheduleVO>) memcache.get(JSBF_LIVE_KEY);//获取直播中的场次
			for (Schedule schedule : list) {
				String key = JSBF_MATCH_KEY + schedule.getScheduleId();
				if (!memcache.contains(key)) {//插入
					int row = scheduleService.insertSchedule(schedule);
					if (row > -1) {
						memcache.set(key, schedule, JSBF_MATCH_KEY_EXPIRE);
						if (row == 0) {//缓存过期，库中已经有该对阵
							scheduleService.update(schedule);
						} else {//新开对阵
							newMatch ++;
						}
					}
				} else {//更新
					Schedule last = (Schedule) memcache.get(key);
					if (StringUtil.isNotEmpty(last) && !sameMc(schedule).equals(sameMc(last))) {
						memcache.set(key, schedule, JSBF_MATCH_KEY_EXPIRE);//更新赛事缓存
						scheduleService.update(schedule);
						//更新直播存留map
						if(StringUtil.isNotEmpty(liveMap) && liveMap.containsKey(schedule.getScheduleId())) {
							JsbfScheduleVO vo = liveMap.get(schedule.getScheduleId());
							if(schedule.getMatchState() == -1) {//完场的比赛，10分钟以后从map中移除
								vo.setWeek(DateUtil.dateDefaultFormat(DateUtil.addMinute(new Date(), 10)));
							}
							setLiveSchedule(vo, schedule);
							liveMap.put(schedule.getScheduleId(), vo);
						}
					}
				}
			}
			if(newMatch > 0) {//有新开对阵则从库中取最新,并删除大对阵缓存
				memcache.delete(jsbfKey);
			}
			long end = System.currentTimeMillis();
			logger.info("[即时比分对阵抓取] 成功抓取处理即时比分对阵数据 " + list.size() + " 条,用时" + (end - start) / 1000 + "秒");
		} catch (Exception e) {
			logger.error("[即时比分对阵抓取] 异常", e);
		}
	}

	/**
	 * 生成即时比分对阵文件-当前期
 	 * @author kouyi
	 */
	public void createCurrentJsbfMatch() {
		Map<String, Schedule> mapSche = null;
		try {
			String key = jsbfkey(0);
			if(curPeriod.equals("")) {
				curPeriod = key;
			}
			if (!memcache.contains(key)) {
				//与完整赛事缓存同步初始化
				mapSche = matchInit(scheduleService.queryScheduleAndLastNoEndList(todaystr(0), todaystr(-1)));
				memcache.set(key, mapSche, JSBF_CURDAY_KEY_EXPIRE);
				if(!curPeriod.equals(key) && createLastJsbfMatch()) {//跳期需删除上一期正在进行的比赛
					curPeriod = key;//赋值最新key
				}
			} else {
				mapSche = (Map<String, Schedule>) memcache.get(key);
				List<Schedule> list = new ArrayList<>();
				list.addAll(mapSche.values());
				mapSche = matchInit(list);
			}
			FileUtils.write(new File(position + todaystr(0) + ".json"), JsonUtil.JsonArray(doSort(mapSche), show), Grab.CHARTSET_UTF8);
		} catch (Exception e) {
			logger.error("[生成当前期即时比分对阵文件] 异常", e);
		}
	}

	/**
	 * 跳期时生成即时比分上一期文件-删除正在进行和未开赛的比赛直播
	 * @author kouyi
	 * @return
	 */
	public boolean createLastJsbfMatch() {
		try {
			Map<String, Schedule> lastMap = (Map<String, Schedule>) memcache.get(jsbfkey(-1));
			if(StringUtil.isEmpty(lastMap)) {
				lastMap = matchInit(scheduleService.queryScheduleList(todaystr(-1)));
			}
			Iterator<Entry<String, Schedule>> it = lastMap.entrySet().iterator();
			while(it.hasNext()){
				Entry<String, Schedule> entry = it.next();
				if(entry.getValue().getMatchState() > -1 && entry.getValue().getMatchState() < 4) {
					it.remove();
				}
			}
			FileUtils.write(new File(position + todaystr(-1) + ".json"), JsonUtil.JsonArray(doSort(lastMap), show), Grab.CHARTSET_UTF8);
			return true;
		} catch (Exception e) {
			logger.error("[即时比分跳期时生成上一期对阵] 异常", e);
		}
		return false;
	}

	/**
	 * 抓取好彩店下一期的竞彩赛事
	 * @author kouyi
	 */
	public void grabHaoCaiNextMatch() {
		try {
			long start = System.currentTimeMillis();
			List<Schedule> list = grabForJsbfMatch.collect(MessageFormat.format(matchUrl, new Object[]{JsbfUtil.next()}), null, Grab.CHARTSET_GBK, host);
			if (StringUtil.isEmpty(list)) {
				return;
			}
			for (Schedule schedule : list) {
				String key = JSBF_MATCH_KEY + schedule.getScheduleId();
				if (!memcache.contains(key)) {//插入
					int row = scheduleService.insertSchedule(schedule);
					if (row > -1) {
						memcache.set(key, schedule, JSBF_MATCH_KEY_EXPIRE);
						if (row == 0) {//缓存过期，库中已经有该对阵
							scheduleService.update(schedule);
						}
					}
				} else {//更新
					Schedule last = (Schedule) memcache.get(key);
					if (StringUtil.isNotEmpty(last) && !sameMc(schedule).equals(sameMc(last))) {
						memcache.set(key, schedule, JSBF_MATCH_KEY_EXPIRE);//更新赛事缓存
						//队列更新库
						scheduleService.update(schedule);
					}
				}
			}
			Map<String, Schedule> lastMap = matchInit(scheduleService.queryScheduleList(todaystr(1)));
			FileUtils.write(new File(position + todaystr(1) + ".json"), JsonUtil.JsonArray(doSort(lastMap), show), Grab.CHARTSET_UTF8);
			long end = System.currentTimeMillis();
			logger.info("[即时比分对阵抓取] 成功抓取处理即时比分对阵数据 " + list.size() + " 条,用时" + (end - start) / 1000 + "秒");
		} catch (Exception e) {
			logger.error("[即时比分对阵抓取] 异常", e);
		}
	}

	/**
	 * 生成期次文件
	 * @author kouyi
	 */
	public void createJsbfPeriod() {
		try {
			List<Schedule> periodList = scheduleService.queryPeriodNumList(todaystr(-1), todaystr(1));
			if(StringUtil.isEmpty(periodList)) {
				return;
			}

			List<JsbfPeriodVo> array = new ArrayList<>();
			for(Schedule sh : periodList) {
				JsbfPeriodVo pd = new JsbfPeriodVo();
				pd.setPeriod(sh.getPeriod());
				pd.setTitle(getTitle(sh.getPeriod(), sh.getFlag()));
				pd.setShow(sh.getPeriod().equals(todaystr(0)) ? true : false);
				array.add(pd);
			}
			FileUtils.write(new File(change_position + "jsbf_period.json"), JsonUtil.JsonArray(array), Grab.CHARTSET_UTF8);
		} catch (Exception e) {
			logger.error("[即时比分生成历史期次文件] 异常", e);
		}
	}

	/**
	 * 即时比分查库后与缓存同步
	 * @author kouyi
	 * @param schelist
	 */
	public Map<String, Schedule> matchInit(List<Schedule> schelist) {
		if (StringUtil.isEmpty(schelist)) {
			return null;
		}
		Map<String, Schedule> mapsche = new HashMap<String, Schedule>();
		for (Schedule sche : schelist) {
			String key = JSBF_MATCH_KEY + sche.getScheduleId();
			Schedule lastsche = (Schedule) memcache.get(key);
			if(StringUtil.isNotEmpty(lastsche)) {
				//已经过开赛半小时 状态还是未开赛的比赛 可能延期，此处直接过滤
				if(new Date().getTime() - lastsche.getMatchTime().getTime() > 30*60*1000 && lastsche.getMatchState() == 0) {
					continue;
				}
				//比赛距离开赛时间超过150分钟还在进行中，直接过滤
				if(new Date().getTime() - lastsche.getMatchTime().getTime() >= 150*60*1000 && lastsche.getMatchState() > 0 && lastsche.getMatchState() < 4) {
					continue;
				}
				//同步
				if (!StringUtil.isEmpty(lastsche) && !sameMc(sche).equals(sameMc(lastsche))) {
					schesynchro(sche, lastsche);
				}
			}
			mapsche.put(sche.getScheduleId(), sche);
		}
		return mapsche;
	}

	/**
	 * 即时比分排序
	 * @author kouyi
	 * @param map
	 */
	public List<JsbfScheduleVO> doSort(Map<String, Schedule> map) throws Exception {
		List<JsbfScheduleVO> live = new ArrayList<JsbfScheduleVO>();// 进行中
		if (StringUtil.isEmpty(map)) {
			return live;
		}
		List<JsbfScheduleVO> nostart = new ArrayList<JsbfScheduleVO>(); // 未开赛
		List<JsbfScheduleVO> over = new ArrayList<JsbfScheduleVO>(); // 已结束
		for (Entry<String, Schedule> entry : map.entrySet()) {
			JsbfScheduleVO vo = new JsbfScheduleVO();
			setSchedule(vo, entry.getValue());
			int code = vo.getState();
			if (code == 1 || code == 2 || code == 3 || code == 4)
			{
				live.add(vo);
			}
			else if (code == 0) {
				nostart.add(vo);
			}
			else {
				over.add(vo);
			}
		}
		Collections.sort(live);
		Collections.sort(nostart);
		Collections.sort(over);
		live.addAll(nostart);
		live.addAll(over);
		return live;
	}

	/**
	 * 即时比分对阵-对象赋值格式化
	 * @param vo
	 * @param dule
	 */
	private static void setSchedule(JsbfScheduleVO vo, Schedule dule) {
		if(StringUtil.isEmpty(vo) || StringUtil.isEmpty(dule)) {
			return;
		}
		vo.setSid(dule.getScheduleId());
		vo.setLeague(dule.getLeague());
		vo.setPeriod(dule.getPeriod());
		vo.setHname(dule.getHomeTeam().length() > 5 ? dule.getHomeTeam().substring(0, 5) : dule.getHomeTeam());
		vo.setGname(dule.getGuestTeam().length() > 5 ? dule.getGuestTeam().substring(0, 5) : dule.getGuestTeam());
		vo.setHaddle(dule.getNeutrality());
		vo.setWeek(dule.getWeek());
		vo.setMid(dule.getJcId());
		vo.setState(dule.getMatchState());
		vo.setBtime(dule.getBeginTime());
		vo.setStime(DateUtil.dateDefaultFormat(new Date()));
		setScoreDesc(vo, dule);
	}

	/**
	 * 即时比分直播文件-对象赋值格式化
	 * @param vo
	 * @param dule
	 */
	private static void setLiveSchedule(JsbfScheduleVO vo, Schedule dule) {
		if(StringUtil.isEmpty(vo) || StringUtil.isEmpty(dule)) {
			return;
		}
		vo.setSid(dule.getScheduleId());
		vo.setState(dule.getMatchState());
		if(StringUtil.isNotEmpty(dule.getBeginTime())) {
			vo.setBtime(dule.getBeginTime());
		}
		vo.setStime(DateUtil.dateDefaultFormat(new Date()));
		setScoreDesc(vo, dule);
	}

	/**
	 * 初始化状态描述和比分
	 * @param vo
	 * @param dule
	 */
	private static void setScoreDesc(JsbfScheduleVO vo, Schedule dule) {
		if(dule.getMatchState() == 0) {
			vo.setSdesc(DateUtil.formatDate(dule.getMatchTime(), DateUtil.CHINESE_HOUR_TIME) + "开赛");
		} else if(dule.getMatchState() == 1) {
			if (StringUtil.isEmpty(vo.getBtime())) {
				vo.setSdesc(DateUtil.formatDate(dule.getMatchTime(), DateUtil.CHINESE_HOUR_TIME) + "开赛");
			} else {
				long mm = (new Date().getTime() - vo.getBtime().getTime()) / 1000 / 60;
				if (mm < 1) {
					mm = 1;
				}
				vo.setSdesc(mm > 45 ? "45+" : (mm + ""));
			}
		} else if(dule.getMatchState() == 2) {
			vo.setSdesc("中场");
		} else if(dule.getMatchState() == 3) {
			if (StringUtil.isEmpty(vo.getBtime())) {
				vo.setSdesc("中场");
			} else {
				long mm = 45 + ((new Date().getTime() - vo.getBtime().getTime()) / 1000 / 60);
				if (mm < 46) {
					mm = 46;
				}
				vo.setSdesc(mm > 90 ? "90+" : (mm + ""));
			}
		} else {
			vo.setSdesc("已完场");
		}

		String score = stateMap.get(dule.getMatchState());
		vo.setScore(score);
		if(dule.getMatchState() > 0 || dule.getMatchState() == -1) {
			if(StringUtil.isNotEmpty(dule.getHomeScore()) && StringUtil.isNotEmpty(dule.getGuestScore())) {
				vo.setScore(dule.getHomeScore() + ":" + dule.getGuestScore());
			} else {
				vo.setScore("0:0");
			}
		}
		String halfScore = stateMap.get(dule.getMatchState());
		vo.setHscore(halfScore.equals("未开")?"":halfScore);
		if(dule.getMatchState() > 0 || dule.getMatchState() == -1) {
			if(StringUtil.isNotEmpty(dule.getHomeHalfScore()) && StringUtil.isNotEmpty(dule.getGuestHalfScore())) {
				vo.setHscore(dule.getHomeHalfScore() + ":" + dule.getGuestHalfScore());
			} else {
				vo.setHscore("0:0");
			}
		}
	}

	/**
	 * 拼接title
	 * @param period
	 * @param n
	 * @return
	 */
	private String getTitle(String period, int n) {
		Date p = DateUtil.dateFormat(period, DateUtil.DEFAULT_DATE);
		return DateUtil.dateFormat(p,DateUtil.CHINESE_MONTH_DATE) + " " + DateUtil.getWeekStr(p) + " " + n + "场比赛";
	}

}
