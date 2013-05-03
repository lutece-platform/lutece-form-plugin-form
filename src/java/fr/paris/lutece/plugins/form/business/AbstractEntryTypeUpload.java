/*
 * Copyright (c) 2002-2012, Mairie de Paris
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

import fr.paris.lutece.plugins.form.business.file.File;
import fr.paris.lutece.plugins.form.service.file.FileService;
import fr.paris.lutece.plugins.form.service.upload.FormAsynchronousUploadHandler;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.filesystem.FileSystemUtil;
import fr.paris.lutece.util.url.UrlItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;


/**
 *
 * AbstractEntryTypeUpload
 *
 */
public abstract class AbstractEntryTypeUpload extends Entry
{
    // PARAMETERS
    protected static final String PARAMETER_ID_RESPONSE = "id_response";
    protected static final String PARAMETER_MAX_FILES = "max_files";
    protected static final String PARAMETER_FILE_MAX_SIZE = "file_max_size";
    protected static final String PARAMETER_EXPORT_BINARY = "export_binary";

    // PROPERTIES
    private static final String PROPERTY_MESSAGE_ERROR_UPLOADING_FILE_MAX_FILES = "form.message.error.uploading_file.max_files";
    private static final String PROPERTY_MESSAGE_ERROR_UPLOADING_FILE_FILE_MAX_SIZE = "form.message.error.uploading_file.file_max_size";
    private static final String PROPERTY_UPLOAD_FILE_DEFAULT_MAX_SIZE = "form.upload.file.default_max_size";

    // FIELDS
    private static final String FIELD_MAX_FILES = "form.createEntry.labelMaxFiles";
    private static final String FIELD_FILE_MAX_SIZE = "form.createEntry.labelFileMaxSize";

    // CONSTANTS
    protected static final String CONSTANT_MAX_FILES = "max_files";
    protected static final String CONSTANT_FILE_MAX_SIZE = "file_max_size";
    protected static final String CONSTANT_EXPORT_BINARY = "export_binary";
    protected static final String ALL = "*";
    protected static final String COMMA = ",";

    /**
     * Set the fields
     * @param request the HTTP request
     * @param listFields the list of fields to set
     */
    protected abstract void setFields( HttpServletRequest request, List<Field> listFields );

    /**
     * Check the response data for a single file item
     * @param fileItem the file item
     * @param locale the locale
     * @return form error if there is an error
     */
    protected abstract FormError checkResponseData( FileItem fileItem, Locale locale );

