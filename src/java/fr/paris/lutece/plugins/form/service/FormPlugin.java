/*
 * Copyright (c) 2002-2011, Mairie de Paris
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

import fr.paris.lutece.plugins.form.business.FormHome;
import fr.paris.lutece.portal.business.style.Theme;
import fr.paris.lutece.portal.service.plugin.PluginDefaultImplementation;

import java.io.Serializable;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * class FormPlugin
 */
public class FormPlugin extends PluginDefaultImplementation implements Serializable
{
    public static final String PLUGIN_NAME = "form";
    private static final long serialVersionUID = 6341523117444246634L;
    private static final String PARAMETER_ID_FORM = "id_form";
    private Map<Integer, Theme> _xPageTheme;

    /**
     * Initialize the plugin form
     */
    public void init(  )
    {
        // Initialize the Poll service
        FormService.getInstance(  ).init(  );

        if ( this.isInstalled(  ) )
        {
            _xPageTheme = FormHome.getXPageThemes( this );
        }
    }

    /**
     * Returns the theme the plugin use for rendering a Xpage
     * @return The theme
     */
    public Theme getXPageTheme( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( strIdForm != null )
        {
            int nIdForm = Integer.parseInt( strIdForm );

            return _xPageTheme.get( nIdForm );
        }

        return null;
    }

    /**
     * Define the theme the plugin use for rendering a Xpage
     * @param xPageTheme The theme
     */
    public void addXPageTheme( int nIdForm, Theme xPageTheme )
    {
        _xPageTheme.put( nIdForm, xPageTheme );
    }

    /**
     * Updates a database connection pool associated to the plugin and stores it
     * @param strPoolName the name of the pool
     */
    public void updatePoolName( String strPoolName )
    {
        super.updatePoolName( strPoolName );
        _xPageTheme = FormHome.getXPageThemes( this );
    }
}
