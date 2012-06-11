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
 * class Entry
 *
 */
public class Entry implements IEntry
{
    //	parameters Entry 
    protected static final String PARAMETER_TITLE = "title";
    protected static final String PARAMETER_HELP_MESSAGE = "help_message";
    protected static final String PARAMETER_COMMENT = "comment";
    protected static final String PARAMETER_MANDATORY = "mandatory";
    protected static final String PARAMETER_FIELD_IN_LINE = "field_in_line";
    protected static final String PARAMETER_HEIGHT = "height";
    protected static final String PARAMETER_WIDTH = "width";
    protected static final String PARAMETER_VALUE = "value";
    protected static final String PARAMETER_MAX_SIZE_ENTER = "max_size_enter";
    protected static final String PARAMETER_CONFIRM_FIELD = "confirm_field";
    protected static final String PARAMETER_CONFIRM_FIELD_TITLE = "confirm_field_title";
    protected static final String SUFFIX_CONFIRM_FIELD = "_confirm_field";
    protected static final String PREFIX_FORM = "form";
    protected static final String PARAMETER_UNIQUE = "unique_field";

    //	message
    protected static final String MESSAGE_MANDATORY_FIELD = "form.message.mandatory.field";
    protected static final String MESSAGE_NUMERIC_FIELD = "form.message.numeric.field";
    protected static final String MESSAGE_CONFIRM_FIELD = "form.message.errorConfirmField";
    protected static final String MESSAGE_UNIQUE_FIELD = "form.message.errorUniqueField";
    protected static final String MESSAGE_XSS_FIELD = "form.message.errorXssField";
    protected static final String MESSAGE_MAXLENGTH = "form.message.maxLength";
    protected static final String MESSAGE_MYLUTECE_AUTHENTIFICATION_REQUIRED = "form.message.myLuteceAuthentificationRequired";
    protected static final String FIELD_TITLE = "form.createEntry.labelTitle";
    protected static final String FIELD_INSERT_GROUP = "form.modifyForm.manageEnter.labelInsertGroup";
    protected static final String FIELD_HELP_MESSAGE = "form.createEntry.labelHelpMessage";
    protected static final String FIELD_COMMENT = "form.createEntry.labelComment";
    protected static final String FIELD_VALUE = "form.createEntry.labelValue";
    protected static final String FIELD_PRESENTATION = "form.createEntry.labelPresentation";
    protected static final String FIELD_MANDATORY = "form.createEntry.labelMandatory";
    protected static final String FIELD_WIDTH = "form.createEntry.labelWidth";
    protected static final String FIELD_HEIGHT = "form.createEntry.labelHeight";
    protected static final String FIELD_MAX_SIZE_ENTER = "form.createEntry.labelMaxSizeEnter";
    protected static final String FIELD_CONFIRM_FIELD = "form.createEntry.labelConfirmField";
    protected static final String FIELD_CONFIRM_FIELD_TITLE = "form.createEntry.labelConfirmFieldTitle";

    //  Jsp Definition
    protected static final String JSP_DOWNLOAD_FILE = "jsp/admin/plugins/form/DoDownloadFile.jsp";

    //MARK
    protected static final String MARK_ENTRY = "entry";

    //Other constants
    protected static final String EMPTY_STRING = "";
    private int _nIdEntry;
    private String _strTitle;
    private String _strHelpMessage;
    private String _strComment;
    private boolean _bMandatory;
    private boolean _bFieldInLine;
    private IMapProvider _mapProvider;
    private int _nPosition;
    private Form _form;
    private EntryType _entryType;
    private IEntry _entryParent;
    private List<IEntry> _listEntryChildren;
    private List<Field> _listFields;
    private Field _fieldDepend;
    private int _nNumberConditionalQuestion;
    private boolean _nFirstInTheList;
    private boolean _nLastInTheList;
    private boolean _bConfirmField;
    private String _strConfirmFieldTitle;
    private boolean _bUnique;
    private FormError _formError;

    /**
     *
     * @return the list of entry who are insert in the group
     */
    public List<IEntry> getChildren(  )
    {
        return _listEntryChildren;
    }

    /**
     *  @return the entry comment
     */
    public String getComment(  )
    {
        return _strComment;
    }

    /**
     *  @return the type of the entry
     */
    public EntryType getEntryType(  )
    {
        return _entryType;
    }

    /**
     * @return the list of field who are associate to the entry
     */
    public List<Field> getFields(  )
    {
        return _listFields;
    }

    /**
     *  @return the entry  help message
     */
    public String getHelpMessage(  )
    {
        return _strHelpMessage;
    }

    /**
     * @return the  id of entry
     */
    public int getIdEntry(  )
    {
        return _nIdEntry;
    }

    /**
     * @return parent entry if the entry is insert in a group
     */
    public IEntry getParent(  )
    {
        return _entryParent;
    }

