package com.nventory.userInterfaces;

import com.nventory.DTO.ProveedorDTO;
import com.nventory.controller.OrdenDeCompraController;
import com.nventory.DTO.OrdenDeCompraDTO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

        TableColumn<OrdenDeCompraDTO, Long> colId = new TableColumn<>("Código");
        colId.setCellValueFactory(new PropertyValueFactory<>("codigo"));

        TableColumn<OrdenDeCompraDTO, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        TableColumn<OrdenDeCompraDTO, String> colProveedor = new TableColumn<>("Proveedor");
        colProveedor.setCellValueFactory(new PropertyValueFactory<>("proveedor"));

        TableColumn<OrdenDeCompraDTO, String> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalOrden"));

        TableColumn<OrdenDeCompraDTO, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEnviar = new Button("Enviar al Proveedor");
            private final Button btnCancelar = new Button("Cancelar");
            private final Button btnRecibir = new Button("Recibir Mercadería");
            private final Button btnEditar = new Button("Editar Artículos");
            private final HBox container = new HBox(5, btnEnviar, btnCancelar, btnRecibir, btnEditar);


            {
                btnEditar.setOnAction(e -> {
                    OrdenDeCompraDTO dto = getTableView().getItems().get(getIndex());
                    abrirVentanaEditarArticulos(dto.getCodigo(), btnEditar);
                });

                btnEnviar.setOnAction(e -> {
                    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmacion.setTitle("Confirmación");
                    confirmacion.setHeaderText(null);
                    confirmacion.setContentText("¿Está seguro que desea enviar esta orden al proveedor?");

                    ButtonType btnSi = new ButtonType("Sí");
                    ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

                    confirmacion.getButtonTypes().setAll(btnSi, btnNo);

                    confirmacion.showAndWait().ifPresent(respuesta -> {
                        if (respuesta == btnSi) {
                            OrdenDeCompraDTO dto = getTableView().getItems().get(getIndex());
                            controller.enviarOrdenDeCompra(dto.getCodigo());
                            cargarDatos();
                        }
                    });
                });


                btnCancelar.setOnAction(e -> {
                    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmacion.setTitle("Confirmación");
                    confirmacion.setHeaderText(null);
                    confirmacion.setContentText("¿Está seguro que desea cancelar esta orden?");

                    ButtonType btnSi = new ButtonType("Sí");
                    ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

                    confirmacion.getButtonTypes().setAll(btnSi, btnNo);

                    confirmacion.showAndWait().ifPresent(respuesta -> {
                        if (respuesta == btnSi) {
                            OrdenDeCompraDTO dto = getTableView().getItems().get(getIndex());
                            controller.cancelarOrdenDeCompra(dto.getCodigo());
                            cargarDatos();

                        }
                    });
                });

                btnRecibir.setOnAction(e -> {
                    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmacion.setTitle("Confirmación");
                    confirmacion.setHeaderText(null);
                    confirmacion.setContentText("¿Confirma la recepción de la mercadería?");

                    ButtonType btnSi = new ButtonType("Sí");
                    ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

                    confirmacion.getButtonTypes().setAll(btnSi, btnNo);

                    confirmacion.showAndWait().ifPresent(respuesta -> {
                        if (respuesta == btnSi) {
                            OrdenDeCompraDTO dto = getTableView().getItems().get(getIndex());
                            controller.recibirOrdenDeCompra(dto.getCodigo());
                            cargarDatos();
                        }
                    });
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
                    btnEditar.setVisible("Pendiente".equals(dto.getEstado()));
                    btnRecibir.setVisible("Enviada".equals(dto.getEstado()));
                    setGraphic(container);
                }
            }
        });

        tablaOrdenes.getColumns().addAll(colId, colEstado, colProveedor, colTotal, colAcciones);
        setCenter(tablaOrdenes);

        Button btnNuevaOrden = new Button("Nueva Orden de Compra");
        btnNuevaOrden.setOnAction(e -> crearNuevaOrden());

        // Barra superior con botón
        HBox barraSuperior = new HBox(10, btnNuevaOrden);
        barraSuperior.setStyle("-fx-padding: 10; -fx-alignment: center_left;"); // opcional: estilo CSS
        setTop(barraSuperior);
    }

    private void abrirVentanaEditarArticulos(Long codigo, Node origen) {
        Stage ventana = new Stage();
        OrdenCompraArticuloPanel panelArt = new OrdenCompraArticuloPanel(controller, codigo);
        Scene escena = new Scene(panelArt, 1280, 720);
        ventana.setScene(escena);
        ventana.setTitle("Editar Artículos de la Orden de Compra");

        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.initOwner(((Stage) origen.getScene().getWindow())); // obtenemos el owner desde el botón u otro nodo

        ventana.showAndWait();
        cargarDatos();
    }

    private void crearNuevaOrden() {
        List<ProveedorDTO> proveedores = controller.obtenerTodosLosProveedores();
        if (proveedores.isEmpty()) {
            Alert alerta = new Alert(Alert.AlertType.WARNING, "No hay proveedores disponibles.");
            alerta.showAndWait();
            return;
        }

        // Mapeo: nombre → proveedor
        Map<String, ProveedorDTO> mapaNombreProveedor = new HashMap<>();
        List<String> nombresProveedores = new ArrayList<>();
        for (ProveedorDTO proveedor : proveedores) {
            String nombre = proveedor.getNombreProveedor();
            mapaNombreProveedor.put(nombre, proveedor);
            nombresProveedores.add(nombre);
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(nombresProveedores.get(0), nombresProveedores);
        dialog.setTitle("Nueva Orden de Compra");
        dialog.setHeaderText("Seleccionar un Proveedor");
        dialog.setContentText("Proveedor:");

        dialog.showAndWait().ifPresent(nombreSeleccionado -> {
            ProveedorDTO proveedorSeleccionado = mapaNombreProveedor.get(nombreSeleccionado);
            Long nuevaOrdenId = controller.crearOrdenDeCompra(proveedorSeleccionado.getCodProveedor());
            abrirVentanaEditarArticulos(nuevaOrdenId, tablaOrdenes);
            cargarDatos();
        });
    }

    private void cargarDatos() {
        List<OrdenDeCompraDTO> ordenes = controller.obtenerTodasOrdenesDeCompra();
        ObservableList<OrdenDeCompraDTO> observableList = FXCollections.observableArrayList(ordenes);
        tablaOrdenes.setItems(observableList);
    }
}
