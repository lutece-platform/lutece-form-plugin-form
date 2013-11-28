ALTER TABLE form_response ADD CONSTRAINT fk_form_response_entry FOREIGN KEY (id_entry)
	REFERENCES form_entry (id_entry);
	
ALTER TABLE form_verify_by ADD CONSTRAINT fk_form_verify_by_field FOREIGN KEY (id_field)
	REFERENCES form_field (id_field);