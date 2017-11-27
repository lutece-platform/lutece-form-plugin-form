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
package fr.paris.lutece.plugins.form.business.iteration;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Response;

/**
 * Object which represent Responses for an Entry for an iteration
 */
public class IterationResponse
{
    private final int _nIdEntry;
    private List<Response> _listEntryResponses = new ArrayList<>( );

    /**
     * Constructor
     * 
     * @param nIdEntry
     *            the id of the entry where the IterationResponse belong to
     */
    public IterationResponse( int nIdEntry )
    {
        _nIdEntry = nIdEntry;
    }

    /**
     * Return the id of the Entry which the IterationResponse belong to
     * 
     * @return the id of the Entry which the IterationResponse belong to
     */
    public int getIdEntry( )
    {
        return _nIdEntry;
    }

    /**
     * Return the responses of the entry
     * 
     * @return the responses of the entry
     */
    public List<Response> getEntryResponses( )
    {
        return _listEntryResponses;
    }

    /**
     * Set the responses of the entry
     * 
     * @param entryResponses
     *            the response of the entry
     */
    public void setEntryResponses( List<Response> entryResponses )
    {
        _listEntryResponses = entryResponses;
    }

    /**
     * Tell if the entry of the IterationResponse has response or not
     * 
     * @return true if the entry has response false otherwise
     */
    public boolean hasResponse( )
    {
        boolean bEntryHasResponse = Boolean.FALSE;

        if ( _listEntryResponses != null && !_listEntryResponses.isEmpty( ) )
        {
            for ( Response response : _listEntryResponses )
            {
                if ( StringUtils.isNotBlank( response.getResponseValue( ) ) )
                {
                    bEntryHasResponse = Boolean.TRUE;
                }
            }
        }

        return bEntryHasResponse;
    }
}
