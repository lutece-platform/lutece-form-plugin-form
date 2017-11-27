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
package fr.paris.lutece.plugins.form.utils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormSubmit;
import fr.paris.lutece.plugins.form.business.iteration.IterationGroup;
import fr.paris.lutece.plugins.form.service.FormPlugin;
import fr.paris.lutece.plugins.form.service.entrytype.EntryTypeArray;
import fr.paris.lutece.plugins.form.service.entrytype.EntryTypeGroup;
import fr.paris.lutece.plugins.form.web.http.GroupHttpServletRequestWrapper;
import fr.paris.lutece.plugins.form.web.http.GroupMultipartHttpServletRequestWrapper;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.utils.MVCMessage;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

public class EntryTypeGroupUtils
{
    // Pattern
    private static final String PATTERN_PREFIX_ITERATION = "nIt%s";
    private static final String PATTERN_URL_ATTRIBUTE_NAME = "^\\b(%s)([0-9]+)$";

    // Marks
    private static final String MARK_ITERATION_CODE = "iteration_code";
    private static final String MARK_REMOVE_ITERATION_NUMBER = "remove_iteration_number";
    private static final String MARK_ITERATION_CHILDREN = "iteration_children";
    private static final String MARK_INFO_ITERABLE_GROUP = "infos_iterable_group";

    // Template
    private static final String TEMPLATE_GROUP_ITERATION_CHILDREN = "skin/plugins/form/entries/html_code_entry_type_group_children.html";

    // Messages
    private static final String MESSAGE_INFO_CANT_ADD_ITERATION = "form.message.cantAddIteration";
    private static final String MESSAGE_INFO_LIMIT_IETRATION_REACHED = "form.message.limitIterationReached";

    // Constants
    private static final int MATCHER_URL_ATTRIBUTE_NUMBER_POSITION = 2;

    /**
     * Construct the Html for an Group entry
     * 
     * @param request
     *            The HttpServletRequest
     * @param entry
     *            The entry
     * @param model
     *            The model to populate
     * @param bDisplayFront
     *            The boolean which tell if it must be displayed in front or not
     */
    public static void getHtmlGroupEntry( HttpServletRequest request, Entry entry, Map<String, Object> model, boolean bDisplayFront )
    {
        // Generate the Html associate at the children of the entry
        StringBuilder sbGroup = generateHtmlEntryGroup( request, entry, bDisplayFront );
        model.put( FormConstants.MARK_STR_LIST_CHILDREN, sbGroup.toString( ) );

        // Manage the case of an adding or a removing of an iteration children group of an entry type group
        int nIdEntryToRemoveIteration = getRemoveIterationParameter( request ).getKey( );
        int nIdEntryAddIteration = NumberUtils.toInt( request.getParameter( FormConstants.PARAMETER_ADD_ITERATION ), NumberUtils.INTEGER_MINUS_ONE );
        if ( nIdEntryAddIteration == entry.getIdEntry( ) || nIdEntryToRemoveIteration == entry.getIdEntry( )
                || request.getAttribute( FormConstants.ATTRIBUTE_RETURN_FROM_ERRORS ) != null )
        {
            // Populate the model with group informations
            populateModelIterableGroupInfo( request, model, entry );
        }
    }

    /**
     * Generate the html code of the current entry of type group. Manage the case where the entry allow iterations and the case where not allow.
     * 
     * @param request
     *            The HttpServletRequest
     * @param entry
     *            The entry type group
     * @param bDisplayFront
     *            True if the entry will be displayed in Front Office, false if it will be displayed in Back Office
     * @param bErrorPresent
     *            True if an error is present false otherwise
     * @return the StringBuffer containing the html code of the current entry of type group
     */
    public static StringBuilder generateHtmlEntryGroup( HttpServletRequest request, Entry entry, boolean bDisplayFront )
    {
        StringBuilder sbGroup = new StringBuilder( );

        // Case where the group allow multiple iterations
        int nbIterationMax = getEntryMaxIterationAllowed( entry.getIdEntry( ) );
        if ( nbIterationMax != NumberUtils.INTEGER_MINUS_ONE )
        {
            sbGroup = getHtmlIteratedEntryGroup( request, entry, bDisplayFront );

            // Remove an iteration for the group on the IterationGroup map in the session if it is necessary
            if ( request.getParameter( FormConstants.PARAMETER_REMOVE_ITERATION ) != null
                    && getRemoveIterationParameter( request ).getKey( ) == entry.getIdEntry( ) )
            {
                removeIterationFromMapIterationGroup( request );
            }
        }
        // Case where the entry has no iteration possible
        else
        {
            sbGroup = getHtmlEntryGroup( request, entry, bDisplayFront, sbGroup, NumberUtils.INTEGER_ZERO );
        }

        return sbGroup;
    }

    /**
     * Return the maximum number of iterations allowed for the entry. Return -1 if none iterations are allowed.
     * 
     * @param idEntry
     *            The id of the entry to find the maximum number of iterations
     * @return the maximum number of iterations allowed for the entry return -1 if none iterations are allowed
     */
    public static int getEntryMaxIterationAllowed( int idEntry )
    {
        Entry entry = EntryHome.findByPrimaryKey( idEntry );

        if ( entry != null )
        {
            Field fieldNbIteration = GenericAttributesUtils.findFieldByTitleInTheList( EntryTypeGroup.CONSTANT_NB_ITERATION, entry.getFields( ) );
            if ( fieldNbIteration != null )
            {
                return NumberUtils.toInt( fieldNbIteration.getValue( ), NumberUtils.INTEGER_MINUS_ONE );
            }

            // If the field doesn't exist it means that the group doesn't allow iteration
            return NumberUtils.INTEGER_MINUS_ONE;
        }

        return NumberUtils.INTEGER_MINUS_ONE;
    }

