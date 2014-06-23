DELETE FROM nl_files;
DELETE FROM nl_keywords;
DELETE FROM nl_keywords_occurrences;
DELETE FROM de_files;
DELETE FROM de_keywords;
DELETE FROM de_keywords_occurrences;

ALTER TABLE  `de_files` AUTO_INCREMENT =1;
ALTER TABLE  `de_keywords` AUTO_INCREMENT =1;
ALTER TABLE  `nl_files` AUTO_INCREMENT =1;
ALTER TABLE  `nl_keywords` AUTO_INCREMENT =1;