package com.qwireapi.mockapi.transport;

import io.qwire.context.ExecutionContext;
import io.qwire.runtime.config.RuntimeProfileContext;
import io.qwire.transport.HttpRequestSpec;
import io.qwire.transport.TransportProcessor;
import io.qwire.transport.TransportRequest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class SimulatedCryptoProcessor implements TransportProcessor {

    @Override
    public void before(ExecutionContext context, TransportRequest request) {
        if (!(request.requestSpec() instanceof HttpRequestSpec spec)) {
            return;
        }

        Map<String, Object> headers = new LinkedHashMap<>();
        headers.putAll(toMap(spec.headers()));

        String merchantId = stringValue(RuntimeProfileContext.get("mid"));
        String reference = stringValue(context.getByPath("request.reference")  );

        String signatureKey = stringValue(RuntimeProfileContext.get("signatureKey"));

        String signature = buildSignature(signatureKey, merchantId, reference);
        headers.put("X-Mock-Signature", signature);
        headers.putIfAbsent("X-QWire-Simulated-Crypto", "enabled");

        HttpRequestSpec rebuilt = HttpRequestSpec.builder()
                .method(spec.method())
                .url(spec.url())
                .headers(headers)
                .queryParams(toMap(spec.queryParams()))
                .payload(spec.payload())
                .build();

        request.setRequestSpec(rebuilt);
        context.putByPath("transport.request.crypto.simulated", true);
    }

    private String buildSignature(String signatureKey, String merchantId, String reference) {
        String source = signatureKey + "|" + merchantId + "|" + reference;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(source.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception exception) {
            return "sig-unavailable";
        }
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private Map<String, Object> toMap(Object value) {
        if (!(value instanceof Map<?, ?> source)) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> out = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            out.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return out;
    }
}
