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
package fr.paris.lutece.plugins.form.business.rss;

import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormFilter;
import fr.paris.lutece.plugins.form.business.FormHome;
import fr.paris.lutece.plugins.form.service.FormPlugin;
import fr.paris.lutece.portal.business.rss.FeedResource;
import fr.paris.lutece.portal.business.rss.FeedResourceItem;
import fr.paris.lutece.portal.business.rss.IFeedResource;
import fr.paris.lutece.portal.business.rss.IFeedResourceItem;
import fr.paris.lutece.portal.business.rss.ResourceRss;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * FormResourceRss
 */
public class FormResourceRss extends ResourceRss
{
    // templates
    private static final String TEMPLATE_PUSH_RSS_XML_FORM = "admin/plugins/form/rss/rss_xml_form.html";
    private static final String TEMPLATE_RSS_FORM_ITEM_TITLE = "admin/plugins/form/rss/rss_form_item_title.html";

    // JSPs
    private static final String JSP_PAGE_FORM = "/jsp/site/Portal.jsp?page=form";

    // Markers
    private static final String MARK_RSS_ITEM_TITLE = "item_title";
    private static final String MARK_RSS_ITEM_DATE_BEGIN_FORM = "item_date_begin_form";
    private static final String MARK_RSS_ITEM_DATE_END_DISPONIBILITY_FORM = "item_date_end_form";
    private static final String MARK_RSS_ITEM_DESCRIPTION = "item_description";

    // Parameters
    private static final String PARAMETER_ID_FORM = "id_form";

    // Messages
    private static final String PROPERTY_SITE_LANGUAGE = "rss.language";
    private static final String PROPERTY_WEBAPP_PROD_URL = "lutece.prod.url";
    private static final String PROPERTY_DESCRIPTION_WIRE = "form.resource_rss.description_wire";
    private static final String PROPERTY_TITLE_WIRE = "form.resource_rss.title_wire";
    private static final String MARK_ITEM_LIST = "itemList";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contentResourceRss( )
    {
        Plugin pluginForm = PluginService.getPlugin( FormPlugin.PLUGIN_NAME );

        FormFilter filter = new FormFilter( );

        if ( FormHome.getFormList( filter, pluginForm ) != null )
        {
            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSaveConfig( HttpServletRequest request, Locale locale )
    {
        Plugin pluginForm = PluginService.getPlugin( FormPlugin.PLUGIN_NAME );

        FormResourceRssConfig config = new FormResourceRssConfig( );
        config.setIdRss( this.getId( ) );

        FormResourceRssConfigHome.create( config, pluginForm );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doUpdateConfig( HttpServletRequest request, Locale locale )
    {
        Plugin pluginForm = PluginService.getPlugin( FormPlugin.PLUGIN_NAME );

        FormResourceRssConfig config = new FormResourceRssConfig( );
        config.setIdRss( this.getId( ) );

        FormResourceRssConfigHome.update( config, pluginForm );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doValidateConfigForm( HttpServletRequest request, Locale locale )
    {
        this.setDescription( I18nService.getLocalizedString( PROPERTY_DESCRIPTION_WIRE, locale ) );

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayCreateConfigForm( HttpServletRequest request, Locale locale )
    {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayModifyConfigForm( HttpServletRequest request, Locale locale )
    {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createHtmlRss( )
    {
        HashMap<String, Object> model = new HashMap<String, Object>( );
        Plugin pluginForm = PluginService.getPlugin( FormPlugin.PLUGIN_NAME );

        String strRssFileLanguage = AppPropertiesService.getProperty( PROPERTY_SITE_LANGUAGE );
        Locale locale = new Locale( strRssFileLanguage );

        FormFilter formFilter = new FormFilter( );
        formFilter.setIdState( 1 );

        List<Form> listResultForm = FormHome.getFormList( formFilter, pluginForm );
        List<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>( );

        // Description of the new form
        for ( Form form : listResultForm )
        {
            HashMap<String, Object> item = new HashMap<String, Object>( );

            item.put( MARK_RSS_ITEM_TITLE, form.getTitle( ) );
            item.put( MARK_RSS_ITEM_DATE_BEGIN_FORM, form.getDateBeginDisponibility( ) );
            item.put( MARK_RSS_ITEM_DATE_END_DISPONIBILITY_FORM, form.getDateEndDisponibility( ) );
            item.put( MARK_RSS_ITEM_DESCRIPTION, form.getDescription( ) );

            listItem.add( item );
        }

        model.put( MARK_ITEM_LIST, listItem );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_PUSH_RSS_XML_FORM, locale, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IFeedResource getFeed( )
    {
        String strRssFileLanguage = AppPropertiesService.getProperty( PROPERTY_SITE_LANGUAGE );
        Locale locale = new Locale( strRssFileLanguage );

        String strWebAppUrl = AppPropertiesService.getProperty( PROPERTY_WEBAPP_PROD_URL );
        String strSiteUrl = strWebAppUrl;

        Plugin pluginForm = PluginService.getPlugin( FormPlugin.PLUGIN_NAME );

        IFeedResource resource = new FeedResource( );
        resource.setTitle( I18nService.getLocalizedString( PROPERTY_TITLE_WIRE, new Locale( strRssFileLanguage ) ) );
        resource.setLink( strSiteUrl + JSP_PAGE_FORM );
        resource.setLanguage( strRssFileLanguage );
        resource.setDescription( I18nService.getLocalizedString( PROPERTY_DESCRIPTION_WIRE, new Locale( strRssFileLanguage ) ) );

        FormFilter formFilter = new FormFilter( );
        formFilter.setIdState( 1 );

        List<Form> listResultForm = FormHome.getFormList( formFilter, pluginForm );
        List<IFeedResourceItem> listItems = new ArrayList<IFeedResourceItem>( );

        // Description of the form
        for ( Form form : listResultForm )
        {
            IFeedResourceItem item = new FeedResourceItem( );

            String strTitle;
            Map<String, Object> model = new HashMap<String, Object>( );

            model.put( MARK_RSS_ITEM_TITLE, form.getTitle( ) );
            model.put( MARK_RSS_ITEM_DATE_BEGIN_FORM, form.getDateBeginDisponibility( ) );
            model.put( MARK_RSS_ITEM_DATE_END_DISPONIBILITY_FORM, form.getDateEndDisponibility( ) );
            model.put( MARK_RSS_ITEM_DESCRIPTION, form.getDescription( ) );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_RSS_FORM_ITEM_TITLE, locale, model );
            strTitle = template.getHtml( );

            item.setTitle( strTitle );
            item.setLink( strSiteUrl + JSP_PAGE_FORM + "&id_form=" + form.getIdForm( ) );
            item.setDescription( form.getDescription( ) );
            item.setDate( form.getDateCreation( ) );

            listItems.add( item );
        }

        resource.setItems( listItems );

        return resource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteResourceRssConfig( int idResourceRss )
    {
        Plugin pluginForm = PluginService.getPlugin( FormPlugin.PLUGIN_NAME );
        FormResourceRssConfigHome.remove( idResourceRss, pluginForm );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getParameterToApply( HttpServletRequest request )
    {
        Map<String, String> map = new HashMap<String, String>( );

        map.put( PARAMETER_ID_FORM, request.getParameter( PARAMETER_ID_FORM ) );

        return map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkResource( )
    {
        Plugin pluginForm = PluginService.getPlugin( FormPlugin.PLUGIN_NAME );
        FormResourceRssConfig config = FormResourceRssConfigHome.findByPrimaryKey( this.getId( ), pluginForm );

        return ( config != null );
    }
}