    /**
     * Returns the StringBuffer containing the html code associated for the current entry which allow multiple iteration
     * 
     * @param request
     *            The HttpServletRequest
     * @param entry
     *            The entry type group
     * @param bDisplayFront
     *            True if the entry will be displayed in Front Office, false if it will be displayed in Back Office
     * @param nNbIterationMax
     *            The maximum number of iteration allowed for the group
     * @return the StringBuffer containing the html code associated for the current entry
     */
    public static StringBuilder getHtmlIteratedEntryGroup( HttpServletRequest request, Entry entry, boolean bDisplayFront )
    {
        StringBuilder sbGroup = new StringBuilder( );

        // Construct the list of all children for the group
        int nIdEntry = entry.getIdEntry( );
        IterationGroup iterationGroup = retrieveIterationGroup( request, nIdEntry );

        // Iterate on all existing iterations
        for ( Integer iterationNumber : iterationGroup.getSetIterationNumber( ) )
        {
            // Get the Html associate to all the children of the group for the current iteration
            HtmlTemplate templateChildrenIterationGroup = generateChildrenItreationGroupTemplate( request, entry, bDisplayFront, iterationNumber );

            // Aggregate the current iteration template with the other of the group
            if ( templateChildrenIterationGroup != null )
            {
                sbGroup.append( templateChildrenIterationGroup.getHtml( ) );
            }
        }

        // Add another iteration if we are in the case of an iteration adding and if the user has filled the field of the previous iteration
        int nIdEntryAddIteration = NumberUtils.toInt( request.getParameter( FormConstants.PARAMETER_ADD_ITERATION ), NumberUtils.INTEGER_MINUS_ONE );
        if ( request.getParameter( FormConstants.PARAMETER_ADD_ITERATION ) != null && nIdEntryAddIteration == nIdEntry )
        {
            int nLastIterationNumber = iterationGroup.getLastIterationNumber( );
            boolean bFillingMade = iterationGroup.fillingMadeOnIteration( nLastIterationNumber );
            if ( bFillingMade )
            {
                if ( !isEntryIterationLimitReached( request, nIdEntryAddIteration ) )
                {
                    HtmlTemplate htmTemplateNextIteration = generateChildrenItreationGroupTemplate( request, entry, bDisplayFront, nLastIterationNumber
                            + NumberUtils.INTEGER_ONE );
                    if ( htmTemplateNextIteration != null )
                    {
                        sbGroup.append( htmTemplateNextIteration.getHtml( ) );
                    }
                }
            }
            else
            {
                // Add an attribute into the request to notify the fact that the user has not filled any field of the last iteration
                request.setAttribute( FormConstants.ATTRIBUTE_NO_FILLED_ENTRY_GROUP, Boolean.TRUE );
            }
        }

        return sbGroup;
    }

    /**
     * 
     * 
     * @param request
     *            The HttpServletRequest
     * @param entry
     *            The entry type group
     * @param bDisplayFront
     *            True if the entry will be displayed in Front Office, false if it will be displayed in Back Office
     * @param nIterationNumber
     *            The current iteration number to generate the iteration template
     * @return the HtmlTemplate for the current entry for the specified iteration
     */
    private static HtmlTemplate generateChildrenItreationGroupTemplate( HttpServletRequest request, Entry entry, boolean bDisplayFront, int nIterationNumber )
    {
        // Get the Html associate to all the children of the group for the current iteration
        StringBuilder sbCurrentChildrenGroup = getHtmlEntryGroup( request, entry, bDisplayFront, new StringBuilder( ), nIterationNumber );
        String strCurrentIterationCode = String.format( PATTERN_PREFIX_ITERATION, nIterationNumber );

        // Generate the model associate with the current iteration
        Map<String, Object> model = new LinkedHashMap<String, Object>( );
        model.put( FormConstants.PARAMETER_ID_ENTRY, entry.getIdEntry( ) );
        model.put( MARK_REMOVE_ITERATION_NUMBER, nIterationNumber );
        model.put( MARK_ITERATION_CODE, strCurrentIterationCode );
        model.put( MARK_ITERATION_CHILDREN, sbCurrentChildrenGroup.toString( ) );

        // Populate the template with the model
        return AppTemplateService.getTemplate( TEMPLATE_GROUP_ITERATION_CHILDREN, request.getLocale( ), model );
    }

    /**
     * Generate the html code for the entry of type group. Modify the name of the id attribute if the entry allow iterations simply construct the html otherwise
     * 
     * @param request
     *            The HttpServletRequest
     * @param entry
     *            The entry type group
     * @param bDisplayFront
     *            Boolean which tell if the entry is display in front or not
     * @param sbEntryGroup
     *            The stringBuilder which contains the html code of the entry
     * @return the StringBuilder which contains the html code of the Entry
     */
    public static StringBuilder getHtmlEntryGroup( HttpServletRequest request, Entry entry, boolean bDisplayFront, StringBuilder sbEntryGroup, int nbIteration )
    {
        for ( Entry entryChild : entry.getChildren( ) )
        {
            int nIdEntryChild = entryChild.getIdEntry( );

            // Construct the Html template for the current entry
            StringBuffer stringBufferHtmEntryGroup = new StringBuffer( sbEntryGroup.toString( ) );
            FormUtils.getHtmlEntry( nIdEntryChild, stringBufferHtmEntryGroup, request.getLocale( ), bDisplayFront, request, nbIteration );

            // Convert the StringBuffer html to the StringBuilder
            sbEntryGroup = new StringBuilder( stringBufferHtmEntryGroup.toString( ) );
        }

        return sbEntryGroup;
    }

