/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for Entry objects
 */
public final class EntryDAO implements IEntryDAO
{
    // Constants
    private static final String EMPTY_STRING = "";
    private static final String SQL_QUERY_NEW_PK = "SELECT MAX( id_entry ) FROM form_entry";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT ent.id_type,typ.title,typ.is_group,typ.is_comment,typ.class_name,typ.is_mylutece_user," +
        "ent.id_entry,ent.id_form,form.title,ent.id_parent,ent.title,ent.help_message," +
        "ent.comment,ent.mandatory,ent.fields_in_line," +
        "ent.pos,ent.id_field_depend,ent.confirm_field,ent.confirm_field_title,ent.field_unique, ent.map_provider " +
        "FROM form_entry ent,form_entry_type typ	,form_form form  WHERE ent.id_entry = ? and ent.id_type=typ.id_type and " +
        "ent.id_form=form.id_form";
    private static final String SQL_QUERY_INSERT = "INSERT INTO form_entry ( " +
        "id_entry,id_form,id_type,id_parent,title,help_message," + "comment,mandatory,fields_in_line," +
        "pos,id_field_depend,confirm_field,confirm_field_title,field_unique,map_provider ) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM form_entry WHERE id_entry = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE  form_entry SET " +
        "id_entry=?,id_form=?,id_type=?,id_parent=?,title=?,help_message=?," +
        "comment=?,mandatory=?,fields_in_line=?," +
        "pos=?,id_field_depend=?,confirm_field=?,confirm_field_title=?,field_unique=?,map_provider=? WHERE id_entry=?";
    private static final String SQL_QUERY_SELECT_ENTRY_BY_FILTER = "SELECT ent.id_type,typ.title,typ.is_group,typ.is_comment,typ.class_name,typ.is_mylutece_user," +
        "ent.id_entry,ent.id_form,ent.id_parent,ent.title,ent.help_message," +
        "ent.comment,ent.mandatory,ent.fields_in_line," +
        "ent.pos,ent.id_field_depend,ent.confirm_field,ent.confirm_field_title,ent.field_unique,ent.map_provider " +
        "FROM form_entry ent,form_entry_type typ WHERE ent.id_type=typ.id_type ";
    private static final String SQL_QUERY_SELECT_NUMBER_ENTRY_BY_FILTER = "SELECT COUNT(ent.id_entry) " +
        "FROM form_entry ent,form_entry_type typ WHERE ent.id_type=typ.id_type ";
    private static final String SQL_QUERY_NEW_POSITION = "SELECT MAX(pos) " + "FROM form_entry ";
    private static final String SQL_QUERY_NUMBER_CONDITIONAL_QUESTION = "SELECT  COUNT(e2.id_entry) " +
        "FROM form_entry e1,form_field f1,form_entry e2 WHERE e1.id_entry=? AND e1.id_entry=f1.id_entry and e2.id_field_depend=f1.id_field ";
    private static final String SQL_FILTER_ID_FORM = " AND ent.id_form = ? ";
    private static final String SQL_FILTER_ID_PARENT = " AND ent.id_parent = ? ";
    private static final String SQL_FILTER_ID_PARENT_IS_NULL = " AND ent.id_parent IS NULL ";
    private static final String SQL_FILTER_IS_GROUP = " AND typ.is_group = ? ";
    private static final String SQL_FILTER_IS_COMMENT = " AND typ.is_comment = ? ";
    private static final String SQL_FILTER_ID_FIELD_DEPEND = " AND ent.id_field_depend = ? ";
    private static final String SQL_FILTER_ID_FIELD_DEPEND_IS_NULL = " AND ent.id_field_depend IS NULL ";
    private static final String SQL_ORDER_BY_POSITION = " ORDER BY ent.pos ";

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
     * Generates a new entry position
     * @param plugin the plugin
     * @return the new entry position
     */
    private int newPosition( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_POSITION, plugin );
        daoUtil.executeQuery(  );

        int nPos;

        if ( !daoUtil.next(  ) )
        {
            // if the table is empty
            nPos = 1;
        }

        nPos = daoUtil.getInt( 1 ) + 1;
        daoUtil.free(  );

