package com.zzg.mybatis.generator.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Owen on 6/18/16.
 */
public class StringUtilTest {

    @Test
    public void testDbStringToCamelStyle() {
        String result = MyStringUtils.dbStringToCamelStyle("person_address");
        Assert.assertEquals("PersonAddress", result);
    }

    @Test
    public void testDbStringToCamelStyle_case2() {
        String result = MyStringUtils.dbStringToCamelStyle("person_address_name");
        Assert.assertEquals("PersonAddressName", result);
    }

    @Test
    public void testDbStringToCamelStyle_case3() {
        String result = MyStringUtils.dbStringToCamelStyle("person_DB_name");
        Assert.assertEquals("PersonDBName", result);
    }

    @Test
    public void testDbStringToCamelStyle_case4() {
        String result = MyStringUtils.dbStringToCamelStyle("person_jobs_");
        Assert.assertEquals("PersonJobs", result);
    }

    @Test
    public void testDbStringToCamelStyle_case5() {
        String result = MyStringUtils.dbStringToCamelStyle("a");
        Assert.assertEquals("A", result);
    }

    @Test
    public void dbStringToCamelStyle() {
        String str = "AML_DEE_CV";
        if (str != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.valueOf(str.charAt(0)).toUpperCase());
            for (int i = 1; i < str.length(); i++) {
                char c = str.charAt(i);
                if (c != '_') {
                    sb.append(String.valueOf(c).toLowerCase());
                } else {
                    if (i + 1 < str.length()) {
                        sb.append(String.valueOf(str.charAt(i + 1)).toUpperCase());
                        i++;
                    }
                }
            }
            System.out.println(sb.toString());
        }
    }
}
