package com.qwireapi.mockapi.runtime;

import io.qwire.runtime.config.RuntimeProfile;
import io.qwire.runtime.config.RuntimeProfileContext;
import io.qwire.runtime.config.RuntimeProfileProcessor;
import org.testng.ITestContext;

import java.util.LinkedHashMap;
import java.util.Map;

public final class MockApiRuntimeBinder {

    private static final String SYSTEM_RUNTIME = "runtime";
    private static final String SYSTEM_QWIRE_RUNTIME = "qWire.runtime";
    private static final String SYSTEM_QWIRE_RUNTIME_LOWER = "qwire.runtime";
    private static final String PARAM_RUNTIME = "qWire.runtime";
    private static final String PARAM_RUNTIME_LOWER = "qwire.runtime";

    private MockApiRuntimeBinder() {
    }

    public static Map<String, Object> bindFromConfigFile(ITestContext testContext) {
        String runtimeName = resolveRuntimeName(testContext);
        RuntimeProfile profile = new RuntimeProfileProcessor().load(runtimeName);

        Map<String, Object> runtimeProfile = new LinkedHashMap<>();
        runtimeProfile.put("profileName", profile.getProfileName());
        runtimeProfile.put("runtime", profile.getRuntime());
        runtimeProfile.put("execution", profile.getExecution());
        runtimeProfile.put("env", profile.getEnv());
        runtimeProfile.put("resources", profile.getResources());

        RuntimeProfileContext.bind(runtimeProfile);
        if (testContext != null) {
            testContext.setAttribute(RuntimeProfileContext.ATTRIBUTE_KEY, runtimeProfile);
        }
        return runtimeProfile;
    }

    public static void clear() {
        RuntimeProfileContext.remove();
    }

    private static String resolveRuntimeName(ITestContext testContext) {
        String fromSystem = firstNonBlank(
                System.getProperty(SYSTEM_RUNTIME),
                System.getProperty(SYSTEM_QWIRE_RUNTIME),
                System.getProperty(SYSTEM_QWIRE_RUNTIME_LOWER));
        if (!fromSystem.isBlank()) {
            return fromSystem;
        }

        String fromXml = readXmlParameter(testContext, PARAM_RUNTIME);
        if (!fromXml.isBlank()) {
            return fromXml;
        }

        String fromXmlLower = readXmlParameter(testContext, PARAM_RUNTIME_LOWER);
        if (!fromXmlLower.isBlank()) {
            return fromXmlLower;
        }

        return "";
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "";
    }

    private static String readXmlParameter(ITestContext testContext, String key) {
        if (testContext == null || testContext.getCurrentXmlTest() == null) {
            return "";
        }
        String value = testContext.getCurrentXmlTest().getParameter(key);
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.trim();
    }
}
