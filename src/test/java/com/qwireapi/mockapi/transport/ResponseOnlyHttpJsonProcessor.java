package com.qwireapi.mockapi.transport;

import io.qwire.context.ExecutionContext;
import io.qwire.transport.HttpJsonProcessor;
import io.qwire.transport.TransportProcessor;
import io.qwire.transport.TransportRequest;
import io.qwire.transport.TransportResponse;

public class ResponseOnlyHttpJsonProcessor implements TransportProcessor {

    private final HttpJsonProcessor delegate = new HttpJsonProcessor();

    @Override
    public void before(ExecutionContext context, TransportRequest request) {
        // RequestSpec is prepared by API clients.
    }

    @Override
    public void after(ExecutionContext context, TransportResponse response) {
        delegate.after(context, response);
    }
}
