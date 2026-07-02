package com.qwireapi.mockapi.client;

import io.qwire.transport.HttpMethod;
import io.qwire.context.ExecutionContext;

import java.util.Set;

public class OrderApiClient extends AbstractMockApiClient {

    private static final String ENDPOINT = "/order";


    public OrderApiClient() {
    }

    public void pay(ExecutionContext context) {
        super.execute(ENDPOINT, HttpMethod.POST, context);
    }

    public void query(ExecutionContext context) {
        super.execute(ENDPOINT, HttpMethod.GET, context);
    }

}