        return nPos;
    }

    /**
     * return the number of conditional question who are associate to the entry
     * @param nIdEntry the id of the entry
     * @param plugin the plugin
     * @return the number of conditional question
     */
    private int nunberConditionalQuestion( int nIdEntry, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NUMBER_CONDITIONAL_QUESTION, plugin );
        daoUtil.setInt( 1, nIdEntry );
        daoUtil.executeQuery(  );

        int nNumberConditionalQuestion = 0;

        if ( daoUtil.next(  ) )
        {
            nNumberConditionalQuestion = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nNumberConditionalQuestion;
    }

    /**
     * Insert a new record in the table.
     *
     * @param entry instance of the Entry object to insert
     * @param plugin the plugin
     * @return the id of the new entry
     */
    public synchronized int insert( IEntry entry, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        entry.setIdEntry( newPrimaryKey( plugin ) );

        daoUtil.setInt( 1, entry.getIdEntry(  ) );
        daoUtil.setInt( 2, entry.getForm(  ).getIdForm(  ) );
        daoUtil.setInt( 3, entry.getEntryType(  ).getIdType(  ) );

        if ( entry.getParent(  ) != null )
        {
            daoUtil.setInt( 4, entry.getParent(  ).getIdEntry(  ) );
        }
        else
        {
            daoUtil.setIntNull( 4 );
        }

        daoUtil.setString( 5, entry.getTitle(  ) );
        daoUtil.setString( 6, entry.getHelpMessage(  ) );
        daoUtil.setString( 7, entry.getComment(  ) );
        daoUtil.setBoolean( 8, entry.isMandatory(  ) );
        daoUtil.setBoolean( 9, entry.isFieldInLine(  ) );
        daoUtil.setInt( 10, newPosition( plugin ) );

        if ( entry.getFieldDepend(  ) != null )
        {
            daoUtil.setInt( 11, entry.getFieldDepend(  ).getIdField(  ) );
        }
        else
        {
            daoUtil.setIntNull( 11 );
        }

        daoUtil.setBoolean( 12, entry.isConfirmField(  ) );
        daoUtil.setString( 13, entry.getConfirmFieldTitle(  ) );
        daoUtil.setBoolean( 14, entry.isUnique(  ) );

        String strMapProviderKey = ( entry.getMapProvider(  ) == null ) ? FormUtils.EMPTY_STRING
                                                                        : entry.getMapProvider(  ).getKey(  );
        daoUtil.setString( 15, strMapProviderKey );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );

        return entry.getIdEntry(  );
    }

    /**
     * Load the data of the entry from the table
     *
     * @param nId The identifier of the entry
     * @param plugin the plugin
     * @return the instance of the Entry
     */
    public IEntry load( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery(  );

        IEntry entry = null;
        EntryType entryType = null;
        IEntry entryParent = null;
        Field fieldDepend = null;
        Form form = null;

        if ( daoUtil.next(  ) )
        {
            entryType = new EntryType(  );
            entryType.setIdType( daoUtil.getInt( 1 ) );
            entryType.setTitle( daoUtil.getString( 2 ) );
            entryType.setGroup( daoUtil.getBoolean( 3 ) );
            entryType.setComment( daoUtil.getBoolean( 4 ) );
            entryType.setClassName( daoUtil.getString( 5 ) );
            entryType.setMyLuteceUser( daoUtil.getBoolean( 6 ) );

            try
            {
                entry = (IEntry) Class.forName( entryType.getClassName(  ) ).newInstance(  );
            }
            catch ( ClassNotFoundException e )
            {
                //  class doesn't exist
                AppLogService.error( e );

                return null;
            }
            catch ( InstantiationException e )
            {
                // Class is abstract or is an  interface or haven't accessible builder
                AppLogService.error( e );

                return null;
            }
            catch ( IllegalAccessException e )
            {
                // can't access to rhe class
                AppLogService.error( e );

                return null;
            }

            entry.setEntryType( entryType );
            entry.setIdEntry( daoUtil.getInt( 7 ) );
            // insert form
            form = new Form(  );
            form.setIdForm( daoUtil.getInt( 8 ) );
            form.setTitle( daoUtil.getString( 9 ) );
            entry.setForm( form );

            if ( daoUtil.getObject( 10 ) != null )
            {
                entryParent = new Entry(  );
                entryParent.setIdEntry( daoUtil.getInt( 10 ) );
                entry.setParent( entryParent );
            }

            entry.setTitle( daoUtil.getString( 11 ) );
            entry.setHelpMessage( daoUtil.getString( 12 ) );
            entry.setComment( daoUtil.getString( 13 ) );
            entry.setMandatory( daoUtil.getBoolean( 14 ) );
            entry.setFieldInLine( daoUtil.getBoolean( 15 ) );
            entry.setPosition( daoUtil.getInt( 16 ) );

            if ( daoUtil.getObject( 17 ) != null )
            {
                fieldDepend = new Field(  );
                fieldDepend.setIdField( daoUtil.getInt( 17 ) );
                entry.setFieldDepend( fieldDepend );
            }

            entry.setConfirmField( daoUtil.getBoolean( 18 ) );
            entry.setConfirmFieldTitle( daoUtil.getString( 19 ) );
            entry.setUnique( daoUtil.getBoolean( 20 ) );
            entry.setMapProvider( MapProviderManager.getMapProvider( daoUtil.getString( 21 ) ) );

            entry.setNumberConditionalQuestion( nunberConditionalQuestion( entry.getIdEntry(  ), plugin ) );
        }

        daoUtil.free(  );

        return entry;
    }

    /**
     * Delete a record from the table
     *
     * @param nIdEntry The identifier of the entry
     * @param plugin the plugin
     */
    public void delete( int nIdEntry, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdEntry );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Update the entry in the table
     *
     * @param entry instance of the Entry object to update
     * @param plugin the plugin
     */
    public void store( IEntry entry, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setInt( 1, entry.getIdEntry(  ) );
        daoUtil.setInt( 2, entry.getForm(  ).getIdForm(  ) );
        daoUtil.setInt( 3, entry.getEntryType(  ).getIdType(  ) );

        if ( entry.getParent(  ) != null )
        {
            daoUtil.setInt( 4, entry.getParent(  ).getIdEntry(  ) );
        }
        else
        {
            daoUtil.setIntNull( 4 );
        }

        daoUtil.setString( 5, entry.getTitle(  ) );
        daoUtil.setString( 6, entry.getHelpMessage(  ) );
        daoUtil.setString( 7, entry.getComment(  ) );
        daoUtil.setBoolean( 8, entry.isMandatory(  ) );
        daoUtil.setBoolean( 9, entry.isFieldInLine(  ) );
        daoUtil.setInt( 10, entry.getPosition(  ) );

        if ( entry.getFieldDepend(  ) != null )
        {
            daoUtil.setInt( 11, entry.getFieldDepend(  ).getIdField(  ) );
        }
        else
        {
            daoUtil.setIntNull( 11 );
        }

        daoUtil.setBoolean( 12, entry.isConfirmField(  ) );
        daoUtil.setString( 13, entry.getConfirmFieldTitle(  ) );
        daoUtil.setBoolean( 14, entry.isUnique(  ) );

        String strMapProviderKey = ( entry.getMapProvider(  ) == null ) ? FormUtils.EMPTY_STRING
                                                                        : entry.getMapProvider(  ).getKey(  );
        daoUtil.setString( 15, strMapProviderKey );

        daoUtil.setInt( 16, entry.getIdEntry(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Load the data of all the entry who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of entry
     */
    public List<IEntry> selectEntryListByFilter( EntryFilter filter, Plugin plugin )
    {
        List<IEntry> entryList = new ArrayList<IEntry>(  );
        IEntry entry = null;
        EntryType entryType = null;
        IEntry entryParent = null;
        Field fieldDepend = null;
        Form form = null;

        String strSQL = SQL_QUERY_SELECT_ENTRY_BY_FILTER;
        strSQL += ( ( filter.containsIdForm(  ) ) ? SQL_FILTER_ID_FORM : EMPTY_STRING );
        strSQL += ( ( filter.containsIdEntryParent(  ) ) ? SQL_FILTER_ID_PARENT : EMPTY_STRING );
        strSQL += ( ( filter.containsEntryParentNull(  ) ) ? SQL_FILTER_ID_PARENT_IS_NULL : EMPTY_STRING );
        strSQL += ( ( filter.containsIdIsGroup(  ) ) ? SQL_FILTER_IS_GROUP : EMPTY_STRING );
        strSQL += ( ( filter.containsIdField(  ) ) ? SQL_FILTER_ID_FIELD_DEPEND : EMPTY_STRING );
        strSQL += ( ( filter.containsFieldDependNull(  ) ) ? SQL_FILTER_ID_FIELD_DEPEND_IS_NULL : EMPTY_STRING );

        strSQL += SQL_ORDER_BY_POSITION;

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        if ( filter.containsIdForm(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdForm(  ) );
            nIndex++;
        }

        if ( filter.containsIdEntryParent(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdEntryParent(  ) );
            nIndex++;
        }

        if ( filter.containsIdIsGroup(  ) )
        {
            if ( filter.getIdIsGroup(  ) == 0 )
            {
                daoUtil.setBoolean( nIndex, false );
            }
            else
            {
                daoUtil.setBoolean( nIndex, true );
            }

            nIndex++;
        }

        if ( filter.containsIdField(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdFieldDepend(  ) );
            nIndex++;
        }

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            entryType = new EntryType(  );
            entryType.setIdType( daoUtil.getInt( 1 ) );
            entryType.setTitle( daoUtil.getString( 2 ) );
            entryType.setGroup( daoUtil.getBoolean( 3 ) );
            entryType.setComment( daoUtil.getBoolean( 4 ) );
            entryType.setClassName( daoUtil.getString( 5 ) );
            entryType.setMyLuteceUser( daoUtil.getBoolean( 6 ) );

            try
            {
                entry = (IEntry) Class.forName( entryType.getClassName(  ) ).newInstance(  );
            }
            catch ( ClassNotFoundException e )
            {
                //  class doesn't exist
                AppLogService.error( e );

                return null;
            }
            catch ( InstantiationException e )
            {
                // Class is abstract or is an  interface or haven't accessible builder
                AppLogService.error( e );

                return null;
            }
            catch ( IllegalAccessException e )
            {
                // can't access to rhe class
                AppLogService.error( e );

                return null;
            }

            entry.setEntryType( entryType );
            entry.setIdEntry( daoUtil.getInt( 7 ) );
            // insert form
            form = new Form(  );
            form.setIdForm( daoUtil.getInt( 8 ) );
            entry.setForm( form );

            if ( daoUtil.getObject( 9 ) != null )
            {
                entryParent = new Entry(  );
                entryParent.setIdEntry( daoUtil.getInt( 9 ) );
                entry.setParent( entryParent );
            }

            entry.setTitle( daoUtil.getString( 10 ) );
            entry.setHelpMessage( daoUtil.getString( 11 ) );
            entry.setComment( daoUtil.getString( 12 ) );
            entry.setMandatory( daoUtil.getBoolean( 13 ) );
            entry.setFieldInLine( daoUtil.getBoolean( 14 ) );
            entry.setPosition( daoUtil.getInt( 15 ) );

            if ( daoUtil.getObject( 16 ) != null )
            {
                fieldDepend = new Field(  );
                fieldDepend.setIdField( daoUtil.getInt( 16 ) );
                entry.setFieldDepend( fieldDepend );
            }

            entry.setConfirmField( daoUtil.getBoolean( 17 ) );
            entry.setConfirmFieldTitle( daoUtil.getString( 18 ) );
            entry.setUnique( daoUtil.getBoolean( 19 ) );
            entry.setMapProvider( MapProviderManager.getMapProvider( daoUtil.getString( 20 ) ) );

            entry.setNumberConditionalQuestion( nunberConditionalQuestion( entry.getIdEntry(  ), plugin ) );
            entryList.add( entry );
        }

        daoUtil.free(  );

        return entryList;
    }

    /**
     * Return  the number of entry who verify the filter
     * @param filter the filter
     * @param plugin the plugin
     * @return   the number of entry who verify the filter
     */
    public int selectNumberEntryByFilter( EntryFilter filter, Plugin plugin )
    {
        int nNumberEntry = 0;
        String strSQL = SQL_QUERY_SELECT_NUMBER_ENTRY_BY_FILTER;
        strSQL += ( ( filter.containsIdForm(  ) ) ? SQL_FILTER_ID_FORM : EMPTY_STRING );
        strSQL += ( ( filter.containsIdEntryParent(  ) ) ? SQL_FILTER_ID_PARENT : EMPTY_STRING );
        strSQL += ( ( filter.containsEntryParentNull(  ) ) ? SQL_FILTER_ID_PARENT_IS_NULL : EMPTY_STRING );
        strSQL += ( ( filter.containsIdIsGroup(  ) ) ? SQL_FILTER_IS_GROUP : EMPTY_STRING );
        strSQL += ( ( filter.containsIdIsComment(  ) ) ? SQL_FILTER_IS_COMMENT : EMPTY_STRING );
        strSQL += ( ( filter.containsIdField(  ) ) ? SQL_FILTER_ID_FIELD_DEPEND : EMPTY_STRING );

        strSQL += SQL_ORDER_BY_POSITION;

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        if ( filter.containsIdForm(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdForm(  ) );
            nIndex++;
        }

        if ( filter.containsIdEntryParent(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdEntryParent(  ) );
            nIndex++;
        }

        if ( filter.containsIdIsGroup(  ) )
        {
            if ( filter.getIdIsGroup(  ) == 0 )
            {
                daoUtil.setBoolean( nIndex, false );
            }
            else
            {
                daoUtil.setBoolean( nIndex, true );
            }

            nIndex++;
        }

        if ( filter.containsIdIsComment(  ) )
        {
            if ( filter.getIdIsComment(  ) == 0 )
            {
                daoUtil.setBoolean( nIndex, false );
            }
            else
            {
                daoUtil.setBoolean( nIndex, true );
            }

            nIndex++;
        }

        if ( filter.containsIdField(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdFieldDepend(  ) );
            nIndex++;
        }

        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nNumberEntry = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nNumberEntry;
    }
}
