package com.nventory.userInterfaces;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class ProveedorPanel extends BorderPane {
    /*
     * GUI Proveedores.
     *
     *  Define la interfaz gráfica para la gestión de proveedores.
     *
     * @author Juan Pablo
     * @version 1.0
     */
    public ProveedorPanel() {
        Label lbl1 = new Label("Proveedor");
        setCenter(lbl1);
    }
}