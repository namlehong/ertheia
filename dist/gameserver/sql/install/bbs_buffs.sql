/*
MySQL Data Transfer
Source Host: localhost
Source Database: l2jgodh
Target Host: localhost
Target Database: l2jgodh
Date: 10.06.2012 17:59:17
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for bbs_buffs
-- ----------------------------
DROP TABLE IF EXISTS `bbs_buffs`;
CREATE TABLE `bbs_buffs` (
  `char_id` int(11) NOT NULL DEFAULT '0',
  `name` varchar(256) NOT NULL DEFAULT '',
  `skills` varchar(256) NOT NULL DEFAULT '',
  PRIMARY KEY (`char_id`,`name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records 
-- ----------------------------
INSERT INTO `bbs_buffs` VALUES ('0', 'Fighter;Воину', '1499,1501,1502,1503,1504,1519,4358,1388,4349,4346,4352,264,267,268,269,304,364,349,271,275,274,11532,11529,11523,11524,11519,11522,11520,11521');
INSERT INTO `bbs_buffs` VALUES ('0', 'Mystic;Магу', '1500,1501,1504,4355,1303,1389,1461,4346,4350,4351,4352,1397,1460,264,267,268,304,364,363,349,276,273,365,11525,11517,11519,11520,11518,11521');
INSERT INTO `bbs_buffs` VALUES ('0', 'Resistance;Сопротивление', '4346,4350,1032,1033,1182,1189,1191,1352,1353,1354,1392,1393,306,308,529,307,309,311,530,11565,11567,11566');
INSERT INTO `bbs_buffs` VALUES ('0', 'Dance\'s / Song\'s;Песни / Танцы', '1588,1586,1590,1599,1592');
INSERT INTO `bbs_buffs` VALUES ('0', 'Maximum speed;Максимум скорости', '1504,1257,4352,268,11530');
