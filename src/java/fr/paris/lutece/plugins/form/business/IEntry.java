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
package fr.paris.lutece.plugins.form.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.Paginator;

import org.apache.commons.fileupload.FileItem;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 *
 *IEntry Class
 */
public interface IEntry
{
    /**
     * @return the  id of entry
     */
    int getIdEntry(  );

    /**
     * @return title entry
     */
    String getTitle(  );

    /**
     *  @return the entry  help message
     */
    String getHelpMessage(  );

    /**
     *  @return the entry comment
     */
    String getComment(  );

    /**
     * @return true if the question is mandatory
     */
    boolean isMandatory(  );

    /**
     * @return true if the question must be confirmed by a duplicated field
     */
    boolean isConfirmField(  );

    /**
     * @return true if the value of the response to this question must be unique
     */
    boolean isUnique(  );

    /**
     * Get the title of the confirmation field
     * @return The title of the confirmation field
     */
    String getConfirmFieldTitle(  );

    /**
     * @return true if the field associate must be display in line
     */
    boolean isFieldInLine(  );

    /**
     * @return position entry
     */
    int getPosition(  );

    /**
     *
     * @return the form of the entry
     */
    Form getForm(  );

    /**
     *  @return the type of the entry
     */
    EntryType getEntryType(  );

    /**
     * @return parent entry if the entry is insert in a group
     */
    IEntry getParent(  );

    /**
     *
     * @return the list of entry who are insert in the group
     */
    List<IEntry> getChildren(  );

    /**
     * @return the list of field who are associate to the entry
     */
    List<Field> getFields(  );

    /**
     * @return the field  if the entry is a conditional question
     */
    Field getFieldDepend(  );

    /**
     * @return the number of conditional questions who are assocaite to the entry
     */
    int getNumberConditionalQuestion(  );

    /**
     * Get the request data
     * @param request HttpRequest
     * @param locale the locale
     * @return null if all data requiered are in the request else the url of jsp error
     */
    String getRequestData( HttpServletRequest request, Locale locale );

    /**
     * save in the list of response the response associate to the entry in the form submit
     * @param request HttpRequest
     * @param listResponse the list of response associate to the entry in the form submit
     * @param locale the locale
     * @return a Form error object if there is an error in the response
     */
    FormError getResponseData( HttpServletRequest request, List<Response> listResponse, Locale locale );

    /**
     * Get the HtmlCode  of   the entry
     * @return the HtmlCode  of   the entry
     *
     * */
    String getHtmlCode(  );

    /**
     * Get template create url of the entry
     * @return template create url of the entry
     */
    String getTemplateCreate(  );

    /**
     * Get the template modify url  of the entry
     * @return template modify url  of the entry
     */
    String getTemplateModify(  );

