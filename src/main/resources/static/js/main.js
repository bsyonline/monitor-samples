/*****list.html*****/
function flowListHtml(n, flow) {
    return html = "<tr>" +
        "<td class='cell-icon'><span  class='glyphicon glyphicon-file'></span></td>" +
        "<td class='cell-center'>" + (n + 1) + "</td>" +
        "<td class='cell-left cell-width'>" + flow.id + "</td>" +
        "<td class='cell-left'>" + flow.name + "</td>" +
        "<td class='cell-center'>" + flow.date + "</td>" +
        "<td class='cell-center'>" + statusFormat(flow.status) + "</td>" +
        "<td class='cell-center'>" + flow.startTime + "</td>" +
        "<td class='cell-center'>" + flow.completeTime + "</td>" +
        "<td class='cell-center'>" + flow.cost + "</td>" +
        "<td class='cell-center'>" +
        "<a href='javascript:void(0)' onclick=\"detail('" + flow.id + "')\">查看</a>&nbsp;&nbsp;" +
        "<a href='javascript:void(0)' onclick=\"exec('" + flow.id + "')\">执行</a>" +
        "</td>" +
        taskListHtml(flow.id) +
        "</tr>";
}

function taskListHtml(flowId) {
    return html = "<tr class='cell-collapse'><td colspan='10' class='cell-collapse cell-right flow-detail " + flowId + "-detail' ><a href='javascript:void(0)' onclick=\"collapseList('" + flowId + "')\">收起</a></td></tr>"+
        "<tr class='flow-detail " + flowId + "-detail'>" +
        "<td class='cell-center' colspan='10'>" +
        "<table class='table table-bordered tb-task' >" +
        "<tr>" +
        "<td class='cell-center'></td>" +
        "<td class='cell-center cell-width'>编号</td>" +
        "<td class='cell-center'>名称</td>" +
        "<td class='cell-center'>状态</td>" +
        "<td class='cell-center'>开始时间</td>" +
        "<td class='cell-center'>结束时间</td>" +
        "<td class='cell-center'>耗时</td>" +
        "</tr>" +
        "<tbody id='flow-" + flowId + "-task-window'>" +

        "</tbody>" +
        "</table>" +
        "</td>" +
        "</tr>"
}

function taskHtml(k, v) {

    var task = $.parseJSON(k);

    var html =
        "<tr>" +
        "<td class='cell-icon'><span class='glyphicon glyphicon-tags'></span></td>"+
        "<td class='cell-left cell-width'>" + task.taskId + "</td>" +
        "<td class='cell-left'>" + task.taskName + "</td>" +
        "<td class='cell-center'>" + statusFormat(task.status) + "</td>" +
        "<td class='cell-center'>" + task.startTime + "</td>" +
        "<td class='cell-center'>" + task.completeTime + "</td>" +
        "<td class='cell-center'>" + task.cost + "</td>" +
        "</tr>"

    if (v.length > 0) {
        html += "<tr>" +
            "<td class='cell-center' colspan='7'>" +
            "<table class='table table-bordered tb-item'>" +
            "<tr>" +
            "<td class='cell-center'></td>" +
            "<td class='cell-center cell-width'>编号</td>" +
            "<td class='cell-center'>名称</td>" +
            "<td class='cell-center'>状态</td>" +
            "<td class='cell-center'>开始时间</td>" +
            "<td class='cell-center'>结束时间</td>" +
            "<td class='cell-center'>耗时</td>" +
            "</tr>"
        $(v).each(function (n, item) {
            html += "<tr>" +
                "<td class='cell-icon'><span class='glyphicon glyphicon-tag'></span></td>"+
                "<td class='cell-left cell-width'>" + item.itemId + "</td>" +
                "<td class='cell-left'>" + item.itemName + "</td>" +
                "<td class='cell-center'>" + statusFormat(item.status) + "</td>" +
                "<td class='cell-center'>" + item.startTime + "</td>" +
                "<td class='cell-center'>" + item.completeTime + "</td>" +
                "<td class='cell-center'>" + item.cost + "</td>" +
                "</tr>"

        })

        html += "</table>" +
            "</td>" +
            "</tr>"
    }
    return html;
}

