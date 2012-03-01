/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for NotifySenderConfiguration objects
 */
public final class NotifySenderConfigurationDAO implements INotifySenderConfigurationDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_form,id_entry_email_sender, message, send_attachments FROM form_notify_sender_configuration WHERE id_form = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO form_notify_sender_configuration ( id_form, id_entry_email_sender, message, send_attachments ) VALUES ( ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM form_notify_sender_configuration WHERE id_form = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE form_notify_sender_configuration SET  id_form = ?,id_entry_email_sender = ?,  message = ?, send_attachments = ? WHERE id_form = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_form, id_entry_email_sender, message, send_attachments FROM form_notify_sender_configuration";

    /**
     * {@inheritDoc}
     */
    public void insert( NotifySenderConfiguration notifySenderConfiguration, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        daoUtil.setInt( 1, notifySenderConfiguration.getIdForm(  ) );
        daoUtil.setInt( 2, notifySenderConfiguration.getIdEntryEmailSender(  ) );
        daoUtil.setString( 3, notifySenderConfiguration.getMessage(  ) );
        daoUtil.setBoolean( 4, notifySenderConfiguration.isSendAttachments(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    public NotifySenderConfiguration load( int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.executeQuery(  );

        NotifySenderConfiguration notifySenderConfiguration = null;

        if ( daoUtil.next(  ) )
        {
            notifySenderConfiguration = new NotifySenderConfiguration(  );

            notifySenderConfiguration.setIdForm( daoUtil.getInt( 1 ) );
            notifySenderConfiguration.setIdEntryEmailSender( daoUtil.getInt( 2 ) );
            notifySenderConfiguration.setMessage( daoUtil.getString( 3 ) );
            notifySenderConfiguration.setSendAttachments( daoUtil.getBoolean( 4 ) );
        }

        daoUtil.free(  );

        return notifySenderConfiguration;
    }

    /**
     * {@inheritDoc}
     */
    public void delete( int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    public void store( NotifySenderConfiguration notifySenderConfiguration, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setInt( 1, notifySenderConfiguration.getIdForm(  ) );
        daoUtil.setInt( 2, notifySenderConfiguration.getIdEntryEmailSender(  ) );
        daoUtil.setString( 3, notifySenderConfiguration.getMessage(  ) );
        daoUtil.setBoolean( 4, notifySenderConfiguration.isSendAttachments(  ) );

        daoUtil.setInt( 5, notifySenderConfiguration.getIdForm(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    public List<NotifySenderConfiguration> selectNotifySenderConfigurationsList( Plugin plugin )
    {
        List<NotifySenderConfiguration> notifySenderConfigurationList = new ArrayList<NotifySenderConfiguration>(  );
        NotifySenderConfiguration notifySenderConfiguration;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            notifySenderConfiguration = new NotifySenderConfiguration(  );

            notifySenderConfiguration.setIdForm( daoUtil.getInt( 1 ) );
            notifySenderConfiguration.setIdEntryEmailSender( daoUtil.getInt( 2 ) );
            notifySenderConfiguration.setMessage( daoUtil.getString( 3 ) );
            notifySenderConfiguration.setSendAttachments( daoUtil.getBoolean( 4 ) );

            notifySenderConfigurationList.add( notifySenderConfiguration );
        }

        daoUtil.free(  );

        return notifySenderConfigurationList;
    }
}
