package com.agrohackathon.utils;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

//import org.apache.commons.lang.StringUtils;

public class ParseGET {

	public String parseURL(HttpServletRequest request)
	{
		Enumeration<String> params=request.getParameterNames();
		String param="", param_value="";
		
		String title="";
		
		while(params.hasMoreElements())
		{
			param=params.nextElement();
			param_value=request.getParameter(param);
			
			if(param.equalsIgnoreCase("url"))
			{
				//title=StringUtils.trim(param_value);
				return param_value;
			}
		}
		
		return "";
	}

	public String parseSource(HttpServletRequest request)
	{
		Enumeration<String> params=request.getParameterNames();
		String param="", param_value="";
		
		String title="";
		
		while(params.hasMoreElements())
		{
			param=params.nextElement();
			param_value=request.getParameter(param);
			
			if(param.equalsIgnoreCase("source"))
			{
				//title=StringUtils.trim(param_value);
				return param_value;
			}
		}
		
		return "";
	}
	
}
