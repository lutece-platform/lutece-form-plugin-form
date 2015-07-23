package fr.paris.lutece.plugins.form.service.entrytype;

import fr.paris.lutece.plugins.form.service.upload.FormAsynchronousUploadHandler;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.service.upload.AbstractGenAttUploadHandler;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeImage;

public class EntryTypeCropImage extends AbstractEntryTypeImage{

	 /**
     * Name of the bean of this service
     */
    public static final String BEAN_NAME = "form.entryTypeCropImage";
    private static final String JSP_DOWNLOAD_FILE = "jsp/admin/plugins/form/DoDownloadFile.jsp";
    private static final String TEMPLATE_CREATE = "admin/plugins/form/entries/create_entry_type_crop_image.html";
    private static final String TEMPLATE_MODIFY = "admin/plugins/form/entries/modify_entry_type_crop_image.html";
    private static final String TEMPLATE_HTML_CODE = "skin/plugins/form/entries/html_code_entry_type_crop_image.html";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateHtmlForm( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_HTML_CODE;
    }

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
    public AbstractGenAttUploadHandler getAsynchronousUploadHandler(  )
    {
        return FormAsynchronousUploadHandler.getHandler(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrlDownloadFile( int nResponseId, String strBaseUrl )
    {
        UrlItem url = new UrlItem( strBaseUrl + JSP_DOWNLOAD_FILE );
        url.addParameter( PARAMETER_ID_RESPONSE, nResponseId );

        return url.getUrl(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkForImages(  )
    {
        return true;
    }
}

