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

import fr.paris.lutece.plugins.form.business.DefaultMessage;
import fr.paris.lutece.plugins.form.business.EntryFilter;
import fr.paris.lutece.plugins.form.business.EntryHome;
import fr.paris.lutece.plugins.form.business.EntryType;
import fr.paris.lutece.plugins.form.business.EntryTypeSession;
import fr.paris.lutece.plugins.form.business.ExportFormat;
import fr.paris.lutece.plugins.form.business.ExportFormatHome;
import fr.paris.lutece.plugins.form.business.Field;
import fr.paris.lutece.plugins.form.business.FieldHome;
import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.IEntry;
import fr.paris.lutece.plugins.form.business.Recap;
import fr.paris.lutece.plugins.form.business.RecapHome;
import fr.paris.lutece.plugins.form.business.Response;
import fr.paris.lutece.plugins.form.business.exporttype.IExportTypeFactory;
import fr.paris.lutece.plugins.form.service.parameter.EntryParameterService;
import fr.paris.lutece.plugins.form.service.parameter.FormParameterService;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.style.Theme;
import fr.paris.lutece.portal.business.style.ThemeHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 *
 * FormService
 *
 */
public final class FormService
{
    private static final String MARK_PERMISSION_MANAGE_EXPORT_FORMAT = "permission_manage_export_format";
    private static final String MARK_PERMISSION_MANAGE_DEFAULT_MESSAGE = "permission_manage_default_message";
    private static final String MARK_LIST_FORM_PARAM_DEFAULT_VALUES = "list_form_param_default_values";
    private static final String MARK_LIST_ENTRY_PARAM_DEFAULT_VALUES = "list_entry_param_default_values";
    private static final String MARK_LIST_EXPORT_PARAM = "list_export_param";
    private static final String MARK_IS_ACTIVE_CAPTCHA = "is_active_captcha";
    private static final String MARK_IS_ACTIVE_MYLUTECE_AUTHENTIFICATION = "is_active_mylutece_authentification";
    private static final String MARK_THEME_REF_LIST = "theme_list";
    private static final String MARK_DEFAULT_THEME = "default_theme";
    private static final String MARK_EXPORT_FORMAT_REF_LIST = "export_format_list";
    private static final String MARK_EXPORT_DAEMON_TYPE_LIST = "export_daemon_type_list";
    private static final String JCAPTCHA_PLUGIN = "jcaptcha";
    private static final String MYLUTECE_PLUGIN = "mylutece";
    private static FormService _singleton;
    private EntryTypeService _entryTypeService = (EntryTypeService) SpringContextService.getBean( FormUtils.BEAN_ENTRY_TYPE_SERVICE );
    private IExportTypeFactory _exportDaemonTypeFactory = (IExportTypeFactory) SpringContextService.getBean( FormUtils.BEAN_EXPORT_DAEMON_TYPE_FACTORY );

    /**
     * Private constructor
     */
    private FormService(  )
    {
    }

    /**
     * Initialize the Form service
     *
     */
    public void init(  )
    {
        Form.init(  );
    }

    /**
    * Returns the instance of the singleton
    *
    * @return The instance of the singleton
    */
    public static FormService getInstance(  )
    {
        if ( _singleton == null )
        {
            _singleton = new FormService(  );
        }

        return _singleton;
    }

    /**
     * Build the advanced parameters management
     * @param user the current user
     * @return The model for the advanced parameters
     */
    public Map<String, Object> getManageAdvancedParameters( AdminUser user )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        Plugin plugin = PluginService.getPlugin( FormPlugin.PLUGIN_NAME );

        if ( RBACService.isAuthorized( Form.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    FormResourceIdService.PERMISSION_MANAGE_ADVANCED_PARAMETERS, user ) )
        {
            ReferenceList listDirectoryParamDefaultValues = FormParameterService.getService(  )
                                                                                .findDefaultValueParameters(  );
            ReferenceList listEntryParamDefaultValues = EntryParameterService.getService(  ).findAll(  );
            ReferenceList listExportParam = FormParameterService.getService(  ).findExportParameters(  );

            model.put( MARK_LIST_FORM_PARAM_DEFAULT_VALUES, listDirectoryParamDefaultValues );
            model.put( MARK_LIST_ENTRY_PARAM_DEFAULT_VALUES, listEntryParamDefaultValues );
            model.put( MARK_LIST_EXPORT_PARAM, listExportParam );
        }

        List<ExportFormat> listExportFormat = ExportFormatHome.getList( plugin );
        listExportFormat = (List<ExportFormat>) RBACService.getAuthorizedCollection( listExportFormat,
                ExportFormatResourceIdService.PERMISSION_MANAGE, user );

        if ( ( listExportFormat.size(  ) != 0 ) ||
                RBACService.isAuthorized( ExportFormat.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    ExportFormatResourceIdService.PERMISSION_MANAGE, user ) )
        {
            model.put( MARK_PERMISSION_MANAGE_EXPORT_FORMAT, true );
        }
        else
        {
            model.put( MARK_PERMISSION_MANAGE_EXPORT_FORMAT, false );
        }

