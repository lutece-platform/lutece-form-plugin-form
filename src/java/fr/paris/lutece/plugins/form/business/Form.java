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
package fr.paris.lutece.plugins.form.business;

import fr.paris.lutece.portal.service.rbac.RBACResource;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionRemovalListenerService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupResource;
import fr.paris.lutece.portal.service.workgroup.WorkgroupRemovalListenerService;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;


/**
 * Class form
 */
public class Form implements AdminWorkgroupResource, RBACResource
{
    /**
     * Form resource type
     */
    public static final String RESOURCE_TYPE = "FORM_FORM_TYPE";
    /**
     * State of forms that are enabled
     */
    public static final int STATE_ENABLE = 1;
    /**
     * State of forms that are disabled
     */
    public static final int STATE_DISABLE = 0;
    private static FormWorkgroupRemovalListener _listenerWorkgroup = new FormWorkgroupRemovalListener( );
    private static FormRegularExpressionRemovalListener _listenerRegularExpression = new FormRegularExpressionRemovalListener( );
    private int _nIdForm;
    private String _strTitle;
    private String _strFrontOfficeTitle;
    private boolean _bIsShownFrontOfficeTitle;
    private String _strDescription;
    private String _strWelcomeMessage;
    private String _strUnavailabilityMessage;
    private String _strRequirement;
    private String _strWorkgroup;
    private int _nIdMailingList;
    private boolean _bActiveCaptcha;
    private boolean _bActiveStoreAdresse;
    private boolean _bLimitNumberResponse;
    private boolean _bActiveRequirement;
    private boolean _bSupportHTTPS;
    private String _strLibelleValidateButton;
    private String _strLibelleResetButton;
    private Date _tDateBeginDisponibility;
    private Date _tDateEndDisponibility;
    private Timestamp _tDateCreation;
    private boolean _nActive;
    private boolean _bAutoPublicationActive;
    private Recap _recap;
    private List<FormAction> _listActions;
    private int _nFormPageId;
    private String _strInfoComplementary1;
    private String _strInfoComplementary2;
    private String _strInfoComplementary3;
    private String _strInfoComplementary4;
    private String _strInfoComplementary5;
    private String _strCodeTheme;
    private boolean _bActiveMyLuteceAuthentification;
    private Category _category;
    private boolean _bAutomaticCleaning;
    private boolean _bCleaningByRemoval;
    private int _nNbDaysBeforeCleaning;

    /**
     * Initialize the Form
     */
    public static void init( )
    {
        // Create removal listeners and register them
        WorkgroupRemovalListenerService.getService( ).registerListener( _listenerWorkgroup );
        RegularExpressionRemovalListenerService.getService( ).registerListener( _listenerRegularExpression );
    }

    /**
     * Get the id of the mailing list associate to the form
     * @return the id of the mailing list associate to the form
     */
    public int getIdMailingList( )
    {
        return _nIdMailingList;
    }

    /**
     * set the id of the mailing list associate to the form
     * @param mailingListId the id of the mailing list associate to the form
     */
    public void setIdMailingList( int mailingListId )
    {
        _nIdMailingList = mailingListId;
    }

    /**
     * Get the code theme
     * @return the theme code
     */
    public String getCodeTheme( )
    {
        return _strCodeTheme;
    }

    /**
     * set the theme code
     * @param strCodeTheme the theme code
     */
    public void setCodeTheme( String strCodeTheme )
    {
        _strCodeTheme = strCodeTheme;
    }

    /**
     * Check if the form contain a captcha
     * @return true if the form contain a captcha
     */
    public boolean isActiveCaptcha( )
    {
        return _bActiveCaptcha;
    }

    /**
     * set true if the form contain a captcha
     * @param activeCaptcha true if the form contain a captcha
     */
    public void setActiveCaptcha( boolean activeCaptcha )
    {
        _bActiveCaptcha = activeCaptcha;
    }

    /**
     * true if the ip adresse of the user must be store
     * @return true if the ip adresse of the user must be store
     */
    public boolean isActiveStoreAdresse( )
    {
        return _bActiveStoreAdresse;
    }

    /**
     * set true if the ip adresse of the user must be store
     * @param activeStoreAdrese true if the ip adresse of the user must be store
     */
    public void setActiveStoreAdresse( boolean activeStoreAdrese )
    {
        _bActiveStoreAdresse = activeStoreAdrese;
    }

    /**
     * Check if the requirement must be activate
     * @return true if the requirement must be activate
     */
    public boolean isActiveRequirement( )
    {
        return _bActiveRequirement;
    }

