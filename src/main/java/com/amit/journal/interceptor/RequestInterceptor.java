package com.amit.journal.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amit.journal.constants.Constants;
import com.amit.journal.util.CommonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestInterceptor implements HandlerInterceptor {
	private static final Logger LOG = LogManager.getLogger(RequestInterceptor.class);
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String userId = request.getHeader(Constants.USERID_HEADER);
		if (CommonUtil.isNullOrEmpty(userId)) {
			LOG.warn("@@@@@@@@@@@@@@@ userId is null in the request. ");
		}
		LOG.info(" set userId : {}", userId);
		UserContext.setUserId(userId);

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, OPTIONS");
		return true;
	}

}
