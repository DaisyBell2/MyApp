package com.daisybell.myapp.test;

import java.io.Serializable;

public class SaveResultTests implements Serializable {
    public String fullName, nameTest, resultTest, dateTest, startTimeTest, finishTimeTest;

    public SaveResultTests() {
    }

    public SaveResultTests(String fullName, String nameTest, String resultTest, String dateTest, String startTimeTest, String finishTimeTest) {
        this.fullName = fullName;
        this.nameTest = nameTest;
        this.resultTest = resultTest;
        this.dateTest = dateTest;
        this.startTimeTest = startTimeTest;
        this.finishTimeTest = finishTimeTest;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNameTest() {
        return nameTest;
    }

    public void setNameTest(String nameTest) {
        this.nameTest = nameTest;
    }

    public String getResultTest() {
        return resultTest;
    }

    public void setResultTest(String resultTest) {
        this.resultTest = resultTest;
    }

    public String getDateTest() {
        return dateTest;
    }

    public void setDateTest(String dateTest) {
        this.dateTest = dateTest;
    }

    public String getStartTimeTest() {
        return startTimeTest;
    }

    public void setStartTimeTest(String startTimeTest) {
        this.startTimeTest = startTimeTest;
    }

    public String getFinishTimeTest() {
        return finishTimeTest;
    }

    public void setFinishTimeTest(String finishTimeTest) {
        this.finishTimeTest = finishTimeTest;
    }
}
