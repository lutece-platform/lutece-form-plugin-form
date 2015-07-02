/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.form.business.outputprocessor;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for
 * FormProcessor objects
 */
public final class FormProcessorHome
{
    // Static variable pointed at the DAO instance
    private static IFormProcessorDAO _dao = SpringContextService.getBean( "form.formProcessorDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private FormProcessorHome(  )
    {
    }

    /**
     * store the FormProcessor Object which is specified in parameter
     * @param formProcessor FormProcessor Object
     * @param plugin the plugin
     */
    public static void create( FormProcessor formProcessor, Plugin plugin )
    {
        _dao.insert( formProcessor, plugin );
    }

    /**
     * remove the FormProcessor Object which is specified in parameter
     * @param formProcessor The form processor to remove
     * @param plugin The plugin
     */
    public static void remove( FormProcessor formProcessor, Plugin plugin )
    {
        _dao.delete( formProcessor, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Load the data of all the FormProcessor and returns them as a List
     * @param plugin the plugin
     * @return a List of FormProcessor Object
     */
    public static List<FormProcessor> getAllList( Plugin plugin )
    {
        return _dao.selectAll( plugin );
    }

    /**
     * Load the data of all the FormProcessor wich is associate to the form and
     * returns them as a list
     * @param nIdForm the id of the form
     * @param plugin the plugin
     * @return a List of FormProcessor Object
     */
    public static List<FormProcessor> getListByIdForm( int nIdForm, Plugin plugin )
    {
        return _dao.selectByIdForm( nIdForm, plugin );
    }
}
