package com.caipiao.common.pay.haipay.util;

/**
 * @Description: base64编码解码工具类
 * @author dingzhiwei jmdhappy@126.com
 * @date 2017-07-05
 * @version V1.0
 * @Copyright: www.xxpay.org
 */
public class MyBase64 {
	public final static String encode(byte[] src, int startIndex, int srclen) {
		if (src == null)					return null;
		if (startIndex+srclen>src.length)	srclen = src.length-startIndex;
		byte src2[] = new byte[srclen];
		System.arraycopy(src, startIndex, src2, 0, srclen);
		return encode(src2);
	}

	public final static String encode(byte[] src, int srclen) {
		if (src == null)		return null;
		if (srclen>src.length)	srclen = src.length;
		byte data[] = new byte[srclen+2];
		System.arraycopy(src, 0, data, 0, srclen);
		byte dest[] = new byte[(data.length/3)*4];
		// 3-byte to 4-byte conversion
		for (int sidx = 0, didx=0; sidx < srclen; sidx += 3, didx += 4) {
			dest[didx]   = (byte) ((data[sidx] >>> 2) & 077);
			dest[didx+1] = (byte) ((data[sidx+1] >>> 4) & 017 | (data[sidx] << 4) & 077);
			dest[didx+2] = (byte) ((data[sidx+2] >>> 6) & 003 | (data[sidx+1] << 2) & 077);
			dest[didx+3] = (byte) (data[sidx+2] & 077);
		}
				
		// 0-63 to ascii printable conversion
		for (int idx = 0; idx <dest.length; idx++) {
			if (dest[idx] < 26)     	dest[idx] = (byte)(dest[idx] + 'A');
			else if (dest[idx] < 52)	dest[idx] = (byte)(dest[idx] + 'a' - 26);
			else if (dest[idx] < 62)  	dest[idx] = (byte)(dest[idx] + '0' - 52);
			else if (dest[idx] < 63)  	dest[idx] = (byte)'+';
			else            		dest[idx] = (byte)'/';
		}
		
		// add padding
		for (int idx = dest.length-1; idx > (srclen*4)/3; idx--) {
			dest[idx] = (byte)'=';
		}
		return new String(dest);
	}
	
	public final static String encode(byte[] d) {
		return	encode(d, d.length);
	}
	/** 每64字符后，换行 */
	public static String encode_64(byte[] bin) throws Exception {
		String b64 = MyBase64.encode(bin);
		StringBuffer sb = new StringBuffer();
		for(int offset=0; offset < b64.length(); offset+=64) {
			int idx_begin = offset;
			int idx_end = Math.min(offset+64, b64.length());
			String s = b64.substring(idx_begin, idx_end);
			sb.append(s).append('\n');
			//if (withDebug) debug(idx_begin+"..."+idx_end);
		}
		b64 = sb.toString();
		return b64;
	}

