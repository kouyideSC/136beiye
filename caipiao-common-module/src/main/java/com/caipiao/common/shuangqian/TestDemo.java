package com.caipiao.common.shuangqian;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * 批量代付
 */
public class TestDemo {
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String merno = "168885"; //商户号
		String time = "20170212154230"; //时间
		String totalAmount = "500"; //金额
		String num = "2"; //笔数
		String batchNo = System.currentTimeMillis() + ""; //批次号
		String content = "开户名1|1001|6226131223214231|1|200|20170210000000|302|数字字母汉字"
				+ "#"
				+ "开户名2|1002|6226131223214541|2|300|20170210000000|303|1235"; //内容
		String signature = ""; //签名
		String remark = "备注测试"; //备注
		
		//测试地址
		String url = "http://218.4.234.150:9600/merchant/numberPaid.action";
        // 创建默认的httpClient实例
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httppost = new HttpPost(url);
        
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		resultMap.put("merno", merno);
		resultMap.put("time", time);
		resultMap.put("totalAmount", totalAmount);
		resultMap.put("num", num);
		resultMap.put("batchNo", batchNo);
		resultMap.put("content", content);
		// 商户私钥
        String pfxPath = "D:\\certificate\\168885_test.pfx";
        // 双乾测试环境公钥
		String certPath = "D:\\certificate\\95epay_test_cfca.cer";
		// 商户私钥证书密码
		String passWord = "123123";
        try {
        	String beforeSignedData = joinMapValue(resultMap, '&');
        	signature = CertificateUtil.signMessageByP1(beforeSignedData, pfxPath, passWord);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
        
        // 创建参数队列
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("merno", merno));
        formparams.add(new BasicNameValuePair("time", time));
        formparams.add(new BasicNameValuePair("totalAmount", totalAmount));
        formparams.add(new BasicNameValuePair("num", num));
        formparams.add(new BasicNameValuePair("batchNo", batchNo));
        formparams.add(new BasicNameValuePair("content", content));
        formparams.add(new BasicNameValuePair("remark", remark));
        formparams.add(new BasicNameValuePair("signature", signature));
        
        UrlEncodedFormEntity uefEntity;
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");  
            httppost.setEntity(uefEntity);  
            System.out.println("executing request " + httppost.getURI());  
            CloseableHttpResponse response = httpclient.execute(httppost);  
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                	String entitys = EntityUtils.toString(entity, "UTF-8");
                    System.out.println("--------------------------------------");
                    System.out.println("Response content: " + entitys);
                    System.out.println("--------------------------------------");
                	
                    HashMap<String, Object> data = new LinkedHashMap<String, Object>();
                    JSONObject json = JSONObject.fromObject(entitys);
                    Iterator<?> it = json.keys();  
                    // 遍历jsonObject数据，添加到Map对象  
                    while(it.hasNext()){  
                        String key = String.valueOf(it.next());  
                        Object value = json.get(key);
                        if(key.equals("signature")||key.equals("remark")){
                        	continue;
                        }
                        data.put(key, value);  
                    }
                    String beforeSignedData = joinMapValue(data, '&');
                    String s = (String) json.get("signature");
                    System.out.println(beforeSignedData);
                    System.out.println("--------------------------------------");
                    System.out.println("Verify result: " + CertificateUtil.verifyMessageByP1(beforeSignedData, s, certPath));
                    System.out.println("--------------------------------------");
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源 
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	
	/**
	 * 签名前的字符串
	 * @param map
	 * @param connector
	 * @return
	 */
	public static String joinMapValue(Map<String, Object> map, char connector)	{
		StringBuffer b = new StringBuffer();
		for (Map.Entry<String, Object> entry : map.entrySet()){
			b.append(entry.getKey());
			b.append('=');
			if (entry.getValue() != null){
				b.append(entry.getValue());
			}
			b.append(connector);
		}
		return b.toString().substring(0, b.length()-1);
	}
}
