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
package fr.paris.lutece.plugins.form.service.upload;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.form.service.FormPlugin;
import fr.paris.lutece.plugins.form.utils.JSONUtils;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.web.upload.IAsynchronousUploadHandler;

/**
 * 
 * FormAsynchronousUploadHandler.
 * @see #getFileItem(String, String)
 * @see #removeFileItem(String, String)
 *
 */
public class FormAsynchronousUploadHandler implements IAsynchronousUploadHandler 
{
    private static final String PARAMETER_PAGE = "page";
    private static final String PARAMETER_FIELD_NAME = "fieldname";
    private static final String PARAMETER_JSESSION_ID = "jsessionid";

    /** contains uploaded file items */
    public static Map<String, Map<String, FileItem>> _mapAsynchronousUpload = new ConcurrentHashMap<String, Map<String,FileItem>>(  );

    /**
     * 
     * {@inheritDoc}
     */
	public boolean isInvoked( HttpServletRequest request )
	{
		return FormPlugin.PLUGIN_NAME.equals( request.getParameter( PARAMETER_PAGE ) );
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public void process( HttpServletRequest request, HttpServletResponse response, JSONObject mainObject,
			List<FileItem> fileItems) 
	{
		// prevent 0 or multiple uploads for the same field
		if ( fileItems == null || fileItems.size() == 0 )
		{
			throw new AppException( "No file uploaded" );
		}
		if ( fileItems.size(  ) > 1 )
		{
			throw new AppException( "Upload multiple files for Form is not supported" );
		}
		
		String strSessionId = request.getParameter( PARAMETER_JSESSION_ID );
		
		// find session-related files in the map
		Map<String, FileItem> mapFileItemsSession = _mapAsynchronousUpload.get( strSessionId );
		
		// create map if not exists
		if ( mapFileItemsSession == null )
		{
			synchronized ( _mapAsynchronousUpload )
			{
				if ( _mapAsynchronousUpload.get( strSessionId ) == null )
				{
					mapFileItemsSession = new ConcurrentHashMap<String, FileItem>(  );
					_mapAsynchronousUpload.put( strSessionId, mapFileItemsSession );
				}
			}
		}
		
		
		String strEntryId = request.getParameter( PARAMETER_FIELD_NAME );
		if ( StringUtils.isBlank( strEntryId ) )
		{
			throw new AppException( "fieldname is not provided for the current file upload" );
		}
		
		// put entry id -> fileItem : we don't want more than one file per entry --> overwrite existing file
		mapFileItemsSession.put( strEntryId, fileItems.get( 0 ) );
		
		// add entry id to json
		mainObject.element( JSONUtils.JSON_KEY_FIELD_NAME, strEntryId );
	}
	
	/**
	 * Gets the fileItem for the entry and the given session.
	 * @param strIdEntry the entry
	 * @param strSessionId the session id
	 * @return the fileItem found, <code>null</code> otherwise.
	 */
	public static FileItem getFileItem( String strIdEntry, String strSessionId )
	{
		FileItem fileItem;
		Map<String, FileItem> mapFileItemsSession = _mapAsynchronousUpload.get( strSessionId );
		if ( mapFileItemsSession != null )
		{
			fileItem = mapFileItemsSession.get( strIdEntry );
		}
		else
		{
			fileItem = null;
		}
		
		return fileItem;
	}
	
	/**
	 * Removes the file from the list.
	 * @param strIdEntry the entry id
	 * @param strSessionId the session id
	 */
	public static void removeFileItem( String strIdEntry, String strSessionId )
	{
		Map<String, FileItem> mapFileItemsSession = _mapAsynchronousUpload.get( strSessionId );
		if ( mapFileItemsSession != null && strIdEntry != null )
		{
			mapFileItemsSession.remove( strIdEntry );
		}
	}
	
	/**
	 * Removes all files associated to the session
	 * @param strSessionId the session id
	 */
	public static void removeSessionFiles( String strSessionId )
	{
		_mapAsynchronousUpload.remove( strSessionId );
	}

}
