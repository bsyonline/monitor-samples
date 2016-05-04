package com.daas.cfg.task;

import com.daas.cfg.monitor.TaskExecutor;

/**
 * Created with IntelliJ IDEA.
 * User: rolex
 * Date: 2016/4/17
 * version: 1.0
 */
public class Push {

    String var;
    String taskId;

    public Push(String date, String taskId) {
        this.taskId = taskId;
        var = "CHANGE_PUSH " + date + "000000 rb_validated_change SCAN_BY_DATE " + taskId;
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
                        new TaskExecutor().exec("data_loader", var, "task", taskId);
                    }
                }).start();
    }

}
