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
import fr.paris.lutece.plugins.form.utils.StringUtil;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;


/**
 *
 * class Response
 *
 */
public class Response
{
    private int _nIdResponse;
    private String _strToStringValueResponse;
    private IEntry _entry;
    private Field _field;
    private FormSubmit _formSubmit;
    private File _file;
    private String _strResponseValue;

    /**
     * @deprecated The response value is stored in String
     * @since 2.4.8
     * @see #_strResponseValue
     */
    private byte[] _byValueResponse;

    /**
     * @deprecated The file is stored in class File
     * @since 2.4.8
     * @see {@link File}
     */
    private String _strFileName;

    /**
     * @deprecated The file is stored in class File
     * @since 2.4.8
     * @see {@link File}
     */
    private String _strFileExtension;

    /**
     *
     * @return the form submit of the response
     */
    public FormSubmit getFormSubmit(  )
    {
        return _formSubmit;
    }

    /**
     * the form submit of the response
     * @param formSubmit the form submit of the response
     */
    public void setFormSubmit( FormSubmit formSubmit )
    {
        _formSubmit = formSubmit;
    }

    /**
    *
    * @return the question associate to the response
    */
    public IEntry getEntry(  )
    {
        return _entry;
    }

    /**
     * set the question associate to the response
     * @param entry the question associate to the response
     */
    public void setEntry( IEntry entry )
    {
        _entry = entry;
    }

    /**
     *
     * @return the id of the response
     */
    public int getIdResponse(  )
    {
        return _nIdResponse;
    }

    /**
     * set the id of the response
     * @param idResponse the id of the response
     */
    public void setIdResponse( int idResponse )
    {
        _nIdResponse = idResponse;
    }

    /**
     * get the field associate to the response
     * @return the field associate to the response
     */
    public Field getField(  )
    {
        return _field;
    }

    /**
     * set the field associate to the response
     * @param field field
     */
    public void setField( Field field )
    {
        _field = field;
    }

    /**
     * return the string value response
     * @return the string value of the response
     */
    public String getToStringValueResponse(  )
    {
        if ( _strToStringValueResponse != null )
        {
            return _strToStringValueResponse;
        }

        return _strResponseValue;
    }

    /**
    * set the string value response
    * @param strValueResponse the string value of the response
    */
    public void setToStringValueResponse( String strValueResponse )
    {
        _strToStringValueResponse = strValueResponse;
    }

    /**
     * Set file
     * @param file the file
     */
    public void setFile( File file )
    {
        _file = file;
    }

    /**
     * Get file
     * @return the file
     */
    public File getFile(  )
    {
        return _file;
    }

    /**
     * Set the response value
     * @param strResponseValue the response value
     */
    public void setResponseValue( String strResponseValue )
    {
        _strResponseValue = strResponseValue;
    }

    /**
     * Get the response value
     * @return the response value
     */
    public String getResponseValue(  )
    {
        return _strResponseValue;
    }

    // DEPRECATED METHODS

    /**
     * 
     * @return the value of the response
     * @deprecated The response is now in String and not in byte
     * @since 2.4.8
     * @see File
     */
    public byte[] getValueResponse(  )
    {
        if ( _byValueResponse != null )
        {
            return _byValueResponse;
        }

        // If the response has a file, then return the content of the file
        if ( ( _file != null ) && ( _file.getPhysicalFile(  ) != null ) &&
                ( _file.getPhysicalFile(  ).getValue(  ) != null ) )
        {
            return _file.getPhysicalFile(  ).getValue(  );
        }

        // Otherwise, return the content of the response value
        return StringUtil.convertToByte( _strResponseValue );
    }

    /**
     * set the value of the response
     * @param valueResponse the value of the response
     * @deprecated The response is now in String and not in byte
     * @since 2.4.8
     * @see File
     */
    public void setValueResponse( byte[] valueResponse )
    {
        _byValueResponse = valueResponse;
    }

    /**
     * the file name if the response value is a file
     * @param fileName the file name if the response value is a file
     * @deprecated the file name is now stored in class File
     * @since 2.4.8
     * @see File
     */
    public void setFileName( String fileName )
    {
        _strFileName = fileName;
    }

    /**
     * 
     * @return the file extension if the response value is a file
     * @deprecated the file is now stored in class File
     * @since 2.4.8
     * @see File
     */
    public String getFileExtension(  )
    {
        if ( StringUtils.isNotBlank( _strFileExtension ) )
        {
            return _strFileExtension;
        }

        if ( ( _file != null ) && StringUtils.isNotBlank( _file.getTitle(  ) ) )
        {
            FilenameUtils.getExtension( _file.getTitle(  ) );
        }

        return null;
    }

    /**
     * set the file extension if the response value is a file
     * @param fileExtension the file extension if the response value is a file
     * @deprecated the file is now stored in class File
     * @since 2.4.8
     * @see File
     */
    public void setFileExtension( String fileExtension )
    {
        _strFileExtension = fileExtension;
    }

    /**
     * the file name if the response value is a file
     * @return the file name if the response value is a file
     * @deprecated the file name is now stored in class File
     * @since 2.4.8
     * @see File
     */
    public String getFileName(  )
    {
        if ( StringUtils.isNotBlank( _strFileName ) )
        {
            return _strFileName;
        }

        if ( _file != null )
        {
            return _file.getTitle(  );
        }

        return null;
    }
}