        if ( RBACService.isAuthorized( DefaultMessage.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    DefaultMessageResourceIdService.PERMISSION_MANAGE, user ) )
        {
            model.put( MARK_PERMISSION_MANAGE_DEFAULT_MESSAGE, true );
        }
        else
        {
            model.put( MARK_PERMISSION_MANAGE_DEFAULT_MESSAGE, false );
        }

        //Style management
        Collection<Theme> themes = ThemeHome.getThemesList(  );
        ReferenceList themesRefList = new ReferenceList(  );

        for ( Theme theme : themes )
        {
            themesRefList.addItem( theme.getCodeTheme(  ), theme.getThemeDescription(  ) );
        }

        model.put( MARK_DEFAULT_THEME, ThemeHome.getGlobalTheme(  ) );
        model.put( MARK_THEME_REF_LIST, themesRefList );
        model.put( MARK_IS_ACTIVE_CAPTCHA, PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) );
        model.put( MARK_IS_ACTIVE_MYLUTECE_AUTHENTIFICATION, PluginService.isPluginEnable( MYLUTECE_PLUGIN ) );
        model.put( MARK_EXPORT_FORMAT_REF_LIST, ExportFormatHome.getListExport( plugin ) );
        model.put( MARK_EXPORT_DAEMON_TYPE_LIST, _exportDaemonTypeFactory.getExportTypesAsRefList( user.getLocale(  ) ) );

        return model;
    }

    /**
     * Check if the user is authorized to view the form.
     * <br />
     * This method checks every mandatory EntryTypeSession, and check if there is a value for the
     * attribute of the session.
     * @param form the form
     * @param request the HTTP request
     * @return true if he is authorized, false otherwise
     */
    public boolean isSessionValid( Form form, HttpServletRequest request )
    {
        Plugin plugin = PluginService.getPlugin( FormPlugin.PLUGIN_NAME );

        EntryType entryTypeSession = _entryTypeService.getEntryType( EntryTypeSession.class.getName(  ) );

        EntryFilter eFilter = new EntryFilter(  );
        eFilter.setIdForm( form.getIdForm(  ) );

        if ( entryTypeSession != null )
        {
            eFilter.setIdEntryType( entryTypeSession.getIdType(  ) );
        }

        List<IEntry> listEntries = EntryHome.getEntryList( eFilter, plugin );

        HttpSession session = request.getSession( false );

        for ( IEntry entry : listEntries )
        {
            if ( entry instanceof EntryTypeSession && entry.isMandatory(  ) )
            {
                List<Field> listFields = FieldHome.getFieldListByIdEntry( entry.getIdEntry(  ), plugin );

                if ( session == null )
                {
                    return false;
                }
                else if ( ( session != null ) && ( listFields != null ) && !listFields.isEmpty(  ) &&
                        ( listFields.get( 0 ) != null ) && StringUtils.isNotBlank( listFields.get( 0 ).getValue(  ) ) )
                {
                    String strAttributeName = listFields.get( 0 ).getValue(  );

                    if ( StringUtils.isBlank( (String) session.getAttribute( strAttributeName ) ) )
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Check if the given form has a recap or not
     * @param form the form
     * @return true if the form has a recap, false otherwise
     */
    public boolean hasRecap( Form form )
    {
        Plugin plugin = PluginService.getPlugin( FormPlugin.PLUGIN_NAME );
        Recap recap = RecapHome.findByPrimaryKey( form.getRecap(  ).getIdRecap(  ), plugin );

        return ( recap != null ) && recap.isRecapData(  );
    }

    /**
     * Check if the session has a list of responses without form errors and
     * the validate requirement is indeed checked
     * @param session the HTTP session
     * @return true if the session has errors, false otherwise
     */
    public boolean hasFormErrors( HttpSession session )
    {
        Map<Integer, List<Response>> listSubmittedResponses = (Map<Integer, List<Response>>) session.getAttribute( FormUtils.SESSION_FORM_LIST_SUBMITTED_RESPONSES );

        if ( listSubmittedResponses != null )
        {
            for ( Entry<Integer, List<Response>> param : listSubmittedResponses.entrySet(  ) )
            {
                for ( Response response : param.getValue(  ) )
                {
                    if ( ( response.getEntry(  ) != null ) && ( response.getEntry(  ).getFormError(  ) != null ) )
                    {
                        return true;
                    }
                }
            }
        }

        if ( session.getAttribute( FormUtils.SESSION_VALIDATE_REQUIREMENT ) != null )
        {
            boolean bValidateRequirement = (Boolean) session.getAttribute( FormUtils.SESSION_VALIDATE_REQUIREMENT );

            if ( !bValidateRequirement )
            {
                return true;
            }
        }

        return false;
    }
}
