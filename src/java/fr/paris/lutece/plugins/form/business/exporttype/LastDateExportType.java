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
package fr.paris.lutece.plugins.form.business.exporttype;

import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormSubmit;
import fr.paris.lutece.plugins.form.utils.DateUtils;
import fr.paris.lutece.plugins.form.utils.FileUtils;
import fr.paris.lutece.plugins.genericattributes.business.ResponseFilter;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;

import java.util.List;
import java.util.Locale;


/**
 *
 * This class represents the export type from the date written at the last line
 * of the file <strong>/plugins/form/export/formExportResponses</strong>.
 * In other words, every responses with a date greater than the date stored in the file
 * will be included in the export.
 *
 */
public class LastDateExportType extends AbstractExportType
{
    // PROPERTIES
    private static final String PROPERTY_FILE_FOLDER_PATH = "form.export.file.folder.path";
    private static final String PROPERTY_LOG_FILE_NAME = "form.export.log.fileName";

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseFilter getResponseFilter( Form form, Locale locale )
    {
        // Get the last date
        String strLastDate = FileUtils.readLastLine( getLogFile(  ) );
        Timestamp lastDate = DateUtils.formatTimestamp( strLastDate, locale );

        ResponseFilter filter = new ResponseFilter(  );
        filter.setIdResource( form.getIdForm(  ) );
        filter.setDateFirst( lastDate );

        return filter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveExport( List<FormSubmit> listFormSubmits, Locale locale )
    {
        String strCurrentDate = DateUtils.getCurrentDateTime( locale );

        if ( StringUtils.isNotBlank( strCurrentDate ) )
        {
            FileUtils.writeToFile( strCurrentDate, getLogFile(  ) );
        }
    }

    /**
     * Get the log file
     * @return the log file
     */
    private String getLogFile(  )
    {
        String strFolderPath = AppPathService.getAbsolutePathFromRelativePath( AppPropertiesService.getProperty( 
                    PROPERTY_FILE_FOLDER_PATH ) );
        String strLogFileName = AppPropertiesService.getProperty( PROPERTY_LOG_FILE_NAME );

        return strFolderPath + strLogFileName;
    }
}
