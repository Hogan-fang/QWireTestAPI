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
