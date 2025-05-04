package com.kayak.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Configuration
@PropertySource("classpath:application.properties")
public class ConfigurationManager {

    @Getter
    @Value("${base.url}")
    private String baseUrl;

    @Getter
    @Value("${browser}")
    private String browser;

    @Getter
    @Value("${timeout}")
    private int timeout;

    @Getter
    @Value("${remote.url:#{null}}")
    private String remoteUrl;

    @Getter
    @Value("${headless:false}")
    private boolean headless;

    @Value("${mobile.view}")
    private boolean mobileView;

    @Value("${mobile.device:iPhone 12}")
    private String mobileDevice;

//    public boolean isMobileView() {
//        return mobileView;
//    }

    public boolean isMobileView() {
        // First check system property
        String sysProp = System.getProperty("mobile.view");
        if (sysProp != null) {
            return Boolean.parseBoolean(sysProp);
        }
        // Fall back to application.properties
        return Boolean.parseBoolean(String.valueOf(mobileView));
    }

    public String getMobileDevice() {
        return mobileDevice;
    }
}