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

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
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

import fr.paris.lutece.plugins.form.business.FormSubmit;
import fr.paris.lutece.plugins.form.business.FormSubmitHome;
import fr.paris.lutece.plugins.form.business.iteration.IterationEntry;
import fr.paris.lutece.plugins.form.service.FormPlugin;
import fr.paris.lutece.plugins.form.service.entrytype.EntryTypeGroup;
import fr.paris.lutece.plugins.form.web.http.GroupHttpServletRequestWrapper;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.utils.MVCMessage;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

public class EntryTypeGroupUtils
{
    // Prefix
    private static final String PREFIX_ITERATION = "nIt";
    
    // Pattern
    private static final String REGEX_PREFIX_ID_ATTRIBUTE = "\\b(?!%s)\\w*%s";
    private static final String PATTERN_PREFIX_ITERATION = "nIt%s";
    private static final String PATTERN_URL_ATTRIBUTE_NAME = "^\\b(%s)([0-9]+)$";
    private static final String PATTERN_NEW_ATTRIBUTE_ID = PREFIX_ITERATION + "%s_%s";
    
    // Marks
    private static final String MARK_ITERATION_CODE = "iteration_code";
    private static final String MARK_REMOVE_ITERATION_NUMBER = "remove_iteration_number";
    private static final String MARK_ITERATION_CHILDREN = "iteration_children";
    private static final String MARK_INFO_ITERABLE_GROUP = "infos_iterable_group";
    private static final String MARK_NB_CURRENT_ITERATION = "nb_current_iteration";
    
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
     *          The HttpServletRequest
     * @param entry
     *          The entry
     * @param model
     *          The model to populate
     * @param bDisplayFront
     *          The boolean which tell if it must be displayed in front or not
     */
    public static void getHtmlGroupEntry( HttpServletRequest request, Entry entry, Map<String, Object> model, boolean bDisplayFront )
    {
        // Boolean which tell if errors are present or not
        Boolean bErrorPresent = Boolean.FALSE;
        
        // Manage the case of an adding or a removing of an iteration children group of an entry type group
        if ( request.getParameter( FormConstants.PARAMETER_ADD_ITERATION ) != null || request.getParameter( FormConstants.PARAMETER_REMOVE_ITERATION ) != null
                    || request.getAttribute( FormConstants.ATTRIBUTE_RETURN_FROM_ERRORS ) != null )
        {
            // Populate the model with group informations
            bErrorPresent = populateModelIterableGroupInfo( request, model, entry );
        }
        
        // Generate the Html associate at the children of the entry
        StringBuilder sbGroup = generateHtmlEntryGroup( request, entry, bDisplayFront, bErrorPresent );
        model.put( FormConstants.MARK_STR_LIST_CHILDREN, sbGroup.toString( ) );
    }
    
    /**
     * Generate the html code of the current entry of type group. Manage the case
     * where the entry allow iterations and the case where not allow.
     * 
     * @param request
     *          The HttpServletRequest
     * @param entry
     *          The entry type group
     * @param bDisplayFront
     *          True if the entry will be displayed in Front Office, false if it will be displayed in Back Office
     * @param bErrorPresent
     *          True if an error is present false otherwise
     * @return the StringBuffer containing the html code of the current entry of type group
     */
    public static StringBuilder generateHtmlEntryGroup( HttpServletRequest request, Entry entry, boolean bDisplayFront, boolean bErrorPresent )
    {
        StringBuilder sbGroup = new StringBuilder( );
        
        // Case where the group allow multiple iterations
        int nbIterationMax = getEntryMaxIterationAllowed( entry );
        if ( nbIterationMax != NumberUtils.INTEGER_ZERO )
        {
            // Compute the number of iteration to determine how many group of children of the entry group 
            // are necessary and compare it with the max limit allowed by the entry type
            int nbIteration = computeNumberOfIteration( request, entry.getIdEntry( ), nbIterationMax, bErrorPresent );
            
            sbGroup = getHtmlIteratedEntryGroup( request, entry, bDisplayFront, nbIteration );
        }
        // Case where the entry has no iteration possible
        else
        {
            sbGroup = getHtmlEntryGroup( request, entry, bDisplayFront, sbGroup, NumberUtils.INTEGER_ZERO );
        }
        
        return sbGroup;
    }
    
    /**
     * Return the maximum number of iterations allowed for the entry. Return 0 if none iterations are allowed.
     * 
     * @param entry
     *          The entry to find the maximum number of iterations
     * @return the maximum number of iterations allowed for the entry return 0 if none iterations are allowed
     */
    public static int getEntryMaxIterationAllowed( Entry entry )
    {
        if ( entry != null )
        {
            Field fieldNbIteration = GenericAttributesUtils.findFieldByTitleInTheList( EntryTypeGroup.CONSTANT_NB_ITERATION, entry.getFields( ) );
            if ( fieldNbIteration != null )
            {
                return NumberUtils.toInt( fieldNbIteration.getValue( ), NumberUtils.INTEGER_ZERO );
            }
            
            // If the field doesn't exist it means that the group doesn't allow iteration
            return NumberUtils.INTEGER_ZERO;
        }
        
        return NumberUtils.INTEGER_ZERO;
    }
    
