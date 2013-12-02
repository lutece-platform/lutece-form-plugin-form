/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.plugins.form.business.Category;
import fr.paris.lutece.plugins.form.business.CategoryHome;
import fr.paris.lutece.plugins.form.business.DefaultMessage;
import fr.paris.lutece.plugins.form.business.DefaultMessageHome;
import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormHome;
import fr.paris.lutece.plugins.form.business.Recap;
import fr.paris.lutece.plugins.form.business.RecapHome;
import fr.paris.lutece.plugins.form.service.EntryTypeService;
import fr.paris.lutece.plugins.form.service.FormPlugin;
import fr.paris.lutece.plugins.form.service.FormResourceIdService;
import fr.paris.lutece.plugins.form.service.FormService;
import fr.paris.lutece.plugins.form.service.entrytype.EntryTypeGroup;
import fr.paris.lutece.plugins.form.service.parameter.FormParameterService;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.EntryTypeHome;
import fr.paris.lutece.plugins.genericattributes.business.IEntry;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.style.Theme;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.portal.ThemesService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;


/**
 * JspBean to manage form modifications
 */
public abstract class ModifyFormJspBean extends FormJspBean
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 4481277438126226460L;
    // templates
    private static final String TEMPLATE_CREATE_FORM = "admin/plugins/form/create_form.html";
    private static final String TEMPLATE_MODIFY_QUESTION = "admin/plugins/form/modify_form/modify_questions.html";
    private static final String TEMPLATE_MODIFY_FORM_ADVANCED_SETTINGS = "admin/plugins/form/modify_form/modify_advanced_settings.html";
    private static final String TEMPLATE_MODIFY_FORM_PUBLICATION = "admin/plugins/form/modify_form/modify_publication.html";
    private static final String TEMPLATE_MODIFY_FORM_ANSWER_MANAGEMENT = "admin/plugins/form/modify_form/modify_answers_management.html";

    // messages
    private static final String MESSAGE_ILLOGICAL_DATE_BEGIN_DISPONIBILITY = "form.message.illogicalDateBeginDisponibility";
    private static final String MESSAGE_ILLOGICAL_DATE_END_DISPONIBILITY = "form.message.illogicalDateEndDisponibility";
    private static final String MESSAGE_DATE_END_DISPONIBILITY_BEFORE_CURRENT_DATE = "form.message.dateEndDisponibilityBeforeCurrentDate";
    private static final String MESSAGE_DATE_END_DISPONIBILITY_BEFORE_DATE_BEGIN = "form.message.dateEndDisponibilityBeforeDateBegin";
    private static final String MESSAGE_MANDATORY_FIELD = "form.message.mandatory.field";
    private static final String FIELD_TITLE = "form.createForm.labelTitle";
    private static final String FIELD_DESCRIPTION = "form.createForm.labelDescription";

    // properties
    private static final String PROPERTY_NOTHING = "form.createForm.select.nothing";
    private static final String PROPERTY_MODIFY_FORM_TITLE = "form.modifyForm.title";
    private static final String PROPERTY_NO_GROUP = "form.manageForm.noGroup";
    private static final String PROPERTY_CREATE_FORM_TITLE = "form.createForm.title";

    // Markers
    private static final String MARK_DEFAULT_MESSAGE = "default_message";
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_USER_WORKGROUP_REF_LIST = "user_workgroup_list";
    private static final String MARK_MAILING_REF_LIST = "mailing_list";
    private static final String MARK_ENTRY_TYPE_REF_LIST = "entry_type_list";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_FORM = "form";
    private static final String MARK_CATEGORY_LIST = "category_list";
    private static final String MARK_ENTRY_TYPE_GROUP = "entry_type_group";
    private static final String MARK_ENTRY_LIST = "entry_list";
    private static final String MARK_GROUP_ENTRY_LIST = "entry_group_list";
    private static final String MARK_THEME_REF_LIST = "theme_list";
    private static final String MARK_NUMBER_QUESTION = "number_question";
    private static final String MARK_IS_ACTIVE_CAPTCHA = "is_active_captcha";
    private static final String MARK_IS_ACTIVE_MYLUTECE_AUTHENTIFICATION = "is_active_mylutece_authentification";
    private static final String MARK_MAP_CHILD = "mapChild";
    private static final String MARK_ANONYMIZE_ENTRY_LIST = "anonymize_entry_list";
    private static final String MARK_DEFAULT_THEME = "default_theme";
    private static final String MARK_LIST_PARAM_DEFAULT_VALUES = "list_param_default_values";
    private static final String MARK_DEFAULT_VALUE_WORKGROUP_KEY = "workgroup_key_default_value";

    // Jsp Definition
    private static final String JSP_MODIFY_FORM = "jsp/admin/plugins/form/ModifyForm.jsp";
    private static final String JSP_MODIFY_FORM_ADVANCED_PARAMETERS = "jsp/admin/plugins/form/modifyForm/GetModifyFormAdvancedParameters.jsp";
    private static final String JSP_MODIFY_FORM_PUBLICATION = "jsp/admin/plugins/form/modifyForm/GetModifyFormPublication.jsp";
    private static final String JSP_MODIFY_FORM_ANSWER_MANAGEMENT = "jsp/admin/plugins/form/modifyForm/GetModifyFormAnswersManagement.jsp";

    // parameters form
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String PARAMETER_APPLY = "apply";
    private static final String PARAMETER_ANONYMIZE_ENTRIES = "anonymizeEntries";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_DESCRIPTION = "description";
    private static final String PARAMETER_ACTIVE_CAPTCHA = "active_captcha";
    private static final String PARAMETER_ACTIVE_STORE_ADRESSE = "active_store_adresse";
    private static final String PARAMETER_ACTIVE_REQUIREMENT = "active_requirement";
    private static final String PARAMETER_LIMIT_NUMBER_RESPONSE = "limit_number_response";
    private static final String PARAMETER_PUBLICATION_MODE = "publication_mode";
    private static final String PARAMETER_DATE_BEGIN_DISPONIBILITY = "date_begin_disponibility";
    private static final String PARAMETER_DATE_END_DISPONIBILITY = "date_end_disponibility";
    private static final String PARAMETER_ID_MAILINIG_LIST = "id_mailing_list";
    private static final String PARAMETER_ID_CATEGORY = "id_category";
    private static final String PARAMETER_INFORMATION_COMPLEMENTARY_1 = "information_complementary_1";
    private static final String PARAMETER_INFORMATION_COMPLEMENTARY_2 = "information_complementary_2";
    private static final String PARAMETER_INFORMATION_COMPLEMENTARY_3 = "information_complementary_3";
    private static final String PARAMETER_INFORMATION_COMPLEMENTARY_4 = "information_complementary_4";
    private static final String PARAMETER_INFORMATION_COMPLEMENTARY_5 = "information_complementary_5";
    private static final String PARAMETER_SUPPORT_HTTPS = "support_https";
    private static final String PARAMETER_THEME_XPAGE = "id_theme_list";
    private static final String PARAMETER_ACTIVE_MYLUTECE_AUTHENTIFICATION = "active_mylutece_authentification";
    private static final String PARAMETER_FRONT_OFFICE_TITLE = "front_office_title";
    private static final String PARAMETER_IS_SHOWN_FRONT_OFFICE_TITLE = "is_shown_front_office_title";
    private static final String PARAMETER_AUTOMATIC_CLEANING = "automaticCleaning";
    private static final String PARAMETER_CLEANING_BY_REMOVAL = "cleaningByRemoval";
    private static final String PARAMETER_NB_DAYS_BEFORE_CLEANING = "nb_days_before_cleaning";
    private static final String PARAMETER_WORKGROUP = "workgroup";

    // other constants
    private static final String EMPTY_STRING = "";
    private static final String JCAPTCHA_PLUGIN = "jcaptcha";
    private static final String MYLUTECE_PLUGIN = "mylutece";
    private static final String CONSTANT_DOUBLE_QUOTE = "\"";
    private static final String CONSTANT_TWO_SIMPLE_QUOTES = "''";

    /**
     * The number of items to display per page
     */
    protected int _nItemsPerPageEntry;
    private String _strCurrentPageIndexEntry;
    private EntryTypeService _entryTypeService = SpringContextService.getBean( FormUtils.BEAN_ENTRY_TYPE_SERVICE );

    /**
     * Gets the form creation page
     * @param request The HTTP request
     * @return The form creation page
     */
    public String getCreateForm( HttpServletRequest request )
    {
        AdminUser adminUser = getUser( );
        Locale locale = getLocale( );
        ReferenceList refListWorkGroups;
        ReferenceList refMailingList;
        refListWorkGroups = AdminWorkgroupService.getUserWorkgroups( adminUser, locale );
        refMailingList = new ReferenceList( );

        String strNothing = I18nService.getLocalizedString( PROPERTY_NOTHING, locale );
        refMailingList.addItem( -1, strNothing );
        refMailingList.addAll( AdminMailingListService.getMailingLists( adminUser ) );

        String defaultTheme = ThemesService.getGlobalTheme( );

        DefaultMessage defaultMessage = DefaultMessageHome.find( getPlugin( ) );

        if ( !RBACService.isAuthorized( Form.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                FormResourceIdService.PERMISSION_CREATE, adminUser ) )
        {
            return getManageForm( request );
        }

        // Style management
        Collection<Theme> themes = ThemesService.getThemesList( );
        ReferenceList themesRefList = new ReferenceList( );

        for ( Theme theme : themes )
        {
            themesRefList.addItem( theme.getCodeTheme( ), theme.getThemeDescription( ) );
        }

        // Default Values
        ReferenceList listParamDefaultValues = FormParameterService.getService( ).findDefaultValueParameters( );

        HashMap<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_USER_WORKGROUP_REF_LIST, refListWorkGroups );
        model.put( MARK_MAILING_REF_LIST, refMailingList );
        model.put( MARK_THEME_REF_LIST, themesRefList );
        model.put( MARK_CATEGORY_LIST, getCategoriesReferenceList( getPlugin( ) ) );
        model.put( MARK_DEFAULT_MESSAGE, defaultMessage );
        model.put( MARK_DEFAULT_THEME, defaultTheme );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage( ) );
        model.put( MARK_IS_ACTIVE_CAPTCHA, PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) );
        model.put( MARK_IS_ACTIVE_MYLUTECE_AUTHENTIFICATION, PluginService.isPluginEnable( MYLUTECE_PLUGIN ) );
        model.put( MARK_LIST_PARAM_DEFAULT_VALUES, listParamDefaultValues );
        model.put( MARK_DEFAULT_VALUE_WORKGROUP_KEY, AdminWorkgroupService.ALL_GROUPS );
        setPageTitleProperty( PROPERTY_CREATE_FORM_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_FORM, locale, model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the form creation
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doCreateForm( HttpServletRequest request )
    {
        if ( ( request.getParameter( PARAMETER_CANCEL ) == null )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                        FormResourceIdService.PERMISSION_CREATE, getUser( ) ) )
        {
            Plugin plugin = getPlugin( );
            Form form = new Form( );
            String strError = getFormData( request, form );
            if ( strError != null )
            {
                return strError;
            }

            Recap recap = new Recap( );
            recap.setIdRecap( RecapHome.create( recap, plugin ) );
            form.setRecap( recap );
            form.setDateCreation( FormUtils.getCurrentTimestamp( ) );

            // Use default messages
            DefaultMessage defaultMessage = DefaultMessageHome.find( plugin );
            form.setWelcomeMessage( defaultMessage.getWelcomeMessage( ) );
            form.setUnavailabilityMessage( defaultMessage.getUnavailabilityMessage( ) );
            form.setRequirement( defaultMessage.getRequirement( ) );
            form.setLibelleValidateButton( defaultMessage.getLibelleValidateButton( ) );
            form.setLibelleResetButton( defaultMessage.getLibelleResetButton( ) );
            form.setWorkgroup( StringUtils.EMPTY );
            ReferenceItem defaultCodeTheme = FormParameterService.getService( ).findByKey( PARAMETER_THEME_XPAGE );
            if ( defaultCodeTheme != null )
            {
                form.setCodeTheme( defaultCodeTheme.getName( ) );
            }
            else
            {
                form.setCodeTheme( StringUtils.EMPTY );
            }
            int nIdForm = FormHome.create( form, plugin );

            String[] arrayIdEntries = request.getParameterValues( PARAMETER_ANONYMIZE_ENTRIES );
            if ( arrayIdEntries != null )
            {
                List<Integer> listIdEntries = new ArrayList<Integer>( arrayIdEntries.length );
                for ( String strIdEntry : arrayIdEntries )
                {
                    listIdEntries.add( Integer.parseInt( strIdEntry ) );
                }
                FormService.getInstance( ).updateAnonymizeEntryList( form.getIdForm( ), listIdEntries );
            }

            if ( PluginService.isPluginEnable( MYLUTECE_PLUGIN ) && form.isActiveMyLuteceAuthentification( ) )
            {
                FormUtils.activateMyLuteceAuthentification( form, plugin, getLocale( ), request );
            }
            return getJspModifyForm( request, nIdForm );
        }

        return getJspManageForm( request );
    }

    /**
     * Gets the form modification page
     * @param request The HTTP request
     * @return The form modification page
     */
    public String getModifyForm( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        List<IEntry> listEntry = new ArrayList<IEntry>( );
        List<IEntry> listEntryFirstLevel;
        int nNumberQuestion;
        EntryFilter filter;
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = -1;
        Form form;

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            nIdForm = Integer.parseInt( strIdForm );
            _nIdForm = nIdForm;
        }
        else
        {
            return getManageForm( request );
        }

        form = FormHome.findByPrimaryKey( nIdForm, plugin );

        if ( ( form == null )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_MODIFY,
                        getUser( ) ) )
        {
            return getManageForm( request );
        }

        filter = new EntryFilter( );
        filter.setIdResource( nIdForm );
        filter.setResourceType( Form.RESOURCE_TYPE );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        listEntryFirstLevel = EntryHome.getEntryList( filter );

        filter.setEntryParentNull( GenericAttributesUtils.CONSTANT_ID_NULL );
        filter.setIdIsComment( EntryFilter.FILTER_FALSE );
        filter.setIdIsGroup( EntryFilter.FILTER_FALSE );
        nNumberQuestion = EntryHome.getNumberEntryByFilter( filter );

        Map<String, List<Integer>> mapIdParentOrdersChildren = new HashMap<String, List<Integer>>( );

        // List of entry first level order
        List<Integer> listOrderEntryFirstLevel = new ArrayList<Integer>( );
        initOrderFirstLevel( listEntryFirstLevel, listOrderEntryFirstLevel );

        mapIdParentOrdersChildren.put( "0", listOrderEntryFirstLevel );

        if ( listEntryFirstLevel.size( ) != 0 )
        {
            listEntryFirstLevel.get( 0 ).setFirstInTheList( true );
            listEntryFirstLevel.get( listEntryFirstLevel.size( ) - 1 ).setLastInTheList( true );
        }

        fillEntryListWithEntryFirstLevel( plugin, listEntry, listEntryFirstLevel );

        populateEntryMap( listEntry, mapIdParentOrdersChildren );

        _strCurrentPageIndexEntry = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                _strCurrentPageIndexEntry );
        _nItemsPerPageEntry = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE,
                _nItemsPerPageEntry, _nDefaultItemsPerPage );

        LocalizedPaginator<IEntry> paginator = new LocalizedPaginator<IEntry>( listEntry, _nItemsPerPageEntry,
                AppPathService.getBaseUrl( request ) + JSP_MODIFY_FORM + "?id_form=" + nIdForm, PARAMETER_PAGE_INDEX,
                _strCurrentPageIndexEntry, getLocale( ) );

        Locale locale = getLocale( );
        ReferenceList refEntryType;

        EntryType entryTypeGroup = new EntryType( );
        refEntryType = initRefListEntryType( locale, entryTypeGroup );

        //get only the group type entries
        filter = new EntryFilter( );
        filter.setIdResource( nIdForm );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setIdEntryType( _entryTypeService.getEntryType( EntryTypeGroup.BEAN_NAME ).getIdType( ) );

        List<IEntry> listGroupEntry = EntryHome.getEntryList( filter );

        ReferenceList refListGroupEntry = ReferenceList.convert( listGroupEntry, "idEntry", "title", true );

        //add the root choice to the reference list
        ReferenceItem emptyItem = new ReferenceItem( );
        emptyItem.setCode( StringUtils.EMPTY );
        emptyItem.setName( StringEscapeUtils.escapeHtml( I18nService.getLocalizedString( PROPERTY_NO_GROUP, locale ) ) );
        refListGroupEntry.add( 0, emptyItem );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, Integer.toString( _nItemsPerPageEntry ) );
        model.put( MARK_ENTRY_TYPE_REF_LIST, refEntryType );
        model.put( MARK_ENTRY_TYPE_GROUP, entryTypeGroup );
        model.put( MARK_FORM, form );
        model.put( MARK_CATEGORY_LIST, getCategoriesReferenceList( plugin ) );
        model.put( MARK_ENTRY_LIST, paginator.getPageItems( ) );
        model.put( MARK_GROUP_ENTRY_LIST, refListGroupEntry );
        model.put( MARK_NUMBER_QUESTION, nNumberQuestion );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage( ) );

        setPageTitleProperty( PROPERTY_MODIFY_FORM_TITLE );

        model.put( MARK_MAP_CHILD, mapIdParentOrdersChildren );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_QUESTION, locale, model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the form modification
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doModifyForm( HttpServletRequest request )
    {
        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            Plugin plugin = getPlugin( );
            String strIdForm = request.getParameter( PARAMETER_ID_FORM );
            int nIdForm = -1;
            Form updatedForm;

            if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
            {
                nIdForm = Integer.parseInt( strIdForm );
            }

            if ( nIdForm != -1 )
            {
                updatedForm = FormHome.findByPrimaryKey( nIdForm, plugin );

                if ( !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_MODIFY,
                        getUser( ) ) )
                {
                    return getJspManageForm( request );
                }

                String strError = getFormData( request, updatedForm );

                if ( strError != null )
                {
                    return strError;
                }

                updatedForm.setIdForm( nIdForm );
                FormHome.update( updatedForm, getPlugin( ) );

                if ( request.getParameter( PARAMETER_APPLY ) != null )
                {
                    return getJspModifyForm( request, nIdForm );
                }
            }
        }

        return getJspManageForm( request );
    }

    /**
     * Get the form advanced settings page
     * @param request The request
     * @return The HTML to display
     */
    public String getModifyFormAdvancedParameters( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = -1;
        Form form;
        AdminUser adminUser = getUser( );

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            nIdForm = Integer.parseInt( strIdForm );
            _nIdForm = nIdForm;
        }
        else
        {
            return getManageForm( request );
        }

        form = FormHome.findByPrimaryKey( nIdForm, plugin );

        if ( ( form == null )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_MODIFY,
                        adminUser ) )
        {
            return getManageForm( request );
        }
        setPageTitleProperty( PROPERTY_MODIFY_FORM_TITLE );
        Locale locale = getLocale( );

        ReferenceList refMailingList = new ReferenceList( );
        String strNothing = I18nService.getLocalizedString( PROPERTY_NOTHING, locale );
        refMailingList.addItem( -1, strNothing );
        refMailingList.addAll( AdminMailingListService.getMailingLists( adminUser ) );

        ReferenceList refListWorkGroups = AdminWorkgroupService.getUserWorkgroups( adminUser, locale );

        // Style management
        Collection<Theme> themes = ThemesService.getThemesList( );
        ReferenceList themesRefList = new ReferenceList( );

        for ( Theme theme : themes )
        {
            themesRefList.addItem( theme.getCodeTheme( ), theme.getThemeDescription( ) );
        }

        if ( form.getCodeTheme( ) == null )
        {
            form.setCodeTheme( ThemesService.getGlobalTheme( ) );
        }

        Map<String, Object> model = new HashMap<String, Object>( );

        model.put( MARK_FORM, form );
        model.put( MARK_MAILING_REF_LIST, refMailingList );
        model.put( MARK_USER_WORKGROUP_REF_LIST, refListWorkGroups );
        model.put( MARK_IS_ACTIVE_CAPTCHA, PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) );
        model.put( MARK_IS_ACTIVE_MYLUTECE_AUTHENTIFICATION, PluginService.isPluginEnable( MYLUTECE_PLUGIN ) );
        model.put( MARK_THEME_REF_LIST, themesRefList );
        model.put( MARK_CATEGORY_LIST, getCategoriesReferenceList( plugin ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_FORM_ADVANCED_SETTINGS, locale, model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Do modify form advanced settings
     * @param request The request
     * @return The next URL to redirect to
     */
    public String doModifyFormAdvancedParameters( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = -1;
        Form updatedForm;

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            nIdForm = Integer.parseInt( strIdForm );
        }

        if ( nIdForm != -1 )
        {
            updatedForm = FormHome.findByPrimaryKey( nIdForm, plugin );

            if ( !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_MODIFY,
                    getUser( ) ) )
            {
                return getJspManageForm( request );
            }

            String strError = getFormData( request, updatedForm );
            if ( strError == null )
            {
                getFormAdvancedParametersData( request, updatedForm );
            }
            if ( strError != null )
            {
                return strError;
            }
            updatedForm.setIdForm( nIdForm );

            // We get the saved form before it is updated
            Form form = FormHome.findByPrimaryKey( nIdForm, plugin );

            // We update the database with the modification made to the form
            FormHome.update( updatedForm, getPlugin( ) );

            if ( PluginService.isPluginEnable( MYLUTECE_PLUGIN ) && updatedForm.isActiveMyLuteceAuthentification( )
                    && !form.isActiveMyLuteceAuthentification( ) )
            {
                FormUtils.activateMyLuteceAuthentification( updatedForm, plugin, getLocale( ), request );
            }
            else if ( PluginService.isPluginEnable( MYLUTECE_PLUGIN )
                    && !updatedForm.isActiveMyLuteceAuthentification( ) && form.isActiveMyLuteceAuthentification( ) )
            {
                FormUtils.deactivateMyLuteceAuthentification( updatedForm, plugin );
            }

            if ( request.getParameter( PARAMETER_APPLY ) != null )
            {
                return getJspModifyFormAdvancedParameters( request, nIdForm );
            }
        }
        return getJspManageForm( request );
    }

    /**
     * Get the form publication management page
     * @param request The request
     * @return The HTML to display
     */
    public String getModifyFormPublication( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = -1;
        Form form;
        AdminUser adminUser = getUser( );

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            nIdForm = Integer.parseInt( strIdForm );
            _nIdForm = nIdForm;
        }
        else
        {
            return getManageForm( request );
        }

        form = FormHome.findByPrimaryKey( nIdForm, plugin );

        if ( ( form == null )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_MODIFY,
                        adminUser ) )
        {
            return getManageForm( request );
        }
        setPageTitleProperty( PROPERTY_MODIFY_FORM_TITLE );
        Locale locale = getLocale( );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_FORM, form );
        model.put( MARK_CATEGORY_LIST, getCategoriesReferenceList( plugin ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage( ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_FORM_PUBLICATION, locale, model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Do modify form publication management
     * @param request The request
     * @return The next URL to redirect to
     */
    public String doModifyFormPublication( HttpServletRequest request )
    {
        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            Plugin plugin = getPlugin( );
            String strIdForm = request.getParameter( PARAMETER_ID_FORM );
            int nIdForm = -1;
            Form updatedForm;

            if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
            {
                nIdForm = Integer.parseInt( strIdForm );
            }

            if ( nIdForm != -1 )
            {
                updatedForm = FormHome.findByPrimaryKey( nIdForm, plugin );

                if ( !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_MODIFY,
                        getUser( ) ) )
                {
                    return getJspManageForm( request );
                }

                String strError = getFormData( request, updatedForm );
                if ( strError == null )
                {
                    getFormPublicationData( request, updatedForm );
                }

                if ( strError != null )
                {
                    return strError;
                }

                updatedForm.setIdForm( nIdForm );
                FormHome.update( updatedForm, getPlugin( ) );

                if ( request.getParameter( PARAMETER_APPLY ) != null )
                {
                    return getJspModifyFormPublication( request, nIdForm );
                }
            }
        }

        return getJspManageForm( request );
    }

    /**
     * Get the form answers management page
     * @param request The request
     * @return The HTML to display
     */
    public String getModifyFormAnswersManagement( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        Locale locale = getLocale( );
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = -1;
        Form form;
        AdminUser adminUser = getUser( );

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            nIdForm = Integer.parseInt( strIdForm );
            _nIdForm = nIdForm;
        }
        else
        {
            return getManageForm( request );
        }
        form = FormHome.findByPrimaryKey( nIdForm, plugin );

        if ( ( form == null )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_MODIFY,
                        adminUser ) )
        {
            return getManageForm( request );
        }
        setPageTitleProperty( PROPERTY_MODIFY_FORM_TITLE );

        EntryFilter filter = new EntryFilter( );
        filter.setIdResource( nIdForm );
        filter.setIdIsGroup( 0 );
        filter.setIdIsComment( 0 );
        filter.setResourceType( Form.RESOURCE_TYPE );
        List<IEntry> listEntries = EntryHome.getEntryList( filter );

        List<Integer> listAnonymizeEntry = FormService.getInstance( ).getAnonymizeEntryList( form.getIdForm( ) );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_FORM, form );
        model.put( MARK_ENTRY_LIST, listEntries );
        model.put( MARK_ANONYMIZE_ENTRY_LIST, listAnonymizeEntry );
        model.put( MARK_CATEGORY_LIST, getCategoriesReferenceList( plugin ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_FORM_ANSWER_MANAGEMENT, locale, model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Do modify form answers management
     * @param request The request
     * @return The next URL to redirect to
     */
    public String doModifyFormAnswersManagement( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = -1;
        Form form;

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            nIdForm = Integer.parseInt( strIdForm );
        }

        if ( nIdForm != -1 )
        {
            form = FormHome.findByPrimaryKey( nIdForm, plugin );

            if ( !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_MODIFY,
                    getUser( ) ) )
            {
                return getJspManageForm( request );
            }

            String strError = getFormData( request, form );
            if ( strError == null )
            {
                strError = getAnswerManagementData( request, form );
            }

            if ( strError != null )
            {
                return strError;
            }
            String[] arrayIdEntries = request.getParameterValues( PARAMETER_ANONYMIZE_ENTRIES );
            if ( arrayIdEntries != null )
            {
                List<Integer> listIdEntries = new ArrayList<Integer>( arrayIdEntries.length );
                for ( String strIdEntry : arrayIdEntries )
                {
                    listIdEntries.add( Integer.parseInt( strIdEntry ) );
                }
                FormService.getInstance( ).updateAnonymizeEntryList( nIdForm, listIdEntries );
            }

            form.setIdForm( nIdForm );
            FormHome.update( form, plugin );

            if ( request.getParameter( PARAMETER_APPLY ) != null )
            {
                return getJspModifyFormAnswerManagement( request, nIdForm );
            }
        }
        return getJspManageForm( request );
    }

    /**
     * Init the list of the attribute's orders (first level only)
     * @param listEntryFirstLevel the list of all the attributes of the first
     *            level
     * @param orderFirstLevel the list to set
     */
    protected void initOrderFirstLevel( List<IEntry> listEntryFirstLevel, List<Integer> orderFirstLevel )
    {
        for ( IEntry entry : listEntryFirstLevel )
        {
            orderFirstLevel.add( entry.getPosition( ) );
        }
    }

    /**
     * Init reference list whidth the different entry type
     * @param locale the locale
     * @param entryTypeGroup the entry type who represent a group
     * @return reference list of entry type
     */
    protected ReferenceList initRefListEntryType( Locale locale, EntryType entryTypeGroup )
    {
        ReferenceList refListEntryType = new ReferenceList( );
        List<EntryType> listEntryType = EntryTypeHome.getList( FormPlugin.PLUGIN_NAME );

        for ( EntryType entryType : listEntryType )
        {
            if ( !entryType.getGroup( ) && !entryType.getMyLuteceUser( ) )
            {
                refListEntryType.addItem( entryType.getIdType( ), entryType.getTitle( ) );
            }
            else if ( entryType.getGroup( ) && !entryType.getMyLuteceUser( ) )
            {
                entryTypeGroup.setIdType( entryType.getIdType( ) );
            }
        }

        return refListEntryType;
    }

    /**
     * return url of the jsp modify form
     * @param request The HTTP request
     * @param nIdForm the key of form to modify
     * @return return url of the jsp modify form
     */
    protected String getJspModifyForm( HttpServletRequest request, int nIdForm )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MODIFY_FORM + "?id_form=" + nIdForm;
    }

    /**
     * Get the JSP url to the form advanced parameters page
     * @param request The request
     * @param nIdForm The id of the form
     * @return The URL of the form advanced parameters page
     */
    protected String getJspModifyFormAdvancedParameters( HttpServletRequest request, int nIdForm )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MODIFY_FORM_ADVANCED_PARAMETERS + "?id_form=" + nIdForm;
    }

    /**
     * Get the JSP url to the form publication page
     * @param request The request
     * @param nIdForm The id of the form
     * @return The URL of the form publication page
     */
    protected String getJspModifyFormPublication( HttpServletRequest request, int nIdForm )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MODIFY_FORM_PUBLICATION + "?id_form=" + nIdForm;
    }

    /**
     * Get the JSP url to the form answers management page
     * @param request The request
     * @param nIdForm The id of the form
     * @return The URL of the form answers management page
     */
    protected String getJspModifyFormAnswerManagement( HttpServletRequest request, int nIdForm )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MODIFY_FORM_ANSWER_MANAGEMENT + "?id_form=" + nIdForm;
    }

    /**
     * Get the request data and if there is no error insert the data in the form
     * specified in parameter. return null if there is no error or else return
     * the error page url
     * @param request the request
     * @param form form
     * @return null if there is no error or else return the error page url
     */
    private String getFormData( HttpServletRequest request, Form form )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strDescription = request.getParameter( PARAMETER_DESCRIPTION );
        String strCategory = request.getParameter( PARAMETER_ID_CATEGORY );

        String strFieldError = EMPTY_STRING;

        if ( StringUtils.isBlank( strTitle ) )
        {
            strFieldError = FIELD_TITLE;
        }
        if ( StringUtils.contains( strTitle, CONSTANT_DOUBLE_QUOTE ) )
        {
            strTitle = StringUtils.replace( strTitle, CONSTANT_DOUBLE_QUOTE, CONSTANT_TWO_SIMPLE_QUOTES );
        }

        else if ( ( strDescription == null ) || strDescription.trim( ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_DESCRIPTION;
        }

        if ( !strFieldError.equals( EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, getLocale( ) ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                    AdminMessage.TYPE_STOP );
        }

        form.setTitle( strTitle );
        form.setDescription( strDescription );

        try
        {
            int nCategoryId = Integer.parseInt( strCategory );

            Category category = CategoryHome.findByPrimaryKey( nCategoryId, getPlugin( ) );
            form.setCategory( category );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne.getMessage( ), ne );

            return getHomeUrl( request );
        }

        return null; // No error
    }

    /**
     * Update the advanced parameters of a form
     * @param request The request
     * @param form The form to update the advanced parameters of
     * @return The next URL if an error occurs, or null if there is no error
     */
    private String getFormAdvancedParametersData( HttpServletRequest request, Form form )
    {
        String strInformationComplementary1 = request.getParameter( PARAMETER_INFORMATION_COMPLEMENTARY_1 );
        String strInformationComplementary2 = request.getParameter( PARAMETER_INFORMATION_COMPLEMENTARY_2 );
        String strInformationComplementary3 = request.getParameter( PARAMETER_INFORMATION_COMPLEMENTARY_3 );
        String strInformationComplementary4 = request.getParameter( PARAMETER_INFORMATION_COMPLEMENTARY_4 );
        String strInformationComplementary5 = request.getParameter( PARAMETER_INFORMATION_COMPLEMENTARY_5 );
        String strFrontOfficeTitle = request.getParameter( PARAMETER_FRONT_OFFICE_TITLE );
        String strIsShownFrontOfficeTitle = request.getParameter( PARAMETER_IS_SHOWN_FRONT_OFFICE_TITLE );
        String strWorkgroup = request.getParameter( PARAMETER_WORKGROUP );
        String strMailingListId = request.getParameter( PARAMETER_ID_MAILINIG_LIST );
        String strThemeXpage = request.getParameter( PARAMETER_THEME_XPAGE );
        String strActiveRequirement = request.getParameter( PARAMETER_ACTIVE_REQUIREMENT );
        String strActiveStoreAdresse = request.getParameter( PARAMETER_ACTIVE_STORE_ADRESSE );
        String strLimitNumberResponse = request.getParameter( PARAMETER_LIMIT_NUMBER_RESPONSE );
        String strActiveMyLuteceAuthentification = request.getParameter( PARAMETER_ACTIVE_MYLUTECE_AUTHENTIFICATION );
        String strSupportsHTTPS = request.getParameter( PARAMETER_SUPPORT_HTTPS );
        String strActiveCaptcha = request.getParameter( PARAMETER_ACTIVE_CAPTCHA );

        form.setWorkgroup( strWorkgroup );

        try
        {
            int nMailingListId = Integer.parseInt( strMailingListId );
            form.setIdMailingList( nMailingListId );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne.getMessage( ), ne );
            return getHomeUrl( request );
        }

        if ( ( strFrontOfficeTitle != null ) )
        {
            form.setFrontOfficeTitle( strFrontOfficeTitle );
        }

        form.setIsShownFrontOfficeTitle( strIsShownFrontOfficeTitle != null );

        if ( strThemeXpage != null )
        {
            form.setCodeTheme( strThemeXpage );
        }

        form.setSupportHTTPS( strSupportsHTTPS != null );
        form.setActiveStoreAdresse( strActiveStoreAdresse != null );
        form.setLimitNumberResponse( strLimitNumberResponse != null );
        form.setActiveRequirement( strActiveRequirement != null );
        form.setActiveMyLuteceAuthentification( strActiveMyLuteceAuthentification != null );
        form.setActiveCaptcha( strActiveCaptcha != null );

        if ( strInformationComplementary1 != null )
        {
            form.setInfoComplementary1( strInformationComplementary1 );
        }

        if ( strInformationComplementary2 != null )
        {
            form.setInfoComplementary2( strInformationComplementary2 );
        }

        if ( strInformationComplementary3 != null )
        {
            form.setInfoComplementary3( strInformationComplementary3 );
        }

        if ( strInformationComplementary4 != null )
        {
            form.setInfoComplementary4( strInformationComplementary4 );
        }

        if ( strInformationComplementary5 != null )
        {
            form.setInfoComplementary5( strInformationComplementary5 );
        }

        return null; // No error
    }

    /**
     * Update the publication parameters of a form
     * @param request The request
     * @param form The form to update the publication parameters of
     * @return The next URL if an error occurs, or null if there is no error
     */
    private String getFormPublicationData( HttpServletRequest request, Form form )
    {
        String strPublicationMode = request.getParameter( PARAMETER_PUBLICATION_MODE );
        String strDateBeginDisponibility = request.getParameter( PARAMETER_DATE_BEGIN_DISPONIBILITY );
        String strDateEndDisponibility = request.getParameter( PARAMETER_DATE_END_DISPONIBILITY );
        if ( ( strPublicationMode != null ) && strPublicationMode.equals( PUBLICATION_MODE_AUTO ) )
        {
            // Set date begin disponibility
            java.util.Date tDateBeginDisponibility = null;

            if ( ( strDateBeginDisponibility != null ) && !strDateBeginDisponibility.equals( EMPTY_STRING ) )
            {
                tDateBeginDisponibility = DateUtil.formatDate( strDateBeginDisponibility, getLocale( ) );

                if ( tDateBeginDisponibility == null )
                {
                    return AdminMessageService.getMessageUrl( request, MESSAGE_ILLOGICAL_DATE_BEGIN_DISPONIBILITY,
                            AdminMessage.TYPE_STOP );
                }

                // no need to check the date begin of validity
            }

            // Set date end disponibility
            form.setDateBeginDisponibility( tDateBeginDisponibility );

            java.util.Date tDateEndDisponibility = null;

            if ( ( strDateEndDisponibility != null ) && !strDateEndDisponibility.equals( EMPTY_STRING ) )
            {
                tDateEndDisponibility = DateUtil.formatDate( strDateEndDisponibility, getLocale( ) );

                if ( tDateEndDisponibility == null )
                {
                    return AdminMessageService.getMessageUrl( request, MESSAGE_ILLOGICAL_DATE_END_DISPONIBILITY,
                            AdminMessage.TYPE_STOP );
                }
                if ( tDateEndDisponibility.before( FormUtils.getCurrentDate( ) ) )
                {
                    return AdminMessageService.getMessageUrl( request,
                            MESSAGE_DATE_END_DISPONIBILITY_BEFORE_CURRENT_DATE, AdminMessage.TYPE_STOP );
                }
            }

            if ( ( tDateBeginDisponibility != null ) && ( tDateEndDisponibility != null )
                    && tDateBeginDisponibility.after( tDateEndDisponibility ) )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_DATE_END_DISPONIBILITY_BEFORE_DATE_BEGIN,
                        AdminMessage.TYPE_STOP );
            }

            form.setDateEndDisponibility( tDateEndDisponibility );
            form.setActive( false );
            form.setAutoPublicationActive( true );
        }
        else
        {
            form.setDateBeginDisponibility( null );
            form.setDateEndDisponibility( null );
            form.setAutoPublicationActive( false );
        }
        return null; // No error
    }

    /**
     * Update the answer management parameters of a form
     * @param request The request
     * @param form The form to update the answer management parameters of
     * @return The next URL if an error occurs, or null if there is no error
     */
    private String getAnswerManagementData( HttpServletRequest request, Form form )
    {
        String strAutomaticCleaning = request.getParameter( PARAMETER_AUTOMATIC_CLEANING );
        String strCleaningRemove = request.getParameter( PARAMETER_CLEANING_BY_REMOVAL );
        String strNbDaysBeforeCleaning = request.getParameter( PARAMETER_NB_DAYS_BEFORE_CLEANING );

        if ( StringUtils.isNotEmpty( strAutomaticCleaning ) )
        {
            form.setAutomaticCleaning( Boolean.parseBoolean( strAutomaticCleaning ) );
        }
        else
        {
            form.setAutomaticCleaning( false );
        }

        if ( StringUtils.isNotEmpty( strCleaningRemove ) )
        {
            form.setCleaningByRemoval( Boolean.parseBoolean( strCleaningRemove ) );
        }
        if ( strNbDaysBeforeCleaning != null && StringUtils.isNumeric( strNbDaysBeforeCleaning ) )
        {
            form.setNbDaysBeforeCleaning( Integer.parseInt( strNbDaysBeforeCleaning ) );
        }

        return null; // No error
    }

    /**
     * Fill an entry list with all the entries of the first level
     * @param plugin the plugin
     * @param listEntry the list of all the entries
     * @param listEntryFirstLevel the list of all the entries of the first level
     */
    private void fillEntryListWithEntryFirstLevel( Plugin plugin, List<IEntry> listEntry,
            List<IEntry> listEntryFirstLevel )
    {
        EntryFilter filter;

        for ( IEntry entry : listEntryFirstLevel )
        {
            listEntry.add( entry );

            if ( entry.getEntryType( ).getGroup( ) )
            {
                filter = new EntryFilter( );
                filter.setIdEntryParent( entry.getIdEntry( ) );
                filter.setResourceType( Form.RESOURCE_TYPE );
                entry.setChildren( EntryHome.getEntryList( filter ) );

                if ( entry.getChildren( ).size( ) != 0 )
                {
                    entry.getChildren( ).get( 0 ).setFirstInTheList( true );
                    entry.getChildren( ).get( entry.getChildren( ).size( ) - 1 ).setLastInTheList( true );
                }

                for ( IEntry entryChild : entry.getChildren( ) )
                {
                    listEntry.add( entryChild );
                }
            }
        }
    }

    /**
     * Populate map with ( idParent : List<Orders> ) except for entry with
     * parent
     * @param listEntry The list of entries
     * @param mapIdParentOrdersChildren The map to add items in
     */
    private void populateEntryMap( List<IEntry> listEntry, Map<String, List<Integer>> mapIdParentOrdersChildren )
    {
        List<Integer> listOrder;

        for ( IEntry entry : listEntry )
        {
            if ( entry.getParent( ) != null )
            {
                Integer key = Integer.valueOf( entry.getParent( ).getIdEntry( ) );
                String strKey = key.toString( );

                if ( mapIdParentOrdersChildren.get( strKey ) != null )
                {
                    mapIdParentOrdersChildren.get( key.toString( ) ).add( entry.getPosition( ) );
                }
                else
                {
                    listOrder = new ArrayList<Integer>( );
                    listOrder.add( entry.getPosition( ) );
                    mapIdParentOrdersChildren.put( key.toString( ), listOrder );
                }
            }
        }
    }

    /**
     * Get a reference list with categories
     * @param plugin The plugin
     * @return A reference list containing categories
     */
    private ReferenceList getCategoriesReferenceList( Plugin plugin )
    {
        List<Category> listCategoriesView = CategoryHome.getList( plugin );
        Category emptyCategory = new Category( );
        emptyCategory.setIdCategory( -2 );
        emptyCategory.setTitle( EMPTY_STRING );
        listCategoriesView.add( emptyCategory );

        return FormUtils.getRefListCategory( listCategoriesView );
    }
}
