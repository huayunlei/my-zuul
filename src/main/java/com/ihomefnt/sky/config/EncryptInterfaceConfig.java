package com.ihomefnt.sky.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.ihomefnt.sky.common.util.JsonUtils;
import com.ihomefnt.sky.common.util.URLEncodeAndDecoder;
import com.ihomefnt.sky.domain.dto.EncryptWhiteListDto;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Data
@Component
public class EncryptInterfaceConfig {

//	@NacosValue(value = "${encryptInterface}", autoRefreshed = true)
//	private Map<String, String> encryptInterface = new HashMap<String, String>();

	@NacosValue(value = "${encrypt.interface.white.lists}", autoRefreshed = true)
	private String encryptInterfaceWhiteLists;

	public List<EncryptWhiteListDto> getEncryptWhiteLists() {
		if (this.encryptInterfaceWhiteLists != null) {
			return JsonUtils.json2list(this.encryptInterfaceWhiteLists, EncryptWhiteListDto.class);
		}
		return null;
	}


	private boolean isContains (String zeusUrl) {
		List<EncryptWhiteListDto> list = this.getEncryptWhiteLists();
		if (!CollectionUtils.isEmpty(list)) {
			for (EncryptWhiteListDto dto : list) {
				if (zeusUrl.equals(dto.getName())) {
					return true;
				}
			}
		}

		return false;
//		return this.encryptInterface.containsKey(zeusUrl);
	}
	
	
	public String getUrl (String requestUrl) {
		String zeusUrl = null;
		try {
			zeusUrl = StringUtils.trim(URLEncodeAndDecoder.encodeAndDecoder(requestUrl));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (zeusUrl == null) {
			return "";
		}
		if (zeusUrl.endsWith("%20")) {
			zeusUrl = zeusUrl.substring(0, zeusUrl.indexOf("%20"));
		}
		zeusUrl = zeusUrl.substring(1).replace("/", ".");
		return zeusUrl;
	}
	
	
	private String getValue (String key) {
		List<EncryptWhiteListDto> list = this.getEncryptWhiteLists();
		if (!CollectionUtils.isEmpty(list)) {
			for (EncryptWhiteListDto dto : list) {
				if (key.equals(dto.getName())) {
					return dto.getVersion();
				}
			}
		}

		return null;
//		return this.encryptInterface.get(key);
	}
	
	public boolean isShouldFilter (String requestUrl, String appVersion) {
		String zeusUrl = this.getUrl(requestUrl);
		return this.isContains(zeusUrl) && appVersion.compareTo(this.getValue(zeusUrl)) >= 0;
		
	}
	

}
