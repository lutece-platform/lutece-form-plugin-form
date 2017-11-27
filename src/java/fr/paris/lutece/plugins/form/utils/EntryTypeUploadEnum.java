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

import java.util.LinkedHashSet;
import java.util.Set;
import fr.paris.lutece.plugins.form.service.entrytype.EntryTypeFile;
import fr.paris.lutece.plugins.form.service.entrytype.EntryTypeImage;
import fr.paris.lutece.plugins.form.service.entrytype.EntryTypeCropImage;

/**
 * Enumeration of all EntryType which need to upload file (file, image, etc...)
 */
public enum EntryTypeUploadEnum {

    EntryTypeFile( EntryTypeFile.class.getName( ) ),
    EntryTypeImage( EntryTypeImage.class.getName( ) ),
    EntryTypeCropImage( EntryTypeCropImage.class.getName( ) );
    
    // The name of the class of the EntryType
    private String _strEntryTypeClassName;

    // Private constructor
    private EntryTypeUploadEnum( String strEntryTypeClassName)
    {
        _strEntryTypeClassName = strEntryTypeClassName;
    }
    
    /**
     * Return the value of the enumeration
     * 
     * @return the value of the enumeration
     */
    public String getValue( )
    {
        return _strEntryTypeClassName;
    }
    
    /**
     * Return all the value of the Enumeration
     * 
     * @return all the value of the Enumeration
     */
    public static Set<String> getValues( )
    {
        Set<String> setEntryTypeName = new LinkedHashSet<>( );
        
        EntryTypeUploadEnum[ ] entryTypeUploadEnums =  values( );
        for ( EntryTypeUploadEnum entryTypeUploadEnum : entryTypeUploadEnums )
        {
            setEntryTypeName.add( entryTypeUploadEnum.getValue( ) );
        }
        
        return setEntryTypeName;
    }
}
