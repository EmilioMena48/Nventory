package com.nventory.userInterfaces;

import com.nventory.controller.OrdenDeCompraController;
import com.nventory.DTO.OrdenDeCompraDTO;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;


public class OrdenCompraPanel extends BorderPane {

    private TableView<OrdenDeCompraDTO> tablaOrdenes;
    private final OrdenDeCompraController controller;

    public OrdenCompraPanel(OrdenDeCompraController controller) {
        this.controller = controller;
        inicializarUI();
        cargarDatos();
    }

    private void inicializarUI() {
        tablaOrdenes = new TableView<>();

        TableColumn<OrdenDeCompraDTO, Long> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<OrdenDeCompraDTO, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        TableColumn<OrdenDeCompraDTO, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEnviar = new Button("Enviar");
            private final Button btnCancelar = new Button("Cancelar");
            private final Button btnRecibir = new Button("Recibir");
            private final HBox container = new HBox(5, btnEnviar, btnCancelar, btnRecibir);

            {
                btnEnviar.setOnAction(e -> {
                    //Acá hay que llamar al controller a hacer las acciones correspondientes
                });

                btnCancelar.setOnAction(e -> {
                    //Acá hay que llamar al controller a hacer las acciones correspondientes
                });

                btnRecibir.setOnAction(e -> {
                    //Acá hay que llamar al controller a hacer las acciones correspondientes
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    OrdenDeCompraDTO dto = getTableView().getItems().get(getIndex());
                    btnEnviar.setVisible("Pendiente".equals(dto.getEstado()));
                    btnCancelar.setVisible("Pendiente".equals(dto.getEstado()));
                    btnRecibir.setVisible("Enviada".equals(dto.getEstado()));
                    setGraphic(container);
                }
            }
        });

        tablaOrdenes.getColumns().addAll(colId, colEstado, colAcciones);
        setCenter(tablaOrdenes);
    }

    private void cargarDatos() {
        //Llamar al controller para traer los DTOs de OrdenesDeCompra
    }
}
