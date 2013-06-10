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
package fr.paris.lutece.plugins.form.business;

import fr.paris.lutece.plugins.form.business.file.File;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;


/**
 * This class provides Data Access methods for Response objects
 */
public final class ResponseDAO implements IResponseDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = " SELECT MAX( id_response ) FROM form_response ";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = " SELECT "
            + " resp.id_response, resp.id_form_submit, resp.response_value, type.class_name, ent.id_type, ent.id_entry, ent.title, "
            + " resp.id_field, resp.id_file, resp.status FROM form_response resp, form_entry ent, form_entry_type type  "
            + " WHERE resp.id_response = ? and resp.id_entry = ent.id_entry and ent.id_type = type.id_type ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO form_response ( "
            + " id_response, id_form_submit, response_value, id_entry, id_field, id_file, status ) VALUES ( ?,?,?,?,?,?,? )";
    private static final String SQL_QUERY_DELETE = "DELETE FROM form_response WHERE id_form_submit = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE form_response SET "
            + " id_form_submit = ?, response_value = ?, id_entry = ?, id_field = ?, id_file = ?, status = ? WHERE id_response = ?";
    private static final String SQL_QUERY_SELECT_RESPONSE_BY_FILTER = "SELECT "
            + " resp.id_response, resp.id_form_submit, resp.response_value, type.class_name, ent.id_type, ent.id_entry, ent.title, "
            + " resp.id_field, resp.id_file, resp.status FROM form_response resp, form_entry ent, form_entry_type type "
            + " WHERE resp.id_entry = ent.id_entry and ent.id_type = type.id_type ";
    private static final String SQL_QUERY_SELECT_COUNT_RESPONSE_BY_ID_ENTRY = " SELECT field.title, COUNT( resp.id_response )"
            + " FROM form_entry e LEFT JOIN form_field field ON ( e.id_entry = field.id_entry ) LEFT JOIN form_response resp on ( resp.id_field = field.id_field ) "
            + " WHERE e.id_entry = ? GROUP BY field.id_field ORDER BY field.pos ";
    private static final String SQL_QUERY_ANONYMIZE_RESPONSES = " UPDATE form_response fr SET response_value = ?, status = ? WHERE status < ? AND ( SELECT date_response FROM form_submit fs WHERE fs.id_form_submit = fr.id_form_submit) < ? AND id_entry IN ( ";

    // Special query in order to sort numerically and not alphabetically (thus avoiding list like 1, 10, 11, 2, ... instead of 1, 2, ..., 10, 11)
    private static final String SQL_QUERY_SELECT_MAX_NUMBER = " SELECT fr.response_value FROM form_response fr "
            + " INNER JOIN form_submit fs ON fs.id_form_submit = fr.id_form_submit "
            + " INNER JOIN form_entry ent ON fr.id_entry = ent.id_entry "
            + " WHERE ent.id_entry = ? AND fs.id_form = ? ORDER BY CAST(fr.response_value AS DECIMAL) DESC LIMIT 1 ";
    private static final String SQL_FILTER_ID_FORM_SUBMITION = " AND resp.id_form_submit = ? ";
    private static final String SQL_FILTER_ID_ENTRY = " AND resp.id_entry = ? ";
    private static final String SQL_FILTER_ID_FIELD = " AND resp.id_field = ? ";
    private static final String SQL_FILTER_ID_RESPONSE = " resp.id_response ";
    private static final String SQL_ORDER_BY = " ORDER BY ";
    private static final String SQL_ASC = " ASC ";
    private static final String SQL_DESC = " DESC ";
    private static final String CONSTANT_COMMA = ",";
    private static final String CONSTANT_QUESTION_MARK = "?";
    private static final String CONSTANT_CLOSE_PARENTHESIS = ")";

    /**
     * Generates a new primary key
     * 
     * @param plugin the plugin
     * @return The new primary key
     */
    private int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery( );

        int nKey;

        if ( !daoUtil.next( ) )
        {
            // if the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;
        daoUtil.free( );

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
        int nIndex = 1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        response.setIdResponse( newPrimaryKey( plugin ) );
        daoUtil.setInt( nIndex++, response.getIdResponse( ) );
        daoUtil.setInt( nIndex++, response.getFormSubmit( ).getIdFormSubmit( ) );
        daoUtil.setString( nIndex++, response.getResponseValue( ) );
        daoUtil.setInt( nIndex++, response.getEntry( ).getIdEntry( ) );

        if ( response.getField( ) != null )
        {
            daoUtil.setInt( nIndex++, response.getField( ).getIdField( ) );
        }
        else
        {
            daoUtil.setIntNull( nIndex++ );
        }

        if ( response.getFile( ) != null )
        {
            daoUtil.setInt( nIndex++, response.getFile( ).getIdFile( ) );
        }
        else
        {
            daoUtil.setIntNull( nIndex++ );
        }
        daoUtil.setInt( nIndex++, Response.CONSTANT_STATUS_ACTIVE );

        daoUtil.executeUpdate( );

        daoUtil.free( );
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

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nIdResponse );
        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            int nIndex = 1;

            response = new Response( );
            response.setIdResponse( daoUtil.getInt( nIndex++ ) );

            FormSubmit formResponse = new FormSubmit( );
            formResponse.setIdFormSubmit( daoUtil.getInt( nIndex++ ) );
            response.setFormSubmit( formResponse );

            response.setResponseValue( daoUtil.getString( nIndex++ ) );

            EntryType entryType = new EntryType( );
            entryType.setClassName( daoUtil.getString( nIndex++ ) );
            entryType.setIdType( daoUtil.getInt( nIndex++ ) );

            IEntry entry = null;

            try
            {
                entry = (IEntry) Class.forName( entryType.getClassName( ) ).newInstance( );
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
                daoUtil.free( );

                return null;
            }

            entry.setEntryType( entryType );
            entry.setIdEntry( daoUtil.getInt( nIndex++ ) );
            entry.setTitle( daoUtil.getString( nIndex++ ) );
            response.setEntry( entry );

            // Get field if it exists
            if ( daoUtil.getObject( nIndex ) != null )
            {
                Field field = new Field( );
                field.setIdField( daoUtil.getInt( nIndex ) );
                response.setField( field );
            }

            nIndex++;

            // Get file if it exists
            if ( daoUtil.getObject( nIndex ) != null )
            {
                File file = new File( );
                file.setIdFile( daoUtil.getInt( nIndex ) );
                response.setFile( file );
            }

            nIndex++;
            response.setStatus( daoUtil.getInt( nIndex++ ) );
        }

        daoUtil.free( );

        return response;
    }

    /**
     * Delete all responses associate to the form submit whose identifier is
     * specified in parameter
     * 
     * @param nIdFormSubmit The identifier of the formSubmit
     * @param plugin the plugin
     */
    public void delete( int nIdFormSubmit, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdFormSubmit );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Update the the response in the table
     * 
     * @param response instance of the response object to update
     * @param plugin the plugin
     */
    public void store( Response response, Plugin plugin )
    {
        int nIndex = 1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setInt( nIndex++, response.getFormSubmit( ).getIdFormSubmit( ) );
        daoUtil.setString( nIndex++, response.getResponseValue( ) );
        daoUtil.setInt( nIndex++, response.getEntry( ).getIdEntry( ) );

        if ( response.getField( ) != null )
        {
            daoUtil.setInt( nIndex++, response.getField( ).getIdField( ) );
        }
        else
        {
            daoUtil.setIntNull( nIndex++ );
        }

        if ( response.getFile( ) != null )
        {
            daoUtil.setInt( nIndex++, response.getFile( ).getIdFile( ) );
        }
        else
        {
            daoUtil.setIntNull( nIndex++ );
        }
        daoUtil.setInt( nIndex++, response.getStatus( ) );

        daoUtil.setInt( nIndex++, response.getIdResponse( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Load the data of all the response who verify the filter and returns them
     * in a list
     * @param filter the filter
     * @param plugin the plugin
     * @return the list of response
     */
    public List<Response> selectListByFilter( ResponseFilter filter, Plugin plugin )
    {
        boolean bException = false;
        List<Response> responseList = new ArrayList<Response>( );

        StringBuilder sbSQL = new StringBuilder( SQL_QUERY_SELECT_RESPONSE_BY_FILTER );
        sbSQL.append( ( filter.containsIdForm( ) ) ? SQL_FILTER_ID_FORM_SUBMITION : StringUtils.EMPTY );
        sbSQL.append( ( filter.containsIdEntry( ) ) ? SQL_FILTER_ID_ENTRY : StringUtils.EMPTY );
        sbSQL.append( ( filter.containsIdField( ) ) ? SQL_FILTER_ID_FIELD : StringUtils.EMPTY );
        sbSQL.append( SQL_ORDER_BY );
        sbSQL.append( ( filter.containsOrderBy( ) ) ? filter.getOrderBy( ) : SQL_FILTER_ID_RESPONSE );
        sbSQL.append( ( filter.isOrderByAsc( ) ) ? SQL_ASC : SQL_DESC );

        DAOUtil daoUtil = new DAOUtil( sbSQL.toString( ), plugin );
        int nIndex = 1;

        if ( filter.containsIdForm( ) )
        {
            daoUtil.setInt( nIndex, filter.getIdForm( ) );
            nIndex++;
        }

        if ( filter.containsIdEntry( ) )
        {
            daoUtil.setInt( nIndex, filter.getIdEntry( ) );
            nIndex++;
        }

        if ( filter.containsIdField( ) )
        {
            daoUtil.setInt( nIndex, filter.getIdField( ) );
            nIndex++;
        }

        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            nIndex = 1;

            Response response = new Response( );
            response.setIdResponse( daoUtil.getInt( nIndex++ ) );

            FormSubmit formResponse = new FormSubmit( );
            formResponse.setIdFormSubmit( daoUtil.getInt( nIndex++ ) );
            response.setFormSubmit( formResponse );
            response.setResponseValue( daoUtil.getString( nIndex++ ) );

            EntryType entryType = new EntryType( );
            entryType.setClassName( daoUtil.getString( nIndex++ ) );
            entryType.setIdType( daoUtil.getInt( nIndex++ ) );

            IEntry entry = null;

            try
            {
                entry = (IEntry) Class.forName( entryType.getClassName( ) ).newInstance( );
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
            entry.setIdEntry( daoUtil.getInt( nIndex++ ) );
            entry.setTitle( daoUtil.getString( nIndex++ ) );
            response.setEntry( entry );

            // Get field if it exists
            if ( daoUtil.getObject( nIndex ) != null )
            {
                Field field = new Field( );
                field.setIdField( daoUtil.getInt( nIndex ) );
                response.setField( field );
            }

            nIndex++;

            // Get file if it exists
            if ( daoUtil.getObject( nIndex ) != null )
            {
                File file = new File( );
                file.setIdFile( daoUtil.getInt( nIndex ) );
                response.setFile( file );
            }

            nIndex++;
            response.setStatus( daoUtil.getInt( nIndex++ ) );

            responseList.add( response );
        }

        daoUtil.free( );

        return responseList;
    }

    /**
     * return a list of statistic on the entry
     * @param nIdEntry the id of the entry
     * @param plugin the plugin
     * @return return a list of statistic on the entry
     */
    public List<StatisticEntrySubmit> getStatisticByIdEntry( int nIdEntry, Plugin plugin )
    {
        List<StatisticEntrySubmit> listStatisticEntrySubmit = new ArrayList<StatisticEntrySubmit>( );
        StatisticEntrySubmit statisticEntrySubmit;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_COUNT_RESPONSE_BY_ID_ENTRY, plugin );
        daoUtil.setInt( 1, nIdEntry );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            statisticEntrySubmit = new StatisticEntrySubmit( );
            statisticEntrySubmit.setFieldLibelle( daoUtil.getString( 1 ) );
            statisticEntrySubmit.setNumberResponse( daoUtil.getInt( 2 ) );
            listStatisticEntrySubmit.add( statisticEntrySubmit );
        }

        daoUtil.free( );

        return listStatisticEntrySubmit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxNumber( int nIdEntry, int nIdForm, Plugin plugin )
    {
        int nIndex = 1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_MAX_NUMBER, plugin );
        daoUtil.setInt( nIndex++, nIdEntry );
        daoUtil.setInt( nIndex++, nIdForm );
        daoUtil.executeQuery( );

        int nKey = 1;

        if ( daoUtil.next( ) )
        {
            nKey = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free( );

        return nKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void anonymizeEntries( List<Integer> listIdEntries, Timestamp dateCleanTo, Plugin plugin )
    {
        if ( listIdEntries == null || listIdEntries.size( ) <= 0 )
        {
            return;
        }
        StringBuilder sbSql = new StringBuilder( SQL_QUERY_ANONYMIZE_RESPONSES );
        sbSql.append( CONSTANT_QUESTION_MARK );
        for ( int i = 1; i < listIdEntries.size( ); i++ )
        {
            sbSql.append( CONSTANT_COMMA ).append( CONSTANT_QUESTION_MARK );
        }
        sbSql.append( CONSTANT_CLOSE_PARENTHESIS );

        DAOUtil daoUtil = new DAOUtil( sbSql.toString( ), plugin );
        int nIndex = 1;
        daoUtil.setString( nIndex++, FormUtils.CONSTANT_RESPONSE_VALUE_ANONYMIZED );
        // We put the anonymized status twice : once for the new status, and once for the filter
        daoUtil.setInt( nIndex++, Response.CONSTANT_STATUS_ANONYMIZED );
        daoUtil.setInt( nIndex++, Response.CONSTANT_STATUS_ANONYMIZED );
        daoUtil.setTimestamp( nIndex++, dateCleanTo );

        for ( Integer nIdEntry : listIdEntries )
        {
            daoUtil.setInt( nIndex++, nIdEntry );
        }

        daoUtil.executeUpdate( );

        daoUtil.free( );
    }
}
