/*
 Navicat Premium Data Transfer

 Source Server         : 125.212.219.54
 Source Server Type    : MySQL
 Source Server Version : 50619
 Source Host           : localhost
 Source Database       : l2vh_ertheia

 Target Server Type    : MySQL
 Target Server Version : 50619
 File Encoding         : utf-8

 Date: 10/31/2014 17:54:05 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `fpc`
-- ----------------------------
DROP TABLE IF EXISTS `fpc`;
CREATE TABLE `fpc` (
  `obj_Id` int(11) NOT NULL,
  PRIMARY KEY (`obj_Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

SET FOREIGN_KEY_CHECKS = 1;
