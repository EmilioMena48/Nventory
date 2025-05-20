package com.nventory.userInterfaces;


import com.nventory.DTO.ArticuloDTO;
import com.nventory.controller.MaestroArticuloController;
import com.nventory.model.Articulo;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class MaestroArticuloPanel extends BorderPane {

    private TableView<Articulo> tablaArticulos;
    private Button btnAgregar;
    private Button btnListarReponer;
    private Button btnProductosFaltantes;
    private Button btnProveedoresPorArticulo;
    private final MaestroArticuloController controller;

    public MaestroArticuloPanel(MaestroArticuloController controller) {
        this.controller = controller;
        construirUI();
        controller.setTablaArticulos(tablaArticulos);
    }

    //Se crea la estructura de la pantalla
    private void construirUI() {
        Label titulo = new Label("Maestro Articulos");
        titulo.setFont(new Font("Arial", 24));
        titulo.setPadding(new Insets(10));
        setTop(titulo);

        tablaArticulos = new TableView<>();
        construirTabla();

        btnAgregar = new Button("+ AÑADIR");
        btnAgregar.setStyle("-fx-background-color: #4ea3f1; -fx-text-fill: white;");

        //BOTON DE AÑADIR ARTICULO
        btnAgregar.setOnAction(e -> {
            Stage ventanaAlta = new Stage();
            ventanaAlta.setTitle("Nuevo Artículo");
            ventanaAlta.initModality(Modality.APPLICATION_MODAL);
            VBox layout = new VBox(10);
            layout.setPadding(new Insets(20));

            // Campos vacíos para alta
            TextField txtNombre = new TextField();
            TextArea txtDescripcion = new TextArea();
            TextField txtStock = new TextField();
            TextField txtCostoAlmacenamiento = new TextField();
            TextField txtCostoCompra = new TextField();
            TextField txtCostoCapital = new TextField();
            TextField txtDemanda = new TextField();
            DatePicker fechaBaja = new DatePicker();

            // Botones
            Button btnGuardar = new Button("Guardar");
            Button btnCancelar = new Button("Cancelar");

            HBox botones = new HBox(10, btnGuardar, btnCancelar);
            botones.setAlignment(Pos.CENTER_RIGHT);

            layout.getChildren().addAll(
                    new Label("Nombre:"), txtNombre,
                    new Label("Descripción:"), txtDescripcion,
                    new Label("Stock actual:"), txtStock,
                    new Label("Costo de almacenamiento:"), txtCostoAlmacenamiento,
                    new Label("Costo de compra:"), txtCostoCompra,
                    new Label("Costo de capital inmovilizado:"), txtCostoCapital,
                    new Label("Demanda del artículo:"), txtDemanda,
                    new Label("Fecha de baja:"), fechaBaja,
                    botones
            );

            Scene scene = new Scene(layout);
            ventanaAlta.setScene(scene);
            ventanaAlta.show();

            btnGuardar.setOnAction(event -> {
                try {
                    ArticuloDTO articuloDTO = new ArticuloDTO();
                    articuloDTO.setNombreArticulo(txtNombre.getText());
                    articuloDTO.setDescripcionArticulo(txtDescripcion.getText());
                    articuloDTO.setStockActual(Integer.valueOf(txtStock.getText()));
                    articuloDTO.setCostoAlmacenamiento(new BigDecimal(txtCostoAlmacenamiento.getText()));
                    articuloDTO.setCostoCompra(new BigDecimal(txtCostoCompra.getText()));
                    articuloDTO.setCostoCapitalInmovilizado(new BigDecimal(txtCostoCapital.getText()));
                    articuloDTO.setDemandaArt(Integer.parseInt(txtDemanda.getText()));
                    articuloDTO.setFechaHoraBajaArticulo(
                            fechaBaja.getValue() != null ? fechaBaja.getValue().atStartOfDay() : null
                    );

                    // Acá llamás a tu controller para guardar el nuevo artículo
                    controller.darDeAltaArticulo(articuloDTO);
                    ventanaAlta.close();

                } catch (NumberFormatException ex) {
                    Alert alerta = new Alert(Alert.AlertType.ERROR);
                    alerta.setTitle("Error de formato");
                    alerta.setHeaderText("Datos inválidos");
                    alerta.setContentText("Revisá que todos los campos numéricos tengan valores válidos.");
                    alerta.showAndWait();
                }
            });
            btnCancelar.setOnAction(event -> ventanaAlta.close());
        });

        //Boton lista a reponer
        btnListarReponer = new Button("Articulos a reponer");
        btnListarReponer.setOnAction(e ->{
            //llamar al metodo del controler
        });

        //Boton productos faltantes
        btnProductosFaltantes = new Button("Productos Faltantes");
        btnProductosFaltantes.setOnAction(e ->{
            //llamar al metodo del controller
        });
        //Boton de proveedores por articulo
        btnProveedoresPorArticulo = new Button("Proveedores por Artículo");
        btnProveedoresPorArticulo.setOnAction(e ->{
            //llamar al metodo del controler
        });

        btnListarReponer.setStyle("-fx-background-color: #5cb85c; -fx-text-fill: white;");
        btnProductosFaltantes.setStyle("-fx-background-color: #f0ad4e; -fx-text-fill: white;");
        btnProveedoresPorArticulo.setStyle("-fx-background-color: #5bc0de; -fx-text-fill: white;");

        HBox contenedorFiltros = new HBox(10, btnListarReponer, btnProductosFaltantes, btnProveedoresPorArticulo);
        contenedorFiltros.setPadding(new Insets(10));

        HBox contenedorBoton = new HBox(btnAgregar);
        contenedorBoton.setPadding(new Insets(10));
        contenedorBoton.setSpacing(10);
        contenedorBoton.setStyle("-fx-alignment: center-right;");

        VBox contenidoCentro = new VBox(tablaArticulos, contenedorFiltros, contenedorBoton);
        contenidoCentro.setSpacing(10);
        contenidoCentro.setPadding(new Insets(10));

        setCenter(contenidoCentro); //se asigna como contenido central
    }

    //Define las columnas de la tabla de articulos
    private void construirTabla() {
        TableColumn<Articulo, Long> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getCodArticulo()).asObject());

        TableColumn<Articulo, Integer> colStock = new TableColumn<>("Stock actual");
        colStock.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getStockActual()).asObject());

        TableColumn<Articulo, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNombreArticulo()));

        TableColumn<Articulo, String> colFechaBaja = new TableColumn<>("Fecha Baja");
        colFechaBaja.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getFechaHoraBajaArticulo() != null ?
                        data.getValue().getFechaHoraBajaArticulo().toLocalDate().toString() : "--/--/--")); //muestra nulo si no hay valor

        TableColumn<Articulo, String> colProveedor = new TableColumn<>("Proveedor determinado");
        colProveedor.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getArticuloProveedor() != null ?
                        data.getValue().getArticuloProveedor().getProveedor().getCodProveedor().toString() : "")); //muestra el proveedor predeterminado si existe

        TableColumn<Articulo, Void> colAccion = new TableColumn<>("Acción");
        colAccion.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("Modificar");
            private final Button btnBorrar = new Button("Eliminar");
            private final Button btnProveedor = new Button("Seleccionar Proveedor");


            {   //BOTON EDITAR ARTICULO
                btnEditar.setOnAction(e -> {
                    Articulo articulo = getTableView().getItems().get(getIndex());

                    Stage ventanaEdicion = new Stage();
                    ventanaEdicion.setTitle("Editar Artículo");
                    ventanaEdicion.initModality(Modality.APPLICATION_MODAL);
                    VBox layout = new VBox(10);
                    layout.setPadding(new Insets(20));

                    // Campos editables
                    TextField txtNombre = new TextField(articulo.getNombreArticulo());
                    TextArea txtDescripcion = new TextArea(articulo.getDescripcionArticulo());
                    TextField txtStock = new TextField(String.valueOf(articulo.getStockActual()));
                    TextField txtCostoAlmacenamiento = new TextField(String.valueOf(articulo.getCostoAlmacenamiento()));
                    TextField txtCostoCompra = new TextField(String.valueOf(articulo.getCostoCompra()));
                    TextField txtCostoCapital = new TextField(String.valueOf(articulo.getCostoCapitalInmovilizado()));
                    TextField txtDemanda = new TextField(String.valueOf(articulo.getDemandaArt()));
                    DatePicker fechaBaja = new DatePicker(
                            articulo.getFechaHoraBajaArticulo() != null ?
                                    articulo.getFechaHoraBajaArticulo().toLocalDate() : null
                    );

                    // Botones
                    Button btnGuardar = new Button("Guardar");
                    Button btnCancelar = new Button("Cancelar");

                    HBox botones = new HBox(10, btnGuardar, btnCancelar);
                    botones.setAlignment(Pos.CENTER_RIGHT);

                    layout.getChildren().addAll(
                            new Label("Nombre:"), txtNombre,
                            new Label("Descripción:"), txtDescripcion,
                            new Label("Stock actual:"), txtStock,
                            new Label("Costo de almacenamiento:"), txtCostoAlmacenamiento,
                            new Label("Costo de compra:"), txtCostoCompra,
                            new Label("Costo de capital inmovilizado:"), txtCostoCapital,
                            new Label("Demanda del artículo:"), txtDemanda,
                            new Label("Fecha de baja:"), fechaBaja,
                            botones
                    );

                    Scene scene = new Scene(layout);
                    ventanaEdicion.setScene(scene);
                    ventanaEdicion.show();

                    btnGuardar.setOnAction(event -> {
                        try {
                            ArticuloDTO articuloDTO = new ArticuloDTO();
                            articuloDTO.setCodArticulo(articulo.getCodArticulo());
                            articuloDTO.setNombreArticulo(txtNombre.getText());
                            articuloDTO.setDescripcionArticulo(txtDescripcion.getText());
                            articuloDTO.setStockActual(Integer.valueOf(txtStock.getText()));
                            articuloDTO.setCostoAlmacenamiento(new BigDecimal(txtCostoAlmacenamiento.getText()));
                            articuloDTO.setCostoCompra(new BigDecimal(txtCostoCompra.getText()));
                            articuloDTO.setCostoCapitalInmovilizado(new BigDecimal(txtCostoCapital.getText()));
                            articuloDTO.setDemandaArt(Integer.parseInt(txtDemanda.getText()));
                            articuloDTO.setFechaHoraBajaArticulo(
                                    fechaBaja.getValue() != null ? fechaBaja.getValue().atStartOfDay() : null
                            );

                            ventanaEdicion.close();
                            //Llama al controller para que actualice los datos
                            controller.actualizarArticulo(articuloDTO);

                        } catch (NumberFormatException ex) {
                            // Manejo básico de errores
                            Alert alerta = new Alert(Alert.AlertType.ERROR);
                            alerta.setTitle("Error de formato");
                            alerta.setHeaderText("Datos inválidos");
                            alerta.setContentText("Revisá que todos los campos numéricos tengan valores válidos.");
                            alerta.showAndWait();
                        }
                    });
                    btnCancelar.setOnAction(event -> ventanaEdicion.close());
                });


                //BOTON DE DAR DE BAJA
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
        //Agrega todas las columnas a la tabla
        tablaArticulos.getColumns().addAll(colCodigo, colStock, colNombre, colFechaBaja, colProveedor, colAccion);
        tablaArticulos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }


}
