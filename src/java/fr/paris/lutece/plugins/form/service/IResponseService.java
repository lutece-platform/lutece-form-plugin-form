/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
import fr.paris.lutece.plugins.form.business.StatisticEntrySubmit;
import fr.paris.lutece.plugins.form.service.file.FileService;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Response service
 *
 */
public interface IResponseService
{
    /**
     * Set the file service
     * @param fileService the file service
     */
    void setFileService( FileService fileService );

    @Transactional( "form.transactionManager" )
    void create( FormSubmit formSubmit );

    /**
     * Creation of an instance of response
     * @param response The instance of the response which contains the informations to store
     */
    void create( Response response );

    /**
     * Update of the response which is specified in parameter
     * @param response The instance of the Response which contains the informations to update
     */
    void update( Response response );

    /**
     * Remove all  response  associate to the form submit whose identifier is specified in parameter
     * @param nIdFormSubmit The formSubmitKey
     */
    @Transactional( "form.transactionManager" )
    void remove( int nIdFormSubmit );

    /**
     * Returns an instance of a Response whose identifier is specified in parameter
     * @param nKey The entry primary key
     * @param bGetFileData get file data
     * @return an instance of Response
     */
    Response findByPrimaryKey( int nKey, boolean bGetFileData );

    /**
     * Load the data of all the response who verify the filter and returns them in a  list
     * @param filter the filter
     * @param bGetFileData get file data
     * @return  the list of response
     */
    List<Response> getResponseList( ResponseFilter filter, boolean bGetFileData );

    /**
     *  Return a list of statistic on the entry
     *  @param nIdEntry the id of the entry
     *  @return return a list of statistic on the entry
     */
    List<StatisticEntrySubmit> getStatisticByIdEntry( int nIdEntry );
}
