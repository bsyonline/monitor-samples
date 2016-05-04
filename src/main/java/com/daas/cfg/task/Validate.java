package com.daas.cfg.task;


import com.daas.cfg.monitor.TaskExecutor;

/**
 * Created with IntelliJ IDEA.
 * User: rolex
 * Date: 2016/4/17
 * version: 1.0
 */
public class Validate {

    String var;
    String taskId;

    public Validate(String date, String type, String taskId) {
        this.taskId = taskId;
        if ("VALIDATE_GS".equals(type)) {
            var = "VALIDATE " + date + "000000 rb_gs_change SCAN_BY_DATE " + taskId;
        } else {
            var = "VALIDATE " + date + "000000 rb_non_gs_change SCAN_BY_DATE " + taskId;
        }
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void execute() {

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        new TaskExecutor().exec("data_loader", var, "task_item", taskId);
                    }
                }).start();
    }

}