    /**
     * Returns the StringBuffer containing the html code associated for the current entry which
     * allow multiple iteration
     * 
     * @param request
     *          The HttpServletRequest
     * @param entry
     *          The entry type group
     * @param bDisplayFront
     *          True if the entry will be displayed in Front Office, false if it will be displayed in Back Office
     * @param nNbIterationMax
     *          The maximum number of iteration allowed for the group
     * @return the StringBuffer containing the html code associated for the current entry
     */
    public static StringBuilder getHtmlIteratedEntryGroup( HttpServletRequest request, Entry entry, boolean bDisplayFront, int nbIteration )
    {
        StringBuilder sbGroup = new StringBuilder( );
        
        // Construct the list of all children for the group
        int nbCurrentIteration = NumberUtils.INTEGER_ONE;
        while ( nbCurrentIteration <= nbIteration )
        {
            // Get the Html associate to all the children of the group for the current iteration
            StringBuilder sbCurrentChildrenGroup = getHtmlEntryGroup( request, entry, bDisplayFront, new StringBuilder( ), nbCurrentIteration );
            String strCurrentIterationCode = String.format( EntryTypeGroupUtils.PATTERN_PREFIX_ITERATION, nbCurrentIteration );
            
            // Generate the model associate with the current iteration
            Map<String, Object> model = new LinkedHashMap<String, Object>( );
            model.put( FormConstants.PARAMETER_ID_ENTRY, entry.getIdEntry( ) );
            model.put( MARK_REMOVE_ITERATION_NUMBER, nbCurrentIteration );
            model.put( MARK_ITERATION_CODE, strCurrentIterationCode );
            model.put( MARK_ITERATION_CHILDREN, sbCurrentChildrenGroup.toString( ) );
            
            // Populate the template with the model
            HtmlTemplate templateChildrenIterationGroup = AppTemplateService.getTemplate( TEMPLATE_GROUP_ITERATION_CHILDREN, request.getLocale( ) , model );
            
            // Aggregate the current iteration template with the other of the group
            sbGroup.append( templateChildrenIterationGroup.getHtml( ) );
            
            // Make the next iteration
            nbCurrentIteration++;
        }
        
        return sbGroup;
    }

    /**
     * Generate the html code for the entry of type group. Modify the name of the id 
     * attribute if the entry allow iterations simply construct the html otherwise
     * 
     * @param request
     *          The HttpServletRequest
     * @param entry
     *          The entry type group
     * @param bDisplayFront
     *          Boolean which tell if the entry is display in front or not
     * @param sbEntryGroup
     *          The stringBuilder which contains the html code of the entry
     * @return the StringBuilder which contains the html code of the Entry 
     */
    public static StringBuilder getHtmlEntryGroup( HttpServletRequest request, Entry entry, boolean bDisplayFront, StringBuilder sbEntryGroup, int nbIteration )
    {
        for ( Entry entryChild : entry.getChildren( ) )
        {
            int nIdEntryChild = entryChild.getIdEntry( );

            // Set the current iteration to associate an entry with her fields
            request.setAttribute( EntryTypeGroup.ATTRIBUTE_CURRENT_ITERATION, nbIteration );
            
            // Construct the Html template for the current entry
            StringBuffer stringBufferHtmEntryGroup = new StringBuffer( sbEntryGroup.toString( ) );
            FormUtils.getHtmlEntry( nIdEntryChild, stringBufferHtmEntryGroup, request.getLocale( ), bDisplayFront, request );
            
            // If the group allow multiple iteration we change the construction of the name and the id of every field for this attribute
            if ( nbIteration != NumberUtils.INTEGER_ZERO )
            {
                String strPrefix = FormConstants.PREFIX_ATTRIBUTE + nIdEntryChild;
                String strAttributeRegex = String.format( REGEX_PREFIX_ID_ATTRIBUTE, PREFIX_ITERATION, strPrefix );
                String strNewAttributeId = String.format( PATTERN_NEW_ATTRIBUTE_ID, nbIteration, strPrefix );
                sbEntryGroup = new StringBuilder ( stringBufferHtmEntryGroup.toString( ).replaceAll( strAttributeRegex, strNewAttributeId ) );
            }
            else
            {
                // In the case where no iteration are necessaries we made no modifications
                sbEntryGroup = new StringBuilder( stringBufferHtmEntryGroup.toString( ) );
            }
        }
        
        return sbEntryGroup;
    }
    
    /**
     * Get the response for the entry of type group
     * 
     * @param request
     *          The HttpServletReqest
     * @param listFormErrors
     *          The list of formerrors
     * @param entry
     *          The entry to analyze
     * @param plugin
     *          The plugin
     * @param formSubmit
     *          The formSubmit
     * @param bReturnErrors
     *           True if errors must be returned
     * @param locale
     *           The locale
     */
    public static void getResponseGroupEntry( HttpServletRequest request, List<GenericAttributeError> listFormErrors, Entry entry, Plugin plugin, FormSubmit formSubmit,
            boolean bReturnErrors, Locale locale )
    {
        if ( listFormErrors == null )
        {
            listFormErrors = new ArrayList<>( );
        }
        
        if ( entry != null  )
        {
            int nbCurrentIteration = getCurrentNumberOfIteration( request, entry.getIdEntry( ) );
            for ( Entry entryChild : entry.getChildren( ) )
            {
                // If there are a number of iteration on the current group we will check errors on the children on every iteration
                if ( nbCurrentIteration != NumberUtils.INTEGER_MINUS_ONE )
                {
                    for( int numIteration = NumberUtils.INTEGER_ONE; numIteration <= nbCurrentIteration; numIteration++ )
                    {
                        // Wrap the current request to allow modification during the gathering of parameter value on the parameter name
                        HttpServletRequest requestWrapper = new GroupHttpServletRequestWrapper( request, numIteration );
                        requestWrapper.setAttribute( FormConstants.ATTRIBUTE_ITERATION_NUMBER, numIteration );

                        listFormErrors.addAll( FormUtils.getResponseEntry( requestWrapper, entryChild.getIdEntry( ), plugin, formSubmit, Boolean.FALSE, bReturnErrors, locale ) );
                        requestWrapper.removeAttribute( FormConstants.ATTRIBUTE_ITERATION_NUMBER );
                    }
                }
                else
                {
                    listFormErrors.addAll( FormUtils.getResponseEntry( request, entryChild.getIdEntry( ), plugin, formSubmit, Boolean.FALSE, bReturnErrors, locale ) );
                }
            }
        }
    }
    
