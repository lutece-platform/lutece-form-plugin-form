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

import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.portal.business.style.Theme;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.portal.ThemesService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

import org.apache.commons.lang.StringUtils;

import java.sql.Date;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * class FormDAO
 */
public final class FormDAO implements IFormDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_form ) FROM form_form";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_form,title,front_office_title,is_shown_front_office_title,description, welcome_message," +
        "unavailability_message, requirement_message,workgroup," +
        "id_mailing_list,active_captcha,active_store_adresse," +
        "libelle_validate_button,libelle_reset_button,date_begin_disponibility,date_end_disponibility," +
        " active,auto_publication,date_creation,limit_number_response,id_recap,active_requirement,information_1," +
        " information_2,information_3,information_4,information_5, supports_https, code_theme, active_mylutece_authentification," +
        " id_category, automatic_cleaning, cleaning_by_removal, nb_days_before_cleaning" +
        " FROM form_form WHERE id_form = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO form_form ( id_form,title,front_office_title,is_shown_front_office_title,description, welcome_message," +
        "unavailability_message,requirement_message,workgroup," +
        "id_mailing_list,active_captcha,active_store_adresse," +
        "libelle_validate_button,libelle_reset_button,date_begin_disponibility," +
        " date_end_disponibility,active,auto_publication,date_creation,limit_number_response," +
        " id_recap,active_requirement,information_1,information_2,information_3,information_4,information_5, " +
        " supports_https, code_theme, active_mylutece_authentification, id_category, automatic_cleaning, cleaning_by_removal, nb_days_before_cleaning ) " +
        "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM form_form WHERE id_form = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE form_form SET id_form=?,title=?,front_office_title=?,is_shown_front_office_title=?,description=?, welcome_message=?," +
        "unavailability_message=?, requirement_message=?,workgroup=?," +
        "id_mailing_list=?,active_captcha=?,active_store_adresse=?," +
        "libelle_validate_button=?,libelle_reset_button=?,date_begin_disponibility=?,date_end_disponibility=?,active=?,auto_publication=?,limit_number_response=? ,active_requirement=?," +
        "information_1=? ,information_2=? ,information_3=? ,information_4=? ,information_5=?, supports_https = ?, code_theme = ?, " +
        "active_mylutece_authentification=? ,id_category=?, automatic_cleaning = ?, cleaning_by_removal = ?, nb_days_before_cleaning = ? WHERE id_form=?";
    private static final String SQL_QUERY_SELECT_FORM_BY_FILTER = "SELECT id_form,title,front_office_title,is_shown_front_office_title,description, welcome_message," +
        "unavailability_message, requirement_message,workgroup," +
        "id_mailing_list,active_captcha,active_store_adresse," +
        "libelle_validate_button,libelle_reset_button,date_begin_disponibility,date_end_disponibility,active," +
        " auto_publication,date_creation,limit_number_response,id_recap,active_requirement,information_1," +
        " information_2,information_3,information_4,information_5,supports_https, code_theme, active_mylutece_authentification, id_category, automatic_cleaning, cleaning_by_removal, nb_days_before_cleaning " +
        " FROM form_form ";
    private static final String SQL_QUERY_SELECT_ALL_THEMES = "SELECT id_form, code_theme FROM form_form";
    private static final String SQL_QUERY_FIND_ANONYMIZE_ENTRIES = " SELECT id_entry FROM form_anonymize_fields WHERE id_form = ? ";
    private static final String SQL_QUERY_REMOVE_ANONYMIZE_ENTRIES = " DELETE FROM form_anonymize_fields WHERE id_form = ? ";
    private static final String SQL_QUERY_INSERT_ANONYMIZE_ENTRIES = " INSERT INTO form_anonymize_fields( id_form, id_entry ) VALUES ( ?, ? ) ";
    private static final String SQL_QUERY_SELECT_BY_AUTOMATIC_CLEANING = " SELECT id_form, automatic_cleaning, cleaning_by_removal, nb_days_before_cleaning FROM form_form WHERE automatic_cleaning = 1 ";
    private static final String SQL_FILTER_OR = " OR ";
    private static final String SQL_FILTER_OPEN_PARENTHESIS = " ( ";
    private static final String SQL_FILTER_CLOSE_PARENTHESIS = " ) ";
    private static final String SQL_FILTER_WORKGROUP = " workgroup = ? ";
    private static final String SQL_FILTER_STATE = " active = ? ";
    private static final String SQL_FILTER_STATE_DAEMON = " auto_publication = ? ";
    private static final String SQL_FILTER_ID_CATEGORY = " id_category = ? ";
    private static final String SQL_FILTER_STATE_BEGIN_DISPONIBILTY_AFTER_CURRENT_DATE = " date_begin_disponibility > ? ";
    private static final String SQL_FILTER_STATE_END_DISPONIBILTY_BEFORE_CURRENT_DATE = " date_end_disponibility < ? ";
    private static final String SQL_ORDER_BY_DATE_CREATION = " ORDER BY date_creation ";
    private static final String SQL_ORDER_BY_TITLE = " ORDER BY title ";
    private static final String SQL_ASC = "ASC";
    private static final String SQL_DESC = "DESC";
    private static final String FILTER_TITLE = "title";
    private static final String FILTER_ASC = "asc";

    /**
     * Generates a new primary key
     *
     * @param plugin the plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery(  );

        int nKey;

        if ( !daoUtil.next(  ) )
        {
            // if the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;
        daoUtil.free(  );

        return nKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized int insert( Form form, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        form.setIdForm( newPrimaryKey( plugin ) );

        int nIndex = 1;
        daoUtil.setInt( nIndex++, form.getIdForm(  ) );
        daoUtil.setString( nIndex++, form.getTitle(  ) );
        daoUtil.setString( nIndex++, form.getFrontOfficeTitle(  ) );
        daoUtil.setBoolean( nIndex++, form.isShownFrontOfficeTitle(  ) );
        daoUtil.setString( nIndex++, form.getDescription(  ) );
        daoUtil.setString( nIndex++, form.getWelcomeMessage(  ) );
        daoUtil.setString( nIndex++, form.getUnavailabilityMessage(  ) );
        daoUtil.setString( nIndex++, form.getRequirement(  ) );
        daoUtil.setString( nIndex++, form.getWorkgroup(  ) );
        daoUtil.setInt( nIndex++, form.getIdMailingList(  ) );
        daoUtil.setBoolean( nIndex++, form.isActiveCaptcha(  ) );
        daoUtil.setBoolean( nIndex++, form.isActiveStoreAdresse(  ) );
        daoUtil.setString( nIndex++, form.getLibelleValidateButton(  ) );
        daoUtil.setString( nIndex++, form.getLibelleResetButton(  ) );
        daoUtil.setDate( nIndex++,
            ( form.getDateBeginDisponibility(  ) != null ) ? new Date( form.getDateBeginDisponibility(  ).getTime(  ) )
                                                           : null );
        daoUtil.setDate( nIndex++,
            ( form.getDateEndDisponibility(  ) != null ) ? new Date( form.getDateEndDisponibility(  ).getTime(  ) ) : null );
        daoUtil.setBoolean( nIndex++, form.isActive(  ) );
        daoUtil.setBoolean( nIndex++, form.isAutoPublicationActive(  ) );
        daoUtil.setTimestamp( nIndex++, form.getDateCreation(  ) );
        daoUtil.setBoolean( nIndex++, form.isLimitNumberResponse(  ) );
        daoUtil.setInt( nIndex++, form.getRecap(  ).getIdRecap(  ) );
        daoUtil.setBoolean( nIndex++, form.isActiveRequirement(  ) );
        daoUtil.setString( nIndex++, form.getInfoComplementary1(  ) );
        daoUtil.setString( nIndex++, form.getInfoComplementary2(  ) );
        daoUtil.setString( nIndex++, form.getInfoComplementary3(  ) );
        daoUtil.setString( nIndex++, form.getInfoComplementary4(  ) );
        daoUtil.setString( nIndex++, form.getInfoComplementary5(  ) );
        daoUtil.setBoolean( nIndex++, form.isSupportHTTPS(  ) );
        daoUtil.setString( nIndex++, form.getCodeTheme(  ) );
        daoUtil.setBoolean( nIndex++, form.isActiveMyLuteceAuthentification(  ) );

        if ( form.getCategory(  ) != null )
        {
            daoUtil.setInt( nIndex++, form.getCategory(  ).getIdCategory(  ) );
        }
        else
        {
            daoUtil.setIntNull( nIndex++ );
        }

        daoUtil.setBoolean( nIndex++, form.getAutomaticCleaning(  ) );
        daoUtil.setBoolean( nIndex++, form.getCleaningByRemoval(  ) );
        daoUtil.setInt( nIndex++, form.getNbDaysBeforeCleaning(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );

        return form.getIdForm(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Form load( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery(  );

        int nIndex = 1;
        Recap recap = null;
        Form form = null;

        if ( daoUtil.next(  ) )
        {
            form = new Form(  );
            form.setIdForm( daoUtil.getInt( nIndex++ ) );
            form.setTitle( daoUtil.getString( nIndex++ ) );
            form.setFrontOfficeTitle( daoUtil.getString( nIndex++ ) );
            form.setIsShownFrontOfficeTitle( daoUtil.getBoolean( nIndex++ ) );
            form.setDescription( daoUtil.getString( nIndex++ ) );
            form.setWelcomeMessage( daoUtil.getString( nIndex++ ) );
            form.setUnavailabilityMessage( daoUtil.getString( nIndex++ ) );
            form.setRequirement( daoUtil.getString( nIndex++ ) );
            form.setWorkgroup( daoUtil.getString( nIndex++ ) );
            form.setIdMailingList( daoUtil.getInt( nIndex++ ) );
            form.setActiveCaptcha( daoUtil.getBoolean( nIndex++ ) );
            form.setActiveStoreAdresse( daoUtil.getBoolean( nIndex++ ) );
            form.setLibelleValidateButton( daoUtil.getString( nIndex++ ) );
            form.setLibelleResetButton( daoUtil.getString( nIndex++ ) );
            form.setDateBeginDisponibility( daoUtil.getDate( nIndex++ ) );
            form.setDateEndDisponibility( daoUtil.getDate( nIndex++ ) );
            form.setActive( daoUtil.getBoolean( nIndex++ ) );
            form.setAutoPublicationActive( daoUtil.getBoolean( nIndex++ ) );
            form.setDateCreation( daoUtil.getTimestamp( nIndex++ ) );
            form.setLimitNumberResponse( daoUtil.getBoolean( nIndex++ ) );
            recap = new Recap(  );
            recap.setIdRecap( daoUtil.getInt( nIndex++ ) );
            form.setRecap( recap );
            form.setActiveRequirement( daoUtil.getBoolean( nIndex++ ) );
            form.setInfoComplementary1( daoUtil.getString( nIndex++ ) );
            form.setInfoComplementary2( daoUtil.getString( nIndex++ ) );
            form.setInfoComplementary3( daoUtil.getString( nIndex++ ) );
            form.setInfoComplementary4( daoUtil.getString( nIndex++ ) );
            form.setInfoComplementary5( daoUtil.getString( nIndex++ ) );
            form.setSupportHTTPS( daoUtil.getBoolean( nIndex++ ) );
            form.setCodeTheme( daoUtil.getString( nIndex++ ) );
            form.setActiveMyLuteceAuthentification( daoUtil.getBoolean( nIndex++ ) );

            int i = nIndex++;

            if ( daoUtil.getObject( i ) != null )
            {
                form.setCategory( CategoryHome.findByPrimaryKey( daoUtil.getInt( i ), plugin ) );
            }

            form.setAutomaticCleaning( daoUtil.getBoolean( nIndex++ ) );
            form.setCleaningByRemoval( daoUtil.getBoolean( nIndex++ ) );
            form.setNbDaysBeforeCleaning( daoUtil.getInt( nIndex++ ) );
        }

        daoUtil.free(  );

        return form;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( Form form, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        int nIndex = 1;
        daoUtil.setInt( nIndex++, form.getIdForm(  ) );
        daoUtil.setString( nIndex++, form.getTitle(  ) );
        daoUtil.setString( nIndex++, form.getFrontOfficeTitle(  ) );
        daoUtil.setBoolean( nIndex++, form.isShownFrontOfficeTitle(  ) );
        daoUtil.setString( nIndex++, form.getDescription(  ) );
        daoUtil.setString( nIndex++, form.getWelcomeMessage(  ) );
        daoUtil.setString( nIndex++, form.getUnavailabilityMessage(  ) );
        daoUtil.setString( nIndex++, form.getRequirement(  ) );
        daoUtil.setString( nIndex++, form.getWorkgroup(  ) );
        daoUtil.setInt( nIndex++, form.getIdMailingList(  ) );
        daoUtil.setBoolean( nIndex++, form.isActiveCaptcha(  ) );
        daoUtil.setBoolean( nIndex++, form.isActiveStoreAdresse(  ) );
        daoUtil.setString( nIndex++, form.getLibelleValidateButton(  ) );
        daoUtil.setString( nIndex++, form.getLibelleResetButton(  ) );
        daoUtil.setDate( nIndex++,
            ( form.getDateBeginDisponibility(  ) != null ) ? new Date( form.getDateBeginDisponibility(  ).getTime(  ) )
                                                           : null );
        daoUtil.setDate( nIndex++,
            ( form.getDateEndDisponibility(  ) != null ) ? new Date( form.getDateEndDisponibility(  ).getTime(  ) ) : null );
        daoUtil.setBoolean( nIndex++, form.isActive(  ) );
        daoUtil.setBoolean( nIndex++, form.isAutoPublicationActive(  ) );
        daoUtil.setBoolean( nIndex++, form.isLimitNumberResponse(  ) );
        daoUtil.setBoolean( nIndex++, form.isActiveRequirement(  ) );
        daoUtil.setString( nIndex++, form.getInfoComplementary1(  ) );
        daoUtil.setString( nIndex++, form.getInfoComplementary2(  ) );
        daoUtil.setString( nIndex++, form.getInfoComplementary3(  ) );
        daoUtil.setString( nIndex++, form.getInfoComplementary4(  ) );
        daoUtil.setString( nIndex++, form.getInfoComplementary5(  ) );
        daoUtil.setBoolean( nIndex++, form.isSupportHTTPS(  ) );
        daoUtil.setString( nIndex++, form.getCodeTheme(  ) );
        daoUtil.setBoolean( nIndex++, form.isActiveMyLuteceAuthentification(  ) );

        if ( form.getCategory(  ) != null )
        {
            daoUtil.setInt( nIndex++, form.getCategory(  ).getIdCategory(  ) );
        }
        else
        {
            daoUtil.setIntNull( nIndex++ );
        }

        daoUtil.setBoolean( nIndex++, form.getAutomaticCleaning(  ) );
        daoUtil.setBoolean( nIndex++, form.getCleaningByRemoval(  ) );
        daoUtil.setInt( nIndex++, form.getNbDaysBeforeCleaning(  ) );

        daoUtil.setInt( nIndex++, form.getIdForm(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Form> selectFormList( FormFilter filter, Plugin plugin )
    {
        List<Form> formList = new ArrayList<Form>(  );
        Form form = null;
        Recap recap = null;
        List<String> listStrFilter = new ArrayList<String>(  );

        if ( filter.containsWorkgroupCriteria(  ) )
        {
            listStrFilter.add( SQL_FILTER_WORKGROUP );
        }

        if ( filter.containsIdState(  ) )
        {
            listStrFilter.add( SQL_FILTER_STATE );
        }

        if ( filter.containsIdAutoPublication(  ) )
        {
            listStrFilter.add( SQL_FILTER_STATE_DAEMON );
        }

        if ( filter.containsDateBeginDisponibilityAfterCurrentDate(  ) &&
                filter.containsDateEndDisponibilityBeforeCurrentDate(  ) )
        {
            listStrFilter.add( SQL_FILTER_OPEN_PARENTHESIS + SQL_FILTER_STATE_BEGIN_DISPONIBILTY_AFTER_CURRENT_DATE +
                SQL_FILTER_OR + SQL_FILTER_STATE_END_DISPONIBILTY_BEFORE_CURRENT_DATE + SQL_FILTER_CLOSE_PARENTHESIS );
        }
        else
        {
            if ( filter.containsDateBeginDisponibilityAfterCurrentDate(  ) )
            {
                listStrFilter.add( SQL_FILTER_STATE_BEGIN_DISPONIBILTY_AFTER_CURRENT_DATE );
            }

            if ( filter.containsDateEndDisponibilityBeforeCurrentDate(  ) )
            {
                listStrFilter.add( SQL_FILTER_STATE_END_DISPONIBILTY_BEFORE_CURRENT_DATE );
            }
        }

        if ( filter.containsIdCategory(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_CATEGORY );
        }

        // By default, order by date
        String strOrder = SQL_ORDER_BY_DATE_CREATION;

        if ( StringUtils.isNotBlank( filter.getOrder(  ) ) )
        {
            if ( FILTER_TITLE.equals( filter.getOrder(  ) ) )
            {
                strOrder = SQL_ORDER_BY_TITLE;
            }
        }

        String strAsc = SQL_DESC;

        if ( StringUtils.isNotBlank( filter.getAsc(  ) ) )
        {
            if ( FILTER_ASC.equals( filter.getAsc(  ) ) )
            {
                strAsc = SQL_ASC;
            }
        }

        String strSQL = FormUtils.buildRequestWithFilter( SQL_QUERY_SELECT_FORM_BY_FILTER, listStrFilter, null,
                strOrder + strAsc );
        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        if ( filter.containsWorkgroupCriteria(  ) )
        {
            daoUtil.setString( nIndex, filter.getWorkgroup(  ) );
            nIndex++;
        }

        if ( filter.containsIdState(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdState(  ) );
            nIndex++;
        }

        if ( filter.containsIdAutoPublication(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdAutoPublicationState(  ) );
            nIndex++;
        }

        if ( filter.containsDateBeginDisponibilityAfterCurrentDate(  ) )
        {
            daoUtil.setDate( nIndex, new Date( new java.util.Date(  ).getTime(  ) ) );
            nIndex++;
        }

        if ( filter.containsDateEndDisponibilityBeforeCurrentDate(  ) )
        {
            daoUtil.setDate( nIndex, new Date( new java.util.Date(  ).getTime(  ) ) );
            nIndex++;
        }

        if ( filter.containsIdCategory(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdCategory(  ) );
            nIndex++;
        }

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            nIndex = 1;
            form = new Form(  );
            form.setIdForm( daoUtil.getInt( nIndex++ ) );
            form.setTitle( daoUtil.getString( nIndex++ ) );
            form.setFrontOfficeTitle( daoUtil.getString( nIndex++ ) );
            form.setIsShownFrontOfficeTitle( daoUtil.getBoolean( nIndex++ ) );
            form.setDescription( daoUtil.getString( nIndex++ ) );
            form.setWelcomeMessage( daoUtil.getString( nIndex++ ) );
            form.setUnavailabilityMessage( daoUtil.getString( nIndex++ ) );
            form.setRequirement( daoUtil.getString( nIndex++ ) );
            form.setWorkgroup( daoUtil.getString( nIndex++ ) );
            form.setIdMailingList( daoUtil.getInt( nIndex++ ) );
            form.setActiveCaptcha( daoUtil.getBoolean( nIndex++ ) );
            form.setActiveStoreAdresse( daoUtil.getBoolean( nIndex++ ) );
            form.setLibelleValidateButton( daoUtil.getString( nIndex++ ) );
            form.setLibelleResetButton( daoUtil.getString( nIndex++ ) );
            form.setDateBeginDisponibility( daoUtil.getDate( nIndex++ ) );
            form.setDateEndDisponibility( daoUtil.getDate( nIndex++ ) );
            form.setActive( daoUtil.getBoolean( nIndex++ ) );
            form.setAutoPublicationActive( daoUtil.getBoolean( nIndex++ ) );
            form.setDateCreation( daoUtil.getTimestamp( nIndex++ ) );
            form.setLimitNumberResponse( daoUtil.getBoolean( nIndex++ ) );
            recap = new Recap(  );
            recap.setIdRecap( daoUtil.getInt( nIndex++ ) );
            form.setRecap( recap );
            form.setActiveRequirement( daoUtil.getBoolean( nIndex++ ) );
            form.setInfoComplementary1( daoUtil.getString( nIndex++ ) );
            form.setInfoComplementary2( daoUtil.getString( nIndex++ ) );
            form.setInfoComplementary3( daoUtil.getString( nIndex++ ) );
            form.setInfoComplementary4( daoUtil.getString( nIndex++ ) );
            form.setInfoComplementary5( daoUtil.getString( nIndex++ ) );
            form.setSupportHTTPS( daoUtil.getBoolean( nIndex++ ) );
            form.setCodeTheme( daoUtil.getString( nIndex++ ) );
            form.setActiveMyLuteceAuthentification( daoUtil.getBoolean( nIndex++ ) );

            int i = nIndex++;

            if ( daoUtil.getObject( i ) != null )
            {
                form.setCategory( CategoryHome.findByPrimaryKey( daoUtil.getInt( i ), plugin ) );
            }

            form.setAutomaticCleaning( daoUtil.getBoolean( nIndex++ ) );
            form.setCleaningByRemoval( daoUtil.getBoolean( nIndex++ ) );
            form.setNbDaysBeforeCleaning( daoUtil.getInt( nIndex++ ) );

            formList.add( form );
        }

        daoUtil.free(  );

        return formList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceList getEnableFormList( Plugin plugin )
    {
        ReferenceList listForm = new ReferenceList(  );
        String strSQL = SQL_QUERY_SELECT_FORM_BY_FILTER + SQL_ORDER_BY_DATE_CREATION;
        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        daoUtil.executeQuery(  );

        Form form;

        while ( daoUtil.next(  ) )
        {
            form = new Form(  );

            int nIndex = 1;
            form.setIdForm( daoUtil.getInt( nIndex++ ) );
            form.setTitle( daoUtil.getString( nIndex++ ) );
            listForm.addItem( form.getIdForm(  ), form.getTitle(  ) );
        }

        daoUtil.free(  );

        return listForm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Integer, Theme> getXPageThemesMap( Plugin plugin )
    {
        Map<Integer, Theme> xPageThemesMap = new HashMap<Integer, Theme>(  );

        String strSQL = SQL_QUERY_SELECT_ALL_THEMES;
        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            int nIndex = 1;
            int nIdForm = daoUtil.getInt( nIndex++ );
            String strCodeTheme = daoUtil.getString( nIndex++ );
            Theme theme = ThemesService.getGlobalTheme( strCodeTheme );
            xPageThemesMap.put( nIdForm, theme );
        }

        daoUtil.free(  );

        return xPageThemesMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getAnonymizeEntryList( int nIdForm, Plugin plugin )
    {
        List<Integer> listAnonymizeEntries = new ArrayList<Integer>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_ANONYMIZE_ENTRIES, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            listAnonymizeEntries.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return listAnonymizeEntries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertAnonymizeEntry( int nIdForm, int nIdEntry, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_ANONYMIZE_ENTRIES, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.setInt( 2, nIdEntry );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAnonymizeEntry( int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_REMOVE_ANONYMIZE_ENTRIES, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Form> getFormListForAutomaticCleaning( Plugin plugin )
    {
        List<Form> listForms = new ArrayList<Form>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_AUTOMATIC_CLEANING );
        daoUtil.executeQuery(  );

        int nIndex = 1;

        while ( daoUtil.next(  ) )
        {
            Form form = new Form(  );
            form.setIdForm( daoUtil.getInt( nIndex++ ) );
            form.setAutomaticCleaning( daoUtil.getBoolean( nIndex++ ) );
            form.setCleaningByRemoval( daoUtil.getBoolean( nIndex++ ) );
            form.setNbDaysBeforeCleaning( daoUtil.getInt( nIndex++ ) );
            listForms.add( form );
        }

        daoUtil.free(  );

        return listForms;
    }

    private String getOrderBy( FormFilter filter )
    {
        return "";
    }
}
