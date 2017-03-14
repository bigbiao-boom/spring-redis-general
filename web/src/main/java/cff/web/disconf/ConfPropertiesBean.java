package cff.web.disconf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
* @author chenff
* @description 读取文件配置
* @date 2017年3月9日 下午3:26:44
* @version 1.0
 */
public class ConfPropertiesBean {

	static Logger logger = LoggerFactory.getLogger(ConfPropertiesBean.class);

	private static Properties pros;

	static {
		pros = new Properties();
		try {
			File config = getConfig();
			pros.load(new FileInputStream(config));
		} catch (IOException ex) {
			logger.warn(String.format("配置文件没有加载到！"));
			ex.printStackTrace();
		}
	}

	public static File getConfig() throws FileNotFoundException {
		URL classPath = ConfPropertiesBean.class.getClassLoader().getResource("");
		File file = new File(classPath.getPath(), "conf.properties");
		if (file.exists()) {
			return file;
		}
		String[] filePaths = { "../web/src/main/resources/conf.properties",
				"web/src/main/resources/conf.properties", "WEB-INF/classes/conf.properties",
				"src/main/resources/conf.properties", "../src/main/resources/conf.properties" };
		for (String filePath : filePaths) {
			file = new File(filePath);
			if (file.exists()) {
				return file;
			}
			logger.info("not found:" + file.getAbsolutePath());
		}

		throw new FileNotFoundException();
	}

	private static Properties getProps() throws IOException {
		File confPath = getConfig();
		Properties prop = new Properties();
		prop.load(new FileInputStream(confPath));
		return prop;
	}

	public static String getValue(String key, String defaultValue) {
		try {
			if (pros.containsKey(key)) {
				return pros.getProperty(key);
			} else {
				pros = getProps();
				if (pros.containsKey(key)) {
					return pros.getProperty(key);
				} else {
					return defaultValue;
				}
			}
		} catch (Exception e) {
			logger.warn("配置项获取失败！");
		}
		return defaultValue;
	}

	public static String getValue(String key) {
		try {
			if (pros.containsKey(key)) {
				return pros.getProperty(key);
			} else {
				pros = getProps();
				if (pros.containsKey(key)) {
					return pros.getProperty(key);
				} else {
					return null;
				}
			}
		} catch (Exception e) {
			logger.warn("配置项获取失败！");
		}
		return null;
	}

}
