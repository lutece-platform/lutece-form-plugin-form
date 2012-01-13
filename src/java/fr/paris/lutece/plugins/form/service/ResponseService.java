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

import java.util.List;

import fr.paris.lutece.plugins.form.business.Response;
import fr.paris.lutece.plugins.form.business.ResponseFilter;
import fr.paris.lutece.plugins.form.business.ResponseHome;
import fr.paris.lutece.plugins.form.business.StatisticEntrySubmit;
import fr.paris.lutece.plugins.form.service.file.FileService;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;

/**
 * 
 * ResponseService
 * 
 */
public final class ResponseService
{
	private FileService _fileService;

	/**
	 * Set the file service
	 * @param fileService the file service
	 */
	public void setFileService( FileService fileService )
	{
		_fileService = fileService;
	}
	
	/**
     * Creation of an instance of response
     * @param response The instance of the response which contains the informations to store
     */
    public void create( Response response )
    {
    	if ( response.getFile(  ) != null )
    	{
    		response.getFile(  ).setIdFile( _fileService.create( response.getFile(  ) ) );
    	}
    	ResponseHome.create( response, FormUtils.getPlugin() );
    }

    /**
     * Update of the response which is specified in parameter
     * @param response The instance of the Response which contains the informations to update
     */
    public void update( Response response )
    {
    	if ( response.getFile(  ) != null )
    	{
    		_fileService.update( response.getFile(  ) );
    	}
    	ResponseHome.update( response, FormUtils.getPlugin() );
    }

    /**
     * Remove all  response  associate to the form submit whose identifier is specified in parameter
     * @param nIdFormSubmit The formSubmitKey
     */
    public void remove( int nIdFormSubmit )
    {
    	// First remove files
    	ResponseFilter filter = new ResponseFilter(  );
    	filter.setIdForm( nIdFormSubmit );
    	for ( Response response : getResponseList( filter, false ) )
    	{
    		if ( response.getFile(  ) != null )
    		_fileService.remove( response.getFile(  ).getIdFile(  ) );
    	}
    	
    	// Then remove responses
        ResponseHome.remove( nIdFormSubmit, FormUtils.getPlugin() );
    }

    // GET
    
    /**
     * Returns an instance of a Response whose identifier is specified in parameter
     * @param nKey The entry primary key
     * @param bGetFileData get file data
     * @return an instance of Response
     */
    public Response findByPrimaryKey( int nKey, boolean bGetFileData )
    {
    	Response response = ResponseHome.findByPrimaryKey( nKey, FormUtils.getPlugin() );
    	if ( bGetFileData && response != null && response.getFile(  ) != null )
    	{
    		response.setFile( _fileService.findByPrimaryKey( response.getFile(  ).getIdFile(  ), true ) );
    	}
        return response; 
    }

    /**
     * Load the data of all the response who verify the filter and returns them in a  list
     * @param filter the filter
     * @param bGetFileData get file data
     * @return  the list of response
     */
    public List<Response> getResponseList( ResponseFilter filter, boolean bGetFileData )
    {
    	List<Response> listResponses = ResponseHome.getResponseList( filter, FormUtils.getPlugin() );
    	if ( bGetFileData && listResponses != null && !listResponses.isEmpty(  ) )
    	{
    		for ( Response response : listResponses )
    		{
    			if ( response.getFile(  ) != null )
    			{
    				response.setFile( _fileService.findByPrimaryKey( response.getFile(  ).getIdFile(  ), true ) );
    			}
    		}
    	}
    	return listResponses;
    }

    /**
     *  Return a list of statistic on the entry
     *  @param nIdEntry the id of the entry
     *  @return return a list of statistic on the entry
     */
    public List<StatisticEntrySubmit> getStatisticByIdEntry( int nIdEntry )
    {
        return ResponseHome.getStatisticByIdEntry( nIdEntry, FormUtils.getPlugin() );
    }
}
