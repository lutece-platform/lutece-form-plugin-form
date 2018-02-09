/*
 * FormHomeTest.java
 * JUnit based test
 *
 * Created on 20 novembre 2007, 10:06
 */
package fr.paris.lutece.plugins.form.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.test.LuteceTestCase;

import java.sql.Timestamp;

import java.util.Date;
import java.util.List;

/**
 * FormHomeTest
 */
public class FormHomeTest extends LuteceTestCase
{
    private final static int ID_1 = 1;
    private final static String TITLE_1 = "Title 1";
    private final static String TITLE_2 = "Title 2";
    private final static String DESCRIPTION_1 = "Description 1";
    private final static String DESCRIPTION_2 = "Description 2";
    private final static String WELCOME_MESSAGE_1 = "Welcome Message 1";
    private final static String WELCOME_MESSAGE_2 = "Welcome Message 2";
    private final static String UNAVAILABILITY_MESSAGE_1 = "Unavailability Message 1";
    private final static String UNAVAILABILITY_MESSAGE_2 = "Unavailability Message 2";
    private final static String REQUIREMENT_1 = "Requirement 1";
    private final static String REQUIREMENT_2 = "Requirement 2";
    private final static String WORKGROUP_1 = "Workgroup 1";
    private final static String WORKGROUP_2 = "Workgroup 2";
    private final static int ID_MAILING_LIST_1 = 1;
    private final static int ID_MAILING_LIST_2 = 2;
    private final static boolean ACTIVE_CAPTCHA_1 = true;
    private final static boolean ACTIVE_CAPTCHA_2 = false;
    private final static boolean ACTIVE_STORE_ADRESSE_1 = true;
    private final static boolean ACTIVE_STORE_ADRESSE_2 = false;
    private final static String VALIDATE_BUTTON_1 = "Button Validate 1";
    private final static String VALIDATE_BUTTON_2 = "Button Validate 2";
    private final static String RESET_BUTTON_1 = "Button Reset 1";
    private final static String RESET_BUTTON_2 = "Button Reset 2";
    private final static Date DATE_BEGIN_DISPONIBILITY_1 = new Date( );
    private final static Date DATE_BEGIN_DISPONIBILITY_2 = new Date( );
    private final static Date DATE_END_DISPONIBILITY_1 = new Date( );
    private final static Date DATE_END_DISPONIBILITY_2 = new Date( );
    private final static boolean AUTO_PUBLICATION_ACTIVE_1 = true;
    private final static boolean AUTO_PUBLICATION_ACTIVE_2 = false;
    private final static boolean ACTIVE_1 = true;
    private final static boolean ACTIVE_2 = false;
    private final static Timestamp DATE_CREATION_1 = new Timestamp( new Date( ).getTime( ) );
    private final static Timestamp DATE_CREATION_2 = new Timestamp( new Date( ).getTime( ) );
    private final static boolean LIMIT_NUMBER_RESPONSE_1 = true;
    private final static boolean LIMIT_NUMBER_RESPONSE_2 = false;
    private final Plugin _plugin = PluginService.getPlugin( "form" );

    public Form createForm( ) 
    {
        Form form = new Form( );
        form.setTitle( TITLE_1 );
        form.setDescription( DESCRIPTION_1 );
        form.setWelcomeMessage( WELCOME_MESSAGE_1 );
        form.setUnavailabilityMessage( UNAVAILABILITY_MESSAGE_1 );
        form.setRequirement( REQUIREMENT_1 );
        form.setWorkgroup( WORKGROUP_1 );
        form.setIdMailingList( ID_MAILING_LIST_1 );
        form.setActiveCaptcha( ACTIVE_CAPTCHA_1 );
        form.setActiveStoreAdresse( ACTIVE_STORE_ADRESSE_1 );
        form.setLibelleValidateButton( VALIDATE_BUTTON_1 );
        form.setLibelleResetButton( RESET_BUTTON_1 );
        form.setDateBeginDisponibility( DATE_BEGIN_DISPONIBILITY_1 );
        form.setDateEndDisponibility( DATE_END_DISPONIBILITY_1 );
        form.setActive( ACTIVE_1 );
        form.setAutoPublicationActive( AUTO_PUBLICATION_ACTIVE_1 );
        form.setDateCreation( DATE_CREATION_1 );
        form.setLimitNumberResponse( LIMIT_NUMBER_RESPONSE_1 );

        Recap recap = new Recap( );
        recap.setIdRecap( RecapHome.create( recap, _plugin ) );
        form.setRecap( recap );
        FormHome.create( form, _plugin );
        
        return form;
        
    }
    
