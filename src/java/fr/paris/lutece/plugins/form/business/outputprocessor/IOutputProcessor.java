/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.form.business.outputprocessor;

import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormSubmit;
import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 *
 *  IOutputProcessor
 *
 */
public interface IOutputProcessor
{
    /**
     * Get the processor key
     * @return the processor key
     */
    String getKey(  );

    /**
     * set the processor key
     * @param strKey the processor key
     */
    void setKey( String strKey );

    /**
     * Return the processor configuration interface
     * @param request The request
     * @param form the form wich using the processor
     * @param locale {@link Locale}
     * @param plugin {@link Plugin}
     * @return the processor configuration interface
     */
    String getOutputConfigForm( HttpServletRequest request, Form form, Locale locale, Plugin plugin );

    /**
     * Store the processor configuration
     * @param request {@link HttpServletRequest}
     * @param locale {@link Locale}
     * @param plugin {@link Plugin}
     * @return a message error if a error appear else null
     */
    String doOutputConfigForm( HttpServletRequest request, Locale locale, Plugin plugin );

    /**
     * Process the OutputProcessor
     * @param formSubmit the formSubmit associate to the process
     * @param request {@link HttpServletRequest}
     * @param plugin {@link Plugin}
     * @return a message error if a error appear else null
     */
    String process( FormSubmit formSubmit, HttpServletRequest request, Plugin plugin );
}
