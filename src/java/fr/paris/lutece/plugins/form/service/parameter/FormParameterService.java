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
package fr.paris.lutece.plugins.form.service.parameter;

import fr.paris.lutece.plugins.form.business.exporttype.IExportType;
import fr.paris.lutece.plugins.form.business.exporttype.IExportTypeFactory;
import fr.paris.lutece.plugins.form.business.parameter.FormParameterFilter;
import fr.paris.lutece.plugins.form.business.parameter.FormParameterHome;
import fr.paris.lutece.plugins.form.service.export.ExportServiceFactory;
import fr.paris.lutece.plugins.form.service.export.IExportService;
import fr.paris.lutece.plugins.form.service.export.IExportServiceFactory;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

import org.apache.commons.lang.StringUtils;


/**
 *
 * FormParameterService
 *
 */
public final class FormParameterService
{
    private static final String BEAN_FORM_PARAMETER_SERVICE = "form.formParameterService";

    // PARAMETERS
    private static final String PARAMETER_EXPORT_CSV_ENCODING = "export_csv_encoding";
    private static final String PARAMETER_EXPORT_XML_ENCODING = "export_xml_encoding";
    private static final String PARAMETER_ID_EXPORT_FORMAT_DAEMON = "id_export_format_daemon";
    private static final String PARAMETER_EXPORT_DAEMON_TYPE = "export_daemon_type";
    private static final String PARAMETER_FILE_EXPORT_DAEMON_TYPE = "file_export_daemon_type";

    // PROPERTIES
    private static final String PROPERTY_DEFAULT_EXPORT_ENCODING = "form.export.encoding.default";

    /**
     * Get the instance of the service
     * @return the instance of the service
     */
    public static FormParameterService getService(  )
    {
        return SpringContextService.getBean( BEAN_FORM_PARAMETER_SERVICE );
    }

    /**
     * Find all form parameters
     * @return a {@link ReferenceList}
     */
    public ReferenceList findAll(  )
    {
        return FormParameterHome.findAll( FormUtils.getPlugin(  ) );
    }

    /**
     * Find the default value parameters of the form
     * @return a {@link ReferenceList}
     */
    public ReferenceList findDefaultValueParameters(  )
    {
        FormParameterFilter filter = new FormParameterFilter(  );
        filter.setExcludeParameterKeys( true );
        filter.addParameterKey( PARAMETER_EXPORT_CSV_ENCODING );
        filter.addParameterKey( PARAMETER_EXPORT_XML_ENCODING );
        filter.addParameterKey( PARAMETER_ID_EXPORT_FORMAT_DAEMON );

        return FormParameterHome.findByFilter( filter, FormUtils.getPlugin(  ) );
    }

    /**
     * Find the export parameters
     * @return a {@link ReferenceList}
     */
    public ReferenceList findExportParameters(  )
    {
        FormParameterFilter filter = new FormParameterFilter(  );
        filter.setExcludeParameterKeys( false );
        filter.addParameterKey( PARAMETER_EXPORT_CSV_ENCODING );
        filter.addParameterKey( PARAMETER_EXPORT_XML_ENCODING );
        filter.addParameterKey( PARAMETER_ID_EXPORT_FORMAT_DAEMON );
        filter.addParameterKey( PARAMETER_EXPORT_DAEMON_TYPE );
        filter.addParameterKey( PARAMETER_FILE_EXPORT_DAEMON_TYPE );

        return FormParameterHome.findByFilter( filter, FormUtils.getPlugin(  ) );
    }

    /**
    * Load the parameter value
    * @param strParameterKey the parameter key
    * @return The parameter value
    */
    public ReferenceItem findByKey( String strParameterKey )
    {
        return FormParameterHome.findByKey( strParameterKey, FormUtils.getPlugin(  ) );
    }

    /**
     * Update the parameter value
     * @param param A reference item contain the association key/value to
     *            update. The key must be in the code parameter of the reference
     *            item, and the value in the value parameter
     */
    public void update( ReferenceItem param )
    {
        FormParameterHome.update( param, FormUtils.getPlugin(  ) );
    }

    /**
     * Get the encoding for export CSV
     * @return the encoding for export CSV
     */
    public String getExportCSVEncoding(  )
    {
        ReferenceItem param = findByKey( PARAMETER_EXPORT_CSV_ENCODING );

        if ( param == null )
        {
            return AppPropertiesService.getProperty( PROPERTY_DEFAULT_EXPORT_ENCODING );
        }

        return param.getName(  );
    }

    /**
     * Get the encoding for export XML
     * @return the encoding for export XML
     */
    public String getExportXMLEncoding(  )
    {
        ReferenceItem param = findByKey( PARAMETER_EXPORT_XML_ENCODING );

        if ( param == null )
        {
            return AppPropertiesService.getProperty( PROPERTY_DEFAULT_EXPORT_ENCODING );
        }

        return param.getName(  );
    }

    /**
     * Get the id export responses for daemon
     * @return the id export responses for daemon
     */
    public int getIdExportResponsesDaemon(  )
    {
        ReferenceItem param = findByKey( PARAMETER_ID_EXPORT_FORMAT_DAEMON );

        if ( ( param == null ) || StringUtils.isBlank( param.getName(  ) ) ||
                !StringUtils.isNumeric( param.getName(  ) ) )
        {
            return 1;
        }

        return GenericAttributesUtils.convertStringToInt( param.getName(  ) );
    }

    /**
     * Check if the given key is from an encoding parameter
     * @param strKey the key
     * @return true if it is an encoding parameter, false otherwise
     */
    public boolean isExportEncodingParameter( String strKey )
    {
        if ( StringUtils.isNotBlank( strKey ) )
        {
            if ( PARAMETER_EXPORT_CSV_ENCODING.equals( strKey ) || PARAMETER_EXPORT_XML_ENCODING.equals( strKey ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the export daemon type configured in the advanced parameters of
     * the plugin-form.
     * @return a {@link IExportType}
     */
    public IExportType getExportDaemonType(  )
    {
        ReferenceItem param = findByKey( PARAMETER_EXPORT_DAEMON_TYPE );

        IExportTypeFactory exportDaemonTypeFactory = SpringContextService.getBean( FormUtils.BEAN_EXPORT_DAEMON_TYPE_FACTORY );

        if ( ( param == null ) || StringUtils.isBlank( param.getName(  ) ) )
        {
            return exportDaemonTypeFactory.getExportType( StringUtils.EMPTY );
        }

        return exportDaemonTypeFactory.getExportType( param.getName(  ) );
    }

    /**
     * Get the export daemon type configured in the advanced parameters of
     * the plugin-form.
     * @return a {@link IExportType}
     */
    public IExportService getFileExportDaemonType(  )
    {
        ReferenceItem param = findByKey( PARAMETER_FILE_EXPORT_DAEMON_TYPE );

        IExportServiceFactory fileExportDaemonTypeFactory = SpringContextService.getBean( ExportServiceFactory.BEAN_FACTORY );

        if ( ( param == null ) || StringUtils.isBlank( param.getName(  ) ) )
        {
            return fileExportDaemonTypeFactory.getExportService( StringUtils.EMPTY );
        }

        return fileExportDaemonTypeFactory.getExportService( param.getName(  ) );
    }
}
