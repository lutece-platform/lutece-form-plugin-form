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
package fr.paris.lutece.plugins.form.modules.processornotifysender.utils;

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.string.StringUtil;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * ZipUtils
 *
 */
public final class ZipUtils
{
    private static final String MESSAGE_DELETE_ERROR = "Error deleting file or folder : ";

    /**
     * Private constructor
     */
    private ZipUtils( )
    {
    }

    /**
     * This Method zip a folder and all files contained in it.
     * 
     * @param strFolderToZip
     *            the folder to zip
     * @param strZipDestination
     *            the place for the created zip
     * @param strZipName
     *            the zip name
     */
    public static void zipFolder( String strFolderToZip, String strZipDestination, String strZipName )
    {
        FileUtils.createFolder( strZipDestination );

        // Delete zip if it exists
        File fileToDelete = new File( strZipDestination + strZipName );

        if ( fileToDelete.exists( ) )
        {
            if ( !fileToDelete.delete( ) )
            {
                AppLogService.error( MESSAGE_DELETE_ERROR + strZipDestination + strZipName );
            }
        }

        File folderToZip = new File( strFolderToZip );
        ZipOutputStream zos = null;

        try
        {
            BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream( strZipDestination + strZipName ) );
            zos = new ZipOutputStream( bos );

            zipFolder( folderToZip, zos, StringUtils.EMPTY );
        }
        catch( FileNotFoundException e )
        {
            AppLogService.error( e );
        }
        catch( IOException e )
        {
            AppLogService.error( e );
        }
        finally
        {
            IOUtils.closeQuietly( zos );
        }
    }

    /**
     * This Method zip a folder and all files contained in it.
     * 
     * @param dir
     *            folder to zip
     * @param zos
     *            zip object
     * @param path
     *            Current path in the zip
     * @exception IOException
     *                exception if there is an error
     */
    private static void zipFolder( File dir, ZipOutputStream zos, String path ) throws IOException
    {
        if ( ( dir != null ) && dir.isDirectory( ) )
        {
            zipFileInFolder( dir, zos, path );
        }
    }

    /**
     * Zip a given directory ( Recursive function )
     * 
     * @param dir
     *            Current directory to zip
     * @param zos
     *            Zip object
     * @param path
     *            Current path in the zip object
     * @exception IOException
     *                exception if there is an error
     */
    private static void zipFileInFolder( File dir, ZipOutputStream zos, String path ) throws IOException
    {
        if ( ( dir != null ) && dir.isDirectory( ) )
        {
            File [ ] entries = dir.listFiles( );
            int sz = entries.length;

            for ( int j = 0; j < sz; j++ )
            {
                if ( entries [j].isDirectory( ) )
                {
                    // FOLDER

                    // Add the new folder in the zip
                    ZipEntry ze = new ZipEntry( path + entries [j].getName( ) + File.separator );
                    zos.putNextEntry( ze );

                    // Call method zipFolder
                    File newDir = new File( dir.getAbsolutePath( ) + File.separator + entries [j].getName( ) );
                    zipFolder( newDir, zos, path + entries [j].getName( ) + File.separator );
                }
                else
                {
                    // FILE

                    // Read the current file
                    FileInputStream bis = null;

                    try
                    {
                        bis = new FileInputStream( entries [j].getAbsolutePath( ) );

                        // Create new entry for the zip
                        ZipEntry ze = new ZipEntry( path + StringUtil.replaceAccent( entries [j].getName( ) ) );
                        byte [ ] tab = IOUtils.toByteArray( bis );

                        // Add the new entry to the zip
                        zos.putNextEntry( ze );
                        zos.write( tab );
                        zos.closeEntry( );
                    }
                    catch( FileNotFoundException e )
                    {
                        AppLogService.error( e );
                    }
                    finally
                    {
                        IOUtils.closeQuietly( bis );
                    }
                }
            }
        }
    }
}
