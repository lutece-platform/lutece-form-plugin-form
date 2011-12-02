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
package fr.paris.lutece.plugins.form.utils;

import java.awt.Color;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Week;
import org.jfree.data.xy.XYDataset;

import fr.paris.lutece.plugins.form.business.Category;
import fr.paris.lutece.plugins.form.business.EntryFilter;
import fr.paris.lutece.plugins.form.business.EntryHome;
import fr.paris.lutece.plugins.form.business.EntryType;
import fr.paris.lutece.plugins.form.business.EntryTypeHome;
import fr.paris.lutece.plugins.form.business.Field;
import fr.paris.lutece.plugins.form.business.FieldHome;
import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormError;
import fr.paris.lutece.plugins.form.business.FormFilter;
import fr.paris.lutece.plugins.form.business.FormHome;
import fr.paris.lutece.plugins.form.business.FormSubmit;
import fr.paris.lutece.plugins.form.business.IEntry;
import fr.paris.lutece.plugins.form.business.Response;
import fr.paris.lutece.plugins.form.business.StatisticFormSubmit;
import fr.paris.lutece.plugins.form.service.draft.FormDraftBackupService;
import fr.paris.lutece.plugins.form.service.parameter.FormParameterService;
import fr.paris.lutece.portal.business.mailinglist.Recipient;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.captcha.CaptchaSecurityService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.filesystem.FileSystemUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.xml.XmlUtil;


/**
 *
 * class FormUtils
 *
 */
public final class FormUtils
{
    // other constants
    public static final String CONSTANT_GROUP_BY_DAY = "0";
    public static final String CONSTANT_GROUP_BY_WEEK = "1";
    public static final String CONSTANT_GROUP_BY_MONTH = "2";
    public static final String EMPTY_STRING = "";
    public static final String CONSTANT_EQUAL = "=";
    public static final String PARAMETER_ID_ENTRY = "id_entry";
    public static final String PARAMETER_DELETE_PREFIX = "delete_";
    public static final String SESSION_ATTRIBUTE_PREFIX_FILE = "FORM_FILE_";
    public static final String BEAN_ENTRY_TYPE_SERVICE = "form.entryTypeService";
    public static final String BEAN_FORM_RESPONSE_SERVICE = "form.responseService";
    public static final int CONSTANT_ID_NULL = -1;

    private static final String MARK_LOCALE = "locale";
    private static final String MARK_URL_ACTION = "url_action";
    private static final String MARK_ENTRY = "entry";
    private static final String MARK_FIELD = "field";
    private static final String MARK_STR_LIST_CHILDREN = "str_list_entry_children";
    private static final String MARK_FORM = "form";
    private static final String MARK_FORM_SUBMIT = "formSubmit";
    private static final String MARK_JCAPTCHA = "jcaptcha";
    private static final String MARK_STR_ENTRY = "str_entry";
    private static final String MARK_CATEGORY_LIST = "category_list";
    private static final String MARK_LIST_RESPONSES = "list_responses";
    private static final String MARK_VALIDATE_REQUIREMENT = "validate_requirement";
    private static final String MARK_DRAFT_SUPPORTED = "draft_supported";
    private static final String PARAMETER_ID_ENTRY_TYPE = "id_type";
    private static final String JCAPTCHA_PLUGIN = "jcaptcha";
    private static final String CONSTANT_WHERE = " WHERE ";
    private static final String CONSTANT_AND = " AND ";
    
	// session
    public static final String SESSION_FORM_LIST_SUBMITTED_RESPONSES = "form_list_submitted_responses";
    public static final String SESSION_VALIDATE_REQUIREMENT = "session_validate_requirement";

    //	 Xml Tags
    private static final String TAG_FORM = "form";
    private static final String TAG_FORM_TITLE = "form-title";
    private static final String TAG_FORM_SUBMIT = "submit";
    private static final String TAG_FORM_SUBMITS = "submits";
    private static final String TAG_FORM_SUBMIT_ID = "submit-id";
    private static final String TAG_FORM_SUBMIT_DATE = "submit-date";
    private static final String TAG_FORM_SUBMIT_IP = "submit-ip";
    private static final String TAG_QUESTIONS = "questions";
    private static final String TAG_QUESTION = "question";
    private static final String TAG_QUESTION_TITLE = "question-title";
    private static final String TAG_QUESTION_ID = "question-id";
    private static final String TAG_RESPONSES = "responses";
    private static final String TAG_RESPONSE = "response";
    private static final String TAG_FORM_ENTRIES = "form-entries";
    private static final String TAG_FORM_ENTRY = "form-entry";
    private static final String TAG_FORM_ENTRY_ID = "form-entry-id";
    private static final String TAG_FORM_ENTRY_TITLE = "form-entry-title";

    //TEMPLATE
    private static final String TEMPLATE_DIV_CONDITIONAL_ENTRY = "admin/plugins/form/html_code_div_conditional_entry.html";
    private static final String TEMPLATE_HTML_CODE_FORM = "admin/plugins/form/html_code_form.html";
    private static final String TEMPLATE_NOTIFICATION_MAIL_END_DISPONIBILITY = "admin/plugins/form/notification_mail_end_disponibility.html";
    private static final String TEMPLATE_NOTIFICATION_MAIL_FORM_SUBMIT = "admin/plugins/form/notification_mail_form_submit.html";

    //property
    private static final String PROPERTY_NOTIFICATION_MAIL_END_DISPONIBILITY_SUBJECT = "form.notificationMailEndDisponibility.subject";
    private static final String PROPERTY_NOTIFICATION_MAIL_END_DISPONIBILITY_SENDER_NAME = "form.notificationMailEndDisponibility.senderName";
    private static final String PROPERTY_NOTIFICATION_MAIL_FORM_SUBMIT_SUBJECT = "form.notificationMailFormSubmit.subject";
    private static final String PROPERTY_NOTIFICATION_MAIL_FORM_SUBMIT_SENDER_NAME = "form.notificationMailFormSubmit.senderName";
    private static final String PROPERTY_CHOOSE_CATEGORY = "form.form.choose.category";
    private static final String REGEX_ID = "^[\\d]+$";

  
    /**
     * FormUtils
     *
     */
    private FormUtils(  )
    {
    }

