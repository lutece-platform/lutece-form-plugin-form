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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * class recap Home
 */
public final class RecapHome
{
    // Static variable pointed at the DAO instance
    private static IRecapDAO _dao = SpringContextService.getBean( "form.recapDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private RecapHome( )
    {
    }

    /**
     * Creation of an instance of Recap
     *
     * @param recap
     *            The instance of the Recap which contains the informations to store
     * @param plugin
     *            the Plugin
     * @return The primary key of the new recap.
     */
    public static int create( Recap recap, Plugin plugin )
    {
        DefaultMessage defaultMessage = DefaultMessageHome.find( plugin );
        recap.setBackUrl( defaultMessage.getBackUrl( ) );
        recap.setRecapMessage( defaultMessage.getRecapMessage( ) );

        return _dao.insert( recap, plugin );
    }

    /**
     * Copy of an instance of recap
     *
     * @param recap
     *            The instance of the Recap who must copy
     * @param plugin
     *            the Plugin
     * @return the id of the nex recap
     *
     */
    public static int copy( Recap recap, Plugin plugin )
    {
        return _dao.insert( recap, plugin );
    }

    /**
     * Update of the recap which is specified in parameter
     *
     * @param recap
     *            The instance of the Recap which contains the informations to update
     * @param plugin
     *            the Plugin
     *
     */
    public static void update( Recap recap, Plugin plugin )
    {
        _dao.store( recap, plugin );
    }

    /**
     * Remove the recap whose identifier is specified in parameter
     *
     * @param nIdRecap
     *            The recap Id
     * @param plugin
     *            the Plugin
     */
    public static void remove( int nIdRecap, Plugin plugin )
    {
        _dao.delete( nIdRecap, plugin );
    }

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a Recap whose identifier is specified in parameter
     *
     * @param nKey
     *            The entry primary key
     * @param plugin
     *            the Plugin
     * @return an instance of Recap
     */
    public static Recap findByPrimaryKey( int nKey, Plugin plugin )
    {
        return _dao.load( nKey, plugin );
    }
}
