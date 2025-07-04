package com.nventory.userInterfaces;


import com.nventory.DTO.ArticuloDTO;
import com.nventory.DTO.ArticuloProveedorDTO;
import com.nventory.DTO.CGIDTO;
import com.nventory.DTO.StockMovimientoDTO;
import com.nventory.controller.MaestroArticuloController;
import com.nventory.model.Articulo;
import com.nventory.model.ArticuloProveedor;
import com.nventory.model.Proveedor;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

    private TableView<ArticuloDTO> tablaArticulos;
    private Button btnAgregar;
    private Button btnListarReponer;
    private Button btnProductosFaltantes;
    private Button btnAjusteInventario;
    private Button btnCalcularCGI;
    private final MaestroArticuloController controller;
    private final ObservableList<ArticuloDTO> listaArticulos = FXCollections.observableArrayList();


    public MaestroArticuloPanel(MaestroArticuloController controller) {
        this.controller = controller;
        construirUI();
        construirTabla();
        cargarArticulos();
    }

    //Se crea la estructura de la pantalla
    private void construirUI() {
        Label titulo = new Label("Maestro Articulos");
        titulo.setFont(new Font("Arial", 24));
        titulo.setPadding(new Insets(10));
        setTop(titulo);

        //Contenedor para el titulo y el boton añadir
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_RIGHT);
        header.setPadding(new Insets(10));
        header.setSpacing(10);

        btnAgregar = new Button("+ AÑADIR");
        btnAgregar.getStylesheets().add(getClass().getResource("/styles/estilosMaestroArticulos.css").toExternalForm());
        btnAgregar.getStyleClass().add("button-agregar");

        //BOTON DE AÑADIR ARTICULO
        btnAgregar.setOnAction(e -> {
            Stage ventanaAlta = new Stage();
            ventanaAlta.setTitle("Nuevo Artículo");
            ventanaAlta.initModality(Modality.APPLICATION_MODAL);
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(20));
            grid.setHgap(10);
            grid.setVgap(10);

            // Campos
            TextField txtNombre = new TextField();
            txtNombre.setPrefColumnCount(20);
            TextArea txtDescripcion = new TextArea();
            txtDescripcion.setWrapText(true);
            txtDescripcion.setPrefRowCount(2);
            txtDescripcion.setPrefColumnCount(20);
            TextField txtStockActual = new TextField();
            txtStockActual.setPrefColumnCount(5);
            TextField txtCostoAlmacenamiento = new TextField();
            txtCostoAlmacenamiento.setPrefColumnCount(5);
            TextField txtPrecioArticulo = new TextField();
            txtPrecioArticulo.setPrefColumnCount(5);
            TextField txtNivelServicioArticulo = new TextField();
            txtNivelServicioArticulo.setPrefColumnCount(5);
            TextField txtDiasEntreRevisiones = new TextField();
            txtDiasEntreRevisiones.setPrefColumnCount(5);
            TextField txtDemanda = new TextField();
            txtDemanda.setPrefColumnCount(5);
            // Agregar al GridPane
            grid.add(new Label("Nombre:"), 0, 0);
            grid.add(txtNombre, 1, 0);
            grid.add(new Label("Precio del artículo:"), 2, 0);
            grid.add(txtPrecioArticulo, 3, 0);
            grid.add(new Label("Descripción:"), 0, 1);
            grid.add(txtDescripcion, 1, 1);
            grid.add(new Label("Nivel de servicio:"), 2, 1);
            grid.add(txtNivelServicioArticulo, 3, 1);
            grid.add(new Label("Stock actual:"), 0, 2);
            grid.add(txtStockActual, 1, 2);
            grid.add(new Label("Días entre revisiones:"), 2, 2);
            grid.add(txtDiasEntreRevisiones, 3, 2);
            grid.add(new Label("Costo de almacenamiento:"), 0, 3);
            grid.add(txtCostoAlmacenamiento, 1, 3);
            grid.add(new Label("Demanda:"), 2, 3);
            grid.add(txtDemanda, 3, 3);
            // Botones
            Button btnGuardar = new Button("Guardar");
            Button btnCancelar = new Button("Cancelar");
            HBox botones = new HBox(10, btnGuardar, btnCancelar);
            botones.getStylesheets().add(getClass().getResource("/styles/estilosMaestroArticulos.css").toExternalForm());
            btnGuardar.getStyleClass().add("button-acciones");
            btnCancelar.getStyleClass().add("button-cancelar");
            botones.setAlignment(Pos.CENTER_RIGHT);
            grid.add(botones, 3, 5);
            Scene scene = new Scene(grid);
            ventanaAlta.setScene(scene);
            ventanaAlta.setResizable(false);
            ventanaAlta.show();

            btnGuardar.setOnAction(event -> {
                try {
                    ArticuloDTO articuloDTO = new ArticuloDTO();
                    articuloDTO.setNombreArticulo(txtNombre.getText());
                    articuloDTO.setDescripcionArticulo(txtDescripcion.getText());
                    articuloDTO.setStockActual(Integer.parseInt(txtStockActual.getText()));
                    articuloDTO.setCostoAlmacenamiento(new BigDecimal(txtCostoAlmacenamiento.getText()));
                    articuloDTO.setPrecioArticulo(new BigDecimal(txtPrecioArticulo.getText()));
                    articuloDTO.setNivelServicioArticulo(new BigDecimal(txtNivelServicioArticulo.getText()));
                    articuloDTO.setDiasEntreRevisiones(Integer.parseInt(txtDiasEntreRevisiones.getText()));
                    articuloDTO.setDemandaArt(Integer.parseInt(txtDemanda.getText()));

                    try {
                        controller.darDeAltaArticulo(articuloDTO);
                        cargarArticulos();
                        ventanaAlta.close();
                    } catch (IllegalArgumentException | IllegalStateException ex) {
                        mostrarError("Error al dar de alta el artículo: " + ex.getMessage());
                    }

                } catch (NumberFormatException ex) {
                    Alert alerta = new Alert(Alert.AlertType.ERROR);
                    alerta.setTitle("Error de formato");
                    alerta.setHeaderText("Datos inválidos");
                    alerta.setContentText("Revisá que todos los campos numéricos tengan valores válidos.");
                    alerta.showAndWait();                }
            });

            btnCancelar.setOnAction(event -> ventanaAlta.close());
        });
        header.getChildren().addAll(btnAgregar);
        // Configurar el título y el header en la parte superior
        HBox titleContainer = new HBox(titulo);
        titleContainer.setPadding(new Insets(10));
        titleContainer.setAlignment(Pos.CENTER_LEFT);

        VBox topContainer = new VBox(titleContainer, header);
        setTop(topContainer);

        tablaArticulos = new TableView<>();
        tablaArticulos.setItems(listaArticulos);

        //Boton lista a reponer
        btnListarReponer = new Button("Articulos a reponer");
        btnListarReponer.setOnAction(e -> {

            List<ArticuloDTO> articulosAReponer = controller.obtenerArticulosParaReponer();

            Stage popup = new Stage();
            popup.setTitle("Artículos a Reponer");
            popup.initModality(Modality.APPLICATION_MODAL);

            ListView<ArticuloDTO> listView = new ListView<>(FXCollections.observableArrayList(articulosAReponer));
            listView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(ArticuloDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText("Nombre: " + item.getNombreArticulo());
                    }
                }
            });

            VBox layout = new VBox(10, new Label(), listView);
            layout.setPadding(new Insets(10));

            Scene scene = new Scene(layout, 600, 400);
            popup.setScene(scene);
            popup.showAndWait();
        });


        // Botón productos faltantes
        btnProductosFaltantes = new Button("Productos Faltantes");
        btnProductosFaltantes.setOnAction(e -> {
            List<Articulo> articulosEnSS = controller.listarArticulosEnStockSeg();

            Stage popup = new Stage();
            popup.setTitle("Artículos en Stock de Seguridad");
            popup.initModality(Modality.APPLICATION_MODAL);

            Label mensaje = new Label("No se muestran artículos que no tengan una configuración de inventario");
            mensaje.setStyle("-fx-font-weight: bold; -fx-text-fill: #cc0000;");

            ListView<Articulo> listView = new ListView<>(FXCollections.observableArrayList(articulosEnSS));
            listView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Articulo item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        Integer stockSeg = item.getArticuloProveedor().getConfiguracionInventario().getStockSeguridad();
                        setText("Nombre: " + item.getNombreArticulo()
                                + " | Stock Actual: " + item.getStockActual()
                                + " | Stock Seguridad: " + stockSeg);
                    }
                }
            });

            VBox layout = new VBox(10, mensaje, listView);
            layout.setPadding(new Insets(10));

            Scene scene = new Scene(layout, 600, 400);
            popup.setScene(scene);
            popup.showAndWait();
        });


        btnAjusteInventario = new Button("Ajuste inventario");
        btnAjusteInventario.setOnAction(e -> {

            Stage popup = new Stage();
            popup.setTitle("Artículos en Stock de Seguridad");
            popup.initModality(Modality.APPLICATION_MODAL);

            ComboBox<String> comboArticulos = new ComboBox<>();

            List<ArticuloDTO> articulos = controller.listarArticulosDisponibles();
            for (ArticuloDTO articulo : articulos) {
                comboArticulos.getItems().add(articulo.getNombreArticulo());
            }

            TextField campoCantidadActual = new TextField();
            campoCantidadActual.setPromptText("Cantidad actual");
            campoCantidadActual.setEditable(false);

            TextField campoNuevaCantidad = new TextField();
            campoNuevaCantidad.setPromptText("Nueva cantidad");

            TextArea campoComentario = new TextArea();
            campoComentario.setPromptText("Comentario (opcional)");
            campoComentario.setPrefRowCount(3);

            comboArticulos.setOnAction(ev -> {
                String seleccionado = comboArticulos.getValue();
                if (seleccionado != null) {
                    Integer cantidadActual = controller.obtenerStockActual(seleccionado);
                    campoCantidadActual.setText(cantidadActual != null ? cantidadActual.toString() : "0");
                }
            });

            Button btnAceptar = new Button("Aceptar");
            Button btnCancelar = new Button("Cancelar");

            btnAceptar.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20 10 20;");
            btnCancelar.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20 10 20;");

            btnAceptar.setOnAction(ev -> {
                String articulo = comboArticulos.getValue();
                String nuevaCantidadStr = campoNuevaCantidad.getText();
                String comentario = campoComentario.getText();

                if (articulo == null || nuevaCantidadStr.isEmpty()) {
                    Alert alerta = new Alert(Alert.AlertType.WARNING);
                    alerta.setTitle("Advertencia");
                    alerta.setHeaderText(null);
                    alerta.setContentText("Debes seleccionar un artículo y una nueva cantidad.");
                    alerta.showAndWait();
                    return;
                }

                try {
                    int nuevaCantidad = Integer.parseInt(nuevaCantidadStr);
                    StockMovimientoDTO stockMovDto = new StockMovimientoDTO();
                    ArticuloDTO articuloDTO = controller.buscarArtPorNombre(articulo);
                    stockMovDto.setArticuloID(articuloDTO.getCodArticulo());
                    stockMovDto.setCantidad(nuevaCantidad);
                    stockMovDto.setComentario(comentario);
                    stockMovDto.setFechaHoraMovimiento(LocalDateTime.now());

                    controller.realizarAjusteInventario(stockMovDto);
                    cargarArticulos();
                    popup.close();

                } catch (NumberFormatException ex) {
                    Alert alerta = new Alert(Alert.AlertType.WARNING);
                    alerta.setTitle("Advertencia");
                    alerta.setHeaderText(null);
                    alerta.setContentText(ex.getMessage());
                    alerta.showAndWait();

                } catch (IllegalArgumentException ex) {
                    Alert alerta = new Alert(Alert.AlertType.ERROR);
                    alerta.setTitle("Error de Validación");
                    alerta.setHeaderText("Cantidad inválida");
                    alerta.setContentText(ex.getMessage());
                    alerta.showAndWait();
                }
            });

            btnCancelar.setOnAction(ev -> popup.close());

            VBox layout = new VBox(10,
                    new Label("Artículo:"),
                    comboArticulos,
                    new Label("Cantidad actual:"),
                    campoCantidadActual,
                    new Label("Nueva cantidad:"),
                    campoNuevaCantidad,
                    new Label("Comentario (opcional):"),
                    campoComentario,
                    new HBox(10, btnAceptar, btnCancelar)
            );
            layout.setPadding(new Insets(20));
            layout.setAlignment(Pos.CENTER);


            Scene scene = new Scene(layout, 500, 350);
            popup.setScene(scene);
            popup.showAndWait();
        });


        btnCalcularCGI = new Button("Calcular CGI");
        btnCalcularCGI.setOnAction(e -> {
            List<CGIDTO> articulosCGI = controller.calcularCGI();

            Stage popup = new Stage();
            popup.setTitle("Costo General de Inventario");
            popup.initModality(Modality.APPLICATION_MODAL);

            Label mensaje = new Label("No se calcula el CGI para artículos que no tengan una configuración de inventario");
            mensaje.setStyle("-fx-font-weight: bold; -fx-text-fill: #cc0000;");

            ListView<CGIDTO> listView = new ListView<>(FXCollections.observableArrayList(articulosCGI));
            listView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(CGIDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText("Nombre: " + item.getNombreArticulo()
                                + " | Costo Gral Inventario: $" + item.getCgi());
                    }
                }
            });

            VBox layout = new VBox(10, mensaje, listView);
            layout.setPadding(new Insets(10));

            Scene scene = new Scene(layout, 600, 400);
            popup.setScene(scene);
            popup.showAndWait();
        });

        HBox contenedorFiltros = new HBox(10, btnListarReponer, btnProductosFaltantes, btnAjusteInventario, btnCalcularCGI);
        contenedorFiltros.setPadding(new Insets(10));

        contenedorFiltros.getStylesheets().add(getClass().getResource("/styles/estilosMaestroArticulos.css").toExternalForm());
        for (Button btn : List.of(btnListarReponer, btnProductosFaltantes, btnAjusteInventario, btnCalcularCGI)) {
            btn.getStyleClass().add("button-acciones");
        }


        VBox contenidoCentro = new VBox(tablaArticulos, contenedorFiltros);
        contenidoCentro.setSpacing(10);
        contenidoCentro.setPadding(new Insets(10));

        setCenter(contenidoCentro); //se asigna como contenido central
    }

    //Define las columnas de la tabla de articulos
    private void construirTabla() {
        TableColumn<ArticuloDTO, Long> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getCodArticulo()).asObject());

        TableColumn<ArticuloDTO, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombreArticulo()));

        TableColumn<ArticuloDTO, Integer> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStockActual()).asObject());

        TableColumn<ArticuloDTO, BigDecimal> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrecioArticulo()));

        TableColumn<ArticuloDTO, String> colProveedorPredeterminado = new TableColumn<>("Proveedor Predeterminado");
        colProveedorPredeterminado.setCellValueFactory(data -> {
            String nombreProveedor = data.getValue().getProveedorPredeterminado();
            return new SimpleStringProperty(nombreProveedor != null ? nombreProveedor : "Sin proveedor");
        });



        TableColumn<ArticuloDTO, Void> colAccion = new TableColumn<>("Acciones");
        colAccion.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("Modificar");
            private final Button btnBorrar = new Button("Eliminar");
            private final Button btnProveedor = new Button("Seleccionar Proveedor");



            {   btnEditar.getStylesheets().add(getClass().getResource("/styles/estilosMaestroArticulos.css").toExternalForm());
                btnEditar.getStyleClass().add("button-modificar");
                btnBorrar.getStylesheets().add(getClass().getResource("/styles/estilosMaestroArticulos.css").toExternalForm());
                btnBorrar.getStyleClass().add("button-cancelar");
                btnProveedor.getStylesheets().add(getClass().getResource("/styles/estilosMaestroArticulos.css").toExternalForm());
                btnProveedor.getStyleClass().add("button-proveedorPredeterminado");

                //BOTON PARA LA MODIFICACION
                btnEditar.setOnAction(e -> {
                    ArticuloDTO artdto = getTableView().getItems().get(getIndex());
                    ArticuloDTO articuloTraido = controller.obtenerArticuloPorId(artdto.getCodArticulo());

                    Stage ventanaEdicion = new Stage();
                    ventanaEdicion.setTitle("Editar Artículo");
                    ventanaEdicion.initModality(Modality.APPLICATION_MODAL);
                    GridPane grid = new GridPane();
                    grid.setVgap(10);
                    grid.setHgap(10);
                    grid.setPadding(new Insets(20));


                    TextField txtNombre = new TextField(articuloTraido.getNombreArticulo());
                    txtNombre.setPrefColumnCount(20);
                    TextArea txtDescripcion = new TextArea(articuloTraido.getDescripcionArticulo());
                    txtDescripcion.setPrefRowCount(2);
                    txtDescripcion.setWrapText(true);
                    txtDescripcion.setPrefColumnCount(20);
                    TextField txtCostoAlmacenamiento = new TextField(String.valueOf(articuloTraido.getCostoAlmacenamiento()));
                    txtCostoAlmacenamiento.setPrefColumnCount(5);
                    TextField txtPrecioArticulo = new TextField(String.valueOf(articuloTraido.getPrecioArticulo()));
                    txtPrecioArticulo.setPrefColumnCount(5);
                    TextField txtNivelServicioArticulo = new TextField(String.valueOf(articuloTraido.getNivelServicioArticulo()));
                    txtNivelServicioArticulo.setPrefColumnCount(5);
                    TextField txtDiasEntreRevisiones = new TextField(String.valueOf(articuloTraido.getDiasEntreRevisiones()));
                    txtDiasEntreRevisiones.setPrefColumnCount(5);
                    TextField txtDemanda = new TextField(String.valueOf(articuloTraido.getDemandaArt()));
                    txtDemanda.setPrefColumnCount(5);
                    // Agregar al gridPane
                    grid.add(new Label("Nombre:"), 0, 0);
                    grid.add(txtNombre, 1, 0);
                    grid.add(new Label("Precio del artículo:"), 2, 0);
                    grid.add(txtPrecioArticulo, 3, 0);
                    grid.add(new Label("Descripción:"), 0, 1);
                    grid.add(txtDescripcion, 1, 1);
                    grid.add(new Label("Nivel de servicio:"), 2, 1);
                    grid.add(txtNivelServicioArticulo, 3, 1);
                    grid.add(new Label("Días entre revisiones:"), 2, 2);
                    grid.add(txtDiasEntreRevisiones, 3, 2);
                    grid.add(new Label("Costo de almacenamiento:"), 0, 3);
                    grid.add(txtCostoAlmacenamiento, 1, 3);
                    grid.add(new Label("Demanda:"), 2, 3);
                    grid.add(txtDemanda, 3, 3);
                    //Botones
                    Button btnGuardar = new Button("Guardar");
                    Button btnCancelar = new Button("Cancelar");
                    btnGuardar.getStylesheets().add(getClass().getResource("/styles/estilosMaestroArticulos.css").toExternalForm());
                    btnCancelar.getStylesheets().add(getClass().getResource("/styles/estilosMaestroArticulos.css").toExternalForm());
                    btnGuardar.getStyleClass().add("button-acciones");
                    btnCancelar.getStyleClass().add("button-cancelar");

                    HBox botones = new HBox(10, btnGuardar, btnCancelar);
                    botones.setAlignment(Pos.CENTER_RIGHT);
                    grid.add(botones, 3, 5);
                    Scene scene = new Scene(grid);
                    ventanaEdicion.setScene(scene);
                    ventanaEdicion.setResizable(false);
                    ventanaEdicion.show();

                    btnGuardar.setOnAction(event -> {
                        try {
                            ArticuloDTO dto = new ArticuloDTO();
                            dto.setCodArticulo(artdto.getCodArticulo());
                            dto.setNombreArticulo(txtNombre.getText());
                            dto.setDescripcionArticulo(txtDescripcion.getText());
                            dto.setStockActual(Integer.parseInt(String.valueOf(articuloTraido.getStockActual())));
                            dto.setCostoAlmacenamiento(new BigDecimal(txtCostoAlmacenamiento.getText()));
                            dto.setPrecioArticulo(new BigDecimal(txtPrecioArticulo.getText()));
                            dto.setNivelServicioArticulo(new BigDecimal(txtNivelServicioArticulo.getText()));
                            dto.setDiasEntreRevisiones(Integer.parseInt(txtDiasEntreRevisiones.getText()));
                            dto.setDemandaArt(Integer.parseInt(txtDemanda.getText()));

                            try {
                                controller.actualizarArticulo(dto);
                                cargarArticulos();
                                ventanaEdicion.close();
                            } catch (IllegalArgumentException | IllegalStateException ex) {
                                mostrarError("Error al modificar el artículo: " + ex.getMessage());
                            }

                        } catch (NumberFormatException ex) {
                            new Alert(Alert.AlertType.ERROR, "Campos numéricos inválidos.").showAndWait();
                        }
                    });

                    btnCancelar.setOnAction(event -> ventanaEdicion.close());
                });

                //---------------------------------------------------------------------------------------------------------

                //BOTON DE DAR DE BAJA
                btnBorrar.setOnAction(e -> {
                    ArticuloDTO articuloDTO = getTableView().getItems().get(getIndex());

                    // Crear un diálogo personalizado
                    Dialog<ButtonType> dialog = new Dialog<>();
                    dialog.setTitle("Confirmar baja");
                    dialog.setHeaderText("¿Seguro que querés dar de baja este artículo?");
                    dialog.setContentText("Esta acción marcará el artículo como dado de baja.");

                    ButtonType aceptarButtonType = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
                    ButtonType cancelarButtonType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
                    dialog.getDialogPane().getButtonTypes().addAll(aceptarButtonType, cancelarButtonType);

                    dialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles/estilosMaestroArticulos.css").toExternalForm());

                    // Mostrar el diálogo y esperar la respuesta del usuario
                    Optional<ButtonType> result = dialog.showAndWait();

                    if (result.isPresent() && result.get() == aceptarButtonType) {
                        ArticuloDTO dto = new ArticuloDTO();
                        dto.setCodArticulo(articuloDTO.getCodArticulo());
                        dto.setFechaHoraBajaArticulo(LocalDateTime.now()); // Se setea la fecha actual para la baja


                        try {
                            // Llamar al controlador pasando el DTO que contiene la fecha de baja
                            controller.darDeBajaArticulo(dto);
                            cargarArticulos();

                            Alert exito = new Alert(Alert.AlertType.INFORMATION);
                            exito.setTitle("Baja exitosa");
                            exito.setHeaderText(null);
                            exito.setContentText("El artículo fue dado de baja correctamente.");
                            exito.showAndWait();

                        } catch (IllegalStateException | IllegalArgumentException ex) {
                            Alert error = new Alert(Alert.AlertType.ERROR);
                            error.setTitle("Error al dar de baja");
                            error.setHeaderText("No se pudo dar de baja el artículo");
                            error.setContentText(ex.getMessage());
                            error.showAndWait();
                        }
                    }
                });

                //BOTON DE MOSTRAR TODOS LOS PROVEEDORES DEL ARTICULO SELECCIONADO
                btnProveedor.setOnAction(e ->{
                    ArticuloDTO articuloDTO = getTableView().getItems().get(getIndex());
                    //Obtener el código del artículo
                    Long codArticulo = articuloDTO.getCodArticulo();

                    //Obtener los proveedores desde el controller
                    List<ArticuloProveedorDTO> proveedoresDisponiblesDTO = controller.obtenerProveedoresDeEseArticulo(codArticulo);

                    Stage stage = new Stage();
                    stage.setTitle("Seleccionar proveedor para " + articuloDTO.getNombreArticulo());
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
                    btnSeleccionar.getStylesheets().add(getClass().getResource("/styles/estilosMaestroArticulos.css").toExternalForm());
                    btnSeleccionar.getStyleClass().add("button-acciones");
                    btnSeleccionar.setOnAction(ev -> {
                        ArticuloProveedorDTO Proveedorseleccionado = listView.getSelectionModel().getSelectedItem();
                        if (Proveedorseleccionado != null) {
                            //se captura el codigo del articuloProveedor seleccionado
                            Long codArticuloProveedor = Proveedorseleccionado.getId();

                            // Acá se actualiza el proveedor predeterminado
                            controller.asignarProveedorPredeterminado(codArticuloProveedor);
                            cargarArticulos();
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
        tablaArticulos.getColumns().addAll(colCodigo,  colNombre, colStock, colPrecio, colProveedorPredeterminado,colAccion);
        tablaArticulos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void cargarArticulos() {
        listaArticulos.clear();
        List<ArticuloDTO> articulos = controller.obtenerTodosArticulos();

        for (ArticuloDTO articulo : articulos) {
            ArticuloProveedorDTO articuloProveedor = controller.obtenerProveedorPredeterminado(articulo.getCodArticulo());
            if (articuloProveedor != null) {
                articulo.setProveedorPredeterminado(articuloProveedor.getNombre());
            } else {
                articulo.setProveedorPredeterminado("Sin proveedor");
            }
        }
        listaArticulos.addAll(articulos);
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

}
