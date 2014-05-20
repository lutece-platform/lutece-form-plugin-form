/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.form.service.entrytype;

import fr.paris.lutece.plugins.form.service.upload.FormAsynchronousUploadHandler;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.MandatoryError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeUpload;
import fr.paris.lutece.plugins.genericattributes.service.upload.IGAAsyncUploadHandler;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionService;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.filesystem.FileSystemUtil;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * class EntryTypeFile
 *
 */
public class EntryTypeFile extends AbstractEntryTypeUpload
{
    private static final String JSP_DOWNLOAD_FILE = "jsp/admin/plugins/form/DoDownloadFile.jsp";
    private static final String TEMPLATE_CREATE = "admin/plugins/form/entries/create_entry_type_file.html";
    private static final String TEMPLATE_MODIFY = "admin/plugins/form/entries/modify_entry_type_file.html";
    private static final String TEMPLATE_HTML_CODE = "skin/plugins/form/entries/html_code_entry_type_file.html";

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
    public GenericAttributeError getResponseData( Entry entry, HttpServletRequest request, List<Response> listResponse,
        Locale locale )
    {
        List<FileItem> listFilesSource = null;

        if ( request instanceof MultipartHttpServletRequest )
        {
            List<FileItem> asynchronousFileItems = getFileSources( entry, request );

            if ( asynchronousFileItems != null )
            {
                listFilesSource = asynchronousFileItems;
            }

            GenericAttributeError formError = null;

            if ( ( listFilesSource != null ) && !listFilesSource.isEmpty(  ) )
            {
                formError = this.checkResponseData( entry, listFilesSource, locale, request );

                if ( formError != null )
                {
                    // Add the response to the list in order to have the error message in the page
                    Response response = new Response(  );
                    response.setEntry( entry );
                    listResponse.add( response );
                }

                for ( FileItem fileItem : listFilesSource )
                {
                    String strFilename = ( fileItem != null ) ? FileUploadService.getFileNameOnly( fileItem )
                                                              : StringUtils.EMPTY;

                    // Add the file to the response list
                    Response response = new Response(  );
                    response.setEntry( entry );

                    if ( ( fileItem != null ) && ( fileItem.get(  ) != null ) &&
                            ( fileItem.getSize(  ) < Integer.MAX_VALUE ) )
                    {
                        PhysicalFile physicalFile = new PhysicalFile(  );
                        physicalFile.setValue( fileItem.get(  ) );

                        File file = new File(  );
                        file.setPhysicalFile( physicalFile );
                        file.setTitle( strFilename );
                        file.setSize( (int) fileItem.getSize(  ) );
                        file.setMimeType( FileSystemUtil.getMIMEType( strFilename ) );

                        response.setFile( file );
                    }

                    listResponse.add( response );

                    String strMimeType = StringUtils.isBlank( strFilename ) ? FileSystemUtil.getMIMEType( strFilename )
                                                                            : StringUtils.EMPTY;
                    List<RegularExpression> listRegularExpression = entry.getFields(  ).get( 0 )
                                                                         .getRegularExpressionList(  );

                    if ( StringUtils.isNotBlank( strMimeType ) && ( listRegularExpression != null ) &&
                            ( listRegularExpression.size(  ) != 0 ) &&
                            RegularExpressionService.getInstance(  ).isAvailable(  ) )
                    {
                        for ( RegularExpression regularExpression : listRegularExpression )
                        {
                            if ( !RegularExpressionService.getInstance(  ).isMatches( strMimeType, regularExpression ) )
                            {
                                formError = new GenericAttributeError(  );
                                formError.setMandatoryError( false );
                                formError.setTitleQuestion( entry.getTitle(  ) );
                                formError.setErrorMessage( regularExpression.getErrorMessage(  ) );
                            }
                        }
                    }
                }
            }
            else if ( entry.isMandatory(  ) )
            {
                formError = new MandatoryError( entry, locale );

                Response response = new Response(  );
                response.setEntry( entry );
                listResponse.add( response );
            }

            return formError;
        }

        return new MandatoryError( entry, locale );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IGAAsyncUploadHandler getAsynchronousUploadHandler(  )
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
}
