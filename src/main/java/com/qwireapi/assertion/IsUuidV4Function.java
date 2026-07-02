package com.qwireapi.assertion;

import java.util.regex.Pattern;

import io.qwire.assertion.IAssertionFunction;

public class IsUuidV4Function implements IAssertionFunction {

    private static final Pattern UUID_V4_PATTERN = Pattern
            .compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$");

    @Override
    public String getName() {
        return "isUuidV4";
    }

    @Override
    public boolean matches(Object actual, Object[] args) {
        if (actual == null) {
            return false;
        }
        String value = String.valueOf(actual).trim();
        return UUID_V4_PATTERN.matcher(value).matches();
    }

    @Override
    public String renderExpected() {
        return "$isUuidV4()";
    }
}
