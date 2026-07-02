package com.qwireapi.assertion;

import io.qwire.assertion.UnifiedAssertionEvaluator;

public final class AssertionBootstrap {

    private AssertionBootstrap() {
    }

    public static void registerCustomAssertions() {
        UnifiedAssertionEvaluator.registerAssertionFunction(new StartsWithFunction());
        UnifiedAssertionEvaluator.registerAssertionFunction(new IsUuidV4Function());
    }
}
