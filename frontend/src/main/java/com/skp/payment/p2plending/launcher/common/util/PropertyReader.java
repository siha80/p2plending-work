/**
 * @(#)PropertyReader.java
 *
 * @version 1.0  2011. 4. 5. 
 * @author ohchanggyu
 * 
 * Copyright 2000-2011 by FEELingk, Inc. All rights reserved.
 */
package com.skp.payment.p2plending.launcher.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * PropertiesConfiguration 은 업무 개발시 직접 객체를 생성하지 않는다.<br>
 * 반드시 아래와 같은 방법으로 사용한다.<br>
 * <b>String hc = PropertyReader.getValue("test.sample.data")</b><br>
 * 
 * System.getenv 는 deprecated 되었으므로 getProperty 사용해야 한다
 * 
 * 
 * @version 0.1
 */
public class PropertyReader {
	private static Logger logger = LoggerFactory.getLogger(PropertyReader.class);

	private static final Map<String, Properties> propertiesMap = new ConcurrentHashMap<String, Properties>();
	private final static String DEFAULT_CONF_FILE = "application.properties";

	static {
		loadProperty(DEFAULT_CONF_FILE);
	}

	public static void loadProperty(String propertyFile) {
		synchronized (propertiesMap) {
			InputStream is = null;
			try {
				is = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertyFile);
				Properties properties = new Properties();
				properties.load(is);

				propertiesMap.put(propertyFile, properties);

				logger.info("{} is loaded", propertyFile);
			} catch (Exception e) {
				logger.warn("{} is load exception ", propertyFile);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (Exception e) {
					}
				}
			}
		}
	}

	public static String getValue(String key) {
		return propertiesMap.get(DEFAULT_CONF_FILE).get(key).toString();
	}
}
