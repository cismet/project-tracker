/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui.profile;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.NavTabs;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;

/**
 *
 * @author dmeiers
 */
public class ProfileMenue extends Composite implements ClickHandler {

    private static ProfileMenueUiBinder uiBinder = GWT.create(ProfileMenueUiBinder.class);
    private FlowPanel detailContainer;
    private AccountSettingsForm accountSettings;
    private ChangePasswordForm changePassword;
    private StatisticsPanel statistics;
    private String selectedPage;
    @UiField
    NavTabs menu;
    @UiField
    NavLink accountLink;
    @UiField
    NavLink passwordLink;
//    @UiField
//    NavLink designLink;
    @UiField
    NavLink statisticsLink;

    interface ProfileMenueUiBinder extends UiBinder<Widget, ProfileMenue> {
    }

    public ProfileMenue(FlowPanel detailView) {
        initWidget(uiBinder.createAndBindUi(this));
        detailContainer = detailView;
        menu.addStyleName("nav-stacked");
        changePassword = new ChangePasswordForm();
        detailContainer.add(changePassword);
        selectedPage = passwordLink.getText();
    }

    @UiHandler(value = {"accountLink", "passwordLink", "statisticsLink"})
    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() instanceof IconAnchor) {
            final String text = ((IconAnchor) event.getSource()).getText();
            selectedPage = text;
            refreshDetailContainer();
        }
    }

    public void refreshDetailContainer() {
        detailContainer.clear();
        if (selectedPage.equals(accountLink.getText())) {
            accountSettings = new AccountSettingsForm();
            detailContainer.add(accountSettings);
        } else if (selectedPage.equals(passwordLink.getText())) {
            if (changePassword == null) {
                changePassword = new ChangePasswordForm();
            }
            detailContainer.add(changePassword);
        } else if (selectedPage.equals(statisticsLink.getText())) {
            statistics = new StatisticsPanel();
            detailContainer.add(statistics);
        }
        //            else if (text.equals(designLink.getText())) {
        //            } 

    }
}
