/*
 * Copyright (c) 2002-2009, Mairie de Paris
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


/**
 *
 * class Response
 *
 */
public class Response
{
    private int _nIdResponse;
    private byte[] _byValueResponse;
    private String _strToStringValueResponse;
    private String _strFileName;
    private String _strFileExtension;
    private IEntry _entry;
    private Field _field;
    private FormSubmit _formSubmit;

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
     *
     * @return the value of the response
     */
    public byte[] getValueResponse(  )
    {
        return _byValueResponse;
    }

    /**
     * set the value of the response
     * @param valueResponse the value of the response
     */
    public void setValueResponse( byte[] valueResponse )
    {
        _byValueResponse = valueResponse;
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
        this._field = field;
    }

    /**
     * return the string value response
     * @return the string value of the response
     */
    public String getToStringValueResponse(  )
    {
        return _strToStringValueResponse;
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
     *
     * @return the file extension if the response value is a file
     */
    public String getFileExtension(  )
    {
        return _strFileExtension;
    }

    /**
     * set the file extension if the response value is a file
     * @param fileExtension the file extension if the response value is a file
     */
    public void setFileExtension( String fileExtension )
    {
        _strFileExtension = fileExtension;
    }

    /**
     * the file name if the response value is a file
     * @return the file name if the response value is a file
     */
    public String getFileName(  )
    {
        return _strFileName;
    }

    /**
     * the file name if the response value is a file
     * @param fileName the file name if the response value is a file
     */
    public void setFileName( String fileName )
    {
        _strFileName = fileName;
    }
}
