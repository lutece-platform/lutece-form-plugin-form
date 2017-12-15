/*
 * Copyright (c) 2002-2017, Mairie de Paris
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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormHome;
import fr.paris.lutece.plugins.form.business.parameter.FormParameterHome;
import fr.paris.lutece.plugins.form.service.FormResourceIdService;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.business.style.Theme;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.portal.ThemesService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.filesystem.FileSystemUtil;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * Controller for the Advanced Parameters page of a Form
 */
@Controller( controllerJsp = FormAdvancedParametersJspBean.ADVANCED_PARAMETERS_CONTROLLER_JSP_NAME, controllerPath = FormAdvancedParametersJspBean.ADVANCED_PARAMETERS_CONTROLLER_PATH, right = FormAdvancedParametersJspBean.ADVANCED_PARAMETERS_CONTROLLER_RIGHT )
public class FormAdvancedParametersJspBean extends ModifyFormJspBean
{
    /**
     * Serial UID
     */
    private static final long serialVersionUID = -1937165761317528828L;

    // Controller attributes
    protected static final String ADVANCED_PARAMETERS_CONTROLLER_JSP_NAME = "ModifyFormAdvancedParameters.jsp";
    protected static final String ADVANCED_PARAMETERS_CONTROLLER_PATH = "jsp/admin/plugins/form";
    protected static final String ADVANCED_PARAMETERS_CONTROLLER_RIGHT = "FORM_MANAGEMENT";

    // Jsp
    private static final String JSP_FORM_ADVANCED_PARAMETERS = "jsp/admin/plugins/form/modifyForm/ModifyFormAdvancedParameters.jsp";

    // Templates
    private static final String TEMPLATE_MODIFY_FORM_ADVANCED_SETTINGS = "admin/plugins/form/modify_form/modify_advanced_settings.html";

    // Views
    private static final String VIEW_MODIFY_FORM_ADVANCED_PARAMETERS = "modifyFormAdvancedParameters";

    // Actions
    private static final String ACTION_SAVE_ADVANCED_PARAMETERS = "save";
    private static final String ACTION_APPLY_ADVANCED_PARAMETERS = "apply";

    // Parameters
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_ACTIVE_CAPTCHA = "active_captcha";
    private static final String PARAMETER_ACTIVE_STORE_ADRESSE = "active_store_adresse";
    private static final String PARAMETER_ACTIVE_REQUIREMENT = "active_requirement";
    private static final String PARAMETER_LIMIT_NUMBER_RESPONSE = "limit_number_response";
    private static final String PARAMETER_ID_MAILINIG_LIST = "id_mailing_list";
    private static final String PARAMETER_THEME_XPAGE = "id_theme_list";
    private static final String PARAMETER_INFORMATION_COMPLEMENTARY_1 = "information_complementary_1";
    private static final String PARAMETER_INFORMATION_COMPLEMENTARY_2 = "information_complementary_2";
    private static final String PARAMETER_INFORMATION_COMPLEMENTARY_3 = "information_complementary_3";
    private static final String PARAMETER_INFORMATION_COMPLEMENTARY_4 = "information_complementary_4";
    private static final String PARAMETER_INFORMATION_COMPLEMENTARY_5 = "information_complementary_5";
    private static final String PARAMETER_SUPPORT_HTTPS = "support_https";
    private static final String PARAMETER_ACTIVE_MYLUTECE_AUTHENTIFICATION = "active_mylutece_authentification";
    private static final String PARAMETER_FRONT_OFFICE_TITLE = "front_office_title";
    private static final String PARAMETER_IS_SHOWN_FRONT_OFFICE_TITLE = "is_shown_front_office_title";
    private static final String PARAMETER_WORKGROUP = "workgroup";
    private static final String PARAMETER_MAX_NUMBER_RESPONSE = "max_number_response";
    private static final String PARAMETER_FRONT_OFFICE_PICTURE = "front_office_picture";
    private static final String PARAMETER_IS_SHOW_PICTURE_FRONT_OFFICE = "is_shown_picture_front_office";

    // Marks
    private static final String MARK_FORM = "form";
    private static final String MARK_MAILING_REF_LIST = "mailing_list";
    private static final String MARK_USER_WORKGROUP_REF_LIST = "user_workgroup_list";
    private static final String MARK_IS_ACTIVE_CAPTCHA = "is_active_captcha";
    private static final String MARK_IS_ACTIVE_MYLUTECE_AUTHENTIFICATION = "is_active_mylutece_authentification";
    private static final String MARK_THEME_REF_LIST = "theme_list";
    private static final String MARK_CATEGORY_LIST = "category_list";
    private static final String MARK_UPLOAD_FRONT_OFFICE_PICTURE = "frontOfficePicture";
    private static final String MARK_UPLOAD_FRONT_OFFICE_PICTURE_SRC = "frontOfficePictureSource";

