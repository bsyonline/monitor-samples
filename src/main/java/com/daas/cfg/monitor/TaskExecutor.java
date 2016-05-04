package com.daas.cfg.monitor;


import com.daas.cfg.service.FlowService;
import com.daas.cfg.util.SpringUtil;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Created by rolex on 16-4-21.
 */
public class TaskExecutor {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger("TaskExecutor");
    String shellPath = "";

    public boolean exec(String name, String param, String type, String id) {
        FlowService flowService = SpringUtil.getBean(FlowService.class);
        shellPath = flowService.getConfig("shell.path");
        try {
            Process process = Runtime.getRuntime().exec(shellPath + name + ".sh " + param);
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            //读取标准输出流
            BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line=bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            //读取标准错误流
            BufferedReader brError = new BufferedReader(new InputStreamReader(process.getErrorStream(), "gb2312"));
            String errline = null;
            while ((errline = brError.readLine()) != null) {
                System.out.println(errline);
            }
            //waitFor()判断Process进程是否终止，通过返回值判断是否正常终止。0代表正常终止
            int c=process.waitFor();
            if(c!=0){
                logger.error("执行shell异常终止");
                return false;
            }

            if("task_item".equals(type)){
                flowService.completeTaskItem(flowService.findTaskItem(id));
            }else{
                flowService.completeTask(flowService.findTask(id));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

}
