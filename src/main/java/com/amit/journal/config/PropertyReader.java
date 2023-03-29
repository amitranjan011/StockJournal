package com.amit.journal.config;

import com.amit.journal.util.CommonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@EnableScheduling
@Order(1)
public class PropertyReader implements InitializingBean {
	private static final Logger LOG = LogManager.getLogger(PropertyReader.class);
	private static PropertyReader instance;
	@Autowired
	private Environment environment;

	@PostConstruct
	public void initialize() throws Exception {
		LOG.info("******************** Initialized*******************");
		PropertyReader.instance = this;
	}

	public static PropertyReader getInstance() {
		return instance;
	}

	private String getPropertyVal(String propertyKey) {
		try {
			return environment.getProperty(propertyKey);
		} catch (Exception e) {
			LOG.error("error getting value for property : " + propertyKey + " from external propSource, exception : {} ", CommonUtil.getStackTrace(e));
		}
		return "";
	}

	public static String getProperty(String propertyKey) {
		return instance.getPropertyVal(propertyKey);
	}
	public static String getProperty(String propertyKey, String defaultValue) {
		try {
			String value = getProperty(propertyKey);
			if(CommonUtil.isNullOrEmpty(value)) {
				return defaultValue;
			} else return value;
		} catch (Exception e) {
			LOG.error("error getting value for property : {} ", propertyKey);
			return "";
		}
	}

	public Environment getEnvironment() {
		return environment;
	}


	@Override
	public void afterPropertiesSet() throws Exception {

	}
}