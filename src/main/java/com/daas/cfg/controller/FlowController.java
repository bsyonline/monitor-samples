package com.daas.cfg.controller;

import com.daas.cfg.beans.Flow;
import com.daas.cfg.beans.Task;
import com.daas.cfg.beans.TaskItem;
import com.daas.cfg.monitor.FlowMonitor;
import com.daas.cfg.service.FlowService;
import com.daas.cfg.util.SpringUtil;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Created by rolex on 16-4-26.
 */
@Controller
@EnableAutoConfiguration
@ImportResource("beans.xml")
public class FlowController {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    FlowService flowService;

    @RequestMapping(value = "/global", method = RequestMethod.POST)
    @ResponseBody
    public String global(String arg0) {
        try {
            Gson gson = new Gson();
            Map map = gson.fromJson(arg0, Map.class);
            flowService.saveGlobal(map);
        } catch (Exception e) {
            logger.info("global configuration save failed.", e);
            return "false";
        }
        return "true";
    }
    @RequestMapping(value = "/cfg", method = RequestMethod.POST)
    @ResponseBody
    public String cfg(String flowName, String flowDate, String flowCfg) {
        String flowId = UUID.randomUUID().toString();
        try {
            Flow flow = new Flow();
            Task[] tasks = null;
            flow.setId(flowId);
            flow.setStatus(0);
            flow.setDate(flowDate);
            flow.setName(flowName);
            if (flowCfg != null && !"".equals(flowCfg)) {
                String[] str = flowCfg.split(",");

                int flowStep = str.length;
                tasks = new Task[flowStep];

                for (int i = flowStep - 1; i >= 0; i--) {
                    if (str[i].contains("[")) {
                        String sub = str[i].substring(str[i].indexOf("[") + 1, str[i].indexOf("]"));
                        Task task = new Task();
                        task.setFlow(flow);
                        task.setTaskId(UUID.randomUUID().toString());
                        task.setTaskName(str[i].substring(0,str[i].indexOf("[")));
                        task.setOrder(i+1);
                        task.setStatus(0);
                        if (i < flowStep - 1) {
                            task.setNextTask(tasks[i + 1]);
                        }
                        tasks[i] = task;
                        for (String s : sub.split("\\|")) {
                            TaskItem item = new TaskItem();
                            item.setItemId(UUID.randomUUID().toString());
                            item.setItemName(s);
                            item.setStatus(0);
                            item.setTask(task);
                            flowService.saveTaskItem(item);
                        }
                    }else{
                        Task task = new Task();
                        task.setFlow(flow);
                        task.setTaskId(UUID.randomUUID().toString());
                        task.setTaskName(str[i]);
                        task.setOrder(i+1);
                        task.setStatus(0);
                        if (i < flowStep - 1) {
                            task.setNextTask(tasks[i + 1]);
                        }
                        tasks[i] = task;
                    }
                }
                flow.setCurrentTask(tasks[0]);
            }

            flowService.saveFlow(flow);
            for(Task task : tasks){
                flowService.saveTask(task);
            }

        } catch (Exception e) {
            logger.info("global configuration save failed.", e);
            return "false";
        }
        return flowId;
    }

    @RequestMapping(value = "/exec", method = RequestMethod.POST)
    @ResponseBody
    public String start(String flowId) {
        new FlowMonitor(flowId).start();
        return "true";
    }

    @RequestMapping(value = "/flow", method = RequestMethod.GET)
    @ResponseBody
    public String start() {
        List<Flow> list = flowService.findAllFlow();
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @ResponseBody
    public String detail(String flowId) {
        String json = flowService.findTaskAndItem(flowId);
        return json;
    }


    public static void main(String[] args) {
        //new SpringUtil(SpringApplication.run("classpath:/beans.xml", args));
        new SpringUtil(SpringApplication.run(FlowController.class, args));
    }


}
