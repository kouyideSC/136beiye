package com.caipiao.common.pay.huichao;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.caipiao.common.pay.huichao.utils.HTTPClientUtils;
import com.caipiao.common.pay.huichao.utils.RsaUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * 代付请求查询
 */
public class Query {
	
	private static String Daifu_REQUEST_URL = "https://gwapi.yemadai.com/transfer/transferQueryFixed";   //正式环境请求地址
	
	public static void main(String[] args) {
		Query testpay = new Query();
		try {
			testpay.testpay();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testpay() throws UnsupportedEncodingException {
		
		RsaUtils rsaUtils = RsaUtils.getInstance();
        String accountNumber="45375";
        String requestTime="20180706114322";
        String plain ="merchantNumber="+accountNumber+"&requestTime="+requestTime;
        String prikey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALgFkT1o8Dag4eJSFHpPMiBHLvTv556awOj8OfMPafe9Q1w76HL7SvGCYn48+bfodW+PLrcIsjc5AwlooLT9B7bUx8tkBjvyMj83EigzteofPEGD7+K3NSCJR2UFFE5k0ow9IFoUsWIW7qbylxQG+t6qfMzRodtMK1zJ2iO2Y4PnAgMBAAECgYEAp0uIasfH+iHwuQvdygPNkkKkkdC4RRxzXFxRYoMU10CcyHE+Nan2y/C5EgLlEyil+rG0ynmBa2rNM/SGhYOzShi/fFGT+MEfXjUtJDeJLXu3G6bnUWndkqIYibbk+fmshy81hQm6TXh0bDvOdUjjEajYmUpCIMAof7tfsBtHywECQQD8C2qJlfQCwDK9UV+QaWjBgHVVdiFqL3L+Gu+Me+jbzbx+Shk28fm6pyi998fbxXXTCUGeflqho5z7KOyUisrhAkEAuuje7q8A4TJs6M0INag6VBs47pz8rUGkIGlnmocr6cDm7ouBlBHomQtV6xW2CxoSQQVvT2nsAZOx1b3MZ9CvxwJAeOjFF/Gel/85mAZEUNOwVDtajj/YMcdHY8zqI7uBbohYp0DGrcwQ39C2w8Ls1mn4Zt+m4fB9a9NASGBOdcfLIQJBAJAomgE34xLN5Kgtsz5HUS2bjW6kkFJFBYSmJ21NAjaZPMQRv1Bn+6FG1+6oYS7w3dFekrqKdKfGtWuopuYPU/MCQBVMzhzyastpwLnBh1xOlRbtleCqxVe0+BcIZAW1TqXa7StWaefbukuT4jW3ELTAipg5ufPNsSkkxiTXpeqngoA=";
        String signInfo = rsaUtils.signData(plain, prikey);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        stringBuffer.append("<yemadai>");
        stringBuffer.append("<merchantNumber>"+accountNumber+"</merchantNumber>");
        stringBuffer.append("<signType>RSA</signType>");
        stringBuffer.append("<mertransferID>TX201807060951048034</mertransferID>");
        stringBuffer.append("<requestTime>" + requestTime + "</requestTime>");
        stringBuffer.append("<sign>"+signInfo+"</sign>");
        stringBuffer.append("</yemadai>");
        
        Base64 base64 = new Base64();
        
        List<NameValuePair> nvps = new ArrayList<NameValuePair>(1);
		nvps.add(new BasicNameValuePair("requestDomain", base64.encodeToString(stringBuffer.toString().getBytes("UTF-8"))));
        System.out.println(nvps);
        String httpPost = this.connect(nvps, Daifu_REQUEST_URL);
        System.out.println(httpPost);
        System.out.println(new String(base64.decode(httpPost), "utf-8"));
	}
	
	/**
	 * 连接类
	 * 
	 * @param nvps
	 * @param requestURL
	 * @return
	 */
	public String connect(List<NameValuePair> nvps, String requestURL) {
		try {
			
			HTTPClientUtils h = new HTTPClientUtils();
			String httpPost = h.httpPostPara(nvps, requestURL);
			return httpPost;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
