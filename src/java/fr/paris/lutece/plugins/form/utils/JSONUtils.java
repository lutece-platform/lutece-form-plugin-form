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
package fr.paris.lutece.plugins.form.utils;

import fr.paris.lutece.plugins.form.business.EntryHome;
import fr.paris.lutece.plugins.form.business.Field;
import fr.paris.lutece.plugins.form.business.FieldHome;
import fr.paris.lutece.plugins.form.business.FormError;
import fr.paris.lutece.plugins.form.business.IEntry;
import fr.paris.lutece.plugins.form.business.Response;
import fr.paris.lutece.plugins.form.service.FormPlugin;
import fr.paris.lutece.plugins.form.service.upload.FormAsynchronousUploadHandler;
import fr.paris.lutece.portal.service.blobstore.BlobStoreFileItem;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppLogService;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * Provides json utility methods for forms
 *
 */
public final class JSONUtils
{
    public static final String TAG_ID_BLOB = "id_blob";
    public static final String MESSAGE_DATA_NOT_FOUND = "id_blob not found";
    public static final String JSON_KEY_ID_FORM = "id_form";
    public static final String JSON_KEY_RESPONSE = "response";
    public static final String JSON_KEY_ID_ENTRY = "id_entry";
    public static final String JSON_KEY_ID_RESPONSE = "id_response";
    public static final String JSON_KEY_ID_FIELD = "id_field";
    public static final String JSON_KEY_VALUE_RESPONSE = "value_response";
    public static final String JSON_KEY_FILE_NAME = "file_name";
    public static final String JSON_KEY_FILE_EXTENSION = "file_extension";
    public static final String JSON_KEY_ERROR_MESSAGE = "error_message";
    public static final String JSON_KEY_MANDATORY_ERROR = "mandatory_error";
    public static final String JSON_KEY_TITLE_QUESTION = "title_question";
    public static final String JSON_KEY_FORM_ERROR = "form_error";
    public static final String JSON_KEY_FIELD_NAME = "field_name";
    public static final String JSON_KEY_MIME_TYPE = "mime_type";
    public static final String JSON_KEY_UPLOADED_FILES = "uploadedFiles";
    public static final String JSON_KEY_FILE_COUNT = "fileCount";
    public static final String JSON_KEY_FIELD = "field";
    public static final String JSON_KEY_SUCCESS = "success";

    // PROPERTIES
    private static final String PROPERTY_MESSAGE_ERROR_REMOVING_FILE = "form.message.error.removingFile";

    /**
     * Empty constructor
     */
    private JSONUtils(  )
    {
        // nothing
    }

    /**
     * Builds the json string for the response map
     * @param mapResponse the response map
     * @param nIdForm the id form
     * @param session the session
     * @return the json string
     */
    public static String buildJson( Map<Integer, List<Response>> mapResponse, int nIdForm, HttpSession session )
    {
        JSONObject jsonResponses = new JSONObject(  );

        for ( List<Response> listResponse : mapResponse.values(  ) )
        {
            for ( Response response : listResponse )
            {
                jsonResponses.accumulate( JSON_KEY_RESPONSE, buildJson( response, session ) );
            }
        }

        jsonResponses.element( JSON_KEY_ID_FORM, nIdForm );

        return jsonResponses.toString(  );
    }

