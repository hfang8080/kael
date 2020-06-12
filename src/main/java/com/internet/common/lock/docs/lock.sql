CREATE TABLE IF NOT EXISTS `distributed_lock` (
  `id` BIGINT ( 20 ) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `key` VARCHAR ( 255 ) NOT NULL DEFAULT '' COMMENT 'unique lock key',
  `owner` VARCHAR ( 255 ) NOT NULL DEFAULT '' COMMENT '拥有者',
  `expire_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '锁过期时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY ( `id` ),
  UNIQUE INDEX `uniq_lock_key` ( `key` )
) COMMENT='distributed lock table' ENGINE = INNODB DEFAULT CHARSET = utf8mb4;