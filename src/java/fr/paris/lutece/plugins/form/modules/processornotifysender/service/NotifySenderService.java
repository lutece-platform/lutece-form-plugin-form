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
package fr.paris.lutece.plugins.form.modules.processornotifysender.service;

import fr.paris.lutece.plugins.form.business.FormSubmit;
import fr.paris.lutece.plugins.form.business.Response;
import fr.paris.lutece.plugins.form.modules.processornotifysender.utils.FileUtils;
import fr.paris.lutece.plugins.form.modules.processornotifysender.utils.ZipUtils;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.filesystem.FileSystemUtil;
import fr.paris.lutece.util.mail.FileAttachment;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * NotifySenderService
 *
 */
public final class NotifySenderService
{
    private static final String PROPERTY_ZIP_EXTENSION = "processornotifysender.zip.extension";
    private static final String PROPERTY_FILE_FOLDER_PATH = "processornotifysender.file.folder.path";
    private static final String PROPERTY_ZIP_FOLDER_PATH = "processornotifysender.zip.folder.path";
    private static final String PROPERTY_ATTACHMENT_NAME = "processornotifysender.attachment.name";

    /**
     * Private constructor
     */
    private NotifySenderService(  )
    {
    }

    /**
     * Send notification to the sender
     * @param formSubmit the form submit
     * @param strEmailSender the email of the sender
     * @param strSenderName the sender name
     * @param strSenderEmail the sender email
     * @param strSubject the subject
     * @param strMessage the message
     * @param bSendAttachments true if it must send the attachments, false otherwise
     */
    public void sendNotification( FormSubmit formSubmit, String strEmailSender, String strSenderName,
        String strSenderEmail, String strSubject, String strMessage, boolean bSendAttachments )
    {
        if ( bSendAttachments )
        {
            sendMultiPartNotification( formSubmit, strEmailSender, strSenderName, strSenderEmail, strSubject, strMessage );
        }
        else
        {
            try
            {
                // Send Mail
                if ( AppLogService.isDebugEnabled(  ) )
                {
                    AppLogService.debug( "NotifySenderService : Sending email to '" + strEmailSender + "'" );
                }

                MailService.sendMailHtml( strEmailSender, strSenderName, strSenderEmail, strSubject, strMessage );
            }
            catch ( Exception e )
            {
                AppLogService.error( " Error during Process > Notify sender : " + e.getMessage(  ) );
            }
        }
    }

    /**
     * Send notification to the sender with its file
     * @param formSubmit the form submit
     * @param strEmailSender the email of the sender
     * @param strSenderName the sender name
     * @param strSenderEmail the sender email
     * @param strSubject the subject
     * @param strMessage the message
     */
    private synchronized void sendMultiPartNotification( FormSubmit formSubmit, String strEmailSender,
        String strSenderName, String strSenderEmail, String strSubject, String strMessage )
    {
        int nIdFormSubmit = formSubmit.getIdFormSubmit(  );

        // Add the response files to a temporary folder
        for ( Response response : formSubmit.getListResponse(  ) )
        {
            if ( ( response.getFile(  ) != null ) && StringUtils.isNotBlank( response.getFile(  ).getTitle(  ) ) &&
                    ( response.getFile(  ).getPhysicalFile(  ) != null ) &&
                    ( response.getFile(  ).getPhysicalFile(  ).getValue(  ) != null ) )
            {
                if ( AppLogService.isDebugEnabled(  ) )
                {
                    AppLogService.debug( "NotifySenderService : Adding '" + response.getFile(  ).getTitle(  ) +
                        "' to folder '" + getFileFolderPath(  ) + "'" );
                }

                try
                {
                    FileUtils.addFileResponseToFolder( response, getFileFolderPath(  ) );
                }
                catch ( IOException e )
                {
                    AppLogService.error( "NotifySenderService : Cannot add file '" + response.getFile(  ).getTitle(  ) +
                        "' to folder '" + getFileFolderPath(  ) + "'" );
                }
            }
        }

        InputStream fis = null;

        try
        {
            // Zip the folder where the response files are stored
            if ( AppLogService.isDebugEnabled(  ) )
            {
                AppLogService.debug( "NotifySenderService : Ziping folder '" + getFileFolderPath(  ) + "'" );
            }

            ZipUtils.zipFolder( getFileFolderPath(  ), getZipFolderPath(  ), getZipName( nIdFormSubmit ) );

            // Add the zip to the email attachment
            if ( AppLogService.isDebugEnabled(  ) )
            {
                AppLogService.debug( "NotifySenderService : Reading zip '" + getZipFolderPath(  ) +
                    getZipName( nIdFormSubmit ) + "'" );
            }

            fis = new FileInputStream( getZipFolderPath(  ) + getZipName( nIdFormSubmit ) );

            byte[] data = IOUtils.toByteArray( fis );

            String strAttachmentName = AppPropertiesService.getProperty( PROPERTY_ATTACHMENT_NAME );
            List<FileAttachment> listAttachments = new ArrayList<FileAttachment>(  );
            listAttachments.add( new FileAttachment( strAttachmentName, data,
                    FileSystemUtil.getMIMEType( strAttachmentName ) ) );

            try
            {
                // Send Mail
                if ( AppLogService.isDebugEnabled(  ) )
                {
                    AppLogService.debug( "NotifySenderService : Sending multipart email to '" + strEmailSender + "'" );
                }

                MailService.sendMailMultipartHtml( strEmailSender, StringUtils.EMPTY, StringUtils.EMPTY, strSenderName,
                    strSenderEmail, strSubject, strMessage, null, listAttachments );
            }
            catch ( Exception e )
            {
                AppLogService.error( " Error during Process > Notify sender : " + e.getMessage(  ) );
            }
        }
        catch ( FileNotFoundException fnofe )
        {
            AppLogService.error( fnofe );
        }
        catch ( IOException ioe )
        {
            AppLogService.error( ioe );
        }
        finally
        {
            IOUtils.closeQuietly( fis );
        }

        // Clean folders
        FileUtils.cleanFolder( getFileFolderPath(  ) );
        FileUtils.cleanFolder( getZipFolderPath(  ) );
    }

    /**
     * Get the tmp folder in which the zip will be generated
     * @return the tmp folder
     */
    private String getFileFolderPath(  )
    {
        return AppPathService.getAbsolutePathFromRelativePath( AppPropertiesService.getProperty( 
                PROPERTY_FILE_FOLDER_PATH ) );
    }

    /**
     * Get the zip folder path
     * @return the zip folder path
     */
    private String getZipFolderPath(  )
    {
        return AppPathService.getAbsolutePathFromRelativePath( AppPropertiesService.getProperty( 
                PROPERTY_ZIP_FOLDER_PATH ) );
    }

    /**
     * Get the zip name
     * @param nIdFormSubmit the id form submit
     * @return the zip name
     */
    private String getZipName( int nIdFormSubmit )
    {
        return nIdFormSubmit + AppPropertiesService.getProperty( PROPERTY_ZIP_EXTENSION );
    }
}
