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
import fr.paris.lutece.plugins.genericattributes.business.ResponseFilter;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.test.LuteceTestCase;

import java.util.List;


/**
 * FormSubmitHomeTest
 */
public class FormSubmitHomeTest extends LuteceTestCase
{
    private final static int ID_FORM_RESPONSE_1 = 1;
    private final static String IP_1 = "Adresse ip 1";
    private final static String IP_2 = "Adresse ip 2";
    private final Plugin _plugin = PluginService.getPlugin( "form" );

    public void testCreate( )
    {
        FormSubmit formSubmit = new FormSubmit( );

        FormDAO formDAO = new FormDAO( );
        int LastPrimaryKey = formDAO.newPrimaryKey( _plugin ) - 1;
        Form form = FormHome.findByPrimaryKey( LastPrimaryKey, _plugin );

        formSubmit.setIdFormSubmit( ID_FORM_RESPONSE_1 );
        formSubmit.setIp( IP_1 );
        formSubmit.setForm( form );
        formSubmit.setDateResponse( FormUtils.getCurrentTimestamp( ) );
        FormSubmitHome.create( formSubmit, _plugin );

        FormSubmit storedFormSubmit = FormSubmitHome.findByPrimaryKey( formSubmit.getIdFormSubmit( ), _plugin );

        assertEquals( storedFormSubmit.getIdFormSubmit( ), formSubmit.getIdFormSubmit( ) );
        assertEquals( storedFormSubmit.getIp( ), formSubmit.getIp( ) );
        assertEquals( storedFormSubmit.getForm( ).getIdForm( ), formSubmit.getForm( ).getIdForm( ) );
    }

    public void testUpdate( )
    {
        FormSubmit loadformSubmit = FormSubmitHome.findByPrimaryKey( ID_FORM_RESPONSE_1, _plugin );

        FormSubmit formSubmit = new FormSubmit( );

        FormDAO formDAO = new FormDAO( );
        int LastPrimaryKey = formDAO.newPrimaryKey( _plugin ) - 1;
        Form form = FormHome.findByPrimaryKey( LastPrimaryKey, _plugin );

        formSubmit.setIdFormSubmit( loadformSubmit.getIdFormSubmit( ) );
        formSubmit.setIp( IP_2 );
        formSubmit.setForm( form );

        FormSubmitHome.update( formSubmit, _plugin );

        FormSubmit storedFormSubmit = FormSubmitHome.findByPrimaryKey( formSubmit.getIdFormSubmit( ), _plugin );

        assertEquals( storedFormSubmit.getIdFormSubmit( ), formSubmit.getIdFormSubmit( ) );
        assertEquals( storedFormSubmit.getIp( ), formSubmit.getIp( ) );
        assertEquals( storedFormSubmit.getForm( ).getIdForm( ), formSubmit.getForm( ).getIdForm( ) );
    }

    public void testGetFormSubmitList( )
    {
        List<FormSubmit> listFormSubmit = null;

        ResponseFilter responseFilter = new ResponseFilter( );

        listFormSubmit = FormSubmitHome.getFormSubmitList( responseFilter, _plugin );

        assertNotNull( listFormSubmit );
    }

    public void testGetCountFormSubmit( )
    {
        int nbFormSubmit;

        ResponseFilter responseFilter = new ResponseFilter( );

        nbFormSubmit = FormSubmitHome.getCountFormSubmit( responseFilter, _plugin );

        assertNotNull( nbFormSubmit );
    }

    public void testGetStatisticFormSubmit( )
    {
        List<StatisticFormSubmit> statisticFormSubmit = null;

        ResponseFilter responseFilter = new ResponseFilter( );
        responseFilter.setGroupbyDay( true );

        statisticFormSubmit = FormSubmitHome.getStatisticFormSubmit( responseFilter, _plugin );

        assertNotNull( statisticFormSubmit );
    }

    public void testRemove( )
    {
        FormSubmit formSubmit = new FormSubmit( );

        FormDAO formDAO = new FormDAO( );
        int LastPrimaryKey = formDAO.newPrimaryKey( _plugin ) - 1;
        Form form = FormHome.findByPrimaryKey( LastPrimaryKey, _plugin );

        formSubmit.setIdFormSubmit( ID_FORM_RESPONSE_1 );
        formSubmit.setIp( IP_1 );
        formSubmit.setForm( form );
        formSubmit.setDateResponse( FormUtils.getCurrentTimestamp( ) );

        int LastIdFormSubmit = FormSubmitHome.create( formSubmit, _plugin );

        FormSubmit loadformSubmit = FormSubmitHome.findByPrimaryKey( LastIdFormSubmit, _plugin );

        FormSubmitHome.remove( loadformSubmit.getIdFormSubmit( ), _plugin );

        FormSubmit formSubmitStored = FormSubmitHome.findByPrimaryKey( loadformSubmit.getIdFormSubmit( ), _plugin );

        assertNull( formSubmitStored );
    }
}
