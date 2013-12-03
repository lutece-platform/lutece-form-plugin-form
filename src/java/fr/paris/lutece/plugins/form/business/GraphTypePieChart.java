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

import fr.paris.lutece.plugins.genericattributes.business.StatisticEntrySubmit;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import java.util.List;


/**
 *
 * GraphTypePieChart
 *
 */
public class GraphTypePieChart extends GraphType
{
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
        return createPieChartGraph( listStatistic, strGraphTitle, nGraphThreeDimension, nGraphLabelValue );
    }

    /**
        * return the PieChartGraph  JFreeChart graph
        * @param listStatistic listStatistic
        * @param strGraphTitle graph title
        * @param nGraphThreeDimension true if the graph must be in three dimension
        * @param nGraphLabelValue true if the labels must appear in the graph
        * @return the JFreeChart graph associate to the graph type
        */
    public static JFreeChart createPieChartGraph( List<StatisticEntrySubmit> listStatistic, String strGraphTitle,
        boolean nGraphThreeDimension, boolean nGraphLabelValue )
    {
        PieDataset pieDataset = createPieDataset( listStatistic );
        JFreeChart chart;

        if ( nGraphThreeDimension )
        {
            chart = ChartFactory.createPieChart3D( strGraphTitle, pieDataset, true, true, false );
        }
        else
        {
            chart = ChartFactory.createPieChart( strGraphTitle, pieDataset, true, true, false );
        }

        PiePlot plot = (PiePlot) chart.getPlot(  );

        if ( nGraphLabelValue )
        {
            plot.setLabelGenerator( new StandardPieSectionLabelGenerator( "{2}" ) );
        }
        else
        {
            plot.setLabelGenerator( null );
        }

        return chart;
    }

    /**
     * create dataset for pie chart with a statistic list
     * @param listStatistic list of Statistic
     * @return dataset for pie chart
     */
    private static PieDataset createPieDataset( List<StatisticEntrySubmit> listStatistic )
    {
        DefaultPieDataset pieDataset = new DefaultPieDataset(  );

        for ( StatisticEntrySubmit statistic : listStatistic )
        {
            pieDataset.setValue( statistic.getFieldLibelle(  ), statistic.getNumberResponse(  ) );
        }

        return pieDataset;
    }
}
