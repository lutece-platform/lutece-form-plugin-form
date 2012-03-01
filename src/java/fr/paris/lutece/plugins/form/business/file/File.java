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
package fr.paris.lutece.plugins.form.business.file;

import fr.paris.lutece.plugins.form.business.physicalfile.PhysicalFile;
import fr.paris.lutece.util.filesystem.FileSystemUtil;

import org.apache.commons.lang.StringUtils;


/**
 *
 * class File
 *
 */
public class File
{
    private int _nIdFile;
    private PhysicalFile _physicalFile;
    private String _strTitle;
    private int _nSize;
    private String _strMimeType;

    /**
     * Get the id file
     * @return the id of the file
     */
    public int getIdFile(  )
    {
        return _nIdFile;
    }

    /**
     * Set the id of the file
     * @param nIdFile id of the file
     */
    public void setIdFile( int nIdFile )
    {
        _nIdFile = nIdFile;
    }

    /**
     * Get the title of the file
     * @return the title of the file
     */
    public String getTitle(  )
    {
        return _strTitle;
    }

    /**
     * Set the title of the file
     * @param strTitle the title of the file
     */
    public void setTitle( String strTitle )
    {
        _strTitle = strTitle;
    }

    /**
     * Get the size
     * @return the size of the file
     */
    public int getSize(  )
    {
        return _nSize;
    }

    /**
     * Set the size of the file
     * @param nSize the size of the file
     */
    public void setSize( int nSize )
    {
        _nSize = nSize;
    }

    /**
     * Get the mime type
     * @return the mime type of the file
     */
    public String getMimeType(  )
    {
        if ( StringUtils.isBlank( _strMimeType ) )
        {
            if ( StringUtils.isBlank( _strTitle ) )
            {
                return StringUtils.EMPTY;
            }

            return FileSystemUtil.getMIMEType( _strTitle );
        }

        return _strMimeType;
    }

    /**
     * Set the mime type
     * @param strMimeType the mime type
     */
    public void setMimeType( String strMimeType )
    {
        _strMimeType = strMimeType;
    }

    /**
     * Get the physical file
     * @return the PhysicalFile associate to the file
     */
    public PhysicalFile getPhysicalFile(  )
    {
        return _physicalFile;
    }

    /**
     * Set the PhysicalFile associate to the file
     * @param physicalFile physicalFile
     */
    public void setPhysicalFile( PhysicalFile physicalFile )
    {
        _physicalFile = physicalFile;
    }
}
