ALTER TABLE `de_keywords_occurrences` ADD FOREIGN KEY ( `keyword_id` ) REFERENCES `ltv_keywords`.`de_keywords` (
`keyword_id`
) ON DELETE CASCADE ON UPDATE CASCADE ;

ALTER TABLE `de_keywords_occurrences` ADD FOREIGN KEY ( `file_id` ) REFERENCES `ltv_keywords`.`de_files` (
`file_id`
) ON DELETE CASCADE ON UPDATE CASCADE ;


ALTER TABLE `nl_keywords_occurrences` ADD FOREIGN KEY ( `keyword_id` ) REFERENCES `ltv_keywords`.`nl_keywords` (
`keyword_id`
) ON DELETE CASCADE ON UPDATE CASCADE ;

ALTER TABLE `nl_keywords_occurrences` ADD FOREIGN KEY ( `file_id` ) REFERENCES `ltv_keywords`.`nl_files` (
`file_id`
) ON DELETE CASCADE ON UPDATE CASCADE ;