    /**
     * Get the response for the entry of type group
     * 
     * @param request
     *            The HttpServletReqest
     * @param listFormErrors
     *            The list of formerrors
     * @param entry
     *            The entry to analyze
     * @param plugin
     *            The plugin
     * @param formSubmit
     *            The formSubmit
     * @param bReturnErrors
     *            True if errors must be returned
     * @param locale
     *            The locale
     */
    public static void getResponseGroupEntry( HttpServletRequest request, List<GenericAttributeError> listFormErrors, Entry entry, Plugin plugin,
            FormSubmit formSubmit, boolean bReturnErrors, Locale locale )
    {
        if ( listFormErrors == null )
        {
            listFormErrors = new ArrayList<>( );
        }

        if ( entry != null )
        {
            int nNbMaxIteration = getEntryMaxIterationAllowed( entry.getIdEntry( ) );
            for ( Entry entryChild : entry.getChildren( ) )
            {
                // If there are a number of iteration on the current group we will check errors on the children on every iteration
                if ( nNbMaxIteration != NumberUtils.INTEGER_MINUS_ONE )
                {
                    // The set of all iteration number used in the form
                    Set<Integer> setIterationNumber = new LinkedHashSet<>( );
                    IterationGroup iterationGroup = retrieveIterationGroup( request, entry.getIdEntry( ) );
                    if ( iterationGroup != null )
                    {
                        setIterationNumber = iterationGroup.getSetIterationNumber( );
                    }

                    // Check if the entry is of type File or Image
                    boolean bIsEntryTypeFileImage = isEntryOfUploadType( request, entryChild );

                    for ( Integer nIterationNumber : setIterationNumber )
                    {
                        // Wrap the current request to allow modification during the gathering of parameter value on the parameter name
                        HttpServletRequest requestWrapper = null;
                        if ( bIsEntryTypeFileImage && request instanceof MultipartHttpServletRequest )
                        {
                            requestWrapper = new GroupMultipartHttpServletRequestWrapper( (MultipartHttpServletRequest) request, nNbMaxIteration );
                        }
                        else
                        {
                            requestWrapper = new GroupHttpServletRequestWrapper( request, nIterationNumber );
                        }

                        requestWrapper.setAttribute( FormConstants.ATTRIBUTE_ITERATION_NUMBER, nIterationNumber );

                        listFormErrors.addAll( FormUtils.getResponseEntry( requestWrapper, entryChild.getIdEntry( ), plugin, formSubmit, Boolean.FALSE,
                                bReturnErrors, locale, nIterationNumber ) );
                        requestWrapper.removeAttribute( FormConstants.ATTRIBUTE_ITERATION_NUMBER );
                    }
                }
                else
                {
                    listFormErrors.addAll( FormUtils.getResponseEntry( request, entryChild.getIdEntry( ), plugin, formSubmit, Boolean.FALSE, bReturnErrors,
                            locale ) );
                }
            }
        }
    }

