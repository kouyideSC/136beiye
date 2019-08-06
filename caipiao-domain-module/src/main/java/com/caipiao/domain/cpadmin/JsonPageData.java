package com.caipiao.domain.cpadmin;

import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * json数据对象(针对页面)
 * @author  mcdog
 */
public class JsonPageData
{
    private int dcode;//状态码
    private String dmsg;//状态描述
    private Dto datas;//数据对象

    public JsonPageData(){}

    /**
     * 返回数据数据构造对象(只返回状态/描述)
     * @author  mcdog
     * @param   dcode  状态码
     * @param   dmsg   状态描述
     */
    public JsonPageData(int dcode,String dmsg)
    {
        this.dcode = dcode;
        this.dmsg = dmsg;
    }

    /**
     * 返回数据数据构造对象(返回状态/描述 + 回显数据)
     * @author  mcdog
     * @param   dcode  状态码
     * @param   dmsg   状态描述
     * @param   datas   返回数据对象
     */
    public JsonPageData(int dcode,String dmsg,Dto datas)
    {
        this.dcode = dcode;
        this.dmsg = dmsg;
        this.datas = datas;
    }

    /**
     * 返回数据数据构造对象(返回状态/描述 + 无分页的业务数据)
     * @author  mcdog
     * @param   dcode      状态码
     * @param   dmsg       状态描述
     * @param   dataList    数据集合
     */
    public JsonPageData(int dcode,String dmsg,List<Dto> dataList)
    {
        this.dcode = dcode;
        this.dmsg = dmsg;
        this.datas = new BaseDto("list",dataList);
    }

    /**
     * 返回数据数据构造对象(默认返回成功状态/描述 + 有分页的业务数据)
     * @author  mcdog
     * @param   dataList    数据集合
     * @param   total       数据总记录条数
     */
    public JsonPageData(List<Dto> dataList,int total,Dto params)
    {
        this.dcode = 1000;
        this.dmsg = "success";
        this.datas = new BaseDto();
        datas.put("list",dataList == null? new Object[]{} : dataList);
        if(StringUtils.isNotEmpty(params.getAsString("psize")))
        {
            if(dataList == null)
            {
                datas.put("tsize",0);
                datas.put("tpage",0);
            }
            else
            {
                int psize = params.getAsInteger("psize");
                datas.put("tsize",total);
                datas.put("tpage",total % psize > 0? (total / psize + 1) : (total / psize));
            }
        }
    }

    /**
     * 返回数据数据构造对象(返回状态/描述 + 有分页的业务数据)
     * @author  mcdog
     * @param   dcode      状态码
     * @param   dmsg       状态描述
     * @param   dataList    返回数据对象
     * @param   total       总记录条数
     * @param   params      分页等参数对象
     */
    public JsonPageData(int dcode,String dmsg,List<Dto> dataList,int total,Dto params)
    {
        this.dcode = dcode;
        this.dmsg = dmsg;
        this.datas = new BaseDto();
        datas.put("list",dataList == null? new Object[]{} : dataList);
        if(StringUtils.isNotEmpty(params.getAsString("psize")))
        {
           if(dataList == null)
           {
               datas.put("tsize",0);
               datas.put("tpage",0);
           }
           else
           {
               int psize = params.getAsInteger("psize");
               datas.put("tsize",total);
               datas.put("tpage",total % psize > 0? (total / psize + 1) : (total / psize));
           }
        }
    }

    /**
     * 重写toString方法
     * @author  mcdog
     */
    public String toString()
    {
        Dto json = new BaseDto();
        json.put("dcode",this.dcode);
        json.put("dmsg",this.dmsg);
        json.put("datas",datas);
        return JsonHelper.encodeObject2Json(json).replace(":null", ":\"\"");
    }

    public int getDcode() {
        return dcode;
    }

    public void setDcode(int dcode) {
        this.dcode = dcode;
    }

    public String getDmsg() {
        return dmsg;
    }

    public void setDmsg(String dmsg) {
        this.dmsg = dmsg;
    }

    public Dto getDatas() {
        return datas;
    }

    public void setDatas(Dto datas) {
        this.datas = datas;
    }
}