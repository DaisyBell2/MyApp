package com.daisybell.myapp.test;

import android.os.Parcelable;

import com.daisybell.myapp.Constant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Test implements Serializable {
    public String nameTest, question, option1, option2, option3, option4, rightAnswer;
    public int id;
    public int position;

    public Test() {
    }

    public Test(int id, String nameTest, String question, String option1, String option2, String option3,
                String option4, String rightAnswer) {
        this.id = id;
        this.nameTest = nameTest;
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.rightAnswer = rightAnswer;
    }

    public Map<String, Object> toMap() {
        TreeMap<String, Object> result = new TreeMap<>();
        result.put("id" + "_"+Constant.TEST_SIZE+"."+Constant.QUEST_SIZE, id);
        result.put("nameTest" + "_"+Constant.TEST_SIZE+"."+Constant.QUEST_SIZE, nameTest);
        result.put("question" + "_"+Constant.TEST_SIZE+"."+Constant.QUEST_SIZE, question);
        result.put("option1" + "_"+Constant.TEST_SIZE+"."+Constant.QUEST_SIZE, option1);
        result.put("option2" + "_"+Constant.TEST_SIZE+"."+Constant.QUEST_SIZE, option2);
        result.put("option3" + "_"+Constant.TEST_SIZE+"."+Constant.QUEST_SIZE, option3);
        result.put("option4" + "_"+Constant.TEST_SIZE+"."+Constant.QUEST_SIZE, option4);
        result.put("rightAnswer" + "_"+Constant.TEST_SIZE+"."+Constant.QUEST_SIZE, rightAnswer);

        return result;
    }
    public Map<String, Object> toMapQuestion() {
        TreeMap<String, Object> result = new TreeMap<>();
        result.put("question" + "_"+Constant.TEST_SIZE+"."+Constant.QUEST_SIZE, question);

        return result;
    }
    public Map<String, Object> toMapAllOption() {
        TreeMap<String, Object> result = new TreeMap<>();
        result.put("opti1on" + "_"+Constant.TEST_SIZE+"."+Constant.QUEST_SIZE, option1);
        result.put("opti2on" + "_"+Constant.TEST_SIZE+"."+Constant.QUEST_SIZE, option2);
        result.put("opti3on" + "_"+Constant.TEST_SIZE+"."+Constant.QUEST_SIZE, option3);
        result.put("opti4on" + "_"+Constant.TEST_SIZE+"."+Constant.QUEST_SIZE, option4);

        return result;
    }
    public Map<String, Object> toMapRightAnswer() {
        TreeMap<String, Object> result = new TreeMap<>();
        result.put("rightAnswer" + "_"+Constant.TEST_SIZE+"."+Constant.QUEST_SIZE, rightAnswer);

        return result;
    }
//    public Map<String, Object> toMapTestKey() {
//        TreeMap<String, Object> result = new TreeMap<>();
//        result.put("testKey" + "("+Constant.INDEX_QUEST_MAP_TEST+")", testKey);
//
//        return result;
//    }


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getNameTest() {
        return nameTest;
    }

    public void setNameTest(String nameTest) {
        this.nameTest = nameTest;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public void setRightAnswer(String rightAnswer) {
        this.rightAnswer = rightAnswer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
