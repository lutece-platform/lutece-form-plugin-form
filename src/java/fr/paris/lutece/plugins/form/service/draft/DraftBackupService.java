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
package fr.paris.lutece.plugins.form.service.draft;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormSubmit;
import fr.paris.lutece.portal.service.message.SiteMessageException;

/**
 * 
 * DraftBackupService for {@link Form}
 *
 */
public interface DraftBackupService 
{
    /**
     * Save the draft. Useful when the user save his form but does not submit it.
     * @param request The HTTP request
     * @param form The Form
     */
    void saveDraft( HttpServletRequest request, Form form ) throws SiteMessageException;

    /**
     * Validate a draft. Usefull when the user submitted his form.
     * @param request The HTTP request
     * @param form The form
     */
    void validateDraft( HttpServletRequest request, Form form ) throws SiteMessageException;
    
    /**
     * Pre Process Request
     * @param request The HTTP request
     * @param form The form
     * @return true if the request is processed, false if the process
     * should be continued
     * @throws SiteMessageException if an error occurs
     */
    boolean preProcessRequest( HttpServletRequest request, Form form ) throws SiteMessageException;
    
    /**
     * Saves the draft for the formSubmit
     * @param request the request
     * @param formSubmit the formsubmit
     * @throws SiteMessageException if an error occurs
     */
    void saveDraft( HttpServletRequest request, FormSubmit formSubmit ) throws SiteMessageException;
}
