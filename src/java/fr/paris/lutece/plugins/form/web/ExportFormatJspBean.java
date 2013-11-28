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

import fr.paris.lutece.plugins.form.business.ExportFormat;
import fr.paris.lutece.plugins.form.business.ExportFormatHome;
import fr.paris.lutece.plugins.form.service.ExportFormatResourceIdService;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.fileupload.FileItem;
import org.xml.sax.InputSource;


/**
 * 
 * class ExportFormatJspBean
 * 
 */
public class ExportFormatJspBean extends PluginAdminPageJspBean
{
    /**
     * Right to manage forms
     */
    public static final String RIGHT_MANAGE_FORM = FormJspBean.RIGHT_MANAGE_FORM;

    private static final long serialVersionUID = 3584561879462442339L;

    //	templates
    private static final String TEMPLATE_MANAGE_EXPORT = "admin/plugins/form/manage_export_format.html";
    private static final String TEMPLATE_CREATE_EXPORT = "admin/plugins/form/create_export_format.html";
    private static final String TEMPLATE_MODIFY_EXPORT = "admin/plugins/form/modify_export_format.html";

    //	Markers
    private static final String MARK_EXPORT_LIST = "export_list";
    private static final String MARK_EXPORT = "export";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";

    //	parameters form
    private static final String PARAMETER_ID_EXPORT = "id_export";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_DESCRIPTION = "description";
    private static final String PARAMETER_EXTENSION = "extension";
    private static final String PARAMETER_PAGE_INDEX = "page_index";

    //	 other constants
    private static final String EMPTY_STRING = "";

    //	message
    private static final String MESSAGE_CONFIRM_REMOVE_EXPORT = "form.message.confirmRemoveExportFormat";
    private static final String MESSAGE_MANDATORY_FIELD = "form.message.mandatory.field";
    private static final String FIELD_TITLE = "form.createExportFormat.labelTitle";
    private static final String FIELD_DESCRIPTION = "form.createExportFormat.labelDescription";
    private static final String FIELD_EXTENSION = "form.createExportFormat.labelExtension";
    private static final String FIELD_XSL = "form.createExportFormat.labelXsl";
    private static final String MESSAGE_STYLESHEET_NOT_VALID = "form.message.stylesheetNotValid";

    //	properties
    private static final String PROPERTY_ITEM_PER_PAGE = "form.itemsPerPage";
    private static final String PROPERTY_MANAGE_EXPORT_FORMAT_TITLE = "form.manageExportFormat.pageTitle";
    private static final String PROPERTY_MODIFY_EXPORT_FORMAT_TITLE = "form.modifyExportFormat.title";
    private static final String PROPERTY_CREATE_EXPORT_FORMAT_TITLE = "form.createExportFormat.title";

    //Jsp Definition
    private static final String JSP_MANAGE_EXPORT_FORMAT = "jsp/admin/plugins/form/ManageExportFormat.jsp";
    private static final String JSP_DO_REMOVE_EXPORT_FORMAT = "jsp/admin/plugins/form/DoRemoveExportFormat.jsp";

