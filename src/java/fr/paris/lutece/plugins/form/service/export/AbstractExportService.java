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
package fr.paris.lutece.plugins.form.service.export;

import fr.paris.lutece.plugins.form.business.ExportFormat;
import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormSubmit;
import fr.paris.lutece.plugins.form.business.FormSubmitHome;
import fr.paris.lutece.plugins.form.business.exporttype.IExportType;
import fr.paris.lutece.plugins.form.service.IResponseService;
import fr.paris.lutece.plugins.form.service.parameter.FormParameterService;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.plugins.genericattributes.business.ResponseFilter;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.UniqueIDGenerator;

import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Locale;


/**
 *
 * AbstractFormExportService
 *
 */
public abstract class AbstractExportService implements IExportService
{
    // CONSTANTS
    protected static final String SQL_FILTER_ENTRY_POS = " ent.pos ";
    protected static final String XSL_UNIQUE_PREFIX_ID = UniqueIDGenerator.getNewId(  ) + "form-";
    protected static final String CONSTANT_CSV = "csv";

    // PROPERTIES
    private static final String PROPERTY_FILE_FOLDER_PATH = "form.export.file.folder.path";

    // VARIABLE
    private String _strKey;
    private String _strTitleI18nKey;

    /**
     * Do export the form responses
     * @param form the form
     * @param listFormSubmit the list of form submits
     * @param strFolderPath the folder path
     * @param exportFormat the export format
     * @param strEncoding the encoding
     * @param sbLog the log
     * @param plugin the plugin
     */
    public abstract void doExport( Form form, List<FormSubmit> listFormSubmit, String strFolderPath,
        ExportFormat exportFormat, String strEncoding, StringBuilder sbLog, Plugin plugin );

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey(  )
    {
        return _strKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setKey( String strKey )
    {
        _strKey = strKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( Locale locale )
    {
        if ( locale == null )
        {
            return I18nService.getLocalizedString( _strTitleI18nKey, I18nService.getDefaultLocale(  ) );
        }

        return I18nService.getLocalizedString( _strTitleI18nKey, locale );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTitleI18nKey( String strTitleI18nKey )
    {
        _strTitleI18nKey = strTitleI18nKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doExport( Form form, StringBuilder sbLog, ExportFormat exportFormat, Plugin plugin )
    {
        IExportType exportType = FormParameterService.getService(  ).getExportDaemonType(  );

        // Since it is a daemon, no Locale available. We take the default Locale
        Locale locale = I18nService.getDefaultLocale(  );

        // Filter the responses given the export type defined in the advanced parameters of the plugin-form
        ResponseFilter filter = exportType.getResponseFilter( form, locale );
        List<FormSubmit> listFormSubmit = FormSubmitHome.getFormSubmitList( filter, plugin );
        IResponseService responseService = SpringContextService.getBean( FormUtils.BEAN_FORM_RESPONSE_SERVICE );

        for ( FormSubmit formSubmit : listFormSubmit )
        {
            filter = new ResponseFilter(  );
            filter.setIdResource( formSubmit.getIdFormSubmit(  ) );
            filter.setOrderBy( SQL_FILTER_ENTRY_POS );
            filter.setOrderByAsc( true );
            formSubmit.setListResponse( responseService.getResponseList( filter, false ) );
        }

        // Get the folder path from the file "form.properties"
        String strFolderPath = AppPathService.getAbsolutePathFromRelativePath( AppPropertiesService.getProperty( 
                    PROPERTY_FILE_FOLDER_PATH ) );

        // Get the encoding defined in the advanced parameters of the plugin-form
        String strEncoding = StringUtils.EMPTY;

        if ( CONSTANT_CSV.equals( exportFormat.getExtension(  ).trim(  ) ) )
        {
            strEncoding = FormParameterService.getService(  ).getExportCSVEncoding(  );
        }
        else
        {
            strEncoding = FormParameterService.getService(  ).getExportXMLEncoding(  );
        }

        doExport( form, listFormSubmit, strFolderPath, exportFormat, strEncoding, sbLog, plugin );

        exportType.saveExport( listFormSubmit, locale );
    }
}
