/*
 * Copyright (c) 2002-2017, Mairie de Paris
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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * class CategoryDAO
 *
 */
public class CategoryDAO implements ICategoryDAO
{
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_category) FROM form_category";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_category,title,color FROM form_category WHERE id_category=?";
    private static final String SQL_QUERY_SELECT = "SELECT id_category,title,color FROM form_category ORDER BY title";
    private static final String SQL_QUERY_INSERT = "INSERT INTO form_category (id_category,title,color )VALUES(?,?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE form_category SET id_category=?,title=?,color=? WHERE id_category=?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM form_category WHERE id_category = ? ";
    private static final String SQL_QUERY_COUNT_NUMBER_OF_FORM_ASSOCIATE_TO_THE_CATEGORY = "select COUNT(id_category) "
            + " FROM form_form WHERE id_category=? ";

    /**
     * Generates a new primary key
     *
     * @param plugin
     *            the plugin
     * @return The new primary key
     */
    protected int newPrimaryKey( Plugin plugin )
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
     * {@inheritDoc}
     */
    @Override
    public void insert( Category category, Plugin plugin )
    {
        category.setIdCategory( newPrimaryKey( plugin ) );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        daoUtil.setInt( 1, category.getIdCategory( ) );
        daoUtil.setString( 2, category.getTitle( ) );
        daoUtil.setString( 3, category.getColor( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( Category category, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setInt( 1, category.getIdCategory( ) );
        daoUtil.setString( 2, category.getTitle( ) );
        daoUtil.setString( 3, category.getColor( ) );
        daoUtil.setInt( 4, category.getIdCategory( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Category load( int idKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, idKey );
        daoUtil.executeQuery( );

        Category category = null;

        if ( daoUtil.next( ) )
        {
            category = new Category( );
            category.setIdCategory( daoUtil.getInt( 1 ) );
            category.setTitle( daoUtil.getString( 2 ) );
            category.setColor( daoUtil.getString( 3 ) );
        }

        daoUtil.free( );

        return category;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Category> select( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.executeQuery( );

        Category category = null;
        List<Category> listCategory = new ArrayList<Category>( );

        while ( daoUtil.next( ) )
        {
            category = new Category( );
            category.setIdCategory( daoUtil.getInt( 1 ) );
            category.setTitle( daoUtil.getString( 2 ) );
            category.setColor( daoUtil.getString( 3 ) );
            listCategory.add( category );
        }

        daoUtil.free( );

        return listCategory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdCategory, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdCategory );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAssociateToForm( int nIdCategory, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_COUNT_NUMBER_OF_FORM_ASSOCIATE_TO_THE_CATEGORY, plugin );
        daoUtil.setInt( 1, nIdCategory );
        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            if ( daoUtil.getInt( 1 ) != 0 )
            {
                daoUtil.free( );

                return true;
            }
        }

        daoUtil.free( );

        return false;
    }
}
