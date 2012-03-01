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
package fr.paris.lutece.plugins.form.business;


/**
 *
 * class Recap
 */
public class Recap
{
    private int _nIdRecap;
    private Form _form;
    private String _strBackUrl;
    private String _strRecapMessage;
    private boolean _nRecapData;
    private boolean _nGraph;
    private boolean _nGraphThreeDimension;
    private boolean _nGraphLegende;
    private boolean _nGraphLabelValue;
    private String _strGraphValueLegende;
    private GraphType _graphType;

    /**
     *
     * @return the graph type associate to the recap
     */
    public GraphType getGraphType(  )
    {
        return _graphType;
    }

    /**
     * set the graph type associate to the recap
     * @param graphType the graph type associate to the recap
     */
    public void setGraphType( GraphType graphType )
    {
        this._graphType = graphType;
    }

    /***
     *
     * @return the form associate to the recap
     */
    public Form getForm(  )
    {
        return _form;
    }

    /**
     * set the form associate to the recap
     * @param form the form associate to the recap
     */
    public void setForm( Form form )
    {
        this._form = form;
    }

    /**
     *
     * @return true if a graph is associate to the recap
     */
    public boolean isGraph(  )
    {
        return _nGraph;
    }

    /**
     * set true if a graph is associate to the recap
     * @param graph true if a graph is associate to the recap
     */
    public void setGraph( boolean graph )
    {
        _nGraph = graph;
    }

    /**
     *
     * @return true if the graph have a legende
     */
    public boolean isGraphLegende(  )
    {
        return _nGraphLegende;
    }

    /**
     * set true if the graph have a legende
     * @param graphLegende true if the graph have a legende
     */
    public void setGraphLegende( boolean graphLegende )
    {
        _nGraphLegende = graphLegende;
    }

    /**
     *
     * @return the id of the recap
     */
    public int getIdRecap(  )
    {
        return _nIdRecap;
    }

    /**
     * set the id of the recap
     * @param idRecap the id of the recap
     */
    public void setIdRecap( int idRecap )
    {
        _nIdRecap = idRecap;
    }

    /**
     *
     * @return true if the graph have a label
     */
    public boolean isGraphLabelValue(  )
    {
        return _nGraphLabelValue;
    }

    /**
     * set  true if the graph have a label
     * @param label  true if the graph have a label
     */
    public void setGraphLabelValue( boolean label )
    {
        _nGraphLabelValue = label;
    }

    /**
     *
     * @return  true if the recap have a recap data
     */
    public boolean isRecapData(  )
    {
        return _nRecapData;
    }

    /**
     * set true if the recap have a recap data
     * @param recapData true if the recap have a recap data
     */
    public void setRecapData( boolean recapData )
    {
        _nRecapData = recapData;
    }

    /**
     *
     * @return true if the graph associate to the recap  is in three dimension
     */
    public boolean isGraphThreeDimension(  )
    {
        return _nGraphThreeDimension;
    }

    /**
     * set true if the graph associate to the recap  is in three dimension
     * @param threeDimension true if the graph associate to the recap  is in three dimension
     */
    public void setGraphThreeDimension( boolean threeDimension )
    {
        _nGraphThreeDimension = threeDimension;
    }

    /**
     *
     * @return the recap message
     */
    public String getRecapMessage(  )
    {
        return _strRecapMessage;
    }

    /**
     * set  the recap message
     * @param recapMessage  the recap message
     */
    public void setRecapMessage( String recapMessage )
    {
        _strRecapMessage = recapMessage;
    }

    /**
     *
     * @return the back url
     */
    public String getBackUrl(  )
    {
        return _strBackUrl;
    }

    /**
     * set the back url
     * @param backUrl the back url
     */
    public void setBackUrl( String backUrl )
    {
        _strBackUrl = backUrl;
    }

    /**
     *
     * @return graph value legende
     */
    public String getGraphValueLegende(  )
    {
        return _strGraphValueLegende;
    }

    /**
     * set graph value legende
     * @param valueLegende graph value legende
     */
    public void setGraphValueLegende( String valueLegende )
    {
        _strGraphValueLegende = valueLegende;
    }
}
