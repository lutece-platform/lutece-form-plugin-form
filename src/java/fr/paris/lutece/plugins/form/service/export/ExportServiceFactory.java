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

import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Locale;


/**
 *
 * ExportServiceFactory
 *
 */
public class ExportServiceFactory implements IExportServiceFactory
{
    /**
     * Bean name of this factory
     */
    public static final String BEAN_FACTORY = "form.exportServiceFactory";

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IExportService> getExportServices(  )
    {
        return SpringContextService.getBeansOfType( IExportService.class );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceList getExportServicesAsRefList( Locale locale )
    {
        ReferenceList listExportTypes = new ReferenceList(  );

        for ( IExportService exportType : getExportServices(  ) )
        {
            if ( exportType != null )
            {
                listExportTypes.addItem( exportType.getKey(  ), exportType.getTitle( locale ) );
            }
        }

        return listExportTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IExportService getExportService( String strKey )
    {
        for ( IExportService exportService : getExportServices(  ) )
        {
            if ( ( exportService != null ) && StringUtils.isNotBlank( exportService.getKey(  ) ) &&
                    exportService.getKey(  ).equals( strKey ) )
            {
                return exportService;
            }
        }

        // Default : export all responses in one file for each form
        return SpringContextService.getBean( FormExportService.BEAN_SERVICE );
    }
}
