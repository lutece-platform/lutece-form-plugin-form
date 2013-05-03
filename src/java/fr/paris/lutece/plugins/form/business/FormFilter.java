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
 * class FormFilter
 *
 */
public class FormFilter
{
    public static final String ALL_STRING = "all";
    public static final int ALL_INT = -1;
    private int _nIdState = ALL_INT;
    private int _nIdAutoPublicationState = ALL_INT;
    private String _strWorkgroup = ALL_STRING;
    private boolean _bDateBeginDisponibilityAfterCurrentDate;
    private boolean _bDateEndDisponibilityBeforeCurrentDate;
    private int _nIdCategory = ALL_INT;

    /**
     *
     * @return 1 if the forms return must be enabled
     *                    0 if the forms return must be disabled
     */
    public int getIdState(  )
    {
        return _nIdState;
    }

    /**
     * Set 1 if the forms return must be enabled
     *            0 if the forms return must be disabled
     * @param idState  1 if the forms return must be enabled
     *                                      0 if the forms return must be disabled
     */
    public void setIdState( int idState )
    {
        _nIdState = idState;
    }

    /**
     *
     * @return true if the filter contain form state
     */
    public boolean containsIdState(  )
    {
        return ( _nIdState != ALL_INT );
    }

    /**
         *
         * @return the workgroup of the search forms
         */
    public String getWorkgroup(  )
    {
        return _strWorkgroup;
    }

    /**
     * set the workgroup of the search forms
     * @param workgroup the workgroup of the search forms
     */
    public void setWorkGroup( String workgroup )
    {
        _strWorkgroup = workgroup;
    }

    /**
     *
     * @return true if the filter contaion workgroup criteria
     */
    public boolean containsWorkgroupCriteria(  )
    {
        return ( !_strWorkgroup.equals( ALL_STRING ) );
    }

    /**
     * set true if the date end availability must be before current date
     * @param bCurrentDate true if the date end availability must be before
     *            current date
     */
    public void setDateEndDisponibilityBeforeCurrentDate( boolean bCurrentDate )
    {
        _bDateEndDisponibilityBeforeCurrentDate = bCurrentDate;
    }

    /**
     * 
     * @return true if the date end availability must be before current date
     */
    public boolean containsDateEndDisponibilityBeforeCurrentDate(  )
    {
        return _bDateEndDisponibilityBeforeCurrentDate;
    }

    /**
     * Set true if the date begin availability must be after current date
     * @param bDateBeginDisponibilityAfterCurrentDate true if the date begin
     *            availability must be after current date, false otherwise
     */
    public void setDateBeginDisponibilityAfterCurrentDate( boolean bDateBeginDisponibilityAfterCurrentDate )
    {
        this._bDateBeginDisponibilityAfterCurrentDate = bDateBeginDisponibilityAfterCurrentDate;
    }

    /**
     * 
     * @return true if the date begin availability must be after current date
     */
    public boolean isDateBeginDisponibilityAfterCurrentDate(  )
    {
        return _bDateBeginDisponibilityAfterCurrentDate;
    }

    /**
    *
    * @return true if the date begin disponibility must be after current date
    */
    public boolean containsDateBeginDisponibilityAfterCurrentDate(  )
    {
        return _bDateBeginDisponibilityAfterCurrentDate;
    }

    /**
    *
    * @return 1 if the forms return must be in auto publication enabled
    *                    0 if the forms return must be in auto publication disabled
    */
    public int getIdAutoPublicationState(  )
    {
        return _nIdAutoPublicationState;
    }

    /**
     * Set 1 if the forms return must be in auto publication enabled
     *            0 if the forms return must be in auto publication disabled
     * @param idAutoPublicationState  1 if the forms return must be in auto publication enabled
     *                                      0 if the forms return must be in auto publication disabled
     */
    public void setIdAutoPublicationState( int idAutoPublicationState )
    {
        _nIdAutoPublicationState = idAutoPublicationState;
    }

    /**
     *
     * @return true if the filter contain form auto publication state
     */
    public boolean containsIdAutoPublication(  )
    {
        return ( _nIdAutoPublicationState != ALL_INT );
    }

    /**
     *
     * @return  the id of the category insert in the filter
     */
    public int getIdCategory(  )
    {
        return _nIdCategory;
    }

    /**
     * set  the id of the category in the filter
     * @param idCategory the id of the category to insert in the filter
     */
    public void setIdCategory( int idCategory )
    {
        _nIdCategory = idCategory;
    }

    /**
     *
     * @return true if the filter contain an id of category
     */
    public boolean containsIdCategory(  )
    {
        return ( _nIdCategory != ALL_INT );
    }
}
