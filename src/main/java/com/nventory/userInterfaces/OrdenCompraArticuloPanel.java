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
import java.util.ArrayList;
import java.util.List;

public class OrdenCompraArticuloPanel extends BorderPane {

    private final OrdenDeCompraController controller;
    private final Long idOrdenDeCompra;
    private final String estadoOrden;

    private TableView<OrdenDeCompraArticuloDTO> tablaArticulos;
    private ComboBox<ArticuloProveedorDTO> comboArticulo;
    private TextField txtCantidad;
    private HBox form;

    public OrdenCompraArticuloPanel(OrdenDeCompraController controller, Long idOrdenDeCompra, String estadoOrden) {
        this.controller = controller;
        this.idOrdenDeCompra = idOrdenDeCompra;
        this.estadoOrden = estadoOrden;
        inicializarUI();
        cargarArticulos();
        if ("Pendiente".equals(estadoOrden)) {
            cargarArticulosProveedor();
        }
    }

    private void inicializarUI() {
        tablaArticulos = new TableView<>();


        TableColumn<OrdenDeCompraArticuloDTO, String> colArticulo = new TableColumn<>("Artículo");
        colArticulo.setCellValueFactory(new PropertyValueFactory<>("nombreArticulo"));
        colArticulo.prefWidthProperty().bind(tablaArticulos.widthProperty().multiply(0.15));

        TableColumn<OrdenDeCompraArticuloDTO, BigDecimal> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitarioOCA"));
        colPrecio.prefWidthProperty().bind(tablaArticulos.widthProperty().multiply(0.10));

        TableColumn<OrdenDeCompraArticuloDTO, Integer> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidadSolicitadaOCA"));
        colCantidad.prefWidthProperty().bind(tablaArticulos.widthProperty().multiply(0.10));

        TableColumn<OrdenDeCompraArticuloDTO, String> colSubtotal = new TableColumn<>("Subtotal");
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subTotalOCA"));
        colSubtotal.prefWidthProperty().bind(tablaArticulos.widthProperty().multiply(0.10));

        TableColumn<OrdenDeCompraArticuloDTO, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.prefWidthProperty().bind(tablaArticulos.widthProperty().multiply(0.55));
        colAcciones.setCellFactory(param -> new TableCell<>(){
            private final Button btnModificar = new Button("Modificar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox hbox = new HBox(5, btnModificar, btnEliminar);

            {
                hbox.getStylesheets().add(getClass().getResource("/styles/estilosOrdenCompra.css").toExternalForm());

                btnModificar.getStyleClass().add("button-generar-ordenes");
                btnModificar.setOnAction(e -> {
                    OrdenDeCompraArticuloDTO dto = getTableView().getItems().get(getIndex());
                    TextInputDialog dialog = new TextInputDialog(String.valueOf(dto.getCantidadSolicitadaOCA()));
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
                            controller.modificarCantidadArticulo(idOrdenDeCompra, dto.getCodOrdenCompraA(), nuevoCantidad);
                            cargarArticulos();
                        } catch (NumberFormatException exception) {
                            mostrarError("Cantidad inválida. Ingrese un número entero.");
                        }
                    });
                });

                btnEliminar.getStyleClass().add("button-cancelar");
                btnEliminar.setOnAction(e -> {
                    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmacion.setTitle("Confirmacion");
                    confirmacion.setHeaderText(null);
                    confirmacion.setContentText("¿Esta seguro que desea eliminar el árticulo de la orden de compra?");

                    ButtonType btnSi = new ButtonType("Si");
                    ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

                    confirmacion.getButtonTypes().setAll(btnSi, btnNo);

                    // Cargar CSS
                    DialogPane dialogPane = confirmacion.getDialogPane();
                    dialogPane.getStylesheets().add(getClass().getResource("/styles/estilosOrdenCompra.css").toExternalForm());
                    dialogPane.getStyleClass().add("alerta-personalizada");

                    // Estilizar los botones del Alert
                    confirmacion.setOnShown(ev -> {
                        Button btnYes = (Button) confirmacion.getDialogPane().lookupButton(btnSi);
                        Button btnNoBtn = (Button) confirmacion.getDialogPane().lookupButton(btnNo);

                        btnYes.getStyleClass().add("button-nueva-orden");
                        btnNoBtn.getStyleClass().add("button-cancelar");
                    });
                    confirmacion.showAndWait().ifPresent(respuesta -> {
                        if (respuesta.equals(btnSi)) {
                            OrdenDeCompraArticuloDTO dto = getTableView().getItems().get(getIndex());
                            controller.eliminarArticuloDeOrden(idOrdenDeCompra, dto.getCodOrdenCompraA());
                            cargarArticulos();
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
                    setGraphic("Pendiente".equals(estadoOrden) ? hbox : null);
                }
            }
        });

        tablaArticulos.getColumns().addAll(colArticulo, colPrecio, colCantidad, colSubtotal, colAcciones);

        // Formulario - solo visible si estado es Pendiente
        if ("Pendiente".equals(estadoOrden)) {
            comboArticulo = new ComboBox<>();
            comboArticulo.setCellFactory(listView -> new ListCell<>() {
                @Override
                protected void updateItem(ArticuloProveedorDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getNombre() + " - $" + item.getPrecioUnitario() + " c/u" + " Costo Pedido: " + item.getCostoPedido());
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

            Button btnAgregar = new Button("Agregar");
            btnAgregar.getStyleClass().add("button-nueva-orden");

            form = new HBox(10, comboArticulo, txtCantidad, btnAgregar);
            form.getStylesheets().add(getClass().getResource("/styles/estilosOrdenCompra.css").toExternalForm());

            btnAgregar.setOnAction(e -> {
                String textoCantidad = txtCantidad.getText();
                try {
                    int cantidad = Integer.parseInt(textoCantidad);
                    if (cantidad <= 0) {
                        throw new NumberFormatException();
                    }
                    agregarArticulo();
                } catch (NumberFormatException ex) {
                    Alert alerta = new Alert(Alert.AlertType.ERROR);
                    alerta.setTitle("Error de Validación");
                    alerta.setHeaderText("Cantidad inválida");
                    alerta.setContentText("Debe ingresar un número mayor a 0.");
                    alerta.showAndWait();
                }
            });
        }

        VBox vbox = new VBox(10, tablaArticulos);
        if ("Pendiente".equals(estadoOrden)) {
            vbox.getChildren().add(form);
        }
        setCenter(vbox);
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
        if (articulos == null) {
            articulos = new ArrayList<>();
        }
        tablaArticulos.setItems(FXCollections.observableArrayList(articulos));
    }

    private void cargarArticulosProveedor() {
        List<ArticuloProveedorDTO> disponibles = controller.obtenerArticulosProveedorDisponibles(idOrdenDeCompra);
        if (disponibles == null) {
            disponibles = new ArrayList<>();
        }
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
            controller.agregarArticuloAOrden(idOrdenDeCompra, (long) seleccionado.getId(), cantidad);
            cargarArticulos();
        } catch (NumberFormatException ex) {
            mostrarError("Cantidad inválida. Ingrese un número entero.");
        }
    }

}