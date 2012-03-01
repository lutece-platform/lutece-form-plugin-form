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
package fr.paris.lutece.plugins.form.modules.processornotifysender.utils;

import fr.paris.lutece.plugins.form.business.Response;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;


/**
 *
 * FileUtils
 *
 */
public final class FileUtils
{
    private static final String MESSAGE_DELETE_ERROR = "Error deleting file or folder : ";
    private static final String MESSAGE_CREATE_ERROR = "Error creating folder : ";

    /**
     * Private constructor
     */
    private FileUtils(  )
    {
    }

    /**
     * Method to clean a specific repository
     * @param strFolder name of repository
     */
    public static void cleanFolder( String strFolder )
    {
        File file = new File( strFolder );

        if ( file.isDirectory(  ) )
        {
            File[] entries = file.listFiles(  );
            int sz = entries.length;

            for ( int j = 0; j < sz; j++ )
            {
                cleanFolder( entries[j].getPath(  ) );
            }
        }

        if ( file.isFile(  ) )
        {
            if ( !file.delete(  ) )
            {
                AppLogService.error( MESSAGE_DELETE_ERROR + strFolder );
            }
        }
    }

    /**
     * This method extracts a specific file to a tmp folder
     * @param response the response of the form
     * @param strFolder the temporary folder for extraction
     * @throws IOException exception if the copy from byte[] to File has an error
     */
    public static void addFileResponseToFolder( Response response, String strFolder )
        throws IOException
    {
        if ( ( response.getFile(  ) != null ) && StringUtils.isNotBlank( response.getFile(  ).getTitle(  ) ) &&
                ( response.getFile(  ).getPhysicalFile(  ) != null ) &&
                ( response.getFile(  ).getPhysicalFile(  ).getValue(  ) != null ) )
        {
            // Create the folder first
            createFolder( strFolder );

            File file = new File( strFolder + response.getFile(  ).getTitle(  ) );

            // Delete the file if it exists
            if ( file.exists(  ) )
            {
                if ( !file.delete(  ) )
                {
                    AppLogService.error( MESSAGE_DELETE_ERROR + strFolder + response.getFile(  ).getTitle(  ) );
                }
            }

            org.apache.commons.io.FileUtils.writeByteArrayToFile( file,
                response.getFile(  ).getPhysicalFile(  ).getValue(  ) );
        }
    }

    /**
     * Method to create a specific folder
     * @param strPath repository path
     */
    public static void createFolder( String strPath )
    {
        File file = new File( strPath );

        if ( !file.isDirectory(  ) )
        {
            if ( !file.mkdirs(  ) )
            {
                AppLogService.error( MESSAGE_CREATE_ERROR + strPath );
            }
        }
    }
}
