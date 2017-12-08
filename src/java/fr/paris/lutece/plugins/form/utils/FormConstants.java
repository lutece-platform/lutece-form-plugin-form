/*
 * Copyright (c) 2002-2017, Mairie de Paris
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

import org.apache.commons.lang3.math.NumberUtils;

/**
 * 
 * Constants class for the plugin-form
 *
 */
public class FormConstants
{
    // Session
    public static final String SESSION_FORM_ERRORS = "form_errors";
    public static final String SESSION_ITERATION_MAP = "session_iteration_map";

    // Parameters
    public static final String PARAMETER_ID_FORM = "id_form";
    public static final String PARAMETER_ID_ENTRY = "id_entry";
    public static final String PARAMETER_KEY = "key";

    // Attributes
    public static final String ATTRIBUTE_ITERATION_NUMBER = "attribute_iteration_number";
    public static final String ATTRIBUTE_RETURN_FROM_ERRORS = "attribute_return_from_errors";
    public static final String ATTRIBUTE_RESPONSE_ITERATION_NUMBER = "response_iteration_number";

    // Marks
    public static final String MARK_FORM = "form";
    public static final String MARK_ID_FORM = "id_form";
    public static final String MARK_FORM_SUBMIT = "formSubmit";
    public static final String MARK_URL_ACTION = "url_action";
    public static final String MARK_STR_LIST_CHILDREN = "str_list_entry_children";

    // Prefixes
    public static final String PREFIX_ITERATION = "nIt";
    public static final String PREFIX_ATTRIBUTE = "attribute";

    // Symbols
    public static final String ANCHOR_DELIMITER = "#";
    public static final String SLASH = "/";

    // Other constants
    public static final String CONSTANT_WHERE = " WHERE ";
    public static final String CONSTANT_AND = " AND ";
    public static final int DEFAULT_ITERATION_NUMBER = NumberUtils.INTEGER_MINUS_ONE;
}
