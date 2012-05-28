package com.zuehlke.pgadmissions.interceptors;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;



public class DecryptionInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Map<String, String[]> paramters = request.getParameterMap();
		Set<Entry<String,String[]>> entrySet = paramters.entrySet();
		for (Entry<String, String[]> entry : entrySet) {
			String key = entry.getKey();
			String[] values = entry.getValue();
			int counter = 0; 
			for (String value : values) {
				if(!StringUtils.isBlank(value) && value.length() > 3 && value.startsWith("pfy_")){
					
					values[counter]= new EncryptionHelper().encrypt(value);
				}
			}
		
			//paramters.put(key, values);
		}
	
		return true;
	}

}