    /**
     * Compute the number of iteration necessary for the current displaying of the form
     * 
     * @param request
     *          The HttpServletRequest
     * @param entry
     *          The entry type group
     * @param nbIterationMax
     *          The number of max iteration allowed for the group
     * @param bErrorPresent
     *          the boolean which tell if an error is present or not
     * @return the number of iteration necessary for the current form
     */
    public static int computeNumberOfIteration( HttpServletRequest request, int idEntry, int nbIterationMax, boolean bErrorPresent )
    {
        // In all case there always must be one iteration
        Integer nbIteration = NumberUtils.INTEGER_ONE;
        
        // Retrieve the current number of iteration from the request
        int nbCurrentIteration = getCurrentNumberOfIteration( request, idEntry );
        
        if ( nbCurrentIteration != NumberUtils.INTEGER_MINUS_ONE )
        {
            // Manage the case where an error is present
            if ( bErrorPresent || request.getSession( ).getAttribute( FormConstants.SESSION_FORM_ERRORS ) != null )
            {              
                return nbCurrentIteration;
            }
            
            // Iteration duplication case
            if ( request.getParameter( FormConstants.PARAMETER_ADD_ITERATION ) != null )
            {
                nbIteration = nbCurrentIteration + NumberUtils.INTEGER_ONE;
            }
            
            // Iteration removing case
            if ( request.getParameter( FormConstants.PARAMETER_REMOVE_ITERATION ) != null )
            {            
                nbIteration = nbCurrentIteration - NumberUtils.INTEGER_ONE;
            }
        }
        
        // We cannot create more iteration than the group authorize
        return ( nbIteration > nbIterationMax ) ? nbIterationMax : nbIteration;
    }
    
    /**
     * Retrieve all parameters associated to all iteration of an entry and return true if
     * the user has made a selection false otherwise
     * 
     * @param request
     *          the HttpServletRequest
     * @param listParameterNames
     *          the list of all parameters name of the request
     * @param strNbCurrentIteration
     *          the current number of iteration
     * @param strRemoveIteration
     *          the iteration to remove
     * @return true if the user has made selection false otherwise
     */
    public static Boolean retrieveAllIterationValues( HttpServletRequest request, List<String> listParameterNames, String strNbCurrentIteration, String strRemoveIteration )
    {
        // Boolean which check if a user has filled entries
        Boolean bNoFilledEntry = Boolean.TRUE;
        
        if ( listParameterNames != null && !listParameterNames.isEmpty( ) )
        {
            int nNbCurrentIteration = NumberUtils.toInt( strNbCurrentIteration, NumberUtils.INTEGER_ONE );
            int nRemoveIteration = NumberUtils.toInt( strRemoveIteration, NumberUtils.INTEGER_MINUS_ONE );
            
            // Boolean which check if an iteration must be skipped or not
            Boolean bSkipIteration = Boolean.FALSE;
            
            for( int nbIteration = NumberUtils.INTEGER_ONE; nbIteration <= nNbCurrentIteration; nbIteration++ )
            {                
                // If we are in the case of a removing we will skip the removed iteration
                if ( strRemoveIteration != null && nRemoveIteration != NumberUtils.INTEGER_MINUS_ONE && nRemoveIteration == nbIteration )
                {
                    // Set to true to tell the next iterations to set their value to the previous attribute
                    bSkipIteration = Boolean.TRUE;
                    continue;
                }
                
                // Retrieve all parameter values for the current iteration and set them to the request
                bNoFilledEntry = retrieveIterationParameterValues( request, listParameterNames, nbIteration, bSkipIteration );
            }
        }
        
        return bNoFilledEntry;
    }
    
