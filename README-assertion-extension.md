# QwireTestAPI: Customized Assertion Function Extension Guide

This document explains how to extend assertion functions used by `QwireCore` unified assertions, so API projects can add custom checks like `$startsWith(...)`, `$isUuidV4()`, `$isBusinessDay(...)`.

## 1) Prerequisites

- API project can access `QwireCore` classes on classpath.
- Core extension SPI already exists:
  - `io.qwire.check.AssertionFunction`
  - `io.qwire.check.UnifiedAssertionEvaluator`

## 2) Implement a custom assertion function

Create a class that implements `io.qwire.check.AssertionFunction`.

```java
package com.qwireapi.assertion;

import io.qwire.check.AssertionFunction;
import io.qwire.framework.api.RequestManager;
import io.qwire.testcase.CaseData;

public class StartsWithFunction implements AssertionFunction {

    @Override
    public String getName() {
        return "startsWith";
    }

    @Override
    public boolean matches(Object actual, String args, RequestManager request, CaseData caseData) {
        if (actual == null || args == null) {
            return false;
        }
        return String.valueOf(actual).startsWith(args.trim());
    }

    @Override
    public String renderExpected(String args) {
        return "$startsWith(" + (args == null ? "" : args) + ")";
    }
}
```

## 3) Register the function (choose one)

The sample files are already prepared in this project:

- `src/main/java/com/qwireapi/assertion/StartsWithFunction.java`
- `src/main/java/com/qwireapi/assertion/IsUuidV4Function.java`
- `src/main/java/com/qwireapi/assertion/AssertionBootstrap.java`
- `src/main/resources/META-INF/services/io.qwire.check.AssertionFunction`

### Option A: Runtime registration (recommended for explicit bootstrap)

Call registration once during API test/app startup:

```java
import io.qwire.check.UnifiedAssertionEvaluator;
import com.qwireapi.assertion.StartsWithFunction;
import com.qwireapi.assertion.IsUuidV4Function;

public class AssertionBootstrap {
    public static void init() {
        UnifiedAssertionEvaluator.registerAssertionFunction(new StartsWithFunction());
        UnifiedAssertionEvaluator.registerAssertionFunction(new IsUuidV4Function());
    }
}
```

### Option B: ServiceLoader auto-discovery

1. Create file:

`src/main/resources/META-INF/services/io.qwire.check.AssertionFunction`

2. Add implementation class full name (one per line):

```text
com.qwireapi.assertion.StartsWithFunction
com.qwireapi.assertion.IsUuidV4Function
```

3. If needed, trigger reload after classpath/resource ready:

```java
import io.qwire.check.UnifiedAssertionEvaluator;

UnifiedAssertionEvaluator.reloadAssertionFunctions();
```

## 4) Use in check YAML

After registration, use custom function directly in assertion expression:

```yaml
check:
  response:
    result.traceId: "$startsWith(TRC-)"
        result.requestId: "$isUuidV4()"
```

## 5) Behavior and fallback rules

- Built-in functions (`$ref`, `$regex`, `$gt`, `$in`, etc.) still work as before.
- Unknown `$func(...)` handling:
  - If a custom function with the same name is registered, it is executed.
  - If not registered, expression falls back to literal comparison.
- Function names are case-insensitive during registration/lookup.

## 6) Best practices

- Keep `matches(...)` pure and deterministic (no side effects).
- Return fast and provide clear `renderExpected(...)` for better failure report readability.
- Prefer runtime registration in test bootstrap for predictable loading order.
- Use ServiceLoader for plugin-like packaging across multiple API modules.

## 7) Quick smoke test suggestion

- Positive case: actual value satisfies custom function.
- Negative case: actual value does not satisfy function and report includes expected expression.
- Missing registration case: verify fallback behavior is understood by team.
