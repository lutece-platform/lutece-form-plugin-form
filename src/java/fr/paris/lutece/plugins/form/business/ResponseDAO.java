/*
 * Copyright (c) 2002-2009, Mairie de Paris
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
package fr.paris.lutece.plugins.form.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for Response objects
 */
public final class ResponseDAO implements IResponseDAO
{
    // Constants
    private static final String EMPTY_STRING = "";
    private static final String SQL_QUERY_NEW_PK = "SELECT MAX( id_response ) FROM form_response";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT " +
        "resp.id_response,resp.id_form_submit,resp.response_value,type.class_name,ent.id_entry,ent.title,ent.id_type, " +
        "resp.id_field,resp.file_name,resp.file_extension FROM form_response resp,form_entry ent,form_entry_type type  " +
        "WHERE resp.id_response=? and resp.id_entry =ent.id_entry and ent.id_type=type.id_type ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO form_response ( " +
        "id_response,id_form_submit,response_value,id_entry,id_field,file_name,file_extension) VALUES(?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM form_response WHERE id_form_submit = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE  form_response SET " +
        "id_response=?,id_form_submit=?,response_value=?,id_entry=?,id_field=?,file_name=?,file_extension=? WHERE id_response=?";
    private static final String SQL_QUERY_SELECT_RESPONSE_BY_FILTER = "SELECT " +
        "resp.id_response,resp.id_form_submit,resp.response_value,type.class_name,ent.id_entry,ent.title,ent.id_type, " +
        "resp.id_field,resp.file_name,resp.file_extension FROM form_response resp,form_entry ent,form_entry_type type  " +
        "WHERE resp.id_entry =ent.id_entry and ent.id_type=type.id_type ";
    private static final String SQL_QUERY_SELECT_COUNT_RESPONSE_BY_ID_ENTRY = "SELECT field.title,COUNT(resp.id_response)" +
        "FROM form_entry e LEFT JOIN form_field field ON(e.id_entry=field.id_entry) LEFT JOIN  form_response resp on (resp.id_field=field.id_field )" +
        "WHERE e.id_entry=? GROUP BY field.id_field ORDER BY field.pos ";
    private static final String SQL_FILTER_ID_FORM_SUBMITION = " AND resp.id_form_submit = ? ";
    private static final String SQL_FILTER_ID_ENTRY = " AND resp.id_entry = ? ";
    private static final String SQL_FILTER_ID_FIELD = " AND resp.id_field = ? ";
    private static final String SQL_ORDER_BY_ID_RESPONSE = " ORDER BY id_response ";

    /**
     * Generates a new primary key
     *
     * @param plugin the plugin
     * @return The new primary key
     */
    private int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery(  );

        int nKey;

        if ( !daoUtil.next(  ) )
        {
            // if the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;
        daoUtil.free(  );

        return nKey;
    }

    /**
     * Insert a new record in the table.
     *
     * @param response instance of the Response object to insert
     * @param plugin the plugin
     */
    public synchronized void insert( Response response, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        response.setIdResponse( newPrimaryKey( plugin ) );
        daoUtil.setInt( 1, response.getIdResponse(  ) );
        daoUtil.setInt( 2, response.getFormSubmit(  ).getIdFormSubmit(  ) );
        daoUtil.setBytes( 3, response.getValueResponse(  ) );
        daoUtil.setInt( 4, response.getEntry(  ).getIdEntry(  ) );

        if ( response.getField(  ) != null )
        {
            daoUtil.setInt( 5, response.getField(  ).getIdField(  ) );
        }
        else
        {
            daoUtil.setIntNull( 5 );
        }

        daoUtil.setString( 6, response.getFileName(  ) );
        daoUtil.setString( 7, response.getFileExtension(  ) );

        daoUtil.executeUpdate(  );

        daoUtil.free(  );
    }

    /**
     * Load the data of the response from the table
     *
     * @param nIdResponse The identifier of the response
     * @param plugin the plugin
     * @return the instance of the response
     */
    public Response load( int nIdResponse, Plugin plugin )
    {
        boolean bException = false;
        Response response = null;
        IEntry entry = null;
        EntryType entryType = null;
        Field field = null;
        FormSubmit formResponse = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nIdResponse );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            response = new Response(  );
            response.setIdResponse( daoUtil.getInt( 1 ) );
            formResponse = new FormSubmit(  );
            formResponse.setIdFormSubmit( daoUtil.getInt( 2 ) );
            response.setFormSubmit( formResponse );
            response.setValueResponse( daoUtil.getBytes( 3 ) );
            entryType = new EntryType(  );
            entryType.setClassName( daoUtil.getString( 4 ) );

            try
            {
                entry = (IEntry) Class.forName( entryType.getClassName(  ) ).newInstance(  );
            }
            catch ( ClassNotFoundException e )
            {
                //  class doesn't exist
                AppLogService.error( e );
                bException = true;
            }
            catch ( InstantiationException e )
            {
                // Class is abstract or is an  interface or haven't accessible builder
                AppLogService.error( e );
                bException = true;
            }
            catch ( IllegalAccessException e )
            {
                // can't access to rhe class
                AppLogService.error( e );
                bException = true;
            }

            if ( bException )
            {
                return null;
            }

            entry.setEntryType( entryType );
            entry.setIdEntry( daoUtil.getInt( 5 ) );
            entry.setTitle( daoUtil.getString( 6 ) );
            response.setEntry( entry );

