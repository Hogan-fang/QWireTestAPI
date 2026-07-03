package com.qwireapi.mockapi.executor;

import com.qwireapi.mockapi.client.OrderApiClient;
import io.qwire.check.GenericCheckerExecutor;
import io.qwire.testcase.CaseDefine;
import io.qwire.testcase.CommonCaseDataProvider;
import io.qwire.context.ExecutionContext;
import org.testng.ITestContext;
import org.testng.annotations.Test;


public class PaymentCaseExecutor extends BaseApiCaseExecutor {

    private static final String SERVICE_NAME = "payment";

    private final OrderApiClient client = new OrderApiClient();

    @Test(dataProvider = CommonCaseDataProvider.PROVIDER_NAME, dataProviderClass = CommonCaseDataProvider.class)
    public void testPayment(CaseDefine caseDefine, ITestContext testContext) throws Exception {
        super.supports(text(caseDefine.getProfile().get("service")), testContext);
        ExecutionContext context = new ExecutionContext();
        context.putByPath("request", caseDefine.getData().getByPath("request"));

        client.pay(context);
        new GenericCheckerExecutor().execute(testContext, context, caseDefine);
        super.putServiceContext(testContext, SERVICE_NAME, context);

    }

}
