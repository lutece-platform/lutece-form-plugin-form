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

import fr.paris.lutece.portal.service.rbac.RBACResource;

/**
 *
 * class category
 *
 */
public class Category implements RBACResource
{
    /**
     * Form category resource type
     */
    public static final String RESOURCE_TYPE = "FORM_CATEGORY_TYPE";
    private int _nIdCategory;
    private String _strTitle;
    private String _strColor;

    /**
     *
     * @return the id of the category
     */
    public int getIdCategory( )
    {
        return _nIdCategory;
    }

    /**
     * set the id of the category
     * 
     * @param idCategory
     *            the id of the category
     */
    public void setIdCategory( int idCategory )
    {
        _nIdCategory = idCategory;
    }

    /**
     *
     * @return the title of the category
     */
    public String getTitle( )
    {
        return _strTitle;
    }

    /**
     * set the title of the category
     * 
     * @param title
     *            the title of the category
     */
    public void setTitle( String title )
    {
        _strTitle = title;
    }

    /**
     * @param obj
     *            the category to compare
     * @return true if category in parameter is the same category
     */
    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof Category && ( ( (Category) obj ).getIdCategory( ) == _nIdCategory ) )
        {
            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode( )
    {
        // We just return the id of the category to be sure
        // that a.equals( b ) => a.hashCode( ) == b.hashCode( )
        return _nIdCategory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResourceTypeCode( )
    {
        return RESOURCE_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResourceId( )
    {
        return Integer.toString( _nIdCategory );
    }

    /**
     * @param strColor
     *            the _strColor to set
     */
    public void setColor( String strColor )
    {
        this._strColor = strColor;
    }

    /**
     * @return the _strColor
     */
    public String getColor( )
    {
        return _strColor;
    }
}
