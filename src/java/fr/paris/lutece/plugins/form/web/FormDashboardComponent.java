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
package fr.paris.lutece.plugins.form.web;

import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormAction;
import fr.paris.lutece.plugins.form.business.FormActionHome;
import fr.paris.lutece.plugins.form.business.FormFilter;
import fr.paris.lutece.plugins.form.business.FormHome;
import fr.paris.lutece.plugins.form.business.FormSubmitHome;
import fr.paris.lutece.plugins.form.business.ResponseFilter;
import fr.paris.lutece.plugins.form.service.FormPlugin;
import fr.paris.lutece.plugins.form.service.FormResourceIdService;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.right.Right;
import fr.paris.lutece.portal.business.right.RightHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.dashboard.DashboardComponent;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * Calendar Dashboard Component
 * This component displays directories
 */
public class FormDashboardComponent extends DashboardComponent
{
    public static final String MARK_URL = "url";
    public static final String MARK_ICON = "icon";
    public static final String MARK_FORM_LIST = "form_list";
    public static final String MARK_PERMISSION_CREATE_FORM = "permission_create_form";
    public static final String MARK_RESPONSE_COUNT_MAP = "response_count_map";
    public static final String MARK_AUTHORIZED_FORM_MODIFICATION_LIST = "authorized_form_modification_list";
    public static final String MARK_PERMISSION_CREATE = "permission_create";
    private static final int ZONE_1 = 1;
    private static final String TEMPLATE_DASHBOARD_ZONE_1 = "/admin/plugins/form/form_dashboard_zone_1.html";
    private static final String TEMPLATE_DASHBOARD_OTHER_ZONE = "/admin/plugins/form/form_dashboard_other_zone.html";

    /**
     * The HTML code of the component
     * @param user The Admin User
     * @return The dashboard component
     */
    public String getDashboardData( AdminUser user, HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        Right right = RightHome.findByPrimaryKey( getRight(  ) );
        Locale locale = user.getLocale(  );
        List<FormAction> listActionsForFormEnable;
        List<FormAction> listActionsForFormDisable;
        List<FormAction> listActions;

        UrlItem url = new UrlItem( right.getUrl(  ) );
        url.addParameter( FormPlugin.PLUGIN_NAME, right.getPluginName(  ) );

        //build Filter
        FormFilter formFilter = new FormFilter(  );

        List<Form> listForm = FormHome.getFormList( formFilter, getPlugin(  ) );
        listForm = (List<Form>) AdminWorkgroupService.getAuthorizedCollection( listForm, user );

        Map<String, Object> model = new HashMap<String, Object>(  );

        listActionsForFormEnable = FormActionHome.selectActionsByFormState( Form.STATE_ENABLE, plugin, locale );
        listActionsForFormDisable = FormActionHome.selectActionsByFormState( Form.STATE_DISABLE, plugin, locale );

        Map<String, Object> nCountResponseMap = new HashMap<String, Object>(  );
        List<Integer> nAuthorizedModificationList = new ArrayList<Integer>(  );

        for ( Form form : listForm )
        {
            if ( form.isActive(  ) )
            {
                listActions = listActionsForFormEnable;
            }
            else
            {
                listActions = listActionsForFormDisable;
            }

            listActions = (List<FormAction>) RBACService.getAuthorizedActionsCollection( listActions, form, user );
            form.setActions( listActions );

            ResponseFilter responseFilter = new ResponseFilter(  );
            responseFilter.setIdForm( form.getIdForm(  ) );
            nCountResponseMap.put( form.getIdForm(  ) + "", FormSubmitHome.getCountFormSubmit( responseFilter, plugin ) );

            if ( RBACService.isAuthorized( form, FormResourceIdService.PERMISSION_MODIFY, user ) )
            {
                nAuthorizedModificationList.add( form.getIdForm(  ) );
            }
        }

        model.put( MARK_FORM_LIST, listForm );

        if ( RBACService.isAuthorized( Form.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    FormResourceIdService.PERMISSION_CREATE, user ) )
        {
            model.put( MARK_PERMISSION_CREATE_FORM, true );
        }
        else
        {
            model.put( MARK_PERMISSION_CREATE_FORM, false );
        }

        model.put( MARK_URL, url.getUrl(  ) );
        model.put( MARK_ICON, plugin.getIconUrl(  ) );
        model.put( MARK_RESPONSE_COUNT_MAP, nCountResponseMap );
        model.put( MARK_AUTHORIZED_FORM_MODIFICATION_LIST, nAuthorizedModificationList );
        model.put( MARK_PERMISSION_CREATE,
            RBACService.isAuthorized( Form.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                FormResourceIdService.PERMISSION_CREATE, user ) );

        HtmlTemplate t = AppTemplateService.getTemplate( getTemplateDashboard(  ), user.getLocale(  ), model );

        return t.getHtml(  );
    }

    /**
     * Get the template
     * @return the template
     */
    private String getTemplateDashboard(  )
    {
        if ( getZone(  ) == ZONE_1 )
        {
            return TEMPLATE_DASHBOARD_ZONE_1;
        }

        return TEMPLATE_DASHBOARD_OTHER_ZONE;
    }
}
