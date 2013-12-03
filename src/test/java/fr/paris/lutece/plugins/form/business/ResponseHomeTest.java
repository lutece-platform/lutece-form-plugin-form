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

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseFilter;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.test.LuteceTestCase;

import java.util.List;


/**
 * Test class for responses
 */
public class ResponseHomeTest extends LuteceTestCase
{
    private final static int ID_RESPONSE_1 = 1;
    private final static int ID_FIELD_1 = 1;
    private final static int ID_FIELD_2 = 2;
    private final static int ID_ENTRY_1 = 1;
    private final static int ID_ENTRY_2 = 2;
    private final static int ID_FORM_RESPONSE_1 = 1;
    private final static int ID_FORM_RESPONSE_2 = 2;
    private final static String VALUE_RESPONSE_1 = "response value 1";
    private final static String VALUE_RESPONSE_2 = "response value 2";

    /**
     * Test the creation of a response
     */
    public void testCreate(  )
    {
        Response response = new Response(  );

        FieldHomeTest fieldHomeTest = new FieldHomeTest(  );
        fieldHomeTest.testCreate(  );

        Field field = new Field(  );
        field.setIdField( ID_FIELD_1 );

        EntryHomeTest entryHomeTest = new EntryHomeTest(  );
        entryHomeTest.testCreate(  );

        Entry entry = new Entry(  );
        entry.setIdEntry( ID_ENTRY_1 );

        FormSubmitHomeTest formSubmitTest = new FormSubmitHomeTest(  );
        formSubmitTest.testCreate(  );

        FormSubmit formResponse = new FormSubmit(  );
        formResponse.setIdFormSubmit( ID_FORM_RESPONSE_1 );

        response.setField( field );
        response.setEntry( entry );
        //        response.setFormSubmit( formResponse );
        response.setResponseValue( VALUE_RESPONSE_1 );

        ResponseHome.create( response );

        Response responseStored = ResponseHome.findByPrimaryKey( response.getIdResponse(  ) );

        //assertEquals( responseStored.getValueResponse(  ), response.getValueResponse(  ) );
        assertEquals( responseStored.getField(  ).getIdField(  ), response.getField(  ).getIdField(  ) );
        assertEquals( responseStored.getEntry(  ).getIdEntry(  ), response.getEntry(  ).getIdEntry(  ) );

        //        assertEquals( responseStored.getFormSubmit(  ).getIdFormSubmit(  ),
        //            response.getFormSubmit(  ).getIdFormSubmit(  ) );
    }

    /**
     * Test the update of a response
     */
    public void testUpdate(  )
    {
        Response responseLoad = ResponseHome.findByPrimaryKey( ID_RESPONSE_1 );

        Response response = new Response(  );

        response.setIdResponse( responseLoad.getIdResponse(  ) );

        Field field = new Field(  );
        field.setIdField( ID_FIELD_2 );

        Entry entry = new Entry(  );
        entry.setIdEntry( ID_ENTRY_2 );

        FormSubmit formResponse = new FormSubmit(  );
        formResponse.setIdFormSubmit( ID_FORM_RESPONSE_2 );

        response.setField( field );
        response.setEntry( entry );
        //        response.setFormSubmit( formResponse );
        response.setResponseValue( VALUE_RESPONSE_2 );

        ResponseHome.update( response );

        Response responseStored = ResponseHome.findByPrimaryKey( response.getIdResponse(  ) );

        //assertEquals( responseStored.getValueResponse(  ), response.getValueResponse(  ) );
        assertEquals( responseStored.getField(  ).getIdField(  ), response.getField(  ).getIdField(  ) );
        assertEquals( responseStored.getEntry(  ).getIdEntry(  ), response.getEntry(  ).getIdEntry(  ) );

        //        assertEquals( responseStored.getFormSubmit( ).getIdFormSubmit( ), response.getFormSubmit( ).getIdFormSubmit( ) );
    }

    public void testGetResponseList(  )
    {
        List<Response> listResponse = null;

        ResponseFilter responseFilter = new ResponseFilter(  );
        responseFilter.setGroupbyMonth( true );

        listResponse = ResponseHome.getResponseList( responseFilter );

        assertNotNull( listResponse );
    }

    /**
     * Test the removal of a response
     */
    public void testRemove(  )
    {
        Response responseLoad = ResponseHome.findByPrimaryKey( ID_RESPONSE_1 );

        ResponseHome.remove( responseLoad.getIdResponse(  ) );

        Response responseStored = ResponseHome.findByPrimaryKey( responseLoad.getIdResponse(  ) );

        assertNull( responseStored );
    }
}