    /**
     * Builds the response
     * @param json the json
     * @param locale the locale
     * @param session the session
     * @return response the response
     */
    private static Response buildResponse( JSONObject json, Locale locale, HttpSession session )
    {
        Response response = new Response(  );
        response.setIdResponse( json.getInt( JSON_KEY_ID_RESPONSE ) );

        IEntry entry = EntryHome.findByPrimaryKey( json.getInt( JSON_KEY_ID_ENTRY ),
                PluginService.getPlugin( FormPlugin.PLUGIN_NAME ) );
        response.setEntry( entry );

        if ( json.containsKey( JSON_KEY_FORM_ERROR ) )
        {
            response.getEntry(  ).setFormError( buildFormError( json.getString( JSON_KEY_FORM_ERROR ) ) );
        }

        if ( json.containsKey( JSON_KEY_VALUE_RESPONSE ) && !json.containsKey( JSON_KEY_FILE_NAME ) )
        {
            response.setResponseValue( json.getString( JSON_KEY_VALUE_RESPONSE ) );
        }

        if ( json.containsKey( JSON_KEY_ID_FIELD ) )
        {
            Field field = FieldHome.findByPrimaryKey( json.getInt( JSON_KEY_ID_FIELD ),
                    PluginService.getPlugin( FormPlugin.PLUGIN_NAME ) );
            response.setField( field );
        }

        // file specific
        boolean bIsFile = false;

        if ( json.containsKey( JSON_KEY_FILE_NAME ) )
        {
            fr.paris.lutece.plugins.form.business.file.File file = null;

            try
            {
                file = new fr.paris.lutece.plugins.form.business.file.File(  );
                file.setTitle( json.getString( JSON_KEY_FILE_NAME ) );
                file.setMimeType( json.getString( JSON_KEY_MIME_TYPE ) );
            }
            catch ( JSONException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }

            response.setFile( file );
            bIsFile = true;
        }

        if ( !bIsFile && ( response.getResponseValue(  ) != null ) )
        {
            // if the entry is not a file, we can set the string value
            // data entry as specific behavior
            entry.setResponseToStringValue( response, locale );
        }

        return response;
    }

    /**
     * Builds the responses list - null if {@link #JSON_KEY_RESPONSE} is missing.
     * @param strJSON the json
     * @param locale the locale
     * @param session the session
     * @return the responses list - null if {@link #JSON_KEY_RESPONSE} is missing
     */
    @SuppressWarnings( "unchecked" )
    public static Map<Integer, List<Response>> buildListResponses( String strJSON, Locale locale, HttpSession session )
    {
        Map<Integer, List<Response>> mapResponses;
        JSONObject jsonObject = JSONObject.fromObject( strJSON );

        try
        {
            JSON jsonResponses = (JSON) jsonObject.get( JSON_KEY_RESPONSE );

            if ( ( jsonResponses != null ) && !jsonResponses.isEmpty(  ) )
            {
                // there is at least one result
                mapResponses = new HashMap<Integer, List<Response>>(  );

                if ( jsonResponses.isArray(  ) )
                {
                    // array
                    for ( JSONObject jsonResponse : ( (Collection<JSONObject>) ( (JSONArray) jsonResponses ) ) )
                    {
                        Response response = buildResponse( jsonResponse, locale, session );
                        List<Response> listResponses = mapResponses.get( response.getEntry(  ).getIdEntry(  ) );

                        if ( listResponses == null )
                        {
                            listResponses = new ArrayList<Response>(  );
                            mapResponses.put( response.getEntry(  ).getIdEntry(  ), listResponses );
                        }

                        listResponses.add( response );
                    }
                }
                else
                {
                    // only one response ?
                    JSONObject jsonResponse = (JSONObject) jsonResponses;

                    Response response = buildResponse( jsonResponse, locale, session );

                    List<Response> listResponses = new ArrayList<Response>(  );
                    listResponses.add( response );
                    mapResponses.put( response.getEntry(  ).getIdEntry(  ), listResponses );
                }
            }
            else
            {
                // nothing to do - no response found
                mapResponses = null;
            }
        }
        catch ( JSONException jsonEx )
        {
            // nothing to do - response might no be present
            mapResponses = null;
        }

        return mapResponses;
    }