function detail(flowId) {

    $(".flow-detail").each(function(){
        $(this).hide()
    })
    $("#flow-" + flowId + "-task-window").empty()
    $.post("/detail", {flowId: flowId}, function (data) {

        $.each(data, function (k, v) {

            $("#flow-" + flowId + "-task-window").append(taskHtml(k, v))
        })

        $("." + flowId + "-detail").show()
    }, 'json')
}
function collapseList(flowId){
    $("." + flowId + "-detail").hide()
}
function replaceBlank(arg) {
    if (arg == 'null' || arg == undefined) {
        return '';
    } else {
        return arg;
    }
}

function statusFormat(arg) {
    if (arg == 0) {
        return "未启动"
    } else if (arg == 1) {
        return "执行中"
    } else if (arg == 2) {
        return "已完成"
    }
}

/*****flow.html*****/
function getTaskList() {
    var list = "";
    $(".task-item-name").each(function () {
        list += $(this).attr("name") + getSubTaskList($(this)) + ","
    })
    return list.substring(0, list.length - 1);
}
function getSubTaskList(arg) {
    var ck = "["
    arg.parent().find(".ck-panel").find("input[type=checkbox]").each(function () {
        if ($(this).prop("checked") == true) {
            ck += $(this).attr("t-name") + "|"
        }
    })
    if (ck == '[') {
        ck = "";
    } else {
        ck = ck.substring(0, ck.length - 1);
        ck += "]";
    }

    return ck
}

function addTask(arg, val) {
    var html = "";
    if (arg == 't-change') {
        html += "<div class='task-item'><span>"
            + (getRowNo() + 1)
            + "</span><span class='task-item-name' name='" + arg + "'>"
            + val
            + "</span><span><input type='checkbox' name='ck"
            + getRowNo() + "All' onclick=\"ckALl('ck"
            + getRowNo() + "')\">全选</span>" + changePanelHtml() + "</div>";
    } else if (arg == 't-validate') {
        html += "<div class='task-item'><span>"
            + (getRowNo() + 1)
            + "</span><span class='task-item-name' name='" + arg + "'>"
            + val + "</span><span><input type='checkbox' name='ck"
            + getRowNo() + "All' onclick=\"ckALl('ck"
            + getRowNo() + "')\">全选</span>" + validatePanelHtml() + "</div>";
    } else {
        html += "<div class='task-item'><span>" + (getRowNo() + 1) + "</span><span class='task-item-name'  name='" + arg + "'>" + val + "</span></div>";
    }
    $("#task-list").append(html);
}

function getRowNo() {
    return $(".task-item").size();
}

function exec(flowId) {
    if (flowId == '')
        flowId = $("#flow-id").text();
    $.post("http://localhost:8000/exec", {
        flowId: flowId
    }, function (data) {
    }, 'text');
}

