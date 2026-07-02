package com.qwireapi.mockapi.executer;

import io.qwire.check.GenericCheckerExecutor;
import io.qwire.runtime.config.RuntimeProfile;
import io.qwire.runtime.config.RuntimeProfileContext;
import io.qwire.runtime.config.RuntimeProfileProcessor;
import io.qwire.testcase.CommonCaseDataProvider;
import io.qwire.testcase.CaseCheck;
import io.qwire.testcase.CaseDefine;
import io.qwire.context.ExecutionContext;
import io.qwire.context.WorkflowContext;

import org.testng.ITestContext;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import java.util.LinkedHashMap;
import java.util.Map;

public class BaseApiCaseExecuter implements CaseExecuter {
    
    @BeforeSuite
    public void beforeSuite() {
        if (!RuntimeProfileContext.getAll().isEmpty()) {
            return;
        }
        RuntimeProfile profile = new RuntimeProfileProcessor().loadFromSystemProperty();
        RuntimeProfileContext.bind(profile);
    }

    protected String text(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    protected Map<String, Object> toStringObjectMap(Object source) {
        if (!(source instanceof Map<?, ?> map)) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> converted = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            converted.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return converted;
    }

    protected void putServiceContext(ITestContext testContext, String serviceName, ExecutionContext context) {
        if (testContext != null) {
            Object existing = testContext.getAttribute(WorkflowContext.ATTRIBUTE_KEY);
            Map<String, ExecutionContext> serviceContexts;
            if (existing instanceof Map<?, ?> map) {
                serviceContexts = new LinkedHashMap<>();
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    serviceContexts.put(String.valueOf(entry.getKey()), (ExecutionContext) entry.getValue());
                }
            } else {
                serviceContexts = new LinkedHashMap<>();
            }
            serviceContexts.put(serviceName, context);
            testContext.setAttribute(WorkflowContext.ATTRIBUTE_KEY, serviceContexts);
        }
    }

    @Override
    public boolean supports(String serviceName, ITestContext testContext) {
        if (serviceName == null || serviceName.isBlank()) {
            throw new IllegalArgumentException("service in case definition profile part must not be blank");
        }
        String resolvedServiceName = resolveServiceName();
        if (resolvedServiceName == null || resolvedServiceName.isBlank()) {
            throw new IllegalStateException("Executer class must declare static SERVICE_NAME");
        }
        if (!resolvedServiceName.equalsIgnoreCase(serviceName.trim())) {
            String caseDefineFile = resolveCaseDefineFile(testContext);
            throw new IllegalArgumentException(
                    "Unsupported service: '" + serviceName + "', expected: '" + resolvedServiceName
                            + "', caseDefine: '" + caseDefineFile + "'");
        }
        return true;
    }

    protected String resolveCaseDefineFile(ITestContext testContext) {
        if (testContext != null) {
            Object loadedCaseFile = testContext.getAttribute(CommonCaseDataProvider.LOADED_CASE_FILES_ATTRIBUTE);
            if (loadedCaseFile != null) {
                String value = String.valueOf(loadedCaseFile).trim();
                if (!value.isEmpty()) {
                    return value;
                }
            }
        }

        String fromSystem = System.getProperty(CommonCaseDataProvider.PARAM_CASE_DEFINE);
        if (fromSystem != null && !fromSystem.trim().isEmpty()) {
            return fromSystem.trim();
        }
        return "unknown";
    }

    protected String resolveServiceName() {
        try {
            var field = this.getClass().getDeclaredField("SERVICE_NAME");
            field.setAccessible(true);
            Object value = field.get(null);
            return value == null ? null : String.valueOf(value).trim();
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            return null;
        }
    }
}