    //	session fields
    private int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 15 );
    private String _strCurrentPageIndexExport;
    private int _nItemsPerPageForm;

    /**
     * Return management export format ( list of export format )
     * @param request The Http request
     * @return Html form
     */
    public String getManageExportFormat( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        Locale locale = getLocale( );
        HashMap<String, Object> model = new HashMap<String, Object>( );
        List<ExportFormat> listExportFormat = ExportFormatHome.getList( plugin );
        listExportFormat = (List<ExportFormat>) RBACService.getAuthorizedCollection( listExportFormat,
                ExportFormatResourceIdService.PERMISSION_MANAGE, getUser( ) );
        _strCurrentPageIndexExport = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                _strCurrentPageIndexExport );
        _nItemsPerPageForm = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE,
                _nItemsPerPageForm, _nDefaultItemsPerPage );

        LocalizedPaginator<ExportFormat> paginator = new LocalizedPaginator<ExportFormat>( listExportFormat,
                _nItemsPerPageForm, getJspManageExportFormat( request ), PARAMETER_PAGE_INDEX,
                _strCurrentPageIndexExport, getLocale( ) );

        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + _nItemsPerPageForm );
        model.put( MARK_EXPORT_LIST, paginator.getPageItems( ) );
        setPageTitleProperty( PROPERTY_MANAGE_EXPORT_FORMAT_TITLE );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_EXPORT, locale, model );

        //ReferenceList refMailingList;
        //refMailingList=AdminMailingListService.getMailingLists(adminUser);
        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Gets the export format creation page
     * @param request The HTTP request
     * @return The export format creation page
     */
    public String getCreateExportFormat( HttpServletRequest request )
    {
        if ( !RBACService.isAuthorized( ExportFormat.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                ExportFormatResourceIdService.PERMISSION_MANAGE, getUser( ) ) )
        {
            return getManageExportFormat( request );
        }

        Locale locale = getLocale( );
        HashMap<String, Object> model = new HashMap<String, Object>( );
        setPageTitleProperty( PROPERTY_CREATE_EXPORT_FORMAT_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_EXPORT, locale, model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the export format creation
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doCreateExportFormat( HttpServletRequest request )
    {
        if ( !RBACService.isAuthorized( ExportFormat.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                ExportFormatResourceIdService.PERMISSION_MANAGE, getUser( ) ) )
        {
            return getJspManageExportFormat( request );
        }

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        ExportFormat exportFormat = new ExportFormat( );
        String strError = getExportFormatData( multipartRequest, exportFormat );

        if ( strError != null )
        {
            return strError;
        }

        ExportFormatHome.create( exportFormat, getPlugin( ) );

        return getJspManageExportFormat( request );
    }

    /**
     * Gets the export format modification page
     * @param request The HTTP request
     * @return The export format creation page
     */
    public String getModifyExportFormat( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        Locale locale = getLocale( );
        ExportFormat exportFormat;
        String strIdExport = request.getParameter( PARAMETER_ID_EXPORT );
        HashMap<String, Object> model = new HashMap<String, Object>( );
        int nIdExport = -1;

        if ( ( strIdExport != null )
                && !strIdExport.equals( EMPTY_STRING )
                && RBACService.isAuthorized( ExportFormat.RESOURCE_TYPE, strIdExport,
                        ExportFormatResourceIdService.PERMISSION_MANAGE, getUser( ) ) )
        {
            try
            {
                nIdExport = Integer.parseInt( strIdExport );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getManageExportFormat( request );
            }
        }
        else
        {
            return getManageExportFormat( request );
        }

        exportFormat = ExportFormatHome.findByPrimaryKey( nIdExport, plugin );
        model.put( MARK_EXPORT, exportFormat );
        setPageTitleProperty( PROPERTY_MODIFY_EXPORT_FORMAT_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_EXPORT, locale, model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the export format modification
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doModifyExportFormat( HttpServletRequest request )
    {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Plugin plugin = getPlugin( );
        ExportFormat exportFormat;
        String strIdExport = multipartRequest.getParameter( PARAMETER_ID_EXPORT );
        int nIdExport = -1;

        if ( ( strIdExport != null )
                && !strIdExport.equals( EMPTY_STRING )
                && RBACService.isAuthorized( ExportFormat.RESOURCE_TYPE, strIdExport,
                        ExportFormatResourceIdService.PERMISSION_MANAGE, getUser( ) ) )
        {
            try
            {
                nIdExport = Integer.parseInt( strIdExport );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getHomeUrl( request );
            }
        }
        else
        {
            return getHomeUrl( request );
        }

        exportFormat = ExportFormatHome.findByPrimaryKey( nIdExport, plugin );

        String strError = getExportFormatData( multipartRequest, exportFormat );

        if ( strError != null )
        {
            return strError;
        }

        ExportFormatHome.update( exportFormat, getPlugin( ) );

        return getJspManageExportFormat( request );
    }

    /**
     * Gets the confirmation page of delete export format
     * @param request The HTTP request
     * @return the confirmation page of delete export format
     */
    public String getConfirmRemoveExportFormat( HttpServletRequest request )
    {
        if ( request.getParameter( PARAMETER_ID_EXPORT ) == null )
        {
            return getHomeUrl( request );
        }

        String strIdExport = request.getParameter( PARAMETER_ID_EXPORT );
        UrlItem url = new UrlItem( JSP_DO_REMOVE_EXPORT_FORMAT );
        url.addParameter( PARAMETER_ID_EXPORT, strIdExport );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_EXPORT, url.getUrl( ),
                AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform the export format supression
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doRemoveExportFormat( HttpServletRequest request )
    {
        String strIdExport = request.getParameter( PARAMETER_ID_EXPORT );
        int nIdExport = -1;

        if ( ( request.getParameter( PARAMETER_ID_EXPORT ) == null )
                || !RBACService.isAuthorized( ExportFormat.RESOURCE_TYPE, strIdExport,
                        ExportFormatResourceIdService.PERMISSION_MANAGE, getUser( ) ) )
        {
            return getJspManageExportFormat( request );
        }

        try
        {
            nIdExport = Integer.parseInt( strIdExport );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return getHomeUrl( request );
        }

        if ( nIdExport != -1 )
        {
            ExportFormatHome.remove( nIdExport, getPlugin( ) );
        }

        return getJspManageExportFormat( request );
    }

    /**
     * Get the request data and if there is no error insert the data in the
     * exportFormat object specified in parameter.
     * return null if there is no error or else return the error page url
     * @param multipartRequest the request
     * @param exportFormat the exportFormat Object
     * @return null if there is no error or else return the error page url
     */
    private String getExportFormatData( MultipartHttpServletRequest multipartRequest, ExportFormat exportFormat )
    {
        String strTitle = multipartRequest.getParameter( PARAMETER_TITLE );
        String strDescription = multipartRequest.getParameter( PARAMETER_DESCRIPTION );
        String strExtension = multipartRequest.getParameter( PARAMETER_EXTENSION );

        String strFieldError = EMPTY_STRING;
        FileItem fileSource = multipartRequest.getFile( Parameters.STYLESHEET_SOURCE );
        String strFilename = FileUploadService.getFileNameOnly( fileSource );

        if ( ( strTitle == null ) || strTitle.trim( ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_TITLE;
        }
        else if ( ( strDescription == null ) || strDescription.trim( ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_DESCRIPTION;
        }
        else if ( ( strExtension == null ) || strExtension.trim( ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_EXTENSION;
        }
        else if ( ( strFilename == null ) || ( strFilename.equals( "" ) && ( exportFormat.getXsl( ) == null ) ) )
        {
            strFieldError = FIELD_XSL;
        }

        //Mandatory fields
        if ( !strFieldError.equals( EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, getLocale( ) ) };

            return AdminMessageService.getMessageUrl( multipartRequest, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                    AdminMessage.TYPE_STOP );
        }

        byte[] baXslSource = fileSource.get( );

        //Check the XML validity of the XSL stylesheet
        if ( ( strFilename != null ) && !strFilename.equals( "" ) && ( isValid( baXslSource ) != null ) )
        {
            Object[] args = { isValid( baXslSource ) };

            return AdminMessageService.getMessageUrl( multipartRequest, MESSAGE_STYLESHEET_NOT_VALID, args,
                    AdminMessage.TYPE_STOP );
        }

        exportFormat.setTitle( strTitle );
        exportFormat.setDescription( strDescription );
        exportFormat.setExtension( strExtension );

        if ( ( strFilename != null ) && !strFilename.equals( "" ) )
        {
            exportFormat.setXsl( baXslSource );
        }

        return null;
    }

    /**
     * Use parsing for validate the modify xsl file
     * @param baXslSource the xsl source
     * @return the message exception when the validation is false
     */
    private String isValid( byte[] baXslSource )
    {
        String strError = null;

        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance( );
            SAXParser analyzer = factory.newSAXParser( );
            InputSource is = new InputSource( new ByteArrayInputStream( baXslSource ) );
            analyzer.getXMLReader( ).parse( is );
        }
        catch ( Exception e )
        {
            strError = e.getMessage( );
        }

        return strError;
    }

    /**
     * return the url of manage export format
     * @param request the request
     * @return the url of manage export format
     */
    private String getJspManageExportFormat( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_EXPORT_FORMAT;
    }
}
