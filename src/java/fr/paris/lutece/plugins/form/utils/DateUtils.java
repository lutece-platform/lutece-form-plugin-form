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
package fr.paris.lutece.plugins.form.utils;

import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppLogService;

import java.sql.Timestamp;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


/**
 *
 * DateUtils
 *
 */
public final class DateUtils
{
    /**
     * Private constructor
     */
    private DateUtils(  )
    {
    }

    /**
     * Get the current date time
     * @param locale the locale
     * @return the current date as String
     */
    public static String getCurrentDateTime( Locale locale )
    {
        Locale localeTmp = locale;

        if ( localeTmp == null )
        {
            localeTmp = I18nService.getDefaultLocale(  );
        }

        Calendar calendar = new GregorianCalendar(  );

        return I18nService.getLocalizedDateTime( calendar.getTime(  ), localeTmp, DateFormat.SHORT, DateFormat.SHORT );
    }

    /**
     * Format a String into a timestamp
     * @param strTimestamp the String to format
     * @param locale the locale
     * @return a {@link Timestamp}
     */
    public static Timestamp formatTimestamp( String strTimestamp, Locale locale )
    {
        DateFormat dateFormatter = DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT, locale );
        Date date = null;

        try
        {
            date = dateFormatter.parse( strTimestamp );

            return new Timestamp( date.getTime(  ) );
        }
        catch ( ParseException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        return null;
    }
}
