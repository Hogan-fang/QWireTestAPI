package com.qwireapi.assertion;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.qwire.check.UnifiedAssertionEvaluator;

public class IsUuidV4FunctionTest {

    @BeforeClass
    public void registerCustomFunctions() {
        AssertionBootstrap.registerCustomAssertions();
    }

    @Test
    public void shouldPassWhenValueIsUuidV4() {
        Map<String, Object> actual = new HashMap<>();
        actual.put("requestId", "7b8f7f52-4f85-4d13-9680-c3702f4a124d");

        Map<String, Object> expected = new HashMap<>();
        expected.put("requestId", "$isUuidV4()");

        UnifiedAssertionEvaluator.AssertionResult result = UnifiedAssertionEvaluator.evaluateMap(
                "response",
                actual,
                expected,
                null,
                null);

        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void shouldFailWhenValueIsNotUuidV4() {
        Map<String, Object> actual = new HashMap<>();
        actual.put("requestId", "550e8400-e29b-11d4-a716-446655440000");

        Map<String, Object> expected = new HashMap<>();
        expected.put("requestId", "$isUuidV4()");

        UnifiedAssertionEvaluator.AssertionResult result = UnifiedAssertionEvaluator.evaluateMap(
                "response",
                actual,
                expected,
                null,
                null);

        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(result.toReadableReport().contains("$isUuidV4()"));
    }

    @Test
    public void shouldFailWhenValueIsMalformedUuid() {
        Map<String, Object> actual = new HashMap<>();
        actual.put("requestId", "not-a-uuid");

        Map<String, Object> expected = new HashMap<>();
        expected.put("requestId", "$isUuidV4()");

        UnifiedAssertionEvaluator.AssertionResult result = UnifiedAssertionEvaluator.evaluateMap(
                "response",
                actual,
                expected,
                null,
                null);

        Assert.assertFalse(result.isSuccess());
    }
}
