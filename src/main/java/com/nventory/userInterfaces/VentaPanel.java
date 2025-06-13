package com.nventory.userInterfaces;
import com.nventory.DTO.ArticuloDTO;
import com.nventory.DTO.VentaArticuloDTO;
import com.nventory.DTO.VentaDTO;
import com.nventory.controller.MaestroArticuloController;
import com.nventory.controller.VentaController;
import com.nventory.model.Venta;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


public class VentaPanel extends BorderPane {

    private static final String ERROR_CARGAR_VENTAS = "No se pudo cargar el historial de ventas: ";

    private static final String CSS = "/styles/estilosVenta.css";
    private final DatePicker datePicker = new DatePicker(LocalDate.now());
    private final ComboBox<Integer> hourComboBox = new ComboBox<>();
    private final ComboBox<Integer> minuteComboBox = new ComboBox<>();
    private final TextField cantidadLineasField = new TextField();
    private final Button crearLineasButton = new Button("Crear líneas");
    private final VBox lineasBox = new VBox(10);
    private final Button guardarButton = new Button("Guardar");
    private final Button cancelarButton = new Button("Cancelar");

    private final MaestroArticuloController maestroArticuloController = new MaestroArticuloController();
    private final VentaController controller = new VentaController();

    private final List<LineaVentaUI> lineasVenta = new ArrayList<>();
    private final Set<String> articulosSeleccionados = new HashSet<>();

    private Button botonNuevaVenta;

    private VBox contenido;
    private TableView tablaVentas;


    public VentaPanel() {
        this.getStylesheets().add(Objects.requireNonNull(getClass().getResource(CSS)).toExternalForm());
        inicializarInterfaz();
        cargarTablaVentas();
    }

    private void inicializarInterfaz() {
        configurarHeader();
        configurarMenuLateral();
        configurarAreaContenido();
    }

    private void configurarHeader() {
        Label titulo = new Label("Historial Ventas");
        titulo.setId("tituloHeader");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        setTop(titulo);
        BorderPane.setMargin(titulo, new Insets(10));
    }