    /**
     * set true if the requirement must be activate
     * @param activeRequirement true if the form contain requirement
     */
    public void setActiveRequirement( boolean activeRequirement )
    {
        _bActiveRequirement = activeRequirement;
    }

    /**
     * Get the label of the validate button
     * @return the label of the validate button
     */
    public String getLibelleValidateButton( )
    {
        return _strLibelleValidateButton;
    }

    /**
     * set the label of the validate button
     * @param libelleValidateButton the label of the validate button
     */
    public void setLibelleValidateButton( String libelleValidateButton )
    {
        _strLibelleValidateButton = libelleValidateButton;
    }

    /**
     * Get the date of end of disponibility
     * @return the date of end disponibility
     */
    public Date getDateEndDisponibility( )
    {
        return _tDateEndDisponibility;
    }

    /**
     * set the date of end of disponibility
     * @param dateEndDisponibility the date of end of disponibility
     */
    public void setDateEndDisponibility( Date dateEndDisponibility )
    {
        _tDateEndDisponibility = dateEndDisponibility;
    }

    /**
     * Get the requirement of the form
     * @return the requirement of the form
     */
    public String getRequirement( )
    {
        return _strRequirement;
    }

    /**
     * set the requirement of the form
     * @param requirement the requirement of the form
     */
    public void setRequirement( String requirement )
    {
        _strRequirement = requirement;
    }

    /**
     * Get the title of the form
     * @return the title of the form
     */
    public String getTitle( )
    {
        return _strTitle;
    }

    /**
     * set the title of the form
     * @param strTitle the title of the form
     */
    public void setTitle( String strTitle )
    {
        this._strTitle = strTitle;
    }

    /**
     * gets the front office's title
     * @return the title of the form in the front office
     */
    public String getFrontOfficeTitle( )
    {
        return _strFrontOfficeTitle;
    }

    /**
     * Set the front office's title
     * @param strFrontOfficeTitle the title to set
     */
    public void setFrontOfficeTitle( String strFrontOfficeTitle )
    {
        this._strFrontOfficeTitle = strFrontOfficeTitle;
    }

    /**
     * get the value of the boolean isShownFrontOfficeTitle
     * @return bIsShownFrontOfficeTitle
     */
    public boolean isShownFrontOfficeTitle( )
    {
        return _bIsShownFrontOfficeTitle;
    }

    /**
     * set the value of the boolean isShownFrontOfficeTitle
     * @param bIsShownFrontOfficeTitle The value of the boolean
     *            isShownFrontOfficeTitle
     */
    public void setIsShownFrontOfficeTitle( boolean bIsShownFrontOfficeTitle )
    {
        this._bIsShownFrontOfficeTitle = bIsShownFrontOfficeTitle;
    }

    /**
     * Get the description of the form
     * @return the description of the form
     */
    public String getDescription( )
    {
        return _strDescription;
    }

    /**
     * set the description of the form
     * @param description the description of the form
     */
    public void setDescription( String description )
    {
        this._strDescription = description;
    }

    /**
     * Get the welcome message of the form
     * @return the welcome message of the form
     */
    public String getWelcomeMessage( )
    {
        return _strWelcomeMessage;
    }

    /**
     * set the welcome message of the form
     * @param strWelcomeMessage the welcome message of the form
     */
    public void setWelcomeMessage( String strWelcomeMessage )
    {
        this._strWelcomeMessage = strWelcomeMessage;
    }

    /**
     * Get the unavailability message of the form
     * @return the unavailability message of the form
     */
    public String getUnavailabilityMessage( )
    {
        return _strUnavailabilityMessage;
    }

    /**
     * set the unavailability message of the form
     * @param unavailabilityMessage the unavailability message of the form
     */
    public void setUnavailabilityMessage( String unavailabilityMessage )
    {
        _strUnavailabilityMessage = unavailabilityMessage;
    }

    /**
     * Get the work group associate to the form
     * @return the work group associate to the form
     */
    public String getWorkgroup( )
    {
        return _strWorkgroup;
    }

    /**
     * set the work group associate to the form
     * @param workGroup the work group associate to the form
     */
    public void setWorkgroup( String workGroup )
    {
        _strWorkgroup = workGroup;
    }

    /**
     * Get the id of the form
     * @return the id of the form
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * set the id of the form
     * @param idForm the id of the form
     */
    public void setIdForm( int idForm )
    {
        _nIdForm = idForm;
    }

    /**
     * Check if the form is active or not
     * @return true if the form is active
     */
    public boolean isActive( )
    {
        return _nActive;
    }

