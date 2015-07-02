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
package fr.paris.lutece.plugins.form.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;


/**
 *
 * class DefaultMessageDAO
 *
 */
public class DefaultMessageDAO implements IDefaultMessageDAO
{
    private static final String SQL_QUERY_SELECT = "SELECT welcome_message," +
        "unavailability_message,requirement_message,recap_message,libelle_validate_button,libelle_reset_button,back_url " +
        "FROM form_default_message";
    private static final String SQL_QUERY_UPDATE = "UPDATE form_default_message SET welcome_message=?," +
        "unavailability_message=?,requirement_message=? ,recap_message=?,libelle_validate_button=?,libelle_reset_button=?,back_url=?";

    /**
     * Update the record in the table
     *
     * @param defaultMessage the defaultMessage
     * @param plugin the Plugin
     */
    @Override
    public void store( DefaultMessage defaultMessage, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setString( 1, defaultMessage.getWelcomeMessage(  ) );
        daoUtil.setString( 2, defaultMessage.getUnavailabilityMessage(  ) );
        daoUtil.setString( 3, defaultMessage.getRequirement(  ) );
        daoUtil.setString( 4, defaultMessage.getRecapMessage(  ) );
        daoUtil.setString( 5, defaultMessage.getLibelleValidateButton(  ) );
        daoUtil.setString( 6, defaultMessage.getLibelleResetButton(  ) );
        daoUtil.setString( 7, defaultMessage.getBackUrl(  ) );
        daoUtil.executeUpdate(  );

        daoUtil.free(  );
    }

    /**
     * load the only record from the table
     *
     * @param plugin the Plugin
     * @return the default message object
     */
    @Override
    public DefaultMessage load( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.executeQuery(  );

        DefaultMessage defaultMessage = null;

        if ( daoUtil.next(  ) )
        {
            defaultMessage = new DefaultMessage(  );
            defaultMessage.setWelcomeMessage( daoUtil.getString( 1 ) );
            defaultMessage.setUnavailabilityMessage( daoUtil.getString( 2 ) );
            defaultMessage.setRequirement( daoUtil.getString( 3 ) );
            defaultMessage.setRecapMessage( daoUtil.getString( 4 ) );
            defaultMessage.setLibelleValidateButton( daoUtil.getString( 5 ) );
            defaultMessage.setLibelleResetButton( daoUtil.getString( 6 ) );
            defaultMessage.setBackUrl( daoUtil.getString( 7 ) );
        }

        daoUtil.free(  );

        return defaultMessage;
    }
}
