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
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.test.LuteceTestCase;

import java.util.List;


public class EntryHomeTest extends LuteceTestCase
{
    private final static String TITLE_1 = "Title 1";
    private final static String HELP_MESSAGE_1 = "Help Message 1";
    private final static String COMMENT_1 = "Comment 1";
    private final static String COMMENT_2 = "Comment 2";
    private final static boolean MANDATORY_1 = true;
    private final static boolean FIELD_IN_LINE_1 = true;
    private final static int ID_ENTRY_1 = 1;
    private final static int ID_ENTRY_TYPE_1 = 1;
    private final Plugin _plugin = PluginService.getPlugin( "form" );

    public void testCreate(  )
    {
        FormHomeTest formHomeTest = new FormHomeTest(  );
        formHomeTest.testCreate(  );

        Entry entry = new Entry(  );

        EntryType entryType = new EntryType(  );
        entryType.setIdType( ID_ENTRY_TYPE_1 );

        FormDAO formDAO = new FormDAO(  );
        int LastPrimaryKey = formDAO.newPrimaryKey( _plugin ) - 1;
        Form form = FormHome.findByPrimaryKey( LastPrimaryKey, _plugin );

        entry.setComment( COMMENT_1 );
        entry.setEntryType( entryType );
        entry.setFieldInLine( FIELD_IN_LINE_1 );
        entry.setForm( form );
        entry.setHelpMessage( HELP_MESSAGE_1 );
        entry.setMandatory( MANDATORY_1 );
        entry.setTitle( TITLE_1 );

        EntryHome.create( entry, _plugin );

        IEntry storedEntry = EntryHome.findByPrimaryKey( entry.getIdEntry(  ), _plugin );

        assertEquals( storedEntry.getComment(  ), entry.getComment(  ) );
        assertEquals( storedEntry.getEntryType(  ).getIdType(  ), entry.getEntryType(  ).getIdType(  ) );
        assertEquals( storedEntry.isFieldInLine(  ), entry.isFieldInLine(  ) );
        assertEquals( storedEntry.getForm(  ).getIdForm(  ), entry.getForm(  ).getIdForm(  ) );
        assertEquals( storedEntry.getHelpMessage(  ), entry.getHelpMessage(  ) );
        assertEquals( storedEntry.isMandatory(  ), entry.isMandatory(  ) );
        assertEquals( storedEntry.getTitle(  ), entry.getTitle(  ) );
    }

    public void testGetEntryList(  )
    {
        List<IEntry> storedListEntry = null;

        EntryFilter entryFilter = new EntryFilter(  );

        storedListEntry = EntryHome.getEntryList( entryFilter, _plugin );

        assertNotNull( storedListEntry );
    }

    public void testGetNumberEntryByFilter(  )
    {
        int nbEntry;

        EntryFilter entryFilter = new EntryFilter(  );

        nbEntry = EntryHome.getNumberEntryByFilter( entryFilter, _plugin );

        assertNotNull( nbEntry );
    }

    public void testUpdate(  )
    {
        IEntry loadEntry = EntryHome.findByPrimaryKey( ID_ENTRY_1, _plugin );

        Entry entry = new Entry(  );

        EntryType entryType2 = new EntryType(  );
        entryType2.setIdType( 2 );

        FormDAO formDAO = new FormDAO(  );
        int LastPrimaryKey = formDAO.newPrimaryKey( _plugin ) - 1;
        Form form = FormHome.findByPrimaryKey( LastPrimaryKey, _plugin );

        entry.setIdEntry( loadEntry.getIdEntry(  ) );
        entry.setComment( COMMENT_2 );
        entry.setEntryType( entryType2 );
        entry.setFieldInLine( FIELD_IN_LINE_1 );
        entry.setForm( form );
        entry.setHelpMessage( HELP_MESSAGE_1 );
        entry.setMandatory( MANDATORY_1 );
        entry.setTitle( TITLE_1 );

        EntryHome.update( entry, _plugin );

        IEntry storedEntry = EntryHome.findByPrimaryKey( entry.getIdEntry(  ), _plugin );

        assertEquals( storedEntry.getComment(  ), entry.getComment(  ) );
        assertEquals( storedEntry.getEntryType(  ).getIdType(  ), entry.getEntryType(  ).getIdType(  ) );
        assertEquals( storedEntry.isFieldInLine(  ), entry.isFieldInLine(  ) );
        assertEquals( storedEntry.getForm(  ).getIdForm(  ), entry.getForm(  ).getIdForm(  ) );
        assertEquals( storedEntry.getHelpMessage(  ), entry.getHelpMessage(  ) );
        assertEquals( storedEntry.isMandatory(  ), entry.isMandatory(  ) );
        assertEquals( storedEntry.getTitle(  ), entry.getTitle(  ) );
    }

    public void testRemove(  )
    {
        Entry entry = new Entry(  );

        EntryType entryType = new EntryType(  );
        entryType.setIdType( ID_ENTRY_TYPE_1 );

        FormDAO formDAO = new FormDAO(  );
        int LastPrimaryKey = formDAO.newPrimaryKey( _plugin ) - 1;
        Form form = FormHome.findByPrimaryKey( LastPrimaryKey, _plugin );

        entry.setComment( COMMENT_1 );
        entry.setEntryType( entryType );
        entry.setFieldInLine( FIELD_IN_LINE_1 );
        entry.setForm( form );
        entry.setHelpMessage( HELP_MESSAGE_1 );
        entry.setMandatory( MANDATORY_1 );
        entry.setTitle( TITLE_1 );

        int lastIdEntry = EntryHome.create( entry, _plugin );

        IEntry loadEntry = EntryHome.findByPrimaryKey( lastIdEntry, _plugin );

        EntryHome.remove( loadEntry.getIdEntry(  ), _plugin );

        IEntry entryStored = EntryHome.findByPrimaryKey( loadEntry.getIdEntry(  ), _plugin );
        assertNull( entryStored );
    }
}
