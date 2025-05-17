package com.nventory.userInterfaces;


import com.nventory.controller.MaestroArticuloController;
import com.nventory.model.Articulo;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class MaestroArticuloPanel extends BorderPane {

    private TableView<Articulo> tablaArticulos;
    private Button btnAgregar;

    public MaestroArticuloPanel(MaestroArticuloController controller) {
        construirUI();
        controller.setTablaArticulos(tablaArticulos);
    }

    private void construirUI() {
        Label titulo = new Label("Maestro Articulos");
        titulo.setFont(new Font("Arial", 24));
        titulo.setPadding(new Insets(10));
        setTop(titulo);

        tablaArticulos = new TableView<>();
        construirTabla();

        btnAgregar = new Button("+ AÑADIR");
        //btnAgregar.setStyle("-fx-background-color: #4ea3f1; -fx-text-fill: white;");
        btnAgregar.setOnAction(e -> {
            // evento dummy
        });

        HBox contenedorBoton = new HBox(btnAgregar);
        contenedorBoton.setPadding(new Insets(10));
        contenedorBoton.setSpacing(10);
        contenedorBoton.setStyle("-fx-alignment: center-right;");

        VBox contenidoCentro = new VBox(tablaArticulos, contenedorBoton);
        contenidoCentro.setSpacing(10);
        contenidoCentro.setPadding(new Insets(10));

        setCenter(contenidoCentro);
    }

    private void construirTabla() {
        TableColumn<Articulo, Long> colCodigo = new TableColumn<>("Codigo");
        colCodigo.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getCodArticulo()).asObject());

        TableColumn<Articulo, Integer> colStock = new TableColumn<>("Stock actual");
        colStock.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getStockActual()).asObject());

        TableColumn<Articulo, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNombreArticulo()));

        TableColumn<Articulo, String> colFechaBaja = new TableColumn<>("Fecha Baja");
        colFechaBaja.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getFechaHoraBajaArticulo() != null ?
                        data.getValue().getFechaHoraBajaArticulo().toLocalDate().toString() : "dd/mm/aa"));

        TableColumn<Articulo, String> colProveedor = new TableColumn<>("Proveedor determinado");
        colProveedor.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getArticuloProveedor() != null ?
                        data.getValue().getArticuloProveedor().getProveedor().getCodProveedor().toString() : ""));

        TableColumn<Articulo, Void> colAccion = new TableColumn<>("Accion");
        colAccion.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("Modificar");
            private final Button btnBorrar = new Button("Eliminar");
            private final Button btnProveedor = new Button("Seleccionar Proveedor");

            {
                btnEditar.setOnAction(e -> {
                    Articulo articulo = getTableView().getItems().get(getIndex());
                    // Acción editar
                });

                btnBorrar.setOnAction(e -> {
                    Articulo articulo = getTableView().getItems().get(getIndex());
                    // Acción borrar
                });

                btnProveedor.setOnAction(e -> {
                    Articulo articulo = getTableView().getItems().get(getIndex());
                    // Acción seleccionar proveedor
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(5, btnEditar, btnBorrar, btnProveedor);
                    setGraphic(box);
                }
            }
        });

        tablaArticulos.getColumns().addAll(colCodigo, colStock, colNombre, colFechaBaja, colProveedor, colAccion);
        tablaArticulos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
}
