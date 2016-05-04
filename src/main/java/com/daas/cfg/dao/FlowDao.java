package com.daas.cfg.dao;

import com.daas.cfg.beans.Flow;
import com.daas.cfg.beans.Task;
import com.daas.cfg.beans.TaskItem;
import com.daas.cfg.util.DateUtil;
import com.google.common.collect.Lists;
import com.sun.org.apache.xerces.internal.impl.dv.dtd.ENTITYDatatypeValidator;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Hours;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * User: rolex
 * Date: 2016/4/17
 * version: 1.0
 */
@Repository
public class FlowDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Flow findFlowById(String flowId) {
        String sql = "select * from t_flow i where i.id = '" + flowId + "'";
        Flow flow = new Flow();
        jdbcTemplate.query(sql, new Object[]{}, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                flow.setId(resultSet.getString("id"));
                flow.setName(resultSet.getString("name"));
                flow.setDate(resultSet.getString("date"));
                flow.setStatus(resultSet.getInt("status"));
                flow.setCurrentTask(new Task(resultSet.getString("current_task")));
                flow.setStartTime(resultSet.getTimestamp("start_time") == null ? "" : new SimpleDateFormat("yyyy-MM-dd HH24:mm:ss").format(resultSet.getTimestamp("start_time")));
                flow.setCompleteTime(resultSet.getTimestamp("complete_time") == null ? "" : new SimpleDateFormat("yyyy-MM-dd HH24:mm:ss").format(resultSet.getTimestamp("complete_time")));
                flow.setCost(getCost(resultSet.getTimestamp("start_time"), resultSet.getTimestamp("complete_time")));
            }
        });
        return flow;
    }

    String getCost(Date start, Date end) {

        if (end == null || start == null || "".equals(end) || "".equals(start)) {
            return "";
        } else {
            return DateUtil.format(start,end);
        }
    }

    public List<Flow> findAllFlow() {
        String sql = "select * from t_flow i";
        List<Flow> list = Lists.newArrayList();
        jdbcTemplate.query(sql, new Object[]{}, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                Flow flow = new Flow();
                flow.setId(resultSet.getString("id"));
                flow.setName(resultSet.getString("name"));
                flow.setDate(resultSet.getString("date"));
                flow.setStatus(resultSet.getInt("status"));
                flow.setCurrentTask(new Task(resultSet.getString("current_task")));
                flow.setStartTime(resultSet.getTimestamp("start_time") == null ? "" : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(resultSet.getTimestamp("start_time")));
                flow.setCompleteTime(resultSet.getTimestamp("complete_time") == null ? "" : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(resultSet.getTimestamp("complete_time")));
                flow.setCost(getCost(resultSet.getTimestamp("start_time"), resultSet.getTimestamp("complete_time")));
                list.add(flow);
            }
        });
        return list;
    }

    public List<Task> findAllTask(String flowId) {
        String sql = "select * from t_task i where i.flow_id = '" + flowId + "' order by `order` asc";
        List<Task> list = new ArrayList();
        jdbcTemplate.query(sql, new Object[]{}, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                Task task = new Task();
                task.setTaskId(resultSet.getString("task_id"));
                task.setTaskName(resultSet.getString("task_name"));
                task.setFlow(new Flow(resultSet.getString("flow_id")));
                task.setStatus(resultSet.getInt("status"));
                task.setNextTask(resultSet.getString("next_task") == null ? null : new Task(resultSet.getString("next_task")));
                task.setStartTime(resultSet.getTimestamp("start_time") == null ? "" : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(resultSet.getDate("start_time")));
                task.setCompleteTime(resultSet.getTimestamp("complete_time") == null ? "" : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(resultSet.getDate("complete_time")));
                task.setOrder(resultSet.getInt("order"));
                task.setCost(getCost(resultSet.getTimestamp("start_time"), resultSet.getTimestamp("complete_time")));
                list.add(task);
            }
        });
        return list;
    }

    public Task findTaskById(String taskId) {
        String sql = "select * from t_task i where i.task_id = '" + taskId + "'";
        Task task = new Task();
        jdbcTemplate.query(sql, new Object[]{}, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                task.setTaskId(resultSet.getString("task_id"));
                task.setTaskName(resultSet.getString("task_name"));
                task.setFlow(new Flow(resultSet.getString("flow_id")));
                task.setStatus(resultSet.getInt("status"));
                task.setNextTask(resultSet.getString("next_task") == null ? null : new Task(resultSet.getString("next_task")));
                task.setStartTime(resultSet.getTimestamp("start_time") == null ? "" : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(resultSet.getDate("start_time")));
                task.setCompleteTime(resultSet.getTimestamp("complete_time") == null ? "" : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(resultSet.getDate("complete_time")));
                task.setOrder(resultSet.getInt("order"));
                task.setCost(getCost(resultSet.getTimestamp("start_time"), resultSet.getTimestamp("complete_time")));
            }
        });
        return task;
    }

    public List<TaskItem> findAllTaskItem(String taskId) {
        String sql = "select * from t_task_item i where i.task_id = '" + taskId + "'";
        List<TaskItem> list = new ArrayList();
        jdbcTemplate.query(sql, new Object[]{}, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                TaskItem taskItem = new TaskItem();
                taskItem.setItemId(resultSet.getString("item_id"));
                taskItem.setItemName(resultSet.getString("item_name"));
                taskItem.setTask(new Task(resultSet.getString("task_id")));
                taskItem.setStatus(resultSet.getInt("status"));
                taskItem.setStartTime(resultSet.getTimestamp("start_time") == null ? "" : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(resultSet.getDate("start_time")));
                taskItem.setCompleteTime(resultSet.getTimestamp("complete_time") == null ? "" : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(resultSet.getDate("complete_time")));
                list.add(taskItem);
                taskItem.setCost(getCost(resultSet.getTimestamp("start_time"), resultSet.getTimestamp("complete_time")));
            }
        });
        return list;
    }

    public TaskItem findTaskItemById(String itemId) {
        String sql = "select * from t_task_item i where i.item_id = '" + itemId + "'";
        TaskItem taskItem = new TaskItem();
        jdbcTemplate.query(sql, new Object[]{}, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                taskItem.setItemId(resultSet.getString("item_id"));
                taskItem.setItemName(resultSet.getString("item_name"));
                taskItem.setTask(new Task(resultSet.getString("task_id")));
                taskItem.setStatus(resultSet.getInt("status"));
                taskItem.setStartTime(resultSet.getTimestamp("start_time") == null ? "" : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(resultSet.getDate("start_time")));
                taskItem.setCompleteTime(resultSet.getTimestamp("complete_time") == null ? "" : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(resultSet.getDate("complete_time")));
                taskItem.setCost(getCost(resultSet.getTimestamp("start_time"), resultSet.getTimestamp("complete_time")));
            }
        });
        return taskItem;
    }

    public void startFlow(String flowId) {
        jdbcTemplate.execute("update t_flow i set i.status = 1,i.start_time=sysdate() where i.id = '" + flowId + "'");
    }

    public void completeFlow(String flowId) {
        jdbcTemplate.execute("update t_flow i set i.status = 2,i.complete_time=sysdate() where i.id = '" + flowId + "'");
    }

    public Task startNextTask(String flowId, String itemId) {
        String nextId = findTaskById(itemId).getNextTask().getTaskId();
        updateCurrent(flowId, findTaskById(nextId).getTaskId());
        return startTask(flowId, nextId);
    }

    public Task startTask(String flowId, String taskId) {
        String sql = "update t_task i set i.status = 1,i.start_time=sysdate() where i.task_id = '" + taskId + "' and flow_id = '" + flowId + "'";
        jdbcTemplate.execute(sql);
        return findTaskById(taskId);
    }

    public TaskItem startTaskItem(String taskId, String itemId) {
        String sql = "update t_task_item i set i.status = 1,i.start_time=sysdate() where i.item_id = '" + itemId + "' and task_id = '" + taskId + "'";
        jdbcTemplate.execute(sql);
        return findTaskItemById(itemId);
    }

    public void updateCurrent(String flowId, String nextId) {
        jdbcTemplate.execute("update t_flow i set i.current_task = '" + nextId + "' where i.id = '" + flowId + "'");
    }

    public void saveGlobalCfg(Map<String, String> map) {
        jdbcTemplate.execute("delete from t_global");
        map.entrySet().forEach(e ->
                jdbcTemplate.execute("insert into t_global (`key`,`value`) values ('" + e.getKey() + "','" + e.getValue() + "')")
        );

    }

    public void completeTask(String flowId, String taskId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date());

        String sql = "update t_task i set i.status = 2, i.complete_time = sysdate() where i.task_id = '" + taskId + "' and i.flow_id = '" + flowId + "'";
        jdbcTemplate.execute(sql);
        Task item = findTaskById(taskId);
        if (item.getNextTask() == null) {
            jdbcTemplate.execute("update t_flow i set i.status = 2,i.current_task = null ,i.complete_time = sysdate() where i.id = '" + flowId + "'");
        } else {
            jdbcTemplate.execute("update t_flow i set i.current_task = '" + item.getNextTask().getTaskId() + "' where i.id = '" + flowId + "'");
        }
    }

    public void completeTaskItem(String flowId, String taskId, String itemId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date());

        jdbcTemplate.execute("update t_task_item i set i.status = 2, i.complete_time = sysdate() where i.item_id = '" + itemId + "' and i.task_id = '" + taskId + "'");

        List list = jdbcTemplate.queryForList("select * from t_task_item where task_id='" + taskId + "' and status<>2");
        if (list == null || list.size() == 0) {
            completeTask(flowId, taskId);
        }
    }

    public void saveFlow(Flow flow) {
        jdbcTemplate.execute("insert into t_flow (id,name,date,status,current_task) values ('" + flow.getId() + "','" + flow.getName() + "','" + flow.getDate() + "',0,'" + flow.getCurrentTask().getTaskId() + "')");
    }

    public void saveTask(Task task) {
        if (task.getNextTask() == null) {
            jdbcTemplate.execute("insert into t_task (task_id,task_name,flow_id,status,`order`) values ('" + task.getTaskId() + "','" + task.getTaskName() + "','" + task.getFlow().getId() + "',0," + task.getOrder() + ")");
        } else {
            jdbcTemplate.execute("insert into t_task (task_id,task_name,flow_id,status,next_task,`order`) values ('" + task.getTaskId() + "','" + task.getTaskName() + "','" + task.getFlow().getId() + "',0,'" + task.getNextTask().getTaskId() + "'," + task.getOrder() + ")");
        }
    }

    public void saveTaskItem(TaskItem taskItem) {
        jdbcTemplate.execute("insert into t_task_item (item_id,item_name,task_id,status) values ('" + taskItem.getItemId() + "','" + taskItem.getItemName() + "','" + taskItem.getTask().getTaskId() + "',0" + ")");
    }

    public String getConfig(String key) {
        String sql = "select i.value from t_global i where i.key = '" + key + "'";
        return (String) jdbcTemplate.queryForMap(sql).get("value");
    }
}

