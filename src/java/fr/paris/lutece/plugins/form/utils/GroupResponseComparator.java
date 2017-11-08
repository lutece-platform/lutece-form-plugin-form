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

import java.util.Comparator;

import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.genericattributes.business.Response;

/**
 * 
 * Comparator class for response which are belong to an entry of a multi group
 *
 */
public class GroupResponseComparator implements Comparator<Response>
{

    /**
     * The comparison of the responses is based on the id of their entries
     */
    @Override
    public int compare( Response response1, Response response2 )
    {
        Integer nIdEntryResponse1 = NumberUtils.INTEGER_ZERO;
        Integer nIdEntryResponse2 = NumberUtils.INTEGER_ZERO;

        if ( response1 != null && response1.getEntry( ) != null )
        {
            nIdEntryResponse1 = Integer.valueOf( response1.getEntry( ).getIdEntry( ) );
        }

        if ( response2 != null && response2.getEntry( ) != null )
        {
            nIdEntryResponse2 = Integer.valueOf( response2.getEntry( ).getIdEntry( ) );
        }

        return nIdEntryResponse1.compareTo( nIdEntryResponse2 );
    }

}
