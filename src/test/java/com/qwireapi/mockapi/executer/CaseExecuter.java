package com.qwireapi.mockapi.executer;

import org.testng.ITestContext;

public interface CaseExecuter {


    boolean supports(String serviceName, ITestContext testContext);


}
