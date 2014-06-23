-- Count tf
UPDATE  `keywords_occurrences` koo
JOIN (

SELECT file_id, SUM( count ) AS sum_count
FROM keywords_occurrences ko
GROUP BY file_id
)ko ON ( ko.file_id = koo.file_id )
SET koo.tf=koo.count / ko.sum_count

-- Count idf German - ATTENTION! It is necessary to update total document count (380 for curret German) appropriately 
-- UPDATE keywords k JOIN (SELECT keyword_id, LOG(380 / COUNT(DISTINCT file_id)) idfc FROM `keywords_occurrences` GROUP BY keyword_id) ko ON (k.keyword_id = ko.keyword_id) SET idf = idfc

-- Count idf Universal
UPDATE keywords k JOIN (SELECT keyword_id, LOG((SELECT COUNT(*) FROM files) / COUNT(DISTINCT file_id)) idfc FROM `keywords_occurrences` GROUP BY keyword_id) ko ON (k.keyword_id = ko.keyword_id) SET idf = idfc

-- Select keywords German
SELECT *, (tf*idf) FROM keywords JOIN `keywords_occurrences` USING (keyword_id) WHERE file_id = 22 AND(SUBSTRING(word, 1, 1) COLLATE utf8_bin) = UPPER(SUBSTRING(word, 1, 1)) 
AND (SUBSTRING(word, 2, 1) COLLATE utf8_bin ) = LOWER(SUBSTRING(word, 2, 1)) ORDER BY (tf * idf) DESC

-- Select keywords Dutch
SELECT *, (tf*idf) FROM keywords JOIN `keywords_occurrences` USING (keyword_id) WHERE file_id = 785 ORDER BY (tf*idf) DESC

-- CoOccurring entities
INSERT INTO en_cooccurrences (entity_id, cooccur_entity_id, cooccurrence_count) 
    SELECT e1.entity_id, e2.entity_id, COUNT(*) FROM `en_entities_mentions` e1 
        JOIN `en_entities_mentions` e2 USING (paragraph_id) 
    GROUP BY  e1.entity_id, e2.entity_id;