            if ( daoUtil.getObject( 7 ) != null )
            {
                field = new Field(  );
                field.setIdField( daoUtil.getInt( 8 ) );
                response.setField( field );
            }

            response.setFileName( daoUtil.getString( 9 ) );
            response.setFileExtension( daoUtil.getString( 10 ) );
        }

        daoUtil.free(  );

        return response;
    }

    /**
     * Delete  all  responses  associate to the form submit whose identifier is specified in parameter
     *
     * @param nIdFormSubmit The identifier of the formSubmit
     * @param plugin the plugin
     */
    public void delete( int nIdFormSubmit, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdFormSubmit );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Update the the response in the table
     *
     * @param response instance of the response object to update
     * @param plugin the plugin
     */
    public void store( Response response, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setInt( 1, response.getIdResponse(  ) );
        daoUtil.setInt( 2, response.getFormSubmit(  ).getIdFormSubmit(  ) );
        daoUtil.setBytes( 3, response.getValueResponse(  ) );
        daoUtil.setInt( 4, response.getEntry(  ).getIdEntry(  ) );

        if ( response.getField(  ) != null )
        {
            daoUtil.setInt( 5, response.getField(  ).getIdField(  ) );
        }
        else
        {
            daoUtil.setIntNull( 5 );
        }

        daoUtil.setString( 6, response.getFileName(  ) );
        daoUtil.setString( 7, response.getFileExtension(  ) );
        daoUtil.setInt( 8, response.getIdResponse(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Load the data of all the response who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of response
     */
    public List<Response> selectListByFilter( ResponseFilter filter, Plugin plugin )
    {
        boolean bException = false;
        List<Response> responseList = new ArrayList<Response>(  );
        Response response;
        IEntry entry = null;
        EntryType entryType = null;
        Field field = null;
        FormSubmit formResponse = null;

        String strSQL = SQL_QUERY_SELECT_RESPONSE_BY_FILTER;
        strSQL += ( ( filter.containsIdForm(  ) ) ? SQL_FILTER_ID_FORM_SUBMITION : EMPTY_STRING );
        strSQL += ( ( filter.containsIdEntry(  ) ) ? SQL_FILTER_ID_ENTRY : EMPTY_STRING );
        strSQL += ( ( filter.containsIdField(  ) ) ? SQL_FILTER_ID_FIELD : EMPTY_STRING );
        strSQL += SQL_ORDER_BY_ID_RESPONSE;

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        if ( filter.containsIdForm(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdForm(  ) );
            nIndex++;
        }

        if ( filter.containsIdEntry(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdEntry(  ) );
            nIndex++;
        }

        if ( filter.containsIdField(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdField(  ) );
            nIndex++;
        }

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            response = new Response(  );
            response.setIdResponse( daoUtil.getInt( 1 ) );
            formResponse = new FormSubmit(  );
            formResponse.setIdFormSubmit( daoUtil.getInt( 2 ) );
            response.setFormSubmit( formResponse );
            response.setValueResponse( daoUtil.getBytes( 3 ) );
            entryType = new EntryType(  );
            entryType.setClassName( daoUtil.getString( 4 ) );

            try
            {
                entry = (IEntry) Class.forName( entryType.getClassName(  ) ).newInstance(  );
            }
            catch ( ClassNotFoundException e )
            {
                //  class doesn't exist
                AppLogService.error( e );
                bException = true;
            }
            catch ( InstantiationException e )
            {
                // Class is abstract or is an  interface or haven't accessible builder
                AppLogService.error( e );
                bException = true;
            }
            catch ( IllegalAccessException e )
            {
                // can't access to rhe class
                AppLogService.error( e );
                bException = true;
            }

            if ( bException )
            {
                return null;
            }

            entry.setEntryType( entryType );
            entry.setIdEntry( daoUtil.getInt( 5 ) );
            entry.setTitle( daoUtil.getString( 6 ) );
            response.setEntry( entry );

            if ( daoUtil.getObject( 7 ) != null )
            {
                field = new Field(  );
                field.setIdField( daoUtil.getInt( 8 ) );
                response.setField( field );
            }

            response.setFileName( daoUtil.getString( 9 ) );
            response.setFileExtension( daoUtil.getString( 10 ) );
            responseList.add( response );
        }

        daoUtil.free(  );

        return responseList;
    }

    /**
     *  return a list of statistic on the entry
     *  @param nIdEntry the id of the entry
     *  @param plugin the plugin
     *  @return return a list of statistic on the entry
     */
    public List<StatisticEntrySubmit> getStatisticByIdEntry( int nIdEntry, Plugin plugin )
    {
        List<StatisticEntrySubmit> listStatisticEntrySubmit = new ArrayList<StatisticEntrySubmit>(  );
        StatisticEntrySubmit statisticEntrySubmit;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_COUNT_RESPONSE_BY_ID_ENTRY, plugin );
        daoUtil.setInt( 1, nIdEntry );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            statisticEntrySubmit = new StatisticEntrySubmit(  );
            statisticEntrySubmit.setFieldLibelle( daoUtil.getString( 1 ) );
            statisticEntrySubmit.setNumberResponse( daoUtil.getInt( 2 ) );
            listStatisticEntrySubmit.add( statisticEntrySubmit );
        }

        daoUtil.free(  );

        return listStatisticEntrySubmit;
    }
}
