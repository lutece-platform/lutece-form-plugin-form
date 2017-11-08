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

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;

/**
 * The representation of an entry for an iteration
 */
public class IterationEntry
{
    private int _nIdEntryComputed;
    private int _nIdEntryOriginal;
    private int _nIdIterableParentGroup;
    private int _nIterationNumber;
    
    /**
     * Return the nIdEntryComputed
     * 
     * @return the nIdEntryComputed
     */
    public int getIdEntryComputed( )
    {
        return _nIdEntryComputed;
    }
    
    /**
     * Set the nIdEntryComputed
     * 
     * @param nIdEntryComputed
     *      the nIdEntryComputed to set
     */
    public void setIdEntryComputed( int nIdEntryComputed )
    {
        this._nIdEntryComputed = nIdEntryComputed;
    }

    /**
     * Return the nIdEntryOriginal
     * 
     * @return the nIdEntryOriginal
     */
    public int getIdEntryOriginal( )
    {
        return _nIdEntryOriginal;
    }

    /**
     * Set the nIdEntryOriginal
     * 
     * @param nIdEntryOriginal
     *      the nIdEntryOriginal to set
     */
    public void setIdEntryOriginal( int nIdEntryOriginal )
    {
        this._nIdEntryOriginal = nIdEntryOriginal;
    }

    /**
     * Return the nIdIterableParentGroup
     * 
     * @return the nIdIterableParentGroup
     */
    public int getIdIterableParentGroup( )
    {
        return _nIdIterableParentGroup;
    }

    /**
     * Set the nIdIterableParentGroup
     * 
     * @param nIdIterableParentGroup 
     *      the nIdIterableParentGroup to set
     */
    public void setIdIterableParentGroup( int nIdIterableParentGroup )
    {
        this._nIdIterableParentGroup = nIdIterableParentGroup;
    }

    /**
     * Return the nIterationNumber
     * 
     * @return the nIterationNumber
     */
    public int getIterationNumber( )
    {
        return _nIterationNumber;
    }

    /**
     * Set the nIterationNumber
     * 
     * @param nIterationNumber
     *      the nIterationNumber to set
     */
    public void setIterationNumber( int nIterationNumber )
    {
        this._nIterationNumber = nIterationNumber;
    }
    
    /**
     * Return the title of the entry of the iteration
     * 
     * @return the title of the entry or null if not found
     */
    public String getEntryTitle( )
    {
        return getTitle( _nIdEntryOriginal );
    }
    
    /**
     * Return the title of the parent entry of the iteration
     * 
     * @return the title of the parent entry or null if not found
     */
    public String getEntryParentTitle( )
    {
        return getTitle( _nIdIterableParentGroup );
    }
    
    /**
     * Return the title of an entry from its id
     * 
     * @param nIdEntry
     *      the id of the entry to retrieve the title from
     * @return the title of the entry or null if not found
     */
    private String getTitle( int nIdEntry )
    {
        Entry entry = EntryHome.findByPrimaryKey( nIdEntry );
        
        if ( entry != null )
        {
            return entry.getTitle( );
        }
        
        return null; 
    }
}