    /**
     * Determine if a user filled entries or not and add in the request all the value 
     * associate to the parameter of an iteration
     * 
     * @param listParameterNames
     *          the list of all parameters
     * @param nbIteration
     *          the iteration number
     * @param bSkipIteration
     *          the boolean which tell if we must skip an iteration (in the case of a removing)
     * @param request
     *          the HttpServletRequest
     * @return true if a filling has been made false otherwise
     */
    public static Boolean retrieveIterationParameterValues( HttpServletRequest request, List<String> listParameterNames, int nbIteration, Boolean bSkipIteration )
    {
        Boolean bNoFilledEntry = Boolean.TRUE; 
        
        if ( listParameterNames != null && !listParameterNames.isEmpty( ) )
        {
            // Generate the prefix of the current iteration
            String strCurrentParameterIteration = String.format( PATTERN_PREFIX_ITERATION, nbIteration );
            
            // We will check if there is a filling has been made for the current iteration by checking
            // if a parameter associate to the current iteration is present or not
            for ( String strParamName : listParameterNames )
            {
                if ( StringUtils.isNotBlank( strParamName ) && strParamName.contains( strCurrentParameterIteration ) )
                {
                    String[] listParameterValuesList = request.getParameterValues( strParamName );
                    if ( listParameterValuesList == null || listParameterValuesList.length == NumberUtils.INTEGER_ZERO 
                            || StringUtils.isBlank( listParameterValuesList[ NumberUtils.INTEGER_ZERO ] ) )
                    {
                        continue;
                    }
                    
                    // In the case of a removing we will skip an iteration so will get all the values associate
                    // to the current iteration and associate them to the previous one because it doesn't exist
                    // anymore
                    if ( bSkipIteration.booleanValue( ) )
                    {
                        String strAttributeName = strParamName.replace( strCurrentParameterIteration, StringUtils.EMPTY );
                        String strPreviousParameterIteration = String.format( PATTERN_PREFIX_ITERATION, ( nbIteration - NumberUtils.INTEGER_ONE ) );

                        // Create the parameter name based on the previous iteration
                        StringBuilder sbNewParamName = new StringBuilder( );
                        sbNewParamName.append( strPreviousParameterIteration );
                        sbNewParamName.append( strAttributeName );

                        // Associate the current parameter value to the previous one named
                        request.setAttribute( sbNewParamName.toString( ), listParameterValuesList );
                    }
                    else
                    {
                        request.setAttribute( strParamName, listParameterValuesList );
                    }

                    // A filling has been made
                    bNoFilledEntry = Boolean.FALSE;
                }
            }
        }
        
        return bNoFilledEntry;
    }
    