	public final static byte[] decode(String str) {
		if (str == null)  		return  new byte[0];
		str = str.trim();	//TODO 去掉结尾空格，但还不彻底，可考虑进一步改善。。。
		if (str.length()==0)	return new byte[0];
		return decode(str.getBytes());
	}
	private final static byte[] decode(byte[] data) {
		int tail = data.length;
		while (data[tail-1] == '=')	tail--;	//去掉结尾的等号
		byte dest[] = new byte[tail - data.length/4];
		for(int i = 0; i <data.length; i++) {// ascii printable to 0-63 conversion
			data[i] = decode_pre_byte(data[i]);	//TODO ？？是否有用？
		}
		// 4-byte to 3-byte conversion
		int sidx, didx;
		for (sidx = 0, didx=0; didx < dest.length-2; sidx += 4, didx += 3) {
			dest[didx]   = (byte) ( ((data[sidx]   << 2) & 255) | ((data[sidx+1] >>> 4) & 3) );
			dest[didx+1] = (byte) ( ((data[sidx+1] << 4) & 255) | ((data[sidx+2] >>> 2) & 017) );
			dest[didx+2] = (byte) ( ((data[sidx+2] << 6) & 255) | ( data[sidx+3] & 077) );
		}
		if (didx < dest.length) {
			dest[didx]   = (byte) ( ((data[sidx] << 2) & 255) | ((data[sidx+1] >>> 4) & 3) );
		}
		if (++didx < dest.length) {
			dest[didx]   = (byte) ( ((data[sidx+1] << 4) & 255) | ((data[sidx+2] >>> 2) & 017) );
		}
		return dest;
	}
	private final static byte decode_pre_byte(byte b0) {	//重构。。。
			byte b = 0;
			if (b0 >= '0' && b0 <= '9')				b = (byte)(b0 - ('0' - 52));
			else if (b0 >= 'a' && b0 <= 'z')		b = (byte)(b0 - ('a' - 26));
			else if (b0 >= 'A'  &&  b0 <= 'Z')		b = (byte)(b0 - 'A');
			else if (b0 == '=')			b = 0;
			else if (b0 == '+')			b = 62;
			else if (b0 == '/')			b = 63;
			return b;
	}
	/**
	*  A simple test.cache that encodes and decodes the first commandline argument.
	*/
	public final static void main(String args[]) throws Exception {
		//String s = "xxpay做最好的开源聚合支付系统";
		//String menuStr ="W3sKCSJuYW1lIjogImFjY291bnQiLAoJInRpdGxlIjogIui0puaItyIsCgkiaWNvbiI6ICJsYXl1aS1pY29uLXVzZXIiLAoJImp1bXAiOiB0cnVlLAoJImxpc3QiOiBbewoJCQkibmFtZSI6ICJpbmZvIiwKCQkJInRpdGxlIjogIuWfuuacrOS/oeaBryIKCQl9LCB7CgkJCSJuYW1lIjogImhpc3RvcnkiLAoJCQkidGl0bGUiOiAi6LWE6YeR5rWB5rC0IgoJCX0sIHsKCQkJIm5hbWUiOiAibW9uZXlfY2hhbmdlIiwKCQkJInRpdGxlIjogIui1hOmHkeWPmOWKqCIKCQl9LAoJCXsKCQkJIm5hbWUiOiAicGFzc2FnZSIsCgkJCSJ0aXRsZSI6ICLmlK/ku5jpgJrpgZMiCgkJfQoJXQp9LCB7CgkibmFtZSI6ICJvcmRlciIsCgkidGl0bGUiOiAi6K6i5Y2VIiwKCSJpY29uIjogImxheXVpLWljb24tY29tcG9uZW50IiwKCSJqdW1wIjogdHJ1ZSwKCSJsaXN0IjogW3sKCQkibmFtZSI6ICJwYXkiLAoJCSJ0aXRsZSI6ICLmlK/ku5jorqLljZUiCgl9LCB7CgkJIm5hbWUiOiAidHJhbnMiLAoJCSJ0aXRsZSI6ICLovazotKborqLljZUiCgl9LCB7CgkJIm5hbWUiOiAicmVmdW5kIiwKCQkidGl0bGUiOiAi6YCA5qy+6K6i5Y2VIgoJfSwgewoJCSJuYW1lIjogInRyYWRlIiwKCQkidGl0bGUiOiAi5Lqk5piT6K6i5Y2VIgoJfV0KfSwgewoJIm5hbWUiOiAic2V0dCIsCgkidGl0bGUiOiAi5Luj5LuYIiwKCSJpY29uIjogImxheXVpLWljb24tc2V0IiwKCSJqdW1wIjogdHJ1ZSwKCSJsaXN0IjogW3sKCQkibmFtZSI6ICJtYW5hZ2UiLAoJCSJ0aXRsZSI6ICLku6Pku5jnrqHnkIYiLAoJCSJzcHJlYWQiOiB0cnVlLAoJCSJsaXN0IjogW3sKCQkJIm5hbWUiOiAibGlzdCIsCgkJCSJ0aXRsZSI6ICLku6Pku5jliJfooagiCgkJfSwgewoJCQkibmFtZSI6ICJjcmVhdGUiLAoJCQkidGl0bGUiOiAi55Sz6K+35Luj5LuYIgoJCX1dCgl9LCB7CgkJIm5hbWUiOiAiYmFuayIsCgkJInRpdGxlIjogIumTtuihjOi0puWPtyIKCX1dCn0sIHsKCSJuYW1lIjogInJlY29uY2lsaWF0aW9uIiwKCSJ0aXRsZSI6ICLlr7notKYiLAoJImljb24iOiAibGF5dWktaWNvbi10ZW1wbGF0ZSIsCgkianVtcCI6IHRydWUsCgkibGlzdCI6IFt7CgkJIm5hbWUiOiAiZG93biIsCgkJInRpdGxlIjogIuS4i+i9veWvuei0piIKCX1dCn1d";
		//String menuStr ="W3sKCSJuYW1lIjogImFjY291bnQiLAoJInRpdGxlIjogIui0puaItyIsCgkiaWNvbiI6ICJsYXl1aS1pY29uLXVzZXIiLAoJImp1bXAiOiB0cnVlLAoJImxpc3QiOiBbewoJCSJuYW1lIjogImluZm8iLAoJCSJ0aXRsZSI6ICLln7rmnKzkv6Hmga8iCgl9LCB7CgkJIm5hbWUiOiAiaGlzdG9yeSIsCgkJInRpdGxlIjogIui1hOmHkea1geawtCIKCX0sIHsKCQkibmFtZSI6ICJhcHAiLAoJCSJ0aXRsZSI6ICLlupTnlKjnrqHnkIYiLAoJCSJzcHJlYWQiOiB0cnVlLAoJCSJsaXN0IjogW3sKCQkJIm5hbWUiOiAibGlzdCIsCgkJCSJ0aXRsZSI6ICLlupTnlKjliJfooagiCgkJfSwgewoJCQkibmFtZSI6ICJjcmVhdGUiLAoJCQkidGl0bGUiOiAi5paw5bu65bqU55SoIgoJCX1dCgl9LCB7CgkJIm5hbWUiOiAicGFzc2FnZSIsCgkJInRpdGxlIjogIuaUr+S7mOmAmumBkyIKCX1dCn0sIHsKCSJuYW1lIjogIm9yZGVyIiwKCSJ0aXRsZSI6ICLorqLljZUiLAoJImljb24iOiAibGF5dWktaWNvbi1jb21wb25lbnQiLAoJImp1bXAiOiB0cnVlLAoJImxpc3QiOiBbewoJCSJuYW1lIjogInBheSIsCgkJInRpdGxlIjogIuaUr+S7mOiuouWNlSIKCX0sIHsKCQkibmFtZSI6ICJ0cmFucyIsCgkJInRpdGxlIjogIui9rOi0puiuouWNlSIKCX0sIHsKCQkibmFtZSI6ICJyZWZ1bmQiLAoJCSJ0aXRsZSI6ICLpgIDmrL7orqLljZUiCgl9LCB7CgkJIm5hbWUiOiAidHJhZGUiLAoJCSJ0aXRsZSI6ICLkuqTmmJPorqLljZUiCgl9XQp9LCB7CgkibmFtZSI6ICJzZXR0IiwKCSJ0aXRsZSI6ICLku6Pku5giLAoJImljb24iOiAibGF5dWktaWNvbi1zZXQiLAoJImp1bXAiOiB0cnVlLAoJImxpc3QiOiBbewoJCSJuYW1lIjogIm1hbmFnZSIsCgkJInRpdGxlIjogIuS7o+S7mOeuoeeQhiIsCgkJInNwcmVhZCI6IHRydWUsCgkJImxpc3QiOiBbCgkJICB7CgkJCSJuYW1lIjogImxpc3QiLAoJCQkidGl0bGUiOiAi5Yet6K+B566h55CGIgoJCX0sIAoJCSAgewoJCQkibmFtZSI6ICJsaXN0IiwKCQkJInRpdGxlIjogIuS7o+S7mOWIl+ihqCIKCQl9LCB7CgkJCSJuYW1lIjogImNyZWF0ZSIsCgkJCSJ0aXRsZSI6ICLnlLPor7fku6Pku5giCgkJfV0KCX0sIHsKCQkibmFtZSI6ICJiYW5rIiwKCQkidGl0bGUiOiAi6ZO26KGM6LSm5Y+3IgoJfV0KfSwgewoJIm5hbWUiOiAicmVjb25jaWxpYXRpb24iLAoJInRpdGxlIjogIuWvuei0piIsCgkiaWNvbiI6ICJsYXl1aS1pY29uLXRlbXBsYXRlIiwKCSJqdW1wIjogdHJ1ZSwKCSJsaXN0IjogW3sKCQkibmFtZSI6ICJkb3duIiwKCQkidGl0bGUiOiAi5LiL6L295a+56LSmIgoJfV0KfV0=";

		String s ="W3sKCSJuYW1lIjogImFjY291bnQiLAoJInRpdGxlIjogIui0puaItyIsCgkiaWNvbiI6ICJsYXl1aS1pY29uLXVzZXIiLAoJImp1bXAiOiB0cnVlLAoJImxpc3QiOiBbewoJCSJuYW1lIjogImluZm8iLAoJCSJ0aXRsZSI6ICLln7rmnKzkv6Hmga8iCgl9LCB7CgkJIm5hbWUiOiAiaGlzdG9yeSIsCgkJInRpdGxlIjogIui1hOmHkea1geawtCIKCX1dCn0sIHsKCSJuYW1lIjogInNldHQiLAoJInRpdGxlIjogIuS7o+S7mCIsCgkiaWNvbiI6ICJsYXl1aS1pY29uLXNldCIsCgkianVtcCI6IHRydWUsCgkibGlzdCI6IFt7CgkJIm5hbWUiOiAibWFuYWdlIiwKCQkidGl0bGUiOiAi5Luj5LuY566h55CGIiwKCQkic3ByZWFkIjogdHJ1ZSwKCQkibGlzdCI6IFt7CgkJCQkibmFtZSI6ICJjcmVkZW50aWFsc0xpc3QiLAoJCQkJInRpdGxlIjogIuWHreivgeeuoeeQhiIKCQkJfSwKCQkJewoJCQkJIm5hbWUiOiAibGlzdCIsCgkJCQkidGl0bGUiOiAi5Luj5LuY5YiX6KGoIgoJCQl9LCB7CgkJCQkibmFtZSI6ICJjcmVhdGUiLAoJCQkJInRpdGxlIjogIueUs+ivt+S7o+S7mCIKCQkJfQoJCV0KCX0sIHsKCQkibmFtZSI6ICJiYW5rIiwKCQkidGl0bGUiOiAi6ZO26KGM6LSm5Y+3IgoJfV0KfSwgewoJIm5hbWUiOiAicmVjb25jaWxpYXRpb24iLAoJInRpdGxlIjogIuWvuei0piIsCgkiaWNvbiI6ICJsYXl1aS1pY29uLXRlbXBsYXRlIiwKCSJqdW1wIjogdHJ1ZSwKCSJsaXN0IjogW3sKCQkibmFtZSI6ICJkb3duIiwKCQkidGl0bGUiOiAi5LiL6L295a+56LSmIgoJfV0KfV0=";
		System.out.println(new String(MyBase64.decode(s.getBytes())));

		//System.out.println(new String(MyBase64.decode(menuStr)));
	}
}