    /**
     * Test of create method, of class fr.paris.lutece.plugins.form.business.FormHome.
     */
    public void testCreate( )
    {
        Form form = createForm( );

        Form storedForm = FormHome.findByPrimaryKey( form.getIdForm( ), _plugin );

        assertEquals( storedForm.getIdForm( ), form.getIdForm( ) );
        assertEquals( storedForm.getTitle( ), form.getTitle( ) );
        assertEquals( storedForm.getDescription( ), form.getDescription( ) );
        assertEquals( storedForm.getWelcomeMessage( ), form.getWelcomeMessage( ) );
        assertEquals( storedForm.getUnavailabilityMessage( ), form.getUnavailabilityMessage( ) );
        assertEquals( storedForm.getRequirement( ), form.getRequirement( ) );
        assertEquals( storedForm.getWorkgroup( ), form.getWorkgroup( ) );
        assertEquals( storedForm.getIdMailingList( ), form.getIdMailingList( ) );
        assertEquals( storedForm.isActiveCaptcha( ), form.isActiveCaptcha( ) );
        assertEquals( storedForm.isActiveStoreAdresse( ), form.isActiveStoreAdresse( ) );
        assertEquals( storedForm.getLibelleValidateButton( ), form.getLibelleValidateButton( ) );
        assertTrue( ( storedForm.getDateEndDisponibility( ).getTime( ) - form.getDateEndDisponibility( ).getTime( ) ) < 10 );
        assertEquals( storedForm.isAutoPublicationActive( ), form.isAutoPublicationActive( ) );
        assertEquals( storedForm.isActive( ), form.isActive( ) );

        assertEquals( storedForm.isLimitNumberResponse( ), form.isLimitNumberResponse( ) );

        assertEquals( storedForm.getRecap( ).getIdRecap( ), form.getRecap( ).getIdRecap( ) );
    }

    /**
     * Test of copy method, of class fr.paris.lutece.plugins.form.business.FormHome.
     */
    public void testCopy( )
    {
        Form loadForm = createForm( );

        FormHome.copy( loadForm, _plugin );

        FormDAO formDAO = new FormDAO( );
        int LastPrimaryKey = formDAO.newPrimaryKey( _plugin ) - 1;
        Form copyForm = FormHome.findByPrimaryKey( LastPrimaryKey, _plugin );

        assertEquals( copyForm.getTitle( ), loadForm.getTitle( ) );
        assertEquals( copyForm.getDescription( ), loadForm.getDescription( ) );
        assertEquals( copyForm.getWelcomeMessage( ), loadForm.getWelcomeMessage( ) );
        assertEquals( copyForm.getUnavailabilityMessage( ), loadForm.getUnavailabilityMessage( ) );
        assertEquals( copyForm.getRequirement( ), loadForm.getRequirement( ) );
        assertEquals( copyForm.getWorkgroup( ), loadForm.getWorkgroup( ) );
        assertEquals( copyForm.getIdMailingList( ), loadForm.getIdMailingList( ) );
        assertEquals( copyForm.isActiveCaptcha( ), loadForm.isActiveCaptcha( ) );
        assertEquals( copyForm.isActiveStoreAdresse( ), loadForm.isActiveStoreAdresse( ) );
        assertEquals( copyForm.getLibelleValidateButton( ), loadForm.getLibelleValidateButton( ) );
        assertEquals( copyForm.getLibelleResetButton( ), loadForm.getLibelleResetButton( ) );
        assertTrue( ( copyForm.getDateBeginDisponibility( ).getTime( ) - loadForm.getDateBeginDisponibility( ).getTime( ) ) < 10 );
        assertTrue( ( copyForm.getDateEndDisponibility( ).getTime( ) - loadForm.getDateEndDisponibility( ).getTime( ) ) < 10 );
        assertEquals( copyForm.isActive( ), loadForm.isActive( ) );
        assertEquals( copyForm.isLimitNumberResponse( ), loadForm.isLimitNumberResponse( ) );

        assertEquals( copyForm.getRecap( ).getIdRecap( ), loadForm.getRecap( ).getIdRecap( ) );
    }

