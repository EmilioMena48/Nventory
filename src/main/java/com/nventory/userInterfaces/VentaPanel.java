package com.nventory.userInterfaces;


import com.nventory.DTO.ProveedorDTO;
import com.nventory.DTO.VentaArticuloDTO;
import com.nventory.DTO.VentaDTO;
import com.nventory.controller.ProveedorController;
import com.nventory.controller.VentaController;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Box;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class VentaPanel extends BorderPane {

    private static final String ERROR_CARGAR_VENTAS = "No se pudo cargar el historial de ventas: ";
    private static final String ERROR_REGISTRAR_VENTA = "No se pudo guardar la venta: ";
    private static final String VENTA_REGISTRADA = "Venta registrada correctamente.";
    private static final String CAMPOS_VACIOS = "Los campos obligatorios no pueden estar vacíos.";
    private static final String CSS = "/styles/estilosVenta.css";

    private final VentaController controller;
    private VentaDTO ventaDTO;
    private VentaArticuloDTO ventaArticuloDTO;
    private List<VentaArticuloDTO> ventaArticuloDTOList = new ArrayList<>();

    private VBox contenido;
    private TableView tablaVentas;


    public VentaPanel( VentaController controller) {
        this.controller = controller;
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
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        setTop(titulo);
        BorderPane.setMargin(titulo, new Insets(10));
    }

    private void configurarMenuLateral() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.getChildren().addAll(crearBoton("Nueva Venta", this::mostrarFormularioAlta));
        menu.getStyleClass().add("sombreadoMenu");
        setRight(menu);
    }

    private void configurarAreaContenido() {
        contenido = new VBox();
        contenido.setPadding(new Insets(0, 0, 0, 10));
        contenido.setSpacing(10);
        setCenter(contenido);
    }

    private Button crearBoton(String texto, Runnable accion) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> accion.run());
        btn.getStyleClass().add("button-menu");
        return btn;
    }

    private void mostrarFormularioAlta() {
        contenido.getChildren().clear();

        HBox contenidoBtn = new HBox();
        contenidoBtn.setSpacing(10);

        TextField fecha = new TextField();
        Label total = new Label("0");
        total.setId("total");
        List<LineaVentaFormulario > lineaVentaList = new ArrayList<>();

        fecha.getStyleClass().add("text-field");
        Button btnGuardar = crearBotonGuardar(fecha, lineaVentaList);
        Button btnCancelar = crearBotonCancelar();

        contenidoBtn.getChildren().addAll(btnGuardar, btnCancelar);

        GridPane formularioVenta = crearFormulario(fecha, total);
        Button btnAgregarLinea = crearBotonAgregarLineaVenta(formularioVenta, lineaVentaList);
        Button btnQuitarLinea = crearBotonQuitarLineaVenta(formularioVenta, lineaVentaList);
        agregarLineaVenta(formularioVenta, lineaVentaList);

        animarFormulario(formularioVenta);
        contenido.getChildren().addAll(formularioVenta, btnAgregarLinea, btnQuitarLinea, contenidoBtn);
    }

    private Button crearBotonAgregarLineaVenta(GridPane formularioVenta, List<LineaVentaFormulario> lineaVentaList) {
        Button btnAgregarLinea = new Button("Agregar Linea");
        btnAgregarLinea.getStyleClass().add("button-guardar");
        btnAgregarLinea.setOnAction(e -> {
            agregarLineaVenta(formularioVenta, lineaVentaList);
        });
        return btnAgregarLinea;
    }

    private Button crearBotonQuitarLineaVenta(GridPane formularioVenta, List<LineaVentaFormulario> lineaVentaList) {
        Button btnQuitarLinea = new Button("Quitar Linea");
        btnQuitarLinea.getStyleClass().add("button-guardar");
        btnQuitarLinea.setOnAction(e -> {
            quitarLineaVenta(formularioVenta, lineaVentaList);
        });
        return btnQuitarLinea;
    }

    private Button crearBotonGuardar(TextField fecha, List<LineaVentaFormulario> lineaVentaList) {
        Button btnGuardar = new Button("Guardar");
        btnGuardar.getStyleClass().add("button-guardar");
        btnGuardar.setOnAction(e -> {
            if (!lineaVentaList.isEmpty()) {
                LineaVentaFormulario primera = lineaVentaList.get(0);
                System.out.println(primera.getCodArt().getText());
                if (primera != null &&
                        !primera.getCantidadVendida().getText().trim().isEmpty() &&
                        primera.getPrecioVenta().getText() != null &&
                        primera.getCodArt().getText() != null) {
                    for (LineaVentaFormulario lv : lineaVentaList) {
                        registrarVentaArticulo(lv.getCantidadVendida(), lv.getPrecioVenta(), lv.getCodArt());
                    }
                    lineaVentaList.clear();
                    registrarVenta(fecha);
                }
            }
        });
        return btnGuardar;
    }

    private Button crearBotonCancelar() {
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("button-cancelar");
        btnCancelar.setOnAction(e -> cargarTablaVentas());
        return btnCancelar;
    }

    private GridPane crearFormulario(TextField fecha, Label total) {
        GridPane formularioVenta = new GridPane();
        formularioVenta.getStyleClass().add("formulario");

        formularioVenta.setVgap(10);
        formularioVenta.setHgap(10);

        int filaUltima = formularioVenta.getRowCount();

        formularioVenta.add(new Label("Fecha:"), 0, 0);
        formularioVenta.add(fecha, 0, 1);
        //formularioVenta.add(new Label("Total:"), 4, (filaUltima-1));
        formularioVenta.add(total, 4, filaUltima);

        return formularioVenta;
    }

    private void agregarLineaVenta(GridPane formularioVenta, List<LineaVentaFormulario> lineaVentaList) {
        Label cantVendida = new Label("Cantidad Vendida");
        Label precioV = new Label("Precio de Venta");
        Label codArticulo = new Label("Código de Articulo");

        TextField cantidadVendida = new TextField();
        TextField precioVenta = new TextField();
        TextField codArt = new TextField();

        VBox lineaVentaBox = new VBox(10);
        HBox lineaLabel = new HBox(10);
        HBox lineaField = new HBox(10);
        lineaField.setId("lineaField");
        lineaLabel.getChildren().addAll(cantVendida, precioV, codArticulo);
        lineaField.getChildren().addAll(cantidadVendida, precioVenta, codArt);

        lineaVentaBox.getChildren().addAll(lineaLabel, lineaField);

        int filaUltima = formularioVenta.getRowCount();
        System.out.println(filaUltima);

        formularioVenta.add(lineaVentaBox, 0, filaUltima);
        Integer rowIndex = GridPane.getRowIndex(lineaVentaBox);

        aplicarSubtotalLinea(cantidadVendida, precioVenta, codArt, lineaField, formularioVenta, rowIndex);

        GridPane.setRowIndex(formularioVenta.lookup("#total"), formularioVenta.getRowCount());

        LineaVentaFormulario lineaVenta = new LineaVentaFormulario();
        lineaVenta.setCodArt(codArt);
        lineaVenta.setPrecioVenta(precioVenta);
        lineaVenta.setCantidadVendida(cantidadVendida);
        lineaVentaList.add(lineaVenta);
    }

    private BigDecimal aplicarSubtotalLinea(TextField cantidadVendida, TextField precioVenta, TextField codArt, HBox lineaField, GridPane formularioVenta, int rowIndex) {
        ventaArticuloDTO = new VentaArticuloDTO();
        Runnable accion = () -> {
            if (!cantidadVendida.getText().trim().isEmpty() &&
                    !precioVenta.getText().trim().isEmpty() &&
                    !codArt.getText().trim().isEmpty()) {
                if(rowIndex > 3) {
                    quitarSubTotalLinea(formularioVenta, rowIndex);
                }
                //mostrarTotalMenosLinea(ventaArticuloDTO.getSubTotalVenta(),formularioVenta);
                ventaArticuloDTO.setCantidadVendida(Integer.parseInt(cantidadVendida.getText()));
                ventaArticuloDTO.setPrecioVenta(new BigDecimal(precioVenta.getText()));
                ventaArticuloDTO.setCodArticulo(Long.parseLong(codArt.getText()));
                ventaArticuloDTO.setSubTotalVenta(controller.calcularSubTotalVenta(ventaArticuloDTO));
                ventaArticuloDTOList.add(ventaArticuloDTO);
                mostrarSubTotalLinea(ventaArticuloDTO.getSubTotalVenta(), lineaField, formularioVenta, rowIndex);
                mostrarTotalLinea(ventaArticuloDTO.getSubTotalVenta(),formularioVenta);
                ventaArticuloDTOList.remove(ventaArticuloDTO);
                //ventaArticuloDTO = null;
            }
        };

        ChangeListener<String> listener = (obs, oldVal, newVal) -> accion.run();

        cantidadVendida.textProperty().addListener(listener);
        precioVenta.textProperty().addListener(listener);
        codArt.textProperty().addListener(listener);

        cantidadVendida.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.isEmpty() && newValue.isEmpty()) {
                quitarSubTotalLinea(formularioVenta, rowIndex);
                mostrarTotalMenosLinea(ventaArticuloDTO.getSubTotalVenta(),formularioVenta);
            }
        });

        precioVenta.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.isEmpty() && newValue.isEmpty()) {
                quitarSubTotalLinea(formularioVenta, rowIndex);
                mostrarTotalMenosLinea(ventaArticuloDTO.getSubTotalVenta(),formularioVenta);
            }
        });

        codArt.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.isEmpty() && newValue.isEmpty()) {
                quitarSubTotalLinea(formularioVenta, rowIndex);
                mostrarTotalMenosLinea(ventaArticuloDTO.getSubTotalVenta(),formularioVenta);
            }
        });
        return ventaArticuloDTO.getSubTotalVenta();
    }

    private void mostrarSubTotalLinea(BigDecimal subTotal, HBox lineaField, GridPane formularioVenta, int rowIndex) {

        Label subTotalLabel = new Label(subTotal.toString());
        formularioVenta.add(subTotalLabel, 4, rowIndex);
        GridPane.setValignment(subTotalLabel, VPos.BOTTOM);
    }

    private void quitarSubTotalLinea(GridPane formularioVenta, int rowIndex) {
        int filaObjetivo = rowIndex;
        int columnaObjetivo = 4;
        Node nodoAEliminar = null;

        for (Node node : formularioVenta.getChildren()) {
            Integer fila = GridPane.getRowIndex(node);
            Integer columna = GridPane.getColumnIndex(node);

            if ((fila == null ? 0 : fila) == filaObjetivo &&
                    (columna == null ? 0 : columna) == columnaObjetivo) {
                nodoAEliminar = node;
                break;
            }
        }
        if (nodoAEliminar != null) {
            formularioVenta.getChildren().remove(nodoAEliminar);
        }
        System.out.println("acaaa");
    }

    private void mostrarTotalLinea(BigDecimal subtotal, GridPane formularioVenta) {
        Label campoTotal = (Label) formularioVenta.lookup("#total");
        BigDecimal nuevoValor = controller.calcularTotalVenta(ventaArticuloDTOList);
        //BigDecimal nuevoValor = new BigDecimal(campoTotal.getText()).add(subtotal);
        campoTotal.setText(nuevoValor.toString());
    }

    private void mostrarTotalMenosLinea(BigDecimal subtotal, GridPane formularioVenta) {
        Label campoTotal = (Label) formularioVenta.lookup("#total");
        BigDecimal nuevoValor = new BigDecimal(campoTotal.getText()).subtract(subtotal);
        campoTotal.setText(nuevoValor.toString());
    }


    private void quitarLineaVenta(GridPane formularioVenta, List<LineaVentaFormulario> lineaVentaList) {

        ObservableList<Node> hijos = formularioVenta.getChildren();
        System.out.println(hijos);
        boolean fila1=false;
        int indiceSubtotal = 0;
        int indiceFilaSubtotal = 0;
        for (int i = hijos.size() - 1; i > 3; i--) {
            Node node = hijos.get(i);
            indiceSubtotal = i;
            indiceFilaSubtotal = GridPane.getRowIndex(node);
            if (node instanceof VBox) {
                hijos.remove(i);
                fila1 = true;
                break;
            }
        }
        if (lineaVentaList.size() > 2) {
            lineaVentaList.remove(lineaVentaList.size() - 1);
            for(LineaVentaFormulario linea : lineaVentaList) {
                System.out.println(linea);
            }
        }

        if(fila1) {
            Label label = (Label) formularioVenta.getChildren().get(indiceSubtotal);
            BigDecimal subtotal = new BigDecimal(label.getText());
            quitarSubTotalLinea(formularioVenta, indiceFilaSubtotal);
            mostrarTotalMenosLinea(subtotal, formularioVenta);
        }

    }

    private void registrarVenta(TextField fecha) {
        if (fecha.getText().isEmpty()) {
            mostrarAlerta(CAMPOS_VACIOS, 2, null);
        } else {
            try {
                ventaDTO = null;
                ventaDTO = new VentaDTO();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                LocalDateTime fechaHora = LocalDateTime.parse(fecha.getText().trim(), formatter);
                ventaDTO.setFechaHoraVenta(fechaHora);
                for (VentaArticuloDTO ventaArticuloDTO : ventaArticuloDTOList) {
                    ventaDTO.addVentaArticuloDTO(ventaArticuloDTO);
                }

                if (controller != null) {
                    controller.AltaVenta(ventaDTO);
                    mostrarAlerta(VENTA_REGISTRADA, 4, () -> {
                        fecha.clear();
                        cargarTablaVentas();
                    });
                } else {
                    mostrarAlerta("Error: El controlador no está inicializado.", 2, null);
                }
            } catch (Exception ex) {
                mostrarAlerta(ERROR_REGISTRAR_VENTA + ex.getMessage(), 2, null);
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void registrarVentaArticulo(TextField cantidad, TextField precio, TextField codArt) {
        if (cantidad.getText().isEmpty() || precio.getText().isEmpty() || codArt.getText().isEmpty()) {
            mostrarAlerta(CAMPOS_VACIOS, 2, null);
        } else {
            ventaArticuloDTO = new VentaArticuloDTO();
            ventaArticuloDTO.setCantidadVendida(Integer.parseInt(cantidad.getText()));
            ventaArticuloDTO.setPrecioVenta(new BigDecimal(precio.getText()));
            ventaArticuloDTO.setCodArticulo(Long.parseLong(codArt.getText()));
            ventaArticuloDTOList.add(ventaArticuloDTO);
            //System.out.println(ventaArticuloDTO.getPrecioVenta());
        }
    }

    private void cargarTablaVentas() {
        contenido.getChildren().clear();

        tablaVentas = new TableView<>();
        tablaVentas.getStyleClass().add("tablaProveedor");

        tablaVentas.getColumns().addAll(

                crearColumna("Código", "numeroVenta"),
                crearColumna("Fecha", "fechaHoraVenta"),
                crearColumna("Monto Total", "montoTotalVenta"),
                crearColumnaVerDetalle()
        );

       try {
            tablaVentas.getItems().setAll(controller.ListarVentas());
            contenido.getChildren().add(tablaVentas);
        } catch (Exception e) {
            mostrarAlerta(ERROR_CARGAR_VENTAS + e.getMessage(), 2, null);
           System.out.println(e.getMessage());
        }
    }

    private TableColumn crearColumna(String titulo, String propiedad) {
        TableColumn columna = new TableColumn<>(titulo);
        columna.setCellValueFactory(new PropertyValueFactory<>(propiedad));
        return columna;
    }

    private TableColumn<VentaDTO, Void> crearColumnaVerDetalle() {
        TableColumn<VentaDTO, Void> colVerDetalle = new TableColumn<>("Detalles");
        colVerDetalle.setCellFactory(param -> new TableCell<>() {
            private final Button btnVerDetalle = new Button("Ver Detalle");
            private final HBox container = new HBox(5, btnVerDetalle);

            {
                btnVerDetalle.setOnAction(e -> {
                    ventaDTO = getTableView().getItems().get(getIndex());
                    mostrarPopupDetalle(ventaDTO);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
        return colVerDetalle;
    }

    private void listarArticulosPorProveedor() {
        contenido.getChildren().clear();
        // Implementar lógica
    }

    private void asociarArticuloAProveedor() {
        contenido.getChildren().clear();
        // Implementar lógica
    }

    private void mostrarAlerta(String mensaje) {
        PopupMensaje.mostrarPopup(mensaje,1, null);
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

    public void mostrarPopupDetalle(VentaDTO ventaDTO) {
        Stage popup = new Stage();
        popup.initStyle(StageStyle.UNDECORATED);
        popup.initModality(Modality.APPLICATION_MODAL);

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));

        ventaArticuloDTOList = controller.ListarVentasArticulo(ventaDTO.getNumeroVenta());
        for (VentaArticuloDTO ventaArticuloDTO : ventaArticuloDTOList) {
            Label lblMensaje = new Label(ventaArticuloDTO.getCodArticulo().toString());
            Label lb2Mensaje = new Label(ventaArticuloDTO.getNombreArticulo().toString());
            Label lb3Mensaje = new Label(ventaArticuloDTO.getOrdenVentaArticulo().toString());
            Label lb4Mensaje = new Label(String.valueOf(ventaArticuloDTO.getCantidadVendida()));
            Label lb5Mensaje = new Label(ventaArticuloDTO.getPrecioVenta().toString());
            VBox bloque = new VBox(lblMensaje, lb2Mensaje, lb3Mensaje, lb4Mensaje, lb5Mensaje);
            bloque.setStyle("-fx-border-color: gray; -fx-padding: 5;");
            vbox.getChildren().add(bloque);
        }
        Button btnAceptar = new Button("Aceptar");
        btnAceptar.setOnAction(e -> {
            popup.close();
            ventaArticuloDTOList.clear();
            cargarTablaVentas();
        });
        vbox.getChildren().add(btnAceptar);

        StackPane.setMargin(btnAceptar, new Insets(10));
        StackPane.setAlignment(btnAceptar, Pos.BOTTOM_CENTER);
        btnAceptar.getStyleClass().add("button-aceptar");
        Scene scene = new Scene(vbox, 350, 400);
        scene.setFill(null);
        popup.setScene(scene);
        popup.centerOnScreen();
        popup.show();
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

    public class LineaVentaFormulario {
        private TextField cantidadVendida;

        public void setCantidadVendida(TextField cantidadVendida) {
            this.cantidadVendida = cantidadVendida;
        }

        public void setPrecioVenta(TextField precioVenta) {
            this.precioVenta = precioVenta;
        }

        public void setCodArt(TextField codArt) {
            this.codArt = codArt;
        }

        private TextField precioVenta;
        private TextField codArt;

        public LineaVentaFormulario() {}

        public TextField getCantidadVendida() {
            return cantidadVendida;
        }

        public TextField getPrecioVenta() {
            return precioVenta;
        }

        public TextField getCodArt() {
            return codArt;
        }
    }

}
