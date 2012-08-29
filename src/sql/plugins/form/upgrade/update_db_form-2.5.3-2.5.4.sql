-- FORM-178 : Add the possibility to order the attributes (with a select list of orders)
-- give another order system for the conditional questions
ALTER TABLE form_entry ADD pos_conditional INT default 0;

ALTER TABLE form_form ADD front_office_title VARCHAR(255) DEFAULT NULL;
ALTER TABLE form_form ADD is_shown_front_office_title SMALLINT(6) DEFAULT 0;