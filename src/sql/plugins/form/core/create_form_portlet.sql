--
-- Table structure for table form_portlet
--
DROP TABLE IF EXISTS form_portlet;
CREATE TABLE form_portlet (
	id_portlet int default NULL,
	id_form int default NULL
);

--
-- Table structure for table list_form_portlet
--
DROP TABLE IF EXISTS form_list_portlet;
CREATE TABLE list_form_portlet (
	id_portlet int default NULL,
	id_category_form int default NULL
);
