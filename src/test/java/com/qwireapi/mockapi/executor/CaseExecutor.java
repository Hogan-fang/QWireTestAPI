package com.qwireapi.mockapi.executor;

import org.testng.ITestContext;

public interface CaseExecutor {


    boolean supports(String serviceName, ITestContext testContext);


}
