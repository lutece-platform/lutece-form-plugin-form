/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.plugins.genericattributes.business.Response;

import java.sql.Timestamp;
import java.util.List;


/**
 *
 * class FormResponse
 *
 */
public class FormSubmit
{
    private int _nIdFormSubmit;
    private Timestamp _tDateResponse;
    private String _strIp;
    private Form _form;
    private List<Response> _listResponse;

    /**
     *
     * @return the form associate to the response
     */
    public Form getForm( )
    {
        return _form;
    }

    /**
     * set the form associate to the response
     * @param form the form associate to the response
     */
    public void setForm( Form form )
    {
        this._form = form;
    }

    /**
     * return the id of form resonse
     * @return the id of form resonse
     */
    public int getIdFormSubmit(  )
    {
        return _nIdFormSubmit;
    }

    /**
     * set the id of form resonse
     * @param idFormResponse the id of form resonse
     */
    public void setIdFormSubmit( int idFormResponse )
    {
        _nIdFormSubmit = idFormResponse;
    }

    /**
     *
     * @return the ip adress of the response
     */
    public String getIp(  )
    {
        return _strIp;
    }

    /**
     * set the ip adress of the response
     * @param ip the ip adress of the response
     */
    public void setIp( String ip )
    {
        _strIp = ip;
    }

    /**
     *  return the response date
     * @return the response date
     */
    public Timestamp getDateResponse(  )
    {
        return _tDateResponse;
    }

    /**
     * set the response date
     * @param dateResponse the response date
     */
    public void setDateResponse( Timestamp dateResponse )
    {
        _tDateResponse = dateResponse;
    }

    /**
     *
     * @return the list of response associate to the form submit
     */
    public List<Response> getListResponse(  )
    {
        return _listResponse;
    }

    /**
     * set the list of response associate to the form submit
     * @param listResponse the list of response associate to the form submit
     */
    public void setListResponse( List<Response> listResponse )
    {
        _listResponse = listResponse;
    }
}
