CREATE TABLE `t_flow` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `date` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `current_task` varchar(255) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `complete_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `t_task` (
  `task_id` varchar(255) NOT NULL,
  `task_name` varchar(255) DEFAULT NULL,
  `flow_id` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `next_task` varchar(255) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `complete_time` datetime DEFAULT NULL,
  `order` int(11) DEFAULT NULL,
  PRIMARY KEY (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `t_task_item` (
  `item_id` varchar(255) NOT NULL,
  `item_name` varchar(255) DEFAULT NULL,
  `task_id` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `complete_time` datetime DEFAULT NULL,
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `t_global` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `key` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into t_flow (name,date,status,current_task) values ('flow_20160314','20160314',0,1);

insert into t_task (task_name,flow_id,status,next_task,`order`) values ('change',1,0,1,1);

insert into t_task (task_name,flow_id,status,`order`) values ('push',1,0,1)