package cff.web.test;

public abstract class AbstractTest {

	static {
		String workDir = System.getProperty("user.dir") + "/src/main";
		System.setProperty("cfftest.home", workDir);
	}
	
}
