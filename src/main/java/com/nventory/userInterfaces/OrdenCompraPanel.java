package com.nventory.userInterfaces;

import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;

import java.awt.*;

public class OrdenCompraPanel extends BorderPane {
    public OrdenCompraPanel() {
        Label lblOrdenCompra = new Label("Orden de Compra");
        setCenter(lblOrdenCompra);
    }
}
