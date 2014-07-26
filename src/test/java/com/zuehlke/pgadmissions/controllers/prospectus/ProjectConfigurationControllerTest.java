package com.zuehlke.pgadmissions.controllers.prospectus;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.inject.annotation.TestedObject;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ProjectConfigurationControllerTest {

    @TestedObject
    private ProjectConfigurationController controller;

    @Before
    public void setup() throws IOException {
        controller.customizeJsonSerializer();
    }

    @Test
    public void sampleTest() {
        Assert.fail();
    }

}
