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
package fr.paris.lutece.plugins.form.service.draft;

import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormSubmit;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.util.AppLogService;

import javax.servlet.http.HttpServletRequest;

/**
 * Static wrapper for {@link DraftBackupService}. <br>
 * Modules implementing {@link DraftBackupService} have to set the service using {@link #setDraftBackupService(DraftBackupService)}. Make sure there is only one
 * implementation at runtime, multiple implementation will lead to non predictable behavior. <br>
 * Does nothing if no implementation is set.
 */
public final class FormDraftBackupService
{
    private static DraftBackupService _draftBackupService;

    /**
     * Empty constructor
     */
    private FormDraftBackupService( )
    {
        // nothing
    }

    /**
     * Sets the {@link DraftBackupService}
     * 
     * @param draftBackupService
     *            the backup service
     */
    public static synchronized void setDraftBackupService( DraftBackupService draftBackupService )
    {
        _draftBackupService = draftBackupService;
        AppLogService.debug( "Using " + draftBackupService.getClass( ) + " as draftBackupService" );
    }

    /**
     * Save the draft. Useful when the user save his form but does not submit it.
     * 
     * @param request
     *            The HTTP request
     * @param form
     *            The Form
     * @throws SiteMessageException
     *             if an error occurs
     */
    public static void saveDraft( HttpServletRequest request, Form form ) throws SiteMessageException
    {
        if ( _draftBackupService != null )
        {
            _draftBackupService.saveDraft( request, form );
        }
    }

    /**
     * Saves the formSubmit as draft. Useful after the user submitted his form.
     * 
     * @param request
     *            the request
     * @param formSubmit
     *            formSubmit
     * @throws SiteMessageException
     *             if an error occurs
     */
    public static void saveDraft( HttpServletRequest request, FormSubmit formSubmit ) throws SiteMessageException
    {
        if ( _draftBackupService != null )
        {
            _draftBackupService.saveDraft( request, formSubmit );
        }
    }

    /**
     * Validate a draft.
     * 
     * @param request
     *            The HTTP request
     * @param form
     *            The form
     * @throws SiteMessageException
     *             if an error occurs
     */
    public static void validateDraft( HttpServletRequest request, Form form ) throws SiteMessageException
    {
        if ( _draftBackupService != null )
        {
            _draftBackupService.validateDraft( request, form );
        }
    }

    /**
     * Pre Process Request. Always returns <code>false</code> if no implementation.
     * 
     * @param request
     *            The HTTP request
     * @param form
     *            The form
     * @return true if the request is processed, false if the process should be continued
     * @throws SiteMessageException
     *             if an error occurs
     */
    public static boolean preProcessRequest( HttpServletRequest request, Form form ) throws SiteMessageException
    {
        if ( _draftBackupService != null )
        {
            return _draftBackupService.preProcessRequest( request, form );
        }

        return false;
    }

    /**
     * Returns <code>true</code> if draft is supported, <code>false</code> otherwise.
     * 
     * @return <code>true</code> if draft is supported, <code>false</code> otherwise.
     */
    public static boolean isDraftSupported( )
    {
        return _draftBackupService != null;
    }
}
