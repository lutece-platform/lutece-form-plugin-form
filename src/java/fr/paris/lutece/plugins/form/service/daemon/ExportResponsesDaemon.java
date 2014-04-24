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
package fr.paris.lutece.plugins.form.service.daemon;

import fr.paris.lutece.plugins.form.business.ExportFormat;
import fr.paris.lutece.plugins.form.business.ExportFormatHome;
import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormFilter;
import fr.paris.lutece.plugins.form.business.FormHome;
import fr.paris.lutece.plugins.form.service.FormPlugin;
import fr.paris.lutece.plugins.form.service.export.IExportService;
import fr.paris.lutece.plugins.form.service.parameter.FormParameterService;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.commons.lang.StringUtils;

import java.util.List;


/**
 *
 * ExportResponsesDaemon
 *
 */
public class ExportResponsesDaemon extends Daemon
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void run(  )
    {
        StringBuilder sbLog = new StringBuilder(  );
        FormParameterService formParamService = FormParameterService.getService(  );
        int nIdExport = formParamService.getIdExportResponsesDaemon(  );
        Plugin plugin = PluginService.getPlugin( FormPlugin.PLUGIN_NAME );
        ExportFormat exportFormat = ExportFormatHome.findByPrimaryKey( nIdExport, plugin );

        if ( exportFormat != null )
        {
            FormFilter filter = new FormFilter(  );
            filter.setIdState( 1 );

            List<Form> listForms = FormHome.getFormList( filter, plugin );

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

        IExportService exportService = FormParameterService.getService(  ).getFileExportDaemonType(  );

        if ( exportService != null )
        {
            exportService.doExport( form, sbLog, exportFormat, plugin );
        }
        else
        {
            AppLogService.error( "ExportResponsesDaemon - No file export service found." );
            sbLog.append( "\nNo file export service found." );
        }
    }
}
