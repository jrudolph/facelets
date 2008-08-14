/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    
    public String method() {
        return "Hello World!";
    }
}
