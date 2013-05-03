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

import fr.paris.lutece.plugins.form.service.FormPlugin;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.url.UrlItem;


/**
 *
 * class FormError
 *
 */
public class FormError
{
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PREFIX_FORM = "form";
    private String _strTitleQuestion;
    private String _strErrorMessage;
    private boolean _bMandatoryError;
    private String _strUrl;

    /**
     * return true if the error is a mandatory error
     * @return true if the error is a mandatory error
     */
    public boolean isMandatoryError(  )
    {
        return _bMandatoryError;
    }

    /**
     * set true if the error is a mandatory error
     * @param mandatoryError true if the error is a mandatory error
     */
    public void setMandatoryError( boolean mandatoryError )
    {
        _bMandatoryError = mandatoryError;
    }

    /**
     * Gets the error Message
     * @return the error Message
     */
    public String getErrorMessage(  )
    {
        return _strErrorMessage;
    }

    /**
     * set the error message
     * @param errorMessage the erroer message
     */
    public void setErrorMessage( String errorMessage )
    {
        _strErrorMessage = errorMessage;
    }

    /**
     *
     * @return the title of the mandatory question
     */
    public String getTitleQuestion(  )
    {
        return _strTitleQuestion;
    }

    /**
     * set the title of the mandatory question
     * @param titleMandatoryQuestion the title of the mandatory question
     */
    public void setTitleQuestion( String titleMandatoryQuestion )
    {
        _strTitleQuestion = titleMandatoryQuestion;
    }

    /**
     * @param strUrl the _strUrl to set
     */
    public void setUrl( String strUrl )
    {
        this._strUrl = strUrl;
    }

    /**
     * Set the url
     * @param entry the entry
     */
    public void setUrl( IEntry entry )
    {
        UrlItem url = new UrlItem( AppPathService.getPortalUrl(  ) );
        url.addParameter( XPageAppService.PARAM_XPAGE_APP, FormPlugin.PLUGIN_NAME );

        if ( ( entry != null ) && ( entry.getForm(  ) != null ) )
        {
            url.addParameter( PARAMETER_ID_FORM, entry.getForm(  ).getIdForm(  ) );
            url.setAnchor( PREFIX_FORM + entry.getIdEntry(  ) );
        }

        _strUrl = url.getUrl(  );
    }

    /**
     * @return the _strUrl
     */
    public String getUrl(  )
    {
        return _strUrl;
    }
}