function saveCfg() {

    var flowName = $("#flow-name").val();
    var flowDate = $("#flow-date").val();
    var flowCfg = getTaskList();

    $.post("http://localhost:8000/cfg", {
        flowName: flowName, flowDate: flowDate, flowCfg: flowCfg
    }, function (data) {
        alert(data)
        if (data != 'false') {
            $("#exec-btn").attr("disabled", false);
            $("#flow-id").text(data)
        }

    }, 'text');

}
function changePanelHtml() {
    return "<div class='panel ck-panel'>" +
        "<div class='panel-heading'>" +
        "<div class='sub-ck-body'>" +
        "<div class='sub-ck'><input type='checkbox' t-name='E_ENT_BASEINFO' name=\"ck" + getRowNo() + "\" class='sub-task-name'></div>" +
        "<div class='sub-ck-name'>企业基本信息</div>" +
        "</div>" +
        "<div class='sub-ck-body'>" +
        "<div class='sub-ck'><input type='checkbox' t-name='E_PRI_PERSON' name=\"ck" + getRowNo() + "\" class='sub-task-name'></div>" +
        "<div class='sub-ck-name'>主要管理人员</div>" +
        "</div>" +
        "<div class='sub-ck-body'>" +
        "<div class='sub-ck'><input type='checkbox' t-name='E_INV_INVESTMENT' name=\"ck" + getRowNo() + "\" class='sub-task-name'></div>" +
        "<div class='sub-ck-name'>股东与对外投资</div>" +
        "</div>" +
        "</div>" +
        "<div class='panel-heading'>" +
        "<div class='sub-ck-body'>" +
        "<div class='sub-ck'><input type='checkbox' t-name='FROST' name=\"ck" + getRowNo() + "\" class='sub-task-name'></div>" +
        "<div class='sub-ck-name'>股权冻结</div>" +
        "</div>" +
        "<div class='sub-ck-body'>" +
        "<div class='sub-ck'><input type='checkbox' t-name='IMPAWN' name=\"ck" + getRowNo() + "\" class='sub-task-name'></div>" +
        "<div class='sub-ck-name'>股权出质</div>" +
        "</div>" +
        "<div class='sub-ck-body'>" +
        "<div class='sub-ck'><input type='checkbox' t-name='XZCF' name=\"ck" + getRowNo() + "\" class='sub-task-name'></div>" +
        "<div class='sub-ck-name'>行政处罚</div>" +
        "</div>" +
        "<div class='sub-ck-body'>" +
        "<div class='sub-ck'><input type='checkbox' t-name='DIS_SXBZXR' name=\"ck" + getRowNo() + "\" class='sub-task-name'></div>" +
        "<div class='sub-ck-name'>失信被执行人</div>" +
        "</div>" +
        "</div>" +
        "</div>";
}

function validatePanelHtml() {
    return "<div class='panel ck-panel'>" +
        "<div class='panel-heading'>" +
        "<div class='sub-ck-body'>" +
        "<div class='sub-ck'><input type='checkbox' t-name='VALIDATE_GS' name=\"ck" + getRowNo() + "\" class='sub-task-name'></div>" +
        "<div class='sub-ck-name'>工商数据</div>" +
        "</div>" +
        "<div class='sub-ck-body'>" +
        "<div class='sub-ck'><input type='checkbox' t-name='VALIDATE_NON_GS' name=\"ck" + getRowNo() + "\" class='sub-task-name'></div>" +
        "<div class='sub-ck-name'>股权冻结等4类数据</div>" +
        "</div>" +
        "</div>" +
        "</div>";
}

function ckALl(arg) {
    if ($("input[name='" + arg + "All']").prop("checked") == true) {
        $("input[name='" + arg + "']").prop("checked", "checked");
    } else {
        $("input[name='" + arg + "']").removeAttr("checked");
    }
}
function clean() {
    $("#task-list").text("")
}

/*****index.html*****/
function globalCfg() {
    var host = $("#hdfs-host").val();
    var port = $("#hdfs-port").val();
    var thread = $("#thread-num").val();
    var deHost = $("#de-host").val();
    var dePort = $("#de-port").val();
    var shPath = $("#sh-path").val();
    var arg0 = "{\"hdfs.host\":\"" + host + "\",\"hdfs.port\":\"" + port + "\"," +
        "\"thread\":\"" + thread + "\",\"data.engine.host\":\"" + deHost + "\"," +
        "\"shell.path\":\"" + shPath + "\",\"data.engine.port\":\"" + dePort + "\"}";
    $.post("http://localhost:8000/global", {
        arg0: arg0
    }, function (data) {
        alert(true)
    }, 'json');
}