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
package org.dashbuilder.dataset.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.*;
import org.dashbuilder.dataset.client.resources.i18n.DataSetEditorConstants;
import org.dashbuilder.dataset.client.widgets.editors.DataSetAdvancedAttributesEditor;
import org.dashbuilder.dataset.client.widgets.editors.DataSetBasicAttributesEditor;
import org.dashbuilder.dataset.client.widgets.editors.DataSetColumnsAndFilterEditor;
import org.dashbuilder.dataset.client.widgets.editors.DataSetProviderTypeEditor;
import org.dashbuilder.dataset.client.widgets.events.EditDataSetEvent;
import org.dashbuilder.dataset.client.widgets.events.NewDataSetEvent;
import org.dashbuilder.dataset.def.DataSetDef;

import javax.enterprise.context.Dependent;

/**
 * <p>Data Set Definition editor widget.</p>
 * <p>This widget allows edition or creation of a data set definitions.</p>
 *  
 * <p>This widget is presented using four views:</p>
 * <ul>
 *     <li>Provider type editor view - @see <code>org.dashbuilder.dataset.client.widgets.editors.DataSetProviderTypeEditor</code></li>     
 *     <li>Basic attributes editor view - @see <code>org.dashbuilder.dataset.client.widgets.editors.DataSetBasicAttributesEditor</code></li>     
 *     <li>Advanced attributes editor view - @see <code>org.dashbuilder.dataset.client.widgets.editors.DataSetAdvancedAttributesEditor</code></li>     
 *     <li>Column, filter and table results editor view - @see <code>org.dashbuilder.dataset.client.widgets.editors.DataSetColumnsAndFilterEditor</code></li>     
 * </ul>
 * 
 * <p>This editor provides three screens:</p>
 * <ul>
 *     <li>Basic data set attributes edition.</li>     
 *     <li>Advanced data set attributes edition.</li>
 *     <li>Data set columns and initial filter edition.</li>     
 * </ul> 
 */
@Dependent
public class DataSetEditor implements IsWidget {
    

    public interface View extends IsWidget, HasHandlers {
        void set(DataSetDef dataSetDef);
        void clear();
        Widget show();
        void hide();
    }

    final DataSetProviderTypeEditor dataSetProviderTypeEditorView = new DataSetProviderTypeEditor();
    final DataSetBasicAttributesEditor dataSetBasicAttributesEditorView = new DataSetBasicAttributesEditor();
    final DataSetAdvancedAttributesEditor dataSetAdvancedAttributesEditorView = new DataSetAdvancedAttributesEditor();
    final DataSetColumnsAndFilterEditor dataSetColumnsAndFilterEditorView = new DataSetColumnsAndFilterEditor();
    private FlowPanel mainPanel = new FlowPanel();
    
    public DataSetEditor() {
        buildInitialView();
    }
    
    public void newDataSet(String uuid) {
        // Create a new data set def.
        DataSetDef dataSetDef = new DataSetDef();
        dataSetDef.setUUID(uuid);
        
        setDataSetDef(dataSetDef);
        buildBasicAttributesEditionView();
    }

    public void editDataSet(String uuid) {
        DataSetDef dataSetDef = null; // TODO: Obtain instance using uuid.
        setDataSetDef(dataSetDef);

        buildBasicAttributesEditionView();
    }

    @Override
    public Widget asWidget() {
        return mainPanel;
    }
    
    public void buildInitialView() {
        FlowPanel mainPanel = new FlowPanel();
        Hyperlink link = new InlineHyperlink();
        link.setText(DataSetEditorConstants.INSTANCE.newDataSet());
        link.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // TODO: Generate uuid.
                newDataSet("new-uuid");
            }
        });
        mainPanel.add(link);
        this.mainPanel.clear();
        this.mainPanel.add(mainPanel);
    }

    public void buildBasicAttributesEditionView() {
        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(dataSetBasicAttributesEditorView.show());
        mainPanel.add(dataSetProviderTypeEditorView.show());
        final ClickHandler nextButtonClickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                buildAdvancedAttributesEditionView();
            }
        };
        mainPanel.add(buildButtons(false, true, null, nextButtonClickHandler));
        this.mainPanel.clear();
        this.mainPanel.add(mainPanel);
    }

    public void buildAdvancedAttributesEditionView() {
        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(dataSetBasicAttributesEditorView.show());
        mainPanel.add(dataSetAdvancedAttributesEditorView.show());

        final ClickHandler nextButtonClickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                buildColumnsAndFilterEditionView();
            }
        };
        mainPanel.add(buildButtons(false, true, null, nextButtonClickHandler));
        this.mainPanel.clear();
        this.mainPanel.add(mainPanel);
    }

    public void buildColumnsAndFilterEditionView() {
        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(dataSetBasicAttributesEditorView.show());
        mainPanel.add(dataSetColumnsAndFilterEditorView.show());
        
        final ClickHandler backButtonClickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                buildAdvancedAttributesEditionView();
            }
        };
        final ClickHandler nextButtonClickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // TODO
            }
        };
        mainPanel.add(buildButtons(true, true, backButtonClickHandler, nextButtonClickHandler));
        this.mainPanel.clear();
        this.mainPanel.add(mainPanel);
    }


    private Panel buildButtons(final boolean showBackButton, final boolean showNextButton, final ClickHandler backButtonClickHandler, final ClickHandler nextButtonClickHandler) {
        final HorizontalPanel buttonsPanel = new HorizontalPanel();
        buttonsPanel.setSpacing(10);
        
        final com.github.gwtbootstrap.client.ui.Button cancelButton = new com.github.gwtbootstrap.client.ui.Button(DataSetEditorConstants.INSTANCE.cancel());
        buttonsPanel.add(cancelButton);
        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clear();
            }
        });

        if (showBackButton) {
            final com.github.gwtbootstrap.client.ui.Button backButton = new com.github.gwtbootstrap.client.ui.Button(DataSetEditorConstants.INSTANCE.back());
            buttonsPanel.add(backButton);
            if (backButtonClickHandler != null) backButton.addClickHandler(backButtonClickHandler);
        }

        if (showNextButton) {
            final com.github.gwtbootstrap.client.ui.Button nextButton = new com.github.gwtbootstrap.client.ui.Button(DataSetEditorConstants.INSTANCE.next());
            buttonsPanel.add(nextButton);
            if (nextButtonClickHandler != null) nextButton.addClickHandler(nextButtonClickHandler);
        }
        
        return buttonsPanel;
    }

    public void clear() {
        dataSetProviderTypeEditorView.clear();
        dataSetBasicAttributesEditorView.clear();
        dataSetAdvancedAttributesEditorView.clear();
        dataSetColumnsAndFilterEditorView.clear();
        buildInitialView();
    }
    
    private void setDataSetDef(DataSetDef dataSetDef) {
        dataSetProviderTypeEditorView.set(dataSetDef);
        dataSetBasicAttributesEditorView.set(dataSetDef);
        dataSetAdvancedAttributesEditorView.set(dataSetDef);
        dataSetColumnsAndFilterEditorView.set(dataSetDef);
    }
}
