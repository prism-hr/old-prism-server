package com.zuehlke.pgadmissions.cucumber;


public class WebDriverProvider {

	private static WebDriverProvider instance;

	private WebDriver webdriver;
	private WebDriverProvider() {
		System.setProperty("webdriver.chrome.driver", "C:\\chromedriver\\chromedriver.exe");
		webdriver = new ChromeDriver();
	}

	public static synchronized WebDriverProvider getInstance() {
		if(instance == null){
			instance = new WebDriverProvider();
		}
		return instance;
	}

	public WebDriver getWebdriver() {
		return webdriver;
	}
}
