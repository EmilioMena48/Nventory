package com.nventory.userInterfaces;

import com.nventory.DTO.ArticuloProveedorGuardadoDTO;
import com.nventory.DTO.ProveedorDTO;
import com.nventory.DTO.ProveedorEliminadoDTO;
import com.nventory.controller.ArticuloController;
import com.nventory.controller.ProveedorController;
import com.nventory.model.Articulo;
import com.nventory.model.ArticuloProveedor;
import com.nventory.model.Proveedor;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class ProveedorPanel extends BorderPane {

    private static final String CSS = "/styles/estilosProveedor.css";
    private static final String TITULO_PROVEEDORES = "Gestión de Proveedores";
    private static final String ERROR_CARGAR_PROVEEDORES = "No se pudo cargar la lista de proveedores: ";
    private static final String ERROR_GUARDAR_PROVEEDOR = "No se pudo guardar el proveedor: ";
    private static final String PROVEEDOR_GUARDADO = "Proveedor guardado correctamente.";
    private static final String CAMPOS_VACIOS = "Los campos obligatorios no pueden estar vacíos.";

    private final ProveedorController controller;
    private final ArticuloController articuloController;
    private VBox areaContenido;
    private ProveedorDTO proveedorDTO;
    private boolean modificar = false;

    public ProveedorPanel(ProveedorController controller, ArticuloController articuloController) {
        this.controller = controller;
        this.articuloController = articuloController;
        this.getStylesheets().add(Objects.requireNonNull(getClass().getResource(CSS)).toExternalForm());
        inicializarInterfaz();
        cargarTablaProveedoresActivos();
    }

    private void inicializarInterfaz() {
        configurarHeader();
        configurarMenuLateral();
        configurarAreaContenido();
    }

    private void configurarHeader() {
        Label titulo = new Label(TITULO_PROVEEDORES);
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        setTop(titulo);
        BorderPane.setMargin(titulo, new Insets(10));
    }

    private void configurarMenuLateral() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.getChildren().addAll(
                crearBoton("Listar Proveedores", this::cargarTablaProveedoresActivos),
                crearBoton("Alta Proveedor", this::mostrarFormularioAlta),
                crearBoton("Restaurar Proveedor", this::restaurarProveedor)
        );
        menu.getStyleClass().add("sombreadoMenu");
        setLeft(menu);
    }

    private void configurarAreaContenido() {
        areaContenido = new VBox();
        areaContenido.setPadding(new Insets(0, 0, 0, 10));
        areaContenido.setSpacing(10);
        setCenter(areaContenido);
    }

    private Button crearBoton(String texto, Runnable accion) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> accion.run());
        btn.getStyleClass().add("button-menu");
        return btn;
    }

    private void mostrarFormularioAlta() {
        areaContenido.getChildren().clear();

        TextField txtNombre = new TextField();
        TextArea txtDescripcion = new TextArea();

        txtNombre.getStyleClass().add("text-field");
        txtDescripcion.getStyleClass().add("text-area");

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("button-cancelar");
        btnCancelar.setOnAction(e -> cargarTablaProveedoresActivos());

        Button btnGuardar = new Button("Guardar Proveedor");
        btnGuardar.getStyleClass().add("button-guardar");

        btnGuardar.setOnAction(e -> {
            if (txtNombre.getText().isEmpty()) {
                mostrarAlerta(CAMPOS_VACIOS, 2, null);
            } else {
                guardarProveedor(txtNombre, txtDescripcion);
            }
        });

        if (modificar) {
            txtNombre.setText(proveedorDTO.getNombreProveedor());
            txtDescripcion.setText(proveedorDTO.getDescripcionProveedor());
        } else {
            txtNombre.clear();
            txtDescripcion.clear();
            proveedorDTO = null;
        }

        GridPane formulario = new GridPane();
        formulario.getStyleClass().add("formulario");
        formulario.setVgap(10);
        formulario.setHgap(10);

        formulario.add(new Label("Nombre: "), 0, 0);
        formulario.add(txtNombre, 1, 0);

        formulario.add(new Label("Descripción: "), 0, 1);
        formulario.add(txtDescripcion, 1, 1);

        formulario.add(btnGuardar, 1, 2);
        formulario.add(btnCancelar, 0, 2);

        modificar = false;
        animarFormulario(formulario);
        areaContenido.getChildren().add(formulario);
    }

    private void guardarProveedor(TextField txtNombre, TextArea txtDescripcion) {
        if (txtNombre.getText().isEmpty()) {
            mostrarAlerta(CAMPOS_VACIOS, 2, null);
        } else {
            try {
                if (proveedorDTO == null) {
                    proveedorDTO = new ProveedorDTO();
                    proveedorDTO.setCodProveedor(0L);
                }
                proveedorDTO.setNombreProveedor(txtNombre.getText());
                proveedorDTO.setDescripcionProveedor(txtDescripcion.getText());

                if (proveedorDTO.getCodProveedor() == 0L) {
                    mostrarSeleccionArticulo();
                } else {
                    controller.GuardarProveedor(proveedorDTO);
                    mostrarAlerta(PROVEEDOR_GUARDADO, 4, () -> {
                        txtNombre.clear();
                        txtDescripcion.clear();
                        proveedorDTO = null;
                        cargarTablaProveedoresActivos();
                    });
                }
            } catch (Exception ex) {
                mostrarAlerta(ERROR_GUARDAR_PROVEEDOR + ex.getMessage(), 2, null);
            }
        }
    }

    private void cargarTablaProveedoresActivos() {
        areaContenido.getChildren().clear();
        proveedorDTO = null;
        TableView<ProveedorDTO> tabla = new TableView<>();
        tabla.getStyleClass().add("tablaProveedor");

        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.setPlaceholder(new Label("No hay proveedores disponibles."));
        tabla.getColumns().addAll(crearColumnasBasicas());
        tabla.setFixedCellSize(25);

        try {
            List<ProveedorDTO> proveedores = controller.ListarProveedores();
            tabla.getItems().setAll(proveedores);
            tabla.prefHeightProperty().bind(tabla.fixedCellSizeProperty().multiply(proveedores.size() + 1));
            tabla.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

            Button btnModificar = new Button("Modificar");
            btnModificar.getStyleClass().add("button-seleccionar");
            Button btnEliminar = new Button("Eliminar");
            btnEliminar.getStyleClass().add("button-cancelar");
            Button btnAsociarArticulo = new Button("Asociar Artículo");
            btnAsociarArticulo.getStyleClass().add("button-seleccionar");
            Button btnListarArticulos = new Button("Listar Articulos");
            btnListarArticulos.getStyleClass().add("button-seleccionar");

            btnModificar.setDisable(true);
            btnEliminar.setDisable(true);
            btnAsociarArticulo.setDisable(true);
            btnListarArticulos.setDisable(true);

            tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                boolean seleccionado = newVal != null;
                btnModificar.setDisable(!seleccionado);
                btnEliminar.setDisable(!seleccionado);
                btnAsociarArticulo.setDisable(!seleccionado);
                btnListarArticulos.setDisable(!seleccionado);
            });

            btnModificar.setOnAction(e -> {
                proveedorDTO = tabla.getSelectionModel().getSelectedItem();
                if (proveedorDTO != null) {
                    modificar = true;
                    mostrarFormularioAlta();
                }
            });

            btnEliminar.setOnAction(e -> {
                proveedorDTO = tabla.getSelectionModel().getSelectedItem();
                if (proveedorDTO != null) {
                    mostrarAlerta("¿Está seguro de eliminar este proveedor?", 3, () -> {
                        try {
                            controller.EliminarProveedor(proveedorDTO.getCodProveedor());
                            cargarTablaProveedoresActivos();
                        } catch (Exception ex) {
                            mostrarAlerta("Error al Eliminar: " + ex.getMessage(), 2, null);
                        }
                        proveedorDTO = null;
                    });
                }
            });

            btnAsociarArticulo.setOnAction(e -> {
                proveedorDTO = tabla.getSelectionModel().getSelectedItem();
                if (proveedorDTO != null) {
                    modificar = false;
                    mostrarSeleccionArticulo();
                }
            });

            btnListarArticulos.setOnAction(e -> {
                proveedorDTO = tabla.getSelectionModel().getSelectedItem();
                if (proveedorDTO != null) {
                    modificar = false;
                    cargarTablaArticulosDelProveedor();
                }
            });

            HBox contenedorBotones = new HBox(10, btnModificar, btnEliminar, btnAsociarArticulo, btnListarArticulos);
            contenedorBotones.setAlignment(Pos.CENTER);
            contenedorBotones.setPadding(new Insets(10));
            VBox contenedorTotal = new VBox(10, tabla, contenedorBotones);
            contenedorTotal.getStyleClass().add("sombreadoMenu");
            contenedorTotal.setPadding(new Insets(10));

            areaContenido.getChildren().add(contenedorTotal);

        } catch (Exception e) {
            mostrarAlerta(ERROR_CARGAR_PROVEEDORES + e.getMessage(), 2, null);
        }
    }

    private void cargarTablaProveedoresEliminados() {
        areaContenido.getChildren().clear();
        TableView<ProveedorEliminadoDTO> tabla = new TableView<>();
        tabla.getStyleClass().add("tablaProveedor");
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.setPlaceholder(new Label("No hay proveedores eliminados."));
        tabla.getColumns().addAll(crearColumnasBasicas());
        tabla.getColumns().add(crearColumna("Fecha de Baja", "fechaHoraBajaProveedor"));
        tabla.setFixedCellSize(25);

        try {
            List<ProveedorEliminadoDTO> proveedores = controller.ListarProveedoresEliminados();
            tabla.getItems().setAll(proveedores);
            tabla.prefHeightProperty().bind(tabla.fixedCellSizeProperty().multiply(proveedores.size() + 1));
            tabla.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

            Button btnRestaurar = new Button("Restaurar");
            btnRestaurar.getStyleClass().add("button-seleccionar");
            btnRestaurar.setDisable(true);

            tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                btnRestaurar.setDisable(newVal == null);
            });

            btnRestaurar.setOnAction(e -> {
                ProveedorEliminadoDTO proveedor = tabla.getSelectionModel().getSelectedItem();
                if (proveedor != null) {
                    proveedorDTO = new ProveedorDTO();
                    proveedorDTO.setCodProveedor(proveedor.getCodProveedor());
                    proveedorDTO.setNombreProveedor(proveedor.getNombreProveedor());
                    proveedorDTO.setDescripcionProveedor(proveedor.getDescripcionProveedor());
                    modificar = true;
                    mostrarFormularioAlta();
                }
            });

            HBox contenedorBotones = new HBox(10, btnRestaurar);
            contenedorBotones.setAlignment(Pos.CENTER);
            contenedorBotones.setPadding(new Insets(10));
            VBox contenedorTotal = new VBox(10, tabla, contenedorBotones);
            contenedorTotal.getStyleClass().add("sombreadoMenu");
            contenedorTotal.setPadding(new Insets(10));

            areaContenido.getChildren().add(contenedorTotal);

        } catch (Exception e) {
            mostrarAlerta(ERROR_CARGAR_PROVEEDORES + e.getMessage(), 2, null);
        }
    }

    private <S> List<TableColumn<S, ?>> crearColumnasBasicas() {
        return List.of(
                crearColumna("Código", "codProveedor"),
                crearColumna("Nombre", "nombreProveedor"),
                crearColumna("Descripción", "descripcionProveedor")
        );
    }

    private <S, T> TableColumn<S, T> crearColumna(String titulo, String propiedad) {
        TableColumn<S, T> columna = new TableColumn<>(titulo);
        columna.setCellValueFactory(new PropertyValueFactory<>(propiedad));
        return columna;
    }

    private void cargarTablaArticulosDelProveedor() {
        areaContenido.getChildren().clear();
        TableView<Articulo> tabla = new TableView<>();
        tabla.getStyleClass().add("tablaProveedor");

        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.setPlaceholder(new Label("No hay artículos asociados a este proveedor."));
        tabla.getColumns().add(crearColumna("Código", "codArticulo"));
        tabla.getColumns().add(crearColumna("Nombre", "nombreArticulo"));
        tabla.getColumns().add(crearColumna("Descripción", "descripcionArticulo"));
        tabla.getColumns().add(crearColumna("Stock Actual", "stockActual"));

        TableColumn<Articulo, Boolean> columnaAsociado = new TableColumn<>("Predeterminado");
        tabla.setFixedCellSize(25);

        try {
            List<Articulo> articulos = controller.ListarArticulos(proveedorDTO.getCodProveedor());
            tabla.getItems().setAll(articulos);
            tabla.prefHeightProperty().bind(tabla.fixedCellSizeProperty().multiply(articulos.size() + 1));
            tabla.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

            columnaAsociado.setCellValueFactory(param -> {
                Articulo articulo = param.getValue();
                ArticuloProveedor articuloProveedorPredeterminado = articulo.getArticuloProveedor();
                if (articuloProveedorPredeterminado == null) {
                    return new ReadOnlyBooleanWrapper(false);
                } else {
                    boolean asociado = articuloProveedorPredeterminado.getProveedor().getCodProveedor().equals(proveedorDTO.getCodProveedor());
                    return new ReadOnlyBooleanWrapper(asociado);
                }
            });
            columnaAsociado.setCellFactory(CheckBoxTableCell.forTableColumn(columnaAsociado));
            columnaAsociado.setEditable(false);
            tabla.getColumns().add(columnaAsociado);

            Button btnCancelar = new Button("Cancelar");
            btnCancelar.getStyleClass().add("button-cancelar");
            Button btnAgregar = new Button("Agregar");
            btnAgregar.getStyleClass().add("button-seleccionar");
            Button btnModificar = new Button("Modificar");
            btnModificar.getStyleClass().add("button-seleccionar");
            Button btnEliminar = new Button("Eliminar");
            btnEliminar.getStyleClass().add("button-cancelar");

            btnModificar.setDisable(true);
            btnEliminar.setDisable(true);

            tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                boolean seleccionado = newVal != null;
                btnModificar.setDisable(!seleccionado);
                btnEliminar.setDisable(!seleccionado);
            });

            btnCancelar.setOnAction(e -> {
                proveedorDTO = null;
                cargarTablaProveedoresActivos();
            });

            btnAgregar.setOnAction(e -> {
                if (proveedorDTO != null) {
                    modificar = false;
                    mostrarSeleccionArticulo();
                }
            });

            btnModificar.setOnAction(e -> {
                Articulo articuloSeleccionado = tabla.getSelectionModel().getSelectedItem();
                if (articuloSeleccionado != null) {
                    mostrarFormularioAsociarArticulo(articuloSeleccionado);
                }
            });

            btnEliminar.setOnAction(e -> {
                Articulo articuloSeleccionado = tabla.getSelectionModel().getSelectedItem();
                if (articuloSeleccionado != null) {
                    mostrarAlerta("¿Está seguro de eliminar este artículo del proveedor?", 3, () -> {
                        try {
                            controller.EliminarArticuloProveedor(articuloSeleccionado.getCodArticulo(), proveedorDTO.getCodProveedor());
                            cargarTablaProveedoresActivos();
                        } catch (Exception ex) {
                            mostrarAlerta("Error al eliminar artículo: " + ex.getMessage(), 2, null);
                        }
                    });
                }
            });
            tabla.setRowFactory(null);

            Label proveedorLabel = new Label("Proveedor: " + proveedorDTO.getNombreProveedor());
            proveedorLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            proveedorLabel.setPadding(new Insets(5, 0, 5, 0));

            Label labelArticulos = new Label("Artículos Asociados:");
            labelArticulos.setStyle("-fx-font-size: 12px;");

            HBox contenedorBotones = new HBox(10, btnCancelar, btnModificar, btnEliminar, btnAgregar);
            contenedorBotones.setAlignment(Pos.CENTER);
            contenedorBotones.setPadding(new Insets(10));

            VBox contenedor = new VBox(10);
            contenedor.getChildren().addAll(proveedorLabel, labelArticulos, tabla);

            VBox contenedorTotal = new VBox(10, contenedor, contenedorBotones);
            contenedorTotal.getStyleClass().add("sombreadoMenu");
            contenedorTotal.setPadding(new Insets(10));

            areaContenido.getChildren().add(contenedorTotal);

            FadeTransition fade = new FadeTransition(Duration.millis(600), contenedorTotal);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        } catch (Exception e) {
            mostrarAlerta("Error cargando artículos del proveedor: " + e.getMessage(), 2, null);
        }
    }

    private void restaurarProveedor() {
        areaContenido.getChildren().clear();
        cargarTablaProveedoresEliminados();
    }

    private void mostrarAlerta(String mensaje, int tipo, Runnable accion) {
        PopupMensaje.mostrarPopup(mensaje, tipo, accion);
    }

    private void animarFormulario(GridPane formulario) {
        FadeTransition fade = new FadeTransition(Duration.millis(600), formulario);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void mostrarSeleccionArticulo() {
        areaContenido.getChildren().clear();
        TableView<Articulo> tabla = new TableView<>();
        tabla.getStyleClass().add("tablaProveedor");
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.setPlaceholder(new Label("No hay articulos"));
        tabla.getColumns().add(crearColumna("Código", "codArticulo"));
        tabla.getColumns().add(crearColumna("Nombre", "nombreArticulo"));
        tabla.getColumns().add(crearColumna("Descripción", "descripcionArticulo"));

        tabla.setFixedCellSize(25);
        try {
            List<Articulo> articulos = articuloController.listarArticulos();
            if (proveedorDTO.getCodProveedor() != 0L) {
                articulos.removeIf(articulo -> articulo.getArticuloProveedor() != null &&
                        ((controller.BuscarArticuloProveedor(articulo.getCodArticulo(), proveedorDTO.getCodProveedor()) != null) &&
                                (!(controller.EstaEliminadoArticuloProveedor(articulo.getCodArticulo(), proveedorDTO.getCodProveedor())))));
            }
            tabla.getItems().setAll(articulos);
            tabla.prefHeightProperty().bind(
                    tabla.fixedCellSizeProperty().multiply(articulos.size() + 1)
            );

            tabla.setRowFactory(tv -> {
                TableRow<Articulo> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty()) {
                        Articulo articulo = row.getItem();
                        mostrarFormularioAsociarArticulo(articulo);
                    }
                });
                return row;
            });
            tabla.getSelectionModel().clearSelection();

            VBox contenedorSeleccion = new VBox(10);
            contenedorSeleccion.getStyleClass().add("seleccion-articulo");
            Label proveedorLabel = new Label("Proveedor: " + proveedorDTO.getNombreProveedor());
            Label labelArticulos = new Label("Seleccione un Articulo:");
            labelArticulos.setStyle("-fx-font-size: 12px;");
            proveedorLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            proveedorLabel.setPadding(new Insets(5, 0, 5, 0));
            contenedorSeleccion.getChildren().addAll(proveedorLabel, labelArticulos, tabla);
            areaContenido.getChildren().add(contenedorSeleccion);
            FadeTransition fade = new FadeTransition(Duration.millis(600), contenedorSeleccion);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        } catch (Exception e) {
            mostrarAlerta("Error cargando artículos: " + e.getMessage(), 2, null);
        }
    }

    private void mostrarFormularioAsociarArticulo(Articulo articuloSeleccionado) {
        areaContenido.getChildren().clear();
        boolean asignarModelo;
        boolean tiempoFijo = false;

        TextField txtDemoraEntrega = new TextField();
        TextField txtPrecioUnitario = new TextField();
        TextField txtCostoPedido = new TextField();
        ComboBox<String> diasEntregaComboBox = new ComboBox<>();
        diasEntregaComboBox.getItems().addAll("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");
        diasEntregaComboBox.getSelectionModel().select("Lunes");

        SelectorSwitch toggleSwitch = new SelectorSwitch(true);

        txtDemoraEntrega.getStyleClass().add("text-field");
        txtPrecioUnitario.getStyleClass().add("text-field");
        txtCostoPedido.getStyleClass().add("text-field");
        diasEntregaComboBox.getStyleClass().add("text-field");

        HBox toggleContainer = new HBox(toggleSwitch);
        toggleContainer.setMinHeight(15);
        toggleContainer.setAlignment(Pos.CENTER);

        if (proveedorDTO.getCodProveedor() != 0L) {
            ArticuloProveedorGuardadoDTO articuloProveedor = controller.BuscarArticuloProveedor(articuloSeleccionado.getCodArticulo(), proveedorDTO.getCodProveedor());
            if (articuloProveedor != null) {
                asignarModelo = false;
                txtDemoraEntrega.setText(String.valueOf(articuloProveedor.getDemoraEntregaDias()));
                txtPrecioUnitario.setText(articuloProveedor.getPrecioUnitario().toString());
                txtCostoPedido.setText(articuloProveedor.getCostoPedido().toString());
                LocalDate fechaProxRevision = articuloProveedor.getFechaProxRevisionAP();
                if (fechaProxRevision != null) {
                    String fechaProxRevisionString = fechaProxRevision.getDayOfWeek().name();
                    switch (fechaProxRevisionString) {
                        case "MONDAY" -> diasEntregaComboBox.getSelectionModel().select("Lunes");
                        case "TUESDAY" -> diasEntregaComboBox.getSelectionModel().select("Martes");
                        case "WEDNESDAY" -> diasEntregaComboBox.getSelectionModel().select("Miércoles");
                        case "THURSDAY" -> diasEntregaComboBox.getSelectionModel().select("Jueves");
                        case "FRIDAY" -> diasEntregaComboBox.getSelectionModel().select("Viernes");
                        case "SATURDAY" -> diasEntregaComboBox.getSelectionModel().select("Sábado");
                        case "SUNDAY" -> diasEntregaComboBox.getSelectionModel().select("Domingo");
                    }
                    tiempoFijo = true;
                }
            } else {
                tiempoFijo = true;
                asignarModelo = true;

            }
        } else {
            tiempoFijo = true;
            asignarModelo = true;
        }

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("button-cancelar");
        btnCancelar.setOnAction(e -> {
            proveedorDTO = null;
            cargarTablaProveedoresActivos();
        });

        Button btnGuardar = new Button("Guardar Asociación");
        btnGuardar.getStyleClass().add("button-guardar");

        btnGuardar.setOnAction(e -> {
            try {
                int demora = 0;
                BigDecimal precio = BigDecimal.ZERO;
                BigDecimal costoPedido = BigDecimal.ZERO;
                String diaSeleccionado = "";
                try {
                    if (!txtDemoraEntrega.getText().isBlank()) {
                        demora = Integer.parseInt(txtDemoraEntrega.getText());
                    }
                    if (!txtPrecioUnitario.getText().isBlank()) {
                        precio = new BigDecimal(txtPrecioUnitario.getText());
                    }
                    if (!txtCostoPedido.getText().isBlank()) {
                        costoPedido = new BigDecimal(txtCostoPedido.getText());
                    }
                    diaSeleccionado = diasEntregaComboBox.getSelectionModel().getSelectedItem();

                } catch (NumberFormatException ex) {
                    mostrarAlerta("Por favor ingrese solo números válidos en los campos numéricos.", 2, null);
                    return;
                }

                ArticuloProveedorGuardadoDTO ap = new ArticuloProveedorGuardadoDTO();
                ap.setDemoraEntregaDias(demora);
                ap.setPrecioUnitario(precio);
                ap.setCostoPedido(costoPedido);

                if (asignarModelo && !toggleSwitch.isLoteFijo()) {
                    switch (diaSeleccionado) {
                        case "Lunes" -> diaSeleccionado = "MONDAY";
                        case "Martes" -> diaSeleccionado = "TUESDAY";
                        case "Miércoles" -> diaSeleccionado = "WEDNESDAY";
                        case "Jueves" -> diaSeleccionado = "THURSDAY";
                        case "Viernes" -> diaSeleccionado = "FRIDAY";
                        case "Sábado" -> diaSeleccionado = "SATURDAY";
                        case "Domingo" -> diaSeleccionado = "SUNDAY";
                        default -> {
                            mostrarAlerta("Día de entrega no válido.", 2, null);
                            return;
                        }
                    }
                    DayOfWeek diaEntrega = DayOfWeek.valueOf(diaSeleccionado);
                    LocalDate fechaProxRevision = LocalDate.now().with(diaEntrega);
                    if (fechaProxRevision.isBefore(LocalDate.now())) {
                        fechaProxRevision = fechaProxRevision.plusWeeks(1);
                    }
                    ap.setFechaProxRevisionAP(fechaProxRevision);
                } else {
                    ap.setFechaProxRevisionAP(null);
                }

                if (proveedorDTO.getCodProveedor() == 0L) {
                    Proveedor proveedor = controller.GuardarYRetornar(proveedorDTO);
                    controller.AsociarArticuloProveedor(articuloSeleccionado, proveedor, ap, toggleSwitch.isLoteFijo());
                } else {
                    Proveedor proveedor = controller.BuscarProveedorPorId(proveedorDTO.getCodProveedor());
                    if (asignarModelo) {
                        controller.AsociarArticuloProveedor(articuloSeleccionado, proveedor, ap, toggleSwitch.isLoteFijo());
                    } else {
                        controller.AsociarArticuloProveedor(articuloSeleccionado, proveedor, ap);
                    }
                }
                mostrarAlerta("Artículo asociado correctamente", 4, () -> {
                    txtDemoraEntrega.clear();
                    txtPrecioUnitario.clear();
                    txtCostoPedido.clear();
                    cargarTablaProveedoresActivos();
                });
            } catch (Exception ex) {
                mostrarAlerta("Error guardando asociación: " + ex.getMessage(), 2, null);
            }
        });

        GridPane formulario = new GridPane();
        formulario.getStyleClass().add("formulario");
        formulario.setVgap(10);
        formulario.setHgap(10);

        HBox articuloProveedorBox = new HBox(
                new Label("Proveedor: "),
                new Label(proveedorDTO.getNombreProveedor()),
                new Label("Artículo: "),
                new Label(articuloSeleccionado.getNombreArticulo()));
        articuloProveedorBox.setAlignment(Pos.CENTER);
        articuloProveedorBox.setSpacing(10);

        formulario.add(articuloProveedorBox, 0, 0, 2, 1);
        formulario.add(new Label("Demora entrega (días):"), 0, 2);
        formulario.add(txtDemoraEntrega, 1, 2);

        formulario.add(new Label("Precio unitario:"), 0, 3);
        formulario.add(txtPrecioUnitario, 1, 3);

        formulario.add(new Label("Costo pedido:"), 0, 4);
        formulario.add(txtCostoPedido, 1, 4);

        if (tiempoFijo) {
            formulario.add(new Label("Día de entrega:"), 0, 5);
            formulario.add(diasEntregaComboBox, 1, 5);
        } else {
            diasEntregaComboBox.setVisible(false);
        }

        if(asignarModelo) {
            formulario.add(toggleContainer, 0, 6, 2, 1);
        }

        formulario.add(btnGuardar, 1, 7);
        formulario.add(btnCancelar, 0, 7);

        animarFormulario(formulario);
        areaContenido.getChildren().add(formulario);
    }
}

