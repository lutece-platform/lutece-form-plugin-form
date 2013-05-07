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
package fr.paris.lutece.plugins.form.business.file;

import fr.paris.lutece.plugins.form.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;


/**
 * This class provides Data Access methods for Field objects
 */
public final class FileDAO implements IFileDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = " SELECT max( id_file ) FROM form_file ";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_file, title, id_physical_file, file_size, mime_type" +
        " FROM form_file WHERE id_file = ? ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO form_file ( id_file, title, id_physical_file, file_size, mime_type )" +
        " VALUES( ?,?,?,?,? )";
    private static final String SQL_QUERY_DELETE = " DELETE FROM form_file WHERE id_file = ? ";
    private static final String SQL_QUERY_UPDATE = " UPDATE form_file SET " +
        " title = ?, id_physical_file = ?, file_size = ?, mime_type = ? WHERE id_file = ? ";

    /**
     * {@inheritDoc}
     */
    public int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery(  );

        int nKey = 1;

        if ( daoUtil.next(  ) )
        {
            nKey = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free(  );

        return nKey;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized int insert( File file, Plugin plugin )
    {
        file.setIdFile( newPrimaryKey( plugin ) );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        int nIndex = 1;
        daoUtil.setInt( nIndex++, file.getIdFile(  ) );
        daoUtil.setString( nIndex++, file.getTitle(  ) );

        if ( file.getPhysicalFile(  ) != null )
        {
            daoUtil.setInt( nIndex++, file.getPhysicalFile(  ).getIdPhysicalFile(  ) );
        }
        else
        {
            daoUtil.setIntNull( nIndex++ );
        }

        daoUtil.setInt( nIndex++, file.getSize(  ) );
        daoUtil.setString( nIndex++, file.getMimeType(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );

        return file.getIdFile(  );
    }

    /**
     * {@inheritDoc}
     */
    public File load( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery(  );

        File file = null;
        PhysicalFile physicalFile = null;

        if ( daoUtil.next(  ) )
        {
            int nIndex = 1;

            file = new File(  );
            file.setIdFile( daoUtil.getInt( nIndex++ ) );
            file.setTitle( daoUtil.getString( nIndex++ ) );

            if ( daoUtil.getObject( nIndex ) != null )
            {
                physicalFile = new PhysicalFile(  );
                physicalFile.setIdPhysicalFile( daoUtil.getInt( nIndex ) );
                file.setPhysicalFile( physicalFile );
            }

            nIndex++;

            file.setSize( daoUtil.getInt( nIndex++ ) );
            file.setMimeType( daoUtil.getString( nIndex++ ) );
        }

        daoUtil.free(  );

        return file;
    }

    /**
     * {@inheritDoc}
     */
    public void delete( int nIdFile, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdFile );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    public void store( File file, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        int nIndex = 1;
        daoUtil.setString( nIndex++, file.getTitle(  ) );

        if ( file.getPhysicalFile(  ) != null )
        {
            daoUtil.setInt( nIndex++, file.getPhysicalFile(  ).getIdPhysicalFile(  ) );
        }
        else
        {
            daoUtil.setIntNull( nIndex++ );
        }

        daoUtil.setInt( nIndex++, file.getSize(  ) );
        daoUtil.setString( nIndex++, file.getMimeType(  ) );

        daoUtil.setInt( nIndex++, file.getIdFile(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
}
