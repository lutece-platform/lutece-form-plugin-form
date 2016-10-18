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
package fr.paris.lutece.plugins.form.service.publication;

import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormFilter;
import fr.paris.lutece.plugins.form.business.FormHome;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 *
 * PublicationService
 *
 */
public final class PublicationService
{
    private static final String CONSTANTE_PLUGIN_NAME_FORM = "form";

    /**
     * PublicationService
     *
     */
    private PublicationService( )
    {
    }

    /**
     * disable all form who are enable and have a date of end disponibility before the current date
     *
     */
    public static void publication( )
    {
        Plugin plugin = PluginService.getPlugin( CONSTANTE_PLUGIN_NAME_FORM );
        FormFilter formFilter = new FormFilter( );
        List<Form> listForm = FormHome.getFormList( formFilter, plugin );
        Calendar now = new GregorianCalendar( Calendar.getInstance( ).get( Calendar.YEAR ), Calendar.getInstance( ).get( Calendar.MONTH ), Calendar
                .getInstance( ).get( Calendar.DAY_OF_MONTH ) );

        // set disable form
        for ( Form form : listForm )
        {
            boolean active = form.isActive( );
            boolean bAuthorizedAccordingToDateBegin = ( form.getDateBeginDisponibility( ) == null )
                    || form.getDateBeginDisponibility( ).before( now.getTime( ) ) || form.getDateBeginDisponibility( ).equals( now.getTime( ) );
            boolean bAuthorizedAccordingToDateEnd = ( form.getDateEndDisponibility( ) == null ) || form.getDateEndDisponibility( ).after( now.getTime( ) )
                    || form.getDateEndDisponibility( ).equals( now.getTime( ) );

            if ( bAuthorizedAccordingToDateBegin && bAuthorizedAccordingToDateEnd )
            {
                if ( form.isAutoPublicationActive( ) )
                {
                    form.setActive( true );
                }
            }
            else
            {
                form.setActive( false );

                // When the period of a suspended form is passed, reset the autoPublication to 'active'
                if ( !form.isAutoPublicationActive( ) )
                {
                    form.setAutoPublicationActive( true );
                }
            }

            FormHome.update( form, plugin );

            if ( ( active != form.isActive( ) ) && !form.isActive( ) )
            {
                FormUtils.sendNotificationMailEndDisponibility( form, Locale.getDefault( ) );
            }
        }
    }
}
