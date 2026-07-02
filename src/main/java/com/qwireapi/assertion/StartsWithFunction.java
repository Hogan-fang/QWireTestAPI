package com.qwireapi.assertion;

import io.qwire.assertion.IAssertionFunction;

public class StartsWithFunction implements IAssertionFunction {

    @Override
    public String getName() {
        return "startsWith";
    }

    @Override
    public boolean matches(Object actual, Object[] args) {
        if (actual == null || args == null || args.length == 0 || args[0] == null) {
            return false;
        }
        return String.valueOf(actual).startsWith(String.valueOf(args[0]).trim());
    }

    @Override
    public String renderExpected() {
        return "$startsWith(prefix)";
    }
}
