/*
 Navicat Premium Data Transfer

 Source Server         : new server
 Source Server Type    : MySQL
 Source Server Version : 50621
 Source Host           : 192.168.1.2
 Source Database       : l2vh_auth

 Target Server Type    : MySQL
 Target Server Version : 50621
 File Encoding         : utf-8

 Date: 12/02/2014 21:28:04 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `accounts`
-- ----------------------------
DROP TABLE IF EXISTS `accounts`;
CREATE TABLE `accounts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `login` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `password2` varchar(255) DEFAULT NULL,
  `l2email` varchar(100) DEFAULT 'null@null',
  `last_access` int(11) DEFAULT '0',
  `access_level` int(11) DEFAULT '0',
  `last_ip` varchar(15) DEFAULT NULL,
  `last_server` int(11) DEFAULT '0',
  `bonus` int(11) DEFAULT '1',
  `bonus_expire` int(11) DEFAULT '0',
  `ban_expire` int(11) DEFAULT '0',
  `allow_ip` varchar(255) NOT NULL DEFAULT '',
  `allow_hwid` varchar(255) DEFAULT '',
  `points` int(11) DEFAULT '0',
  `l2money` double(10,2) DEFAULT '0.00',
  `user_id` int(10) DEFAULT NULL,
  `acc_switch_times` tinyint(5) NOT NULL DEFAULT '0',
  `first_acc_switch_time` bigint(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `login` (`login`),
  KEY `last_ip` (`last_ip`)
) ENGINE=InnoDB AUTO_INCREMENT=20879 DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
