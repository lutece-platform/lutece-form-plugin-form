/*
 * Copyright (c) 2002-2011, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.form.business;

import fr.paris.lutece.plugins.form.service.upload.FormAsynchronousUploadHandler;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.Paginator;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 *
 * class EntryTypeImage
 *
 */
public class EntryTypeImage extends Entry
{
    private final String _template_create = "admin/plugins/form/create_entry_type_image.html";
    private final String _template_modify = "admin/plugins/form/modify_entry_type_image.html";
    private final String _template_html_code = "admin/plugins/form/html_code_entry_type_image.html";
    
    private static final int INTEGER_QUALITY_MAXIMUM = 1;
    
    private static final String MESSAGE_ERROR_NOT_AN_IMAGE = "form.message.notAnImage";   

    /**
     * Get the HtmlCode  of   the entry
     * @return the HtmlCode  of   the entry
     *
     * */
    public String getHtmlCode(  )
    {
        return _template_html_code;
    }

    /**
     * Get the request data
     * @param request HttpRequest
     * @param locale the locale
     * @return null if all data requiered are in the request else the url of jsp error
     */
    public String getRequestData( HttpServletRequest request, Locale locale )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strHelpMessage = ( request.getParameter( PARAMETER_HELP_MESSAGE ) != null )
            ? request.getParameter( PARAMETER_HELP_MESSAGE ).trim(  ) : null;
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strWidth = request.getParameter( PARAMETER_WIDTH );
        //String strHeight = request.getParameter( PARAMETER_HEIGHT );

        String strFieldError = EMPTY_STRING;
        int nWidth = -1;
        //int nHeight = -1;

