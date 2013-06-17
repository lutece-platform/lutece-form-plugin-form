package fr.paris.lutece.plugins.form.web;

import fr.paris.lutece.plugins.form.business.Entry;
import fr.paris.lutece.plugins.form.business.EntryFilter;
import fr.paris.lutece.plugins.form.business.EntryHome;
import fr.paris.lutece.plugins.form.business.EntryType;
import fr.paris.lutece.plugins.form.business.Field;
import fr.paris.lutece.plugins.form.business.FieldHome;
import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormHome;
import fr.paris.lutece.plugins.form.business.IEntry;
import fr.paris.lutece.plugins.form.service.EntryRemovalListenerService;
import fr.paris.lutece.plugins.form.service.FormResourceIdService;
import fr.paris.lutece.plugins.form.service.parameter.EntryParameterService;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;


/**
 * Jsp Bean for form entries.
 * This class extends the ModifyFormJspBean. Every Jsp should use only this
 * JspBean, and not ModifyFormJspBean or FormJspBean.
 */
public class FormEntryJspBean extends ModifyFormJspBean
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 5768139431154684693L;
    // templates
    private static final String TEMPLATE_CREATE_FIELD = "admin/plugins/form/create_field.html";
    private static final String TEMPLATE_MOVE_ENTRY = "admin/plugins/form/move_entry.html";
    private static final String TEMPLATE_MODIFY_FIELD_WITH_CONDITIONAL_QUESTION = "admin/plugins/form/modify_field_with_conditional_question.html";
    private static final String TEMPLATE_MODIFY_FIELD = "admin/plugins/form/modify_field.html";

    // message
    private static final String MESSAGE_CANT_REMOVE_ENTRY = "form.message.cantRemoveEntry";
    private static final String MESSAGE_CONFIRM_REMOVE_ENTRY = "form.message.confirmRemoveEntry";
    private static final String MESSAGE_CONFIRM_REMOVE_FIELD = "form.message.confirmRemoveField";
    private static final String MESSAGE_CONFIRM_REMOVE_GROUP_WITH_ANY_ENTRY = "form.message.confirmRemoveGroupWithAnyEntry";
    private static final String MESSAGE_CONFIRM_REMOVE_GROUP_WITH_ENTRY = "form.message.confirmRemoveGroupWhithEntry";
    private static final String MESSAGE_MANDATORY_FIELD = "form.message.mandatory.field";
    private static final String MESSAGE_FIELD_VALUE_FIELD = "directory.message.field_value_field";
    private static final String MESSAGE_SELECT_GROUP = "form.message.selectGroup";
    private static final String FIELD_TITLE_FIELD = "form.createField.labelTitle";
    private static final String FIELD_VALUE_FIELD = "directory.create_field.label_value";

    // properties
    private static final String PROPERTY_COPY_ENTRY_TITLE = "form.copyEntry.title";
    private static final String PROPERTY_CREATE_COMMENT_TITLE = "form.createEntry.titleComment";
    private static final String PROPERTY_CREATE_QUESTION_TITLE = "form.createEntry.titleQuestion";
    private static final String PROPERTY_MODIFY_COMMENT_TITLE = "form.modifyEntry.titleComment";
    private static final String PROPERTY_MODIFY_QUESTION_TITLE = "form.modifyEntry.titleQuestion";
    private static final String PROPERTY_MODIFY_GROUP_TITLE = "form.modifyEntry.titleGroup";
    private static final String PROPERTY_CREATE_FIELD_TITLE = "form.createField.title";
    private static final String PROPERTY_MODIFY_FIELD_TITLE = "form.modifyField.title";

    // Markers
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_ENTRY_TYPE_REF_LIST = "entry_type_list";
    private static final String MARK_REGULAR_EXPRESSION_LIST_REF_LIST = "regular_expression_list";
    private static final String MARK_ENTRY = "entry";
    private static final String MARK_FIELD = "field";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_ENTRY_LIST = "entry_list";
    private static final String MARK_LIST = "list";
    private static final String MARK_NUMBER_ITEMS = "number_items";
    private static final String MARK_OPTION_NO_DISPLAY_TITLE = "option_no_display_title";
    private static final String MARK_LIST_PARAM_DEFAULT_VALUES = "list_param_default_values";

    // Jsp Definition
    private static final String JSP_DO_REMOVE_FIELD = "jsp/admin/plugins/form/DoRemoveField.jsp";
    private static final String JSP_DO_REMOVE_ENTRY = "jsp/admin/plugins/form/DoRemoveEntry.jsp";
    private static final String JSP_MODIFY_FORM = "jsp/admin/plugins/form/ModifyForm.jsp";
    private static final String JSP_MODIFY_ENTRY = "jsp/admin/plugins/form/ModifyEntry.jsp";
    private static final String JSP_MODIFY_FIELD = "jsp/admin/plugins/form/ModifyFieldWithConditionalQuestion.jsp";

    // parameters form
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_ID_ENTRY = "id_entry";
    private static final String PARAMETER_ENTRY_ID = "entry_id";
    private static final String PARAMETER_MOVE_BUTTON = "move";
    private static final String PARAMETER_ID_ENTRY_GROUP = "id_entry_group";
    private static final String PARAMETER_ORDER_ID = "order_id";
    private static final String PARAMETER_ID_FIELD = "id_field";
    private static final String PARAMETER_ID_EXPRESSION = "id_expression";
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String PARAMETER_APPLY = "apply";
    private static final String PARAMETER_VALUE = "value";
    private static final String PARAMETER_DEFAULT_VALUE = "default_value";
    private static final String PARAMETER_NO_DISPLAY_TITLE = "no_display_title";
    private static final String PARAMETER_OPTION_NO_DISPLAY_TITLE = "option_no_display_title";
    private static final String PARAMETER_LIST = "list";
    // other constants
    private static final String EMPTY_STRING = "";

    private String _strCurrentPageIndexConditionalEntry;
    private int _nItemsPerPageConditionalEntry;
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;
    private int _nIdEntry = -1;

    /**
     * Gets the entry creation page
     * @param request The HTTP request
     * @return The entry creation page
     */
    public String getCreateEntry( HttpServletRequest request )
    {
        Form form;
        Plugin plugin = getPlugin( );
        IEntry entry;
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = -1;
        entry = FormUtils.createEntryByType( request, plugin );

        if ( ( entry == null )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                        FormResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getManageForm( request );
        }

        if ( ( strIdField != null ) && !strIdField.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdField = Integer.parseInt( strIdField );

                Field field = new Field( );
                field.setIdField( nIdField );
                entry.setFieldDepend( field );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getJspManageForm( request );
            }
        }

        form = FormHome.findByPrimaryKey( _nIdForm, plugin );
        entry.setForm( form );

        // Default Values
        ReferenceList listParamDefaultValues = EntryParameterService.getService( ).findAll( );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_ENTRY, entry );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage( ) );
        model.put( MARK_LIST_PARAM_DEFAULT_VALUES, listParamDefaultValues );

        if ( entry.getEntryType( ).getComment( ) )
        {
            setPageTitleProperty( PROPERTY_CREATE_COMMENT_TITLE );
        }
        else
        {
            setPageTitleProperty( PROPERTY_CREATE_QUESTION_TITLE );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( entry.getTemplateCreate( ), getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the entry creation
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doCreateEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        IEntry entry;
        Field fieldDepend = null;
        Form form;
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = -1;

        if ( ( strIdField != null ) && !strIdField.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdField = Integer.parseInt( strIdField );
                fieldDepend = new Field( );
                fieldDepend.setIdField( nIdField );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );
            }
        }

        if ( ( request.getParameter( PARAMETER_CANCEL ) == null )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                        FormResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            entry = FormUtils.createEntryByType( request, plugin );

            if ( entry == null )
            {
                return getJspManageForm( request );
            }

            String strError = entry.getRequestData( request, getLocale( ) );

            if ( strError != null )
            {
                return strError;
            }

            entry.setFieldDepend( fieldDepend );
            form = new Form( );
            form.setIdForm( _nIdForm );
            entry.setForm( form );
            entry.setIdEntry( EntryHome.create( entry, plugin ) );

            if ( entry.getFields( ) != null )
            {
                for ( Field field : entry.getFields( ) )
                {
                    field.setParentEntry( entry );
                    FieldHome.create( field, plugin );
                }
            }

            if ( request.getParameter( PARAMETER_APPLY ) != null )
            {
                return getJspModifyEntry( request, entry.getIdEntry( ) );
            }
        }

        if ( fieldDepend != null )
        {
            return getJspModifyField( request, fieldDepend.getIdField( ) );
        }
        return getJspModifyForm( request, _nIdForm );
    }

    /**
     * Gets the entry modification page
     * @param request The HTTP request
     * @return The entry modification page
     */
    public String getModifyEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        IEntry entry;
        ReferenceList refListRegularExpression;
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = -1;

        if ( ( strIdEntry != null ) && !strIdEntry.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdEntry = Integer.parseInt( strIdEntry );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getManageForm( request );
            }
        }

        if ( ( nIdEntry == -1 )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                        FormResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getManageForm( request );
        }

        _nIdEntry = nIdEntry;
        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        List<Field> listField = new ArrayList<Field>( );

        for ( Field field : entry.getFields( ) )
        {
            field = FieldHome.findByPrimaryKey( field.getIdField( ), plugin );
            listField.add( field );
        }

        entry.setFields( listField );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_ENTRY, entry );
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        LocalizedPaginator<?> paginator = entry.getPaginator( _nItemsPerPage, AppPathService.getBaseUrl( request )
                + JSP_MODIFY_ENTRY + "?id_entry=" + nIdEntry, PARAMETER_PAGE_INDEX, _strCurrentPageIndex, getLocale( ) );

        if ( paginator != null )
        {
            model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + _nItemsPerPage );
            model.put( MARK_NUMBER_ITEMS, paginator.getItemsCount( ) );
            model.put( MARK_LIST, paginator.getPageItems( ) );
            model.put( MARK_PAGINATOR, paginator );
        }

        refListRegularExpression = entry.getReferenceListRegularExpression( entry, plugin );

        if ( refListRegularExpression != null )
        {
            model.put( MARK_REGULAR_EXPRESSION_LIST_REF_LIST, refListRegularExpression );
        }

        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage( ) );

        if ( entry.getEntryType( ).getComment( ) )
        {
            setPageTitleProperty( PROPERTY_MODIFY_COMMENT_TITLE );
        }
        else if ( entry.getEntryType( ).getGroup( ) )
        {
            setPageTitleProperty( PROPERTY_MODIFY_GROUP_TITLE );
        }
        else
        {
            setPageTitleProperty( PROPERTY_MODIFY_QUESTION_TITLE );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( entry.getTemplateModify( ), getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the entry modification
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doModifyEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        IEntry entry;
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = -1;

        if ( ( strIdEntry != null ) && !strIdEntry.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdEntry = Integer.parseInt( strIdEntry );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );
            }
        }

        if ( ( nIdEntry == -1 )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                        FormResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getManageForm( request );
        }

        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            String strError = entry.getRequestData( request, getLocale( ) );

            if ( strError != null )
            {
                return strError;
            }

            EntryHome.update( entry, plugin );

            if ( entry.getFields( ) != null )
            {
                for ( Field field : entry.getFields( ) )
                {
                    // Check if the field already exists in the database
                    Field fieldStored = FieldHome.findByPrimaryKey( field.getIdField( ), plugin );

                    if ( fieldStored != null )
                    {
                        // If it exists, update
                        FieldHome.update( field, plugin );
                    }
                    else
                    {
                        // If it does not exist, create
                        FieldHome.create( field, plugin );
                    }
                }
            }
        }

        if ( request.getParameter( PARAMETER_APPLY ) == null )
        {
            if ( entry.getFieldDepend( ) != null )
            {
                return getJspModifyField( request, entry.getFieldDepend( ).getIdField( ) );
            }
            return getJspModifyForm( request, _nIdForm );
        }
        return getJspModifyEntry( request, nIdEntry );
    }

    /**
     * Gets the confirmation page of delete entry
     * @param request The HTTP request
     * @return the confirmation page of delete entry
     */
    public String getConfirmRemoveEntry( HttpServletRequest request )
    {
        IEntry entry;
        Plugin plugin = getPlugin( );
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        String strMessage;
        int nIdEntry = -1;

        if ( ( strIdEntry != null ) && !strIdEntry.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdEntry = Integer.parseInt( strIdEntry );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getJspManageForm( request );
            }
        }

        if ( ( nIdEntry == -1 )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                        FormResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getJspManageForm( request );
        }

        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        if ( entry.getEntryType( ).getGroup( ) )
        {
            if ( entry.getChildren( ).size( ) != 0 )
            {
                strMessage = MESSAGE_CONFIRM_REMOVE_GROUP_WITH_ENTRY;
            }
            else
            {
                strMessage = MESSAGE_CONFIRM_REMOVE_GROUP_WITH_ANY_ENTRY;
            }
        }
        else
        {
            strMessage = MESSAGE_CONFIRM_REMOVE_ENTRY;
        }

        UrlItem url = new UrlItem( JSP_DO_REMOVE_ENTRY );
        url.addParameter( PARAMETER_ID_ENTRY, strIdEntry + "#list" );

        return AdminMessageService.getMessageUrl( request, strMessage, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform the entry supression
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doRemoveEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        IEntry entry;
        int nIdEntry = -1;

        if ( ( strIdEntry != null ) && !strIdEntry.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdEntry = Integer.parseInt( strIdEntry );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getJspManageForm( request );
            }
        }

        if ( ( nIdEntry == -1 )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                        FormResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getJspManageForm( request );
        }

        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        ArrayList<String> listErrors = new ArrayList<String>( );

        if ( !EntryRemovalListenerService.getService( ).checkForRemoval( strIdEntry, listErrors, getLocale( ) ) )
        {
            String strCause = AdminMessageService.getFormattedList( listErrors, getLocale( ) );
            Object[] args = { strCause };

            return AdminMessageService.getMessageUrl( request, MESSAGE_CANT_REMOVE_ENTRY, args, AdminMessage.TYPE_STOP );
        }

        // Update order
        List<IEntry> listEntry;
        EntryFilter filter = new EntryFilter( );
        filter.setIdForm( entry.getForm( ).getIdForm( ) );
        listEntry = EntryHome.getEntryList( filter, plugin );

        if ( entry.getFieldDepend( ) == null )
        {
            this.moveDownEntryOrder( plugin, listEntry.size( ), entry, entry.getForm( ).getIdForm( ) );
        }
        else
        {
            //conditional questions
            EntryHome.decrementOrderByOne( plugin, entry.getPosition( ), entry.getFieldDepend( ).getIdField( ), entry
                    .getForm( ).getIdForm( ) );
        }

        // Remove entry
        EntryHome.remove( nIdEntry, plugin );

        if ( entry.getFieldDepend( ) != null )
        {
            return getJspModifyField( request, entry.getFieldDepend( ).getIdField( ) );
        }
        return getJspModifyForm( request, _nIdForm );
    }

    /**
     * copy the entry whose key is specified in the Http request
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doCopyEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        IEntry entry;
        int nIdEntry = -1;

        if ( ( strIdEntry != null ) && !strIdEntry.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdEntry = Integer.parseInt( strIdEntry );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getJspManageForm( request );
            }
        }

        if ( ( nIdEntry == -1 )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                        FormResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getJspManageForm( request );
        }

        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        Object[] tabEntryTileCopy = { entry.getTitle( ) };
        String strTitleCopyEntry = I18nService.getLocalizedString( PROPERTY_COPY_ENTRY_TITLE, tabEntryTileCopy,
                getLocale( ) );

        if ( strTitleCopyEntry != null )
        {
            entry.setTitle( strTitleCopyEntry );
        }

        EntryHome.copy( entry, plugin );

        if ( entry.getFieldDepend( ) != null )
        {
            return getJspModifyField( request, entry.getFieldDepend( ).getIdField( ) );
        }
        return getJspModifyForm( request, _nIdForm );
    }

    /**
     * Gets the list of questions group
     * @param request The HTTP request
     * @return the list of questions group
     */
    public String getMoveEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        IEntry entry;
        List<IEntry> listGroup;
        EntryFilter filter;
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = -1;

        if ( ( strIdEntry != null ) && !strIdEntry.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdEntry = Integer.parseInt( strIdEntry );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getManageForm( request );
            }
        }

        if ( ( nIdEntry == -1 )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                        FormResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getManageForm( request );
        }

        _nIdEntry = nIdEntry;
        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );
        // recup group
        filter = new EntryFilter( );
        filter.setIdForm( entry.getForm( ).getIdForm( ) );
        filter.setIdIsGroup( EntryFilter.FILTER_TRUE );
        listGroup = EntryHome.getEntryList( filter, plugin );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_ENTRY, entry );
        model.put( MARK_ENTRY_LIST, listGroup );
        setPageTitleProperty( EMPTY_STRING );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MOVE_ENTRY, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Move the entry in the questions group specified in parameter
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doMoveEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        IEntry entryToMove;
        IEntry entryGroup;
        String strIdEntryGroup = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntryGroup = -1;

        if ( ( strIdEntryGroup != null ) && !strIdEntryGroup.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdEntryGroup = Integer.parseInt( strIdEntryGroup );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getJspManageForm( request );
            }
        }

        if ( nIdEntryGroup == -1 )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_SELECT_GROUP, AdminMessage.TYPE_STOP );
        }

        if ( !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                FormResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getJspManageForm( request );
        }

        entryToMove = EntryHome.findByPrimaryKey( _nIdEntry, plugin );
        entryGroup = EntryHome.findByPrimaryKey( nIdEntryGroup, plugin );

        Integer nPosition;

        if ( entryToMove.getPosition( ) < entryGroup.getPosition( ) )
        {
            nPosition = entryGroup.getPosition( );
            this.moveDownEntryOrder( plugin, nPosition, entryToMove, entryToMove.getForm( ).getIdForm( ) );
        }
        else
        {
            nPosition = entryGroup.getPosition( ) + entryGroup.getChildren( ).size( ) + 1;
            this.moveUpEntryOrder( plugin, nPosition, entryToMove, entryToMove.getForm( ).getIdForm( ) );
        }

        entryToMove.setParent( entryGroup );
        EntryHome.update( entryToMove, plugin );

        return getJspModifyForm( request, _nIdForm );
    }

    /**
     * Move out the entry
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doMoveOutEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        IEntry entry;
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = -1;

        if ( ( strIdEntry != null ) && !strIdEntry.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdEntry = Integer.parseInt( strIdEntry );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getJspManageForm( request );
            }
        }

        if ( ( nIdEntry == -1 )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                        FormResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getJspManageForm( request );
        }

        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        List<IEntry> listEntry;
        EntryFilter filter = new EntryFilter( );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        filter.setIdForm( entry.getForm( ).getIdForm( ) );
        listEntry = EntryHome.getEntryList( filter, plugin );

        Integer nListEntrySize = listEntry.size( );

        this.doMoveOutEntry( plugin, entry.getForm( ).getIdForm( ), nListEntrySize, entry );

        return this.getJspModifyForm( request, entry.getForm( ).getIdForm( ) );
    }

    /**
     * Delete association between field and regular expression
     * @param request the Http Request
     * @return The URL to go after performing the action
     */
    public String doRemoveRegularExpression( HttpServletRequest request )
    {
        String strIdExpression = request.getParameter( PARAMETER_ID_EXPRESSION );
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = -1;
        int nIdExpression = -1;

        if ( ( strIdExpression != null )
                && ( strIdField != null )
                && ( RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                        FormResourceIdService.PERMISSION_MODIFY, getUser( ) ) ) )
        {
            try
            {
                nIdField = Integer.parseInt( strIdField );
                nIdExpression = Integer.parseInt( strIdExpression );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );
            }

            if ( ( nIdField != -1 ) && ( nIdExpression != -1 ) )
            {
                FieldHome.removeVerifyBy( nIdField, nIdExpression, getPlugin( ) );
            }
        }

        return getJspModifyEntry( request, _nIdEntry );
    }

    /**
     * insert association between field and regular expression
     * @param request the Http Request
     * @return The URL to go after performing the action
     */
    public String doInsertRegularExpression( HttpServletRequest request )
    {
        String strIdExpression = request.getParameter( PARAMETER_ID_EXPRESSION );
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = -1;
        int nIdExpression = -1;

        if ( ( strIdExpression != null )
                && ( strIdField != null )
                && ( RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                        FormResourceIdService.PERMISSION_MODIFY, getUser( ) ) ) )
        {
            try
            {
                nIdField = Integer.parseInt( strIdField );
                nIdExpression = Integer.parseInt( strIdExpression );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );
            }

            if ( ( nIdField != -1 ) && ( nIdExpression != -1 ) )
            {
                FieldHome.createVerifyBy( nIdField, nIdExpression, getPlugin( ) );
            }
        }

        return getJspModifyEntry( request, _nIdEntry );
    }

    /**
     * Change the attribute's order (move up or move down in the list)
     * @param request the request
     * @return The URL of the form management page
     */
    public String doChangeOrderEntry( HttpServletRequest request )
    {
        //gets the entry which needs to be changed (order)
        Plugin plugin = getPlugin( );

        String strEntryId = StringUtils.EMPTY;
        String strOrderToSet = StringUtils.EMPTY;
        Integer nEntryId = 0;
        Integer nOrderToSet = 0;
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = FormUtils.convertStringToInt( strIdForm );
        IEntry entry;

        // To execute mass action "Move into"
        if ( request.getParameter( PARAMETER_MOVE_BUTTON + ".x" ) != null )
        {
            String strIdNewParent = request.getParameter( PARAMETER_ID_ENTRY_GROUP );
            Integer nIdNewParent = 0;

            if ( StringUtils.isNotBlank( strIdNewParent ) )
            {
                nIdNewParent = FormUtils.convertStringToInt( strIdNewParent );
            }

            // gets the entries which needs to be changed
            String[] entryToMoveList = request.getParameterValues( PARAMETER_ENTRY_ID );

            IEntry entryParent = EntryHome.findByPrimaryKey( nIdNewParent, plugin );
            List<IEntry> listEntry = new ArrayList<IEntry>( );

            if ( entryParent == null )
            {
                EntryFilter filter = new EntryFilter( );
                filter.setIdForm( nIdForm );
                filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
                listEntry = EntryHome.getEntryList( filter, plugin );
            }

            Integer nListEntrySize = listEntry.size( );

            if ( entryToMoveList != null )
            {
                // for each entry, move it into selected group
                for ( String strIdEntryToMove : entryToMoveList )
                {
                    IEntry entryToMove = EntryHome.findByPrimaryKey( FormUtils.convertStringToInt( strIdEntryToMove ),
                            plugin );
                    entryParent = EntryHome.findByPrimaryKey( nIdNewParent, plugin );

                    if ( ( entryToMove == null ) )
                    {
                        return AdminMessageService
                                .getMessageUrl( request, MESSAGE_SELECT_GROUP, AdminMessage.TYPE_STOP );
                    }

                    // if entryParent is null, move out selected entries
                    if ( ( entryParent == null ) && ( entryToMove.getParent( ) != null ) )
                    {
                        doMoveOutEntry( plugin, nIdForm, nListEntrySize, entryToMove );
                    }

                    // Move entry into group if not already in
                    else if ( ( entryParent != null )
                            && ( ( entryToMove.getParent( ) == null ) || ( ( entryToMove.getParent( ) != null ) && ( entryToMove
                                    .getParent( ).getIdEntry( ) != entryParent.getIdEntry( ) ) ) ) )
                    {
                        this.doMoveEntryIntoGroup( plugin, entryToMove, entryParent );
                    }
                }
            }
        }

        // To change order of one entry
        else
        {
            EntryFilter filter = new EntryFilter( );
            filter.setIdForm( nIdForm );

            List<IEntry> entryList = EntryHome.getEntryList( filter, getPlugin( ) );

            for ( int i = 0; i < entryList.size( ); i++ )
            {
                entry = entryList.get( i );
                nEntryId = entry.getIdEntry( );
                strEntryId = request.getParameter( PARAMETER_MOVE_BUTTON + "_" + nEntryId.toString( ) );

                if ( StringUtils.isNotBlank( strEntryId ) )
                {
                    strEntryId = nEntryId.toString( );
                    strOrderToSet = request.getParameter( PARAMETER_ORDER_ID + "_" + nEntryId.toString( ) );
                    i = entryList.size( );
                }
            }

            if ( StringUtils.isNotBlank( strEntryId ) )
            {
                nEntryId = FormUtils.convertStringToInt( strEntryId );
            }

            if ( StringUtils.isNotBlank( strOrderToSet ) )
            {
                nOrderToSet = FormUtils.convertStringToInt( strOrderToSet );
            }

            IEntry entryToChangeOrder = EntryHome.findByPrimaryKey( nEntryId, plugin );
            int nActualOrder = entryToChangeOrder.getPosition( );

            // does nothing if the order to set is equal to the actual order
            if ( nOrderToSet != nActualOrder )
            {
                // entry goes up in the list 
                if ( nOrderToSet < entryToChangeOrder.getPosition( ) )
                {
                    moveUpEntryOrder( plugin, nOrderToSet, entryToChangeOrder, entryToChangeOrder.getForm( )
                            .getIdForm( ) );
                }

                // entry goes down in the list
                else
                {
                    moveDownEntryOrder( plugin, nOrderToSet, entryToChangeOrder, entryToChangeOrder.getForm( )
                            .getIdForm( ) );
                }
            }
        }

        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MODIFY_FORM );
        url.addParameter( PARAMETER_ID_FORM, nIdForm );
        url.setAnchor( PARAMETER_LIST );

        return url.getUrl( );
    }

    /**
     * Change the attribute's order to a greater one (move down in the list)
     * @param plugin the plugin
     * @param nOrderToSet the new order for the attribute
     * @param entryToChangeOrder the attribute which will change
     * @param nIdForm the id of the form
     */
    private void moveDownEntryOrder( Plugin plugin, int nOrderToSet, IEntry entryToChangeOrder, int nIdForm )
    {
        if ( entryToChangeOrder.getParent( ) == null )
        {
            int nNbChild = 0;
            int nNewOrder = 0;

            EntryFilter filter = new EntryFilter( );
            filter.setIdForm( nIdForm );
            filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
            filter.setFieldDependNull( EntryFilter.FILTER_TRUE );

            List<IEntry> listEntryFirstLevel = EntryHome.findEntriesWithoutParent( plugin, entryToChangeOrder.getForm( )
                    .getIdForm( ) );

            List<Integer> orderFirstLevel = new ArrayList<Integer>( );
            initOrderFirstLevel( listEntryFirstLevel, orderFirstLevel );

            Integer nbChildEntryToChangeOrder = 0;

            if ( entryToChangeOrder.getChildren( ) != null )
            {
                nbChildEntryToChangeOrder = entryToChangeOrder.getChildren( ).size( );
            }

            for ( IEntry entry : listEntryFirstLevel )
            {
                for ( int i = 0; i < orderFirstLevel.size( ); i++ )
                {
                    if ( ( orderFirstLevel.get( i ) == entry.getPosition( ) )
                            && ( entry.getPosition( ) > entryToChangeOrder.getPosition( ) )
                            && ( entry.getPosition( ) <= nOrderToSet ) )
                    {
                        if ( nNbChild == 0 )
                        {
                            nNewOrder = orderFirstLevel.get( i - 1 );

                            if ( orderFirstLevel.get( i - 1 ) != entryToChangeOrder.getPosition( ) )
                            {
                                nNewOrder -= nbChildEntryToChangeOrder;
                            }
                        }
                        else
                        {
                            nNewOrder += ( nNbChild + 1 );
                        }

                        entry.setPosition( nNewOrder );
                        EntryHome.update( entry, plugin );
                        nNbChild = 0;

                        if ( entry.getChildren( ) != null )
                        {
                            for ( IEntry child : entry.getChildren( ) )
                            {
                                nNbChild++;
                                child.setPosition( nNewOrder + nNbChild );
                                EntryHome.update( child, plugin );
                            }
                        }
                    }
                }
            }

            entryToChangeOrder.setPosition( nNewOrder + nNbChild + 1 );
            EntryHome.update( entryToChangeOrder, plugin );
            nNbChild = 0;

            for ( IEntry child : entryToChangeOrder.getChildren( ) )
            {
                nNbChild++;
                child.setPosition( entryToChangeOrder.getPosition( ) + nNbChild );
                EntryHome.update( child, plugin );
            }
        }
        else
        {
            EntryFilter filter = new EntryFilter( );
            filter.setIdForm( nIdForm );
            filter.setFieldDependNull( EntryFilter.FILTER_TRUE );

            List<IEntry> listAllEntry = EntryHome.getEntryList( filter, plugin );

            for ( IEntry entry : listAllEntry )
            {
                if ( ( entry.getPosition( ) > entryToChangeOrder.getPosition( ) )
                        && ( entry.getPosition( ) <= nOrderToSet ) )
                {
                    entry.setPosition( entry.getPosition( ) - 1 );
                    EntryHome.update( entry, plugin );
                }
            }

            entryToChangeOrder.setPosition( nOrderToSet );
            EntryHome.update( entryToChangeOrder, plugin );
        }
    }

    /**
     * Change the attribute's order to a lower one (move up in the list)
     * @param plugin the plugin
     * @param nOrderToSet the new order for the attribute
     * @param entryToChangeOrder the attribute which will change
     * @param nIdForm the id of the form
     */
    private void moveUpEntryOrder( Plugin plugin, int nOrderToSet, IEntry entryToChangeOrder, int nIdForm )
    {
        if ( entryToChangeOrder.getParent( ) == null )
        {
            List<Integer> orderFirstLevel = new ArrayList<Integer>( );

            int nNbChild = 0;
            int nNewOrder = nOrderToSet;
            int nEntryToMoveOrder = entryToChangeOrder.getPosition( );

            EntryFilter filter = new EntryFilter( );
            filter.setIdForm( nIdForm );
            filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
            filter.setFieldDependNull( EntryFilter.FILTER_TRUE );

            List<IEntry> listEntryFirstLevel = EntryHome.findEntriesWithoutParent( plugin, entryToChangeOrder.getForm( )
                    .getIdForm( ) );
            //the list of all the orders in the first level
            initOrderFirstLevel( listEntryFirstLevel, orderFirstLevel );

            for ( IEntry entry : listEntryFirstLevel )
            {
                Integer entryInitialPosition = entry.getPosition( );

                for ( int i = 0; i < orderFirstLevel.size( ); i++ )
                {
                    if ( ( orderFirstLevel.get( i ) == entryInitialPosition )
                            && ( entryInitialPosition < nEntryToMoveOrder ) && ( entryInitialPosition >= nOrderToSet ) )
                    {
                        if ( entryToChangeOrder.getPosition( ) == nEntryToMoveOrder )
                        {
                            entryToChangeOrder.setPosition( nNewOrder );
                            EntryHome.update( entryToChangeOrder, plugin );

                            for ( IEntry child : entryToChangeOrder.getChildren( ) )
                            {
                                nNbChild++;
                                child.setPosition( entryToChangeOrder.getPosition( ) + nNbChild );
                                EntryHome.update( child, plugin );
                            }
                        }

                        nNewOrder = nNewOrder + nNbChild + 1;
                        entry.setPosition( nNewOrder );
                        EntryHome.update( entry, plugin );
                        nNbChild = 0;

                        for ( IEntry child : entry.getChildren( ) )
                        {
                            nNbChild++;
                            child.setPosition( nNewOrder + nNbChild );
                            EntryHome.update( child, plugin );
                        }
                    }
                }
            }
        }
        else
        {
            EntryFilter filter = new EntryFilter( );
            filter.setIdForm( nIdForm );
            filter.setFieldDependNull( EntryFilter.FILTER_TRUE );

            List<IEntry> listAllEntry = EntryHome.getEntryList( filter, plugin );

            for ( IEntry entry : listAllEntry )
            {
                if ( ( entry.getPosition( ) < entryToChangeOrder.getPosition( ) )
                        && ( entry.getPosition( ) >= nOrderToSet ) )
                {
                    entry.setPosition( entry.getPosition( ) + 1 );
                    EntryHome.update( entry, plugin );
                }
            }

            entryToChangeOrder.setPosition( nOrderToSet );
            EntryHome.update( entryToChangeOrder, plugin );
        }
    }

    /**
     * Move down the entry (conditional questions only)
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doMoveDownEntryConditional( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        IEntry entry;

        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = -1;

        if ( ( strIdEntry != null ) && !strIdEntry.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdEntry = Integer.parseInt( strIdEntry );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getJspManageForm( request );
            }
        }

        if ( ( nIdEntry == -1 )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                        FormResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getJspManageForm( request );
        }

        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        EntryFilter filter = new EntryFilter( );
        filter.setIdForm( entry.getForm( ).getIdForm( ) );

        if ( entry.getParent( ) != null )
        {
            filter.setIdEntryParent( entry.getParent( ).getIdEntry( ) );
        }
        else
        {
            filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        }

        if ( entry.getFieldDepend( ) != null )
        {
            filter.setIdFieldDepend( entry.getFieldDepend( ).getIdField( ) );
        }
        else
        {
            filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        }

        int nOrderToSet = entry.getPosition( ) + 1;
        IEntry entryWithTheSelectedOrder = EntryHome.findByOrderAndIdFieldAndIdForm( plugin, nOrderToSet, entry
                .getFieldDepend( ).getIdField( ), entry.getForm( ).getIdForm( ) );

        entryWithTheSelectedOrder.setPosition( entry.getPosition( ) );
        EntryHome.update( entryWithTheSelectedOrder, plugin );

        entry.setPosition( nOrderToSet );
        EntryHome.update( entry, plugin );

        if ( entry.getFieldDepend( ) != null )
        {
            return getJspModifyField( request, entry.getFieldDepend( ).getIdField( ) );
        }
        return getJspModifyForm( request, _nIdForm );
    }

    /**
     * Move up the entry (conditional questions only)
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doMoveUpEntryConditional( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        IEntry entry;

        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = -1;

        if ( ( strIdEntry != null ) && !strIdEntry.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdEntry = Integer.parseInt( strIdEntry );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getJspManageForm( request );
            }
        }

        if ( ( nIdEntry == -1 )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                        FormResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getJspManageForm( request );
        }

        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        EntryFilter filter = new EntryFilter( );
        filter.setIdForm( entry.getForm( ).getIdForm( ) );

        if ( entry.getParent( ) != null )
        {
            filter.setIdEntryParent( entry.getParent( ).getIdEntry( ) );
        }
        else
        {
            filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        }

        if ( entry.getFieldDepend( ) != null )
        {
            filter.setIdFieldDepend( entry.getFieldDepend( ).getIdField( ) );
        }
        else
        {
            filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        }

        int nOrderToSet = entry.getPosition( ) - 1;
        IEntry entryWithTheSelectedOrder = EntryHome.findByOrderAndIdFieldAndIdForm( plugin, nOrderToSet, entry
                .getFieldDepend( ).getIdField( ), entry.getForm( ).getIdForm( ) );

        entryWithTheSelectedOrder.setPosition( entry.getPosition( ) );
        EntryHome.update( entryWithTheSelectedOrder, plugin );

        entry.setPosition( nOrderToSet );
        EntryHome.update( entry, plugin );

        if ( entry.getFieldDepend( ) != null )
        {
            return getJspModifyField( request, entry.getFieldDepend( ).getIdField( ) );
        }
        return getJspModifyForm( request, _nIdForm );
    }

    /**
     * Move EntryToMove into entryGroup
     * @param plugin the plugin
     * @param entryToMove the entry which will be moved
     * @param entryGroup the entry group
     */
    private void doMoveEntryIntoGroup( Plugin plugin, IEntry entryToMove, IEntry entryGroup )
    {
        int nPosition;

        if ( entryToMove.getPosition( ) < entryGroup.getPosition( ) )
        {
            nPosition = entryGroup.getPosition( );
            this.moveDownEntryOrder( plugin, nPosition, entryToMove, entryToMove.getForm( ).getIdForm( ) );
        }
        else
        {
            nPosition = entryGroup.getPosition( ) + entryGroup.getChildren( ).size( ) + 1;
            this.moveUpEntryOrder( plugin, nPosition, entryToMove, entryToMove.getForm( ).getIdForm( ) );
        }

        entryToMove.setParent( entryGroup );
        EntryHome.update( entryToMove, plugin );
    }

    /**
     * Move out entry (no parent)
     * @param plugin the plugin
     * @param nIdForm the id form
     * @param nListEntrySize the number of entry
     * @param entryToMove the entry to move
     */
    private void doMoveOutEntry( Plugin plugin, int nIdForm, Integer nListEntrySize, IEntry entryToMove )
    {
        this.moveDownEntryOrder( plugin, nListEntrySize, entryToMove, nIdForm );
        entryToMove.setParent( null );
        EntryHome.update( entryToMove, plugin );
    }

    /**
     * Updates the entries position for a form
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String updateEntryOrder( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        List<IEntry> listEntryFirstLevel;
        EntryFilter filter;
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = -1;
        Form form;

        if ( ( strIdForm != null ) && !strIdForm.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdForm = Integer.parseInt( strIdForm );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getManageForm( request );
            }
        }

        form = FormHome.findByPrimaryKey( nIdForm, plugin );

        if ( ( form == null )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_MODIFY,
                        getUser( ) ) )
        {
            return getManageForm( request );
        }

        filter = new EntryFilter( );
        filter.setIdForm( nIdForm );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        listEntryFirstLevel = EntryHome.getEntryList( filter, plugin );

        int nPosition = 1;

        for ( IEntry entry : listEntryFirstLevel )
        {
            entry.setPosition( nPosition );
            EntryHome.update( entry, plugin );
            nPosition++;

            EntryFilter filterSecondLevel = new EntryFilter( );
            filterSecondLevel.setIdEntryParent( entry.getIdEntry( ) );

            List<IEntry> listEntrySecondLevel = EntryHome.getEntryList( filterSecondLevel, plugin );

            if ( listEntrySecondLevel != null )
            {
                for ( IEntry entrySecondLevel : listEntrySecondLevel )
                {
                    entrySecondLevel.setPosition( nPosition );
                    EntryHome.update( entrySecondLevel, plugin );
                    nPosition++;
                }
            }
        }

        return getJspModifyForm( request, nIdForm );
    }

    /* -------- Fields management ---------- */


    /**
     * Gets the field creation page
     * @param request The HTTP request
     * @return the field creation page
     */
    public String getCreateField( HttpServletRequest request )
    {
        Field field = new Field( );
        IEntry entry = EntryHome.findByPrimaryKey( _nIdEntry, getPlugin( ) );

        if ( ( entry == null )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                        FormResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getManageForm( request );
        }

        field.setParentEntry( entry );

        Map<String, Object> model = new HashMap<String, Object>( );
        Locale locale = getLocale( );

        if ( request.getParameter( PARAMETER_OPTION_NO_DISPLAY_TITLE ) != null )
        {
            model.put( MARK_OPTION_NO_DISPLAY_TITLE, true );
        }
        else
        {
            model.put( MARK_OPTION_NO_DISPLAY_TITLE, false );
        }

        model.put( MARK_FIELD, field );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_FIELD, locale, model );
        setPageTitleProperty( PROPERTY_CREATE_FIELD_TITLE );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Gets the field modification page
     * @param request The HTTP request
     * @param bWithConditionalQuestion true if the field is associate to
     *            conditionals questions
     * @return the field modification page
     */
    public String getModifyField( HttpServletRequest request, boolean bWithConditionalQuestion )
    {
        Field field = null;
        IEntry entry = null;
        Plugin plugin = getPlugin( );
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = -1;

        if ( request.getParameter( PARAMETER_ID_FIELD ) == null )
        {
            return getHomeUrl( request );
        }

        try
        {
            nIdField = Integer.parseInt( strIdField );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return getManageForm( request );
        }

        if ( nIdField != -1 )
        {
            field = FieldHome.findByPrimaryKey( nIdField, getPlugin( ) );
        }

        if ( ( field == null )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                        FormResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getManageForm( request );
        }

        entry = EntryHome.findByPrimaryKey( field.getParentEntry( ).getIdEntry( ), plugin );

        field.setParentEntry( entry );

        HashMap<String, Object> model = new HashMap<String, Object>( );
        Locale locale = getLocale( );
        model.put( MARK_FIELD, field );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_FIELD, locale, model );

        if ( bWithConditionalQuestion )
        {
            ReferenceList refEntryType;
            refEntryType = initRefListEntryType( plugin, locale, new EntryType( ) );
            _strCurrentPageIndexConditionalEntry = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                    _strCurrentPageIndexConditionalEntry );
            _nItemsPerPageConditionalEntry = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE,
                    _nItemsPerPageConditionalEntry, _nDefaultItemsPerPage );

            LocalizedPaginator<IEntry> paginator = new LocalizedPaginator<IEntry>( field.getConditionalQuestions( ),
                    _nItemsPerPageConditionalEntry, AppPathService.getBaseUrl( request ) + JSP_MODIFY_FIELD
                            + "?id_field=" + nIdField, PARAMETER_PAGE_INDEX, _strCurrentPageIndexConditionalEntry,
                    getLocale( ) );

            model.put( MARK_ENTRY_TYPE_REF_LIST, refEntryType );
            model.put( MARK_PAGINATOR, paginator );
            model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + _nItemsPerPageEntry );
            model.put( MARK_ENTRY_LIST, paginator.getPageItems( ) );
            model.put( MARK_NUMBER_ITEMS, paginator.getItemsCount( ) );
            template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_FIELD_WITH_CONDITIONAL_QUESTION, locale, model );
        }

        setPageTitleProperty( PROPERTY_MODIFY_FIELD_TITLE );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform creation field
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doCreateField( HttpServletRequest request )
    {
        if ( ( request.getParameter( PARAMETER_CANCEL ) == null )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                        FormResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            IEntry entry = new Entry( );
            entry.setIdEntry( _nIdEntry );

            Field field = new Field( );
            field.setParentEntry( entry );

            String strError = getFieldData( request, field );

            if ( strError != null )
            {
                return strError;
            }

            FieldHome.create( field, getPlugin( ) );
        }

        return getJspModifyEntry( request, _nIdEntry );
    }

    /**
     * Perform modification field
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doModifyField( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        Field field = null;
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = -1;

        if ( strIdField != null )
        {
            try
            {
                nIdField = Integer.parseInt( strIdField );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );
            }
        }

        if ( ( nIdField != -1 )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                        FormResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            field = FieldHome.findByPrimaryKey( nIdField, plugin );

            if ( request.getParameter( PARAMETER_CANCEL ) == null )
            {
                String strError = getFieldData( request, field );

                if ( strError != null )
                {
                    return strError;
                }

                FieldHome.update( field, getPlugin( ) );
            }
        }
        else
        {
            return getJspManageForm( request );
        }

        if ( request.getParameter( PARAMETER_APPLY ) == null )
        {
            return getJspModifyEntry( request, field.getParentEntry( ).getIdEntry( ) );
        }
        return getJspModifyField( request, nIdField );
    }

    /**
     * Gets the confirmation page of delete field
     * @param request The HTTP request
     * @return the confirmation page of delete field
     */
    public String getConfirmRemoveField( HttpServletRequest request )
    {
        if ( ( request.getParameter( PARAMETER_ID_FIELD ) == null )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                        FormResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getHomeUrl( request );
        }

        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        UrlItem url = new UrlItem( JSP_DO_REMOVE_FIELD );
        url.addParameter( PARAMETER_ID_FIELD, strIdField + "#list" );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_FIELD, url.getUrl( ),
                AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform suppression field
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doRemoveField( HttpServletRequest request )
    {
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = -1;

        if ( ( strIdField == null )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                        FormResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getHomeUrl( request );
        }

        try
        {
            nIdField = Integer.parseInt( strIdField );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return getHomeUrl( request );
        }

        if ( nIdField != -1 )
        {
            FieldHome.remove( nIdField, getPlugin( ) );

            return getJspModifyEntry( request, _nIdEntry );
        }

        return getJspManageForm( request );
    }

    /**
     * Move up the field
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doMoveUpField( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        List<Field> listField;
        Field field;
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = -1;

        if ( ( request.getParameter( PARAMETER_ID_FIELD ) == null )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                        FormResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getHomeUrl( request );
        }

        try
        {
            nIdField = Integer.parseInt( strIdField );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return getHomeUrl( request );
        }

        field = FieldHome.findByPrimaryKey( nIdField, plugin );

        listField = FieldHome.getFieldListByIdEntry( field.getParentEntry( ).getIdEntry( ), plugin );

        int nIndexField = FormUtils.getIndexFieldInTheFieldList( nIdField, listField );

        if ( nIndexField != 0 )
        {
            int nNewPosition;
            Field fieldToInversePosition;
            fieldToInversePosition = listField.get( nIndexField - 1 );
            nNewPosition = fieldToInversePosition.getPosition( );
            fieldToInversePosition.setPosition( field.getPosition( ) );
            field.setPosition( nNewPosition );
            FieldHome.update( field, plugin );
            FieldHome.update( fieldToInversePosition, plugin );
        }

        return getJspModifyEntry( request, _nIdEntry );
    }

    /**
     * Move down the field
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doMoveDownField( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        List<Field> listField;
        Field field;
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = -1;

        if ( ( request.getParameter( PARAMETER_ID_FIELD ) == null )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                        FormResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getHomeUrl( request );
        }

        try
        {
            nIdField = Integer.parseInt( strIdField );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return getHomeUrl( request );
        }

        field = FieldHome.findByPrimaryKey( nIdField, plugin );

        listField = FieldHome.getFieldListByIdEntry( field.getParentEntry( ).getIdEntry( ), plugin );

        int nIndexField = FormUtils.getIndexFieldInTheFieldList( nIdField, listField );

        if ( nIndexField != ( listField.size( ) - 1 ) )
        {
            int nNewPosition;
            Field fieldToInversePosition;
            fieldToInversePosition = listField.get( nIndexField + 1 );
            nNewPosition = fieldToInversePosition.getPosition( );
            fieldToInversePosition.setPosition( field.getPosition( ) );
            field.setPosition( nNewPosition );
            FieldHome.update( field, plugin );
            FieldHome.update( fieldToInversePosition, plugin );
        }

        return getJspModifyEntry( request, _nIdEntry );
    }


    /**
     * Get the request data and if there is no error insert the data in the
     * field specified in parameter. return null if there is no error or else
     * return the error page url
     * @param request the request
     * @param field field
     * @return null if there is no error or else return the error page url
     */
    private String getFieldData( HttpServletRequest request, Field field )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strValue = request.getParameter( PARAMETER_VALUE );
        String strDefaultValue = request.getParameter( PARAMETER_DEFAULT_VALUE );
        String strNoDisplayTitle = request.getParameter( PARAMETER_NO_DISPLAY_TITLE );

        String strFieldError = EMPTY_STRING;

        if ( ( strTitle == null ) || strTitle.trim( ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_TITLE_FIELD;
        }
        else if ( ( strValue == null ) || EMPTY_STRING.equals( strValue ) )
        {
            strFieldError = FIELD_VALUE_FIELD;
        }
        else if ( !StringUtil.checkCodeKey( strValue ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_FIELD_VALUE_FIELD, AdminMessage.TYPE_STOP );
        }

        if ( !strFieldError.equals( EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, getLocale( ) ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                    AdminMessage.TYPE_STOP );
        }

        field.setTitle( strTitle );
        field.setValue( strValue );

        if ( strDefaultValue == null )
        {
            field.setDefaultValue( false );
        }
        else
        {
            field.setDefaultValue( true );
        }

        if ( strNoDisplayTitle == null )
        {
            field.setNoDisplayTitle( false );
        }
        else
        {
            field.setNoDisplayTitle( true );
        }

        return null; // No error
    }

    /**
     * return url of the jsp modify entry
     * @param request The HTTP request
     * @param nIdEntry the key of the entry to modify
     * @return return url of the jsp modify entry
     */
    private String getJspModifyEntry( HttpServletRequest request, int nIdEntry )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MODIFY_ENTRY + "?id_entry=" + nIdEntry;
    }

    /**
     * return url of the jsp modify field
     * @param request The HTTP request
     * @param nIdField the key of the field to modify
     * @return return url of the jsp modify field
     */
    private String getJspModifyField( HttpServletRequest request, int nIdField )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MODIFY_FIELD + "?id_field=" + nIdField;
    }

}
