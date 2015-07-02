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
package fr.paris.lutece.plugins.form.business.portlet;

import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppHTTPSService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.xml.XmlUtil;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * This class represents business objects ArticlesList Portlet
 */
public class FormPortlet extends Portlet
{
    /////////////////////////////////////////////////////////////////////////////////
    // Xml Tags
    private static final String TAG_FORM_PORTLET = "form-portlet";
    private static final String TAG_FORM_PORTLET_CONTENT = "form-portlet-content";

    /////////////////////////////////////////////////////////////////////////////////
    private static final String JSP_DO_SUBMIT_FORM = "jsp/site/Portal.jsp?page=form";

    // Constants
    private int _nPortletId;
    private int _nFormId;
    private int _nStatus;

    /**
     * Sets the identifier of the portlet type to the value specified in the
     * ArticlesListPortletHome class
     */
    public FormPortlet(  )
    {
    }

    /**
     * Returns the Xml code of the form portlet without XML heading
     *
     * @param request The HTTP Servlet request
     * @return the Xml code of the form portlet content
     */
    @Override
    public String getXml( HttpServletRequest request )
    {
        Plugin plugin = PluginService.getPlugin( this.getPluginName(  ) );
        Locale locale;

        if ( request != null )
        {
            locale = request.getLocale(  );
        }
        else
        {
            locale = Locale.getDefault(  );
        }

        StringBuffer strXml = new StringBuffer(  );
        XmlUtil.beginElement( strXml, TAG_FORM_PORTLET );

        Form form = FormPortletHome.getFormByPortletId( getId(  ), plugin );

        if ( form.isActive(  ) && ( request != null ) )
        {
            String strUrlAction = JSP_DO_SUBMIT_FORM;

            if ( form.isSupportHTTPS(  ) && AppHTTPSService.isHTTPSSupportEnabled(  ) )
            {
                // put real base url in session
                HttpSession session = request.getSession(  );

                if ( session != null )
                {
                    session.setAttribute( AppPathService.SESSION_BASE_URL, AppPathService.getBaseUrl( request ) );
                }

                strUrlAction = AppHTTPSService.getHTTPSUrl( request ) + strUrlAction;
            }

            XmlUtil.addElementHtml( strXml, TAG_FORM_PORTLET_CONTENT,
                FormUtils.getHtmlForm( form, strUrlAction, locale, true ) );
        }
        else
        {
            XmlUtil.addElementHtml( strXml, TAG_FORM_PORTLET_CONTENT, form.getUnavailabilityMessage(  ) );
        }

        XmlUtil.endElement( strXml, TAG_FORM_PORTLET );

        String str = addPortletTags( strXml );

        return str;
    }

    /**
     * Returns the Xml code of the form portlet with XML heading
     *
     * @param request The HTTP Servlet Request
     * @return the Xml code of the Articles List portlet
     */
    @Override
    public String getXmlDocument( HttpServletRequest request )
    {
        return XmlUtil.getXmlHeader(  ) + getXml( request );
    }

    /**
     * Updates the current instance of the form portlet object
     */
    public void update(  )
    {
        FormPortletHome.getInstance(  ).update( this );
    }

    /**
     * Removes the current instance of the the form portlet object
     */
    @Override
    public void remove(  )
    {
        FormPortletHome.getInstance(  ).remove( this );
    }

    /**
     * Returns the nPortletId
     *
     * @return The nPortletId
     */
    public int getPortletId(  )
    {
        return _nPortletId;
    }

    /**
     * Sets the IdPortlet
     *
     * @param nPortletId The nPortletId
     */
    public void setPortletId( int nPortletId )
    {
        _nPortletId = nPortletId;
    }

    /**
     * Returns the FormId
     *
     * @return The FormId
     */
    public int getFormId(  )
    {
        return _nFormId;
    }

    /**
     * Sets the FormId
     *
     * @param nFormId The nFormId
     */
    public void setFormId( int nFormId )
    {
        _nFormId = nFormId;
    }

    /**
     * Returns the Status
     *
     * @return The Status
     */
    @Override
    public int getStatus(  )
    {
        return _nStatus;
    }

    /**
     * Sets the Status
     *
     * @param nStatus The Status
     */
    @Override
    public void setStatus( int nStatus )
    {
        _nStatus = nStatus;
    }
}