    /**
     * Manage the errors of iterations of a entry type group
     * 
     * @param request
     *          the httpServletRequest
     * @param entry
     *          the entry base of the iteration
     * @param listInfosIterableGroup
     *          the list of all existing messages
     * @return true if there are errors false otherwise
     */
    public static Boolean manageIterationGroupErrors( HttpServletRequest request, Entry entry, List<MVCMessage> listInfosIterableGroup, int NbIteration )
    {
        // Check if a filling has been made or not
        Object objAttributeNoEntryGroupFilled = request.getAttribute( FormConstants.ATTRIBUTE_NO_FILLED_ENTRY_GROUP );
        Boolean bNoFillingMade = ( Boolean ) objAttributeNoEntryGroupFilled;
        if ( objAttributeNoEntryGroupFilled != null && BooleanUtils.isTrue( bNoFillingMade ) )
        {
            listInfosIterableGroup.add( new MVCMessage( I18nService.getLocalizedString( MESSAGE_INFO_CANT_ADD_ITERATION, request.getLocale( ) ) ) );          
            return Boolean.TRUE;
        }
        
        // Check if the user has reach the max limit of duplication and if is not the case of a removing
        if ( getEntryMaxIterationAllowed( entry ) <= NbIteration && request.getParameter( FormConstants.PARAMETER_ADD_ITERATION ) != null 
                && request.getParameter( FormConstants.PARAMETER_REMOVE_ITERATION ) == null )
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
     *          the HttpServletRequest
     * @param idEntry
     *          the id of the entry
     * @param nCurrentIteration
     *          the number of the current iteration
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
     *          the list of all errors
     * @param idEntry
     *          the id of the entry
     * @param nCurrentIterationNumber
     *          the current number of the iteration
     * @return the GenericAttributeError associate to the entry if exists null otherwise
     */
    private static GenericAttributeError getCurrentIterationEntryErrors( List<GenericAttributeError> listErrors, int idEntry, int nCurrentIterationNumber)
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
                    String strIterateAttributeName = strUrlError.split( FormConstants.ANCHOR_DELIMITER )[ NumberUtils.INTEGER_ONE ];
                    String strPatternPrefixIteration = String.format( EntryTypeGroupUtils.PATTERN_PREFIX_ITERATION, nCurrentIterationNumber );

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
     * Populate the model with informations about the iterable group (list of messages and number of iteration)
     * and return a boolean that indicate if errors are present or not.
     * 
     * @param request
     *          The HttpServletRequest
     * @param model
     *          The model of the page
     * @param entry
     *          The entry to analyze
     * @return true if error are present false otherwise
     */
    public static Boolean populateModelIterableGroupInfo( HttpServletRequest request, Map<String, Object> model, Entry entry )
    {
        Boolean bErrorPresent = Boolean.FALSE;
        
        int nIdEntryParameter = NumberUtils.toInt( request.getParameter( FormConstants.PARAMETER_ID_ENTRY ), NumberUtils.INTEGER_MINUS_ONE );
        if ( entry != null && nIdEntryParameter == entry.getIdEntry( ) )
        {
            int nIdEntry = entry.getIdEntry( );
            
            // Retrieve the existing list of error message
            List<MVCMessage> listInfosIterableGroup = getIterableGroupMessageList( model );
            
            // Check if there are errors on iteration
            if ( request.getAttribute( FormConstants.ATTRIBUTE_NO_FILLED_ENTRY_GROUP ) == null || request.getParameter( FormConstants.PARAMETER_REMOVE_ITERATION ) == null )
            {
                int nbCurrentIteration = getCurrentNumberOfIteration( request, nIdEntry );
                bErrorPresent = manageIterationGroupErrors( request, entry, listInfosIterableGroup, nbCurrentIteration );
            }
           
            // Compute the number of iteration from the request managing the adding or removing cases
            int nbMaxIterationAllowed = getEntryMaxIterationAllowed( entry );
            int nbIteration = computeNumberOfIteration( request, nIdEntry, nbMaxIterationAllowed, bErrorPresent );
            
            // Populate the model
            model.put( MARK_NB_CURRENT_ITERATION, nbIteration );
            model.put( MARK_INFO_ITERABLE_GROUP, listInfosIterableGroup );
        }
        
        return bErrorPresent;
    }
    
    /**
     * Manage the response for an entry of type group which allow iteration
     * 
     * @param request
     *          The HttpServletRequest
     * @param entry
     *          The entry to retrieve the values from
     * @param listResponses
     *          The list of existing responses
     */
    public static void manageIterableGroupResponse( HttpServletRequest request, Entry entry, List<Response> listResponses )
    {
        if ( entry != null )
        {
            // Check if the parent of the entry is of group type
            Entry entryParent = EntryHome.findByPrimaryKey( entry.getParent( ).getIdEntry( ) );
            if ( isEntryTypeGroup( entryParent ) )
            {
                // Check if the parent allow iteration or not
                if ( getEntryMaxIterationAllowed( entryParent ) != NumberUtils.INTEGER_ZERO )
                {
                    // Retrieve the current iteration
                    String strPatternName = getPatternIteratedAttributeName( entry.getIdEntry( ) );
                    Integer nCurrentIteration = ( Integer ) request.getAttribute( EntryTypeGroup.ATTRIBUTE_CURRENT_ITERATION );
                    if ( nCurrentIteration != null && nCurrentIteration != NumberUtils.INTEGER_ZERO )
                    {
                        // Retrieve the response of the entry which belong to a group
                        retrieveGroupResponses( request, entry, listResponses, strPatternName, nCurrentIteration );
                    }
                }
            }
        }
    }
    
    /**
     * Retrieve the response of an entry which belong to a group for the current iteration which match the pattern of their name. 
     * 
     * @param request
     *          The HttpServletRequest
     * @param entry
     *          The entry to retrieve the response from
     * @param listResponses
     *          The list of existing responses
     * @param strPatternName
     *          The pattern of the parameter name
     * @param nCurrentIteration
     *          The iteration number of the parameter to retrieve the value from
     */
    private static void retrieveGroupResponses( HttpServletRequest request, Entry entry, List<Response> listResponses, String strPatternName, int nCurrentIteration )
    {
        // Consume the current iteration attribute to manage it only one time
        request.removeAttribute( EntryTypeGroup.ATTRIBUTE_CURRENT_ITERATION );
        
        // Retrieve the error of the current entry for the current iteration if exists
        GenericAttributeError currentIterationEntryError = getGenericAttributeEntryError( request, entry.getIdEntry( ), nCurrentIteration );
        
        // Get all the parameter values associate to the current attribute for the current iteration
        String strAttributeName = String.format( strPatternName, nCurrentIteration );
        String[] listCurrentRequestParamValues = retrieveParameterValues( request, strAttributeName );

        // Fill the list of response for the current entry
        fillResponseList( request, listResponses, listCurrentRequestParamValues, entry, currentIterationEntryError );
        
        // Consume each attribute name to manage them only one time
        request.removeAttribute( strAttributeName );
    }
    
    /**
     * Fill the list of response with those of the entry
     * 
     * @param request
     *          The HttpServletRequest
     * @param listResponses
     *          The list of response to fill
     * @param strCurrentRequestParamValues
     *          The list of parameters values
     * @param entry
     *          The entry to retrieve the response from
     * @param currentIterationEntryError
     *          The GenericAttributeError for the entry if exist
     */
    private static void fillResponseList( HttpServletRequest request, List<Response> listResponses, String[] strCurrentRequestParamValues, 
            Entry entry, GenericAttributeError currentIterationEntryError )
    {
        if ( listResponses == null )
        {
            listResponses = new ArrayList<>( );
        }
        
        if ( strCurrentRequestParamValues != null && strCurrentRequestParamValues.length > 0 )
        {            
            // Create a response for each parameter values
            for ( String strCurrentValue : strCurrentRequestParamValues )
            {
                // Retrieve the id of the field
                int nIdField = NumberUtils.toInt( strCurrentValue, NumberUtils.INTEGER_MINUS_ONE );

                if ( nIdField != NumberUtils.INTEGER_MINUS_ONE )
                {
                    listResponses.add( getResponseWithField( entry, currentIterationEntryError, nIdField ) );
                }
                // If a parameter exists but there are none field associated it means that the current value is the response
                else
                {
                    listResponses.add( getResponseWithoutField( entry, currentIterationEntryError, strCurrentValue ) );
                }
            }
        }
        else
        {
            if ( currentIterationEntryError != null )
            {                                        
                listResponses.add( getResponseWithField( entry, currentIterationEntryError, NumberUtils.INTEGER_MINUS_ONE ) );
            }
        }
    }
    
    /**
     * Create a response for the specified entry with field
     * 
     * @param entry
     *      The entry to create the response from
     * @param genericAttributeEntryError
     *      The generic attribute of the error. Equal to null if not exist
     * @param nIdField
     *      The id of the field of the response. If equal to -1 none field will be attached.
     * @return the response associated to the entry
     */
    private static Response getResponseWithField( Entry entry, GenericAttributeError genericAttributeEntryError, int nIdField )
    {
        Response response = new Response( );
        
        // Add errors if exists
        if ( genericAttributeEntryError != null )
        {
            entry.setError( genericAttributeEntryError );
        }
        response.setEntry( entry );
        
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
     * Create a response for the specified entry without field
     * 
     * @param entry
     *      The entry to create the response from
     * @param genericAttributeEntryError
     *      The generic attribute of the error. Equal to null if not exist
     * @param responseValue
     *      The value of the response retrieve from the request
     * @return the response associated to the entry
     */
    private static Response getResponseWithoutField( Entry entry, GenericAttributeError genericAttributeEntryError, String responseValue )
    {
        Response response = new Response( );
        
        // Add errors if exists
        if ( genericAttributeEntryError != null )
        {
            entry.setError( genericAttributeEntryError );
        }

        response.setEntry( entry );
        response.setField( null );
        response.setResponseValue( responseValue );

        return response;
    }
    
    /**
     * Retrieve all parameter values for the current pattern name
     * 
     * @param request
     *          The HttpServletRequest
     * @param strAttributeName
     *          The name of the attribute to retrieve
     * @return the string array of all parameters for the current pattern name for the current iteration
     */
    private static String[] retrieveParameterValues( HttpServletRequest request, String strAttributeName )
    {
        String[] strRequestParamValues = null;
        
        if ( request.getAttribute( FormConstants.ATTRIBUTE_RETURN_FROM_ERRORS ) != null && ( (Boolean) request.getAttribute( FormConstants.ATTRIBUTE_RETURN_FROM_ERRORS ) ) )
        {
            strRequestParamValues = request.getParameterValues( strAttributeName );
        }
        else
        {
            try
            {
                strRequestParamValues = ( String[] ) request.getAttribute( strAttributeName );
            }
            catch( ClassCastException e )
            {
                strRequestParamValues = String.valueOf( request.getAttribute( strAttributeName ) ).split( StringUtils.EMPTY );
            }
        }
        
        return strRequestParamValues;
    }
    
    /**
     * Manage the case where response are linked to an entry of which belong to an iterable group.
     * In this case the id of the entry will be modified to allow the capacity to distinguish them.
     * The list of responses must be sorted by the group of their entry which they belong to.
     * 
     * @param responsesList
     *          The list of response to analyze
     * @return the list of response with the management of iterable entry
     */
    public static List<Response> manageResponsesList( List<Response> responsesList )
    {
        List<Response> resulResponsesList = new ArrayList<>( );
        
        if ( responsesList != null )
        {
            // The list which contains response of a same iterable group
            List<Response> groupResponsesList = new ArrayList<>( );
            
            // The comparator used to compare to response of entries belong to a multi group
            GroupResponseComparator groupResponseComparator = new GroupResponseComparator( );
            
            int nIdPreviousParent = NumberUtils.INTEGER_MINUS_ONE;
            for ( Response response : responsesList )
            {
                Entry currentEntry = response.getEntry( );
                
                // Check if the entry is belong to an iterable group
                if ( currentEntry != null && entryBelongIterableGroup( currentEntry ) )
                {
                    int nIdParent = currentEntry.getParent( ).getIdEntry( );
                    
                    // If the id of the parent is different from the previous it means that we have switch to another group
                    // so we will sort the current group list and add its elements to the result list before clean it
                    if ( nIdPreviousParent != NumberUtils.INTEGER_MINUS_ONE && nIdParent != nIdPreviousParent )
                    {
                        Collections.sort( groupResponsesList, groupResponseComparator );
                        resulResponsesList.addAll( groupResponsesList );
                        groupResponsesList = new ArrayList<>( );
                    }
                    
                    // Add the iteration entry to the group list
                    groupResponsesList.add( response );
                }
                else
                {
                    // If the group list is not empty we will sort all of its elements and add them to the result list
                    // and clean the list
                    if ( !groupResponsesList.isEmpty( ) )
                    {
                        Collections.sort( groupResponsesList, groupResponseComparator );
                        resulResponsesList.addAll( groupResponsesList );
                        groupResponsesList = new ArrayList<>( );
                    }
                    
                    // Add the entry to the result list
                    resulResponsesList.add( response );
                }
            }
            
            // If the loop is over but it remains responses on group list we will add all its elements to the result list 
            if( !groupResponsesList.isEmpty( ) )
            {
                Collections.sort( groupResponsesList, groupResponseComparator );
                resulResponsesList.addAll( groupResponsesList );
            }
        }
        
        return resulResponsesList;
    }
    
    /**
     * Construct the pattern of the attribute name for entry which allow iterations
     * 
     * @param idEntry
     *          The id of the entry to construct the pattern from
     * @return the pattern of the attribute name for entry which allow iterations
     */
    public static String getPatternIteratedAttributeName( int idEntry )
    {
        return PREFIX_ITERATION + "%s_" + FormConstants.PREFIX_ATTRIBUTE + idEntry;
    }
    
    /**
     * Return the number of current iteration from the request
     * 
     * @param request
     *          the HttpServletRequest
     * @param idEntry
     *          the id of the entry to retrieve the number of iteration
     * @return the number of iteration from the request or -1 if an error occurred.
     */
    public static int getCurrentNumberOfIteration( HttpServletRequest request, int idEntry )
    {
        String strParameterIteration = String.format( FormConstants.PATTERN_CURRENT_ITERATION, idEntry );
        String strParameterNbIterationValue = request.getParameter( strParameterIteration );
        
        if ( StringUtils.isNotBlank( strParameterNbIterationValue ) )
        {
            return NumberUtils.toInt( strParameterNbIterationValue, NumberUtils.INTEGER_MINUS_ONE );
        }
        
        return NumberUtils.INTEGER_MINUS_ONE;
    }
    
    /**
     * Construct the url for an entry of an iterable group for errors
     * 
     * @param entry
     *          the entry to construct the url from
     * @param objIterationNumber
     *          the object representation of the iteration number
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
     * Manage the Response which belong to an iterable Entry. In this case the Response is associated
     * to an Entry with an unknown id. This method will find this Entry an return it or will return null
     * if not found
     * 
     * @param response
     *          the Response to retrieve the Entry which is belong to
     * @return the Entry of the specified Response or null if not found
     */
    public static Entry findResponseGroupEntry( Response response )
    {
        Entry entry = null;
        
        if ( response != null )
        {
            // Retrieve the FormSubmit linked to the response for retrieve the form associated
            FormSubmit formSubmit = FormSubmitHome.findFormSubmitFromResponseId( response.getIdResponse( ), FormUtils.getPlugin( ) );
            if ( formSubmit != null && formSubmit.getForm( ) != null )
            {
                // Retrieve the original id of the entry linked to the response from the modified id of the response and the id of the form
                int nIdEntry = response.getEntry( ).getIdEntry( );
                int nOriginalId = retrieveOriginalIdEntry( nIdEntry, formSubmit.getForm( ).getIdForm( ) );
                
                // If the id are different it means that the original entry of the response has been found
                if ( nOriginalId != nIdEntry )
                {
                    entry = EntryHome.findByPrimaryKey( nOriginalId );
                }
            }
        }
        
        return entry;
    }
    
    /**
     * Find Response missing in the Response list of a formSubmit from the list of id Responses of a FormSubmit.
     * This difference is probably due to the fact that the Response is associated to an iterable entry 
     * and has been saved with a computed id
     * 
     * @param listResponses
     *          the list of Response of a FormSubmit
     * @param listIdResponse
     *          the list of all Response id of a FormSubmit
     */
    public static void completeListResponse( List<Response> listResponses, List<Integer> listIdResponse )
    {
        if ( listIdResponse == null || listIdResponse.isEmpty( ) )
        {
            return;
        }
        
        // Retrieve the id from the response list which are not belong to a response from the result list
        if ( listResponses != null && !listResponses.isEmpty( ) )
        {
            for ( Response response : listResponses )
            {
                int nIdResponse = response.getIdResponse( );
                if ( listIdResponse.contains( nIdResponse ) )
                {
                    listIdResponse.remove( Integer.valueOf( nIdResponse ) );
                }
            }
        }
        
        // If some response id don't have a response it's probably because they are belong
        // to an iterable entry so we will try find this entry if it exists
        if ( !listIdResponse.isEmpty( ) )
        {
            for ( Integer idResponse : listIdResponse )
            {
                Response currentResponse = ResponseHome.lazyFindByPrimaryKey( idResponse );                
                Entry entry = findResponseGroupEntry( currentResponse );
                
                if ( entry != null )
                {
                    currentResponse.setEntry( entry );
                    listResponses.add( currentResponse );
                }
            }
        }
    }
    
    /**
     * Modify the id of response of an entry which is belong to an iterable group
     * 
     * @param request
     *          The HttpServletRequest
     * @param entry
     *          The entry to modify is id in response
     * @param listResponse
     *          The list of response of the entry
     */
    public static void modifyResponseEntryId( HttpServletRequest request, Entry entry, List<Response> listResponse )
    {
        if ( entry != null && listResponse != null )
        {
            int nIdEntryOriginal = entry.getIdEntry( );
            for ( Response response : listResponse )
            {
                int nIterationNumber = (Integer) request.getAttribute( FormConstants.ATTRIBUTE_ITERATION_NUMBER );
                entry.setIdEntry( computeIterationId( nIdEntryOriginal, nIterationNumber ) );
                response.setEntry( entry );
            }
        }
    }
  
    /**
     * Compute the new id of the entry which belong to an iterable group for an iteration
     * 
     * @param nIdEntry
     *      the id of the entry to modify
     * @param nIterationNumber
     *      the number of the iteration to make the calculation from
     * @return the new id of the entry or the given id if the passing parameters are less or equal than 0
     */
    public static int computeIterationId( int nIdEntry, int nIterationNumber )
    {
        int nComputedId = nIdEntry;
        
        if ( nIdEntry > NumberUtils.INTEGER_ZERO && nIterationNumber > NumberUtils.INTEGER_ZERO )
        {
            nComputedId = nIdEntry * FormConstants.ENTRY_ID_MULTIPLIER * nIterationNumber;
        }
        
        return nComputedId;
    }
    
    /**
     * Return the original id of an entry or the identifier given as parameter if no result have been found
     * 
     * @param nIdEntry
     *          the identifier of the entry to analyze
     * @param nIdForm
     *          the identifier of the form where entry belong to
     * @return the original identifier of the entry or the identifier given as parameter if no result have been found
     */
    public static int retrieveOriginalIdEntry( int nIdEntry, int nIdForm )
    {
        IterationEntry iterationEntry = createIterationEntry( nIdEntry, nIdForm );
        
        if ( iterationEntry != null )
        {
            return iterationEntry.getIdEntryOriginal( );
        }
        
        return nIdEntry;
    }
    
    /**
     * Create an IterationEntry from the modified id of an entry and the id of the form of which is attached
     * 
     * @param nIdEntry
     *      the modified id of an entry
     * @param nIdForm
     *      the id of the form where the entry belong to
     * @return the IterationEntry object associated to this entry or null if not found
     */
    public static IterationEntry createIterationEntry( int nIdEntry, int nIdForm )
    {
        IterationEntry iterationEntry = null;
        
        Map<Integer, String> mapEntryIdValue = EntryHome.findEntryByForm( PluginService.getPlugin( FormPlugin.PLUGIN_NAME ), nIdForm );
        if ( mapEntryIdValue != null && !mapEntryIdValue.isEmpty( ) )
        {
            Set<Integer> setIdFormEntry = mapEntryIdValue.keySet( );
            for ( Integer currentIdEntry : setIdFormEntry )
            {
                Entry entry = EntryHome.findByPrimaryKey( currentIdEntry );
                if ( isEntryTypeGroup( entry ) && getEntryMaxIterationAllowed( entry ) != NumberUtils.INTEGER_ZERO )
                {
                    java.util.Map.Entry<Integer, Integer> entryIdEntryIteration = findOriginalIdEntry( nIdEntry, entry );
                    if ( entryIdEntryIteration != null && entryIdEntryIteration.getKey( ) != nIdEntry 
                            && entryIdEntryIteration.getValue( ) != NumberUtils.INTEGER_MINUS_ONE )
                    {
                        iterationEntry = new IterationEntry( );
                        iterationEntry.setIdEntryComputed( nIdEntry );
                        iterationEntry.setIdEntryOriginal( entryIdEntryIteration.getKey( ) );
                        iterationEntry.setIdIterableParentGroup( entry.getIdEntry( ) );
                        iterationEntry.setIterationNumber( entryIdEntryIteration.getValue( ) );
                        
                        break;
                    }
                }
            }
        }
        
        return iterationEntry;
    }
    
    /**
     * Retrieve the original Id of an entry which belong to an iterable group and the number of the iteration 
     * for which it has been constructed
     * 
     * @param nIdModified
     *          the identifier of the entry which has been modified
     * @param entryParent
     *          the parent entry of the current entry to analyze the identifier
     * @return the entry which contains the original id and the number of the iteration associated 
     *      or the given id and {@link NumberUtils#INTEGER_MINUS_ONE} if not found
     */
    private static java.util.Map.Entry<Integer, Integer> findOriginalIdEntry( int nIdModified, Entry entryParent )
    {
        // Check if the parent is of type group and allow the iterations
        if ( entryParent != null && isEntryTypeGroup( entryParent ) && getEntryMaxIterationAllowed( entryParent ) != NumberUtils.INTEGER_ZERO )
        {
            int nNumberIterationMaxAllowed = getEntryMaxIterationAllowed( entryParent );
            
            List<Integer> listEntryChildrenId = new ArrayList<>( );
            List<Entry> childrenEntryList = entryParent.getChildren( );
            
            // Construct the list of all id of children belong to the group
            if ( childrenEntryList != null && !childrenEntryList.isEmpty( ) )
            {
                for ( Entry entry : childrenEntryList )
                {
                    listEntryChildrenId.add( entry.getIdEntry( ) );
                }
            }
            
            if ( !listEntryChildrenId.isEmpty( ) )
            {
                // Make a loop on all iteration possible for the group
                for ( int currentIterationNumber = NumberUtils.INTEGER_ONE ; currentIterationNumber <= nNumberIterationMaxAllowed ; currentIterationNumber++ )
                {
                    // Compute the potential original identifier
                    int nOriginalId = nIdModified / ( FormConstants.ENTRY_ID_MULTIPLIER * currentIterationNumber );
                    
                    // If the computed identifier has been found we return it because it is the original identifier
                    if ( listEntryChildrenId.contains( nOriginalId ) )
                    {
                        return new SimpleEntry<Integer, Integer>( nOriginalId, currentIterationNumber );
                    }
                }
            }
        }
        
        // Return the given identifier if no result have been found
        return new SimpleEntry<Integer, Integer>( nIdModified, NumberUtils.INTEGER_MINUS_ONE );
    }
    
    /**
     * Detect if an entry belong to an iterable group or not
     * 
     * @param entry
     *          The entry to analyze
     * @return true if the entry belong to an iterable group false otherwise
     */
    public static boolean entryBelongIterableGroup( Entry entry )
    {
        if ( entry != null && entry.getParent( ) != null )
        {
            Entry entryParent = EntryHome.findByPrimaryKey( entry.getParent( ).getIdEntry( ) );
            if ( entryParent != null && entryParent.getEntryType( ) != null && entryParent.getEntryType( ).getGroup( ) )
            {
                return ( Integer.valueOf( getEntryMaxIterationAllowed( entryParent ) ) != NumberUtils.INTEGER_ZERO );
            }
        }

        return Boolean.FALSE;
    }
    
    /**
     * Return the list of MVCMessage associate to the iterable group from the model
     * 
     * @param model
     *          The model to retrieve the messages from
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
                return ( List<MVCMessage> ) objInfosIterableGroup;
            }
        }
        
        return new ArrayList<MVCMessage>( );
    }
    
    /**
     * Determine if entry is of a Type Group or not.
     * 
     * @param entry
     *          The entry to analyze
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
}
