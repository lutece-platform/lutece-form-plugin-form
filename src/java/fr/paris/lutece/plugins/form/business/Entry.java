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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.Paginator;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;


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
    protected static final String PARAMETER_CSS_CLASS = "css_class";

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
    private String _strCSSClass;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IEntry> getChildren( )
    {
        return _listEntryChildren;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getComment( )
    {
        return _strComment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntryType getEntryType( )
    {
        return _entryType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Field> getFields( )
    {
        return _listFields;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHelpMessage( )
    {
        return _strHelpMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIdEntry( )
    {
        return _nIdEntry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IEntry getParent( )
    {
        return _entryParent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPosition( )
    {
        return _nPosition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( )
    {
        return _strTitle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFieldInLine( )
    {
        return _bFieldInLine;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMandatory( )
    {
        return _bMandatory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setChildren( List<IEntry> children )
    {
        _listEntryChildren = children;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setComment( String comment )
    {
        _strComment = comment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEntryType( EntryType entryType )
    {
        _entryType = entryType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFieldInLine( boolean fieldInLine )
    {
        _bFieldInLine = fieldInLine;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFields( List<Field> fields )
    {
        _listFields = fields;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHelpMessage( String helpMessage )
    {
        _strHelpMessage = helpMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIdEntry( int idEntry )
    {
        _nIdEntry = idEntry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMandatory( boolean mandatory )
    {
        _bMandatory = mandatory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setParent( IEntry parent )
    {
        _entryParent = parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPosition( int position )
    {
        _nPosition = position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTitle( String title )
    {
        _strTitle = title;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Form getForm( )
    {
        return _form;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setForm( Form form )
    {
        this._form = form;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field getFieldDepend( )
    {
        return _fieldDepend;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFieldDepend( Field depend )
    {
        _fieldDepend = depend;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHtmlCode( )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( HttpServletRequest request, Locale locale )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FormError getResponseData( HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateCreate( )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateModify( )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberConditionalQuestion( )
    {
        return _nNumberConditionalQuestion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForExport( HttpServletRequest request, Response response, Locale locale )
    {
        return StringUtils.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForRecap( HttpServletRequest request, Response response, Locale locale )
    {
        return StringUtils.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNumberConditionalQuestion( int numberConditionalQuestion )
    {
        _nNumberConditionalQuestion = numberConditionalQuestion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Paginator<?> getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
            String strPageIndex )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalizedPaginator<?> getPaginator( int nItemPerPage, String strBaseUrl,
            String strPageIndexParameterName, String strPageIndex, Locale locale )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceList getReferenceListRegularExpression( IEntry entry, Plugin plugin )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLastInTheList( )
    {
        return _nLastInTheList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLastInTheList( boolean lastInTheList )
    {
        _nLastInTheList = lastInTheList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFirstInTheList( )
    {
        return _nFirstInTheList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFirstInTheList( boolean firstInTheList )
    {
        _nFirstInTheList = firstInTheList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setConfirmField( boolean bConfirmField )
    {
        this._bConfirmField = bConfirmField;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConfirmField( )
    {
        return _bConfirmField;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setConfirmFieldTitle( String strConfirmFieldTitle )
    {
        this._strConfirmFieldTitle = strConfirmFieldTitle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConfirmFieldTitle( )
    {
        return _strConfirmFieldTitle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUnique( boolean _bUnique )
    {
        this._bUnique = _bUnique;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUnique( )
    {
        return _bUnique;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IMapProvider getMapProvider( )
    {
        return _mapProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMapProvider( IMapProvider mapProvider )
    {
        _mapProvider = mapProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FormError getFormError( )
    {
        return _formError;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFormError( FormError formError )
    {
        _formError = formError;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResponseToStringValue( Response response, Locale locale )
    {
        response.setToStringValueResponse( response.getResponseValue( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFile( )
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FormError canUploadFiles( List<FileItem> listUploadedFileItems, List<FileItem> listFileItemsToUpload,
            Locale locale )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCSSClass( String strCSSClass )
    {
        this._strCSSClass = strCSSClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCSSClass( )
    {
        return _strCSSClass;
    }
}