    /**
     * @return position entry
     */
    public int getPosition(  )
    {
        return _nPosition;
    }

    /**
     * @return title entry
     */
    public String getTitle(  )
    {
        return _strTitle;
    }

    /**
     * @return true if the field associate must be display in line
     */
    public boolean isFieldInLine(  )
    {
        return _bFieldInLine;
    }

    /**
     * @return true if the question is mandatory
     */
    public boolean isMandatory(  )
    {
        return _bMandatory;
    }

    /**
     * set  the list of entry who are insert in the group
     * @param children the list of entry
     */
    public void setChildren( List<IEntry> children )
    {
        _listEntryChildren = children;
    }

    /**
     * set entry comment
     * @param comment entry comment
     */
    public void setComment( String comment )
    {
        _strComment = comment;
    }

    /**
     * set the type of the entry
     * @param entryType the type of the entry
     */
    public void setEntryType( EntryType entryType )
    {
        _entryType = entryType;
    }

    /**
     * set true if the field associate must be display in line
     * @param  fieldInLine true if the field associate must be display in line
     */
    public void setFieldInLine( boolean fieldInLine )
    {
        _bFieldInLine = fieldInLine;
    }

    /**
     * set the list of field who are associate to the entry
     * @param  fields the list of field
     */
    public void setFields( List<Field> fields )
    {
        _listFields = fields;
    }

    /**
     * set  the entry  help message
     * @param  helpMessage the entry  help message
     */
    public void setHelpMessage( String helpMessage )
    {
        _strHelpMessage = helpMessage;
    }

    /**
     * set the id of the entry
     * @param idEntry  the id of the entry
     */
    public void setIdEntry( int idEntry )
    {
        _nIdEntry = idEntry;
    }

    /**
     * set true if the question is mandatory
     * @param  mandatory true if the question is mandatory
     */
    public void setMandatory( boolean mandatory )
    {
        _bMandatory = mandatory;
    }

    /**
     * set parent entry if the entry is insert in a group
     * @param  parent  parent entry
     */
    public void setParent( IEntry parent )
    {
        _entryParent = parent;
    }

    /**
     * set position entry
     * @param  position  position entry
     */
    public void setPosition( int position )
    {
        _nPosition = position;
    }

    /**
     * set title entry
     * @param  title title
     */
    public void setTitle( String title )
    {
        _strTitle = title;
    }

    /**
     *
     * @return the form of the entry
     */
    public Form getForm(  )
    {
        return _form;
    }

    /**
     * set the form of the entry
     * @param form the form of the entry
     */
    public void setForm( Form form )
    {
        this._form = form;
    }

    /**
     * @return the field  if the entry is a conditional question
     */
    public Field getFieldDepend(  )
    {
        return _fieldDepend;
    }

    /**
     * set the field  if the entry is a conditional question
     * @param depend the field  if the entry is a conditional question
     */
    public void setFieldDepend( Field depend )
    {
        _fieldDepend = depend;
    }

    /**
     * Get the HtmlCode  of   the entry
     * @return the HtmlCode  of   the entry
     *
     * */
    public String getHtmlCode(  )
    {
        return null;
    }

    /**
     * Get the request data
     * @param request HttpRequest
     * @param locale the locale
     * @return null if all data requiered are in the request else the url of jsp error
     */
    public String getRequestData( HttpServletRequest request, Locale locale )
    {
        return null;
    }

    /**
     * save in the list of response the response associate to the entry in the form submit
     * @param request HttpRequest
     * @param listResponse the list of response associate to the entry in the form submit
     * @param locale the locale
     * @return a Form error object if there is an error in the response
     */
    public FormError getResponseData( HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        return null;
    }

    /**
     * Get template create url of the entry
     * @return template create url of the entry
     */
    public String getTemplateCreate(  )
    {
        return null;
    }

    /**
     * Get the template modify url  of the entry
     * @return template modify url  of the entry
     */
    public String getTemplateModify(  )
    {
        return null;
    }

    /**
     * @return the number of conditional questions who are assocaite to the entry
     */
    public int getNumberConditionalQuestion(  )
    {
        return _nNumberConditionalQuestion;
    }

    /**
     * Get the response value  associate to the entry  to export in the file export
     * @param response the response associate to the entry
     * @param locale the locale
     * @param request the request
     * @return  the response value  associate to the entry  to export in the file export
     */
    public String getResponseValueForExport( HttpServletRequest request, Response response, Locale locale )
    {
        return EMPTY_STRING;
    }

    /**
     * Get the response value  associate to the entry  to write in the recap
     * @param response the response associate to the entry
     * @param locale the locale
     * @param request the request
     * @return the response value  associate to the entry  to write in the recap
     */
    public String getResponseValueForRecap( HttpServletRequest request, Response response, Locale locale )
    {
        return EMPTY_STRING;
    }

