package com.qwireapi.mockapi.client;

import com.qwireapi.mockapi.transport.ResponseOnlyHttpJsonProcessor;
import com.qwireapi.mockapi.transport.SimulatedCryptoProcessor;
import io.qwire.context.ExecutionContext;
import io.qwire.transport.HttpMethod;
import io.qwire.transport.HttpJsonProcessor;
import io.qwire.transport.HttpRequestSpec;
import io.qwire.transport.TransportProxy;
import io.qwire.transport.TransportRequest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractMockApiClient {

    private final String connectionName="orderApi";
    private final TransportProxy transportProxy;

    protected AbstractMockApiClient() {
        this.transportProxy = new TransportProxy();
        this.transportProxy.registerProcessor(new HttpJsonProcessor());
        this.transportProxy.registerProcessor(new SimulatedCryptoProcessor());
        this.transportProxy.registerProcessor(new ResponseOnlyHttpJsonProcessor());
    }

    protected void execute(String uri, HttpMethod method, ExecutionContext context) {
        if (context == null) {
            throw new IllegalArgumentException("ExecutionContext must not be null");
        }

        context.put("connectionName", connectionName);
        context.putByPath("transport.request.uri", uri);
        context.putByPath("transport.request.method", method);
        if (context.getByPath("transport.request.payload") == null) {
            context.putByPath("transport.request.payload", context.getByPath("request"));
        }

        try {
            transportProxy.execute(context);
        } catch (Exception exception) {
            throw new RuntimeException("Mock API transport execution failed: " + connectionName, exception);
        }
    }

    protected Set<String> supportedActions() {
        return Set.of();
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
}
