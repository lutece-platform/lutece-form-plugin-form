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
package fr.paris.lutece.plugins.form.modules.processornotifysender.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;

/**
 * This class provides instances management methods (create, find, ...) for NotifySenderConfiguration objects
 */
public final class NotifySenderConfigurationHome
{
    // Static variable pointed at the DAO instance
    private static INotifySenderConfigurationDAO _dao = SpringContextService.getBean( "form.notifySenderConfigurationDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private NotifySenderConfigurationHome( )
    {
    }

    /**
     * Create an instance of the notifySenderConfiguration class
     * 
     * @param notifySenderConfiguration
     *            The instance of the NotifySenderConfiguration which contains the informations to store
     * @param plugin
     *            the Plugin
     */
    public static void create( NotifySenderConfiguration notifySenderConfiguration, Plugin plugin )
    {
        _dao.insert( notifySenderConfiguration, plugin );
    }

    /**
     * Update of the notifySenderConfiguration which is specified in parameter
     * 
     * @param notifySenderConfiguration
     *            The instance of the NotifySenderConfiguration which contains the data to store
     * @param plugin
     *            the Plugin
     */
    public static void update( NotifySenderConfiguration notifySenderConfiguration, Plugin plugin )
    {
        _dao.store( notifySenderConfiguration, plugin );
    }

    /**
     * Remove the notifySenderConfiguration whose identifier is specified in parameter
     * 
     * @param nIdForm
     *            The identifier of the form associate to the notifySenderConfiguration
     * @param plugin
     *            the Plugin
     */
    public static void remove( int nIdForm, Plugin plugin )
    {
        _dao.delete( nIdForm, plugin );
    }

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a notifySenderConfiguration whose identifier is specified in parameter
     * 
     * @param nIdForm
     *            The identifier of the form associate to the notifySenderConfiguration
     * @param plugin
     *            the Plugin
     * @return an instance of NotifySenderConfiguration
     */
    public static NotifySenderConfiguration findByPrimaryKey( int nIdForm, Plugin plugin )
    {
        return _dao.load( nIdForm, plugin );
    }

    /**
     * Load the data of all the notifySenderConfiguration objects and returns them in form of a collection
     * 
     * @param plugin
     *            the Plugin
     * @return the collection which contains the data of all the notifySenderConfiguration objects
     */
    public static List<NotifySenderConfiguration> getNotifySenderConfigurationsList( Plugin plugin )
    {
        return _dao.selectNotifySenderConfigurationsList( plugin );
    }
}