    /**
     * sendMail to the mailing list associate to the form a mail of end disponibility
     * @param form the form
     * @param locale the locale
     */
    public static void sendNotificationMailEndDisponibility( Form form, Locale locale )
    {
        try
        {
            String strSubject = I18nService.getLocalizedString( PROPERTY_NOTIFICATION_MAIL_END_DISPONIBILITY_SUBJECT,
                    locale );
            String strSenderName = I18nService.getLocalizedString( PROPERTY_NOTIFICATION_MAIL_END_DISPONIBILITY_SENDER_NAME,
                    locale );
            String strSenderEmail = MailService.getNoReplyEmail(  );

            Collection<Recipient> listRecipients = AdminMailingListService.getRecipients( form.getIdMailingList(  ) );
            Map<String, Object> model = new HashMap<String, Object>(  );
            model.put( MARK_FORM, form );

            HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_NOTIFICATION_MAIL_END_DISPONIBILITY, locale, model );

            // Send Mail
            for ( Recipient recipient : listRecipients )
            {
                // Build the mail message
                MailService.sendMailHtml( recipient.getEmail(  ), strSenderName, strSenderEmail, strSubject,
                    t.getHtml(  ) );
            }
        }
        catch ( Exception e )
        {
            AppLogService.error( "Error during Notify end disponibilty of form : " + e.getMessage(  ) );
        }
    }

    /**
     * sendMail to the mailing list associate to the form a mail of new form submit
     * @param form the form
     * @param locale the locale
     */
    public static void sendNotificationMailFormSubmit( Form form, Locale locale )
    {
    	Collection<Recipient> listRecipients = AdminMailingListService.getRecipients( form.getIdMailingList(  ) );
    	Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_FORM, form );
        sendNotificationMailFormSubmit( model, listRecipients, locale );
    }
    
    /**
     * SendMail to the mailing list associate to the form a mail of new form submit.
     * It will also display the anwsers submitted by the user.
     * @param formSubmit the submit form
     * @param locale {@link Locale}
     */
    public static void sendNotificationMailFormSubmit( FormSubmit formSubmit, Locale locale )
    {
    	Collection<Recipient> listRecipients = AdminMailingListService.getRecipients( formSubmit.getForm(  ).getIdMailingList(  ) );
    	Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_FORM, formSubmit.getForm(  ) );
        model.put( MARK_FORM_SUBMIT, formSubmit );
        sendNotificationMailFormSubmit( model, listRecipients, locale );
    }
    
    /**
     * Send the mail
     * @param model the model of the template
     * @param listRecipients the list of recipients
     * @param locale {@link Locale}
     */
    private static void sendNotificationMailFormSubmit( Map<String, Object> model, Collection<Recipient> listRecipients, Locale locale )
    {
    	try
        {
            String strSubject = I18nService.getLocalizedString( PROPERTY_NOTIFICATION_MAIL_FORM_SUBMIT_SUBJECT, locale );
            String strSenderName = I18nService.getLocalizedString( PROPERTY_NOTIFICATION_MAIL_FORM_SUBMIT_SENDER_NAME,
                    locale );
            String strSenderEmail = MailService.getNoReplyEmail(  );

            HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_NOTIFICATION_MAIL_FORM_SUBMIT, locale, model );

            // Send Mail
            for ( Recipient recipient : listRecipients )
            {
                // Build the mail message
                MailService.sendMailHtml( recipient.getEmail(  ), strSenderName, strSenderEmail, strSubject,
                    t.getHtml(  ) );
            }
        }
        catch ( Exception e )
        {
            AppLogService.error( "Error during Notify a new form submit" + e.getMessage(  ) );
        }
    }

    /**
     * return a timestamp Object which correspond with the string specified in parameter.
     * @param date the date who must convert
     * @param locale the locale
     * @return a timestamp Object which correspond with the string specified in parameter.
     */
    public static Timestamp getDateLastMinute( Date date, Locale locale )
    {
        if ( date == null )
        {
            return null;
        }

        Calendar caldate = new GregorianCalendar(  );
        caldate.setTime( date );
        caldate.set( Calendar.MILLISECOND, 0 );
        caldate.set( Calendar.SECOND, 0 );
        caldate.set( Calendar.HOUR_OF_DAY, caldate.getActualMaximum( Calendar.HOUR_OF_DAY ) );
        caldate.set( Calendar.MINUTE, caldate.getActualMaximum( Calendar.MINUTE ) );

        Timestamp timeStamp = new Timestamp( caldate.getTimeInMillis(  ) );

        return timeStamp;
    }

    /**
     * return a timestamp Object which correspond with the string specified in parameter.
     * @param date the date who must convert
     * @param locale the locale
     * @return a timestamp Object which correspond with the string specified in parameter.
     */
    public static Timestamp getDateFirstMinute( Date date, Locale locale )
    {
        if ( date == null )
        {
            return null;
        }

        Calendar caldate = new GregorianCalendar(  );
        caldate.setTime( date );
        caldate.set( Calendar.MILLISECOND, 0 );
        caldate.set( Calendar.SECOND, 0 );
        caldate.set( Calendar.HOUR_OF_DAY, caldate.getActualMinimum( Calendar.HOUR_OF_DAY ) );
        caldate.set( Calendar.MINUTE, caldate.getActualMinimum( Calendar.MINUTE ) );

        Timestamp timeStamp = new Timestamp( caldate.getTimeInMillis(  ) );

        return timeStamp;
    }

    /**
     * return the day  of the timestamp
     * in parameter
     * @param timestamp date
     * @return the day  of the timestamp in parameter
     */
    public static int getDay( Timestamp timestamp )
    {
        Calendar caldate = new GregorianCalendar(  );
        caldate.setTime( timestamp );

        return caldate.get( Calendar.DAY_OF_MONTH );
    }

    /**
     * return the week  of the timestamp
     * in parameter
     * @param timestamp date
     * @return the week  of the timestamp in parameter
     */
    public static int getWeek( Timestamp timestamp )
    {
        Calendar caldate = new GregorianCalendar(  );
        caldate.setTime( timestamp );

        return caldate.get( Calendar.WEEK_OF_YEAR );
    }

    /**
     * return the month  of the timestamp
     * in parameter
     * @param timestamp date
     * @return the month  of the timestamp in parameter
     */
    public static int getMonth( Timestamp timestamp )
    {
        Calendar caldate = new GregorianCalendar(  );
        caldate.setTime( timestamp );

        return caldate.get( Calendar.MONTH );
    }

    /**
     * return the year  of the timestamp
     * in parameter
     * @param timestamp date
     * @return the year  of the timestamp in parameter
     */
    public static int getYear( Timestamp timestamp )
    {
        Calendar caldate = new GregorianCalendar(  );
        caldate.setTime( timestamp );

        return caldate.get( Calendar.YEAR );
    }

    /**
     * return a timestamp Object which correspond to the timestamp
     * in parameter add with a  number of times unit (day,week,month)specify in strTimesUnit .
     * @param timestamp date
     * @param strTimesUnit (day,week,month)
     * @param nDecal the number of timesUnit
     * @return a timestamp Object which correspond with the string specified in parameter
     * add with a  number of times unit (day,week,month)specify in strTimesUnit.
     */
    public static Timestamp addStatisticInterval( Timestamp timestamp, String strTimesUnit, int nDecal )
    {
        int nTimesUnit = Calendar.DAY_OF_MONTH;

        if ( strTimesUnit.equals( FormUtils.CONSTANT_GROUP_BY_WEEK ) )
        {
            nTimesUnit = Calendar.WEEK_OF_MONTH;
        }
        else if ( strTimesUnit.equals( FormUtils.CONSTANT_GROUP_BY_MONTH ) )
        {
            nTimesUnit = Calendar.MONTH;
        }

        Calendar caldate = new GregorianCalendar(  );
        caldate.setTime( timestamp );
        caldate.set( Calendar.MILLISECOND, 0 );
        caldate.set( Calendar.SECOND, 0 );
        caldate.set( Calendar.HOUR_OF_DAY, caldate.getActualMaximum( Calendar.HOUR_OF_DAY ) );
        caldate.set( Calendar.MINUTE, caldate.getActualMaximum( Calendar.MINUTE ) );
        caldate.add( nTimesUnit, nDecal );

        Timestamp timeStamp1 = new Timestamp( caldate.getTimeInMillis(  ) );

        return timeStamp1;
    }

    /**
     * Compare two timestamp and return true if  they have the same times unit(Day,week,month)
     * @param  timestamp1 timestamp1
     * @param  timestamp2 timestamp2
     * @param strTimesUnit (day,week,month)
     * @return Compare two timestamp and return true if  they have the same times unit(Day,week,month)
     */
    public static boolean sameDate( Timestamp timestamp1, Timestamp timestamp2, String strTimesUnit )
    {
        Calendar caldate1 = new GregorianCalendar(  );
        caldate1.setTime( timestamp1 );

        Calendar caldate2 = new GregorianCalendar(  );
        caldate2.setTime( timestamp2 );

        if ( strTimesUnit.equals( CONSTANT_GROUP_BY_DAY ) &&
                ( caldate1.get( Calendar.YEAR ) == caldate2.get( Calendar.YEAR ) ) &&
                ( caldate1.get( Calendar.DAY_OF_YEAR ) == caldate2.get( Calendar.DAY_OF_YEAR ) ) )
        {
            return true;
        }
        else if ( strTimesUnit.equals( CONSTANT_GROUP_BY_WEEK ) &&
                ( caldate1.get( Calendar.YEAR ) == caldate2.get( Calendar.YEAR ) ) &&
                ( caldate1.get( Calendar.WEEK_OF_YEAR ) == caldate2.get( Calendar.WEEK_OF_YEAR ) ) )
        {
            return true;
        }
        else if ( strTimesUnit.equals( CONSTANT_GROUP_BY_MONTH ) &&
                ( caldate1.get( Calendar.YEAR ) == caldate2.get( Calendar.YEAR ) ) &&
                ( caldate1.get( Calendar.MONTH ) == caldate2.get( Calendar.MONTH ) ) )
        {
            return true;
        }

        return false;
    }

    /**
     * Converts une java.sql.Timestamp date in a String date in a "jj/mm/aaaa" format
     *
     * @param date java.sql.Timestamp date to convert
     * @param locale the locale
     * @return strDate The String date in the short locale format or the emmpty String if the date is null
     * @deprecated
     */
    public static String getDateString( Timestamp date, Locale locale )
    {
        DateFormat dateFormat = DateFormat.getDateInstance( DateFormat.SHORT, locale );

        return dateFormat.format( date );
    }

    /**
     * return current Timestamp
     * @return return current Timestamp
     */
    public static Timestamp getCurrentTimestamp(  )
    {
        return new Timestamp( GregorianCalendar.getInstance(  ).getTimeInMillis(  ) );
    }

    /**
     * return current date without hours, minutes and milliseconds
     * @return return current date
     */
    public static Date getCurrentDate(  )
    {
        Calendar cal1 = Calendar.getInstance(  );
        cal1.setTime( new Date(  ) );
        cal1.set( Calendar.HOUR_OF_DAY, 0 );
        cal1.set( Calendar.MINUTE, 0 );
        cal1.set( Calendar.SECOND, 0 );
        cal1.set( Calendar.MILLISECOND, 0 );

        return cal1.getTime(  );
    }

    /**
     * return an instance of IEntry function of type entry
     * @param request the request
     * @param plugin the plugin
     * @return an instance of IEntry function of type entry
     */
    public static IEntry createEntryByType( HttpServletRequest request, Plugin plugin )
    {
        String strIdType = request.getParameter( PARAMETER_ID_ENTRY_TYPE );
        int nIdType = -1;
        IEntry entry = null;
        EntryType entryType;

        if ( ( strIdType != null ) && !strIdType.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdType = Integer.parseInt( strIdType );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return null;
            }
        }

        if ( nIdType == -1 )
        {
            return null;
        }

        entryType = EntryTypeHome.findByPrimaryKey( nIdType, plugin );

        try
        {
            entry = (IEntry) Class.forName( entryType.getClassName(  ) ).newInstance(  );
            entry.setEntryType( entryType );
        }
        catch ( ClassNotFoundException e )
        {
            //  class doesn't exist
            AppLogService.error( e );
        }
        catch ( InstantiationException e )
        {
            // Class is abstract or is an  interface or haven't accessible builder
            AppLogService.error( e );
        }
        catch ( IllegalAccessException e )
        {
            // can't access to rhe class
            AppLogService.error( e );
        }

        return entry;
    }

    /**
     * return the index in the list of the entry whose key is specified  in parameter
     * @param nIdEntry the key of the entry
     * @param listEntry the list of the entry
     * @return the index in the list of the entry whose key is specified  in parameter
     */
    public static int getIndexEntryInTheEntryList( int nIdEntry, List<IEntry> listEntry )
    {
        int nIndex = 0;

        for ( IEntry entry : listEntry )
        {
            if ( entry.getIdEntry(  ) == nIdEntry )
            {
                return nIndex;
            }

            nIndex++;
        }

        return nIndex;
    }

    /**
     *  return the index in the list of the field whose key is specified  in parameter
     * @param nIdField the key of the field
     * @param listField the list of field
     * @return the index in the list of the field whose key is specified  in parameter
     */
    public static int getIndexFieldInTheFieldList( int nIdField, List<Field> listField )
    {
        int nIndex = 0;

        for ( Field field : listField )
        {
            if ( field.getIdField(  ) == nIdField )
            {
                return nIndex;
            }

            nIndex++;
        }

        return nIndex;
    }

    /**
     *  return the html code of the form
     * @param form the form which html code must be return
     * @param strUrlAction  the url who must be call after the form submit
     * @param plugin the plugin
     * @param locale the locale
     * @param request HttpServletRequest
     * @return the html code of the form
     */
    public static String getHtmlForm( Form form, String strUrlAction, Plugin plugin, Locale locale, HttpServletRequest request )
    {
        List<IEntry> listEntryFirstLevel;
        Map<String, Object> model = new HashMap<String, Object>(  );
        HtmlTemplate template;
        EntryFilter filter;
        StringBuffer strBuffer = new StringBuffer(  );
        filter = new EntryFilter(  );
        filter.setIdForm( form.getIdForm(  ) );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        listEntryFirstLevel = EntryHome.getEntryList( filter, plugin );
        
        ArrayList<Category> listCats = new ArrayList<Category>(  );
        Category category = new Category(  );

        category.setIdCategory( -2 );
        category.setTitle( I18nService.getLocalizedString( PROPERTY_CHOOSE_CATEGORY, locale ) );

        if ( form.getCategory(  ) != null )
        {
            listCats.add( category );
        }

        ReferenceList refCategoryList = getRefListCategory( listCats );

        for ( IEntry entry : listEntryFirstLevel )
        {
            FormUtils.getHtmlEntry( entry.getIdEntry(  ), plugin, strBuffer, locale, request );
        }

        if ( form.isActiveCaptcha(  ) && PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) )
        {
            CaptchaSecurityService captchaSecurityService = new CaptchaSecurityService(  );
            model.put( MARK_JCAPTCHA, captchaSecurityService.getHtmlCode(  ) );
        }

        model.put( MARK_CATEGORY_LIST, refCategoryList );
        model.put( MARK_FORM, form );
        model.put( MARK_URL_ACTION, strUrlAction );
        model.put( MARK_STR_ENTRY, strBuffer.toString(  ) );
        model.put( MARK_LOCALE, locale );
        if ( request != null && request.getSession(  ) != null )
        {
        	if ( request.getSession(  ).getAttribute( SESSION_VALIDATE_REQUIREMENT ) != null )
        	{
        		boolean bValidateRequirement = ( Boolean ) request.getSession(  ).getAttribute( SESSION_VALIDATE_REQUIREMENT );
        		model.put( MARK_VALIDATE_REQUIREMENT, bValidateRequirement );
        	}
        	
        }

        //Theme management
        /*Theme theme = ThemeHome.findByPrimaryKey("red");
        model.put( MARK_THEME_URL, theme.getPathCss(  ) );*/
        model.put( MARK_DRAFT_SUPPORTED, FormDraftBackupService.isDraftSupported(  ) );
        template = AppTemplateService.getTemplate( TEMPLATE_HTML_CODE_FORM, locale, model );

        return template.getHtml(  );
    }

    /**
     * Init reference list with the different categories
     *
     *
     * @param listCategories the list of categories
     * @return reference list of  category
     */
    public static ReferenceList getRefListCategory( List<Category> listCategories )
    {
        ReferenceList refListCategories = new ReferenceList(  );

        for ( Category category : listCategories )
        {
            refListCategories.addItem( category.getIdCategory(  ), category.getTitle(  ) );
        }

        return refListCategories;
    }

    /**
     *  return the html code of the form
     * @param form the form which html code must be return
     * @param strUrlAction  the url who must be call after the form submit
     * @param plugin the plugin
     * @param locale the locale
     * @return the html code of the form
     */
    public static String getHtmlForm( Form form, String strUrlAction, Plugin plugin, Locale locale )
    {
        return getHtmlForm( form, strUrlAction, plugin, locale, null );
    }
    
    /**
     * insert in the string buffer the content of the html code of the entry
     * @param nIdEntry the key of the entry which html code must be insert in the stringBuffer
     * @param plugin the plugin
     * @param stringBuffer the buffer which contains the html code
     * @param locale the locale
     * @param request HttpServletRequest
     */
    @SuppressWarnings("unchecked")
	public static void getHtmlEntry( int nIdEntry, Plugin plugin, StringBuffer stringBuffer, Locale locale, HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        StringBuffer strConditionalQuestionStringBuffer = null;
        HtmlTemplate template;
        IEntry entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        if ( entry.getEntryType(  ).getGroup(  ) )
        {
            StringBuffer strGroupStringBuffer = new StringBuffer(  );

            for ( IEntry entryChild : entry.getChildren(  ) )
            {
                getHtmlEntry( entryChild.getIdEntry(  ), plugin, strGroupStringBuffer, locale, request );
            }

            model.put( MARK_STR_LIST_CHILDREN, strGroupStringBuffer.toString(  ) );
        }
        else
        {
            if ( entry.getNumberConditionalQuestion(  ) != 0 )
            {
                for ( Field field : entry.getFields(  ) )
                {
                    field.setConditionalQuestions( FieldHome.findByPrimaryKey( field.getIdField(  ), plugin )
                                                            .getConditionalQuestions(  ) );
                }
            }
        }

        if ( entry.getNumberConditionalQuestion(  ) != 0 )
        {
            strConditionalQuestionStringBuffer = new StringBuffer(  );

            for ( Field field : entry.getFields(  ) )
            {
                if ( field.getConditionalQuestions(  ).size(  ) != 0 )
                {
                    StringBuffer strGroupStringBuffer = new StringBuffer(  );

                    for ( IEntry entryConditional : field.getConditionalQuestions(  ) )
                    {
                        getHtmlEntry( entryConditional.getIdEntry(  ), plugin, strGroupStringBuffer, locale, request );
                    }

                    model.put( MARK_STR_LIST_CHILDREN, strGroupStringBuffer.toString(  ) );
                    model.put( MARK_FIELD, field );
                    template = AppTemplateService.getTemplate( TEMPLATE_DIV_CONDITIONAL_ENTRY, locale, model );
                    strConditionalQuestionStringBuffer.append( template.getHtml(  ) );
                }
            }

            model.put( MARK_STR_LIST_CHILDREN, strConditionalQuestionStringBuffer.toString(  ) );
        }

        model.put( MARK_ENTRY, entry );
        model.put( MARK_LOCALE, locale );
        if ( request != null && request.getSession(  ) != null )
        {
        	Map<Integer, List<Response>> listSubmittedResponses = ( Map<Integer, List<Response>> ) request.getSession(  ).
        			getAttribute( SESSION_FORM_LIST_SUBMITTED_RESPONSES );
        	if ( listSubmittedResponses != null )
        	{
        		List<Response> listResponses = listSubmittedResponses.get( entry.getIdEntry(  ) );
            	if ( listResponses != null )
            	{
            		model.put( MARK_LIST_RESPONSES, listResponses );
            	}
        	}
        }
        template = AppTemplateService.getTemplate( entry.getHtmlCode(  ), locale, model );
        stringBuffer.append( template.getHtml(  ) );
    }

    /**
     * insert in the string buffer the content of the html code of the entry
     * @param nIdEntry the key of the entry which html code must be insert in the stringBuffer
     * @param plugin the plugin
     * @param stringBuffer the buffer which contains the html code
     * @param locale the locale
     */
    public static void getHtmlEntry( int nIdEntry, Plugin plugin, StringBuffer stringBuffer, Locale locale )
    {
    	getHtmlEntry( nIdEntry, plugin, stringBuffer, locale, null );
    }

    /**
     * perform in the object formSubmit the responses associates with a entry specify in parameter.
     * return null if there is no error in the response else return a FormError Object
     * @param request the request
     * @param nIdEntry the key of the entry
     * @param plugin the plugin
     * @param formSubmit Form Submit Object
     * @param bResponseNull true if the response create must be null
     * @param locale the locale
     * @return null if there is no error in the response else return a FormError Object
     */
    @SuppressWarnings("unchecked")
	public static FormError getResponseEntry( HttpServletRequest request, int nIdEntry, Plugin plugin,
        FormSubmit formSubmit, boolean bResponseNull, Locale locale )
    {
        FormError formError = null;
        Response response = null;
        IEntry entry = null;
        List<Response> listResponse = new ArrayList<Response>(  );
        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        List<Field> listField = new ArrayList<Field>(  );

        for ( Field field : entry.getFields(  ) )
        {
            field = FieldHome.findByPrimaryKey( field.getIdField(  ), plugin );
            listField.add( field );
        }

        entry.setFields( listField );

        if ( entry.getEntryType(  ).getGroup(  ) )
        {
            for ( IEntry entryChild : entry.getChildren(  ) )
            {
            	FormError formErrorForThisEntry = getResponseEntry( request, entryChild.getIdEntry(  ), plugin, formSubmit, false, locale ); 
                if ( formErrorForThisEntry != null && formError == null )
                {
                	formError = new FormError(  );
                	formError.setMandatoryError( false );
                	formError.setTitleQuestion( entry.getTitle(  ) );
                }
            }
        }
        else if ( !entry.getEntryType(  ).getComment(  ) )
        {
            if ( !bResponseNull )
            {
                formError = entry.getResponseData( request, listResponse, locale );
            }
            else
            {
                response = new Response(  );
                response.setEntry( entry );
                listResponse.add( response );
            }

            if ( formError != null )
            {
            	entry.setFormError( formError );
            }
            if ( request.getSession(  ) != null )
    		{
        		Map<Integer, List<Response>> listSubmittedResponses = ( Map<Integer, List<Response>> ) request.getSession(  ).
        				getAttribute( SESSION_FORM_LIST_SUBMITTED_RESPONSES );
        		if ( listSubmittedResponses != null )
        		{
        			listSubmittedResponses.put( entry.getIdEntry(  ), listResponse );
            		request.getSession(  ).setAttribute( SESSION_FORM_LIST_SUBMITTED_RESPONSES, listSubmittedResponses );
        		}
    		}

            formSubmit.getListResponse(  ).addAll( listResponse );

            if ( entry.getNumberConditionalQuestion(  ) != 0 )
            {
                for ( Field field : entry.getFields(  ) )
                {
                    if ( isFieldInTheResponseList( field.getIdField(  ), listResponse ) )
                    {
                        for ( IEntry conditionalEntry : field.getConditionalQuestions(  ) )
                        {
                            formError = getResponseEntry( request, conditionalEntry.getIdEntry(  ), plugin, formSubmit,
                                    false, locale );
                        }
                    }
                    else
                    {
                        for ( IEntry conditionalEntry : field.getConditionalQuestions(  ) )
                        {
                            getResponseEntry( request, conditionalEntry.getIdEntry(  ), plugin, formSubmit, true, locale );
                        }
                    }
                }
            }
        }
        return formError;
    }

    /**
     * return the field which key is specified in parameter
     * @param nIdField the id of the field who is search
     * @param listField the list of field
     * @return the field which key is specified in parameter
     */
    public static Field findFieldByIdInTheList( int nIdField, List<Field> listField )
    {
        for ( Field field : listField )
        {
            if ( field.getIdField(  ) == nIdField )
            {
                return field;
            }
        }

        return null;
    }

    /**
     * return true if the field which key is specified in parameter is in the response list
     * @param nIdField the id of the field who is search
     * @param listResponse the list of object Response
     * @return true if the field which key is specified in parameter is in the response list
     */
    public static Boolean isFieldInTheResponseList( int nIdField, List<Response> listResponse )
    {
        for ( Response response : listResponse )
        {
            if ( ( response.getField(  ) != null ) && ( response.getField(  ).getIdField(  ) == nIdField ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * return the xml of form's response
     * @param request the request
     * @param form the form
     * @param listFormSubmit the form submit list
     * @param locale the locale
     * @param plugin the plugin
     * @return the xml of the response of a form
     */
    public static String getXmlResponses( HttpServletRequest request, Form form, List<FormSubmit> listFormSubmit,
        Locale locale, Plugin plugin )
    {
    	// this map stores field in order to not request db multiple time for same field
    	Map<String, Field> mapFields = new HashMap<String, Field>(  );
        StringBuffer buffer = new StringBuffer(  );
        XmlUtil.beginElement( buffer, TAG_FORM );
        XmlUtil.addElementHtml( buffer, TAG_FORM_TITLE, form.getTitle(  ) );
        
        // Build entries list XML
        XmlUtil.beginElement( buffer, TAG_FORM_ENTRIES );
        for ( IEntry entry : getAllQuestionList( form.getIdForm(  ), plugin ) )
        {
        	XmlUtil.beginElement( buffer, TAG_FORM_ENTRY );
        	XmlUtil.addElement( buffer, TAG_FORM_ENTRY_ID, entry.getIdEntry(  ) );
        	XmlUtil.addElementHtml( buffer, TAG_FORM_ENTRY_TITLE, entry.getTitle(  ) );
        	XmlUtil.endElement( buffer, TAG_FORM_ENTRY );
        }
        XmlUtil.endElement( buffer, TAG_FORM_ENTRIES );
        
        // Build Form submits list XML
        XmlUtil.beginElement( buffer, TAG_FORM_SUBMITS );

        //ResponseFilter filter=new ResponseFilter();
        //filter.setIdForm(form.getIdForm());
        //List<FormSubmit> listFormSubmit=FormSubmitHome.getFormSubmitList(filter, plugin);
        for ( FormSubmit formSubmit : listFormSubmit )
        {
            XmlUtil.beginElement( buffer, TAG_FORM_SUBMIT );
            XmlUtil.addElement( buffer, TAG_FORM_SUBMIT_ID, formSubmit.getIdFormSubmit(  ) );
            XmlUtil.addElement( buffer, TAG_FORM_SUBMIT_DATE, getDateString( formSubmit.getDateResponse(  ), locale ) );

            if ( formSubmit.getIp(  ) != null )
            {
                XmlUtil.addElement( buffer, TAG_FORM_SUBMIT_IP, formSubmit.getIp(  ) );
            }
            else
            {
                XmlUtil.addElement( buffer, TAG_FORM_SUBMIT_IP, EMPTY_STRING );
            }

            //filter.setIdForm(formSubmit.getIdFormSubmit());
            //List<Response> listResponses=ResponseHome.getResponseList(filter, plugin);
            Response responseStore = null;
            XmlUtil.beginElement( buffer, TAG_QUESTIONS );

            if ( ( formSubmit.getListResponse(  ) != null ) && !formSubmit.getListResponse(  ).isEmpty(  ) )
            {
                for ( Response response : formSubmit.getListResponse(  ) )
                {
                	if ( response.getField(  ) != null )
                	{
                		Field field = mapFields.get( response.getField(  ).getIdField(  ) );
                		if ( field == null )
                		{
                			field = FieldHome.findByPrimaryKey( response.getField(  ).getIdField(  ), plugin );
                		}
                		response.setField( field );
                	}
                    if ( ( responseStore != null ) &&
                            ( response.getEntry(  ).getIdEntry(  ) != responseStore.getEntry(  ).getIdEntry(  ) ) )
                    {
                        XmlUtil.endElement( buffer, TAG_RESPONSES );
                        XmlUtil.endElement( buffer, TAG_QUESTION );
                    }

                    if ( ( responseStore == null ) ||
                            ( response.getEntry(  ).getIdEntry(  ) != responseStore.getEntry(  ).getIdEntry(  ) ) )
                    {
                        XmlUtil.beginElement( buffer, TAG_QUESTION );
                        XmlUtil.addElementHtml( buffer, TAG_QUESTION_TITLE, response.getEntry(  ).getTitle(  ) );
                        XmlUtil.addElement( buffer, TAG_QUESTION_ID, response.getEntry(  ).getIdEntry(  ) );
                        XmlUtil.beginElement( buffer, TAG_RESPONSES );
                    }

                    if ( StringUtils.isNotBlank( response.getResponseValue(  ) ) || response.getFile(  ) != null )
                    {
                        XmlUtil.addElementHtml( buffer, TAG_RESPONSE,
                            response.getEntry(  ).getResponseValueForExport( request, response, locale ) );
                    }
                    else
                    {
                        XmlUtil.addElement( buffer, TAG_RESPONSE, StringUtils.EMPTY );
                    }

                    responseStore = response;
                }

                XmlUtil.endElement( buffer, TAG_RESPONSES );
                XmlUtil.endElement( buffer, TAG_QUESTION );
            }

            XmlUtil.endElement( buffer, TAG_QUESTIONS );
            XmlUtil.endElement( buffer, TAG_FORM_SUBMIT );
        }

        XmlUtil.endElement( buffer, TAG_FORM_SUBMITS );
        XmlUtil.endElement( buffer, TAG_FORM );

        return buffer.toString(  );
    }

    /**
     * write the http header in the response
     * @param request the httpServletRequest
     * @param response the http response
     * @param strFileName the name of the file who must insert in the response
     * @param strFileExtension the file extension
     */
    public static void addHeaderResponse( HttpServletRequest request, HttpServletResponse response, String strFileName,
        String strFileExtension )
    {
        response.setHeader( "Content-Disposition", "attachment ;filename=\"" + strFileName + "\"" );

        if ( strFileExtension.equals( "csv" ) )
        {
        	response.setCharacterEncoding( FormParameterService.getService(  ).getExportCSVEncoding(  ) );
            response.setContentType( "application/csv" );
        }
        else
        {
        	response.setCharacterEncoding( FormParameterService.getService(  ).getExportXMLEncoding(  ) );
            String strMimeType = FileSystemUtil.getMIMEType( strFileName );

            if ( strMimeType != null )
            {
                response.setContentType( strMimeType );
            }
            else
            {
                response.setContentType( "application/octet-stream" );
            }
        }

        response.setHeader( "Pragma", "public" );
        response.setHeader( "Expires", "0" );
        response.setHeader( "Cache-Control", "must-revalidate,post-check=0,pre-check=0" );
    }

    /**
     * create a JFreeChart Graph function of the statistic form submit
     * @param listStatistic the list of statistic of form submit
     * @param strLabelX the label of axis x
     * @param strLableY the label of axis x
     * @param strTimesUnit the times unit of axis x(Day,Week,Month)
     * @return a JFreeChart Graph function of the statistic form submit
     */
    public static JFreeChart createXYGraph( List<StatisticFormSubmit> listStatistic, String strLabelX,
        String strLableY, String strTimesUnit )
    {
        XYDataset xyDataset = createDataset( listStatistic, strTimesUnit );
        JFreeChart jfreechart = ChartFactory.createTimeSeriesChart( EMPTY_STRING, strLabelX, strLableY, xyDataset,
                false, false, false );
        jfreechart.setBackgroundPaint( Color.white );

        XYPlot xyplot = jfreechart.getXYPlot(  );

        //xyplot.setBackgroundPaint(Color.gray);
        //xyplot.setRangeGridlinesVisible(true);
        xyplot.setBackgroundPaint( Color.white );
        xyplot.setBackgroundPaint( Color.lightGray );
        xyplot.setDomainGridlinePaint( Color.white );
        xyplot.setRangeGridlinePaint( Color.white );

        //		DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis(  );
        //		dateaxis.setLowerMargin(0);
        //		DateFormat formatter = new SimpleDateFormat("d-MMM-yyyy");
        //		dateaxis.setTickUnit(new DateTickUnit(DateTickUnit.DAY,7,formatter));
        //dateaxis.setTickUnit(new DateTickUnit(DateTickUnit.DAY,7));
        //dateaxis.setTickUnit(new DateTickUnit(DateTickUnit.DAY,7));

        //dateaxis.setMinimumDate((Date)listStatistic.get(0).getTimesUnit());
        //dateaxis.setMaximumDate((Date)listStatistic.get(listStatistic.size()-1).getTimesUnit());

        //dateaxis.setTickUnit(new DateTickUnit(DateTickUnit.MONTH,1));
        //dateaxis.setTickUnit(new DateTickUnit(1, 1, DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRENCH)));
        //dateaxis.setTickUnit(new DateTickUnit(DateTickUnit.MONTH, 1, new SimpleDateFormat("MM/YY")));
        //dateaxis.setVerticalTickLabels( true );
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xyplot.getRenderer(  );
        renderer.setBaseShapesVisible( true );
        renderer.setSeriesFillPaint( 0, Color.RED );
        renderer.setUseFillPaint( true );

        //		renderer.setToolTipGenerator( new StandardXYToolTipGenerator( "{0} {1} {2}",
        //				DateFormat.getDateInstance( DateFormat.SHORT, Locale.FRENCH ), NumberFormat.getInstance(  ) ) );
        //
        //		ChartRenderingInfo info = new ChartRenderingInfo( new StandardEntityCollection(  ) );
        return jfreechart;
    }

    /**
     * create graph dataset function of the statistic form submit
     * @param listStatistic the list of statistic of form submit
     * @param strTimesUnit the times unit of axis x(Day,Week,Month)
     * @return create graph dataset function of the statistic form submit
     */
    private static XYDataset createDataset( List<StatisticFormSubmit> listStatistic, String strTimesUnit )
    {
        TimeSeries series = null;

        if ( strTimesUnit.equals( CONSTANT_GROUP_BY_DAY ) )
        {
            series = new TimeSeries( EMPTY_STRING, Day.class );

            for ( StatisticFormSubmit statistic : listStatistic )
            {
                series.add( new Day( (Date) statistic.getStatisticDate(  ) ), statistic.getNumberResponse(  ) );
            }
        }
        else if ( strTimesUnit.equals( CONSTANT_GROUP_BY_WEEK ) )
        {
            series = new TimeSeries( EMPTY_STRING, Week.class );

            for ( StatisticFormSubmit statistic : listStatistic )
            {
                series.add( new Week( (Date) statistic.getStatisticDate(  ) ), statistic.getNumberResponse(  ) );
            }
        }

        else if ( strTimesUnit.equals( CONSTANT_GROUP_BY_MONTH ) )
        {
            series = new TimeSeries( EMPTY_STRING, Month.class );

            for ( StatisticFormSubmit statistic : listStatistic )
            {
                series.add( new Month( (Date) statistic.getStatisticDate(  ) ), statistic.getNumberResponse(  ) );
            }
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(  );
        dataset.addSeries( series );

        return dataset;
    }

    /**
     * Load the data of all  form that the user is authorized to see depends workgroups user
     * @param plugin the plugin
     * @param user the current user
     * @return  a  reference list of form
     */
    @SuppressWarnings("unchecked")
	public static ReferenceList getFormList( Plugin plugin, AdminUser user )
    {
        List<Form> listForms = FormHome.getFormList( new FormFilter(  ), plugin );
        listForms = (List) AdminWorkgroupService.getAuthorizedCollection( listForms, user );

        ReferenceList refListForms = new ReferenceList(  );

        for ( Form form : listForms )
        {
            refListForms.addItem( form.getIdForm(  ), form.getTitle(  ) );
        }

        return refListForms;
    }

    /**
     * Get the ReferenceList associated to all questions
     * @param nIdForm the id form
     * @param plugin the {@link Plugin}
     * @return a {@link ReferenceList}
     */
    public static ReferenceList getRefListAllQuestions( int nIdForm, Plugin plugin )
    {
        ReferenceList refListQuestions = new ReferenceList(  );

        for ( IEntry entry : getAllQuestionList( nIdForm, plugin ) )
        {
            if ( entry.getTitle(  ) != null )
            {
                refListQuestions.addItem( entry.getIdEntry(  ), entry.getTitle(  ) );
            }
            else
            {
                refListQuestions.addItem( entry.getIdEntry(  ), entry.getComment(  ) );
            }
        }

        return refListQuestions;
    }

    /**
     * return the questions list
     * @param nIdForm the form id
     * @param plugin the plugin
     * @return  the questions list
     */
    public static List<IEntry> getAllQuestionList( int nIdForm, Plugin plugin )
    {
        List<IEntry> listEntry = new ArrayList<IEntry>(  );
        EntryFilter filter = new EntryFilter(  );
        filter.setIdForm( nIdForm );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setIdIsComment( EntryFilter.FILTER_FALSE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );

        for ( IEntry entryFirstLevel : EntryHome.getEntryList( filter, plugin ) )
        {
            if ( entryFirstLevel.getEntryType(  ).getGroup(  ) )
            {
                filter = new EntryFilter(  );
                filter.setIdForm( nIdForm );
                filter.setIdEntryParent( entryFirstLevel.getIdEntry(  ) );
                filter.setIdIsComment( EntryFilter.FILTER_FALSE );

                for ( IEntry entryChild : EntryHome.getEntryList( filter, plugin ) )
                {
                    listEntry.add( entryChild );
                    addConditionnalsEntry( entryChild, listEntry, plugin );
                }
            }
            else
            {
                listEntry.add( entryFirstLevel );
                addConditionnalsEntry( entryFirstLevel, listEntry, plugin );
            }
        }

        return listEntry;
    }

    /**
     * add children question of the root entryParent node
     * @param entryParent the parent entry
     * @param listEntry the entry list
     * @param plugin the plugin
     */
    private static void addConditionnalsEntry( IEntry entryParent, List<IEntry> listEntry, Plugin plugin )
    {
        entryParent = EntryHome.findByPrimaryKey( entryParent.getIdEntry(  ), plugin );

        for ( Field field : entryParent.getFields(  ) )
        {
            field = FieldHome.findByPrimaryKey( field.getIdField(  ), plugin );

            if ( field.getConditionalQuestions(  ) != null )
            {
                for ( IEntry entryConditionnal : field.getConditionalQuestions(  ) )
                {
                    listEntry.add( entryConditionnal );
                    addConditionnalsEntry( entryConditionnal, listEntry, plugin );
                }
            }
        }
    }

    /**
     * return  all entries of form
     * @param nIdForm the form id
     * @param plugin the plugin
     * @return  the all entries of form
     */
    public static List<IEntry> getEntriesList( int nIdForm, Plugin plugin )
    {
        List<IEntry> listEntry = new ArrayList<IEntry>(  );
        EntryFilter filter = new EntryFilter(  );
        filter.setIdForm( nIdForm );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );

        for ( IEntry entryFirstLevel : EntryHome.getEntryList( filter, plugin ) )
        {
            if ( entryFirstLevel.getEntryType(  ).getGroup(  ) )
            {
                filter = new EntryFilter(  );
                filter.setIdForm( nIdForm );
                filter.setIdEntryParent( entryFirstLevel.getIdEntry(  ) );

                List<IEntry> listEntryChild = new ArrayList<IEntry>(  );

                for ( IEntry entryChild : EntryHome.getEntryList( filter, plugin ) )
                {
                    listEntryChild.add( entryChild );
                    addConditionnalsEntry( entryChild, listEntryChild, plugin );
                }

                entryFirstLevel.setChildren( listEntryChild );
                listEntry.add( entryFirstLevel );
            }
            else
            {
                listEntry.add( entryFirstLevel );
                addConditionnalsEntry( entryFirstLevel, listEntry, plugin );
            }
        }

        return listEntry;
    }

    /**
     * Builds a query with filters placed in parameters
     * @param strSelect the select of the  query
     * @param listStrFilter the list of filter to add in the query
     * @param listStrGroupBy the list of group by to add in the query
     * @param strOrder the order by of the query
     * @return a query
     */
    public static String buildRequestWithFilter( String strSelect, List<String> listStrFilter,
        List<String> listStrGroupBy, String strOrder )
    {
        StringBuffer strBuffer = new StringBuffer(  );
        strBuffer.append( strSelect );

        int nCount = 0;

        for ( String strFilter : listStrFilter )
        {
            if ( ++nCount == 1 )
            {
                strBuffer.append( CONSTANT_WHERE );
            }

            strBuffer.append( strFilter );

            if ( nCount != listStrFilter.size(  ) )
            {
                strBuffer.append( CONSTANT_AND );
            }
        }

        if ( listStrGroupBy != null )
        {
            for ( String strGroupBy : listStrGroupBy )
            {
                strBuffer.append( strGroupBy );
            }
        }

        if ( strOrder != null )
        {
            strBuffer.append( strOrder );
        }

        return strBuffer.toString(  );
    }    
   
    /**
     * Get entry type mylutece user
     * @param plugin Plugin
     * @return entry type
     */
    public static EntryType getEntryTypeMyLuteceUser( Plugin plugin )
    {
    	for ( EntryType entryType : EntryTypeHome.getList( plugin ) )
		{
			if ( entryType.getClassName(  ).equals( fr.paris.lutece.plugins.form.business.EntryTypeMyLuteceUser.class.getName(  ) ) )
			{
				return entryType;
			}
		}
    	return null;
    }

    /**
     * Activate mylutece authentification for the form
     * @param form form
     * @param plugin Plugin
     * @param locale Locale
     * @param request HttpServletRequest
     */
    public static void activateMyLuteceAuthentification( Form form, Plugin plugin, Locale locale, HttpServletRequest request )
    {
		EntryType entryType = FormUtils.getEntryTypeMyLuteceUser( plugin );
		IEntry entry = null;
		try
        {
            entry = (IEntry) Class.forName( entryType.getClassName(  ) ).newInstance(  );
            entry.setEntryType( entryType );
        }
        catch ( ClassNotFoundException e )
        {
            //  class doesn't exist
            AppLogService.error( e );
        }
        catch ( InstantiationException e )
        {
            // Class is abstract or is an  interface or haven't accessible builder
            AppLogService.error( e );
        }
        catch ( IllegalAccessException e )
        {
            // can't access to rhe class
            AppLogService.error( e );
        }
		entry.getRequestData( request, locale );
        entry.setForm( form );
        entry.setIdEntry( EntryHome.create( entry, plugin ) );
        if ( entry.getFields(  ) != null )
        {
            for ( Field field : entry.getFields(  ) )
            {
                field.setParentEntry( entry );
                FieldHome.create( field, plugin );
            }
        }
    }
    
    /**
     * Deactivate mylutece authentification for the form
     * @param form Form
     * @param plugin Plugin
     */
    public static void deactivateMyLuteceAuthentification( Form form, Plugin plugin )
    {
    	EntryFilter entryFilter = new EntryFilter(  );
    	entryFilter.setIdForm( form.getIdForm(  ) );
    	List<IEntry> listEntries = EntryHome.getEntryList( entryFilter, plugin );
    	for ( IEntry entry : listEntries )
    	{
    		if ( entry instanceof fr.paris.lutece.plugins.form.business.EntryTypeMyLuteceUser )
    		{
    			EntryHome.remove( entry.getIdEntry(  ), plugin );
    			break;
    		}
    	}
    }
    
    /**
     * Restores submitted responses
     * @param session the session
     * @param mapResponses response list, key is entry id
     */
    public static void restoreResponses( HttpSession session, Map<Integer, List<Response>> mapResponses )
	{
		session.setAttribute( SESSION_FORM_LIST_SUBMITTED_RESPONSES, mapResponses );
	}
    
    /**
     * Removes submitted responses
     * @param session the session
     */
    public static void removeResponses( HttpSession session )
    {
    	session.removeAttribute( SESSION_FORM_LIST_SUBMITTED_RESPONSES );
    }
    
    /**
     * Gets the responses bound to the session
     * @param session the session
     * @return the responses if any, <code>null</code> otherwise.
     */
    @SuppressWarnings("unchecked")
	public static Map<Integer, List<Response>> getResponses( HttpSession session )
    {
    	return (Map<Integer, List<Response>>) session.getAttribute( SESSION_FORM_LIST_SUBMITTED_RESPONSES );
    }
    
    /**
     * convert a string to int
     *
     * @param strParameter
     *            the string parameter to convert
     * @return the conversion
     */
    public static int convertStringToInt( String strParameter )
    {
        int nIdParameter = -1;

        try
        {
            if ( ( strParameter != null ) && strParameter.matches( REGEX_ID ) )
            {
                nIdParameter = Integer.parseInt( strParameter );
            }
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        return nIdParameter;
    }
}
