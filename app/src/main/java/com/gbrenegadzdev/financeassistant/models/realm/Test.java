package com.gbrenegadzdev.financeassistant.models.realm;

import io.realm.RealmObject;

public class Test extends RealmObject {
    public static final String TEST_ID = "testId";
    public static final String TEST_NAME = "testName";

    private String testId;
    private String testName;

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    @Override
    public String toString() {
        return "Test{" +
                "testId='" + testId + '\'' +
                ", testName='" + testName + '\'' +
                '}';
    }
}
