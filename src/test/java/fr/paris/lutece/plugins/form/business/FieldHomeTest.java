/**
 *
 */
package fr.paris.lutece.plugins.form.business;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.test.LuteceTestCase;

import java.sql.Timestamp;

import java.util.Date;
import java.util.List;


/**
 * FieldHomeTest
 */
public class FieldHomeTest extends LuteceTestCase
{
    private final static int ID_FIELD_1 = 1;
    private final static int ID_ENTRY_1 = 1;
    private final static int HEIGHT_1 = 1;
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
    private final static Timestamp VALUE_TYPE_DATE_1 = new Timestamp( new Date(  ).getTime(  ) );
    private final static Timestamp VALUE_TYPE_DATE_2 = new Timestamp( new Date(  ).getTime(  ) );

    /**
     * Test method for
     * {@link fr.paris.lutece.plugins.genericattributes.business.FieldHome#create(fr.paris.lutece.plugins.genericattributes.business.Field)}
     * .
     */
    public void testCreate(  )
    {
        EntryHomeTest entryHomeTest = new EntryHomeTest(  );
        entryHomeTest.testCreate(  );

        Field field = new Field(  );

        Entry entry = EntryHome.findByPrimaryKey( ID_ENTRY_1 );

        field.setParentEntry( entry );
        field.setTitle( TYTLE_1 );
        field.setValue( VALUE_1 );
        field.setHeight( HEIGHT_1 );
        field.setWidth( WIDTH_1 );
        field.setDefaultValue( DEFAULT_VALUE_1 );
        field.setMaxSizeEnter( MAX_SIZE_1 );
        field.setValueTypeDate( VALUE_TYPE_DATE_1 );

        FieldHome.create( field );

        Field fieldStored = FieldHome.findByPrimaryKey( field.getIdField(  ) );

        assertEquals( fieldStored.getParentEntry(  ).getIdEntry(  ), field.getParentEntry(  ).getIdEntry(  ) );
        assertEquals( fieldStored.getTitle(  ), field.getTitle(  ) );
        assertEquals( fieldStored.getValue(  ), field.getValue(  ) );
        assertEquals( fieldStored.getHeight(  ), field.getHeight(  ) );
        assertEquals( fieldStored.getWidth(  ), field.getWidth(  ) );
        assertEquals( fieldStored.isDefaultValue(  ), field.isDefaultValue(  ) );
        assertTrue( ( fieldStored.getValueTypeDate(  ).getTime(  ) - field.getValueTypeDate(  ).getTime(  ) ) < 10 );
    }

    /**
     * Test method for
     * {@link fr.paris.lutece.plugins.genericattributes.business.FieldHome#update(fr.paris.lutece.plugins.genericattributes.business.Field)}
     * .
     */
    public void testUpdate(  )
    {
        Field fieldLoad = FieldHome.findByPrimaryKey( ID_FIELD_1 );

        Field field = new Field(  );

        Entry entry = EntryHome.findByPrimaryKey( ID_ENTRY_1 );

        field.setIdField( fieldLoad.getIdField(  ) );
        field.setParentEntry( entry );
        field.setTitle( TYTLE_2 );
        field.setValue( VALUE_2 );
        field.setHeight( HEIGHT_1 );
        field.setWidth( WIDTH_2 );
        field.setDefaultValue( DEFAULT_VALUE_2 );
        field.setMaxSizeEnter( MAX_SIZE_2 );
        field.setValueTypeDate( VALUE_TYPE_DATE_2 );

        FieldHome.update( field );

        Field fieldStored = FieldHome.findByPrimaryKey( field.getIdField(  ) );

        assertEquals( fieldStored.getParentEntry(  ).getIdEntry(  ), field.getParentEntry(  ).getIdEntry(  ) );
        assertEquals( fieldStored.getTitle(  ), field.getTitle(  ) );
        assertEquals( fieldStored.getValue(  ), field.getValue(  ) );
        assertEquals( fieldStored.getHeight(  ), field.getHeight(  ) );
        assertEquals( fieldStored.getWidth(  ), field.getWidth(  ) );
        assertEquals( fieldStored.isDefaultValue(  ), field.isDefaultValue(  ) );
        assertTrue( ( fieldStored.getValueTypeDate(  ).getTime(  ) - field.getValueTypeDate(  ).getTime(  ) ) < 10 );
    }

    /**
     * Test method for
     * {@link fr.paris.lutece.plugins.genericattributes.business.FieldHome#getFieldListByIdEntry(int)}
     * .
     */
    public void testGetFieldListByIdEntry(  )
    {
        List<Field> listField = null;

        listField = FieldHome.getFieldListByIdEntry( ID_ENTRY_1 );

        assertNotNull( listField );
    }

    /**
     * Test method for
     * {@link fr.paris.lutece.plugins.genericattributes.business.FieldHome#remove(int)}
     * .
     */
    public void testRemove(  )
    {
        Field field = new Field(  );

        Entry entry = EntryHome.findByPrimaryKey( ID_ENTRY_1 );

        field.setParentEntry( entry );
        field.setTitle( TYTLE_1 );
        field.setValue( VALUE_1 );
        field.setHeight( HEIGHT_1 );
        field.setWidth( WIDTH_1 );
        field.setDefaultValue( DEFAULT_VALUE_1 );
        field.setMaxSizeEnter( MAX_SIZE_1 );
        field.setValueTypeDate( VALUE_TYPE_DATE_1 );

        int lastIdField = FieldHome.create( field );

        Field fieldLoad = FieldHome.findByPrimaryKey( lastIdField );

        FieldHome.remove( fieldLoad.getIdField(  ) );

        Field fieldStored = FieldHome.findByPrimaryKey( fieldLoad.getIdField(  ) );

        assertNull( fieldStored );
    }
}
