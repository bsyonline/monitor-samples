package com.daas.cfg.task;


import com.daas.cfg.monitor.TaskExecutor;

/**
 * Created with IntelliJ IDEA.
 * User: rolex
 * Date: 2016/4/17
 * version: 1.0
 */
public class Change {

    String var;
    String host = "chinadaas11";
    String port = "8020";
    String taskId;

    public Change(String name, String date, String taskId) {
        this.taskId = taskId;
        var = name + " " + date + "000000 hdfs://" + host + ":" + port + "/hive1/user/hive/warehouse/enterprisebaseinfocollect_" + date + "_compare/ " + taskId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
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
