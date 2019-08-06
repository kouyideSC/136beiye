package com.caipiao.admin.service.weihu.params;

import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.common.ParameterMapper;
import com.caipiao.domain.common.Parameter;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 后台参数相关服务
 * Created by kouyi on 2018/01/31.
 */
@Service("paramsService")
public class ParamsService
{
    @Autowired
    private ParameterMapper paramsMapper;

    /**
     * 查询参数列表（后台管理）
     * @author kouyi
     */
    public List<BaseDto> queryParamsList(Dto params)
    {
        return paramsMapper.queryParameters(params);
    }

    /**
     * 新增参数（后台管理）
     * @author kouyi
     */
    public int saveParams(Dto params)
    {
        return paramsMapper.saveParams(params);
    }

    /**
     * 删除参数（后台管理）
     * @author kouyi
     */
    public int deleteParams(Dto params)
    {
        return paramsMapper.deleteParams(params);
    }

    /**
     * 修改参数（后台管理）
     * @author kouyi
     */
    public int updateParams(Dto params) {
        return paramsMapper.updateParameter(params);
    }
}
