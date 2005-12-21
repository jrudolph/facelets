package com.sun.facelets.tag.jsf.html;

import java.util.logging.Logger;

public class TestBean {
    private static final Logger log = Logger.getLogger(TestBean. class.getName());
    private int integerVal = 10;
    public int getIntegerVal() {
        log.info("getIngerVal called");
        return integerVal;
    }
    public void setIntegerVal(int integerVal) {
        log.info("setIngerVal called");
        this.integerVal = integerVal;
    }
    public String testAction(){
        log.info("test action called ");
        return "runFasta";
    }
}