    /**
     * Manage the errors of iterations of a entry type group
     * 
     * @param request
     *            the httpServletRequest
     * @param entry
     *            the entry base of the iteration
     * @param listInfosIterableGroup
     *            the list of all existing messages
     * @return true if there are errors false otherwise
     */
    public static Boolean manageIterationGroupErrors( HttpServletRequest request, Entry entry, List<MVCMessage> listInfosIterableGroup )
    {
        // Check if a filling has been made or not
        if ( request.getParameter( FormConstants.PARAMETER_ADD_ITERATION ) != null
                && request.getAttribute( FormConstants.ATTRIBUTE_NO_FILLED_ENTRY_GROUP ) != null )
        {
            request.removeAttribute( FormConstants.ATTRIBUTE_NO_FILLED_ENTRY_GROUP );

            listInfosIterableGroup.add( new MVCMessage( I18nService.getLocalizedString( MESSAGE_INFO_CANT_ADD_ITERATION, request.getLocale( ) ) ) );
            return Boolean.TRUE;
        }

        // Check if the user has reach the max limit of duplication and if is not the case of a removing
        if ( isEntryIterationLimitReached( request, entry.getIdEntry( ) ) && request.getParameter( FormConstants.PARAMETER_ADD_ITERATION ) != null
                && getRemoveIterationParameter( request ).getKey( ) != entry.getIdEntry( ) )
        {

            listInfosIterableGroup.add( new MVCMessage( I18nService.getLocalizedString( MESSAGE_INFO_LIMIT_IETRATION_REACHED, request.getLocale( ) ) ) );
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    /**
     * Return the GenericAttributeError of an entry for the current iteration
     * 
     * @param request
     *            the HttpServletRequest
     * @param idEntry
     *            the id of the entry
     * @param nCurrentIteration
     *            the number of the current iteration
     * @return the GenericAttributeError of the entry for the current iteration if exists null otherwise
     */
    public static GenericAttributeError getGenericAttributeEntryError( HttpServletRequest request, int idEntry, int nCurrentIteration )
    {
        GenericAttributeError currentIterationEntryError = null;

        @SuppressWarnings( "unchecked" )
        List<GenericAttributeError> listErrors = (List<GenericAttributeError>) request.getSession( ).getAttribute( FormConstants.SESSION_FORM_ERRORS );

        if ( listErrors != null && !listErrors.isEmpty( ) )
        {
            currentIterationEntryError = getCurrentIterationEntryErrors( listErrors, idEntry, nCurrentIteration );
        }

        return currentIterationEntryError;
    }

    /**
     * Retrieve the GenericAttributeError associate to the entry for the current iteration from the list of errors
     * 
     * @param listErrors
     *            the list of all errors
     * @param idEntry
     *            the id of the entry
     * @param nCurrentIterationNumber
     *            the current number of the iteration
     * @return the GenericAttributeError associate to the entry if exists null otherwise
     */
    private static GenericAttributeError getCurrentIterationEntryErrors( List<GenericAttributeError> listErrors, int idEntry, int nCurrentIterationNumber )
    {
        GenericAttributeError currentIterationEntryError = null;

        if ( listErrors != null && !listErrors.isEmpty( ) )
        {
            for ( GenericAttributeError genericAttributeError : listErrors )
            {
                // Get the url of the error which contain the name of the attribute on error
                String strUrlError = genericAttributeError.getUrl( );
                if ( strUrlError != null && strUrlError.split( FormConstants.ANCHOR_DELIMITER ) != null
                        && strUrlError.split( FormConstants.ANCHOR_DELIMITER ).length > 1 )
                {
                    String strIterateAttributeName = strUrlError.split( FormConstants.ANCHOR_DELIMITER ) [NumberUtils.INTEGER_ONE];
                    String strPatternPrefixIteration = String.format( PATTERN_PREFIX_ITERATION, nCurrentIterationNumber );

                    // Get the attribute name
                    if ( StringUtils.isNotBlank( strIterateAttributeName ) )
                    {
                        StringBuilder sbPatternIterableAttributeName = new StringBuilder( );
                        sbPatternIterableAttributeName.append( strPatternPrefixIteration );
                        sbPatternIterableAttributeName.append( FormUtils.CONSTANT_UNDERSCORE );
                        sbPatternIterableAttributeName.append( FormConstants.PREFIX_ATTRIBUTE );

                        String strFormattedPattern = String.format( PATTERN_URL_ATTRIBUTE_NAME, sbPatternIterableAttributeName.toString( ) );

                        Matcher matcher = Pattern.compile( strFormattedPattern ).matcher( strIterateAttributeName );
                        if ( matcher.find( ) )
                        {
                            int nAttributeNumber = NumberUtils.toInt( matcher.group( MATCHER_URL_ATTRIBUTE_NUMBER_POSITION ), NumberUtils.INTEGER_MINUS_ONE );

                            // If the number of the attribute is the same that the id of the entry we will return this error
                            if ( nAttributeNumber == idEntry )
                            {
                                return genericAttributeError;
                            }
                        }
                    }
                }
            }
        }

        return currentIterationEntryError;
    }

    /**
     * Populate the model with informations about the iterable group (list of messages and number of iteration) and return a boolean that indicate if errors are
     * present or not.
     * 
     * @param request
     *            The HttpServletRequest
     * @param model
     *            The model of the page
     * @param entry
     *            The entry to analyze
     * @return true if error are present false otherwise
     */
    public static Boolean populateModelIterableGroupInfo( HttpServletRequest request, Map<String, Object> model, Entry entry )
    {
        Boolean bErrorPresent = Boolean.FALSE;

        // Retrieve the existing list of error message
        List<MVCMessage> listInfosIterableGroup = getIterableGroupMessageList( model );

        // Check if there are errors on iteration
        if ( getRemoveIterationParameter( request ).getKey( ) != entry.getIdEntry( ) )
        {
            bErrorPresent = manageIterationGroupErrors( request, entry, listInfosIterableGroup );
        }

        // Populate the model
        model.put( MARK_INFO_ITERABLE_GROUP, listInfosIterableGroup );

        return bErrorPresent;
    }

    /**
     * Manage the response for an entry of type group which allow iteration
     * 
     * @param request
     *            The HttpServletRequest
     * @param entry
     *            The entry to retrieve the values from
     * @param listResponses
     *            The list of existing responses
     */
    public static void manageIterableGroupResponse( HttpServletRequest request, Entry entry, List<Response> listResponses, int nIterationNumber )
    {
        if ( entry != null )
        {
            // Check if the current entry belong to an iterable group or not
            if ( entryBelongIterableGroup( entry ) )
            {
                // Retrieve the current iteration
                String strPatternName = getPatternIteratedAttributeName( entry.getIdEntry( ) );
                if ( nIterationNumber != NumberUtils.INTEGER_MINUS_ONE )
                {
                    // Retrieve the response of the entry which belong to a group
                    retrieveGroupResponses( request, entry, listResponses, strPatternName, nIterationNumber );
                }
            }
        }
    }

    /**
     * Retrieve the response of an entry which belong to a group for the current iteration which match the pattern of their name.
     * 
     * @param request
     *            The HttpServletRequest
     * @param entry
     *            The entry to retrieve the response from
     * @param listResponses
     *            The list of existing responses
     * @param strPatternName
     *            The pattern of the parameter name
     * @param nCurrentIteration
     *            The iteration number of the parameter to retrieve the value from
     */
    private static void retrieveGroupResponses( HttpServletRequest request, Entry entry, List<Response> listResponses, String strPatternName,
            int nCurrentIteration )
    {
        // Retrieve the error of the current entry for the current iteration if exists
        GenericAttributeError currentIterationEntryError = getGenericAttributeEntryError( request, entry.getIdEntry( ), nCurrentIteration );

        // Get all the parameter values associate to the current attribute for the current iteration
        String strAttributeName = String.format( strPatternName, nCurrentIteration );
        String [ ] listCurrentRequestParamValues = request.getParameterValues( strAttributeName );

        if ( listCurrentRequestParamValues == null || listCurrentRequestParamValues.length == 0 )
        {
            List<String> listResultValue = new ArrayList<>( );

            @SuppressWarnings( "unchecked" )
            List<String> listParameterNames = new ArrayList<>( request.getParameterMap( ).keySet( ) );
            for ( String strParameterName : listParameterNames )
            {
                if ( strParameterName.contains( strAttributeName ) )
                {
                    listResultValue.add( request.getParameter( strParameterName ) );
                }
            }

            listCurrentRequestParamValues = listResultValue.toArray( new String [ listResultValue.size( )] );
        }

        // Fill the list of response for the current entry
        fillResponseList( request, listResponses, listCurrentRequestParamValues, entry, currentIterationEntryError, nCurrentIteration );
    }

    /**
     * Fill the list of response with those of the entry
     * 
     * @param request
     *            The HttpServletRequest
     * @param listResponses
     *            The list of response to fill
     * @param strCurrentRequestParamValues
     *            The list of parameters values
     * @param entry
     *            The entry to retrieve the response from
     * @param currentIterationEntryError
     *            The GenericAttributeError for the entry if exist
     * @param nCurrentIteration
     *            The current iteration number
     */
    private static void fillResponseList( HttpServletRequest request, List<Response> listResponses, String [ ] strCurrentRequestParamValues, Entry entry,
            GenericAttributeError currentIterationEntryError, int nCurrentIteration )
    {
        if ( listResponses == null )
        {
            listResponses = new ArrayList<>( );
        }

        // Retrieve the entryTypeService of the current entry
        IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( entry );

        if ( strCurrentRequestParamValues != null && strCurrentRequestParamValues.length > 0 )
        {
            // Create a GroupHttpServletRequestWrapper for the entry type which need it
            GroupHttpServletRequestWrapper groupHttpServletRequestWrapper = new GroupHttpServletRequestWrapper( request, nCurrentIteration );
            groupHttpServletRequestWrapper.setAttribute( FormConstants.ATTRIBUTE_RESPONSE_ITERATION_NUMBER, nCurrentIteration );

            if ( entryTypeService instanceof EntryTypeArray )
            {
                // Reset the specific iteration parameter name for this entry type
                StringBuilder strIterationParameterName = new StringBuilder( groupHttpServletRequestWrapper.getIterationParameterName( ) );
                strIterationParameterName.append( FormConstants.PREFIX_ATTRIBUTE );
                strIterationParameterName.append( entry.getIdEntry( ) );
                strIterationParameterName.append( FormConstants.UNDERSCORE );

                groupHttpServletRequestWrapper.setIterationParameterName( strIterationParameterName.toString( ) );
            }

            entryTypeService.getResponseData( entry, groupHttpServletRequestWrapper, listResponses, request.getLocale( ) );
        }
        // Case of an entry of type Upload
        else
            if ( isEntryOfUploadType( request, entry ) )
            {
                GroupMultipartHttpServletRequestWrapper groupMultipartHttpServletRequest = new GroupMultipartHttpServletRequestWrapper(
                        (MultipartHttpServletRequest) request, nCurrentIteration );
                groupMultipartHttpServletRequest.setAttribute( FormConstants.ATTRIBUTE_RESPONSE_ITERATION_NUMBER, nCurrentIteration );

                entryTypeService.getResponseData( entry, groupMultipartHttpServletRequest, listResponses, request.getLocale( ) );
            }
            else
            {
                if ( currentIterationEntryError != null )
                {
                    listResponses.add( getResponseWithField( entry, currentIterationEntryError, NumberUtils.INTEGER_MINUS_ONE, nCurrentIteration ) );
                }
            }
    }

    /**
     * Create a response for the specified entry with field
     * 
     * @param entry
     *            The entry to create the response from
     * @param genericAttributeEntryError
     *            The generic attribute of the error. Equal to null if not exist
     * @param nIdField
     *            The id of the field of the response. If equal to -1 none field will be attached to the response.
     * @param nCurrentIterationNumber
     *            The current iteration number
     * @return the response associated to the entry
     */
    private static Response getResponseWithField( Entry entry, GenericAttributeError genericAttributeEntryError, int nIdField, int nCurrentIterationNumber )
    {
        Response response = new Response( );

        // Add errors if exists
        if ( genericAttributeEntryError != null )
        {
            entry.setError( genericAttributeEntryError );
        }
        response.setEntry( entry );
        response.setIterationNumber( nCurrentIterationNumber );

        // Add field value if necessary
        if ( nIdField != NumberUtils.INTEGER_MINUS_ONE )
        {
            Field field = null;
            field = FieldHome.findByPrimaryKey( nIdField );

            if ( field != null )
            {
                response.setField( field );
                response.setResponseValue( field.getValue( ) );
            }
        }

        return response;
    }

    /**
     * Reorder the list of Response with the management of the iterations and return the new list of ordered response
     * 
     * @param responsesList
     *            The list of Response to reorder for the iteration management
     * @return the ordered list of responses
     */
    public static List<Response> orderResponseList( HttpServletRequest request, List<Response> responsesList )
    {
        List<Response> listResultResponse = new ArrayList<>( );

        if ( responsesList != null && !responsesList.isEmpty( ) )
        {
            Map<Integer, List<Response>> mapIdEntryListResponse = new LinkedHashMap<>( );

            for ( Response response : responsesList )
            {
                Entry entryResponse = response.getEntry( );
                Entry entryResponseParent = entryResponse.getParent( );

                if ( entryResponseParent != null )
                {
                    // Case of an entry which belong to a group
                    int nIdParent = entryResponseParent.getIdEntry( );
                    if ( !mapIdEntryListResponse.containsKey( nIdParent ) )
                    {
                        mapIdEntryListResponse.put( nIdParent, new ArrayList<Response>( ) );
                    }

                    mapIdEntryListResponse.get( nIdParent ).add( response );
                }
                else
                    if ( entryResponse.getFieldDepend( ) != null )
                    {
                        // Case of a conditional entry - retrieve the parent entry of the conditional entry
                        // to store the Response in the map
                        Field field = FieldHome.findByPrimaryKey( entryResponse.getFieldDepend( ).getIdField( ) );
                        Entry entryField = EntryHome.findByPrimaryKey( field.getParentEntry( ).getIdEntry( ) );
                        while ( entryField.getParent( ) == null && entryField.getFieldDepend( ) != null )
                        {
                            field = FieldHome.findByPrimaryKey( entryField.getFieldDepend( ).getIdField( ) );
                            entryField = EntryHome.findByPrimaryKey( field.getParentEntry( ).getIdEntry( ) );
                        }

                        int nIdParentConditional = entryField.getParent( ).getIdEntry( );
                        if ( !mapIdEntryListResponse.containsKey( nIdParentConditional ) )
                        {
                            mapIdEntryListResponse.put( nIdParentConditional, new ArrayList<Response>( ) );
                        }

                        mapIdEntryListResponse.get( nIdParentConditional ).add( response );
                    }
                    else
                    {
                        // The entry of the current response doesn't belong to a group and its not a conditional entry
                        mapIdEntryListResponse.put( entryResponse.getIdEntry( ), Arrays.asList( response ) );
                    }
            }

            // When the map is fulfilled we will sort each Response list by the iteration number
            if ( !mapIdEntryListResponse.isEmpty( ) )
            {
                for ( java.util.Map.Entry<Integer, List<Response>> entryIdEntryListReponse : mapIdEntryListResponse.entrySet( ) )
                {
                    int nIdEntryIterableGroup = entryIdEntryListReponse.getKey( );
                    List<Response> listCurrentResponses = entryIdEntryListReponse.getValue( );

                    // If the entry is an iterable group we will sort its list of response by the iteration number
                    if ( getEntryMaxIterationAllowed( nIdEntryIterableGroup ) != NumberUtils.INTEGER_MINUS_ONE )
                    {
                        // Sort the list of Response by the iteration number of response
                        Collections.sort( listCurrentResponses, new GroupResponseComparator( ) );

                        // Reset the iteration number of response for the current iterable group
                        resetResponseIterationNumber( request, nIdEntryIterableGroup, listCurrentResponses );
                    }

                    listResultResponse.addAll( listCurrentResponses );
                }
            }
        }

        return listResultResponse;
    }

    /**
     * Reset the iteration number with the position of their current iteration number in the list of all iteration number used for the specified group
     * 
     * @param request
     *            The httpServletRequest to retrieve the IterationGroup of the specified group from
     * @param nIdIterableGroup
     *            The id of the entry to retrieve the IterationGroup
     * @param listResponse
     *            The list of Response of the iterable group to reorder
     */
    private static void resetResponseIterationNumber( HttpServletRequest request, int nIdIterableGroup, List<Response> listResponse )
    {
        List<Integer> listIteratioNumber = new ArrayList<>( );

        // Retrieve the IterationGroup object from the session for the current group
        IterationGroup iterationGroup = retrieveIterationGroup( request, nIdIterableGroup );
        if ( iterationGroup != null )
        {
            listIteratioNumber = new ArrayList<>( iterationGroup.getSetIterationNumber( ) );
        }

        // Reset the iteration number of the response if they belong to an iterable group
        if ( !listIteratioNumber.isEmpty( ) )
        {
            for ( Response response : listResponse )
            {
                // Reset the iteration number of the response to their position inside the list of iteration number used
                int nIterationNumber = listIteratioNumber.indexOf( response.getIterationNumber( ) );
                if ( nIterationNumber != NumberUtils.INTEGER_MINUS_ONE )
                {
                    response.setIterationNumber( nIterationNumber + NumberUtils.INTEGER_ONE );
                }
            }
        }
    }

    /**
     * Remove an iteration from the map of IterationGroup for the requested entry for the iteration number in request
     * 
     * @param request
     *            The HttpServletRequest
     */
    private static void removeIterationFromMapIterationGroup( HttpServletRequest request )
    {
        // Retrieve the iteration to remove from the request
        java.util.Map.Entry<Integer, Integer> mapIdEntryIterationNumber = getRemoveIterationParameter( request );
        int nRemoveIteration = mapIdEntryIterationNumber.getValue( );

        if ( nRemoveIteration != NumberUtils.INTEGER_MINUS_ONE )
        {
            // Retrieve the IterationGroup map from the request
            Map<Integer, IterationGroup> mapIterationGroup = retrieveIterationMap( request );
            if ( mapIterationGroup != null )
            {
                // Retrieve the id of the entry to remove the iteration from
                int nIdEntry = mapIdEntryIterationNumber.getKey( );
                IterationGroup iterationGroup = mapIterationGroup.get( nIdEntry );
                if ( iterationGroup != null )
                {
                    iterationGroup.removeIteration( nRemoveIteration );
                }
            }
        }
    }

    /**
     * Construct the pattern of the attribute name for entry which allow iterations
     * 
     * @param idEntry
     *            The id of the entry to construct the pattern from
     * @return the pattern of the attribute name for entry which allow iterations
     */
    public static String getPatternIteratedAttributeName( int idEntry )
    {
        return FormConstants.PREFIX_ITERATION + "%s_" + FormConstants.PREFIX_ATTRIBUTE + idEntry;
    }

    /**
     * Return the number of current iteration from the request
     * 
     * @param request
     *            the HttpServletRequest
     * @param idEntry
     *            the id of the entry to retrieve the number of iteration
     * @return the number of iteration from the request or -1 if the map doesn't exist;
     */
    public static int getCurrentNumberOfIteration( HttpServletRequest request, int nIdEntry )
    {
        Map<Integer, IterationGroup> mapIterationGroup = retrieveIterationMap( request );

        if ( mapIterationGroup != null )
        {
            IterationGroup iterationGroup = mapIterationGroup.get( nIdEntry );

            if ( iterationGroup != null )
            {
                return iterationGroup.getIterationNumber( );
            }
        }

        return NumberUtils.INTEGER_MINUS_ONE;
    }

    /**
     * Construct the url for an entry of an iterable group for errors
     * 
     * @param entry
     *            the entry to construct the url from
     * @param objIterationNumber
     *            the object representation of the iteration number
     * @return the url for the entry for the specified iteration
     */
    public static String getIterableEntryChildUrl( Entry entry, Object objIterationNumber )
    {
        UrlItem url = new UrlItem( AppPathService.getPortalUrl( ) );
        url.addParameter( XPageAppService.PARAM_XPAGE_APP, FormPlugin.PLUGIN_NAME );

        if ( entry != null && entry.getIdResource( ) > 0 )
        {
            // Build the attribute prefix name
            String strPatternPrefix = StringUtils.EMPTY;
            if ( objIterationNumber != null )
            {
                String strIterationNumber = String.valueOf( objIterationNumber );
                strPatternPrefix = String.format( PATTERN_PREFIX_ITERATION + FormUtils.CONSTANT_UNDERSCORE, strIterationNumber );
            }

            url.addParameter( FormConstants.PARAMETER_ID_FORM, entry.getIdResource( ) );
            url.setAnchor( strPatternPrefix + FormConstants.PREFIX_ATTRIBUTE + entry.getIdEntry( ) );
        }

        return url.getUrl( );
    }

    /**
     * Populate the map from the session with the list of response for the given entry for the specified iteration
     * 
     * @param request
     *            the HttpServletRequest
     * @param entry
     *            the entry to create a new IterationResponse
     * @param nIterationNumber
     *            the iteration number to add the new IterationResponse
     * @param listResponses
     *            the list of response of the entry
     */
    public static void populateMapIterationGroup( HttpServletRequest request, Entry entry, int nIterationNumber, List<Response> listResponses )
    {
        // Retrieve the map from the session
        Map<Integer, IterationGroup> mapIterationGroup = retrieveIterationMap( request );

        if ( mapIterationGroup != null && entry.getParent( ) != null )
        {
            int nIdEntryGroupParent = entry.getParent( ).getIdEntry( );
            if ( !mapIterationGroup.containsKey( nIdEntryGroupParent ) )
            {
                mapIterationGroup.put( nIdEntryGroupParent, new IterationGroup( nIdEntryGroupParent ) );
            }

            // Add the list of Response for the current entry to the IterationGroup
            // for the current iteration
            IterationGroup iterationGroup = mapIterationGroup.get( nIdEntryGroupParent );
            iterationGroup.addEntryResponses( nIterationNumber, entry.getIdEntry( ), listResponses );
        }
    }

    /**
     * Retrieve the map which associate for each iterable group entry identifier its IterationGroup object associated
     * 
     * @param request
     *            the HttpServletRequest to retrieve the map from
     * @return the map which associate for each iterable group entry identifier its IterationGroup object associated
     */
    @SuppressWarnings( "unchecked" )
    public static Map<Integer, IterationGroup> retrieveIterationMap( HttpServletRequest request )
    {
        if ( request != null )
        {
            return (Map<Integer, IterationGroup>) request.getSession( ).getAttribute( FormConstants.SESSION_ITERATION_MAP );
        }

        return null;
    }

    /**
     * Retrieve the IterationGroup from the id of an iterable entry from the map store in the session
     * 
     * @param request
     *            The httpServletRequest to retrieve the map from the session
     * @param nIdEntry
     *            The id of the entry to retrieve the IterationGroup associate
     * @return the IterationGroup associate to the specified id or null if not exist
     */
    public static IterationGroup retrieveIterationGroup( HttpServletRequest request, int nIdEntry )
    {
        return retrieveIterationMap( request ).get( nIdEntry );
    }

    /**
     * Detect if an entry belong to an iterable group or not
     * 
     * @param entry
     *            The entry to analyze
     * @return true if the entry belong to an iterable group false otherwise
     */
    public static boolean entryBelongIterableGroup( Entry entry )
    {
        if ( entry != null )
        {
            if ( entry.getParent( ) != null )
            {
                Entry entryParent = EntryHome.findByPrimaryKey( entry.getParent( ).getIdEntry( ) );
                if ( entryParent != null && entryParent.getEntryType( ) != null && entryParent.getEntryType( ).getGroup( ) )
                {
                    return ( Integer.valueOf( getEntryMaxIterationAllowed( entryParent.getIdEntry( ) ) ) != NumberUtils.INTEGER_MINUS_ONE );
                }
            }
            else
            {
                if ( entry.getFieldDepend( ) != null )
                {
                    Field field = FieldHome.findByPrimaryKey( entry.getFieldDepend( ).getIdField( ) );
                    if ( field != null && field.getParentEntry( ) != null )
                    {
                        return entryBelongIterableGroup( EntryHome.findByPrimaryKey( field.getParentEntry( ).getIdEntry( ) ) );
                    }
                }
            }
        }

        return Boolean.FALSE;
    }

    /**
     * Return the list of MVCMessage associate to the iterable group from the model
     * 
     * @param model
     *            The model to retrieve the messages from
     * @return the list of all message of the group if exist or an empty list otherwise
     */
    @SuppressWarnings( "unchecked" )
    public static List<MVCMessage> getIterableGroupMessageList( Map<String, Object> model )
    {
        if ( model != null )
        {
            Object objInfosIterableGroup = model.get( MARK_INFO_ITERABLE_GROUP );

            if ( objInfosIterableGroup != null )
            {
                return (List<MVCMessage>) objInfosIterableGroup;
            }
        }

        return new ArrayList<MVCMessage>( );
    }

    /**
     * Determine if entry is of a Type Group or not.
     * 
     * @param entry
     *            The entry to analyze
     * @return true if the entry is of a type group false otherwise
     */
    public static boolean isEntryTypeGroup( Entry entry )
    {
        if ( entry != null )
        {
            EntryType entryType = entry.getEntryType( );
            return ( entryType != null && BooleanUtils.isTrue( entryType.getGroup( ) ) );
        }

        return Boolean.FALSE;
    }

    /**
     * Return the list of all id of entries from the form which allowed iterations
     * 
     * @param nParameterIdForm
     *            The id of the form to retrieve the entries from
     * @return the list of all id of entries from the form which allowed iterations or null if there are no entry which iteration in the form
     */
    public static List<Integer> findIdEntryGroupIterable( int nIdForm )
    {
        List<Integer> listIdEntry = null;

        EntryFilter filter = new EntryFilter( );
        filter.setIdResource( nIdForm );
        filter.setResourceType( Form.RESOURCE_TYPE );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );

        List<Entry> listEntryFirstLevel = EntryHome.getEntryList( filter );
        if ( listEntryFirstLevel != null && !listEntryFirstLevel.isEmpty( ) )
        {
            listIdEntry = new ArrayList<>( );
            for ( Entry entry : listEntryFirstLevel )
            {
                int nIdEntry = entry.getIdEntry( );
                if ( getEntryMaxIterationAllowed( nIdEntry ) != NumberUtils.INTEGER_MINUS_ONE )
                {
                    listIdEntry.add( nIdEntry );
                }
            }
        }

        return listIdEntry;
    }

    /**
     * Return the couple of the parameters for removing an iteration. The key is the identifier of the entry and the value is the iteration number to remove. If
     * the parameter is not found in the request the key and the value will be equals to -1.
     * 
     * @param request
     *            The request to retrieve the parameter from
     * @return the couple of the id of the entry and the iteration number to remove
     */
    public static java.util.Map.Entry<Integer, Integer> getRemoveIterationParameter( HttpServletRequest request )
    {
        java.util.Map.Entry<Integer, Integer> entryIdEntryIterationNumber = new AbstractMap.SimpleEntry<>( NumberUtils.INTEGER_MINUS_ONE,
                NumberUtils.INTEGER_MINUS_ONE );

        if ( request != null && request.getParameter( FormConstants.PARAMETER_REMOVE_ITERATION ) != null )
        {
            String [ ] listParameterRemoveIteration = request.getParameter( FormConstants.PARAMETER_REMOVE_ITERATION ).split( FormConstants.UNDERSCORE );
            if ( listParameterRemoveIteration != null && listParameterRemoveIteration.length > 1 )
            {
                int nIdEntry = NumberUtils.toInt( listParameterRemoveIteration [0], NumberUtils.INTEGER_MINUS_ONE );
                int nIterationNumber = NumberUtils.toInt( listParameterRemoveIteration [1], NumberUtils.INTEGER_MINUS_ONE );
                entryIdEntryIterationNumber = new AbstractMap.SimpleEntry<>( nIdEntry, nIterationNumber );
            }
        }

        return entryIdEntryIterationNumber;
    }

    /**
     * Check if the entry belong to a service type of a file upload (file or image) and if the request is of type MultipartHttpServletRequest which allow the
     * upload
     * 
     * @param request
     *            The HtpServletRequest to analyze
     * @param entry
     *            The entry to analyze
     * @return true if the entry belong to an entry type which allow the upload and if the request allow it too
     */
    private static boolean isEntryOfUploadType( HttpServletRequest request, Entry entry )
    {
        boolean bIsEntryOfUploadType = Boolean.FALSE;

        if ( entry != null )
        {
            IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( entry );
            bIsEntryOfUploadType = request instanceof MultipartHttpServletRequest
                    && EntryTypeUploadEnum.getValues( ).contains( entryTypeService.getClass( ).getName( ) );
        }

        return bIsEntryOfUploadType;
    }

    /**
     * Tell if an iterable group has reached its maximum iteration limit or not.
     * 
     * @param request
     *            The HttpServletRequest to retrieve the ietrationGroup of the entry from
     * @param nIdEntry
     *            The id of the entry to retrieve the IterationGroup
     * @return true if the iteration limit has been reached false otherwise
     */
    public static boolean isEntryIterationLimitReached( HttpServletRequest request, int nIdEntry )
    {
        boolean bLimitReached = Boolean.FALSE;

        // Retrieve the current IterationGroup for the specified entry
        Map<Integer, IterationGroup> mapIterationGroup = EntryTypeGroupUtils.retrieveIterationMap( request );
        IterationGroup iterationGroup = mapIterationGroup.get( nIdEntry );

        // Check if the user reach the limit of iteration for the current entry
        if ( iterationGroup != null )
        {
            bLimitReached = iterationGroup.isIterationLimitReached( );
        }

        return bLimitReached;
    }
}