    private void configurarMenuLateral() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        botonNuevaVenta = crearBoton("Nueva Venta", this::mostrarFormularioAlta);
        botonNuevaVenta.getStyleClass().add("botonNuevaVenta");
        menu.getChildren().addAll(botonNuevaVenta);
        menu.getStyleClass().add("sombreadoMenu");
        setRight(menu);
    }

    private void configurarAreaContenido() {
        contenido = new VBox();
        contenido.setPadding(new Insets(0, 0, 0, 10));
        contenido.setSpacing(10);

        StackPane contenedorCentrado = new StackPane(contenido);
        contenedorCentrado.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(contenedorCentrado);
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(10));

        this.setCenter(scrollPane);
    }

    private void cargarTablaVentas() {
        contenido.getChildren().clear();
        botonNuevaVenta.setDisable(false);
        botonNuevaVenta.setVisible(true);

        tablaVentas = new TableView<>();
        tablaVentas.getStyleClass().add("tablaProveedor");
        tablaVentas.getStyleClass().add("table-view");
        tablaVentas.setMaxWidth(364);

        tablaVentas.getColumns().addAll(
                crearColumna("Código", "numeroVenta"),
                crearColumna("Fecha", "fechaHoraVenta"),
                crearColumna("Monto Total", "montoTotalVenta"),
                crearColumnaVerDetalle()
        );
        try {
            tablaVentas.getItems().setAll(controller.ListarVentas());
            contenido.getChildren().add(tablaVentas);
            contenido.setAlignment(Pos.CENTER);

        } catch (Exception e) {
            mostrarAlerta(ERROR_CARGAR_VENTAS + e.getMessage(), 2, null);
        }
        Label header = (Label) this.lookup("#tituloHeader");
        header.setText("Historial Ventas");
    }

    private TableColumn<Venta, Object> crearColumna(String titulo, String propiedad) {
        TableColumn<Venta, Object> columna = new TableColumn<>(titulo);
        columna.setCellValueFactory(new PropertyValueFactory<>(propiedad));

        columna.setCellFactory(tc -> new TableCell<Venta, Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    if ("fechaHoraVenta".equals(propiedad) && item instanceof LocalDateTime fecha) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                        setText(formatter.format(fecha));
                    } else if ("montoTotalVenta".equals(propiedad) && item instanceof Number numero) {
                        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
                        setText(formato.format(numero));
                    } else {
                        setText(item.toString());
                    }
                }
                setAlignment(Pos.CENTER);
            }
        });
        return columna;
    }


    private TableColumn<VentaDTO, Void> crearColumnaVerDetalle() {
        TableColumn<VentaDTO, Void> colVerDetalle = new TableColumn<>("Detalles");
        colVerDetalle.setCellFactory(param -> new TableCell<>() {
            private Button btnVerDetalle = new Button("Ver Detalle");
            private final HBox container = new HBox(5, btnVerDetalle);
            VentaDTO ventaDTO;
            {
                btnVerDetalle.setOnAction(e -> {
                    ventaDTO = getTableView().getItems().get(getIndex());
                    mostrarPopupDetalle(ventaDTO);
                });
                btnVerDetalle.getStyleClass().add("btnVerDetalle");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
        return colVerDetalle;
    }

    private void mostrarAlerta(String mensaje, int tipo, Runnable accion) {
        VentaPanel.PopupMensaje.mostrarPopup(mensaje, tipo, accion);
    }

    public void mostrarPopupDetalle(VentaDTO ventaDTO) {
        Stage popup = new Stage();
        popup.initStyle(StageStyle.UNDECORATED);
        popup.initModality(Modality.APPLICATION_MODAL);

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));

        List<VentaArticuloDTO> ventaArticuloDTOList;

        ventaArticuloDTOList = controller.ListarVentasArticulo(ventaDTO.getNumeroVenta());
        for (VentaArticuloDTO ventaArticuloDTO : ventaArticuloDTOList) {
            Label lblMensaje = new Label("Código Artículo: "+ventaArticuloDTO.getCodArticulo().toString());
            Label lb2Mensaje = new Label("Nombre Artículo: "+ventaArticuloDTO.getNombreArticulo().toString());
            Label lb3Mensaje = new Label("Órden de Linea: "+ventaArticuloDTO.getOrdenVentaArticulo().toString());
            Label lb4Mensaje = new Label("Cantidad: "+ventaArticuloDTO.getCantidadVendida());
            Label lb5Mensaje = new Label("Precio de Venta: $"+ventaArticuloDTO.getPrecioVenta().toString());
            VBox bloque = new VBox(lb3Mensaje, lblMensaje, lb2Mensaje, lb4Mensaje, lb5Mensaje);
            bloque.setStyle("-fx-border-color: gray; -fx-padding: 5;");
            vbox.getChildren().add(bloque);
        }
        Button btnAceptar = new Button("Aceptar");
        btnAceptar.setOnAction(e -> {
            popup.close();
            ventaArticuloDTOList.clear();
            cargarTablaVentas();
        });
        btnAceptar.getStyleClass().add("btnAceptar");
        vbox.getChildren().add(btnAceptar);

        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPadding(new Insets(10));
        scrollPane.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(scrollPane, 350, 400);
        scene.setFill(null);
        popup.setScene(scene);
        popup.centerOnScreen();
        popup.show();
    }

    private void mostrarFormularioAlta() {
        contenido.getChildren().clear();
        hourComboBox.setItems(FXCollections.observableArrayList(generarRango(0, 23)));
        minuteComboBox.setItems(FXCollections.observableArrayList(generarRango(0, 59)));

        VBox fechaBox = new VBox(5, new Label("Fecha:"), datePicker);
        VBox horaBox = new VBox(5, new Label("Hora:"), hourComboBox);
        VBox minutoBox = new VBox(5, new Label("Minuto:"), minuteComboBox);
        HBox fechaHoraBox = new HBox(10, fechaBox, horaBox, minutoBox);

        VBox cantidadLabelBox = new VBox(5, new Label("Cantidad de líneas:"), cantidadLineasField);

        VBox buttonBox = new VBox(crearLineasButton);
        buttonBox.setAlignment(Pos.BOTTOM_CENTER);

        HBox cantidadBox = new HBox(10, cantidadLabelBox, buttonBox);

        crearLineasButton.setOnAction(e -> crearLineas());
        guardarButton.setOnAction(e -> guardarVenta());
        cancelarButton.setOnAction(e -> {
            resetearFormulario();
            cargarTablaVentas();
            botonNuevaVenta.setDisable(false);
            botonNuevaVenta.setVisible(true);
        });

        cantidadLineasField.getStyleClass().add("campo-cantidad");

        guardarButton.setDisable(true);

        guardarButton.getStyleClass().add("boton-guardar");
        cancelarButton.getStyleClass().add("boton-cancelar");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox botonesBox = new HBox(10, guardarButton, spacer, cancelarButton);

        VBox centro = new VBox(15, fechaHoraBox, cantidadBox, lineasBox, botonesBox);
        centro.getStyleClass().add("contenedor-formulario");

        contenido.getChildren().add(centro);

        botonNuevaVenta.setDisable(true);
        botonNuevaVenta.setVisible(false);

        Label header = (Label) this.lookup("#tituloHeader");
        header.setText("Registrar Venta");

    }

    private Button crearBoton(String texto, Runnable accion) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> accion.run());
        btn.getStyleClass().add("button-menu");
        return btn;
    }

    private void crearLineas() {
        try {
            int cantidad = Integer.parseInt(cantidadLineasField.getText());
            if (cantidad <= 0) throw new NumberFormatException();

            lineasBox.getChildren().clear();
            lineasVenta.clear();
            articulosSeleccionados.clear();

            List<ArticuloDTO> articulos = maestroArticuloController.listarArticulosDisponibles();

            List<String> nombresArticulos = new ArrayList<>();
            for (ArticuloDTO articulo : articulos) {
                nombresArticulos.add(articulo.getNombreArticulo());
            }

            for (int i = 0; i < cantidad; i++) {
                LineaVentaUI linea = new LineaVentaUI(nombresArticulos, maestroArticuloController);
                final int index = i;

                linea.getComboArticulo().valueProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        // Validar si la línea anterior está completa
                        if (index > 0 && !lineasVenta.get(index - 1).esValida()) {
                            mostrarAlerta("Error", "Debe completar primero la línea anterior.");
                            // Revertir selección sin deshabilitar
                            Platform.runLater(() -> linea.getComboArticulo().setValue(null));
                        } else {
                            // Si es válido, registrar selección
                            articulosSeleccionados.add(newVal);
                            actualizarOpcionesArticulos();
                            linea.deshabilitarSeleccionArticulo();
                        }
                    }
                });
                lineasVenta.add(linea);
                lineasBox.getChildren().add(linea.getContenedor());
            }
            crearLineasButton.setDisable(true);
            cantidadLineasField.setDisable(true);
            guardarButton.setDisable(false);

        } catch (NumberFormatException ex) {
            mostrarAlerta("Error", "Ingrese una cantidad válida de líneas.");
        }
    }

    private void actualizarOpcionesArticulos() {
        for (LineaVentaUI linea : lineasVenta) {
            if (!linea.getComboArticulo().isDisabled()) {
                String valorActual = linea.getComboArticulo().getValue();
                linea.getComboArticulo().getItems().setAll(
                        maestroArticuloController.listarArticulosDisponibles().stream()
                                .map(ArticuloDTO::getNombreArticulo)
                                .filter(nombre -> !articulosSeleccionados.contains(nombre) || nombre.equals(valorActual))
                                .collect(Collectors.toList())
                );
            }
        }
    }

    private void guardarVenta() {
        if (datePicker.getValue() == null || hourComboBox.getValue() == null || minuteComboBox.getValue() == null) {
            mostrarAlerta("Error", "Debe completar la fecha y hora de la venta.");
            return;
        }

        for (LineaVentaUI linea : lineasVenta) {
            if (!linea.esValida()) {
                mostrarAlerta("Error", "Complete todas las líneas con valores válidos.");
                return;
            }
        }

        LocalDate fecha = datePicker.getValue();
        LocalTime hora = LocalTime.of(hourComboBox.getValue(), minuteComboBox.getValue());
        LocalDateTime fechaHoraVenta = LocalDateTime.of(fecha, hora);

        VentaDTO venta = new VentaDTO();
        venta.setFechaHoraVenta(fechaHoraVenta);

        List<VentaArticuloDTO> vList = new ArrayList<>();
        for (LineaVentaUI linea : lineasVenta) {
            VentaArticuloDTO dto = new VentaArticuloDTO();
            dto.setNombreArticulo(linea.getNombreArticulo());
            dto.setCantidadVendida(linea.getCantidad());
            dto.setPrecioVenta(linea.getPrecio());

            // Aquí se calcula el subtotal llamando al controller pasando el DTO
            BigDecimal subTotal = controller.calcularSubTotalVenta(dto);
            dto.setSubTotalVenta(subTotal);

            vList.add(dto);
            venta.addVentaArticuloDTO(dto);
        }

        // Se calcula el total pasando la lista completa al controller
        BigDecimal total = controller.calcularTotalVenta(vList);
        venta.setMontoTotalVenta(total);

        mostrarConfirmacion(venta);
    }


    private void mostrarConfirmacion(VentaDTO venta) {
        Stage confirmStage = new Stage();
        TableView<VentaArticuloDTO> table = new TableView<>();
        table.setPrefHeight(146);
        table.setMaxHeight(146);

        table.setPrefWidth(255);
        table.setMaxWidth(255);

        TableColumn<VentaArticuloDTO, String> nombreCol = new TableColumn<>("Artículo");
        nombreCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombreArticulo()));

        TableColumn<VentaArticuloDTO, Integer> cantCol = new TableColumn<>("Cantidad");
        cantCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getCantidadVendida()).asObject());

        TableColumn<VentaArticuloDTO, String> precioCol = new TableColumn<>("Precio");
        precioCol.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>("$"+c.getValue().getPrecioVenta()));

        TableColumn<VentaArticuloDTO, String> subTotalCol = new TableColumn<>("Subtotal");
        subTotalCol.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>("$"+c.getValue().getSubTotalVenta()));

        table.getColumns().addAll(nombreCol, cantCol, precioCol, subTotalCol);
        table.getItems().addAll(venta.getVentaArticuloDTO());

        Label totalLabel = new Label("Total: $" + venta.getMontoTotalVenta());
        Button confirmar = new Button("Confirmar");
        Button cancelar = new Button("Cancelar");

        confirmar.getStyleClass().add("boton-guardar-confirmacion");
        cancelar.getStyleClass().add("boton-cancelar-confirmacion");

        confirmar.setOnAction(e -> {
            try {
                controller.AltaVenta(venta);
                confirmStage.close();
                resetearFormulario();
                cargarTablaVentas();
            } catch (Exception ex) {
                mostrarAlerta(ERROR_CARGAR_VENTAS + ex.getMessage(), 2, null);
            }
        });


        cancelar.setOnAction(e -> confirmStage.close());

        VBox layout = new VBox(10, table, totalLabel, new HBox(10, confirmar, cancelar));
        layout.setPadding(new Insets(20));

        confirmStage.setScene(new Scene(layout));
        confirmStage.setTitle("Confirmación de Venta");
        confirmStage.show();
    }

    private void resetearFormulario() {
        datePicker.setValue(LocalDate.now());
        hourComboBox.setValue(null);
        minuteComboBox.setValue(null);
        cantidadLineasField.clear();
        cantidadLineasField.setDisable(false);
        crearLineasButton.setDisable(false);
        lineasBox.getChildren().clear();
        lineasVenta.clear();
        articulosSeleccionados.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

   private List<Integer> generarRango(int inicio, int fin) {
        return java.util.stream.IntStream.rangeClosed(inicio, fin).boxed().collect(Collectors.toList());
    }


    // Clases auxiliares (Lineas, DTOs, Controladores falsos) vendrán en la siguiente respuesta.
    public class LineaVentaUI {
        private final ComboBox<String> comboArticulo;
        private final TextField precio;
        private final Spinner<Integer> cantidad;
        private final HBox contenedor;

        public LineaVentaUI(List<String> nombresArticulosDisponibles, MaestroArticuloController maestroArticuloController) {
            comboArticulo = new ComboBox<>(FXCollections.observableArrayList(nombresArticulosDisponibles));
            comboArticulo.setPrefWidth(200);

            precio = new TextField();
            precio.setEditable(false);
            precio.setPrefWidth(100);

            cantidad = new Spinner<>(1, 1000, 1);
            cantidad.setEditable(true);
            cantidad.setPrefWidth(80);

            VBox articuloBox = new VBox(5, new Label("Nombre de Artículo"), comboArticulo);
            VBox precioBox = new VBox(5, new Label("Precio"), precio);
            VBox cantidadBox = new VBox(5, new Label("Cantidad"), cantidad);

            contenedor = new HBox(15, articuloBox, precioBox, cantidadBox);

            contenedor.setPadding(new Insets(10));
            contenedor.getStyleClass().add("linea-venta");


            // Listener para actualizar el precio al seleccionar un artículo
            comboArticulo.setOnAction(e -> {
                String articuloSeleccionado = comboArticulo.getValue();
                if (articuloSeleccionado != null && !articuloSeleccionado.isBlank()) {
                    ArticuloDTO artDTO = maestroArticuloController.buscarArtPorNombre(articuloSeleccionado);
                    if (artDTO != null) {
                        BigDecimal precioArticulo = artDTO.getPrecioArticulo();
                        precio.setText(precioArticulo.toPlainString());

                    } else {
                        precio.setText("");
                    }
                } else {
                    precio.setText("");
                }
            });

        }

        public HBox getContenedor() {
            return contenedor;
        }

        public String getNombreArticulo() {
            return comboArticulo.getValue();
        }

        public BigDecimal getPrecio() {
            try {
                return new BigDecimal(precio.getText());
            } catch (NumberFormatException e) {
                return BigDecimal.ZERO;
            }
        }

        public int getCantidad() {
            return cantidad.getValue();
        }

        public void deshabilitarSeleccionArticulo() {
            comboArticulo.setDisable(true);
        }

        public ComboBox<String> getComboArticulo() {
            return comboArticulo;
        }

        public boolean esValida() {
            return comboArticulo.getValue() != null &&
                    !comboArticulo.getValue().isBlank() &&
                    getPrecio().compareTo(BigDecimal.ZERO) > 0 &&
                    getCantidad() > 0;
        }
    }

    class PopupMensaje {
        public static void mostrarPopup(String mensaje, int tipo, Runnable accion) {
            Stage popup = new Stage();
            popup.initStyle(StageStyle.UNDECORATED);
            popup.initModality(Modality.APPLICATION_MODAL);

            Label lblMensaje = new Label(mensaje);
            lblMensaje.setStyle("-fx-background-color: #6dbef1; -fx-text-fill: white; -fx-padding: 5px; -fx-font-size: 14px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
            lblMensaje.setWrapText(true);
            lblMensaje.setAlignment(Pos.CENTER);

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
                    btnAceptar.setOnAction(e -> {
                        popup.close();
                    });
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

}
