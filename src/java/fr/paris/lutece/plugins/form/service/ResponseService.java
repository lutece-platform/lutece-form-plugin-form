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
import fr.paris.lutece.plugins.form.business.Response;
import fr.paris.lutece.plugins.form.business.ResponseFilter;
import fr.paris.lutece.plugins.form.business.ResponseHome;
import fr.paris.lutece.plugins.form.business.StatisticEntrySubmit;
import fr.paris.lutece.plugins.form.service.file.FileService;
import fr.paris.lutece.plugins.form.utils.FormUtils;

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
    public void setFileService( FileService fileService )
    {
        _fileService = fileService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "form.transactionManager" )
    public void create( FormSubmit formSubmit )
    {
        for ( Response response : formSubmit.getListResponse(  ) )
        {
            response.setFormSubmit( formSubmit );
            create( response );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create( Response response )
    {
        if ( response.getFile(  ) != null )
        {
            response.getFile(  ).setIdFile( _fileService.create( response.getFile(  ) ) );
        }

        ResponseHome.create( response, FormUtils.getPlugin(  ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update( Response response )
    {
        if ( response.getFile(  ) != null )
        {
            _fileService.update( response.getFile(  ) );
        }

        ResponseHome.update( response, FormUtils.getPlugin(  ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove( int nIdFormSubmit )
    {
        // First remove files
        ResponseFilter filter = new ResponseFilter(  );
        filter.setIdForm( nIdFormSubmit );

        for ( Response response : getResponseList( filter, false ) )
        {
            if ( response.getFile(  ) != null )
            {
                _fileService.remove( response.getFile(  ).getIdFile(  ) );
            }
        }

        // Then remove responses
        ResponseHome.remove( nIdFormSubmit, FormUtils.getPlugin(  ) );
    }

    // GET

    /**
     * {@inheritDoc}
     */
    @Override
    public Response findByPrimaryKey( int nKey, boolean bGetFileData )
    {
        Response response = ResponseHome.findByPrimaryKey( nKey, FormUtils.getPlugin(  ) );

        if ( bGetFileData && ( response != null ) && ( response.getFile(  ) != null ) )
        {
            response.setFile( _fileService.findByPrimaryKey( response.getFile(  ).getIdFile(  ), true ) );
        }

        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Response> getResponseList( ResponseFilter filter, boolean bGetFileData )
    {
        List<Response> listResponses = ResponseHome.getResponseList( filter, FormUtils.getPlugin(  ) );

        if ( bGetFileData && ( listResponses != null ) && !listResponses.isEmpty(  ) )
        {
            for ( Response response : listResponses )
            {
                if ( response.getFile(  ) != null )
                {
                    response.setFile( _fileService.findByPrimaryKey( response.getFile(  ).getIdFile(  ), bGetFileData ) );
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
        return ResponseHome.getStatisticByIdEntry( nIdEntry, FormUtils.getPlugin(  ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void anonymizeEntries( List<Integer> listIdEntries, Timestamp dateCleanTo )
    {
        ResponseHome.anonymizeEntries( listIdEntries, dateCleanTo, FormUtils.getPlugin( ) );
    }
}