    /**
     * Builds the json string for the {@link Response}
     * @param response the response
     * @param session the session
     * @return the json string
     */
    public static JSONObject buildJson( Response response, HttpSession session )
    {
        JSONObject jsonResponse = new JSONObject(  );
        jsonResponse.element( JSON_KEY_ID_ENTRY, response.getEntry(  ).getIdEntry(  ) );
        jsonResponse.element( JSON_KEY_ID_RESPONSE, response.getIdResponse(  ) );

        if ( response.getField(  ) != null )
        {
            jsonResponse.element( JSON_KEY_ID_FIELD, response.getField(  ).getIdField(  ) );
        }

        if ( ( response.getResponseValue(  ) != null ) && ( response.getFile(  ) == null ) )
        {
            jsonResponse.element( JSON_KEY_VALUE_RESPONSE, response.getResponseValue(  ) );
        }

        // file specific data
        if ( ( response.getFile(  ) != null ) && StringUtils.isNotBlank( response.getFile(  ).getTitle(  ) ) )
        {
            jsonResponse.element( JSON_KEY_FILE_NAME, response.getFile(  ).getTitle(  ) );
            jsonResponse.element( JSON_KEY_FILE_EXTENSION,
                FilenameUtils.getExtension( response.getFile(  ).getTitle(  ) ) );
            jsonResponse.element( JSON_KEY_MIME_TYPE, response.getFile(  ).getMimeType(  ) );

            List<FileItem> listFileItems = FormAsynchronousUploadHandler.getHandler(  )
                                                                        .getFileItems( Integer.toString( 
                        response.getEntry(  ).getIdEntry(  ) ), session.getId(  ) );

            if ( ( listFileItems != null ) && !listFileItems.isEmpty(  ) )
            {
                for ( FileItem fileItem : listFileItems )
                {
                    if ( fileItem instanceof BlobStoreFileItem &&
                            fileItem.getName(  ).equals( response.getFile(  ).getTitle(  ) ) )
                    {
                        jsonResponse.accumulate( BlobStoreFileItem.JSON_KEY_FILE_METADATA_BLOB_ID,
                            ( (BlobStoreFileItem) fileItem ).getBlobId(  ) );

                        break;
                    }
                }
            }
        }

        // form error
        if ( response.getEntry(  ).getFormError(  ) != null )
        {
            jsonResponse.element( JSON_KEY_FORM_ERROR, buildJson( response.getEntry(  ).getFormError(  ) ) );
        }

        return jsonResponse;
    }

    /**
     * Builds json form {@link FormError}
     * @param formError {@link FormError}
     * @return json string
     */
    public static String buildJson( FormError formError )
    {
        JSONObject jsonError = new JSONObject(  );

        jsonError.element( JSON_KEY_ERROR_MESSAGE,
            StringUtils.isNotBlank( formError.getErrorMessage(  ) ) ? formError.getErrorMessage(  ) : StringUtils.EMPTY );
        jsonError.element( JSON_KEY_MANDATORY_ERROR, formError.isMandatoryError(  ) );
        jsonError.element( JSON_KEY_TITLE_QUESTION, formError.getTitleQuestion(  ) );

        return jsonError.toString(  );
    }

    /**
     * Builds {@link FormError} from json string
     * @param strJson json string
     * @return the {@link FormError}
     */
    public static FormError buildFormError( String strJson )
    {
        JSONObject jsonObject = JSONObject.fromObject( strJson );
        FormError formError = new FormError(  );
        formError.setErrorMessage( jsonObject.getString( JSON_KEY_ERROR_MESSAGE ) );
        formError.setMandatoryError( jsonObject.getBoolean( JSON_KEY_MANDATORY_ERROR ) );
        formError.setTitleQuestion( jsonObject.getString( JSON_KEY_TITLE_QUESTION ) );

        return formError;
    }

