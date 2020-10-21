package com.ihomefnt.sky.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class URLEncodeAndDecoder {
	
	public static String encodeAndDecoder (String body) throws UnsupportedEncodingException {
		return URLDecoder.decode(URLEncoder.encode(body, "utf-8"), "utf-8");
	}


	public static String decoder (String body) throws UnsupportedEncodingException {
		return URLDecoder.decode(body, "utf-8");
	}

}
