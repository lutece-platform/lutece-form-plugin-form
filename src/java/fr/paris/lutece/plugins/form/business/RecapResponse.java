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
package fr.paris.lutece.plugins.form.business;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.form.utils.EntryTypeGroupUtils;
import fr.paris.lutece.plugins.form.utils.FormConstants;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;

/**
 *
 * Response object with the identifier of the group which is belong to
 *
 */
public class RecapResponse
{
    private final Response _response;
    private final int _nIdGroup;
    private final String _strGroupTitle;
    
    // Constructor
    public RecapResponse( Response response )
    {
        _response = response;
        
        // Retrieve the identifier of the group where the entry of the response belong to
        int nIdGroupEntryResponse = EntryTypeGroupUtils.findIdGroupEntryResponse( response );
        _nIdGroup = nIdGroupEntryResponse;
        
        // Retrieve the title of the group if it exists
        String strEntryGrouptitle = StringUtils.EMPTY;
        if ( nIdGroupEntryResponse != FormConstants.DEFAULT_GROUP_NUMBER )
        {
            Entry entryGroup = EntryHome.findByPrimaryKey( nIdGroupEntryResponse );
            if ( entryGroup != null )
            {
                strEntryGrouptitle = entryGroup.getTitle( );
            }
        }
        _strGroupTitle = strEntryGrouptitle;
    }

    /**
     * Return the response
     * 
     * @return the response
     */
    public Response getResponse( )
    {
        return _response;
    }

    /**
     * Return the identifier of the group of the entry of the response
     * 
     * @return the nIdGroup
     */
    public int getIdGroup( )
    {
        return _nIdGroup;
    }

    /**
     * Return the title of the group where the Response belong to
     * 
     * @return the strGroupTitle
     */
    public String getGroupTitle( )
    {
        return _strGroupTitle;
    }
}
