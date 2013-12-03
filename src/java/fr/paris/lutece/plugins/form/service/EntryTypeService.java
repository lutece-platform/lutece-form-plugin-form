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
package fr.paris.lutece.plugins.form.service;

import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.EntryTypeHome;

import java.util.HashMap;
import java.util.Map;


/**
 *
 * EntryTypeService
 *
 */
public final class EntryTypeService
{
    private Map<String, EntryType> _mapEntryTypes;

    /**
     * Private constructor
     */
    private EntryTypeService(  )
    {
    }

    /**
     * Get the map of entry types
     * @return the map of entry types
     */
    public Map<String, EntryType> getMapEntryTypes(  )
    {
        if ( _mapEntryTypes == null )
        {
            initMapEntryTypes(  );
        }

        return _mapEntryTypes;
    }

    /**
     * Get the entry type given the class name
     * @param strBeanName the class name
     * @return an {@link EntryType}
     */
    public EntryType getEntryType( String strBeanName )
    {
        if ( _mapEntryTypes == null )
        {
            initMapEntryTypes(  );
        }

        return _mapEntryTypes.get( strBeanName );
    }

    /**
     * Init the map of entry types
     */
    private void initMapEntryTypes(  )
    {
        _mapEntryTypes = new HashMap<String, EntryType>(  );

        for ( EntryType entryType : EntryTypeHome.getList( FormPlugin.PLUGIN_NAME ) )
        {
            _mapEntryTypes.put( entryType.getBeanName(  ), entryType );
        }
    }
}
