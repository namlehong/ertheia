CREATE TABLE IF NOT EXISTS `commission_items` (
	`id` INT(11) NOT NULL,
	`owner_id` INT(11) NOT NULL,
	`item_id` INT(7) NOT NULL,
	`item_price` BIGINT(20) NOT NULL,
	`count` BIGINT(20) NOT NULL,
	`period_days` TINYINT UNSIGNED NOT NULL,
	`register_date` INT UNSIGNED NOT NULL,
	`enchant_level` INT(11) NOT NULL,
	`attribute_fire` INT(11) NOT NULL,
	`attribute_water` INT(11) NOT NULL,
	`attribute_wind` INT(11) NOT NULL,
	`attribute_earth` INT(11) NOT NULL,
	`attribute_holy` INT(11) NOT NULL,
	`attribute_unholy` INT(11) NOT NULL,
	PRIMARY KEY  (`id`)
) ENGINE=InnoDB;
