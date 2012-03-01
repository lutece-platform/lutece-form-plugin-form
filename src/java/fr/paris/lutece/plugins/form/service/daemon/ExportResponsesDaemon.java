/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
package fr.paris.lutece.plugins.form.service.daemon;

import fr.paris.lutece.plugins.form.business.ExportFormat;
import fr.paris.lutece.plugins.form.business.ExportFormatHome;
import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormFilter;
import fr.paris.lutece.plugins.form.business.FormHome;
import fr.paris.lutece.plugins.form.business.FormSubmit;
import fr.paris.lutece.plugins.form.business.FormSubmitHome;
import fr.paris.lutece.plugins.form.business.ResponseFilter;
import fr.paris.lutece.plugins.form.business.exporttype.IExportType;
import fr.paris.lutece.plugins.form.service.FormPlugin;
import fr.paris.lutece.plugins.form.service.IResponseService;
import fr.paris.lutece.plugins.form.service.parameter.FormParameterService;
import fr.paris.lutece.plugins.form.utils.FileUtils;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.html.XmlTransformerService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.UniqueIDGenerator;
import fr.paris.lutece.util.xml.XmlUtil;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;

import java.util.List;
import java.util.Locale;


/**
 *
 * ExportResponsesDaemon
 *
 */
public class ExportResponsesDaemon extends Daemon
{
    private static final String SQL_FILTER_ENTRY_POS = " ent.pos ";
    private static final String XSL_UNIQUE_PREFIX_ID = UniqueIDGenerator.getNewId(  ) + "form-";
    private static final String CONSTANT_CSV = "csv";

    // PROPERTIES
    private static final String PROPERTY_FILE_FOLDER_PATH = "form.export.file.folder.path";

    /**
     * {@inheritDoc}
     */
    public void run(  )
    {
        StringBuilder sbLog = new StringBuilder(  );
        FormParameterService formParamService = FormParameterService.getService(  );
        int nIdExport = formParamService.getIdExportResponsesDaemon(  );
        Plugin plugin = PluginService.getPlugin( FormPlugin.PLUGIN_NAME );
        ExportFormat exportFormat = ExportFormatHome.findByPrimaryKey( nIdExport, plugin );

        if ( exportFormat != null )
        {
            List<Form> listForms = FormHome.getFormList( new FormFilter(  ), plugin );

            if ( ( listForms != null ) && !listForms.isEmpty(  ) )
            {
                for ( Form form : listForms )
                {
                    if ( form != null )
                    {
                        exportFormResponses( form, sbLog, exportFormat, plugin );
                    }
                }
            }
            else
            {
                sbLog.append( "\nThere are no forms in the application. No export done." );
            }
        }
        else
        {
            sbLog.append( 
                "\nInvalid export format. The daemon is not well configured. \nPlease configure the daemon export format in the " +
                "advanced parameters of the plugin-form." );
        }

        if ( StringUtils.isBlank( sbLog.toString(  ) ) )
        {
            sbLog.append( "\nNo responses to export" );
        }

        setLastRunLogs( sbLog.toString(  ) );
    }

    /**
     * Export the form responses
     * @param form the form
     * @param sbLog the log
     * @param exportFormat the export format
     * @param plugin the plugin
     */
    private void exportFormResponses( Form form, StringBuilder sbLog, ExportFormat exportFormat, Plugin plugin )
    {
        sbLog.append( "\nExporting responses for form ID " + form.getIdForm(  ) );

        IExportType exportType = FormParameterService.getService(  ).getExportDaemonType(  );

        // Since it is a daemon, no Locale available. We take the default Locale
        Locale locale = I18nService.getDefaultLocale(  );
        ResponseFilter filter = exportType.getResponseFilter( form, locale );

        List<FormSubmit> listFormSubmit = FormSubmitHome.getFormSubmitList( filter, plugin );
        IResponseService responseService = (IResponseService) SpringContextService.getBean( FormUtils.BEAN_FORM_RESPONSE_SERVICE );

        for ( FormSubmit formSubmit : listFormSubmit )
        {
            filter = new ResponseFilter(  );
            filter.setIdForm( formSubmit.getIdFormSubmit(  ) );
            filter.setOrderBy( SQL_FILTER_ENTRY_POS );
            filter.setOrderByAsc( true );
            formSubmit.setListResponse( responseService.getResponseList( filter, false ) );
        }

        String strFormatExtension = exportFormat.getExtension(  ).trim(  );
        String strFileName = FileUtils.buildFileName( form.getTitle(  ), strFormatExtension );
        String strFolderPath = AppPathService.getAbsolutePathFromRelativePath( AppPropertiesService.getProperty( 
                    PROPERTY_FILE_FOLDER_PATH ) );
        boolean bHasFormSubmit = false;

        if ( ( listFormSubmit != null ) && !listFormSubmit.isEmpty(  ) )
        {
            XmlTransformerService xmlTransformerService = new XmlTransformerService(  );
            String strXmlSource = XmlUtil.getXmlHeader(  ) +
                FormUtils.getXmlResponses( null, form, listFormSubmit, null, plugin );
            String strXslUniqueId = XSL_UNIQUE_PREFIX_ID + exportFormat.getIdExport(  );
            String strFileOutPut = xmlTransformerService.transformBySourceWithXslCache( strXmlSource,
                    exportFormat.getXsl(  ), strXslUniqueId, null, null );

            String strEncoding = StringUtils.EMPTY;

            if ( CONSTANT_CSV.equals( strFormatExtension ) )
            {
                strEncoding = FormParameterService.getService(  ).getExportCSVEncoding(  );
            }
            else
            {
                strEncoding = FormParameterService.getService(  ).getExportXMLEncoding(  );
            }

            try
            {
                FileUtils.createFile( strFolderPath, strFileName, strFileOutPut, strEncoding );
                sbLog.append( "\n\t" + listFormSubmit.size(  ) + " responses exported." );
            }
            catch ( IOException e )
            {
                AppLogService.error( e.getMessage(  ), e );
                sbLog.append( "\n\tERROR when writing file " + strFileName );
            }

            bHasFormSubmit = true;
        }

        if ( !bHasFormSubmit )
        {
            // Delete
            try
            {
                FileUtils.deleteFile( strFolderPath, strFileName );
                sbLog.append( "\n\tNo response exported. Deleting file " + strFileName + "..." );
            }
            catch ( IOException e )
            {
                AppLogService.error( e.getMessage(  ), e );
                sbLog.append( "\n\tERROR when deleting file " + strFileName );
            }
        }

        exportType.saveExport( listFormSubmit, locale );
    }
}