    /**
     * set the number of conditional questions who are assocaite to the entry
     * @param numberConditionalQuestion the number of conditional questions who are assocaite to the entry
     *
     */
    public void setNumberConditionalQuestion( int numberConditionalQuestion )
    {
        _nNumberConditionalQuestion = numberConditionalQuestion;
    }

    /**
     * The paginator who is use in the template modify of the entry
     * @param nItemPerPage Number of items to display per page
     * @param strBaseUrl The base Url for build links on each page link
     * @param strPageIndexParameterName The parameter name for the page index
     * @param strPageIndex The current page index
     * @return the paginator who is use in the template modify of the entry
     */
    public Paginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
        String strPageIndex )
    {
        return null;
    }

    /**
     * The paginator who is use in the template modify of the entry
     * @param nItemPerPage Number of items to display per page
     * @param strBaseUrl The base Url for build links on each page link
     * @param strPageIndexParameterName The parameter name for the page index
     * @param strPageIndex The current page index
     * @param locale Locale
     * @return the paginator who is use in the template modify of the entry
     */
    public LocalizedPaginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
        String strPageIndex, Locale locale )
    {
        return null;
    }

    /**
     * Get the list of regular expression  who is use in the template modify of the entry
     * @param entry the entry
     * @param plugin the plugin
     * @return the regular expression list who is use in the template modify of the entry
     */
    public ReferenceList getReferenceListRegularExpression( IEntry entry, Plugin plugin )
    {
        return null;
    }

    /**
     * @return true if the entry is the last entry of a group or the list of entry
     */
    public boolean isLastInTheList(  )
    {
        return _nLastInTheList;
    }

    /**
     * set true if the entry is the last entry of a group or the list of entry
     * @param lastInTheList true if the entry is the last entry of a group or the list of entry
     */
    public void setLastInTheList( boolean lastInTheList )
    {
        _nLastInTheList = lastInTheList;
    }

    /**
     * @return true if the entry is the first entry of a group or the list of entry
     */
    public boolean isFirstInTheList(  )
    {
        return _nFirstInTheList;
    }

    /**
     * set true if the entry is the first entry of a group or the list of entry
     * @param firstInTheList true if the entry is the last entry of a group or the list of entry
     */
    public void setFirstInTheList( boolean firstInTheList )
    {
        _nFirstInTheList = firstInTheList;
    }

    /**
     * Set the confirmField
     * @param nConfirmField
     */
    public void setConfirmField( boolean bConfirmField )
    {
        this._bConfirmField = bConfirmField;
    }

    /**
     * Get the confirmField value
     * @return the value of the confirmField
     */
    public boolean isConfirmField(  )
    {
        return _bConfirmField;
    }

    /**
     * Set the title of the confirmation field
     * @param strConfirmFieldTitle
     */
    public void setConfirmFieldTitle( String strConfirmFieldTitle )
    {
        this._strConfirmFieldTitle = strConfirmFieldTitle;
    }

    /**
     * Get the title of the confirmation field
     * @return The title of the confirmation field
     */
    public String getConfirmFieldTitle(  )
    {
        return _strConfirmFieldTitle;
    }

    /**
     * Set to true if the value of the response to this question must be unique
     */
    public void setUnique( boolean _bUnique )
    {
        this._bUnique = _bUnique;
    }

    /**
     * @return true if the value of the response to this question must be unique
     */
    public boolean isUnique(  )
    {
        return _bUnique;
    }

    /**
     * The selected map provider
     * @see IMapProvider
     * @return the select map provider
     */
    public IMapProvider getMapProvider(  )
    {
        return _mapProvider;
    }

    /**
     * Sets the map provider
     * @param mapProvider the map provider
     */
    public void setMapProvider( IMapProvider mapProvider )
    {
        _mapProvider = mapProvider;
    }

    /**
     * Get the form error associated to the entry
     * @return the form error
     */
    public FormError getFormError(  )
    {
        return _formError;
    }

    /**
     * Set the form error associated to the entry
     * @param formError the form error
     */
    public void setFormError( FormError formError )
    {
        _formError = formError;
    }

    /**
     * Sets the string value of the response
     * @param response the response
     * @param locale the locale - will use a default one if not specified
     */
    public void setResponseToStringValue( Response response, Locale locale )
    {
        response.setToStringValueResponse( response.getResponseValue(  ) );
    }

    /**
     * Returns <code>true</code> if the entry is a file, <code>false</code> otherwise.
     * @return <code>true</code> if the entry is a file, <code>false</code> otherwise
     */
    public boolean isFile(  )
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public FormError canUploadFiles( List<FileItem> listUploadedFileItems, List<FileItem> listFileItemsToUpload,
        Locale locale )
    {
        return null;
    }
}
