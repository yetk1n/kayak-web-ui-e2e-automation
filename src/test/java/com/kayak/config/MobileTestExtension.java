package com.kayak.config;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class MobileTestExtension implements BeforeAllCallback {
    @Override
    public void beforeAll(ExtensionContext context) {
        boolean isMobileTest = context.getRequiredTestClass().isAnnotationPresent(MobileTest.class);
        System.setProperty("mobile.view", String.valueOf(isMobileTest));
    }
}