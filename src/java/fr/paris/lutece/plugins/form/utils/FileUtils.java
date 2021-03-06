/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
package fr.paris.lutece.plugins.form.utils;

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.string.StringUtil;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * FileUtils
 *
 */
public final class FileUtils
{
    private static final String DOT = ".";
    private static final String UNDERSCORE = "_";
    private static final String REGEX = "\\W";

    /**
     * Private constructor
     */
    private FileUtils( )
    {
    }

    /**
     * Create a file
     * 
     * @param strFolderPath
     *            the folder path
     * @param strFileName
     *            the file name
     * @param strFileOutPut
     *            the file output
     * @param strEncoding
     *            the encoding
     * @throws IOException
     *             exception if there is an error during the deletion
     */
    public static void createFile( String strFolderPath, String strFileName, String strFileOutPut, String strEncoding ) throws IOException
    {
        File file = new File( strFolderPath + strFileName );

        // Delete the file if it exists
        deleteFile( strFolderPath, strFileName );

        org.apache.commons.io.FileUtils.writeStringToFile( file, strFileOutPut, strEncoding );
    }

    /**
     * Delete a file
     * 
     * @param strFolderPath
     *            the folder path
     * @param strFileName
     *            the file name
     * @throws IOException
     *             exception if there is an error during the deletion
     */
    public static void deleteFile( String strFolderPath, String strFileName ) throws IOException
    {
        File file = new File( strFolderPath + strFileName );

        if ( file.exists( ) )
        {
            if ( !file.delete( ) )
            {
                throw new IOException( "ERROR when deleting the file or folder " + strFolderPath + strFileName );
            }
        }
    }

    /**
     * Build the file name
     * 
     * @param strFileName
     *            the name of the file
     * @param strFormatExtension
     *            the format extension
     * @return the file name
     */
    public static String buildFileName( String strFileName, String strFormatExtension )
    {
        if ( StringUtils.isNotBlank( strFileName ) )
        {
            String strFullName = StringUtil.replaceAccent( strFileName ).replaceAll( REGEX, UNDERSCORE );

            if ( StringUtils.isNotBlank( strFormatExtension ) )
            {
                return strFullName + DOT + strFormatExtension;
            }

            return strFullName;
        }

        return StringUtils.EMPTY;
    }

    /**
     * Read the last line from the given file
     * 
     * @param strFile
     *            the file absolute path (ex : /home/filetopath/file.txt)
     * @return the last line, an empty string if the file does not exists
     */
    public static String readLastLine( String strFile )
    {
        FileInputStream in = null;

        try
        {
            in = new FileInputStream( strFile );
        }
        catch( FileNotFoundException e )
        {
            return StringUtils.EMPTY;
        }

        String strLastLine = StringUtils.EMPTY;
        BufferedReader br = new BufferedReader( new InputStreamReader( in ) );
        String strTmp = null;

        try
        {
            while ( br.ready( ) )
            {
                strTmp = br.readLine( );
            }
        }
        catch( IOException e )
        {
            AppLogService.error( e.getMessage( ), e );
        }
        finally
        {
            IOUtils.closeQuietly( in );
        }

        strLastLine = strTmp;

        return strLastLine;
    }

    /**
     * Write to the given file
     * 
     * @param strContent
     *            the content to write
     * @param strFile
     *            the file
     */
    public static void writeToFile( String strContent, String strFile )
    {
        FileWriter fw = null;

        try
        {
            fw = new FileWriter( strFile, false );
            fw.write( strContent );
        }
        catch( IOException e )
        {
            AppLogService.error( e.getMessage( ), e );
        }
        finally
        {
            IOUtils.closeQuietly( fw );
        }
    }
}