class PopupMensaje {
    private static final String CSS = "/styles/estilosProveedor.css";
    public static void mostrarPopup(String mensaje, int tipo, Runnable accion) {
        Stage popup = new Stage();

        popup.initStyle(StageStyle.UNDECORATED);
        popup.initModality(Modality.APPLICATION_MODAL);

        Label lblMensaje = new Label(mensaje);
        lblMensaje.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        lblMensaje.setMaxWidth(320);
        lblMensaje.setWrapText(true);
        lblMensaje.setTextOverrun(OverrunStyle.ELLIPSIS);

        VBox vbox = new VBox(15);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(15));
        vbox.getChildren().add(lblMensaje);

        StackPane root = new StackPane(vbox);
        root.setPadding(new Insets(15));
        root.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: #bdc3c7;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 5px;"
        );
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 350, 150);
        scene.setFill(null);
        scene.getStylesheets().add(Objects.requireNonNull(PopupMensaje.class.getResource(CSS)).toExternalForm());
        popup.setScene(scene);
        popup.centerOnScreen();

        switch (tipo) {
            case 1 -> {
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(e -> popup.close());
                popup.show();
                pause.play();
            }
            case 2 -> {
                Button btnAceptar = new Button("Aceptar");
                btnAceptar.setOnAction(e -> popup.close());
                btnAceptar.getStyleClass().add("button-seleccionar");
                HBox hbox = new HBox(10, btnAceptar);
                hbox.setAlignment(Pos.BOTTOM_CENTER);
                root.getChildren().add(hbox);
                popup.show();
            }
            case 3 -> {
                Button btnAceptar = new Button("Aceptar");
                Button btnCancelar = new Button("Cancelar");

                btnAceptar.getStyleClass().add("button-seleccionar");
                btnCancelar.getStyleClass().add("button-cancelar");

                btnAceptar.setOnAction(e -> {
                    popup.close();
                    if (accion != null) accion.run();
                });
                btnCancelar.setOnAction(e -> popup.close());

                HBox hbox = new HBox(10, btnAceptar, btnCancelar);
                hbox.setAlignment(Pos.BOTTOM_CENTER);
                root.getChildren().add(hbox);

                popup.show();
            }
            case 4 -> {
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(e -> {
                    popup.close();
                    if (accion != null) accion.run();
                });
                popup.show();
                pause.play();
            }
        }
    }
}

