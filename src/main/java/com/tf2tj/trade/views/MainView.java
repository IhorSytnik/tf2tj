package com.tf2tj.trade.views;

import com.tf2tj.trade.models.items.ScrapOffer;
import com.tf2tj.trade.views.elements.ItemDetailsLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

@Route("")
public class MainView extends HorizontalLayout {
    private final Collection<ScrapOffer> sellOffers;
    private final Grid<ScrapOffer> grid;
    private final ItemDetailsLayout itemDetailsLayout;

    public MainView(@Autowired Collection<ScrapOffer> sellOffers) {
        this.sellOffers = sellOffers;
        this.grid = createItemGrid();
        this.itemDetailsLayout = new ItemDetailsLayout();

        add(grid, itemDetailsLayout);
    }

    private Grid<ScrapOffer> createItemGrid() {
        var grid = new Grid<ScrapOffer>();

        grid.addComponentColumn(scrapOffer -> new Image(scrapOffer.getItemDescription().getBackgroundImage(), scrapOffer.getItemDescription().getName())).setHeader("Image");
        grid.addColumn(scrapOffer -> scrapOffer.getItemDescription().getName()).setHeader("Name");
        grid.addComponentColumn(scrapOffer -> {
            var span = new Span(scrapOffer.getItemDescription().getQuality().getName());
            span.getStyle()
                    .set("font-weight", "bold")
                    .set("color", scrapOffer.getItemDescription().getQuality().getColor());
            return span;
        }).setHeader("Quality");
        grid.addComponentColumn(scrapOffer -> {
            var horizontalLayout = new HorizontalLayout();
            var div = new Div();
            div.getStyle()
                    .set("height", "3em")
                    .set("width", "3em")
                    .set("background-color", "#" + scrapOffer.getItemDescription().getPaint().getHexValue());
            var span = new Span(scrapOffer.getItemDescription().getPaint().getName());
            span.getStyle()
                    .set("font-weight", "bold");

            horizontalLayout.add(div, span);

            return horizontalLayout;
        }).setHeader("Paint");

        grid.addColumn(ScrapOffer::getPriceFull).setHeader("Price");
        grid.addColumn(ScrapOffer::getAmount).setHeader("Amount");

        grid.setItems(sellOffers);
        grid.addCellFocusListener(event -> {
            if (event.getItem().isPresent())
                itemDetailsLayout.setScrapOffer(event.getItem().get());
        });

        return grid;
    }
}
