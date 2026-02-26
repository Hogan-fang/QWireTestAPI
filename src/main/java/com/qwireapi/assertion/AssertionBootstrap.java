package com.qwireapi.assertion;

import io.qwire.check.UnifiedAssertionEvaluator;

public final class AssertionBootstrap {

    private AssertionBootstrap() {
    }

    public static void registerCustomAssertions() {
        UnifiedAssertionEvaluator.registerAssertionFunction(new StartsWithFunction());
        UnifiedAssertionEvaluator.registerAssertionFunction(new IsUuidV4Function());
    }
}
