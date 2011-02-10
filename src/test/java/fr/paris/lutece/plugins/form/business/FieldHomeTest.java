/**
 *
 */
package fr.paris.lutece.plugins.form.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.test.LuteceTestCase;

import java.sql.Timestamp;

import java.util.Date;
import java.util.List;


/**

 */
public class FieldHomeTest extends LuteceTestCase
{
    private final static int ID_FIELD_1 = 1;
    private final static int ID_ENTRY_1 = 1;
    private final static int ID_ENTRY_2 = 2;
    private final static int ID_EXPRESSION_1 = 1;
    private final static int ID_EXPRESSION_2 = 2;
    private final static int HEIGHT_1 = 1;
    private final static int HEIGHT_2 = 2;
    private final static int WIDTH_1 = 1;
    private final static int WIDTH_2 = 2;
    private final static String TYTLE_1 = "title 1";
    private final static String TYTLE_2 = "title 2";
    private final static String VALUE_1 = "value 1";
    private final static String VALUE_2 = "value 2";
    private final static boolean DEFAULT_VALUE_1 = true;
    private final static boolean DEFAULT_VALUE_2 = false;
    private final static int MAX_SIZE_1 = 10;
    private final static int MAX_SIZE_2 = 20;
    private final static String VALUE_RESPONSE_1 = "response value 1";
    private final static String VALUE_RESPONSE_2 = "response value 2";
    private final static Timestamp VALUE_TYPE_DATE_1 = new Timestamp( new Date(  ).getTime(  ) );
    private final static Timestamp VALUE_TYPE_DATE_2 = new Timestamp( new Date(  ).getTime(  ) );
    private final Plugin _plugin = PluginService.getPlugin( "form" );

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.business.FieldHome#create(fr.paris.lutece.plugins.form.business.Field, fr.paris.lutece.portal.service.plugin.Plugin)}.
     */
    public void testCreate(  )
    {
        EntryHomeTest entryHomeTest = new EntryHomeTest(  );
        entryHomeTest.testCreate(  );

        Field field = new Field(  );

        IEntry entry = EntryHome.findByPrimaryKey( ID_ENTRY_1, _plugin );

        field.setParentEntry( entry );
        field.setTitle( TYTLE_1 );
        field.setValue( VALUE_1 );
        field.setHeight( HEIGHT_1 );
        field.setWidth( WIDTH_1 );
        field.setDefaultValue( DEFAULT_VALUE_1 );
        field.setMaxSizeEnter( MAX_SIZE_1 );
        field.setValueTypeDate( VALUE_TYPE_DATE_1 );

        FieldHome.create( field, _plugin );

        Field fieldStored = FieldHome.findByPrimaryKey( field.getIdField(  ), _plugin );

        assertEquals( fieldStored.getParentEntry(  ).getIdEntry(  ), field.getParentEntry(  ).getIdEntry(  ) );
        assertEquals( fieldStored.getTitle(  ), field.getTitle(  ) );
        assertEquals( fieldStored.getValue(  ), field.getValue(  ) );
        assertEquals( fieldStored.getHeight(  ), field.getHeight(  ) );
        assertEquals( fieldStored.getWidth(  ), field.getWidth(  ) );
        assertEquals( fieldStored.isDefaultValue(  ), field.isDefaultValue(  ) );
        assertTrue( ( fieldStored.getValueTypeDate(  ).getTime(  ) - field.getValueTypeDate(  ).getTime(  ) ) < 10 );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.business.FieldHome#update(fr.paris.lutece.plugins.form.business.Field, fr.paris.lutece.portal.service.plugin.Plugin)}.
     */
    public void testUpdate(  )
    {
        Field fieldLoad = FieldHome.findByPrimaryKey( ID_FIELD_1, _plugin );

        Field field = new Field(  );

        IEntry entry = EntryHome.findByPrimaryKey( ID_ENTRY_1, _plugin );

        field.setIdField( fieldLoad.getIdField(  ) );
        field.setParentEntry( entry );
        field.setTitle( TYTLE_2 );
        field.setValue( VALUE_2 );
        field.setHeight( HEIGHT_1 );
        field.setWidth( WIDTH_2 );
        field.setDefaultValue( DEFAULT_VALUE_2 );
        field.setMaxSizeEnter( MAX_SIZE_2 );
        field.setValueTypeDate( VALUE_TYPE_DATE_2 );

        FieldHome.update( field, _plugin );

        Field fieldStored = FieldHome.findByPrimaryKey( field.getIdField(  ), _plugin );

        assertEquals( fieldStored.getParentEntry(  ).getIdEntry(  ), field.getParentEntry(  ).getIdEntry(  ) );
        assertEquals( fieldStored.getTitle(  ), field.getTitle(  ) );
        assertEquals( fieldStored.getValue(  ), field.getValue(  ) );
        assertEquals( fieldStored.getHeight(  ), field.getHeight(  ) );
        assertEquals( fieldStored.getWidth(  ), field.getWidth(  ) );
        assertEquals( fieldStored.isDefaultValue(  ), field.isDefaultValue(  ) );
        assertTrue( ( fieldStored.getValueTypeDate(  ).getTime(  ) - field.getValueTypeDate(  ).getTime(  ) ) < 10 );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.business.FieldHome#getFieldListByIdEntry(int, fr.paris.lutece.portal.service.plugin.Plugin)}.
     */
    public void testGetFieldListByIdEntry(  )
    {
        List<Field> listField = null;

        listField = FieldHome.getFieldListByIdEntry( ID_ENTRY_1, _plugin );

        assertNotNull( listField );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.business.FieldHome#remove(int, fr.paris.lutece.portal.service.plugin.Plugin)}.
     */
    public void testRemove(  )
    {
        Field field = new Field(  );

        IEntry entry = EntryHome.findByPrimaryKey( ID_ENTRY_1, _plugin );

        field.setParentEntry( entry );
        field.setTitle( TYTLE_1 );
        field.setValue( VALUE_1 );
        field.setHeight( HEIGHT_1 );
        field.setWidth( WIDTH_1 );
        field.setDefaultValue( DEFAULT_VALUE_1 );
        field.setMaxSizeEnter( MAX_SIZE_1 );
        field.setValueTypeDate( VALUE_TYPE_DATE_1 );

        int lastIdField = FieldHome.create( field, _plugin );

        Field fieldLoad = FieldHome.findByPrimaryKey( lastIdField, _plugin );

        FieldHome.remove( fieldLoad.getIdField(  ), _plugin );

        Field fieldStored = FieldHome.findByPrimaryKey( fieldLoad.getIdField(  ), _plugin );

        assertNull( fieldStored );
    }
}