class SelectorSwitch extends StackPane {
    private static final String CSS = "/styles/estilosProveedor.css";
    private final StackPane sliderPane;
    private final double width = 200;
    private final double height = 40;
    @Getter
    private boolean isLoteFijo;

    public SelectorSwitch(boolean initialLoteFijo) {
        this.isLoteFijo = initialLoteFijo;
        this.getStylesheets().add(Objects.requireNonNull(SelectorSwitch.class.getResource(CSS)).toExternalForm());

        Label loteLabel = new Label("Lote Fijo");
        Label tiempoLabel = new Label("Tiempo Fijo");

        loteLabel.setPrefSize(width / 2, height);
        tiempoLabel.setPrefSize(width / 2, height);
        loteLabel.setAlignment(Pos.CENTER);
        tiempoLabel.setAlignment(Pos.CENTER);

        HBox switchBox = new HBox(loteLabel, tiempoLabel);
        switchBox.setPrefSize(width, height);
        switchBox.setMaxWidth(width);
        switchBox.setMinWidth(width);
        switchBox.getStyleClass().add("switch-container");

        sliderPane = new StackPane();
        sliderPane.setPrefSize(width / 2, height);
        sliderPane.setMaxWidth(width / 2);
        sliderPane.setMinWidth(width / 2);
        sliderPane.getStyleClass().add("switch-slider");
        sliderPane.setTranslateX(initialLoteFijo ? 0 : width / 2);

        StackPane container = new StackPane(switchBox, sliderPane);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPrefSize(width, height);

        container.setOnMouseClicked(e -> toggle());

        this.getChildren().add(container);
    }

    private void toggle() {
        double targetX = isLoteFijo ? width / 2 : 0;
        TranslateTransition transition = new TranslateTransition(Duration.millis(200), sliderPane);
        transition.setToX(targetX);
        transition.play();
        isLoteFijo = !isLoteFijo;
    }
}