/*
 * Copyright (c) 2002-2012, Mairie de Paris
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

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;


/**
 * 
 * StringUtil
 * 
 */
public final class StringUtil
{
    private static final String PROPERTY_ENCODING_ENABLE = "form.response.encoding.enable";
    private static final String PROPERTY_ENCODING = "form.response.encoding";

    /**
     * Private constructor
     */
    private StringUtil( )
    {
    }

    /**
     * Convert a byte[] to a String. <br />
     * If the conversion with a specified encoding is not enable, then it will
     * use the default encoding.
     * @param toConvert the String to convert
     * @return the convert String if there is no error, the String in the
     *         parameter otherwise
     */
    public static String convertToString( byte[] toConvert )
    {
        String strEncoding = getConversionEncoding( );

        if ( isConversionEnable( ) && StringUtils.isNotBlank( strEncoding ) )
        {
            try
            {
                return new String( toConvert, strEncoding );
            }
            catch ( UnsupportedEncodingException e )
            {
                AppLogService.error( e );
            }
        }

        return new String( toConvert );
    }

    /**
     * Convert a String to byte[]. <br />
     * If the conversion encoding is enable, then it will use the
     * encoding defined in <code>form.response.encoding</code>, otherwise
     * it will use the default encoding
     * @param strToConvert the String to convert
     * @return a String converted in byte[]
     */
    public static byte[] convertToByte( String strToConvert )
    {
        String strEncoding = getConversionEncoding( );

        if ( isConversionEnable( ) && StringUtils.isNotBlank( strEncoding ) )
        {
            try
            {
                return strToConvert.getBytes( strEncoding );
            }
            catch ( UnsupportedEncodingException e )
            {
                AppLogService.error( e );
            }
        }

        return strToConvert.getBytes( );
    }

    /**
     * Check if the conversion is enable. <br />
     * It is defined in the <code>form.response.encoding.enable</code>.
     * @return true if the conversion is enable, false otherwise
     */
    public static boolean isConversionEnable( )
    {
        return Boolean.valueOf( AppPropertiesService.getProperty( PROPERTY_ENCODING_ENABLE ) );
    }

    /**
     * Get the conversion encoding defined in
     * <code>form.response.encoding</code>
     * @return the conversion encoding
     */
    public static String getConversionEncoding( )
    {
        return AppPropertiesService.getProperty( PROPERTY_ENCODING );
    }
}
