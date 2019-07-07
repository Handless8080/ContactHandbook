/*create user 'contact_handbook'@'localhost' identified by '12345';

drop database `contact_handbook_db`;
create database `contact_handbook_db`
charset utf8 collate utf8_general_ci;

use `contact_handbook_db`;

create table `contact` (
	`id` int unsigned primary key auto_increment not null,
    `name` varchar(20) not null,
    `surname` varchar(20) not null,
    `patronymic` varchar(20),
    `born_date` date,
    `gender` enum('Мужской', 'Женский') not null default 'Мужской',
    `citizenship` varchar(30),
    `martial_status` enum('Не замужем/не женат', 'Разведен/разведена', 'Замужем/женат', 'Состоит в гражданском браке') not null default 'Не замужем/не женат',
    `web_site` varchar(50),
    `email` varchar(50),
    `job` varchar(50),
    `country` varchar(30),
    `town` varchar(30),
    `street` varchar(30),
    `house_number` varchar(5),
    `flat_number` tinyint(1) unsigned,
    `zip_code` mediumint(1) unsigned,
    `avatar_filename` varchar(255),
    `enabled` bit not null default 1
) engine InnoDB;

create table `phone` (
	`id` int unsigned primary key auto_increment not null,
    `country_code` mediumint(1) unsigned,
    `operator_code` tinyint(1) unsigned,
    `number` int unsigned not null,
    `phone_type` enum('Мобильный', 'Домашний') not null default 'Мобильный',
    `comment` varchar(255),
    `contact_id` int unsigned not null,
    `enabled` bit not null default 1,
    foreign key (`contact_id`) references `contact` (`id`) on delete cascade
) engine InnoDB;

create table `attachment` (
	`id` int unsigned primary key auto_increment not null,
    `file_name` varchar(50),
    `original_file_name` varchar(50),
    `up_date` datetime default current_timestamp,
    `upload_date` date as (date(`up_date`)),
    `comment` varchar(255),
    `contact_id` int unsigned not null,
    `enabled` bit not null default 1,
    foreign key (`contact_id`) references `contact` (`id`) on delete cascade
) engine InnoDB;

grant all privileges on `contact_handbook_db`.* to 'contact_handbook'@'localhost';*/

use `contact_handbook_db`;

select * from `contact`;