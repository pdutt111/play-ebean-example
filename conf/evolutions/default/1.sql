# --- First database schema

# --- !Ups
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `password` varchar(500) DEFAULT NULL,
  `research_areas` text,
  `position` varchar(255) DEFAULT NULL,
  `affiliation` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `fax` varchar(255) DEFAULT NULL,
  `address` text,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `zip` bigint(20) DEFAULT NULL,
  `authority` varchar(45) DEFAULT 'user',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_email` (`email`),
  KEY `idx_user` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;

CREATE TABLE `submission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(200) DEFAULT NULL,
  `categories` varchar(200) DEFAULT NULL,
  `email` varchar(200) DEFAULT NULL,
  `author1` varchar(200) DEFAULT NULL,
  `author2` varchar(200) DEFAULT NULL,
  `author3` varchar(200) DEFAULT NULL,
  `author4` varchar(200) DEFAULT NULL,
  `author5` varchar(200) DEFAULT NULL,
  `author6` varchar(200) DEFAULT NULL,
  `author7` varchar(200) DEFAULT NULL,
  `project_abstract` text,
  `details` text,
  `status` enum('1','0','-1') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;


# --- !Downs

-- SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists company;

drop table if exists computer;

-- SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists company_seq;

drop sequence if exists computer_seq;

