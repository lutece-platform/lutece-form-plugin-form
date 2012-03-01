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
package fr.paris.lutece.plugins.form.business;

import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

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
         * @param formSubmit instance of the formResponse object to insert
         * @param plugin the plugin
         * @return the id of the new form Submit
         */
    public synchronized int insert( FormSubmit formSubmit, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        formSubmit.setIdFormSubmit( newPrimaryKey( plugin ) );
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
         * Load the data of the formResponse from the table
         *
         * @param nIdFormSubmit The identifier of the formResponse
         * @param plugin the plugin
         * @return the instance of the formSubmit
         */
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
         * Delete  all  response  associate to the form submit whose identifier is specified in parameter
         *
         * @param nIdFormSubmit The identifier of the formResponse
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
         * Update the the formSubmit in the table
         *
         * @param formSubmit instance of the formSubmit object to update
         * @param plugin the plugin
         */
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
         * Load the data of all the formSubmit who verify the filter and returns them in a  list
         * @param filter the filter
         * @param plugin the plugin
         * @return  the list of formResponse
         */
    public List<FormSubmit> selectListByFilter( ResponseFilter filter, Plugin plugin )
    {
        List<FormSubmit> formResponseList = new ArrayList<FormSubmit>(  );
        FormSubmit formSubmit;
        Form form;
        List<String> listStrFilter = new ArrayList<String>(  );

        if ( filter.containsIdForm(  ) )
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

        if ( filter.containsIdForm(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdForm(  ) );
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
         * Load the data of all the formSubmit who verify the filter and returns them in a  list
         * @param filter the filter
         * @param plugin the plugin
         * @return  the list of formResponse
         */
    public int selectCountByFilter( ResponseFilter filter, Plugin plugin )
    {
        int nIdCount = 0;
        List<String> listStrFilter = new ArrayList<String>(  );

        if ( filter.containsIdForm(  ) )
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

        if ( filter.containsIdForm(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdForm(  ) );
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
         * Load the number of formSubmit group by day  who verify the filter and returns them in a  list of statistic
         * @param filter the filter
         * @param plugin the plugin
         * @return  the list of statistic
         */
    public List<StatisticFormSubmit> selectStatisticFormSubmit( ResponseFilter filter, Plugin plugin )
    {
        List<StatisticFormSubmit> statList = new ArrayList<StatisticFormSubmit>(  );
        StatisticFormSubmit statistic;
        List<String> listStrFilter = new ArrayList<String>(  );
        List<String> listStrGroupBy = new ArrayList<String>(  );

        if ( filter.containsIdForm(  ) )
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

        if ( filter.containsIdForm(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdForm(  ) );
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
}