    // Properties
    private static final String PROPERTY_MODIFY_FORM_TITLE = "form.modifyForm.title";
    private static final String PROPERTY_NOTHING = "form.createForm.select.nothing";
    private static final String FORM_PARAMETER_MAX_FILE_SIZE_KEY = "front_office_picture_max_size";

    // Messages
    private static final String MESSAGE_FRONT_OFFICE_ERROR_NOT_AN_IMAGE = "form.message.frontOfficePicture.error.notImage";
    private static final String MESSAGE_FRONT_OFFICE_ERROR_SIZE = "form.message.frontOfficePicture.error.imageSize";

    // Plugin names
    private static final String JCAPTCHA_PLUGIN = "jcaptcha";
    private static final String MYLUTECE_PLUGIN = "mylutece";

    /**
     * Get the form advanced settings page
     * 
     * @param request
     *            The request
     * @return The HTML to display
     */
    @View( value = VIEW_MODIFY_FORM_ADVANCED_PARAMETERS, defaultView = true )
    public String getViewModifyFormAdvancedParameters( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = -1;
        Form form;
        AdminUser adminUser = getUser( );

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            nIdForm = Integer.parseInt( strIdForm );
            setFormId( nIdForm );
        }
        else
        {
            return getManageForm( request );
        }

        form = FormHome.findByPrimaryKey( nIdForm, plugin );

