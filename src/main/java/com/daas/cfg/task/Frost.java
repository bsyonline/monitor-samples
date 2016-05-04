package com.daas.cfg.task;


import com.daas.cfg.monitor.TaskExecutor;

/**
 * Created with IntelliJ IDEA.
 * User: rolex
 * Date: 2016/4/17
 * version: 1.0
 */
public class Frost {

    String var;
    String taskId;

    public Frost(String date, String taskId) {
        this.taskId = taskId;
        var = "FROST " + date + "000000 frost_pripid " + taskId;
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
