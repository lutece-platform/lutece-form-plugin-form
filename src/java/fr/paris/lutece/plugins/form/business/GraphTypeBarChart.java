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
package fr.paris.lutece.plugins.form.business;

import fr.paris.lutece.plugins.genericattributes.business.StatisticEntrySubmit;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import org.jfree.ui.TextAnchor;

import java.text.DecimalFormat;

import java.util.List;


/**
 *
 *  class GraphTypeBarChart
 *
 */
public class GraphTypeBarChart extends GraphType
{
    /**
     * {@inheritDoc}
     */
    @Override
    public JFreeChart createChart( List<StatisticEntrySubmit> listStatistic, String strGraphTitle,
        boolean nGraphThreeDimension, boolean nGraphLabelValue )
    {
        return createBarChart( listStatistic, strGraphTitle, nGraphThreeDimension, nGraphLabelValue );
    }

    /**
     * Return the JFreeChart BarChart graph
     * @param listStatistic listStatistic
     * @param strGraphTitle graph title
     * @param nGraphThreeDimension true if the graph must be in three dimension
     * @param nGraphLabelValue true if the labels must appear in the graph
     * @return the JFreeChart graph associate to the graph type
     */
    public static JFreeChart createBarChart( List<StatisticEntrySubmit> listStatistic, String strGraphTitle,
        boolean nGraphThreeDimension, boolean nGraphLabelValue )
    {
        JFreeChart chart;
        CategoryDataset dataset = createBarChartDataset( listStatistic );

        if ( nGraphThreeDimension )
        {
            chart = ChartFactory.createBarChart3D( strGraphTitle, null, null, dataset, PlotOrientation.VERTICAL, true,
                    false, false );
        }
        else
        {
            chart = ChartFactory.createBarChart( strGraphTitle, null, null, dataset, PlotOrientation.VERTICAL, true,
                    false, false );
        }

        CategoryPlot categoryPlot = chart.getCategoryPlot(  );
        CategoryAxis categoryAxis = categoryPlot.getDomainAxis(  );
        CategoryLabelPositions labelPositions = CategoryLabelPositions.UP_45;
        categoryAxis.setCategoryLabelPositions( labelPositions );

        BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer(  );

        if ( nGraphLabelValue )
        {
            renderer.setItemLabelsVisible( true );

            DecimalFormat decimalformat1 = new DecimalFormat( "#.#" );
            renderer.setItemLabelGenerator( new StandardCategoryItemLabelGenerator( "{2}", decimalformat1 ) );

            if ( nGraphThreeDimension )
            {
                renderer.setPositiveItemLabelPositionFallback( new ItemLabelPosition( ItemLabelAnchor.OUTSIDE3,
                        TextAnchor.BASELINE_LEFT ) );
                categoryPlot.setRenderer( renderer );
            }
        }

        return chart;
    }

    /**
     * Create barChatDataset function of the list of statistic
     * @param listStatistic create barChatDataset function of the list of
     *            statistic
     * @return barCharDataset
     */
    private static CategoryDataset createBarChartDataset( List<StatisticEntrySubmit> listStatistic )
    {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset(  );

        for ( StatisticEntrySubmit statistic : listStatistic )
        {
            dataset.addValue( statistic.getNumberResponse(  ), statistic.getFieldLibelle(  ),
                statistic.getFieldLibelle(  ) );
        }

        return dataset;
    }
}