    /**
    * Builds a json object for the file item list.
    * Key is {@link #JSON_UPLOADED_FILES}, value is the array of uploaded file.
    * @param listFileItem the fileItem list
    * @return the json
    */
    public static JSONObject getUploadedFileJSON( List<FileItem> listFileItem )
    {
        JSONObject json = new JSONObject(  );

        if ( listFileItem != null )
        {
            for ( FileItem fileItem : listFileItem )
            {
                json.accumulate( JSON_KEY_UPLOADED_FILES, fileItem.getName(  ) );
            }

            json.element( JSON_KEY_FILE_COUNT, listFileItem.size(  ) );
        }
        else
        {
            // no file
            json.element( JSON_KEY_FILE_COUNT, 0 );
        }

        return json;
    }

    /**
     * Builds a json object with the error message.
     * @param request the request
     * @return the json object.
     */
    public static JSONObject buildJsonErrorRemovingFile( HttpServletRequest request )
    {
        JSONObject json = new JSONObject(  );

        json.element( JSONUtils.JSON_KEY_FORM_ERROR,
            I18nService.getLocalizedString( PROPERTY_MESSAGE_ERROR_REMOVING_FILE, request.getLocale(  ) ) );

        return json;
    }

    /**
     * Builds a json object with the error message.
     * @param json the JSON
     * @param strMessage the error message
     */
    public static void buildJsonError( JSONObject json, String strMessage )
    {
        if ( json != null )
        {
            json.accumulate( JSON_KEY_FORM_ERROR, strMessage );
        }
    }

    public static List<String> getBlobIds( String strJSON )
    {
        return getBlobIds( strJSON, FormUtils.CONSTANT_ID_NULL );
    }

    public static List<String> getBlobIds( String strJSON, int nIdEntry )
    {
        List<String> listBlobIds = new ArrayList<String>(  );
        JSONObject jsonObject = JSONObject.fromObject( strJSON );

        try
        {
            JSON jsonResponses = (JSON) jsonObject.get( JSON_KEY_RESPONSE );

            if ( ( jsonResponses != null ) && !jsonResponses.isEmpty(  ) )
            {
                if ( jsonResponses.isArray(  ) )
                {
                    // array
                    for ( JSONObject jsonResponse : ( (Collection<JSONObject>) ( (JSONArray) jsonResponses ) ) )
                    {
                        if ( ( ( nIdEntry == jsonResponse.getInt( JSON_KEY_ID_ENTRY ) ) ||
                                ( nIdEntry == FormUtils.CONSTANT_ID_NULL ) ) &&
                                jsonResponse.containsKey( BlobStoreFileItem.JSON_KEY_FILE_METADATA_BLOB_ID ) )
                        {
                            listBlobIds.addAll( getFileMetadataBlobIdsFromJson( jsonResponse ) );
                        }
                    }
                }
                else
                {
                    // only one response ?
                    JSONObject jsonResponse = (JSONObject) jsonResponses;

                    if ( ( ( nIdEntry == jsonResponse.getInt( JSON_KEY_ID_ENTRY ) ) ||
                            ( nIdEntry == FormUtils.CONSTANT_ID_NULL ) ) &&
                            jsonResponse.containsKey( BlobStoreFileItem.JSON_KEY_FILE_METADATA_BLOB_ID ) )
                    {
                        listBlobIds.addAll( getFileMetadataBlobIdsFromJson( jsonResponse ) );
                    }
                }
            }
            else
            {
                // nothing to do - no response found
            }
        }
        catch ( JSONException jsonEx )
        {
            // nothing to do - response might no be present
        }

        return listBlobIds;
    }

    /**
     * Gets blobs id for files metadata
     * @param strJSONFields the strJSONFields
     * @param field the field
     * @return blob ids found, empty list otherwise.
     */
    private static Collection<String> getFileMetadataBlobIdsFromJson( JSONObject json )
    {
        Object oMetadata = json.get( BlobStoreFileItem.JSON_KEY_FILE_METADATA_BLOB_ID );

        if ( oMetadata == null )
        {
            return Collections.emptyList(  );
        }

        if ( oMetadata instanceof JSONArray )
        {
            return (Collection<String>) oMetadata;
        }

        return Collections.singletonList( oMetadata.toString(  ) );
    }
}
