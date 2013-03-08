package de.cismet.projecttracker.client.common.ui;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class FlowPanelWithSpacer extends FlowPanel implements HasDoubleClickHandlers {

    public FlowPanelWithSpacer() {
        HTML spacerLabel = new HTML("<hr />");
        spacerLabel.setStyleName("span1V2 no-margin");
        spacerLabel.setHeight("50px");
//        spacerLabel.setWidth("90px");
        super.add(spacerLabel);
    }
    


    @Override
    public void add(Widget w) {
        super.insert(w, getWidgetCount() == 0? 0: getWidgetCount() - 1);
    }

    @Override
    public void insert(Widget w, int beforeIndex) {
        if (beforeIndex == getWidgetCount()) {
            beforeIndex--;
        }
        super.insert(w, beforeIndex);
    }

    @Override
    public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
        return addHandler(handler, DoubleClickEvent.getType());
    }

}
