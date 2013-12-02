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
package fr.paris.lutece.plugins.form.service;

import fr.paris.lutece.plugins.form.business.FormSubmit;
import fr.paris.lutece.plugins.form.business.FormSubmitHome;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseFilter;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.genericattributes.business.StatisticEntrySubmit;
import fr.paris.lutece.plugins.genericattributes.service.file.FileService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;


/**
 * 
 * ResponseService
 * 
 */
public class ResponseService implements IResponseService
{
    private FileService _fileService;

    /**
     * {@inheritDoc}
     */
    @Override
    public FileService getFileService( )
    {
        if ( _fileService == null )
        {
            _fileService = SpringContextService.getBean( FileService.BEAN_SERVICE );
        }
        return _fileService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "form.transactionManager" )
    public void create( FormSubmit formSubmit )
    {
        for ( Response response : formSubmit.getListResponse( ) )
        {
            create( response, formSubmit.getIdFormSubmit( ) );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create( Response response, int nIdFormSubmit )
    {
        ResponseHome.create( response );
        FormSubmitHome
                .associateResponseWithFormSubmit( response.getIdResponse( ), nIdFormSubmit, FormUtils.getPlugin( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update( Response response )
    {
        ResponseHome.update( response );
    }

    // GET

    /**
     * {@inheritDoc}
     */
    @Override
    public Response findByPrimaryKey( int nKey, boolean bGetFileData )
    {
        return ResponseHome.findByPrimaryKey( nKey );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Response> getResponseList( ResponseFilter filter, boolean bGetFileData )
    {
        List<Response> listResponses = ResponseHome.getResponseList( filter );

        if ( bGetFileData && ( listResponses != null ) && !listResponses.isEmpty( ) )
        {
            for ( Response response : listResponses )
            {
                if ( response.getFile( ) != null )
                {
                    response.setFile( getFileService( ).findByPrimaryKey( response.getFile( ).getIdFile( ),
                            bGetFileData ) );
                }
            }
        }

        return listResponses;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<StatisticEntrySubmit> getStatisticByIdEntry( int nIdEntry )
    {
        return ResponseHome.getStatisticByIdEntry( nIdEntry );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void anonymizeEntries( List<Integer> listIdEntries, Timestamp dateCleanTo )
    {
        FormSubmitHome.anonymizeEntries( listIdEntries, dateCleanTo, FormUtils.getPlugin( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFromFormSubmit( int nIdFormSubmit )
    {
        Plugin plugin = FormUtils.getPlugin( );
        List<Integer> listIdResponse = FormSubmitHome.getResponseListFromIdFormSubmit( nIdFormSubmit, plugin );
        for ( Integer nIdResponse : listIdResponse )
        {
            ResponseHome.remove( nIdResponse );
        }
    }
}
