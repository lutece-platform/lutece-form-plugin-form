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
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * class GraphTypeDAO
 *
 */
public class GraphTypeDAO implements IGraphTypeDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT class_name,id_graph_type,title FROM form_graph_type WHERE id_graph_type=?";
    private static final String SQL_QUERY_SELECT = "SELECT id_graph_type,title FROM form_graph_type ";

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphType load( int idKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, idKey );
        daoUtil.executeQuery( );

        GraphType graphType = null;

        if ( daoUtil.next( ) )
        {
            try
            {
                graphType = (GraphType) Class.forName( daoUtil.getString( 1 ) ).newInstance( );
                graphType.setClassName( daoUtil.getString( 1 ) );
                graphType.setIdGraphType( daoUtil.getInt( 2 ) );
                graphType.setTitle( daoUtil.getString( 3 ) );
            }
            catch( ClassNotFoundException e )
            {
                // class doesn't exist
                AppLogService.error( e );

                return null;
            }
            catch( InstantiationException e )
            {
                // Class is abstract or is an interface or haven't accessible builder
                AppLogService.error( e );

                return null;
            }
            catch( IllegalAccessException e )
            {
                // can't access to rhe class
                AppLogService.error( e );

                return null;
            }
        }

        daoUtil.free( );

        return graphType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<GraphType> select( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.executeQuery( );

        List<GraphType> listGraphType = new ArrayList<GraphType>( );
        GraphType graphType = null;

        while ( daoUtil.next( ) )
        {
            graphType = new GraphType( );
            graphType.setIdGraphType( daoUtil.getInt( 1 ) );
            graphType.setTitle( daoUtil.getString( 2 ) );
            listGraphType.add( graphType );
        }

        daoUtil.free( );

        return listGraphType;
    }
}
