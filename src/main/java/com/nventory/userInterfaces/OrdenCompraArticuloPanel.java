package com.nventory.userInterfaces;
import com.nventory.DTO.ArticuloProveedorDTO;
import com.nventory.DTO.OrdenDeCompraArticuloDTO;
import com.nventory.controller.OrdenDeCompraController;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.util.List;


public class OrdenCompraArticuloPanel extends BorderPane {

    private final OrdenDeCompraController controller;
    private final Long idOrdenDeCompra;

    private TableView<OrdenDeCompraArticuloDTO> tablaArticulos;
    private ComboBox<ArticuloProveedorDTO> comboArticulo;
    private TextField txtCantidad;
    private Button btnAgregar;

    public OrdenCompraArticuloPanel(OrdenDeCompraController controller, Long idOrdenDeCompra) {
        this.controller = controller;
        this.idOrdenDeCompra = idOrdenDeCompra;
        inicializarUI();
        cargarArticulos();
        cargarArticulosProveedor();
    }

    private void inicializarUI() {
        tablaArticulos = new TableView<>();

        TableColumn<OrdenDeCompraArticuloDTO, String> colArticulo = new TableColumn<>("Artículo");
        colArticulo.setCellValueFactory(new PropertyValueFactory<>("nombreArticulo"));

        TableColumn<OrdenDeCompraArticuloDTO, BigDecimal> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        TableColumn<OrdenDeCompraArticuloDTO, Integer> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        TableColumn<OrdenDeCompraArticuloDTO, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<>(){
            private final Button btnModificar = new Button("Modificar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox hbox = new HBox(5, btnModificar, btnEliminar);
            {
                btnModificar.setOnAction(e -> {
                    OrdenDeCompraArticuloDTO dto = getTableView().getItems().get(getIndex());
                    TextInputDialog dialog = new TextInputDialog(String.valueOf(dto.getCantidad()));
                    dialog.setTitle("Modificar Cantidad");
                    dialog.setHeaderText("Modificar cantidad a pedir del articulo");
                    dialog.setContentText("Ingrese nueva cantidad del articulo:");

                    dialog.showAndWait().ifPresent(nuevaCantidadStr -> {
                        try {
                            int nuevoCantidad = Integer.parseInt(nuevaCantidadStr);
                            if (nuevoCantidad <= 0) {
                                mostrarError("La cantidad debe ser mayor a 0.");
                                return;
                            }
                            controller.modificarCantidadArticulo(idOrdenDeCompra, dto.getId(), nuevoCantidad);
                            cargarArticulos();
                        } catch (NumberFormatException exception) {
                            mostrarError("Cantidad inválida. Ingrese un número entero.");
                        }
                    });
                });
                btnEliminar.setOnAction(e -> {
                    OrdenDeCompraArticuloDTO dto = getTableView().getItems().get(getIndex());
                    controller.eliminarArticuloDeOrden(idOrdenDeCompra, dto.getId());
                    cargarArticulos();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(hbox);
                }
            }
        });

        tablaArticulos.getColumns().addAll(colArticulo, colPrecio, colCantidad, colAcciones);

        // Formulario
        comboArticulo = new ComboBox<>();
        comboArticulo.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(ArticuloProveedorDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombre() + " - $" + item.getPrecioUnitario() + " c/u" );
                }
            }
        });

        comboArticulo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ArticuloProveedorDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombre() + " - $" + item.getPrecioUnitario() + " c/u");
                }
            }
        });

        txtCantidad = new TextField();
        txtCantidad.setPromptText("Cantidad");

        btnAgregar = new Button("Agregar");


        HBox form = new HBox(10, comboArticulo, txtCantidad, btnAgregar);
        VBox vbox = new VBox(10, tablaArticulos, form);
        setCenter(vbox);

        // Acciones de botones
        btnAgregar.setOnAction(e -> agregarArticulo());

    }

    private void mostrarError(String cantidadInvalida) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(cantidadInvalida);
        alerta.showAndWait();
    }

    private void cargarArticulos() {
        List<OrdenDeCompraArticuloDTO> articulos = controller.obtenerArticulosDeOrden(idOrdenDeCompra);
        tablaArticulos.setItems(FXCollections.observableArrayList(articulos));
    }

    private void cargarArticulosProveedor() {
        List<ArticuloProveedorDTO> disponibles = controller.obtenerArticulosProveedorDisponibles(idOrdenDeCompra);
        comboArticulo.setItems(FXCollections.observableArrayList(disponibles));
    }

    private void agregarArticulo() {
        ArticuloProveedorDTO seleccionado = comboArticulo.getValue();
        if (seleccionado == null) {
            mostrarError("Debe seleccionar un artículo.");
            return;
        }
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText());
            controller.agregarArticuloAOrden(idOrdenDeCompra, seleccionado.getId(), cantidad);
            cargarArticulos();
        } catch (NumberFormatException ex) {
            mostrarError("Cantidad inválida. Ingrese un número entero.");
        }
    }


}