    /**
     * Test of update method, of class fr.paris.lutece.plugins.form.business.FormHome.
     */
    public void testUpdate( )
    {
        
        Form form  = createForm( );
        
        form.setTitle( TITLE_2 );
        form.setDescription( DESCRIPTION_2 );
        form.setWelcomeMessage( WELCOME_MESSAGE_2 );
        form.setUnavailabilityMessage( UNAVAILABILITY_MESSAGE_2 );
        form.setRequirement( REQUIREMENT_2 );
        form.setWorkgroup( WORKGROUP_2 );
        form.setIdMailingList( ID_MAILING_LIST_2 );
        form.setActiveCaptcha( ACTIVE_CAPTCHA_2 );
        form.setActiveStoreAdresse( ACTIVE_STORE_ADRESSE_2 );
        form.setLibelleValidateButton( VALIDATE_BUTTON_2 );
        form.setLibelleValidateButton( RESET_BUTTON_2 );
        form.setDateBeginDisponibility( DATE_BEGIN_DISPONIBILITY_2 );
        form.setDateEndDisponibility( DATE_END_DISPONIBILITY_2 );
        form.setActive( ACTIVE_2 );
        form.setAutoPublicationActive( AUTO_PUBLICATION_ACTIVE_2 );
        form.setDateCreation( DATE_CREATION_2 );
        form.setLimitNumberResponse( LIMIT_NUMBER_RESPONSE_2 );

        Recap recap = new Recap( );
        recap.setIdRecap( form.getRecap( ).getIdRecap( ) );
        form.setRecap( recap );

        FormHome.update( form, _plugin );

        Form storedForm = FormHome.findByPrimaryKey( form.getIdForm( ), _plugin );

        assertEquals( storedForm.getIdForm( ), form.getIdForm( ) );
        assertEquals( storedForm.getTitle( ), form.getTitle( ) );
        assertEquals( storedForm.getDescription( ), form.getDescription( ) );
        assertEquals( storedForm.getWelcomeMessage( ), form.getWelcomeMessage( ) );
        assertEquals( storedForm.getUnavailabilityMessage( ), form.getUnavailabilityMessage( ) );
        assertEquals( storedForm.getRequirement( ), form.getRequirement( ) );
        assertEquals( storedForm.getWorkgroup( ), form.getWorkgroup( ) );
        assertEquals( storedForm.getIdMailingList( ), form.getIdMailingList( ) );
        assertEquals( storedForm.isActiveCaptcha( ), form.isActiveCaptcha( ) );
        assertEquals( storedForm.isActiveStoreAdresse( ), form.isActiveStoreAdresse( ) );
        assertEquals( storedForm.getLibelleValidateButton( ), form.getLibelleValidateButton( ) );
        assertTrue( ( storedForm.getDateBeginDisponibility( ).getTime( ) - form.getDateBeginDisponibility( ).getTime( ) ) < 10 );
        assertTrue( ( storedForm.getDateEndDisponibility( ).getTime( ) - form.getDateEndDisponibility( ).getTime( ) ) < 10 );
        assertEquals( storedForm.isAutoPublicationActive( ), form.isAutoPublicationActive( ) );
        assertEquals( storedForm.isActive( ), form.isActive( ) );

        assertEquals( storedForm.isLimitNumberResponse( ), form.isLimitNumberResponse( ) );

        assertEquals( storedForm.getRecap( ).getIdRecap( ), form.getRecap( ).getIdRecap( ) );
    }

    /**
     * Test of getFormList method, of class fr.paris.lutece.plugins.form.business.FormHome.
     */
    public void testGetFormList( )
    {
        List<Form> storedListForm = null;

        FormFilter formFilter = new FormFilter( );

        storedListForm = FormHome.getFormList( formFilter, _plugin );

        assertNotNull( storedListForm );
    }

    /**
     * Test of remove method, of class fr.paris.lutece.plugins.form.business.FormHome.
     */
    public void testRemove( )
    {

        Form form = createForm( );

        FormHome.remove( form.getIdForm( ), _plugin );

        Form formStored = FormHome.findByPrimaryKey( form.getIdForm( ), _plugin );
        assertNull( formStored );
    }
}
