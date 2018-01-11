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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.form.service.entrytype.EntryTypeGroup;
import fr.paris.lutece.plugins.form.utils.EntryTypeGroupUtils;
import fr.paris.lutece.plugins.form.utils.FormConstants;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.util.mvc.utils.MVCMessage;

/**
 * Object which represent the iterations of a group
 */
public class IterationGroup
{
    private final int _nNbMaxIteration;
    private final int _nNbMinIteration;
    private Map<Integer, List<IterationResponse>> _mapIterationResponses = new LinkedHashMap<>( );
    private List<MVCMessage> _listErrorMessages = new ArrayList<>( );

    // Constructor
    public IterationGroup( Entry entry )
    {
        int nNbMaxIteration = FormConstants.DEFAULT_MINIMUM_ITERATION_NUMBER;
        int nNbMinIteration = FormConstants.DEFAULT_MINIMUM_ITERATION_NUMBER;

        if ( entry != null )
        {
            int nIdEntry = entry.getIdEntry( );
            nNbMaxIteration = EntryTypeGroupUtils.getEntryMaxIterationAllowed( nIdEntry );

            int nMinNumberOfIteration = getEntryMinIterationAllowed( nIdEntry );
            nNbMinIteration = nMinNumberOfIteration;

            for ( int nIterationNumber = NumberUtils.INTEGER_ONE; nIterationNumber <= nMinNumberOfIteration; nIterationNumber++ )
            {
                _mapIterationResponses.put( nIterationNumber, new ArrayList<IterationResponse>( ) );
            }
        }

        _nNbMaxIteration = nNbMaxIteration;
        _nNbMinIteration = nNbMinIteration;
    }

    /**
     * Return the maximum number of iteration allow for the group
     * 
     * @return the maximum number of iteration allow for the group
     */
    public int getNbMaxIteration( )
    {
        return _nNbMaxIteration;
    }

    /**
     * Return the minimum number of iteration necessary for the group
     * 
     * @return the minimum number of iteration necessary for the group
     */
    public int getNbMinIteration( )
    {
        return _nNbMinIteration;
    }

    /**
     * Return the list of error messages
     * 
     * @return the list of error messages of the iteration group
     */
    public List<MVCMessage> getListErrorMessages( )
    {
        return _listErrorMessages;
    }

    /**
     * Reset the list of error messages for the iteration group
     */
    public void resetListErrorMessages( )
    {
        _listErrorMessages = new ArrayList<>( );
    }

    /**
     * Return the last iteration number used for the group
     * 
     * @return the iteration number used for the group
     */
    public int getLastIterationNumber( )
    {
        int nIterationNumber = NumberUtils.INTEGER_ONE;

        Set<Integer> setIterationNumber = _mapIterationResponses.keySet( );
        if ( setIterationNumber != null && !setIterationNumber.isEmpty( ) )
        {
            int nSetSize = setIterationNumber.size( );
            nIterationNumber = setIterationNumber.toArray( new Integer [ nSetSize] ) [nSetSize - NumberUtils.INTEGER_ONE];
        }

        return nIterationNumber;
    }

    /**
     * Return the number of iteration for the group
     * 
     * @return the number of iteration for the group
     */
    public int getIterationNumber( )
    {
        return _mapIterationResponses.size( );
    }

    /**
     * Remove an iteration to the group
     */
    public void removeIteration( int nIterationNumber )
    {
        _mapIterationResponses.remove( nIterationNumber );
    }

    /**
     * Add an iteration to the group
     */
    public void addIteration( )
    {
        _mapIterationResponses.put( getLastIterationNumber( ) + NumberUtils.INTEGER_ONE, new ArrayList<IterationResponse>( ) );
    }

    /**
     * Add Responses for the specified entry for the specified iteration
     * 
     * @param nIterationNumber
     *            the iteration number
     * @param nIdEntry
     *            the id of the entry to attached the list of Response
     * @param responseList
     *            the Response list of the Entry
     */
    public void addEntryResponses( int nIterationNumber, int nIdEntry, List<Response> responseList )
    {
        List<IterationResponse> listIterationResponse = _mapIterationResponses.get( nIterationNumber );

        // Create a new list of IterationResponse if it doesn't exist for the specified iteration
        if ( listIterationResponse == null )
        {
            listIterationResponse = new ArrayList<>( );
            _mapIterationResponses.put( nIterationNumber, listIterationResponse );
        }

        // Retrieve the IterationResponse form the list of existing IterationResponse
        IterationResponse iterationResponse = retrieveEntryIterationResponse( nIdEntry, listIterationResponse );

        // If there is no IterationResponse object for the given entry we will create a new one
        // and add it to the list of IterationResponse for the given iteration
        if ( iterationResponse == null )
        {
            iterationResponse = new IterationResponse( nIdEntry );
            listIterationResponse.add( iterationResponse );
        }

        // Update the list of response for the given entry
        iterationResponse.setEntryResponses( responseList );
    }

    /**
     * Return the IterationResponse object from a list of IterationResponse
     * 
     * @param nIdEntry
     *            the id of the entry to retrieve the list of IterationResponse from
     * @param listIterationResponse
     *            the list of IterationResponse to search from
     * @return the IterationResponse associated to the entry from the given list or null if not found
     */
    private IterationResponse retrieveEntryIterationResponse( int nIdEntry, List<IterationResponse> listIterationResponse )
    {
        IterationResponse iterationResponseResult = null;

        if ( listIterationResponse != null && !listIterationResponse.isEmpty( ) )
        {
            for ( IterationResponse iterationResponse : listIterationResponse )
            {
                if ( iterationResponse.getIdEntry( ) == nIdEntry )
                {
                    iterationResponseResult = iterationResponse;
                    break;
                }
            }
        }

        return iterationResponseResult;
    }

    /**
     * Return the set of all iteration number of the group
     * 
     * @return the set of all iteration number of the group
     */
    public Set<Integer> getSetIterationNumber( )
    {
        return _mapIterationResponses.keySet( );
    }

    /**
     * Return a boolean which indicate if the user filled at last one field for the specified iteration
     * 
     * @return true if the user filled at last one field for the specified iteration
     */
    public boolean fillingMadeOnIteration( int nIterationNumber )
    {
        boolean bFillingMade = Boolean.FALSE;

        List<IterationResponse> listIterationResponse = _mapIterationResponses.get( nIterationNumber );
        if ( listIterationResponse != null && !listIterationResponse.isEmpty( ) )
        {
            for ( IterationResponse iterationResponse : listIterationResponse )
            {
                if ( iterationResponse.hasResponse( ) )
                {
                    bFillingMade = Boolean.TRUE;
                    break;
                }
            }
        }

        return bFillingMade;
    }

    /**
     * Tell if the maximum number of iteration has been reached or not
     * 
     * @return true if the maximum number of iteration is reached false otherwise
     */
    public boolean isIterationLimitReached( )
    {
        return getIterationNumber( ) >= _nNbMaxIteration;
    }

    /**
     * Return the minimum number of iterations necessary for the entry. Return 0 if a problem occurred.
     * 
     * @param idEntry
     *            The id of the entry to find the minimum number of iterations necessary
     * @return the minimum number of iterations necessary for the entry return 0 if a problem occurred
     */
    public static int getEntryMinIterationAllowed( int idEntry )
    {
        return EntryTypeGroupUtils.findFieldValue( idEntry, EntryTypeGroup.CONSTANT_NB_MINIMUM_ITERATION, FormConstants.DEFAULT_MINIMUM_ITERATION_NUMBER );
    }
}
