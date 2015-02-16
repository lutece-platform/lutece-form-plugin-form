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
package fr.paris.lutece.plugins.form.business;

import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseFilter;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for FormResponse objects
 */
public final class FormSubmitDAO implements IFormSubmitDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT MAX( id_form_submit ) FROM form_submit";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_form_submit,date_response,ip,id_form " +
        "FROM form_submit WHERE id_form_submit=? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO form_submit ( " +
        "id_form_submit,date_response,day_date_response,week_date_response,month_date_response,year_date_response,ip,id_form) VALUES(?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM form_submit WHERE id_form_submit = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE  form_submit SET " +
        "id_form_submit=?,date_response=?,ip=?,id_form=? WHERE id_form_submit=?";
    private static final String SQL_QUERY_SELECT_FORM_RESPONSE_BY_FILTER = "SELECT id_form_submit,date_response,ip,id_form " +
        "FROM form_submit ";
    private static final String SQL_QUERY_SELECT_COUNT_BY_FILTER = "SELECT COUNT(id_form_submit) " +
        "FROM form_submit ";
    private static final String SQL_QUERY_SELECT_STATISTIC_FORM_SUBMIT = "SELECT COUNT(*),date_response " +
        "FROM form_submit ";
    private static final String SQL_FILTER_ID_FORM = " id_form = ? ";
    private static final String SQL_FILTER_DATE_FIRST_SUBMIT = " date_response >= ? ";
    private static final String SQL_FILTER_DATE_LAST_SUBMIT = " date_response <= ? ";
    private static final String SQL_GROUP_BY_DAY = " GROUP BY day_date_response,month_date_response,year_date_response ";
    private static final String SQL_GROUP_BY_WEEK = " GROUP BY week_date_response,year_date_response ";
    private static final String SQL_GROUP_BY_MONTH = " GROUP BY month_date_response,year_date_response ";
    private static final String SQL_ORDER_BY_DATE_RESPONSE_ASC = " ORDER BY date_response ASC ";
    private static final String SQL_QUERY_ANONYMIZE_RESPONSES = " UPDATE genatt_response fr SET response_value = ?, status = ? WHERE status < ? AND ( SELECT date_response FROM form_submit fs WHERE fs.id_form_submit = fr.id_form_submit) < ? AND id_entry IN ( ";
    private static final String SQL_QUERY_FIND_FORM_SUBMIT_FROM_ID_RESPONSE = "SELECT fs.id_form_submit,fs.date_response,fs.ip,fs.id_form " +
        "FROM form_submit fs INNER JOIN form_response_submit frs ON fs.id_form_submit = frs.id_form_submit WHERE frs.id_response = ?";
    private static final String SQL_QUERY_FIND_ID_RESPONSE_FROM_FORM_SUBMIT = "SELECT id_response FROM form_response_submit WHERE id_form_submit = ?";
    private static final String SQL_QUERY_ASSOCIATE_RESPONSE_WITH_FORM_SUBMIT = "INSERT INTO form_response_submit (id_response,id_form_submit) VALUES (?,?)";
    private static final String SQL_QUERY_REMOVE_RESPONSE_FORM_SUBMIT_ASSOCIATION = "DELETE FROM form_response_submit (id_response,id_form_submit) WHERE id_response = ? ";
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
     * {@inheritDoc}
     */
    @Override
    public synchronized int insert( FormSubmit formSubmit, Plugin plugin )
    {
        formSubmit.setIdFormSubmit( newPrimaryKey( plugin ) );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        daoUtil.setInt( 1, formSubmit.getIdFormSubmit(  ) );
        daoUtil.setTimestamp( 2, formSubmit.getDateResponse(  ) );
        daoUtil.setInt( 3, FormUtils.getDay( formSubmit.getDateResponse(  ) ) );
        daoUtil.setInt( 4, FormUtils.getWeek( formSubmit.getDateResponse(  ) ) );
        daoUtil.setInt( 5, FormUtils.getMonth( formSubmit.getDateResponse(  ) ) );
        daoUtil.setInt( 6, FormUtils.getYear( formSubmit.getDateResponse(  ) ) );
        daoUtil.setString( 7, formSubmit.getIp(  ) );
        daoUtil.setInt( 8, formSubmit.getForm(  ).getIdForm(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );

        return formSubmit.getIdFormSubmit(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FormSubmit load( int nIdFormSubmit, Plugin plugin )
    {
        FormSubmit formSubmit = null;
        Form form;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nIdFormSubmit );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            formSubmit = new FormSubmit(  );
            formSubmit.setIdFormSubmit( daoUtil.getInt( 1 ) );
            formSubmit.setDateResponse( daoUtil.getTimestamp( 2 ) );
            formSubmit.setIp( daoUtil.getString( 3 ) );
            form = new Form(  );
            form.setIdForm( daoUtil.getInt( 4 ) );
            formSubmit.setForm( form );
        }

        daoUtil.free(  );

        return formSubmit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdFormSubmit, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdFormSubmit );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( FormSubmit formSubmit, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setInt( 1, formSubmit.getIdFormSubmit(  ) );
        daoUtil.setTimestamp( 2, formSubmit.getDateResponse(  ) );
        daoUtil.setString( 3, formSubmit.getIp(  ) );
        daoUtil.setInt( 4, formSubmit.getForm(  ).getIdForm(  ) );
        daoUtil.setInt( 5, formSubmit.getIdFormSubmit(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FormSubmit> selectListByFilter( ResponseFilter filter, Plugin plugin )
    {
        List<FormSubmit> formResponseList = new ArrayList<FormSubmit>(  );
        FormSubmit formSubmit;
        Form form;
        List<String> listStrFilter = new ArrayList<String>(  );

        if ( filter.containsIdResource(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_FORM );
        }

        if ( filter.containsDateFirst(  ) )
        {
            listStrFilter.add( SQL_FILTER_DATE_FIRST_SUBMIT );
        }

        if ( filter.containsDateLast(  ) )
        {
            listStrFilter.add( SQL_FILTER_DATE_LAST_SUBMIT );
        }

        String strSQL = FormUtils.buildRequestWithFilter( SQL_QUERY_SELECT_FORM_RESPONSE_BY_FILTER, listStrFilter,
                null, SQL_ORDER_BY_DATE_RESPONSE_ASC );
        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        if ( filter.containsIdResource(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdResource(  ) );
            nIndex++;
        }

        if ( filter.containsDateFirst(  ) )
        {
            daoUtil.setTimestamp( nIndex, filter.getDateFirst(  ) );
            nIndex++;
        }

        if ( filter.containsDateLast(  ) )
        {
            daoUtil.setTimestamp( nIndex, filter.getDateLast(  ) );
            nIndex++;
        }

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            formSubmit = new FormSubmit(  );
            formSubmit.setIdFormSubmit( daoUtil.getInt( 1 ) );
            formSubmit.setDateResponse( daoUtil.getTimestamp( 2 ) );
            formSubmit.setIp( daoUtil.getString( 3 ) );
            form = new Form(  );
            form.setIdForm( daoUtil.getInt( 4 ) );
            formSubmit.setForm( form );
            formResponseList.add( formSubmit );
        }

        daoUtil.free(  );

        return formResponseList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int selectCountByFilter( ResponseFilter filter, Plugin plugin )
    {
        int nIdCount = 0;
        List<String> listStrFilter = new ArrayList<String>(  );

        if ( filter.containsIdResource(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_FORM );
        }

        if ( filter.containsDateFirst(  ) )
        {
            listStrFilter.add( SQL_FILTER_DATE_FIRST_SUBMIT );
        }

        if ( filter.containsDateLast(  ) )
        {
            listStrFilter.add( SQL_FILTER_DATE_LAST_SUBMIT );
        }

        String strSQL = FormUtils.buildRequestWithFilter( SQL_QUERY_SELECT_COUNT_BY_FILTER, listStrFilter, null, null );
        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        if ( filter.containsIdResource(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdResource(  ) );
            nIndex++;
        }

        if ( filter.containsDateFirst(  ) )
        {
            daoUtil.setTimestamp( nIndex, filter.getDateFirst(  ) );
            nIndex++;
        }

        if ( filter.containsDateLast(  ) )
        {
            daoUtil.setTimestamp( nIndex, filter.getDateLast(  ) );
            nIndex++;
        }

        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nIdCount = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nIdCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<StatisticFormSubmit> selectStatisticFormSubmit( ResponseFilter filter, Plugin plugin )
    {
        List<StatisticFormSubmit> statList = new ArrayList<StatisticFormSubmit>(  );
        StatisticFormSubmit statistic;
        List<String> listStrFilter = new ArrayList<String>(  );
        List<String> listStrGroupBy = new ArrayList<String>(  );

        if ( filter.containsIdResource(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_FORM );
        }

        if ( filter.containsDateFirst(  ) )
        {
            listStrFilter.add( SQL_FILTER_DATE_FIRST_SUBMIT );
        }

        if ( filter.containsDateLast(  ) )
        {
            listStrFilter.add( SQL_FILTER_DATE_LAST_SUBMIT );
        }

        if ( filter.isGroupbyDay(  ) )
        {
            listStrGroupBy.add( SQL_GROUP_BY_DAY );
        }

        if ( filter.isGroupbyWeek(  ) )
        {
            listStrGroupBy.add( SQL_GROUP_BY_WEEK );
        }

        if ( filter.isGroupbyMonth(  ) )
        {
            listStrGroupBy.add( SQL_GROUP_BY_MONTH );
        }

        String strSQL = FormUtils.buildRequestWithFilter( SQL_QUERY_SELECT_STATISTIC_FORM_SUBMIT, listStrFilter,
                listStrGroupBy, SQL_ORDER_BY_DATE_RESPONSE_ASC );
        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        if ( filter.containsIdResource(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdResource(  ) );
            nIndex++;
        }

        if ( filter.containsDateFirst(  ) )
        {
            daoUtil.setTimestamp( nIndex, filter.getDateFirst(  ) );
            nIndex++;
        }

        if ( filter.containsDateLast(  ) )
        {
            daoUtil.setTimestamp( nIndex, filter.getDateLast(  ) );
            nIndex++;
        }

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            statistic = new StatisticFormSubmit(  );
            statistic.setNumberResponse( daoUtil.getInt( 1 ) );
            statistic.setStatisticDate( daoUtil.getTimestamp( 2 ) );
            statList.add( statistic );
        }

        daoUtil.free(  );

        return statList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void anonymizeEntries( List<Integer> listIdEntries, Timestamp dateCleanTo, Plugin plugin )
    {
        if ( ( listIdEntries == null ) || ( listIdEntries.size(  ) <= 0 ) )
        {
            return;
        }

        StringBuilder sbSql = new StringBuilder( SQL_QUERY_ANONYMIZE_RESPONSES );
        sbSql.append( CONSTANT_QUESTION_MARK );

        for ( int i = 1; i < listIdEntries.size(  ); i++ )
        {
            sbSql.append( CONSTANT_COMMA ).append( CONSTANT_QUESTION_MARK );
        }

        sbSql.append( CONSTANT_CLOSE_PARENTHESIS );

        DAOUtil daoUtil = new DAOUtil( sbSql.toString(  ), plugin );
        int nIndex = 1;
        daoUtil.setString( nIndex++, GenericAttributesUtils.CONSTANT_RESPONSE_VALUE_ANONYMIZED );
        // We put the anonymized status twice : once for the new status, and once for the filter
        daoUtil.setInt( nIndex++, Response.CONSTANT_STATUS_ANONYMIZED );
        daoUtil.setInt( nIndex++, Response.CONSTANT_STATUS_ANONYMIZED );
        daoUtil.setTimestamp( nIndex++, dateCleanTo );

        for ( Integer nIdEntry : listIdEntries )
        {
            daoUtil.setInt( nIndex++, nIdEntry );
        }

        daoUtil.executeUpdate(  );

        daoUtil.free(  );
    }

  

    /**
     * {@inheritDoc}
     */
    @Override
    public FormSubmit findFormSubmitFromResponseId( int nIdResponse, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_FORM_SUBMIT_FROM_ID_RESPONSE, plugin );
        daoUtil.setInt( 1, nIdResponse );

        FormSubmit formSubmit = null;
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            formSubmit = new FormSubmit(  );
            formSubmit.setIdFormSubmit( daoUtil.getInt( 1 ) );
            formSubmit.setDateResponse( daoUtil.getTimestamp( 2 ) );
            formSubmit.setIp( daoUtil.getString( 3 ) );

            Form form = new Form(  );
            form.setIdForm( daoUtil.getInt( 4 ) );
            formSubmit.setForm( form );
        }

        daoUtil.free(  );

        return formSubmit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getResponseListFromIdFormSubmit( int nIdFormSubmit, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_ID_RESPONSE_FROM_FORM_SUBMIT, plugin );
        daoUtil.setInt( 1, nIdFormSubmit );

        List<Integer> listIdResponse = new ArrayList<Integer>(  );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            listIdResponse.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return listIdResponse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void associateResponseWithFormSubmit( int nIdResponse, int nIdFormSubmit, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_ASSOCIATE_RESPONSE_WITH_FORM_SUBMIT, plugin );

        daoUtil.setInt( 1, nIdResponse );
        daoUtil.setInt( 2, nIdFormSubmit );
        daoUtil.executeUpdate(  );

        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeResponseFormSubmitAssociation( int nIdResponse, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_REMOVE_RESPONSE_FORM_SUBMIT_ASSOCIATION, plugin );

        daoUtil.setInt( 1, nIdResponse );
        daoUtil.executeUpdate(  );

        daoUtil.free(  );
    }
}
