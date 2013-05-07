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

import fr.paris.lutece.plugins.form.business.outputprocessor.FormProcessor;
import fr.paris.lutece.plugins.form.business.outputprocessor.FormProcessorHome;
import fr.paris.lutece.plugins.form.business.outputprocessor.IOutputProcessor;
import fr.paris.lutece.plugins.form.business.outputprocessor.IOutputProcessorSet;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * OutputProcessorService
 * 
 */
public class OutputProcessorService
{
    private static final String BEAN_MAP_OUTPUT_PROCESSOR = "form.outputProcessorSet";
    private static OutputProcessorService _singleton;
    private IOutputProcessorSet _OutputProcessorSet;

    /** Creates a new instance of DirectorySearchService */
    private OutputProcessorService( )
    {
        _OutputProcessorSet = SpringContextService.getBean( BEAN_MAP_OUTPUT_PROCESSOR );
    }

    /**
     * Get the instance of this service
     * @return The instance of this service
     */
    public static OutputProcessorService getInstance( )
    {
        if ( _singleton == null )
        {
            _singleton = new OutputProcessorService( );
        }

        return _singleton;
    }

    /**
     * Get the list of all processors
     * @return The list of all processors
     */
    public Collection<IOutputProcessor> getAllProcessors( )
    {
        return _OutputProcessorSet.getAllOutputProcessor( );
    }

    /**
     * Get processors associated with a given form
     * @param nIdForm The if of the form the processors must be associated with
     * @return The list of processors, or an empty list of none was found
     */
    public List<IOutputProcessor> getProcessorsByIdForm( int nIdForm )
    {
        List<IOutputProcessor> lisOutputProcessor = new ArrayList<IOutputProcessor>( );

        List<FormProcessor> listFormProcessor = FormProcessorHome.getListByIdForm( nIdForm,
                PluginService.getPlugin( FormPlugin.PLUGIN_NAME ) );
        IOutputProcessor outputProcessor;

        for ( FormProcessor formProcessor : listFormProcessor )
        {
            outputProcessor = _OutputProcessorSet.getOutputProcessor( formProcessor.getKeyProcessor( ) );

            if ( outputProcessor != null )
            {
                lisOutputProcessor.add( outputProcessor );
            }
        }

        return lisOutputProcessor;
    }

    /**
     * Remove associations between a form and processors
     * @param nIdForm The id of the form to remove the associations of
     */
    public void removeProcessorAssociationsByIdForm( int nIdForm )
    {
        List<FormProcessor> listFormProcessor = FormProcessorHome.getListByIdForm( nIdForm,
                PluginService.getPlugin( FormPlugin.PLUGIN_NAME ) );

        for ( FormProcessor formProcessor : listFormProcessor )
        {
            FormProcessorHome.remove( formProcessor, PluginService.getPlugin( FormPlugin.PLUGIN_NAME ) );
        }
    }

    /**
     * Remove an association between a form and a processor
     * @param nIdForm The id of the form
     * @param strKeyProcessor The key of the processor
     */
    public void removeProcessorAssociation( int nIdForm, String strKeyProcessor )
    {
        FormProcessor formProcessor = new FormProcessor( );
        formProcessor.setIdForm( nIdForm );
        formProcessor.setKeyProcessor( strKeyProcessor );

        FormProcessorHome.remove( formProcessor, PluginService.getPlugin( FormPlugin.PLUGIN_NAME ) );
    }

    /**
     * Add an association between a form and a processor
     * @param nIdForm The id of the form
     * @param strKeyProcessor The key of the processor
     */
    public void addProcessorAssociation( int nIdForm, String strKeyProcessor )
    {
        FormProcessor formProcessor = new FormProcessor( );
        formProcessor.setIdForm( nIdForm );
        formProcessor.setKeyProcessor( strKeyProcessor );

        FormProcessorHome.create( formProcessor, PluginService.getPlugin( FormPlugin.PLUGIN_NAME ) );
    }

    /**
     * Check if an association exist between a form and a processor
     * @param nIdForm The id of the form
     * @param strKeyProcessor The key of the processor
     * @return True if an association exist, false otherwise
     */
    public boolean isUsed( int nIdForm, String strKeyProcessor )
    {
        List<FormProcessor> listFormProcessor = FormProcessorHome.getListByIdForm( nIdForm,
                PluginService.getPlugin( FormPlugin.PLUGIN_NAME ) );

        for ( FormProcessor formProcessor : listFormProcessor )
        {
            if ( formProcessor.getKeyProcessor( ).equals( strKeyProcessor ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Get a processor by its key
     * @param strKey The key of the processor to get
     * @return The processor, or null if no processor has the given key
     */
    public IOutputProcessor getProcessorByKey( String strKey )
    {
        return _OutputProcessorSet.getOutputProcessor( strKey );
    }
}
