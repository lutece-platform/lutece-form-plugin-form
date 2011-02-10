/*
 * Copyright (c) 2002-2009, Mairie de Paris
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

import org.jfree.chart.JFreeChart;

import java.util.List;


/**
 *
 * class GraphType
 *
 */
public class GraphType
{
    private int _nIdGraphType;
    private String _strTitle;
    private String _strClassName;

    /**
     *
     * @return the name of the class associate to the graph type
     */
    public String getClassName(  )
    {
        return _strClassName;
    }

    /**
     * set the name of the class associate to the graph type
     * @param className the name of the class associate to the graph type
     */
    public void setClassName( String className )
    {
        _strClassName = className;
    }

    /**
    *
    * @return the id of the graph type
    */
    public int getIdGraphType(  )
    {
        return _nIdGraphType;
    }

    /**
     * set the id of the graph type
     * @param idGraphType the id of the graph type
     */
    public void setIdGraphType( int idGraphType )
    {
        _nIdGraphType = idGraphType;
    }

    /**
     *
     * @return the title of the graph type
     */
    public String getTitle(  )
    {
        return _strTitle;
    }

    /**
     * set the title of the graph type
     * @param title the title of the graph type
     */
    public void setTitle( String title )
    {
        _strTitle = title;
    }

    /**
     * return the JFreeChart graph associate to the graph type
     * @param listStatistic listStatistic
     * @param strGraphTitle graph title
     * @param nGraphThreeDimension true if the graph must be in three dimension
     * @param nGraphLabelValue true if the labels must appear in the graph
     * @return the JFreeChart graph associate to the graph type
     */
    public JFreeChart createChart( List<StatisticEntrySubmit> listStatistic, String strGraphTitle,
        boolean nGraphThreeDimension, boolean nGraphLabelValue )
    {
        return null;
    }
}
