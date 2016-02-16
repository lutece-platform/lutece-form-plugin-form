/**
 * 
 */
package fr.paris.lutece.plugins.form.service.entrytype;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeGeospatial;

/**
 * @author bass
 *
 */
public class EntryTypeGeospatial extends AbstractEntryTypeGeospatial {

	/** The Constant CONSTANT_ID_ADDRESS. */
    private static final String TEMPLATE_CREATE = "admin/plugins/form/entries/create_entry_type_geospatial.html";
    private static final String TEMPLATE_MODIFY = "admin/plugins/form/entries/modify_entry_type_geospatial.html";
    private static final String TEMPLATE_HTML_CODE = "skin/plugins/form/entries/html_code_entry_type_geospatial.html";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateCreate( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_CREATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateModify( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_MODIFY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateHtmlForm( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_HTML_CODE;
    }

}
