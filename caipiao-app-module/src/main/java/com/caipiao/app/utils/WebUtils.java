package com.caipiao.app.utils;

import com.caipiao.common.util.StringUtil;
import com.caipiao.common.util.exception.EmptyPropertiesException;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.github.pagehelper.PageInfo;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class WebUtils {
    public static int SQL_NEXTVAL = 1;

    public WebUtils() {
    }

    public static BaseDto getPraramsAsDto(HttpServletRequest request) {
        BaseDto dto = new BaseDto();
        Map map = request.getParameterMap();
        Iterator keyIterator = map.keySet().iterator();

        while (keyIterator.hasNext()) {
            String key = (String) keyIterator.next();
            String value = "";
            if (((String[]) ((String[]) map.get(key))).length > 0) {
                String[] arr$ = (String[]) ((String[]) map.get(key));
                int len$ = arr$.length;

                for (int i$ = 0; i$ < len$; ++i$) {
                    String v = arr$[i$];
                    value = value + v + ",";
                }

                dto.put(key, value.substring(0, value.length() - 1));
            } else {
                value = ((String[]) ((String[]) map.get(key)))[0];
                dto.put(key, value.trim());
            }
        }
        dto.put("current_login_creator", request.getAttribute("current_login_creator"));
        dto.put("current_login_personal", request.getAttribute("current_login_personal"));
        dto.put("dataSecurityContractFlag", request.getAttribute("dataSecurityContractFlag"));
        dto.put("dataSecurityContractApplicationFlag", request.getAttribute("dataSecurityContractApplicationFlag"));
        dto.put("dataSecurityFlag", request.getAttribute("dataSecurityFlag"));
        return dto;
    }

    public static Dto getPraramsAsDtoUTF8(HttpServletRequest request) {
        BaseDto dto = new BaseDto();
        Map map = request.getParameterMap();

        String key;
        String value;
        for (Iterator keyIterator = map.keySet().iterator(); keyIterator.hasNext(); dto.put(key, value)) {
            key = (String) keyIterator.next();
            value = ((String[]) ((String[]) map.get(key)))[0];

            try {
                value = new String(value.getBytes("ISO8859-1"), "UTF-8");
            } catch (UnsupportedEncodingException var7) {
                var7.printStackTrace();
            }
        }
        dto.put("current_login_creator", request.getAttribute("current_login_creator"));
        dto.put("current_login_personal", request.getAttribute("current_login_personal"));
        dto.put("dataSecurityContractFlag", request.getAttribute("dataSecurityContractFlag"));
        dto.put("dataSecurityContractApplicationFlag", request.getAttribute("dataSecurityContractApplicationFlag"));
        dto.put("dataSecurityFlag", request.getAttribute("dataSecurityFlag"));
        return dto;
    }

    public static Dto getMultipartPraramsAsDto(HttpServletRequest request) {
        DiskFileUpload upload = new DiskFileUpload();
        upload.setHeaderEncoding("UTF-8");
        BaseDto dto = new BaseDto();

        try {
            List ex = upload.parseRequest(request);
            Iterator i$ = ex.iterator();

            while (i$.hasNext()) {
                Object item1 = i$.next();
                FileItem item = (FileItem) item1;
                if (item.isFormField()) {
                    String key = item.getFieldName();
                    String value = item.getString("UTF-8");
                    dto.put(key, value);
                }
            }
        } catch (Exception var9) {
            var9.printStackTrace();
        }
        dto.put("current_login_creator", request.getAttribute("current_login_creator"));
        dto.put("current_login_personal", request.getAttribute("current_login_personal"));
        dto.put("dataSecurityContractFlag", request.getAttribute("dataSecurityContractFlag"));
        dto.put("dataSecurityContractApplicationFlag", request.getAttribute("dataSecurityContractApplicationFlag"));
        dto.put("dataSecurityFlag", request.getAttribute("dataSecurityFlag"));
        return dto;
    }

    public static String getCodeDesc(String pField, String pCode, HttpServletRequest request) {
        List codeList = (List) request.getSession().getServletContext().getAttribute("EACODELIST");
        String codedesc = null;

        for (int i = 0; i < codeList.size(); ++i) {
            BaseDto codeDto = (BaseDto) codeList.get(i);
            if (pField.equalsIgnoreCase(codeDto.getAsString("field")) && pCode.equalsIgnoreCase(codeDto.getAsString("code"))) {
                codedesc = codeDto.getAsString("codedesc");
            }
        }

        return codedesc;
    }

    public static List getCodeListByField(String pField, HttpServletRequest request) {
        List codeList = (List) request.getSession().getServletContext().getAttribute("EACODELIST");
        ArrayList lst = new ArrayList();

        for (int i = 0; i < codeList.size(); ++i) {
            BaseDto codeDto = (BaseDto) codeList.get(i);
            if (codeDto.getAsString("field").equalsIgnoreCase(pField)) {
                lst.add(codeDto);
            }
        }

        return lst;
    }

    public static String getParamValue(String pParamKey, HttpServletRequest request) {
        String paramValue = "";
        ServletContext context = request.getSession().getServletContext();
        if (StringUtil.isEmpty(context)) {
            return "";
        } else {
            List paramList = (List) context.getAttribute("EAPARAMLIST");

            for (int i = 0; i < paramList.size(); ++i) {
                BaseDto paramDto = (BaseDto) paramList.get(i);
                if (pParamKey.equals(paramDto.getAsString("paramkey"))) {
                    paramValue = paramDto.getAsString("paramvalue");
                }
            }

            return paramValue;
        }
    }

    public static List getParamList(HttpServletRequest request) {
        ServletContext context = request.getSession().getServletContext();
        return (List) (StringUtil.isEmpty(context) ? new ArrayList() : (List) context.getAttribute("EAPARAMLIST"));
    }

    public static String getCookieValue(Cookie[] cookies, String cookieName, String defaultValue) {
        if (cookies == null) {
            return defaultValue;
        } else {
            for (int i = 0; i < cookies.length; ++i) {
                Cookie cookie = cookies[i];
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }

            return defaultValue;
        }
    }

    public static String getClassPath() {
        String path;
        for (path = WebUtils.class.getResource("/").getPath(); path.indexOf("%20") != -1; path = path.replace("%20", " ")) {
            ;
        }

        if (path.startsWith("/")) {
            path = path.substring(1, path.length());
        }

        return path;
    }

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }


    public static void printImageToClient(File file, HttpServletResponse response) throws IOException {
        String mimeType = "";
        String fileName = file.getName();
        if (fileName.length() > 5) {
            if (fileName.substring(fileName.length() - 5, fileName.length()).equals(".jpeg")) {
                mimeType = "image/jpeg";
            } else if (fileName.substring(fileName.length() - 4, fileName.length()).equals(".png")) {
                mimeType = "image/png";
            } else if (fileName.substring(fileName.length() - 4, fileName.length()).equals(".gif")) {
                mimeType = "image/gif";
            } else {
                mimeType = "image/jpg";
            }
        }

        if (file.exists()) {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            response.setHeader("Content-Type", mimeType);
            response.setHeader("Content-Length", String.valueOf(file.length()));
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            response.setHeader("Last-Modified", sdf.format(new Date(file.lastModified())));
            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
            byte[] input = new byte[4096];
            boolean eof = false;

            while (!eof) {
                int length = bis.read(input);
                if (length == -1) {
                    eof = true;
                } else {
                    bos.write(input, 0, length);
                }
            }

            bos.flush();
            bis.close();
            bos.close();
            response.setStatus(200);
            response.flushBuffer();
        } else {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
    }

    public static void write(String str, HttpServletResponse response) {
        try {
            response.setHeader("Cache-Control", "no-cache");
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(str);
            response.getWriter().flush();
            response.getWriter().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> PageInfo<T> convertPara2Page(HttpServletRequest request, Class cls) throws Exception {
        //paging info parameter : rows-->10; page-->1;sord-->asc/desc;sidx-->sort for filed
        Dto params = WebUtils.getPraramsAsDto(request);
        checkProperties(params, "size", "page");
        int rows = Integer.valueOf(request.getParameter("size"));
        int page = Integer.valueOf(request.getParameter("page"));
        PageInfo<T> pageInfo = new PageInfo<T>();
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(rows);
        if (null != request.getParameter("sorts") && !StringUtils.isEmpty(request.getParameter("sorts"))) {
            pageInfo.setOrderBy(request.getParameter("sorts"));
        } else {
            try {
                if (cls.getDeclaredField("createTime") != null) {
                    pageInfo.setOrderBy("CREATE_TIME DESC");
                }
            } catch (Exception e) {
                System.out.println("无默认排序！");
            }
        }
        return pageInfo;
    }

    private static boolean checkProperty(Map dto, String property) {
        if (dto.get(property) == null || "".equals((dto.get(property).toString()).trim())) {
            return false;
        }
        return true;
    }

    /**
     * 检查Dto 存在为null或者“”抛出异常
     *
     * @param dto
     * @param properties
     * @return
     */

    public static boolean checkProperties(Map dto, String... properties) throws Exception {
        for (String property : properties) {
            if (!checkProperty(dto, property)) {
                throw new EmptyPropertiesException("必要参数 \"" + property + "\" 不能为空！");
            }
        }

        return true;
    }

    protected ModelAndView ajaxDone(int statusCode, String message) {
        ModelAndView mav = new ModelAndView("ajaxDone");
        mav.addObject("statusCode", Integer.valueOf(statusCode));
        mav.addObject("message", message);
        return mav;
    }

    protected Dto ajaxDoneDto(int statusCode, String message) {
        BaseDto mav = new BaseDto();
        mav.put("statusCode", Integer.valueOf(statusCode));
        mav.put("message", message);
        return mav;
    }

    protected Dto ajaxDoneDtoAndClose(int statusCode, String message) {
        BaseDto mav = new BaseDto();
        mav.put("callbackType", "closeCurrent");
        mav.put("statusCode", Integer.valueOf(statusCode));
        mav.put("message", message);
        return mav;
    }
    
    public static Dto getParamsAsDto(HttpServletRequest request) 
    {
        BaseDto dto = new BaseDto();
        Map map = request.getParameterMap();
        Iterator keyIterator = map.keySet().iterator();

        while (keyIterator.hasNext()) {
            String key = (String) keyIterator.next();
            String value = "";
            if (((String[]) ((String[]) map.get(key))).length > 0) {
                String[] arr$ = (String[]) ((String[]) map.get(key));
                int len$ = arr$.length;

                for (int i$ = 0; i$ < len$; ++i$) {
                    String v = arr$[i$];
                    value = value + v + ",";
                }

                dto.put(key, value.substring(0, value.length() - 1));
            } else {
                value = ((String[]) ((String[]) map.get(key)))[0];
                dto.put(key, value.trim());
            }
        }
        return dto;
    }
    
    public static Dto getParamsAsDtoFromJson(HttpServletRequest request)
    {
        Dto result = new BaseDto();
        try
        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            InputStream is = request.getInputStream();
            FileCopyUtils.copy(is, os);
            result.putAll(JSONObject.fromObject(new String(os.toByteArray())));
            os.close();
            is.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static Dto getParamsAsDtoFromXml(HttpServletRequest request)
    {
        Dto result = new BaseDto();
        try
        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            InputStream is = request.getInputStream();
            FileCopyUtils.copy(is, os);
            result.putAll(StringUtil.parseDtoFromXmlStr(new String(os.toByteArray())));
            os.close();
            is.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }
}