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



/**
 *
 * class EntryTypeSession
 * This entry is used to fetch the value of a session's attribute.
 * One example is when coupling form with crm, the module-crm-form
 * will put in session the ID demand and the user GUID. This entry will
 * be able to fetch the ID demand and user GUID when validating the form.
 * Then, it is easier to export the value to directory with the
 * module-form-exportdirectory.
 *
 */
public class EntryTypeSession extends AbstractEntryTypeSession
{
    private final String _template_create = "admin/plugins/form/entries/create_entry_type_session.html";
    private final String _template_modify = "admin/plugins/form/entries/modify_entry_type_session.html";
    private final String _template_html_code = "admin/plugins/form/entries/html_code_entry_type_session.html";

    /**
     * {@inheritDoc}
     */
    public String getHtmlCode(  )
    {
        return _template_html_code;
    }

    /**
     * {@inheritDoc}
     */
    public String getTemplateCreate(  )
    {
        return _template_create;
    }

    /**
     * {@inheritDoc}
     */
    public String getTemplateModify(  )
    {
        return _template_modify;
    }
}
