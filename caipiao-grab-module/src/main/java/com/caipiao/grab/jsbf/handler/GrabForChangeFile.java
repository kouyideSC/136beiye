package com.caipiao.grab.jsbf.handler;

import java.util.ArrayList;
import java.util.List;

import com.caipiao.common.http.Grab;
import com.caipiao.common.util.DOMXmlUtil;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.jsbf.Schedule;
import com.caipiao.grab.util.JsbfUtil;
import com.caipiao.memcache.MemCached;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 抓取即时比分直播文件
 * Created by kouyi on 2017/11/22.
 */
@Component("grabForChangeFile")
public class GrabForChangeFile extends Grab<List<Schedule>, Integer> {
	private static Logger logger = LoggerFactory.getLogger(GrabForChangeFile.class);
	@Autowired
	private MemCached memcache;

	@Override
	public List<Schedule> parse(String content, Integer p) {
		try {
			List<Schedule> list = new ArrayList<Schedule>();
			if(StringUtil.isEmpty(content)){
				return list;
			}
			List<Element> elements = DOMXmlUtil.fromtoXml(content);
			if(StringUtil.isEmpty(elements)){
				return list;
			}
			for(Element el : elements) {
				Schedule sche = new Schedule();
				String[] text = el.getText().split("\\^");
				if(StringUtil.isEmpty(text) || text.length < 14) {
					continue;
				}
				sche.setScheduleId(parseString(text[0]));
				if(!memcache.contains(JsbfUtil.JSBF_MATCH_KEY + sche.getScheduleId())) {
	 				continue;
				}
				sche.setMatchState(parseInt(text[1]));
				if(!StringUtil.isEmpty(text[2]))
				{
					sche.setHomeScore(parseInt(text[2]));
				}
				if(!StringUtil.isEmpty(text[3]))
				{
					sche.setGuestScore(parseInt(text[3]));
				}
				if(!StringUtil.isEmpty(text[4]))
				{
					sche.setHomeHalfScore(parseInt(text[4]));
				}
				if(!StringUtil.isEmpty(text[5]))
				{
					sche.setGuestHalfScore(parseInt(text[5]));
				}
				if(!StringUtil.isEmpty(text[6]))
				{
					sche.setHomeRed(parseInt(text[6]));
				}
				if(!StringUtil.isEmpty(text[7]))
				{
					sche.setGuestRed(parseInt(text[7]));
				}
				if(!StringUtil.isEmpty(text[9])){
					sche.setBeginTime(DateUtil.getDateTime(text[9]));
				}
				if(!StringUtil.isEmpty(text[12]))
				{
					sche.setHomeYellow(parseInt(text[12]));
				}
				if(!StringUtil.isEmpty(text[13]))
				{
					sche.setGuestYellow(parseInt(text[13]));
				}
				list.add(sche);
				return list;
			}
		} catch (Exception e) {
			logger.error("[即时比分直播文件抓取] 解析数据异常", e);
		}
		return null;
	}
}
