package com.nventory.userInterfaces;

import com.nventory.DTO.ProveedorDTO;
import com.nventory.DTO.ProveedorEliminadoDTO;
import com.nventory.controller.ProveedorController;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

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
    private VBox areaContenido;
    private TableView<ProveedorDTO> tablaProveedores = new TableView<>();
    private TableView<ProveedorEliminadoDTO> tablaProveedoresEliminados = new TableView<>();
    private ProveedorDTO proveedorDTO;
    private boolean modificar = false;

    public ProveedorPanel(ProveedorController controller) {
        this.controller = controller;
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
                crearBoton("Listar Proveedores", () -> cargarTablaProveedoresActivos()),
                crearBoton("Alta Proveedor", this::mostrarFormularioAlta),
                crearBoton("Listar Artículos por Proveedor", this::listarArticulosPorProveedor),
                crearBoton("Asociar Artículo a Proveedor", this::asociarArticuloAProveedor),
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

        Button btnGuardar = crearBotonGuardar(txtNombre, txtDescripcion);
        Button btnCancelar = crearBotonCancelar();

        GridPane formulario = crearFormulario(txtNombre, txtDescripcion, btnGuardar, btnCancelar);

        if (modificar) {
            txtNombre.setText(proveedorDTO.getNombreProveedor());
            txtDescripcion.setText(proveedorDTO.getDescripcionProveedor());
        } else {
            txtNombre.clear();
            txtDescripcion.clear();
            proveedorDTO = null;
        }
        modificar = false;

        animarFormulario(formulario);
        areaContenido.getChildren().addAll(formulario);
    }

    private Button crearBotonGuardar(TextField txtNombre, TextArea txtDescripcion) {
        Button btnGuardar = new Button("Guardar");
        btnGuardar.getStyleClass().add("button-guardar");
        btnGuardar.setOnAction(e -> guardarProveedor(txtNombre, txtDescripcion));
        return btnGuardar;
    }

    private Button crearBotonCancelar() {
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("button-cancelar");
        btnCancelar.setOnAction(e -> cargarTablaProveedoresActivos());
        return btnCancelar;
    }

    private GridPane crearFormulario(TextField txtNombre, TextArea txtDescripcion, Button btnGuardar, Button btnCancelar) {
        GridPane formulario = new GridPane();
        formulario.getStyleClass().add("formulario");

        formulario.setVgap(10);
        formulario.setHgap(10);

        formulario.add(new Label("Nombre:"), 0, 0);
        formulario.add(txtNombre, 0, 1);
        formulario.add(new Label("Descripción:"), 0, 2);
        formulario.add(txtDescripcion, 0, 3);
        formulario.add(btnGuardar, 0, 4);
        formulario.add(btnCancelar, 1, 4);

        return formulario;
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

                controller.GuardarProveedor(proveedorDTO);
                mostrarAlerta(PROVEEDOR_GUARDADO, 4, () -> {
                    txtNombre.clear();
                    txtDescripcion.clear();
                    proveedorDTO = null;
                    cargarTablaProveedoresActivos();
                });
            } catch (Exception ex) {
                mostrarAlerta(ERROR_GUARDAR_PROVEEDOR + ex.getMessage(), 2, null);
            }
        }
    }

    private void cargarTablaProveedoresActivos() {
        areaContenido.getChildren().clear();
        tablaProveedores.getColumns().clear();
        TableView<ProveedorDTO> tabla = tablaProveedores;
        if (!tabla.getStyleClass().contains("tablaProveedor")) {
            tabla.getStyleClass().add("tablaProveedor");
        }
        tabla.setPlaceholder(new Label("No hay proveedores disponibles."));
        tabla.getColumns().addAll(crearColumnasBasicas());
        tabla.getColumns().addAll(
                crearColumnaAcciones()
        );

        tabla.setFixedCellSize(25);

        try {
            List<ProveedorDTO> proveedores = controller.ListarProveedores();
            tabla.getItems().setAll(proveedores);
            tabla.prefHeightProperty().bind(
                    tabla.fixedCellSizeProperty().multiply(proveedores.size() + 1)
            );
            areaContenido.getChildren().add(tabla);
        } catch (Exception e) {
            mostrarAlerta(ERROR_CARGAR_PROVEEDORES + e.getMessage(), 2, null);
        }
    }

    private void cargarTablaProveedoresEliminados() {
        areaContenido.getChildren().clear();
        tablaProveedoresEliminados.getColumns().clear();
        TableView<ProveedorEliminadoDTO> tabla = tablaProveedoresEliminados;
        if (!tabla.getStyleClass().contains("tablaProveedor")) {
            tabla.getStyleClass().add("tablaProveedor");
        }
        tabla.setPlaceholder(new Label("No hay proveedores eliminados."));

        tabla.getColumns().addAll(crearColumnasBasicas());
        tabla.getColumns().addAll(
                crearColumna("Fecha de Baja", "fechaHoraBajaProveedor"),
                crearColumnaAccionesRestaurar()
        );

        tabla.setFixedCellSize(25);

        try {
            List<ProveedorEliminadoDTO> proveedores = controller.ListarProveedoresEliminados();
            tabla.getItems().setAll(proveedores);
            tabla.prefHeightProperty().bind(
                    tabla.fixedCellSizeProperty().multiply(proveedores.size() + 1)
            );
            areaContenido.getChildren().add(tabla);
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

    private TableColumn<ProveedorDTO, Void> crearColumnaAcciones() {
        TableColumn<ProveedorDTO, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnModificar = new Button("Modificar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox container = new HBox(5, btnModificar, btnEliminar);

            {
                btnModificar.setOnAction(e -> {
                    proveedorDTO = getTableView().getItems().get(getIndex());
                    modificar = true;
                    mostrarFormularioAlta();
                });

                btnEliminar.setOnAction(e -> {
                    proveedorDTO = getTableView().getItems().get(getIndex());
                    mostrarAlerta("¿Está seguro de eliminar este proveedor?", 3, () -> {
                        try {
                            controller.EliminarProveedor(proveedorDTO.getCodProveedor());
                            cargarTablaProveedoresActivos();
                        } catch (Exception ex) {
                            mostrarAlerta(ERROR_GUARDAR_PROVEEDOR + ex.getMessage());
                        }
                        proveedorDTO = null;
                    });

                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
        return colAcciones;
    }

    private TableColumn<ProveedorEliminadoDTO, Void> crearColumnaAccionesRestaurar() {
        TableColumn<ProveedorEliminadoDTO, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnRestaurar = new Button("Restaurar");
            private final HBox container = new HBox(5, btnRestaurar);

            {
                btnRestaurar.setOnAction(e -> {
                    ProveedorEliminadoDTO proveedor = getTableView().getItems().get(getIndex());
                    proveedorDTO = new ProveedorDTO();
                    proveedorDTO.setCodProveedor(proveedor.getCodProveedor());
                    proveedorDTO.setNombreProveedor(proveedor.getNombreProveedor());
                    proveedorDTO.setDescripcionProveedor(proveedor.getDescripcionProveedor());
                    modificar = true;
                    mostrarFormularioAlta();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
        return colAcciones;
    }

    private void listarArticulosPorProveedor() {
        areaContenido.getChildren().clear();
        // Implementar lógica
    }

    private void asociarArticuloAProveedor() {
        areaContenido.getChildren().clear();
        // Implementar lógica
    }

    private void restaurarProveedor() {
        areaContenido.getChildren().clear();
        cargarTablaProveedoresEliminados();
    }

    private void mostrarAlerta(String mensaje) {
        PopupMensaje.mostrarPopup(mensaje, 1, null);
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
}

class PopupMensaje {

    public static void mostrarPopup(String mensaje, int tipo, Runnable accion) {
        Stage popup = new Stage();
        popup.initStyle(StageStyle.UNDECORATED);
        popup.initModality(Modality.APPLICATION_MODAL);

        Label lblMensaje = new Label(mensaje);
        lblMensaje.setStyle("-fx-background-color: #6dbef1; -fx-text-fill: white; -fx-padding: 5px; -fx-font-size: 14px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        StackPane root = new StackPane(lblMensaje);
        root.setStyle("-fx-background-color: transparent; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-border-radius: 5px;");
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 350, 150);
        scene.setFill(null);
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
                root.getChildren().add(btnAceptar);
                StackPane.setAlignment(btnAceptar, Pos.BOTTOM_CENTER);

                StackPane.setMargin(btnAceptar, new Insets(10));
                btnAceptar.getStyleClass().add("button-aceptar");
                popup.show();
            }
            case 3 -> {
                Button btnAceptar = new Button("Aceptar");
                Button btnCancelar = new Button("Cancelar");
                btnAceptar.setOnAction(e -> {
                    popup.close();
                    if (accion != null) {
                        accion.run();
                    }
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
                    if (accion != null) {
                        accion.run();
                    }
                });
                popup.show();
                pause.play();
            }
        }
    }
}