        if ( ( strTitle == null ) || strTitle.trim(  ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_TITLE;
        }
        else if ( ( strWidth == null ) || strWidth.trim(  ).equals( EMPTY_STRING ) 
        		 )
        {
            strFieldError = FIELD_WIDTH;
        }
       /* else if ( ( strHeight == null ) || strHeight.trim(  ).equals( EMPTY_STRING ) )
        {
        	 strFieldError = FIELD_HEIGHT;
        }*/

        if ( !strFieldError.equals( EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        try
        {
            nWidth = Integer.parseInt( strWidth );
        }
        catch ( NumberFormatException ne )
        {
            strFieldError = FIELD_WIDTH;
        }
        
       /* try
        {
            nHeight = Integer.parseInt( strHeight );
        }
        catch ( NumberFormatException ne )
        {
            strFieldError = FIELD_HEIGHT;
        }*/


        if ( !strFieldError.equals( EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_NUMERIC_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        this.setTitle( strTitle );
        this.setHelpMessage( strHelpMessage );
        this.setComment( strComment );

        if ( this.getFields(  ) == null )
        {
            ArrayList<Field> listFields = new ArrayList<Field>(  );
            listFields.add( new Field(  ) );        
            //listFields.add( new Field(  ) );
            this.setFields( listFields );
        }

        this.getFields(  ).get( 0 ).setWidth( nWidth );
       // this.getFields(  ).get( 1 ).setHeight( nHeight );

        if ( strMandatory != null )
        {
            this.setMandatory( true );
        }
        else
        {
            this.setMandatory( false );
        }

        return null;
    }

    /**
     * Get template create url of the entry
     * @return template create url of the entry
     */
    public String getTemplateCreate(  )
    {
        return _template_create;
    }

    /**
     * Get the template modify url  of the entry
     * @return template modify url  of the entry
     */
    public String getTemplateModify(  )
    {
        return _template_modify;
    }

    /**
     * save in the list of response the response associate to the entry in the form submit
     * @param request HttpRequest
     * @param listResponse the list of response associate to the entry in the form submit
     * @param locale the locale
     * @return a Form error object if there is an error in the response
     */
    public FormError getResponseData( HttpServletRequest request, List<Response> listResponse, Locale locale )
    {    
    	HttpSession session = request.getSession( false );
    	// handle file deletion...
    	if ( request.getParameter( FormUtils.PARAMETER_DELETE_PREFIX + Integer.toString( this.getIdEntry(  ) ) ) != null )
    	{
    		// checkbox checked
    		String strSessionId = request.getSession(  ).getId(  );

        	// file may be uploaded asynchronously...
        	FormAsynchronousUploadHandler.removeFileItem( Integer.toString( this.getIdEntry(  ) ), strSessionId );
    		if ( session != null )
    		{
    			request.getSession(  ).removeAttribute( FormUtils.SESSION_ATTRIBUTE_PREFIX_FILE + this.getIdEntry(  ) );
    		}
    	}
    	
    	// find the fileSource the session one first...
    	FileItem fileSource = null;
    	if ( session != null )
    	{
    		// check the file in session - it might no be deleted
    		fileSource = (FileItem) session.getAttribute( FormUtils.SESSION_ATTRIBUTE_PREFIX_FILE + this.getIdEntry(  ) );
    	}
    	
    	if ( request instanceof MultipartHttpServletRequest )
    	{
			// standard upload
    		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
    		FileItem fileItemRequested = multipartRequest.getFile( FormUtils.EMPTY_STRING + this.getIdEntry(  ) );
    		
			FileItem asynchronousFileItem = FormAsynchronousUploadHandler.getFileItem( Integer.toString( getIdEntry(  ) ), request.getSession(  ).getId(  ) );
			// try asynchronous uploaded files
			if ( asynchronousFileItem != null )
			{
				fileSource = asynchronousFileItem;
			}
    		
    		if ( StringUtils.isNotBlank( fileItemRequested.getName(  ) ) )
    		{
    			// a file may have been uploaded
    			fileSource = fileItemRequested;
    		}
    		
    		session.setAttribute( FormUtils.SESSION_ATTRIBUTE_PREFIX_FILE + this.getIdEntry(  ), fileSource );

    		String strFilename = fileSource != null ? FileUploadService.getFileNameOnly( fileSource ) : StringUtils.EMPTY;
            List<RegularExpression> listRegularExpression = this.getFields(  ).get( 0 ).getRegularExpressionList(  );
            
            byte[] byValueEntry = fileSource != null ? fileSource.get(  ) : null;
            //Add the image to the response list
            Response response = new Response(  );
            response.setEntry( this );
            response.setValueResponse( byValueEntry );
            response.setFileName( strFilename );
            response.setFileExtension( FilenameUtils.getExtension( strFilename ) );
            listResponse.add( response );
            
            if ( this.isMandatory(  ) )
            {
                if ( StringUtils.isBlank( strFilename ) )
                {
                    FormError formError = new FormError(  );
                    formError.setMandatoryError( true );
                    formError.setTitleQuestion( this.getTitle(  ) );

                    return formError;
                }
            }

            String strMimeType = fileSource != null ? fileSource.getContentType(  ) : StringUtils.EMPTY;

            if ( StringUtils.isNotBlank( strMimeType ) && ( listRegularExpression != null ) &&
                    ( listRegularExpression.size(  ) != 0 ) && RegularExpressionService.getInstance(  ).isAvailable(  ) )
            {
                for ( RegularExpression regularExpression : listRegularExpression )
                {
                    if ( !RegularExpressionService.getInstance(  ).isMatches( strMimeType, regularExpression ) )
                    {
                        FormError formError = new FormError(  );
                        formError.setMandatoryError( false );
                        formError.setTitleQuestion( this.getTitle(  ) );
                        formError.setErrorMessage( regularExpression.getErrorMessage(  ) );

                        return formError;
                    }
                }
            }
            
            BufferedImage image = null;
            try
            {
            	if( byValueEntry != null )
            	{
            		image = ImageIO.read( new ByteArrayInputStream( byValueEntry ) );
            	}
    		} 
            catch ( IOException e )
    		{			
    			AppLogService.error( e );
    		}

    		if ( ( image == null ) &&  ( strFilename != null ) && ( !strFilename.equals( EMPTY_STRING ) ) )
    		{
    			 FormError formError = new FormError(  );
    			 formError.setErrorMessage( I18nService.getLocalizedString( MESSAGE_ERROR_NOT_AN_IMAGE, request.getLocale(  ) ) );
    			 formError.setTitleQuestion( this.getTitle(  ) );
    			 return formError;
    		}
            //Add the thumbnail to the response list
            
            /*if ( ( strFilename != null ) && ( !strFilename.equals( EMPTY_STRING ) ) )
            {
            	 byValueEntry = ImageUtil.resizeImage( byValueEntry
                 		, String.valueOf( this.getFields( ).get( 1 ).getWidth( ) )
                 		, String.valueOf( this.getFields( ).get( 1 ).getHeight( ) )
                 		, INTEGER_QUALITY_MAXIMUM );
            }       
            
            response = new Response(  );
            response.setEntry( this );
            response.setValueResponse( byValueEntry );
            response.setFileName( strFilename );
            response.setFileExtension( FilenameUtils.getExtension( strFilename ) );*/
            //listResponse.add( response );

            return null;
    	}
    	FormError formError = new FormError(  );
        formError.setMandatoryError( true );
        formError.setTitleQuestion( this.getTitle(  ) );

        return formError;
    }

    /**
     * The paginator who is use in the template modify of the entry
     * @param nItemPerPage Number of items to display per page
    * @param strBaseUrl The base Url for build links on each page link
    * @param strPageIndexParameterName The parameter name for the page index
    * @param strPageIndex The current page index
     * @return the paginator who is use in the template modify of the entry
     */
    public Paginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
        String strPageIndex )
    {
        return new Paginator( this.getFields(  ).get( 0 ).getRegularExpressionList(  ), nItemPerPage, strBaseUrl,
            strPageIndexParameterName, strPageIndex );
    }

    /**
     * return the list of regular expression whose not associate to the entry
     * @param entry the entry
     * @param plugin the plugin
     * @return the list of regular expression whose not associate to the entry
     */
    public ReferenceList getReferenceListRegularExpression( IEntry entry, Plugin plugin )
    {
        ReferenceList refListRegularExpression = null;

        if ( RegularExpressionService.getInstance(  ).isAvailable(  ) )
        {
            refListRegularExpression = new ReferenceList(  );

            List<RegularExpression> listRegularExpression = RegularExpressionService.getInstance(  )
                                                                                    .getAllRegularExpression(  );

            for ( RegularExpression regularExpression : listRegularExpression )
            {
                if ( !entry.getFields(  ).get( 0 ).getRegularExpressionList(  ).contains( regularExpression ) )
                {
                    refListRegularExpression.addItem( regularExpression.getIdExpression(  ),
                        regularExpression.getTitle(  ) );
                }
            }
        }

        return refListRegularExpression;
    }

    /**
     * Get the response value  associate to the entry  to export in the file export
     * @param response the response associate to the entry
     * @param locale the locale
     * @param request the request
     * @return  the response value  associate to the entry  to export in the file export
     */
    public String getResponseValueForExport( HttpServletRequest request, Response response, Locale locale )
    {
        return AppPathService.getBaseUrl( request ) + JSP_DOWNLOAD_FILE + "?id_response=" + response.getIdResponse(  );
    }

    /**
     * Get the response value  associate to the entry  to write in the recap
     * @param response the response associate to the entry
     * @param locale the locale
     * @param request the request
     * @return the response value  associate to the entry  to write in the recap
     */
    public String getResponseValueForRecap( HttpServletRequest request, Response response, Locale locale )
    {
        if ( response.getFileName(  ) != null )
        {
            return response.getFileName(  );
        }
        else
        {
            return EMPTY_STRING;
        }
    }

    @Override
    public LocalizedPaginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
        String strPageIndex, Locale locale )
    {
        return new LocalizedPaginator( this.getFields(  ).get( 0 ).getRegularExpressionList(  ), nItemPerPage,
            strBaseUrl, strPageIndexParameterName, strPageIndex, locale );
    }
    
    /**
     * toStringValue should stay <code>null</code>.
     */
    @Override
    public void setResponseToStringValue( Response response, Locale locale )
    {
    	// nothing - null is default
    }
    
    public boolean isFile(  )
    {
    	return true;
    }
}
