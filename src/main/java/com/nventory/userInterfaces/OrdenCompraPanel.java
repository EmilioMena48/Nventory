package com.nventory.userInterfaces;

import com.nventory.DTO.*;
import com.nventory.controller.OrdenDeCompraController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.math.BigDecimal;
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
        colId.setCellValueFactory(new PropertyValueFactory<>("codOrdenDeCompra"));

        TableColumn<OrdenDeCompraDTO, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoOrdenDeCompra"));

        TableColumn<OrdenDeCompraDTO, String> colProveedor = new TableColumn<>("Proveedor");
        colProveedor.setCellValueFactory(new PropertyValueFactory<>("proveedor"));

        TableColumn<OrdenDeCompraDTO, String> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalOrden"));

        TableColumn<OrdenDeCompraDTO, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEnviar = new Button("Enviar al Proveedor");
            private final Button btnCancelar = new Button("Cancelar");
            private final Button btnRecibir = new Button("Recibir Mercadería");
            private final Button btnVerArticulos = new Button("Ver Artículos");
            private final HBox container = new HBox(5, btnEnviar, btnCancelar, btnRecibir, btnVerArticulos);

            {
                btnVerArticulos.setOnAction(e -> {
                    OrdenDeCompraDTO dto = getTableView().getItems().get(getIndex());
                    abrirVentanaEditarArticulos(dto.getCodOrdenDeCompra(), dto.getEstadoOrdenDeCompra(), btnVerArticulos);
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
                            controller.enviarOrdenDeCompra(dto.getCodOrdenDeCompra());
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
                            controller.cancelarOrdenDeCompra(dto.getCodOrdenDeCompra());
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
                            controller.recibirOrdenDeCompra(dto.getCodOrdenDeCompra());
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
                    btnEnviar.setVisible("Pendiente".equals(dto.getEstadoOrdenDeCompra()));
                    btnCancelar.setVisible("Pendiente".equals(dto.getEstadoOrdenDeCompra()));
                    btnRecibir.setVisible("Enviada".equals(dto.getEstadoOrdenDeCompra()));
                    setGraphic(container);
                }
            }
        });

        tablaOrdenes.getColumns().addAll(colId, colEstado, colProveedor, colTotal, colAcciones);
        setCenter(tablaOrdenes);

        Button btnNuevaOrden = new Button("Nueva Orden de Compra");
        btnNuevaOrden.setOnAction(e -> seleccionarMetodoCreacion());

        HBox barraSuperior = new HBox(10, btnNuevaOrden);
        barraSuperior.setStyle("-fx-padding: 10; -fx-alignment: center_left;");
        setTop(barraSuperior);
    }

    private void abrirVentanaEditarArticulos(Long codigo, String estado, Node origen) {
        Stage ventana = new Stage();
        OrdenCompraArticuloPanel panelArt = new OrdenCompraArticuloPanel(controller, codigo, estado);
        Scene escena = new Scene(panelArt, 1280, 720);
        ventana.setScene(escena);
        ventana.setTitle("Artículos de la Orden de Compra");

        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.initOwner(((Stage) origen.getScene().getWindow()));

        ventana.showAndWait();
        cargarDatos();
    }

    private void mostrarDialogoProveedorYCantidad(Long codArticulo, String nombreArticulo) {
        SugerenciaOrdenDTO sugerencia = controller.obtenerSugerenciaParaArticulo(codArticulo);
        List<ProveedorArticuloDTO> proveedores = controller.obtenerProveedoresParaArticulo(codArticulo);

        if (proveedores.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "No hay proveedores disponibles para este artículo.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nueva Orden por Artículo");
        dialog.setHeaderText("Crear orden para: " + nombreArticulo);

        // UI Elements
        TableView<ProveedorArticuloDTO> proveedorTable = new TableView<>();

        TableColumn<ProveedorArticuloDTO, String> colProveedor = new TableColumn<>("Proveedor");
        colProveedor.setCellValueFactory(new PropertyValueFactory<>("nombreProveedor"));

        TableColumn<ProveedorArticuloDTO, BigDecimal> colPrecio = new TableColumn<>("Precio Unitario");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));

        TableColumn<ProveedorArticuloDTO, Integer> colDemora = new TableColumn<>("Demora (días)");
        colDemora.setCellValueFactory(new PropertyValueFactory<>("demoraEntregaDias"));

        TableColumn<ProveedorArticuloDTO, BigDecimal> colCostoPedido = new TableColumn<>("Costo Pedido");
        colCostoPedido.setCellValueFactory(new PropertyValueFactory<>("costoPedido"));

        proveedorTable.getColumns().addAll(colProveedor, colPrecio, colDemora, colCostoPedido);
        proveedorTable.setItems(FXCollections.observableArrayList(proveedores));

        Label cantidadLabel = new Label("Cantidad sugerida:");
        TextField cantidadField = new TextField(sugerencia.getCantidadSugerida().toString());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Artículo seleccionado:"), 0, 0);
        grid.add(new Label(nombreArticulo), 1, 0);
        grid.add(new Label("Seleccione proveedor:"), 0, 1);
        grid.add(proveedorTable, 1, 1);
        grid.add(cantidadLabel, 0, 2);
        grid.add(cantidadField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                ProveedorArticuloDTO proveedorSeleccionado = proveedorTable.getSelectionModel().getSelectedItem();
                if (proveedorSeleccionado == null) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Debe seleccionar un proveedor.");
                    return;
                }

                try {
                    int cantidad = (int) Long.parseLong(cantidadField.getText());
                    crearYAbrirNuevaOrdenPorArticulo(codArticulo, proveedorSeleccionado.getCodProveedor(), cantidad);
                } catch (NumberFormatException e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Cantidad ingresada inválida.");
                }
            }
        });
    }
    private void mostrarOpcionesOrdenExistente(Long codOrdenExistente, ProveedorDTO proveedorSeleccionado, Long codArticulo, String nombreArticulo) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Orden ya existente");
        alerta.setHeaderText("Ya existe una orden abierta");
        alerta.setContentText("¿Qué desea hacer?");

        ButtonType irAOrden = new ButtonType("Ir a la orden existente");
        ButtonType crearNueva = new ButtonType("Crear nueva orden");
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        alerta.getButtonTypes().setAll(irAOrden, crearNueva, cancelar);

        alerta.showAndWait().ifPresent(opcion -> {
            if (opcion == irAOrden) {
                abrirVentanaEditarArticulos(codOrdenExistente, tablaOrdenes);
            } else if (opcion == crearNueva) {
                if (proveedorSeleccionado == null) {
                    mostrarDialogoProveedorYCantidad(codArticulo, nombreArticulo);
                }
                crearYAbrirNuevaOrden(proveedorSeleccionado.getCodProveedor());
            }
            // Si cancela, no se hace nada
        });
    }

    private void abrirVentanaEditarArticulos(Long codOrdenCompra, Node origen) {
        String estadoOrden =  controller.obtenerEstadoDeUnaOrden(codOrdenCompra);
        Stage ventana = new Stage();
        OrdenCompraArticuloPanel panelArt = new OrdenCompraArticuloPanel(controller, codOrdenCompra, estadoOrden);
        Scene escena = new Scene(panelArt, 1280, 720);
        ventana.setScene(escena);
        ventana.setTitle("Editar Artículos de la Orden de Compra");

        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.initOwner(((Stage) origen.getScene().getWindow())); // obtenemos el owner desde el botón

        ventana.showAndWait();
        cargarDatos();
    }

    private void seleccionarMetodoCreacion() {
        List<String> opciones = List.of("Por Proveedor", "Por Artículo");

        ChoiceDialog<String> dialog = new ChoiceDialog<>(opciones.getFirst(), opciones);
        dialog.setTitle("Crear Orden de Compra");
        dialog.setHeaderText("Selecciona cómo deseas crear la orden");
        dialog.setContentText("Método:");

        dialog.showAndWait().ifPresent(opcion -> {
            if (opcion.equals("Por Proveedor")) {
                crearOrdenPorProveedor();
            } else if (opcion.equals("Por Artículo")) {
                crearOrdenPorArticulo();
            }
        });
    }

    private void crearOrdenPorProveedor() {
        List<ProveedorDTO> proveedores = controller.obtenerTodosLosProveedores();
        if (proveedores.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "No hay proveedores disponibles.");
            return;
        }

        Map<String, ProveedorDTO> mapaNombreProveedor = new HashMap<>();
        List<String> nombresProveedores = new ArrayList<>();
        for (ProveedorDTO proveedor : proveedores) {
            String nombre = proveedor.getNombreProveedor();
            mapaNombreProveedor.put(nombre, proveedor);
            nombresProveedores.add(nombre);
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(nombresProveedores.getFirst(), nombresProveedores);
        dialog.setTitle("Seleccionar Proveedor");
        dialog.setHeaderText("Seleccione un proveedor para la orden");
        dialog.setContentText("Proveedor:");

        dialog.showAndWait().ifPresent(nombreSeleccionado -> {
            ProveedorDTO proveedorSeleccionado = mapaNombreProveedor.get(nombreSeleccionado);
            Long codProveedor = proveedorSeleccionado.getCodProveedor();

            controller.buscarOrdenAbiertaPorProveedor(codProveedor).ifPresentOrElse(
                    ordenExistente -> mostrarOpcionesOrdenExistente(ordenExistente.getCodOrdenDeCompra(), proveedorSeleccionado, null, null),
                    () -> crearYAbrirNuevaOrden(codProveedor)
            );
        });
    }


    private void crearOrdenPorArticulo() {
        List<ArticuloDTO> articulos = controller.obtenerTodosLosArticulos();
        if (articulos.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "No hay artículos disponibles.");
            return;
        }

        Map<String, ArticuloDTO> mapaNombreArticulo = new HashMap<>();
        List<String> nombresArticulos = new ArrayList<>();
        for (ArticuloDTO articulo : articulos) {
            String nombre = articulo.getNombreArticulo();
            mapaNombreArticulo.put(nombre, articulo);
            nombresArticulos.add(nombre);
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(nombresArticulos.getFirst(), nombresArticulos);
        dialog.setTitle("Seleccionar Artículo");
        dialog.setHeaderText("Seleccione un artículo para la orden");
        dialog.setContentText("Artículo:");

        dialog.showAndWait().ifPresent(nombreSeleccionado -> {
            ArticuloDTO articuloSeleccionado = mapaNombreArticulo.get(nombreSeleccionado);
            Long codArticulo = articuloSeleccionado.getCodArticulo();

            controller.buscarOrdenAbiertaPorArticulo(codArticulo).ifPresentOrElse(
                    ordenExistente -> mostrarOpcionesOrdenExistente(ordenExistente.getCodOrdenDeCompra(), null, codArticulo, articuloSeleccionado.getNombreArticulo() ),
                    () -> mostrarDialogoProveedorYCantidad(codArticulo, articuloSeleccionado.getNombreArticulo())
            );
        });
    }


    private void crearYAbrirNuevaOrden(Long codProveedor) {
        Long nuevaOrdenId = controller.crearOrdenDeCompra(codProveedor);
        cargarDatos();
        abrirVentanaEditarArticulos(nuevaOrdenId, tablaOrdenes);

    }

    private void crearYAbrirNuevaOrdenPorArticulo(Long codArticulo, Long codProveedor, int cantidadSolicitada) {
        Long nuevaOrdenId = controller.crearOrdenDeCompraPorArticulo(codArticulo, codProveedor, cantidadSolicitada);
        cargarDatos();
        abrirVentanaEditarArticulos(nuevaOrdenId, tablaOrdenes);

    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje);
        alerta.showAndWait();
    }

    private void cargarDatos() {
        List<OrdenDeCompraDTO> ordenes = controller.obtenerTodasOrdenesDeCompra();
        ObservableList<OrdenDeCompraDTO> observableList = FXCollections.observableArrayList(ordenes);
        tablaOrdenes.setItems(observableList);
    }
}
