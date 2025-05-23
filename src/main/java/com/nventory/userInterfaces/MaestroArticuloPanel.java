package com.nventory.userInterfaces;


import com.nventory.DTO.ArticuloDTO;
import com.nventory.DTO.ArticuloProveedorDTO;
import com.nventory.controller.MaestroArticuloController;
import com.nventory.model.Articulo;
import com.nventory.model.ArticuloProveedor;
import com.nventory.model.Proveedor;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MaestroArticuloPanel extends BorderPane {

    private TableView<Articulo> tablaArticulos;
    private Button btnAgregar;
    private Button btnListarReponer;
    private Button btnProductosFaltantes;
    private Button btnAjusteInventario;
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

                    // Acá llamás al controller para guardar el nuevo artículo
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

        btnAjusteInventario = new Button("Ajuste inventario");
        btnAjusteInventario.setOnAction(e ->{
            //llamar al metodo del controller
        });

        btnListarReponer.setStyle("-fx-background-color: #4ea3f1; -fx-text-fill: white;");
        btnProductosFaltantes.setStyle("-fx-background-color: #4ea3f1; -fx-text-fill: white;");
        btnAjusteInventario.setStyle("-fx-background-color: #4ea3f1; -fx-text-fill: white;");

        HBox contenedorFiltros = new HBox(10, btnListarReponer, btnProductosFaltantes, btnAjusteInventario);
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

                    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmacion.setTitle("Confirmar baja");
                    confirmacion.setHeaderText("¿Seguro que querés dar de baja este artículo?");
                    confirmacion.setContentText("Esta acción marcará el artículo como dado de baja.");

                    Optional<ButtonType> resultado = confirmacion.showAndWait();
                    if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                        ArticuloDTO articuloDTO = new ArticuloDTO();
                        articuloDTO.setCodArticulo(articulo.getCodArticulo());
                        articuloDTO.setFechaHoraBajaArticulo(LocalDateTime.now()); //Se setea la fecha actual para la baja

                        //Llamamos al controller pasandole el dto que contiene la fecha de baja
                        controller.darDeBajaArticulo(articuloDTO);
                    }
                });

                //BOTON DE MOSTRAR TODOS LOS PROVEEDORES DEL ARTICULO SELECCIONADO
                btnProveedor.setOnAction(e ->{
                    Articulo articulo = getTableView().getItems().get(getIndex());
                    //Obtener el código del artículo
                    Long codArticulo = articulo.getCodArticulo();

                    //Obtener los proveedores desde el controller
                    List<ArticuloProveedorDTO> proveedoresDisponiblesDTO = controller.obtenerProveedoresDeEseArticulo(codArticulo);

                    Stage stage = new Stage();
                    stage.setTitle("Seleccionar proveedor para " + articulo.getNombreArticulo());
                    stage.initModality(Modality.APPLICATION_MODAL);

                    ListView<ArticuloProveedorDTO> listView = new ListView<>(FXCollections.observableArrayList(proveedoresDisponiblesDTO));
                    listView.setCellFactory(lv -> new ListCell<>() {
                        @Override
                        protected void updateItem(ArticuloProveedorDTO item, boolean empty) {
                            super.updateItem(item, empty);
                            setText(empty || item == null ? null : item.getNombre());
                        }
                    });
                    //UNA VEZ QUE ME TRAJE LOS PROVEEDORES, SELECCIONAR UNO
                    Button btnSeleccionar = new Button("Asignar como predeterminado");
                    btnSeleccionar.setOnAction(ev -> {
                        ArticuloProveedorDTO Proveedorseleccionado = listView.getSelectionModel().getSelectedItem();
                        if (Proveedorseleccionado != null) {
                            //se captura el codigo del articuloProveedor seleccionado
                            Long codArticuloProveedor = Proveedorseleccionado.getId();

                            // Acá se actualiza el proveedor predeterminado
                            controller.asignarProveedorPredeterminado(codArticuloProveedor);
                            stage.close(); // Cierra la ventana
                        }
                    });

                    VBox layout = new VBox(10, listView, btnSeleccionar);
                    layout.setPadding(new Insets(15));
                    layout.setAlignment(Pos.CENTER);
                    stage.setScene(new Scene(layout, 300, 400));
                    stage.showAndWait();

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
