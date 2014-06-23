/**
 * Copyright (C) 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.client.sales.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.displayer.DataViewer;
import org.dashbuilder.client.displayer.DataViewerCoordinator;
import org.dashbuilder.client.displayer.DataViewerLocator;
import org.dashbuilder.model.dataset.DataSetFactory;
import org.dashbuilder.model.displayer.DisplayerFactory;

import static org.dashbuilder.model.dataset.group.DateIntervalType.*;
import static org.dashbuilder.model.dataset.sort.SortOrder.DESCENDING;
import static org.dashbuilder.model.date.DayOfWeek.*;
import static org.dashbuilder.client.sales.SalesConstants.*;

/**
 * A composite widget that represents an entire dashboard sample composed using an UI binder template.
 * <p>The dashboard itself is composed by a set of DataViewer instances.</p>
 */
public class SalesExpectedByDate extends Composite {

    interface SalesDashboardBinder extends UiBinder<Widget, SalesExpectedByDate>{}
    private static final SalesDashboardBinder uiBinder = GWT.create(SalesDashboardBinder.class);

    @UiField(provided = true)
    DataViewer areaChartByDate;

    @UiField(provided = true)
    DataViewer pieChartYears;

    @UiField(provided = true)
    DataViewer pieChartQuarters;

    @UiField(provided = true)
    DataViewer barChartDayOfWeek;

    @UiField(provided = true)
    DataViewer pieChartByPipeline;

    @UiField(provided = true)
    DataViewer tableAll;

    public SalesExpectedByDate() {

        // Create the chart definitions
        DataViewerLocator viewerLocator = DataViewerLocator.get();

        areaChartByDate = viewerLocator.lookupViewer(DataSetFactory.newDSLookup()
                .dataset(SALES_OPPS)
                .group(CREATION_DATE, 80, MONTH)
                .sum(EXPECTED_AMOUNT)
                .buildLookup(), DisplayerFactory.newAreaChart()
                .title("Expected pipeline")
                .titleVisible(true)
                .width(1000).height(250)
                .margins(10, 100, 100, 100)
                .column("Creation date")
                .column("Amount")
                .buildDisplayer());

        pieChartYears = viewerLocator.lookupViewer(DataSetFactory.newDSLookup()
                .dataset(SALES_OPPS)
                .group(CREATION_DATE, YEAR)
                .count("occurrences")
                .buildLookup(), DisplayerFactory.newPieChart()
                .title("Years")
                .titleVisible(true)
                .width(250).height(200)
                .margins(10, 10, 10, 10)
                .buildDisplayer());

        pieChartQuarters = viewerLocator.lookupViewer(DataSetFactory.newDSLookup()
                .dataset(SALES_OPPS)
                .group(CREATION_DATE).fixed(QUARTER)
                .count("occurrences")
                .buildLookup(), DisplayerFactory.newPieChart()
                .title("Quarters")
                .titleVisible(true)
                .width(250).height(200)
                .margins(10, 10, 10, 10)
                .buildDisplayer());

        barChartDayOfWeek = viewerLocator.lookupViewer(DataSetFactory.newDSLookup()
                .dataset(SALES_OPPS)
                .group(CREATION_DATE).fixed(DAY_OF_WEEK).firstDay(SUNDAY)
                .count("occurrences")
                .buildLookup(), DisplayerFactory.newBarChart()
                .title("Day of week")
                .titleVisible(true)
                .width(250).height(200)
                .margins(10, 50, 80, 10)
                .horizontal()
                .buildDisplayer());


        pieChartByPipeline = viewerLocator.lookupViewer(DataSetFactory.newDSLookup()
                .dataset(SALES_OPPS)
                .group(PIPELINE)
                .count("occurrences")
                .buildLookup(), DisplayerFactory.newPieChart()
                .title("Pipeline")
                .titleVisible(true)
                .width(250).height(200)
                .margins(10, 10, 10, 10)
                .column("Pipeline")
                .column("Number of opps")
                .buildDisplayer());

        tableAll = viewerLocator.lookupViewer(DataSetFactory.newDSLookup()
                .dataset(SALES_OPPS)
                .buildLookup(), DisplayerFactory.newTable()
                .title("List of Opportunities")
                .titleVisible(true)
                .tablePageSize(10)
                .tableOrderEnabled(true)
                .tableOrderDefault(AMOUNT, DESCENDING)
                .column(COUNTRY, "Country")
                .column(CUSTOMER, "Customer")
                .column(PRODUCT, "Product")
                .column(SALES_PERSON, "Salesman")
                .column(STATUS, "Status")
                .column(AMOUNT, "Amount")
                .column(EXPECTED_AMOUNT, "Expected")
                .column(CREATION_DATE, "Creation")
                .column(CLOSING_DATE, "Closing")
                .buildDisplayer());

        // Make that charts interact among them
        DataViewerCoordinator viewerCoordinator = new DataViewerCoordinator();
        viewerCoordinator.addViewer(areaChartByDate);
        viewerCoordinator.addViewer(pieChartYears);
        viewerCoordinator.addViewer(pieChartQuarters);
        viewerCoordinator.addViewer(barChartDayOfWeek);
        viewerCoordinator.addViewer(pieChartByPipeline);
        viewerCoordinator.addViewer(tableAll);

        // Init the dashboard from the UI Binder template
        initWidget(uiBinder.createAndBindUi(this));

        // Draw the charts
        viewerCoordinator.drawAll();
    }


}