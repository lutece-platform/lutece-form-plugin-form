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
package fr.paris.lutece.plugins.form.service.parameter;

import fr.paris.lutece.plugins.form.business.parameter.EntryParameterHome;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;


/**
 *
 * DirectoryParameterService
 *
 */
public final class EntryParameterService
{
    private static final String BEAN_ENTRY_PARAMETER_SERVICE = "form.entryParameterService";

    /**
     * Get the instance of the service
     * @return the instance of the service
     */
    public static EntryParameterService getService(  )
    {
        return SpringContextService.getBean( BEAN_ENTRY_PARAMETER_SERVICE );
    }

    /**
     * Find all directory parameters
     * @return a {@link ReferenceList}
     */
    public ReferenceList findAll(  )
    {
        return EntryParameterHome.findAll( FormUtils.getPlugin(  ) );
    }

    /**
    * Load the parameter value
    * @param strParameterKey the parameter key
    * @return The parameter value
    */
    public ReferenceItem findByKey( String strParameterKey )
    {
        return EntryParameterHome.findByKey( strParameterKey, FormUtils.getPlugin(  ) );
    }

    /**
     * Update the parameter value
     * @param param A reference item contain the association key/value to
     *            update. The key must be in the code parameter of the reference
     *            item, and the value in the value parameter
     */
    public void update( ReferenceItem param )
    {
        EntryParameterHome.update( param, FormUtils.getPlugin(  ) );
    }
}
