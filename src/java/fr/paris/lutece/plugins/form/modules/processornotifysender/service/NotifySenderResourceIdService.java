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
package fr.paris.lutece.plugins.form.modules.processornotifysender.service;

import fr.paris.lutece.plugins.form.service.FormPlugin;
import fr.paris.lutece.portal.service.rbac.Permission;
import fr.paris.lutece.portal.service.rbac.ResourceIdService;
import fr.paris.lutece.portal.service.rbac.ResourceType;
import fr.paris.lutece.portal.service.rbac.ResourceTypeManager;
import fr.paris.lutece.util.ReferenceList;

import java.util.Locale;

/**
 *
 * NotifySenderResourceIdService
 *
 */
public class NotifySenderResourceIdService extends ResourceIdService
{
    /**
     * Form notify sender resource type
     */
    public static final String RESOURCE_TYPE = "FORM_NOTIFY_SENDER_TYPE";

    /**
     * Permission to send attachments
     */
    public static final String PERMISSION_SEND_ATTACHMENTS = "SEND_ATTACHMENTS";
    private static final String PROPERTY_LABEL_RESOURCE_TYPE = "module.form.processornotifysender.permission.label.resource_type";
    private static final String PROPERTY_LABEL_SEND_ATTACHMENTS = "module.form.processornotifysender.permission.label.send_attachments";

    /** Creates a new instance of NotifySenderResourceIdService */
    public NotifySenderResourceIdService( )
    {
        setPluginName( FormPlugin.PLUGIN_NAME );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register( )
    {
        ResourceType rt = new ResourceType( );
        rt.setResourceIdServiceClass( NotifySenderResourceIdService.class.getName( ) );
        rt.setPluginName( FormPlugin.PLUGIN_NAME );
        rt.setResourceTypeKey( RESOURCE_TYPE );
        rt.setResourceTypeLabelKey( PROPERTY_LABEL_RESOURCE_TYPE );

        Permission p = new Permission( );
        p.setPermissionKey( PERMISSION_SEND_ATTACHMENTS );
        p.setPermissionTitleKey( PROPERTY_LABEL_SEND_ATTACHMENTS );
        rt.registerPermission( p );

        ResourceTypeManager.registerResourceType( rt );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceList getResourceIdList( Locale locale )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( String strId, Locale locale )
    {
        return null;
    }
}
