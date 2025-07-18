package com.tf2tj.trade.views.elements;

import com.tf2tj.trade.models.items.ScrapOffer;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.util.Objects;

public class ItemDetailsLayout extends HorizontalLayout {
    private final Image image = new Image();
    private final TextField defIndex = new TextField("defIndex");
    private final TextField classId = new TextField("classId");
    private final TextField instanceId = new TextField("instanceId");
    private final TextField name = new TextField("name");
    private final TextField nameBase = new TextField("nameBase");
    private final TextField craftable = new TextField("craftable");
    private final TextField australium = new TextField("australium");
    private final TextField festivized = new TextField("festivized");
    private final TextField killstreak = new TextField("killstreak");
    private final TextField quality = new TextField("quality");
    private final TextField paint = new TextField("paint");
    private final VerticalLayout properties = new VerticalLayout();
    private ScrapOffer scrapOffer;

    public ItemDetailsLayout() {
        createImage();
        add(image, createPropertyList());
    }

    private Component createPropertyList() {
        defIndex.setReadOnly(true);
        classId.setReadOnly(true);
        instanceId.setReadOnly(true);
        name.setReadOnly(true);
        nameBase.setReadOnly(true);
        craftable.setReadOnly(true);
        australium.setReadOnly(true);
        festivized.setReadOnly(true);
        killstreak.setReadOnly(true);
        quality.setReadOnly(true);
        paint.setReadOnly(true);

        properties.add(defIndex);
        properties.add(classId);
        properties.add(instanceId);
        properties.add(name);
        properties.add(nameBase);
        properties.add(craftable);
        properties.add(australium);
        properties.add(festivized);
        properties.add(killstreak);
        properties.add(quality);
        properties.add(paint);

        properties.getChildren().forEach(component ->
                component.setVisible(component instanceof TextField && !((TextField) component).isEmpty()));

        return properties;
    }

    private void createImage() {
        image.getStyle()
                .set("height", "300px")
                .set("width", "300px");
    }

    public void setScrapOffer(ScrapOffer scrapOffer) {
        image.setSrc(Objects.toString(scrapOffer.getItemDescription().getBackgroundImage(), ""));

        defIndex.setValue(Objects.toString(scrapOffer.getItemDescription().getDefIndex(), ""));
        classId.setValue(Objects.toString(scrapOffer.getItemDescription().getClassId(), ""));
        instanceId.setValue(Objects.toString(scrapOffer.getItemDescription().getInstanceId(), ""));
        name.setValue(Objects.toString(scrapOffer.getItemDescription().getName(), ""));
        nameBase.setValue(Objects.toString(scrapOffer.getItemDescription().getNameBase(), ""));
        craftable.setValue(scrapOffer.getItemDescription().isCraftable() ? "Y" : "N");
        australium.setValue(scrapOffer.getItemDescription().isAustralium() ? "Y" : "");
        festivized.setValue(scrapOffer.getItemDescription().isFestivized() ? "Y" : "");
        killstreak.setValue(scrapOffer.getItemDescription().getKillstreak() == null ? "" : scrapOffer.getItemDescription().getKillstreak().toString());
        quality.setValue(Objects.toString(scrapOffer.getItemDescription().getQuality().getName(), ""));
        paint.setValue(Objects.toString(scrapOffer.getItemDescription().getPaint().getName(), ""));

        properties.getChildren().forEach(component ->
                component.setVisible(component instanceof TextField && !((TextField) component).isEmpty()));
    }


}