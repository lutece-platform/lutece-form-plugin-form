--
-- Table structure for table form_rss_cf
--
DROP TABLE IF EXISTS form_rss_cf;
CREATE TABLE form_rss_cf (
	id_rss int default 0 NOT NULL,
	id_form int default 0 NOT NULL,
	is_submit_rss smallint default 0 NOT NULL,	
	id_form_submit int default 0 NOT NULL,
	PRIMARY KEY (id_rss)
);


--
-- Table structure for table form_category
--
DROP TABLE IF EXISTS form_category;
CREATE TABLE form_category (
	id_category int NOT NULL,
	title varchar(100) NOT NULL,
	color varchar(10),
	PRIMARY KEY (id_category)
);

ALTER TABLE form_form ADD COLUMN id_category int default NULL;
ALTER TABLE form_form ADD CONSTRAINT fk_form_form_category FOREIGN KEY (id_category)
	REFERENCES form_category (id_category);