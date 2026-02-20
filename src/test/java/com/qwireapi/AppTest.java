package com.qwireapi;

import org.testng.Assert;
import org.testng.annotations.Test;

public class AppTest {
    @Test
    public void shouldReturnProjectName() {
        App app = new App();
        Assert.assertEquals(app.getProjectName(), "QwireAPI");
    }
}