    /**
     * set true if the form is active
     * @param active true if the form is active
     */
    public void setActive( boolean active )
    {
        _nActive = active;
    }

    /**
     * Check the user can only submit one form, or if he can submit several
     * @return true if the user can submit just one form
     */
    public boolean isLimitNumberResponse( )
    {
        return _bLimitNumberResponse;
    }

    /**
     * set true if the user can submit just one form
     * @param numberResponse true if the user can submit just one form
     */
    public void setLimitNumberResponse( boolean numberResponse )
    {
        _bLimitNumberResponse = numberResponse;
    }

    /**
     * Get the creation date
     * @return the creation date
     */
    public Timestamp getDateCreation( )
    {
        return _tDateCreation;
    }

    /**
     * set the creation date
     * @param dateCreation the creation date
     */
    public void setDateCreation( Timestamp dateCreation )
    {
        _tDateCreation = dateCreation;
    }

    /**
     * Get the recap associated to the form
     * @return the recap associated to the form
     */
    public Recap getRecap( )
    {
        return _recap;
    }

    /**
     * set the recap associated to the form
     * @param recap the recap associated to the form
     */
    public void setRecap( Recap recap )
    {
        this._recap = recap;
    }

    /**
     * RBAC resource implementation
     * @return The resource type code
     */
    public String getResourceTypeCode( )
    {
        return RESOURCE_TYPE;
    }

    /**
     * RBAC resource implementation
     * @return The resourceId
     */
    public String getResourceId( )
    {
        return StringUtils.EMPTY + _nIdForm;
    }

    /**
     * 
     * @return a list of action can be use for the form
     */
    public List<FormAction> getActions( )
    {
        return _listActions;
    }

    /**
     * set a list of action can be use for the form
     * @param formActions a list of action must be use for the form
     */
    public void setActions( List<FormAction> formActions )
    {
        _listActions = formActions;
    }

    /**
     * Get the recap associate to the form
     * @return the id of the page which contain the form
     */
    public int getFormPageId( )
    {
        return _nFormPageId;
    }

    /**
     * set the id of the page which contain the form
     * @param formPageId the id of the page which contain the form
     */
    public void setFormPageId( int formPageId )
    {
        _nFormPageId = formPageId;
    }

    /**
     * Define the date begin of the publication
     * @param tDateBeginDisponibility The date begin of the publication
     */
    public void setDateBeginDisponibility( Date tDateBeginDisponibility )
    {
        this._tDateBeginDisponibility = tDateBeginDisponibility;
    }

    /**
     * Return the date begin of the publication
     * @return The date begin of the publication
     */
    public Date getDateBeginDisponibility( )
    {
        return _tDateBeginDisponibility;
    }

    /**
     * Set if Auto publication is effectively active
     * @param bAutoPublicationActive True if Auto publication is effectively
     *            active
     */
    public void setAutoPublicationActive( boolean bAutoPublicationActive )
    {
        this._bAutoPublicationActive = bAutoPublicationActive;
    }

    /**
     * Return true if auto publication is effectively active
     * @return true of false
     */
    public boolean isAutoPublicationActive( )
    {
        return _bAutoPublicationActive;
    }

    /**
     * Return true if the form is in auto publication mode, false else
     * @return true if the form is auto published
     */
    public boolean isAutoPublished( )
    {
        return ( getDateBeginDisponibility( ) != null ) || ( getDateEndDisponibility( ) != null );
    }

    /**
     * The label to display for the Reset button
     * @param strLibelleResetButton The label
     */
    public void setLibelleResetButton( String strLibelleResetButton )
    {
        this._strLibelleResetButton = strLibelleResetButton;
    }

    /**
     * The label to display for the Reset button
     * @return the Reset button name
     */
    public String getLibelleResetButton( )
    {
        return _strLibelleResetButton;
    }

    /**
     * Get the Information Complementary 1
     * @return the Information Complementary 1
     */
    public String getInfoComplementary1( )
    {
        return _strInfoComplementary1;
    }

    /**
     * set the Information Complementary 1
     * @param strInfoComplementary1 the Information Complementary 1
     */
    public void setInfoComplementary1( String strInfoComplementary1 )
    {
        _strInfoComplementary1 = strInfoComplementary1;
    }

    /**
     * Get the Information Complementary 2
     * @return the Information Complementary 2
     */
    public String getInfoComplementary2( )
    {
        return _strInfoComplementary2;
    }

    /**
     * set the Information Complementary 2
     * @param strInfoComplementary2 the Information Complementary 2
     */
    public void setInfoComplementary2( String strInfoComplementary2 )
    {
        _strInfoComplementary2 = strInfoComplementary2;
    }

