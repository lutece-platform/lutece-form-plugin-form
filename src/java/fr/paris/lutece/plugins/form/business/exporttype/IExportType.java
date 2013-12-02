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
package fr.paris.lutece.plugins.form.business.exporttype;

import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormSubmit;
import fr.paris.lutece.plugins.genericattributes.business.ResponseFilter;

import java.util.List;
import java.util.Locale;


/**
 *
 * This class represents the export responses type.
 * The method {@link #getResponseFilter(Form, Locale)} enable
 * the user to get the response filter to get the desired
 * list of responses.
 *
 */
public interface IExportType
{
    /**
     * Set the key
     * @param strKey the key
     */
    void setKey( String strKey );

    /**
     * Get the key
     * @return the key
     */
    String getKey(  );

    /**
     * Set the title i18n key
     * @param strTitleI18nKey the i18n key
     */
    void setTitleI18nKey( String strTitleI18nKey );

    /**
     * Get the title
     * @param locale the locale
     * @return the title
     */
    String getTitle( Locale locale );

    /**
     * Get the response filter
     * @param form
     * @param locale the locale
     * @return The response filter
     */
    ResponseFilter getResponseFilter( Form form, Locale locale );

    /**
     * Save the export
     * @param listFormSubmits the list of form submits
     * @param locale the locale
     */
    void saveExport( List<FormSubmit> listFormSubmits, Locale locale );
}
