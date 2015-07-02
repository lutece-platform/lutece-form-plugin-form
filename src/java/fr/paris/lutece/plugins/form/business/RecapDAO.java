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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;


/**
 * This class provides Data Access methods for ReportingFiche objects
 */
public final class RecapDAO implements IRecapDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_recap ) FROM form_recap";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_recap,back_url,id_graph_type,recap_message,recap_data," +
        "graph,graph_three_dimension,graph_legende,graph_value_legende,graph_label" +
        " FROM form_recap  WHERE id_recap = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO form_recap(id_recap,back_url,id_graph_type,recap_message,recap_data," +
        "graph,graph_three_dimension,graph_legende,graph_value_legende,graph_label)" + " VALUES(?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM form_recap WHERE id_recap = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE  form_recap SET " +
        "id_recap=?,back_url=?,id_graph_type=?,recap_message=?,recap_data=?," +
        "graph=?,graph_three_dimension=?,graph_legende=?,graph_value_legende=?,graph_label=? WHERE id_recap= ?";

    /**
     * Generates a new primary key
     *
     * @param plugin the plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
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
    public synchronized int insert( Recap recap, Plugin plugin )
    {
        recap.setIdRecap( newPrimaryKey( plugin ) );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        daoUtil.setInt( 1, recap.getIdRecap(  ) );
        daoUtil.setString( 2, recap.getBackUrl(  ) );

        if ( recap.getGraphType(  ) == null )
        {
            daoUtil.setIntNull( 3 );
        }
        else
        {
            daoUtil.setInt( 3, recap.getGraphType(  ).getIdGraphType(  ) );
        }

        daoUtil.setString( 4, recap.getRecapMessage(  ) );
        daoUtil.setBoolean( 5, recap.isRecapData(  ) );
        daoUtil.setBoolean( 6, recap.isGraph(  ) );
        daoUtil.setBoolean( 7, recap.isGraphThreeDimension(  ) );
        daoUtil.setBoolean( 8, recap.isGraphLegende(  ) );
        daoUtil.setString( 9, recap.getGraphValueLegende(  ) );
        daoUtil.setBoolean( 10, recap.isGraphLabelValue(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );

        return recap.getIdRecap(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Recap load( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery(  );

        Recap recap = null;
        GraphType graphType = null;

        if ( daoUtil.next(  ) )
        {
            recap = new Recap(  );
            recap.setIdRecap( daoUtil.getInt( 1 ) );
            recap.setBackUrl( daoUtil.getString( 2 ) );

            if ( daoUtil.getObject( 3 ) != null )
            {
                graphType = new GraphType(  );
                graphType.setIdGraphType( daoUtil.getInt( 3 ) );
                recap.setGraphType( graphType );
            }

            recap.setRecapMessage( daoUtil.getString( 4 ) );
            recap.setRecapData( daoUtil.getBoolean( 5 ) );
            recap.setGraph( daoUtil.getBoolean( 6 ) );

            recap.setGraphThreeDimension( daoUtil.getBoolean( 7 ) );
            recap.setGraphLegende( daoUtil.getBoolean( 8 ) );
            recap.setGraphValueLegende( daoUtil.getString( 9 ) );
            recap.setGraphLabelValue( daoUtil.getBoolean( 10 ) );
        }

        daoUtil.free(  );

        return recap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdRecap, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdRecap );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( Recap recap, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setInt( 1, recap.getIdRecap(  ) );
        daoUtil.setString( 2, recap.getBackUrl(  ) );

        if ( recap.getGraphType(  ) == null )
        {
            daoUtil.setIntNull( 3 );
        }
        else
        {
            daoUtil.setInt( 3, recap.getGraphType(  ).getIdGraphType(  ) );
        }

        daoUtil.setString( 4, recap.getRecapMessage(  ) );
        daoUtil.setBoolean( 5, recap.isRecapData(  ) );
        daoUtil.setBoolean( 6, recap.isGraph(  ) );
        daoUtil.setBoolean( 7, recap.isGraphThreeDimension(  ) );
        daoUtil.setBoolean( 8, recap.isGraphLegende(  ) );
        daoUtil.setString( 9, recap.getGraphValueLegende(  ) );
        daoUtil.setBoolean( 10, recap.isGraphLabelValue(  ) );
        daoUtil.setInt( 11, recap.getIdRecap(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
}
