/**
 * 
 */
package com.daas.cfg.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created with IntelliJ IDEA.
 * User: rolex
 * Date: 2016/1/13
 * version: 1.0
 */
public class SpringUtil {
    static ApplicationContext ac = null;

    public SpringUtil(ApplicationContext ac) {
        this.ac = ac;
    }

    public static <T> T getBean(Class<T> c) {
        return ac.getBean(c);
    }

    public static <T> T getBean(String name, Class<T> c) {
        return (T) ac.getBean(name);
    }

}