        if ( ( form == null ) || !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_MODIFY, adminUser ) )
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

        String strFrontOfficePictureSource = StringUtils.EMPTY;
        File filePicture = null;
        if ( request instanceof MultipartHttpServletRequest )
        {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            FileItem fileItemPicture = multipartRequest.getFile( PARAMETER_FRONT_OFFICE_PICTURE );

            filePicture = new File( );
            filePicture.setTitle( fileItemPicture.getName( ) );
            filePicture.setMimeType( fileItemPicture.getContentType( ) );
        }
        else
        {
            int nIdPictureFile = form.getIdPictureFile( );
            if ( nIdPictureFile != NumberUtils.INTEGER_ZERO )
            {
                filePicture = FileHome.findByPrimaryKey( nIdPictureFile );
                if ( filePicture != null )
                {
                    PhysicalFile physicalFileLazyLoading = filePicture.getPhysicalFile( );
                    if ( physicalFileLazyLoading != null )
                    {
                        PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey( physicalFileLazyLoading.getIdPhysicalFile( ) );
                        if ( physicalFile != null && physicalFile.getValue( ) != null )
                        {
                            byte [ ] bEncodedPhysicalFile = new Base64( ).encode( physicalFile.getValue( ) );
                            strFrontOfficePictureSource = new String( bEncodedPhysicalFile );
                        }
                    }
                }
            }
        }

        Map<String, Object> model = new HashMap<String, Object>( );

        model.put( MARK_FORM, form );
        model.put( MARK_MAILING_REF_LIST, refMailingList );
        model.put( MARK_USER_WORKGROUP_REF_LIST, refListWorkGroups );
        model.put( MARK_IS_ACTIVE_CAPTCHA, PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) );
        model.put( MARK_IS_ACTIVE_MYLUTECE_AUTHENTIFICATION, PluginService.isPluginEnable( MYLUTECE_PLUGIN ) );
        model.put( MARK_THEME_REF_LIST, themesRefList );
        model.put( MARK_CATEGORY_LIST, getCategoriesReferenceList( plugin ) );
        model.put( MARK_UPLOAD_FRONT_OFFICE_PICTURE, filePicture );
        model.put( MARK_UPLOAD_FRONT_OFFICE_PICTURE_SRC, strFrontOfficePictureSource );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_FORM_ADVANCED_SETTINGS, locale, model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Save the modification of the advanced parameters for the form
     * 
     * @param request
     *            The HttpServletRequest
     * @return redirect to the page of the forms management if there are no errors
     */
    @Action( value = ACTION_SAVE_ADVANCED_PARAMETERS )
    public String doSaveModifyFormAdvancedParameters( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return redirect( request, getJspManageForm( request ) );
        }

        int nIdForm = NumberUtils.toInt( strIdForm, NumberUtils.INTEGER_MINUS_ONE );

        if ( nIdForm != NumberUtils.INTEGER_MINUS_ONE )
        {
            String error = manageAdvancedParameters( request, nIdForm );
            if ( error != null )
            {
                return error;
            }
        }

        return redirect( request, getJspManageForm( request ) );
    }

    /**
     * Apply the modification of the advanced parameters for the form
     * 
     * @param request
     *            The HttpServletRequest
     * @return redirect to the page of the forms management if there are no errors
     */
    @Action( value = ACTION_APPLY_ADVANCED_PARAMETERS )
    public String doApplyModifyFormAdvancedParameters( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return redirect( request, getJspManageForm( request ) );
        }

        int nIdForm = NumberUtils.toInt( strIdForm, NumberUtils.INTEGER_MINUS_ONE );

        if ( nIdForm != NumberUtils.INTEGER_MINUS_ONE )
        {
            String error = manageAdvancedParameters( request, nIdForm );
            if ( error != null )
            {
                return error;
            }

            return redirect( request, getJspModifyFormAdvancedParameters( request, nIdForm ) );
        }

        return redirect( request, getJspManageForm( request ) );
    }

    /**
     * Manage the advanced parameters of a form from the data from the request
     * 
     * @param request
     *            The HttpServletRequest
     * @param nIdForm
     *            The id of the Form
     */
    private String manageAdvancedParameters( HttpServletRequest request, int nIdForm )
    {
        Plugin plugin = getPlugin( );
        Form updatedForm = FormHome.findByPrimaryKey( nIdForm, plugin );

        String strError = getFormData( request, updatedForm );
        if ( strError == null )
        {
            strError = getFormAdvancedParametersData( request, updatedForm );
        }

        if ( strError != null )
        {
            return redirect( request, strError );
        }

        updatedForm.setIdForm( nIdForm );

        // We get the saved form before it is updated
        Form form = FormHome.findByPrimaryKey( nIdForm, plugin );
        if ( updatedForm.getIdPictureFile( ) == NumberUtils.INTEGER_ZERO )
        {
            updatedForm.setIdPictureFile( form.getIdPictureFile( ) );
        }

        // We update the database with the modification made to the form
        FormHome.update( updatedForm, getPlugin( ) );

        // Remove the previous picture if a new one is uploaded
        int nIdPictureFile = form.getIdPictureFile( );
        if ( nIdPictureFile != NumberUtils.INTEGER_ZERO && nIdPictureFile != updatedForm.getIdPictureFile( ) )
        {
            FileHome.remove( nIdPictureFile );
        }

        if ( PluginService.isPluginEnable( MYLUTECE_PLUGIN ) && updatedForm.isActiveMyLuteceAuthentification( ) && !form.isActiveMyLuteceAuthentification( ) )
        {
            FormUtils.activateMyLuteceAuthentification( updatedForm, plugin, getLocale( ), request );
        }
        else
            if ( PluginService.isPluginEnable( MYLUTECE_PLUGIN ) && !updatedForm.isActiveMyLuteceAuthentification( ) && form.isActiveMyLuteceAuthentification( ) )
            {
                FormUtils.deactivateMyLuteceAuthentification( updatedForm, plugin );
            }

        return null;
    }

    /**
     * Update the advanced parameters of a form
     * 
     * @param request
     *            The request
     * @param form
     *            The form to update the advanced parameters of
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
        String strMaxNumberResponse = request.getParameter( PARAMETER_MAX_NUMBER_RESPONSE );
        String strIsShownPictureFrontOffice = request.getParameter( PARAMETER_IS_SHOW_PICTURE_FRONT_OFFICE );

        form.setWorkgroup( strWorkgroup );

        try
        {
            int nMailingListId = Integer.parseInt( strMailingListId );
            form.setIdMailingList( nMailingListId );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne.getMessage( ), ne );

            return redirect( request, getHomeUrl( request ) );
        }

        if ( strFrontOfficeTitle != null )
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

        try
        {
            int nMaxNumberResponse = Integer.parseInt( strMaxNumberResponse );
            form.setMaxNumberResponse( nMaxNumberResponse >= 0 ? nMaxNumberResponse : 0 );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne.getMessage( ), ne );

            return redirect( request, getHomeUrl( request ) );
        }

        if ( strIsShownPictureFrontOffice != null )
        {
            form.setIsShownFrontOfficePicture( Boolean.TRUE );
        }

        if ( request instanceof MultipartHttpServletRequest )
        {
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            FileItem fileItemPictureFrontOffice = multipartHttpServletRequest.getFile( PARAMETER_FRONT_OFFICE_PICTURE );
            if ( fileItemPictureFrontOffice != null )
            {
                String strError = manageFrontOfficePicture( multipartHttpServletRequest, form, fileItemPictureFrontOffice );
                if ( strError != null )
                {
                    return redirect( request, strError );
                }
            }
        }

        return null; // No error
    }

    /**
     * Manage the Front office picture which have been uploaded. Check the constraint and create the associated File object and attached it to the form.
     * 
     * @param request
     *            The HttpServletRequest
     * @param form
     *            The Form to attached the picture
     * @param fileItemPictureFrontOffice
     *            The picture uploaded
     * @return an error if the uploaded file doesn't respect the constraint return null otherwise
     */
    private String manageFrontOfficePicture( HttpServletRequest request, Form form, FileItem fileItemPictureFrontOffice )
    {
        if ( form != null && fileItemPictureFrontOffice != null )
        {
            // check constraints of the uploaded front office picture
            String strError = checkFrontOfficePictureUploaded( request, fileItemPictureFrontOffice );
            if ( strError != null )
            {
                return strError;
            }

            // If an picture is already present in form and the user don't upload another the
            // content of the uploaded file will be empty
            if ( fileItemPictureFrontOffice.get( ) != null )
            {
                File fileFrontOfficePicture = new File( );
                fileFrontOfficePicture.setTitle( fileItemPictureFrontOffice.getName( ) );
                fileFrontOfficePicture.setSize( (int) fileItemPictureFrontOffice.getSize( ) );
                fileFrontOfficePicture.setMimeType( FileSystemUtil.getMIMEType( fileFrontOfficePicture.getTitle( ) ) );

                PhysicalFile physicalFile = new PhysicalFile( );
                physicalFile.setValue( fileItemPictureFrontOffice.get( ) );
                fileFrontOfficePicture.setPhysicalFile( physicalFile );

                int nIdPictureFile = FileHome.create( fileFrontOfficePicture );
                form.setIdPictureFile( nIdPictureFile );
            }
        }

        // No error
        return null;
    }

    /**
     * Check if the FileItem given respect the constraints
     * 
     * @param fileItem
     *            The FileItem to analyze
     * @return an error if the file doesn't respect the constraints return null otherwise
     */
    private String checkFrontOfficePictureUploaded( HttpServletRequest request, FileItem fileItem )
    {
        if ( fileItem != null && fileItem.get( ) != null )
        {
            // Check if the uploaded file is an image
            BufferedImage image = null;
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( fileItem.get( ) );
            try
            {
                image = ImageIO.read( byteArrayInputStream );
            }
            catch( IOException e )
            {
                AppLogService.error( e );
            }
            finally
            {
                try
                {
                    byteArrayInputStream.close( );
                }
                catch( IOException e )
                {
                    AppLogService.error( e );
                }
            }
            if ( image == null && StringUtils.isNotBlank( fileItem.getName( ) ) )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_FRONT_OFFICE_ERROR_NOT_AN_IMAGE, AdminMessage.TYPE_STOP );
            }

            // Check if the uploaded file doesn't exceed the maximum file size authorized
            ReferenceItem referenceItemFormMaxFileSize = FormParameterHome.findByKey( FORM_PARAMETER_MAX_FILE_SIZE_KEY, getPlugin( ) );
            if ( referenceItemFormMaxFileSize != null )
            {
                long nMaxFrontOfficeFileSize = NumberUtils.toLong( referenceItemFormMaxFileSize.getName( ), NumberUtils.LONG_MINUS_ONE );
                long nUploadeFileSize = fileItem.getSize( );
                if ( nUploadeFileSize > nMaxFrontOfficeFileSize )
                {
                    Object [ ] objectMessageArgs = {
                        nMaxFrontOfficeFileSize
                    };
                    return AdminMessageService.getMessageUrl( request, MESSAGE_FRONT_OFFICE_ERROR_SIZE, objectMessageArgs, AdminMessage.TYPE_STOP );
                }
            }
        }

        // No error
        return null;
    }

    /**
     * Get the JSP url to the form advanced parameters page
     * 
     * @param request
     *            The request
     * @param nIdForm
     *            The id of the form
     * @return The URL of the form advanced parameters page
     */
    protected String getJspModifyFormAdvancedParameters( HttpServletRequest request, int nIdForm )
    {
        return AppPathService.getBaseUrl( request ) + JSP_FORM_ADVANCED_PARAMETERS + "?id_form=" + nIdForm;
    }
}
