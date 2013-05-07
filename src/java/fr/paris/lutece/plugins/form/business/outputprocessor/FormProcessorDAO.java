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
package fr.paris.lutece.plugins.form.business.outputprocessor;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * class FormProcessorDAO
 *
 */
public class FormProcessorDAO implements IFormProcessorDAO
{
    private static final String SQL_QUERY_SELECT_BY_ID_FORM = "SELECT id_form,key_processor" +
        " FROM form_form_processor WHERE id_form=?";
    private static final String SQL_QUERY_SELECT_ALL = "SELECT id_form,key_processor" + " FROM form_form_processor ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO form_form_processor(id_form,key_processor)" +
        "VALUES(?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM form_form_processor WHERE id_form =? AND key_processor=? ";

    /* (non-Javadoc)
         * @see fr.paris.lutece.plugins.form.business.outputprocessor.IFormProcessorDAO#selectByIdForm(int, fr.paris.lutece.portal.service.plugin.Plugin)
         */
    public List<FormProcessor> selectByIdForm( int nIdForm, Plugin plugin )
    {
        List<FormProcessor> listFormProcessor = new ArrayList<FormProcessor>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.executeQuery(  );

        FormProcessor formProcessor = null;

        while ( daoUtil.next(  ) )
        {
            formProcessor = new FormProcessor(  );
            formProcessor.setIdForm( daoUtil.getInt( 1 ) );
            formProcessor.setKeyProcessor( daoUtil.getString( 2 ) );
            listFormProcessor.add( formProcessor );
        }

        daoUtil.free(  );

        return listFormProcessor;
    }

    /* (non-Javadoc)
         * @see fr.paris.lutece.plugins.form.business.outputprocessor.IFormProcessorDAO#selectAll(fr.paris.lutece.portal.service.plugin.Plugin)
         */
    public List<FormProcessor> selectAll( Plugin plugin )
    {
        List<FormProcessor> listFormProcessor = new ArrayList<FormProcessor>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL, plugin );

        daoUtil.executeQuery(  );

        FormProcessor formProcessor = null;

        while ( daoUtil.next(  ) )
        {
            formProcessor = new FormProcessor(  );
            formProcessor.setIdForm( daoUtil.getInt( 1 ) );
            formProcessor.setKeyProcessor( daoUtil.getString( 2 ) );
            listFormProcessor.add( formProcessor );
        }

        daoUtil.free(  );

        return listFormProcessor;
    }

    /* (non-Javadoc)
         * @see fr.paris.lutece.plugins.form.business.outputprocessor.IFormProcessorDAO#insert(fr.paris.lutece.plugins.form.business.outputprocessor.FormProcessor, fr.paris.lutece.portal.service.plugin.Plugin)
         */
    public void insert( FormProcessor formProcessor, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        daoUtil.setInt( 1, formProcessor.getIdForm(  ) );
        daoUtil.setString( 2, formProcessor.getKeyProcessor(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /* (non-Javadoc)
         * @see fr.paris.lutece.plugins.form.business.outputprocessor.IFormProcessorDAO#delete(fr.paris.lutece.plugins.form.business.outputprocessor.FormProcessor, fr.paris.lutece.portal.service.plugin.Plugin)
         */
    public void delete( FormProcessor formProcessor, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, formProcessor.getIdForm(  ) );
        daoUtil.setString( 2, formProcessor.getKeyProcessor(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
}
