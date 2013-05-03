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
package fr.paris.lutece.plugins.form.service.file;

import fr.paris.lutece.plugins.form.business.file.File;
import fr.paris.lutece.plugins.form.business.file.FileHome;
import fr.paris.lutece.plugins.form.service.physicalfile.PhysicalFileService;
import fr.paris.lutece.plugins.form.utils.FormUtils;


/**
 *
 * FileService
 *
 */
public class FileService
{
    /**
     * The name of the bean of this service
     */
    public static final String BEAN_SERVICE = "form.fileService";
    private PhysicalFileService _physicalFileService;

    /**
     * Set the physical file service
     * @param physicalFileService the physical file service
     */
    public void setPhysicalFileService( PhysicalFileService physicalFileService )
    {
        _physicalFileService = physicalFileService;
    }

    /**
    * Creation of an instance of record file
    * @param file The instance of the file which contains the informations to store
    * @return the id of the file after creation
    */
    public int create( File file )
    {
        if ( ( file != null ) && ( file.getPhysicalFile(  ) != null ) )
        {
            file.getPhysicalFile(  ).setIdPhysicalFile( _physicalFileService.create( file.getPhysicalFile(  ) ) );
        }

        return FileHome.create( file, FormUtils.getPlugin(  ) );
    }

    /**
     * Update of file which is specified in parameter
     * @param  file The instance of the  record file which contains the informations to update
     */
    public void update( File file )
    {
        if ( ( file != null ) && ( file.getPhysicalFile(  ) != null ) )
        {
            _physicalFileService.update( file.getPhysicalFile(  ) );
        }

        FileHome.update( file, FormUtils.getPlugin(  ) );
    }

    /**
     * Delete the file whose identifier is specified in parameter
     * @param nIdFile The identifier of the record file
     */
    public void remove( int nIdFile )
    {
        File file = findByPrimaryKey( nIdFile, false );

        if ( ( file != null ) && ( file.getPhysicalFile(  ) != null ) )
        {
            _physicalFileService.remove( file.getPhysicalFile(  ).getIdPhysicalFile(  ) );
        }

        FileHome.remove( nIdFile, FormUtils.getPlugin(  ) );
    }

    /**
     * Returns an instance of a file whose identifier is specified in parameter
     * @param nKey The file primary key
     * @param bGetFileData True to get the physical file of the file, false
     *            otherwise
     * @return an instance of file
     */
    public File findByPrimaryKey( int nKey, boolean bGetFileData )
    {
        File file = FileHome.findByPrimaryKey( nKey, FormUtils.getPlugin(  ) );

        if ( bGetFileData && ( file != null ) && ( file.getPhysicalFile(  ) != null ) )
        {
            file.setPhysicalFile( _physicalFileService.findByPrimaryKey( file.getPhysicalFile(  ).getIdPhysicalFile(  ) ) );
        }

        return file;
    }
}
