package com.daas.cfg.monitor;


import com.daas.cfg.beans.Flow;
import com.daas.cfg.beans.Task;
import com.daas.cfg.service.FlowService;
import com.daas.cfg.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: rolex
 * Date: 2016/4/17
 * version: 1.0
 */
@Component
public class FlowMonitor extends Thread {

    Logger logger = LoggerFactory.getLogger(getClass());

    private String flowId;

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public FlowMonitor(String flowId) {
        this.flowId = flowId;
    }

    public FlowMonitor() {
    }

    @Autowired
    FlowService flowService;

    @Override
    public void run() {
        FlowService flowService = SpringUtil.getBean(FlowService.class);

        while (true) {
            logger.info("检查流程执行进度......");
            Flow flow = flowService.findFlow(getFlowId());
            if (flow.getStatus() == 0) {
                logger.info("{} 未启动，执行启动程序......", flow.getName());
                flowService.startFlow(flow);
                logger.info("{} 已启动，正在读取配置信息......", flow.getName());
                Task item = flowService.findTask(flow.getCurrentTask());
                if (item.getStatus() == 0) {
                    flowService.startTask(item);
                }
            } else if (flow.getStatus() == 1) {
                logger.info("{} 正在执行中, 当前任务： {}.", flow.getName(), flow.getCurrentTask().getTaskId());
                if(flow.getCurrentTask()==null){

                } else {
                    Task task = flowService.findTask(flow.getCurrentTask());
                    if (task.getStatus() == 0) {
                        flowService.startTask(task);
                    } else if (task.getStatus() == 1) {
                        logger.info("task {} 正在执行......", task.getTaskName());
                    } else if (task.getStatus() == 2) {

                        if (task.getNextTask() == null) {
                            logger.info("task {} 已执行完成，没有后续任务.", task.getTaskName());
                        } else {
                            flowService.startNextTask(task);
                        }
                    }

                }
            } else if (flow.getStatus() == 2) {
                logger.info("{} 执行完成, 完成时间{}.", flow.getName(), flow.getCompleteTime());
                break;
            }

            try {
                logger.info("等待5秒.");
                Thread.sleep(5000);
                logger.info("重新执行监控.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
