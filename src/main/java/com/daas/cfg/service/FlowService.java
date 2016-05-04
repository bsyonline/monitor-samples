package com.daas.cfg.service;



import com.daas.cfg.beans.Flow;
import com.daas.cfg.beans.Task;
import com.daas.cfg.beans.TaskItem;
import com.daas.cfg.dao.FlowDao;
import com.daas.cfg.task.*;
import com.daas.cfg.util.Constants;
import com.daas.cfg.util.SpringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rolex on 16-4-21.
 */
@Service
public class FlowService {

    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    FlowDao flowDao;

    public FlowDao getFlowDao() {
        return flowDao;
    }

    public void setFlowDao(FlowDao flowDao) {
        this.flowDao = flowDao;
    }

    public String getBatchDate() {
        return batchDate;
    }

    public void setBatchDate(String batchDate) {
        this.batchDate = batchDate;
    }

    static String batchDate;

    public void execute(Task task) {
        if (getBatchDate() == null) {
            Flow flow = flowDao.findFlowById(task.getFlow().getId());
            String batchDate = flow.getDate();
            setBatchDate(batchDate);
        }
        switch (task.getTaskName()) {
            case "t-push":
                new Push(getBatchDate(), task.getTaskId()).execute();
                break;
            case "t-stat":
                new Stat(getBatchDate(), task.getTaskId()).execute();
                break;
            case "t-info":
                new Info(getBatchDate(), task.getTaskId()).execute();
                break;
            case "t-validate":
            case "t-change":
                List<TaskItem> list = flowDao.findAllTaskItem(task.getTaskId());
                if (list != null && list.size() > 0) {
                    for (TaskItem item : list) {
                        switch (item.getItemName()) {
                            case "FROST":
                                startTaskItem(item);
                                new Frost(getBatchDate(), item.getItemId()).execute();
                                break;
                            case "IMPAWN":
                                startTaskItem(item);
                                new Impawn(getBatchDate(), item.getItemId()).execute();
                                break;
                            case "XZCF":
                                startTaskItem(item);
                                new Xzcf(getBatchDate(), item.getItemId()).execute();
                                break;
                            case "DIS_SXBZXR":
                                startTaskItem(item);
                                new DisSxbzxr(getBatchDate(), item.getItemId()).execute();
                                break;
                            case "E_INV_INVESTMENT":
                                startTaskItem(item);
                                new Change("E_INV_INVESTMENT", getBatchDate(), item.getItemId()).execute();
                                break;
                            case "E_ENT_BASEINFO":
                                startTaskItem(item);
                                new Change("E_ENT_BASEINFO", getBatchDate(), item.getItemId()).execute();
                                break;
                            case "E_PRI_PERSON":
                                startTaskItem(item);
                                new Change("E_PRI_PERSON", getBatchDate(), item.getItemId()).execute();
                                break;
                            case "VALIDATE_GS":
                                startTaskItem(item);
                                new Validate(getBatchDate(), "VALIDATE_GS", item.getItemId()).execute();
                                break;
                            case "VALIDATE_NON_GS":
                                startTaskItem(item);
                                new Validate(getBatchDate(), "VALIDATE_NON_GS", item.getItemId()).execute();
                                break;

                        }

                    }
                } else {
                    completeTask(task);
                }
                break;
        }


    }

    public void completeFlow(Flow flow) {
        flowDao.completeFlow(flow.getId());
    }

    public void completeTask(Task task) {
        flowDao.completeTask(task.getFlow().getId(), task.getTaskId());
    }

    public void completeTaskItem(TaskItem item) {
        Task task = flowDao.findTaskById(item.getTask().getTaskId());
        flowDao.completeTaskItem(task.getFlow().getId(), task.getTaskId(), item.getItemId());
    }

    public void startFlow(Flow flow) {
        flowDao.startFlow(flow.getId());
    }

    public Flow findFlow(String id) {
        return flowDao.findFlowById(id);
    }

    public Task findTask(Task task) {
        return flowDao.findTaskById(task.getTaskId());
    }

    public Task findTask(String taskId) {
        return flowDao.findTaskById(taskId);
    }

    public TaskItem findTaskItem(String itemId) {
        return flowDao.findTaskItemById(itemId);
    }

    public Task startTask(Task task) {
        flowDao.startTask(task.getFlow().getId(), task.getTaskId());
        logger.info("task {} 已启动，下一个任务： {}......", task.getTaskName(), task.getNextTask() == null ? null : task.getNextTask().getTaskId());
        execute(task);
        return task;
    }

    public TaskItem startTaskItem(TaskItem taskItem) {
        flowDao.startTaskItem(taskItem.getTask().getTaskId(), taskItem.getItemId());
        logger.info("taskItem {} 已启动......", taskItem.getItemName());
        return taskItem;
    }

    public Task startNextTask(Task task) {
        Task nextTask = flowDao.startNextTask(task.getFlow().getId(), task.getTaskId());
        logger.info("task {} 已执行完成，启动下一个task {}.", nextTask.getTaskName(), nextTask.getNextTask().getTaskId());
        SpringUtil.getBean(FlowService.class).execute(nextTask);
        logger.info("task {} 已执行完成，启动下一个task {}.", nextTask.getTaskName(), nextTask.getTaskName());
        return nextTask;
    }

    public String getConfig(String key) {
        if (Constants.GLOBAL_CONFIGURATION_MAP.get(key) != null) {
            return Constants.GLOBAL_CONFIGURATION_MAP.get(key);
        } else {
            String value = flowDao.getConfig(key);
            if (value != null) {
                Constants.GLOBAL_CONFIGURATION_MAP.put(key, value);
            }
            return value;
        }
    }

    public void saveGlobal(Map<String,String> map){
        flowDao.saveGlobalCfg(map);
    }

    public void saveTaskItem(TaskItem item){
        flowDao.saveTaskItem(item);
    }
    public void saveTask(Task task){
        flowDao.saveTask(task);
    }
    public void saveFlow(Flow flow){
        flowDao.saveFlow(flow);
    }

    public List<Flow> findAllFlow(){
        return flowDao.findAllFlow();
    }

    public String findTaskAndItem(String flowId){
        List<Task> list = flowDao.findAllTask(flowId);
        StringBuffer sb = new StringBuffer("{");
        int count = 0;
        for (Task i : list) {
            List<TaskItem> items = flowDao.findAllTaskItem(i.getTaskId());
            String taskString = "\"{\\\"taskId\\\":\\\""+i.getTaskId()+"\\\",\\\"taskName\\\":\\\""+i.getTaskName()+"\\\",\\\"status\\\":\\\""+i.getStatus()+"\\\",\\\"nextTask\\\":\\\""+(i.getNextTask()==null?"":i.getNextTask().getTaskId())+"\\\",\\\"startTime\\\":\\\""+i.getStartTime()+"\\\",\\\"completeTime\\\":\\\""+i.getCompleteTime()+"\\\",\\\"cost\\\":\\\""+i.getCost()+"\\\"}\"";
            if(count > 0){
                sb.append(",");
            }
            if(items==null||items.size()==0){
                sb.append(taskString + ":" + new Gson().toJson(Lists.newArrayList()));
            }else{
                sb.append(taskString + ":" + new Gson().toJson(items));
            }
            count++;
        }
        sb.append("}");
        return sb.toString();
    }
}
