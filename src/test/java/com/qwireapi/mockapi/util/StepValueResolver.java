package com.qwireapi.mockapi.util;

import io.qwire.context.ExecutionContext;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StepValueResolver {

    public Object resolveValue(Object value, ExecutionContext context) {
        if (value instanceof String text) {
            return resolveString(text, context);
        }
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> resolved = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                resolved.put(String.valueOf(entry.getKey()), resolveValue(entry.getValue(), context));
            }
            return resolved;
        }
        if (value instanceof List<?> list) {
            List<Object> resolved = new ArrayList<>();
            for (Object item : list) {
                resolved.add(resolveValue(item, context));
            }
            return resolved;
        }
        return value;
    }

    public String resolveString(String text, ExecutionContext context) {
        String resolved = text.replace("{{runId}}", String.valueOf(context.get("runId")));
        int guard = 0;
        while (resolved.contains("{{") && resolved.contains("}}") && guard++ < 30) {
            int start = resolved.indexOf("{{");
            int end = resolved.indexOf("}}", start);
            if (end < 0) {
                break;
            }
            String path = resolved.substring(start + 2, end).trim();
            Object replacement = context.getByPath(path);
            resolved = resolved.substring(0, start)
                    + (replacement == null ? "" : String.valueOf(replacement))
                    + resolved.substring(end + 2);
        }
        return resolved;
    }
}