    /**
    * {@inheritDoc}
    */
    @Override
    public FormError canUploadFiles( List<FileItem> listUploadedFileItems, List<FileItem> listFileItemsToUpload,
        Locale locale )
    {
        /** 1) Check max files */
        Field fieldMaxFiles = FormUtils.findFieldByTitleInTheList( CONSTANT_MAX_FILES, getFields(  ) );

        // By default, max file is set at 1
        int nMaxFiles = 1;

        if ( ( fieldMaxFiles != null ) && StringUtils.isNotBlank( fieldMaxFiles.getValue(  ) ) &&
                StringUtils.isNumeric( fieldMaxFiles.getValue(  ) ) )
        {
            nMaxFiles = FormUtils.convertStringToInt( fieldMaxFiles.getValue(  ) );
        }

        if ( ( listUploadedFileItems != null ) && ( listFileItemsToUpload != null ) )
        {
            int nNbFiles = listUploadedFileItems.size(  ) + listFileItemsToUpload.size(  );

            if ( nNbFiles > nMaxFiles )
            {
                Object[] params = { nMaxFiles };
                String strMessage = I18nService.getLocalizedString( PROPERTY_MESSAGE_ERROR_UPLOADING_FILE_MAX_FILES,
                        params, locale );
                FormError formError = new FormError(  );
                formError.setMandatoryError( false );
                formError.setTitleQuestion( this.getTitle(  ) );
                formError.setErrorMessage( strMessage );

                return formError;
            }
        }

        /** 2) Check files size */
        Field fieldFileMaxSize = FormUtils.findFieldByTitleInTheList( CONSTANT_FILE_MAX_SIZE, getFields(  ) );
        int nMaxSize = FormUtils.CONSTANT_ID_NULL;

        if ( ( fieldFileMaxSize != null ) && StringUtils.isNotBlank( fieldFileMaxSize.getValue(  ) ) &&
                StringUtils.isNumeric( fieldFileMaxSize.getValue(  ) ) )
        {
            nMaxSize = FormUtils.convertStringToInt( fieldFileMaxSize.getValue(  ) );
        }

        // If no max size defined in the db, then fetch the default max size from the form.properties file
        if ( nMaxSize == FormUtils.CONSTANT_ID_NULL )
        {
            nMaxSize = AppPropertiesService.getPropertyInt( PROPERTY_UPLOAD_FILE_DEFAULT_MAX_SIZE, 5242880 );
        }

        // If nMaxSize == -1, then no size limit
        if ( ( nMaxSize != FormUtils.CONSTANT_ID_NULL ) && ( listFileItemsToUpload != null ) &&
                ( listUploadedFileItems != null ) )
        {
            boolean bHasFileMaxSizeError = false;
            List<FileItem> listFileItems = new ArrayList<FileItem>(  );
            listFileItems.addAll( listUploadedFileItems );
            listFileItems.addAll( listFileItemsToUpload );

            for ( FileItem fileItem : listFileItems )
            {
                if ( fileItem.getSize(  ) > nMaxSize )
                {
                    bHasFileMaxSizeError = true;

                    break;
                }
            }

            if ( bHasFileMaxSizeError )
            {
                Object[] params = { nMaxSize };
                String strMessage = I18nService.getLocalizedString( PROPERTY_MESSAGE_ERROR_UPLOADING_FILE_FILE_MAX_SIZE,
                        params, locale );
                FormError formError = new FormError(  );
                formError.setMandatoryError( false );
                formError.setTitleQuestion( this.getTitle(  ) );
                formError.setErrorMessage( strMessage );

                return formError;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForExport( HttpServletRequest request, Response response, Locale locale )
    {
        // Check whether the binaries must be exported or just displaying an URL to download the file
        if ( getFields(  ) == null )
        {
            setFields( FieldHome.getFieldListByIdEntry( getIdEntry(  ), FormUtils.getPlugin(  ) ) );
        }

        Field field = FormUtils.findFieldByTitleInTheList( CONSTANT_EXPORT_BINARY, getFields(  ) );

        if ( ( field != null ) && StringUtils.isNotBlank( field.getValue(  ) ) &&
                Boolean.valueOf( field.getValue(  ) ) )
        {
            if ( response.getFile(  ) != null )
            {
                FileService fileService = SpringContextService.getBean( FileService.BEAN_SERVICE );
                File file = fileService.findByPrimaryKey( response.getFile(  ).getIdFile(  ), true );

                if ( ( file != null ) && ( file.getPhysicalFile(  ) != null ) &&
                        ( file.getPhysicalFile(  ).getValue(  ) != null ) )
                {
                    String strPhysicalFile = Arrays.toString( file.getPhysicalFile(  ).getValue(  ) );

                    if ( StringUtils.isNotBlank( strPhysicalFile ) )
                    {
                        // Removing the square brackets ("[]") that "Arrays.toString" added
                        return strPhysicalFile.substring( 1, strPhysicalFile.length(  ) - 1 );
                    }
                }
            }

            return StringUtils.EMPTY;
        }

        UrlItem url = new UrlItem( FormUtils.getAdminBaseUrl( request ) + JSP_DOWNLOAD_FILE );
        url.addParameter( PARAMETER_ID_RESPONSE, response.getIdResponse(  ) );

        return url.getUrl(  );
    }

    // CHECKS

    /**
     * Check the entry data
     * @param request the HTTP request
     * @param locale the locale
     * @return the error message url if there is an error, an empty string otherwise
     */
    protected String checkEntryData( HttpServletRequest request, Locale locale )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strMaxFiles = request.getParameter( PARAMETER_MAX_FILES );
        String strFileMaxSize = request.getParameter( PARAMETER_FILE_MAX_SIZE );
        String strWidth = request.getParameter( PARAMETER_WIDTH );
        String strFieldError = StringUtils.EMPTY;

        if ( StringUtils.isBlank( strTitle ) )
        {
            strFieldError = FIELD_TITLE;
        }
        else if ( StringUtils.isBlank( strMaxFiles ) )
        {
            strFieldError = FIELD_MAX_FILES;
        }
        else if ( StringUtils.isBlank( strFileMaxSize ) )
        {
            strFieldError = FIELD_FILE_MAX_SIZE;
        }
        else if ( StringUtils.isBlank( strWidth ) )
        {
            strFieldError = FIELD_WIDTH;
        }

        if ( StringUtils.isNotBlank( strFieldError ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        if ( !StringUtils.isNumeric( strMaxFiles ) )
        {
            strFieldError = FIELD_MAX_FILES;
        }
        else if ( !StringUtils.isNumeric( strFileMaxSize ) )
        {
            strFieldError = FIELD_FILE_MAX_SIZE;
        }

        if ( !StringUtils.isNumeric( strWidth ) )
        {
            strFieldError = FIELD_WIDTH;
        }

        if ( StringUtils.isNotBlank( strFieldError ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_NUMERIC_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        return StringUtils.EMPTY;
    }

    /**
     * Check the record field data
     * @param listFilesSource the list of source files to upload
     * @param locale the locale
     * @param request the HTTP request
     * @return form error exception if there is an error
     */
    protected FormError checkResponseData( List<FileItem> listFilesSource, Locale locale, HttpServletRequest request )
    {
        // Check if the user can upload the file. The File is already uploaded in the asynchronous uploaded files map
        // Thus the list of files to upload is in the list of uploaded files
        FormError formError = canUploadFiles( listFilesSource, new ArrayList<FileItem>(  ), locale );

        if ( formError != null )
        {
            // The file has been uploaded to the asynchronous uploaded file map, so it should be deleted
            HttpSession session = request.getSession( false );

            if ( session != null )
            {
                FormAsynchronousUploadHandler.getHandler(  )
                                             .removeFileItem( Integer.toString( this.getIdEntry(  ) ),
                    session.getId(  ), listFilesSource.size(  ) - 1 );
            }

            return formError;
        }

        for ( FileItem fileSource : listFilesSource )
        {
            // Check mandatory attribute
            String strFilename = ( fileSource != null ) ? FileUploadService.getFileNameOnly( fileSource )
                                                        : StringUtils.EMPTY;

            if ( isMandatory(  ) && StringUtils.isBlank( strFilename ) )
            {
                return new MandatoryFormError( this, locale );
            }

            String strMimeType = FileSystemUtil.getMIMEType( strFilename );

            // Check mime type with regular expressions
            List<RegularExpression> listRegularExpression = this.getFields(  ).get( 0 ).getRegularExpressionList(  );

            if ( StringUtils.isNotBlank( strFilename ) && ( listRegularExpression != null ) &&
                    !listRegularExpression.isEmpty(  ) && RegularExpressionService.getInstance(  ).isAvailable(  ) )
            {
                for ( RegularExpression regularExpression : listRegularExpression )
                {
                    if ( !RegularExpressionService.getInstance(  ).isMatches( strMimeType, regularExpression ) )
                    {
                        formError = new FormError(  );
                        formError.setMandatoryError( false );
                        formError.setTitleQuestion( this.getTitle(  ) );
                        formError.setErrorMessage( regularExpression.getErrorMessage(  ) );

                        return formError;
                    }
                }
            }

            // Specific Check from each entry types
            formError = checkResponseData( fileSource, locale );

            if ( formError != null )
            {
                return formError;
            }
        }

        return null;
    }

    // FINDERS

    /**
     * Get the file source from the session
     * @param request the HttpServletRequest
     * @param session the HttpSession
     * @return the file item
     */
    protected List<FileItem> getFileSources( HttpServletRequest request )
    {
        HttpSession session = request.getSession( false );

        if ( session != null )
        {
            // check the file in session - it might no be deleted
            return FormAsynchronousUploadHandler.getHandler(  )
                                                .getFileItems( Integer.toString( getIdEntry(  ) ), session.getId(  ) );
        }

        return null;
    }

    // SET

    /**
     * Set the list of fields
     * @param request the HTTP request
     */
    protected void setFields( HttpServletRequest request )
    {
        List<Field> listFields = new ArrayList<Field>(  );
        listFields.add( buildDefaultField( request ) );
        listFields.add( buildFieldMaxFiles( request ) );
        listFields.add( buildFieldFileMaxSize( request ) );
        listFields.add( buildExportBinaryField( request ) );

        setFields( request, listFields );

        this.setFields( listFields );
    }

    // PRIVATE METHODS

    /**
     * Build the field for max files
     * @param request the HTTP request
     * @return the field
     */
    private Field buildFieldMaxFiles( HttpServletRequest request )
    {
        String strMaxFiles = request.getParameter( PARAMETER_MAX_FILES );
        int nMaxFiles = FormUtils.convertStringToInt( strMaxFiles );
        Field fieldMaxFiles = FormUtils.findFieldByTitleInTheList( CONSTANT_MAX_FILES, getFields(  ) );

        if ( fieldMaxFiles == null )
        {
            fieldMaxFiles = new Field(  );
        }

        fieldMaxFiles.setParentEntry( this );
        fieldMaxFiles.setTitle( CONSTANT_MAX_FILES );
        fieldMaxFiles.setValue( Integer.toString( nMaxFiles ) );

        return fieldMaxFiles;
    }

    /**
     * Build the field for file max size
     * @param request the HTTP request
     * @return the field
     */
    private Field buildFieldFileMaxSize( HttpServletRequest request )
    {
        String strFileMaxSize = request.getParameter( PARAMETER_FILE_MAX_SIZE );
        int nFileMaxSize = FormUtils.convertStringToInt( strFileMaxSize );
        Field fieldMaxFiles = FormUtils.findFieldByTitleInTheList( CONSTANT_FILE_MAX_SIZE, getFields(  ) );

        if ( fieldMaxFiles == null )
        {
            fieldMaxFiles = new Field(  );
        }

        fieldMaxFiles.setParentEntry( this );
        fieldMaxFiles.setTitle( CONSTANT_FILE_MAX_SIZE );
        fieldMaxFiles.setValue( Integer.toString( nFileMaxSize ) );

        return fieldMaxFiles;
    }

    /**
     * Build the default field
     * @param request the HTTP request
     * @return the default field
     */
    private Field buildDefaultField( HttpServletRequest request )
    {
        String strWidth = request.getParameter( PARAMETER_WIDTH );
        int nWidth = FormUtils.convertStringToInt( strWidth );

        Field field = FormUtils.findFieldByTitleInTheList( null, getFields(  ) );

        if ( field == null )
        {
            field = new Field(  );
        }

        field.setParentEntry( this );
        field.setWidth( nWidth );

        return field;
    }

    /**
     * Build the field for exporting the binary
     * @param request the HTTP request
     * @return the field
     */
    private Field buildExportBinaryField( HttpServletRequest request )
    {
        String strExportBinary = request.getParameter( PARAMETER_EXPORT_BINARY );
        Field field = FormUtils.findFieldByTitleInTheList( CONSTANT_EXPORT_BINARY, getFields(  ) );

        if ( field == null )
        {
            field = new Field(  );
        }

        field.setParentEntry( this );
        field.setTitle( CONSTANT_EXPORT_BINARY );
        field.setValue( Boolean.toString( StringUtils.isNotBlank( strExportBinary ) ) );

        return field;
    }
}