    /**
     * Get the Information Complementary 3
     * @return the Information Complementary 3
     */
    public String getInfoComplementary3( )
    {
        return _strInfoComplementary3;
    }

    /**
     * set the Information Complementary 3
     * @param strInfoComplementary3 the Information Complementary 3
     */
    public void setInfoComplementary3( String strInfoComplementary3 )
    {
        _strInfoComplementary3 = strInfoComplementary3;
    }

    /**
     * Get the Information Complementary 4
     * @return the Information Complementary 4
     */
    public String getInfoComplementary4( )
    {
        return _strInfoComplementary4;
    }

    /**
     * set the Information Complementary 4
     * @param strInfoComplementary4 the Information Complementary 4
     */
    public void setInfoComplementary4( String strInfoComplementary4 )
    {
        _strInfoComplementary4 = strInfoComplementary4;
    }

    /**
     * Get the Information Complementary 5
     * @return the Information Complementary 5
     */
    public String getInfoComplementary5( )
    {
        return _strInfoComplementary5;
    }

    /**
     * set the Information Complementary 5
     * @param strInfoComplementary5 the Information Complementary 5
     */
    public void setInfoComplementary5( String strInfoComplementary5 )
    {
        _strInfoComplementary5 = strInfoComplementary5;
    }

    /**
     * Set to <b>true</b> if the form support HTTPS, <b>false</b> otherwise
     * @param bSupportHTTPS the support value
     */
    public void setSupportHTTPS( boolean bSupportHTTPS )
    {
        this._bSupportHTTPS = bSupportHTTPS;
    }

    /**
     * Returns <b>true</b> if the form support HTTPS, <b>false</b> otherwise
     * @return <b>true</b> if the form support HTTPS, <b>false</b> otherwise
     */
    public boolean isSupportHTTPS( )
    {
        return _bSupportHTTPS;
    }

    /**
     * Check if the form require mylutece authentication
     * @return true if the form require mylutece authentication
     */
    public boolean isActiveMyLuteceAuthentification( )
    {
        return _bActiveMyLuteceAuthentification;
    }

    /**
     * set true if the form require mylutece authentication
     * @param bActiveMyLuteceAuthentification true if the form require mylutece
     *            authentication
     */
    public void setActiveMyLuteceAuthentification( boolean bActiveMyLuteceAuthentification )
    {
        _bActiveMyLuteceAuthentification = bActiveMyLuteceAuthentification;
    }

    /**
     * Get the category associate to the form
     * @return the category associate to the form
     */
    public Category getCategory( )
    {
        return _category;
    }

    /**
     * set the category associate to the form
     * @param category the category associate to the form
     */
    public void setCategory( Category category )
    {
        _category = category;
    }

    /**
     * Check if this form should be cleaned automatically by the daemon
     * @return True if this form should be cleaned automatically by the daemon,
     *         false otherwise
     */
    public boolean getAutomaticCleaning( )
    {
        return _bAutomaticCleaning;
    }

    /**
     * Set if this form should be cleaned automatically by the daemon
     * @param bAutomaticCleaning True if this form should be cleaned
     *            automatically by the daemon, false otherwise
     */
    public void setAutomaticCleaning( boolean bAutomaticCleaning )
    {
        this._bAutomaticCleaning = bAutomaticCleaning;
    }

    /**
     * Check if the cleaning operation is a remove operation or an anonymization
     * operation
     * @return true if the cleaning operation is a remove operation, false if it
     *         is an anonymization operation
     */
    public boolean getCleaningByRemoval( )
    {
        return _bCleaningByRemoval;
    }

    /**
     * Set the cleaning operation a a remove operation or as an anonymization
     * operation
     * @param bCleaningByRemoval true if the cleaning operation is a remove
     *            operation, false if it is an anonymization operation
     */
    public void setCleaningByRemoval( boolean bCleaningByRemoval )
    {
        this._bCleaningByRemoval = bCleaningByRemoval;
    }

    /**
     * Get the number of days to keep responses before cleaning them
     * @return The number of days to keep responses before cleaning them
     */
    public int getNbDaysBeforeCleaning( )
    {
        return _nNbDaysBeforeCleaning;
    }

    /**
     * Set the number of days to keep responses before cleaning them
     * @param nNbDaysBeforeCleaning The number of days to keep responses before
     *            cleaning them
     */
    public void setNbDaysBeforeCleaning( int nNbDaysBeforeCleaning )
    {
        this._nNbDaysBeforeCleaning = nNbDaysBeforeCleaning;
    }
}
