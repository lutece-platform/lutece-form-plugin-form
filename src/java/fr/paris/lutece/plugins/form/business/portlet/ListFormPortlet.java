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

import fr.paris.lutece.plugins.form.business.Category;
import fr.paris.lutece.plugins.form.business.CategoryHome;
import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormFilter;
import fr.paris.lutece.plugins.form.business.FormHome;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.util.xml.XmlUtil;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author alexandre
 */
public class ListFormPortlet extends Portlet
{
    
     // ///////////////////////////////////////////////////////////////////////////////
    // Xml Tags
    private static final String TAG_FORM_PORTLET = "form-portlet";
    private static final String TAG_FORM_PORTLET_CONTENT = "form-portlet-content";
    private static final String TAG_FORM = "form";
    private static final String TAG_FORM_ID = "form-id";
    private static final String TAG_FORM_TITLE = "form-title";
    private static final String TAG_CATEGORY = "category-form";
    private static final String TAG_CATEGORY_ID = "category-id";
    private static final String TAG_CATEGORY_COLOR = "category-color";
    private static final String TAG_CATEGORY_TITLE = "category-title";
    
    private int _nIdCategory;

    /**
     * Get the id category of the listFormPortlet
     * @return the id category of the listFormPortlet
     */
    public int getIdCategory()
    {
        return _nIdCategory;
    }

    /**
     * Set the id category of the listFormPortlet
     * @param nIdCategory 
     */
    public void setIdCategory( int nIdCategory )
    {
        _nIdCategory = nIdCategory;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void remove()
    {
        ListFormPortletHome.getInstance( ).remove( this );
    }
    
    /**
     * Updates the current instance of the form portlet object
     */
    public void update( )
    {
        ListFormPortletHome.getInstance( ).update( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getXml( HttpServletRequest request ) throws SiteMessageException
    {
        Plugin plugin = PluginService.getPlugin( this.getPluginName( ) );
        Locale locale;

        if ( request != null )
        {
            locale = request.getLocale( );
        }
        else
        {
            locale = Locale.getDefault( );
        }

        StringBuffer strXml = new StringBuffer( );
        XmlUtil.beginElement( strXml, TAG_FORM_PORTLET );
        
        //Get the portlet
        ListFormPortlet portlet = (ListFormPortlet)ListFormPortletHome.findByPrimaryKey( getId( ) );
        
        //Get the list of forms for the configured category
        Category category = CategoryHome.findByPrimaryKey( portlet.getIdCategory(), plugin );
        FormFilter filter = new FormFilter( );
        filter.setIdCategory( category.getIdCategory( ) );
        List<Form> listForm = FormHome.getFormList( filter, plugin );
        
        XmlUtil.beginElement( strXml, TAG_FORM_PORTLET_CONTENT );
        if ( category != null )
        {
            XmlUtil.beginElement( strXml, TAG_CATEGORY );
            XmlUtil.addElement( strXml, TAG_CATEGORY_ID, category.getIdCategory( ) );
            XmlUtil.addElement( strXml, TAG_CATEGORY_COLOR, category.getColor() );
            XmlUtil.addElement( strXml, TAG_CATEGORY_TITLE, category.getTitle( ) );
            XmlUtil.endElement( strXml, TAG_CATEGORY );
        }
        
        if ( !listForm.isEmpty() )
        {
            for ( Form form : listForm )
            {
                if ( form.isActive() )
                {
                    XmlUtil.beginElement( strXml, TAG_FORM );
                    XmlUtil.addElement( strXml, TAG_FORM_ID, form.getIdForm( ) );
                    XmlUtil.addElement( strXml, TAG_FORM_TITLE, form.getTitle( ) );
                    XmlUtil.endElement( strXml, TAG_FORM );
                }
            }
        }
        XmlUtil.endElement( strXml, TAG_FORM_PORTLET_CONTENT );
        XmlUtil.endElement( strXml, TAG_FORM_PORTLET );

        String strXmlDoc = addPortletTags( strXml );

        return strXmlDoc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getXmlDocument( HttpServletRequest request ) throws SiteMessageException
    {
        return XmlUtil.getXmlHeader( ) + getXml( request );
    }
    
}
