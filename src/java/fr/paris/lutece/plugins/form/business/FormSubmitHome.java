/*
 * Copyright (c) 2002-2011, Mairie de Paris
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

import fr.paris.lutece.plugins.form.service.FormPlugin;
import fr.paris.lutece.plugins.form.service.ResponseService;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;


/**
 *class FormSubmitHome
 */
public final class FormSubmitHome
{
    // Static variable pointed at the DAO instance
    private static IFormSubmitDAO _dao = (IFormSubmitDAO) SpringContextService.getPluginBean( "form",
            "form.formSubmitDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private FormSubmitHome(  )
    {
    }

    /**
     * Creation of an instance of formSubmit
     *
     * @param formSubmit The instance of the formSubmit which contains the informations to store
     * @param plugin the Plugin
     * @return the id of the new form submit
     *
     */
    public static int create( FormSubmit formSubmit, Plugin plugin )
    {
        return _dao.insert( formSubmit, plugin );
    }

    /**
     * Update of the formSubmit which is specified in parameter
     *
     * @param formSubmit The instance of the FormSubmit which contains the informations to update
     * @param plugin the Plugin
     *
     */
    public static void update( FormSubmit formSubmit, Plugin plugin )
    {
        _dao.store( formSubmit, plugin );
    }

    /**
     * Remove the formSubmit whose identifier is specified in parameter
     *
     * @param nIdFormSubmit The formSubmitId
     * @param plugin the Plugin
     */
    public static void remove( int nIdFormSubmit, Plugin plugin )
    {
        ResponseService responseService = (ResponseService) SpringContextService.getPluginBean( FormPlugin.PLUGIN_NAME,
                FormUtils.BEAN_FORM_RESPONSE_SERVICE );
        responseService.remove( nIdFormSubmit );
        _dao.delete( nIdFormSubmit, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a FormSubmitwhose identifier is specified in parameter
     *
     * @param nKey The formResponse primary key
     * @param plugin the Plugin
     * @return an instance of FormResponse
     */
    public static FormSubmit findByPrimaryKey( int nKey, Plugin plugin )
    {
        return _dao.load( nKey, plugin );
    }

    /**
        * Load the data of all the formSubmit who verify the filter and returns them in a  list
        * @param filter the filter
        * @param plugin the plugin
        * @return  the list of formSubmit
        */
    public static List<FormSubmit> getFormSubmitList( ResponseFilter filter, Plugin plugin )
    {
        return _dao.selectListByFilter( filter, plugin );
    }

    /**
     * Load the data of all the formSubmit who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of formSubmit
     */
    public static int getCountFormSubmit( ResponseFilter filter, Plugin plugin )
    {
        return _dao.selectCountByFilter( filter, plugin );
    }

    /**
         * Load the number of formSubmit who verify the filter and returns them in a  list of statistic
         * @param filter the filter
         * @param plugin the plugin
         * @return  the list of statistic
         */
    public static List<StatisticFormSubmit> getStatisticFormSubmit( ResponseFilter filter, Plugin plugin )
    {
        return _dao.selectStatisticFormSubmit( filter, plugin );
    }
}