    /**
     * The paginator who is use in the template modify of the entry
     * @param nItemPerPage Number of items to display per page
    * @param strBaseUrl The base Url for build links on each page link
    * @param strPageIndexParameterName The parameter name for the page index
    * @param strPageIndex The current page index
     * @return the paginator who is use in the template modify of the entry
     */
    Paginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName, String strPageIndex );

    /**
     * The paginator who is use in the template modify of the entry
     * @param nItemPerPage Number of items to display per page
     * @param strBaseUrl The base Url for build links on each page link
     * @param strPageIndexParameterName The parameter name for the page index
     * @param strPageIndex The current page index
     * @param locale Locale
     * @return the paginator who is use in the template modify of the entry
     */
    LocalizedPaginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
        String strPageIndex, Locale locale );

    /**
     * Get the list of regular expression  who is use in the template modify of the entry
     * @param plugin the plugin
     * @param entry the entry
     * @return the regular expression list who is use in the template modify of the entry
     */
    ReferenceList getReferenceListRegularExpression( IEntry entry, Plugin plugin );

    /**
     * Get the response value  associate to the entry  to export in the file export
     * @param response the response associate to the entry
     * @param locale the locale
     * @param request the request
     * @return  the response value  associate to the entry  to export in the file export
     */
    String getResponseValueForExport( HttpServletRequest request, Response response, Locale locale );

    /**
     * Get the response value  associate to the entry  to write in the recap
     * @param response the response associate to the entry
     * @param locale the locale
     * @param request the request
     * @return the response value  associate to the entry  to write in the recap
     */
    String getResponseValueForRecap( HttpServletRequest request, Response response, Locale locale );

    /**
     * @return true if the entry is the last entry of a group or the list of entry
     */
    boolean isLastInTheList(  );

    /**
     * @return true if the entry is the first entry of a group or the list of entry
     */
    boolean isFirstInTheList(  );

    /**
     * set the id of the entry
     * @param idEntry  the id of the entry
     */
    void setIdEntry( int idEntry );

    /**
     * set title entry
     * @param  title title
     */
    void setTitle( String title );

    /**
     * set  the entry  help message
     * @param  helpMessage the entry  help message
     */
    void setHelpMessage( String helpMessage );

    /**
     * set entry comment
     * @param comment entry comment
     */
    void setComment( String comment );

    /**
     * set true if the question is mandatory
     * @param  mandatory true if the question is mandatory
     */
    void setMandatory( boolean mandatory );

    /**
     * set true if the field associate must be display in line
     * @param  fieldInLine true if the field associate must be display in line
     */
    void setFieldInLine( boolean fieldInLine );

    /**
     * set position entry
     * @param  position  position entry
     */
    void setPosition( int position );

    /**
     * set the form of the entry
     * @param form the form of the entry
     */
    void setForm( Form form );

    /**
     * set the type of the entry
     * @param entryType the type of the entry
     */
    void setEntryType( EntryType entryType );

    /**
     * set parent entry if the entry is insert in a group
     * @param  parent  parent entry
     */
    void setParent( IEntry parent );

    /**
     * set  the list of entry who are insert in the group
     * @param children the list of entry
     */
    void setChildren( List<IEntry> children );

    /**
     * set the list of field who are associate to the entry
     * @param  fields the list of field
     */
    void setFields( List<Field> fields );

    /**
     * set the field  if the entry is a conditional question
     * @param field depend the field  if the entry is a conditional question
     */
    void setFieldDepend( Field field );

    /**
     * set the number of conditional questions who are assocaite to the entry
     * @param numberConditionalQuestion the number of conditional questions who are assocaite to the entry
     *
     */
    void setNumberConditionalQuestion( int numberConditionalQuestion );

    /**
     * set true if the entry is the last entry of a group or the list of entry
     * @param lastInTheList true if the entry is the last entry of a group or the list of entry
     */
    void setLastInTheList( boolean lastInTheList );

    /**
     * set true if the entry is the first entry of a group or the list of entry
     * @param firstInTheList true if the entry is the last entry of a group or the list of entry
     */
    void setFirstInTheList( boolean firstInTheList );

    /**
     * set true if the question must be confirmed by a duplicated field
     * @param  mandatory true if the question must be confirmed by a duplicated field
     */
    void setConfirmField( boolean bConfirmField );

    /**
     * Set the title of the confirmation field
     * @param strConfirmFieldTitle
     */
    void setConfirmFieldTitle( String strConfirmFieldTitle );

    /**
     * Set to true if the value of the response to this question must be unique
     */
    void setUnique( boolean bUnique );

    /**
     * The selected map provider
     * @see IMapProvider
     * @return the select map provider
     */
    IMapProvider getMapProvider(  );

    /**
     * Sets the map provider
     * @param mapProvider the map provider
     */
    void setMapProvider( IMapProvider mapProvider );

    /**
     * Get the form error associated to the entry
     * @return the form error
     */
    FormError getFormError(  );

    /**
     * Set the form error associated to the entry
     * @param formError the form error
     */
    void setFormError( FormError formError );

    /**
     * Sets the string value of the response
     * @param response the response
     * @param locale the locale - will use a default one if not specified
     */
    void setResponseToStringValue( Response response, Locale locale );

    /**
     * Returns <code>true</code> if the entry is a file, <code>false</code> otherwise.
     * @return <code>true</code> if the entry is a file, <code>false</code> otherwise
     */
    boolean isFile(  );

    /**
     * Check if the file can be uploaded or not.
     * This method will check the size of each file and the number max of files
     * that can be uploaded.
     * @param listUploadedFileItems the list of uploaded files
     * @param listFileItemsToUpload the list of files to upload
     * @param locale the locale
     * @return form error if there is an error
     */
    FormError canUploadFiles( List<FileItem> listUploadedFileItems, List<FileItem> listFileItemsToUpload, Locale locale );
}
