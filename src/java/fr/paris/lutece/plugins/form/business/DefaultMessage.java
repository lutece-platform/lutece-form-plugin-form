/*
 * Copyright (c) 2002-2009, Mairie de Paris
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
 *class DefaultMessage
 *
 */
public class DefaultMessage implements RBACResource
{
    public static final String RESOURCE_TYPE = "FORM_DEFAULT_MESSAGE_TYPE";
    private String _strWelcomeMessage;
    private String _strUnavailabilityMessage;
    private String _strRequirement;
    private String _strRecapMessage;
    private String _strLibelleValidateButton;
    private String _strLibelleResetButton;
    private String _strBackUrl;

    /**
     *
     * @return default back Url
     */
    public String getBackUrl(  )
    {
        return _strBackUrl;
    }

    /**
     * set default back url
     * @param backUrl backurl
     */
    public void setBackUrl( String backUrl )
    {
        _strBackUrl = backUrl;
    }

    /**
     *
     * @return the default value of validate button
     */
    public String getLibelleValidateButton(  )
    {
        return _strLibelleValidateButton;
    }

    /**
     * set the default value of validate button
     * @param libelleValidateButton value of validate button
     */
    public void setLibelleValidateButton( String libelleValidateButton )
    {
        _strLibelleValidateButton = libelleValidateButton;
    }

    /**
     *
     * @return the default summary who see by the user after validate form
     */
    public String getRecapMessage(  )
    {
        return _strRecapMessage;
    }

    /**
     *
     * @param recapMessage the default summary who see by the user after validate form
     */
    public void setRecapMessage( String recapMessage )
    {
        _strRecapMessage = recapMessage;
    }

    /**
     *
     * @return the default requierement
     */
    public String getRequirement(  )
    {
        return _strRequirement;
    }

    /**
     * set  the default requierement
     * @param requierementMessage  the default requierement
     */
    public void setRequirement( String requierementMessage )
    {
        _strRequirement = requierementMessage;
    }

    /**
     *
     * @return the default Unavailability Message who see by the user when the form will be enable
     */
    public String getUnavailabilityMessage(  )
    {
        return _strUnavailabilityMessage;
    }

    /**
     * set the Unavailability Message who see by the user when the form will be enable
     * @param unavailabilityMessage the default Unavailability Message
     */
    public void setUnavailabilityMessage( String unavailabilityMessage )
    {
        _strUnavailabilityMessage = unavailabilityMessage;
    }

    /**
     *
     * @return the default welcome message
     */
    public String getWelcomeMessage(  )
    {
        return _strWelcomeMessage;
    }

    /**
     * set the default welcome message
     * @param welcomeMessage the default welcome message
     */
    public void setWelcomeMessage( String welcomeMessage )
    {
        _strWelcomeMessage = welcomeMessage;
    }

    /**
     * RBAC resource implmentation
     * @return The resource type code
     */
    public String getResourceTypeCode(  )
    {
        return RESOURCE_TYPE;
    }

    /**
     * RBAC resource implmentation
     * @return The resourceId
     */
    public String getResourceId(  )
    {
        return "";
    }

    /**
     * The default label to display for the Reset button
     * @param _strLibelleResetButton The label
     */
    public void setLibelleResetButton( String _strLibelleResetButton )
    {
        this._strLibelleResetButton = _strLibelleResetButton;
    }

    /**
     * The default label to display for the Reset button
     * @return the Reset button name
     */
    public String getLibelleResetButton(  )
    {
        return _strLibelleResetButton;
    }
}
