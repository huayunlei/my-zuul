package com.ihomefnt.sky.common.http;

import javax.servlet.ServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ihomefnt.sky.common.util.URLEncodeAndDecoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * 一句话功能简述
 * 功能详细描述
 *
 * @author jiangjun
 * @version 2.0, 2018-04-11 下午2:59
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class HttpHelper {

	private static Logger LOGGER = LoggerFactory.getLogger(HttpHelper.class);
	
    /**
     * 获取请求Body
     *
     * @param request
     * @return
     */
    public static String getBodyString(ServletRequest request) {
        InputStream inputStream = null;
		try {
			inputStream = request.getInputStream();
		} catch (IOException e) {
			LOGGER.error(" HttpHelper.getBodyString request.getInputStream() error ", e);
		}
		return getBodyStringByStream(inputStream);
    }

	public static String getBodyStringByStream(InputStream inputStream) {
		StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String line = "";
            while ((line = reader.readLine()) != null) {
            	LOGGER.error(" HttpHelper.getBodyStringByStream read InputStream origin {} ", line);
//            	sb.append(line);
            	
            	String u = URLEncodeAndDecoder.encodeAndDecoder(line);
            	if (u.endsWith("=")) {
            		sb.append(u.substring(0, u.length() - 1));
            	} else {
            		sb.append(u);
            	}
            }
        } catch (IOException e) {
        	LOGGER.error(" HttpHelper.getBodyStringByStream read InputStream error ", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                	LOGGER.error(" HttpHelper.getBodyStringByStream close InputStream error ", e);
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                	LOGGER.error(" HttpHelper.getBodyStringByStream close BufferedReader error ", e);
                }
            }
        }
        return sb.toString();
	}
}
