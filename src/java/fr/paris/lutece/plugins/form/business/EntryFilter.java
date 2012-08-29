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


/**
 *
 * class EntryFilter
 *
 */
public class EntryFilter
{
    public static final String ALL_STRING = "all";
    public static final int ALL_INT = -1;
    public static final int FILTER_FALSE = 0;
    public static final int FILTER_TRUE = 1;
    public static final int FILTER_GROUP_ENTRY = 9;
    private int _nIdForm = ALL_INT;
    private int _nIdFieldDepend = ALL_INT;
    private int _nIdEntryParent = ALL_INT;
    private int _nEntryParentNull = ALL_INT;
    private int _nFieldDependNull = ALL_INT;
    private int _nIdIsGroup = ALL_INT;
    private int _nIdIsComment = ALL_INT;
    private int _nIdEntryType = ALL_INT;

    /**
     *
     * @return  the id of form insert in the filter
     */
    public int getIdForm(  )
    {
        return _nIdForm;
    }

    /**
     * set  the id of form  in the filter
     * @param idForm the id of form to insert in the filter
     */
    public void setIdForm( int idForm )
    {
        _nIdForm = idForm;
    }

    /**
     *
     * @return true if the filter contain an id of form
     */
    public boolean containsIdForm(  )
    {
        return ( _nIdForm != ALL_INT );
    }

    /**
     *
     * @return  the id of field insert in the filter
     */
    public int getIdFieldDepend(  )
    {
        return _nIdFieldDepend;
    }

    /**
     * set the id of field depend  in the filter
     * @param idField the id of field depend to insert in the filter
     */
    public void setIdFieldDepend( int idField )
    {
        _nIdFieldDepend = idField;
    }

    /**
     *
     * @return true if the filter contain an id of field depend
     */
    public boolean containsIdField(  )
    {
        return ( _nIdFieldDepend != ALL_INT );
    }

    /**
     *
     * @return  the id of parent entry insert in the filter
     */
    public int getIdEntryParent(  )
    {
        return _nIdEntryParent;
    }

    /**
     * set the id of parent entry
     * @param idEntryParent the id of parent entry to insert in the filter
     */
    public void setIdEntryParent( int idEntryParent )
    {
        _nIdEntryParent = idEntryParent;
    }

    /**
     *
     * @return true if the filter contain an parent id
     */
    public boolean containsIdEntryParent(  )
    {
        return ( _nIdEntryParent != ALL_INT );
    }

    /**
     *
     * @return 1 if the id of parent entry must be null
     */
    public int getEntryParentNull(  )
    {
        return _nEntryParentNull;
    }

    /**
     * set 1 if the id of parent entry must be null
     * @param idEntryParentNull 1 if the id of parent entry must be null
     */
    public void setEntryParentNull( int idEntryParentNull )
    {
        _nEntryParentNull = idEntryParentNull;
    }

    /**
     *
     * @return true if the parent entry must be null
     */
    public boolean containsEntryParentNull(  )
    {
        return ( _nEntryParentNull != ALL_INT );
    }

    /**
     *
     * @return  1 if the id of field depend  must be null
     */
    public int getFieldDependNull(  )
    {
        return _nFieldDependNull;
    }

    /**
     * set 1 if the id of field depend  must be null
     * @param idFieldDependNull 1 if the id of field depend  must be null
     */
    public void setFieldDependNull( int idFieldDependNull )
    {
        _nFieldDependNull = idFieldDependNull;
    }

    /**
     *
     * @return true if  the id of field depend  must be null
     */
    public boolean containsFieldDependNull(  )
    {
        return ( _nFieldDependNull != ALL_INT );
    }

    /**
     *
     * @return 1 if the entry is a group,0 if the entry is not a group
     */
    public int getIdIsGroup(  )
    {
        return _nIdIsGroup;
    }

    /**
     * set 1 if the entry must be a group,0 if the entry must  not be a group
     * @param idIsGroup  1 if the entry must be a group,0 if the entry must  not be a group
     */
    public void setIdIsGroup( int idIsGroup )
    {
        _nIdIsGroup = idIsGroup;
    }

    /**
     *
     * @return true  if the entry must be a group or must not be a group
     */
    public boolean containsIdIsGroup(  )
    {
        return ( _nIdIsGroup != ALL_INT );
    }

    /**
     *
     * @return  1 if the entry must be a comment,0 if the entry must  not be a comment
     */
    public int getIdIsComment(  )
    {
        return _nIdIsComment;
    }

    /**
     * set  1 if the entry must be a comment,0 if the entry must  not be a comment
     * @param idComment 1 if the entry must be a comment,0 if the entry must  not be a comment
     */
    public void setIdIsComment( int idComment )
    {
        _nIdIsComment = idComment;
    }

    /**
     *
     * @return true  if the entry must be a comment or must not be a comment
     */
    public boolean containsIdIsComment(  )
    {
        return ( _nIdIsComment != ALL_INT );
    }

    /**
     * Get the id entry type
     * @return  the id of entry type insert in the filter
     */
    public int getIdEntryType(  )
    {
        return _nIdEntryType;
    }

    /**
     * set the id of entry type
     * @param nIdEntryType the id of entry type to insert in the filter
     */
    public void setIdEntryType( int nIdEntryType )
    {
        _nIdEntryType = nIdEntryType;
    }

    /**
     * Check if the filter contains the id entry type
     * @return true if the filter contains an id entry type
     */
    public boolean containsIdEntryType(  )
    {
        return ( _nIdEntryType != ALL_INT );
    }
}
