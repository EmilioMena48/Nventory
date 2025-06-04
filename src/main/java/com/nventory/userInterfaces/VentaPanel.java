package com.nventory.userInterfaces;


import com.nventory.DTO.ArticuloDTO;
import com.nventory.DTO.ProveedorDTO;
import com.nventory.DTO.VentaArticuloDTO;
import com.nventory.DTO.VentaDTO;
import com.nventory.controller.ArticuloController;
import com.nventory.controller.MaestroArticuloController;
import com.nventory.controller.ProveedorController;
import com.nventory.controller.VentaController;
import com.nventory.model.Venta;
import com.nventory.service.ArticuloService;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class VentaPanel extends BorderPane {

    private static final String ERROR_CARGAR_VENTAS = "No se pudo cargar el historial de ventas: ";
    private static final String ERROR_REGISTRAR_VENTA = "No se pudo guardar la venta: ";
    private static final String VENTA_REGISTRADA = "Venta registrada correctamente.";
    private static final String CAMPOS_VACIOS = "Los campos obligatorios no pueden estar vacíos.";
    private static final String CSS = "/styles/estilosVenta.css";

    private final VentaController controller;
    private MaestroArticuloController maestroArticuloController;
    private VentaDTO ventaDTO;
    private VentaArticuloDTO ventaArticuloDTO;
    private List<VentaArticuloDTO> ventaArticuloDTOList = new ArrayList<>();
    private List<HBox> cajaCalculos = new ArrayList<>();
    private List<VBox> listaLineasVenta = new ArrayList<>();
    private int indiceCaja = 0;
    private int cantLineas = 0;
    private int cantLineas2 = 1;
    private int cantLineasLlenas = 0;
    private int filasRemovidas = 0;
    private boolean ultimaLineaLLena = false;
    private Button botonNuevaVenta;

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
        titulo.setId("tituloHeader");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        setTop(titulo);
        BorderPane.setMargin(titulo, new Insets(10));
    }

    private void configurarMenuLateral() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        botonNuevaVenta = crearBoton("Nueva Venta", this::mostrarFormularioAlta);
        botonNuevaVenta.setStyle(" -fx-min-width: 90px;\n" +
                "    -fx-background-color: #3498db;\n" +
                "    -fx-text-fill: white;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-background-radius: 5px;\n" +
                "    -fx-padding: 10 20;\n" +
                "    -fx-cursor: hand;\n" +
                "    -fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.2), 4, 0.0, 0, 2);\n" +
                "    -fx-transition: background-color 0.3s ease;");
        menu.getChildren().addAll(botonNuevaVenta);
        menu.getStyleClass().add("sombreadoMenu");
        setRight(menu);
    }

    private void configurarAreaContenido() {
        contenido = new VBox();
        contenido.setPadding(new Insets(0, 0, 0, 10));
        contenido.setSpacing(10);

        ScrollPane scrollPane = new ScrollPane(contenido);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);
        setCenter(scrollPane);
    }

    private Button crearBoton(String texto, Runnable accion) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> accion.run());
        btn.getStyleClass().add("button-menu");
        return btn;
    }

    private void mostrarFormularioAlta() {
        ventaArticuloDTOList.clear();
        cajaCalculos.clear();
        indiceCaja = 0;
        cantLineas = 0;
        cantLineasLlenas = 0;
        contenido.getChildren().clear();

        HBox contenidoBtn = new HBox();
        contenidoBtn.setSpacing(323);

        DatePicker fecha = new DatePicker();
        ComboBox<String> horaCombo = new ComboBox<>();
        ComboBox<String> minutoCombo = new ComboBox<>();

        fecha.setMaxWidth(100);

        configurarHorayMin(horaCombo, minutoCombo);

        HBox cajaFecha = new HBox(10);
        cajaFecha.getChildren().addAll(fecha, horaCombo, minutoCombo);
        cajaFecha.setMinWidth(80);
        cajaFecha.setMaxWidth(250);

        List<LineaVentaFormulario > lineaVentaList = new ArrayList<>();

        Button btnGuardar = crearBotonGuardar(cajaFecha, lineaVentaList);
        Button btnCancelar = crearBotonCancelar();

        btnGuardar.setStyle(" -fx-min-width: 90px;\n" +
                "    -fx-background-color: #3498db;\n" +
                "    -fx-text-fill: white;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-background-radius: 5px;\n" +
                "    -fx-padding: 10 20;\n" +
                "    -fx-cursor: hand;\n" +
                "    -fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.2), 4, 0.0, 0, 2);\n" +
                "    -fx-transition: background-color 0.3s ease;");

        btnCancelar.setStyle("-fx-min-width: 90px;\n" +
                "    -fx-background-color: #e74c3c;\n" +
                "    -fx-text-fill: white;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-background-radius: 5px;\n" +
                "    -fx-padding: 10 20;\n" +
                "    -fx-cursor: hand;\n" +
                "    -fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.2), 4, 0.0, 0, 2);\n" +
                "    -fx-transition: background-color 0.3s ease;");

        contenidoBtn.getChildren().addAll(btnGuardar, btnCancelar);
        contenidoBtn.setAlignment(Pos.CENTER);

        GridPane formularioVenta = crearFormulario(cajaFecha);
        Button btnAgregarLinea = crearBotonAgregarLineaVenta(formularioVenta, lineaVentaList);
        Button btnQuitarLinea = crearBotonQuitarLineaVenta(formularioVenta, lineaVentaList);
        HBox botonesLinea = new HBox(10);
        botonesLinea.getChildren().addAll(btnAgregarLinea, btnQuitarLinea);
        botonesLinea.setAlignment(Pos.CENTER);

        cantLineas = cantLineas+1;
        agregarLineaVenta(formularioVenta, lineaVentaList);
        cantLineas = cantLineas-1;

        animarFormulario(formularioVenta);
        contenido.getChildren().addAll(formularioVenta, botonesLinea, contenidoBtn);
        formularioVenta.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-border-insets: 5; -fx-border-radius: 3;");
        formularioVenta.setMaxWidth(510);
        contenido.setAlignment(Pos.CENTER);

        VBox.setMargin(botonesLinea, new Insets(0,320,0,0));

        botonNuevaVenta.setDisable(true);
        botonNuevaVenta.setVisible(false);

        Label header = (Label) this.lookup("#tituloHeader");
        header.setText("Registrar Venta");
    }

    private void configurarHorayMin (ComboBox<String> hora, ComboBox<String> min) {
        hora.setValue(String.format("%02d", 0));
        min.setValue(String.format("%02d", 0));

        for (int i = 0; i < 24; i++) {
            hora.getItems().add(String.format("%02d", i));
        }

        for (int i = 0; i < 60; i ++) {
            min.getItems().add(String.format("%02d", i));
        }
    }

    private Button crearBotonAgregarLineaVenta(GridPane formularioVenta, List<LineaVentaFormulario> lineaVentaList) {
        Button btnAgregarLinea = new Button("Agregar Linea");
        btnAgregarLinea.getStyleClass().add("button-guardar");
        btnAgregarLinea.setOnAction(e -> {
            if(cantLineas == cantLineasLlenas){
                agregarLineaVenta(formularioVenta, lineaVentaList);
            }
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

    private Button crearBotonGuardar(HBox cajaFecha, List<LineaVentaFormulario> lineaVentaList) {
        Button btnGuardar = new Button("Guardar");
        btnGuardar.getStyleClass().add("button-guardar");
        btnGuardar.setOnAction(e -> {
            if (!lineaVentaList.isEmpty()) {
                LineaVentaFormulario primera = lineaVentaList.get(0);
                if (primera != null &&
                        primera.getCantidadVendida().getText() != null &&
                        primera.getPrecioVenta().getText() != null &&
                        primera.getCodArt().getValue() != null) {
                    for (LineaVentaFormulario lv : lineaVentaList) {
                            registrarVentaArticulo(lv.getCantidadVendida(), lv.getPrecioVenta(), lv.getCodArt());
                    }
                    lineaVentaList.clear();
                    registrarVenta(cajaFecha);
                }
            }
        });
        return btnGuardar;
    }

    private Button crearBotonCancelar() {
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("button-cancelar");
        btnCancelar.setOnAction(e -> {
            cargarTablaVentas();
            botonNuevaVenta.setDisable(false);
            botonNuevaVenta.setVisible(true);
            Label header = (Label) this.lookup("#tituloHeader");
            header.setText("Historial Ventas");
        });
        return btnCancelar;
    }

    private GridPane crearFormulario(HBox cajaFecha) {
        GridPane formularioVenta = new GridPane();
        formularioVenta.getStyleClass().add("formulario");
        Label fechaTexto = new Label("Fecha:");
        Label horaTexto = new Label("Hora:");
        Label minutoTexto = new Label("Minuto:");
        Label subtotalTexto = new Label("Subtotal ($):");
        subtotalTexto.setId("subtotalTexto");
        formularioVenta.setVgap(10);
        formularioVenta.setHgap(10);

        HBox cajaFechaTexto = new HBox(0);
        cajaFechaTexto.getChildren().addAll(fechaTexto, horaTexto, minutoTexto);
        cajaFechaTexto.setAlignment(Pos.CENTER_LEFT);
        HBox.setMargin(fechaTexto, new Insets(5, 0, 0, 5));
        HBox.setMargin(horaTexto, new Insets(5, 0, 0, 75));
        HBox.setMargin(minutoTexto, new Insets(5, 0, 0, 40));


        formularioVenta.add(cajaFechaTexto, 0, 0);
        formularioVenta.add(subtotalTexto, 3, 0);
        formularioVenta.add(cajaFecha, 0, 1);

        GridPane.setMargin(fechaTexto, new Insets(5, 0, 0, 5));
        GridPane.setMargin(cajaFecha, new Insets(0, 0, 0, 5));
        GridPane.setMargin(subtotalTexto, new Insets(5, 5, 0, 0));
        GridPane.setHalignment(subtotalTexto, HPos.CENTER);
        GridPane.setHalignment(formularioVenta.lookup("#subtotalTexto"), HPos.LEFT);

        return formularioVenta;
    }

    private void agregarLineaVenta(GridPane formularioVenta, List<LineaVentaFormulario> lineaVentaList) {
        cantLineas2 = cantLineas2 + 1;
        cantLineas = cantLineas + 1;
        ultimaLineaLLena = false;
        Label cantVendida = new Label("Cantidad a Vender:");
        Label precioV = new Label("Precio de Venta:");
        Label codArticulo = new Label("Código de Articulo:");

        cantVendida.setMinWidth(80);
        cantVendida.setMaxWidth(120);
        precioV.setMinWidth(80);
        precioV.setMaxWidth(120);
        codArticulo.setMinWidth(80);
        codArticulo.setMaxWidth(120);

        TextField cantidadVendida = new TextField();
        TextField precioVenta = new TextField();
        ComboBox<String> codArt = new ComboBox<>();
        cargarArticulos(codArt);
        precioVenta.setEditable(false);

        cantidadVendida.setUserData(indiceCaja);
        precioVenta.setUserData(indiceCaja);
        codArt.setUserData(indiceCaja);

        indiceCaja = indiceCaja+1;

        cantidadVendida.setMinWidth(80);
        cantidadVendida.setMaxWidth(120);
        precioVenta.setMinWidth(80);
        precioVenta.setMaxWidth(120);
        codArt.setMinWidth(80);
        codArt.setMaxWidth(120);

        VBox lineaVentaBox = new VBox(10);
        HBox lineaLabel = new HBox(44);
        HBox lineaField = new HBox(15);
        lineaField.setId("lineaField");
        lineaLabel.getChildren().addAll(cantVendida, precioV, codArticulo);
        lineaField.getChildren().addAll(cantidadVendida, precioVenta, codArt);

        lineaVentaBox.getChildren().addAll(lineaLabel, lineaField);

        listaLineasVenta.add(lineaVentaBox);

        GridPane.setMargin(lineaVentaBox, new Insets(0, 0, 0, 5));

        if(cantLineas == 2) {
            formularioVenta.add(lineaVentaBox, 0, cantLineas2);

        } else {
            formularioVenta.add(lineaVentaBox, 0,cantLineas2);
        }

        GridPane.setHalignment(lineaVentaBox, HPos.LEFT);
        GridPane.setHgrow(lineaVentaBox, Priority.NEVER);
        GridPane.setVgrow(lineaVentaBox, Priority.NEVER);

        Integer rowIndex = GridPane.getRowIndex(lineaVentaBox);

        aplicarSubtotalLinea(cantidadVendida, precioVenta, codArt, lineaField, formularioVenta, rowIndex);

        ponerLineaHorizontal(formularioVenta);

        GridPane.setHalignment(formularioVenta.lookup("#totalTexto"), HPos.RIGHT);
        GridPane.setHalignment(formularioVenta.lookup("#total"), HPos.CENTER);

        LineaVentaFormulario lineaVenta = new LineaVentaFormulario();
        lineaVenta.setCodArt(codArt);
        lineaVenta.setPrecioVenta(precioVenta);
        lineaVenta.setCantidadVendida(cantidadVendida);
        lineaVentaList.add(lineaVenta);

        ponerLineaVertical(formularioVenta);
    }

    private void cargarArticulos (ComboBox<String> campoArt) {
        List<ArticuloDTO> articulos;
        maestroArticuloController = new MaestroArticuloController();
        articulos = maestroArticuloController.listarArticulosDisponibles();
        for (ArticuloDTO articulo : articulos) {
            campoArt.getItems().add(articulo.getNombreArticulo());
        }
    }

    private void ponerLineaVertical(GridPane formularioVenta) {
        Node nodoExistente = formularioVenta.lookup("#lineaVertical");
        if (nodoExistente == null) {
            Pane lineaVertical = new Pane();
            lineaVertical.setId("lineaVertical");
            lineaVertical.setPrefWidth(1);
            lineaVertical.setPrefHeight(Region.USE_COMPUTED_SIZE);
            lineaVertical.setStyle("-fx-background-color: black;");

            formularioVenta.add(lineaVertical, 2, 0);
            GridPane.setRowSpan(lineaVertical, cantLineas2+3);
            GridPane.setValignment(lineaVertical, VPos.CENTER);
            GridPane.setMargin(lineaVertical, new Insets(5, 0, 5, 0));
        } else {
            GridPane.setRowSpan(nodoExistente, cantLineas2+3);
        }
    }

    private void ponerLineaHorizontal(GridPane formularioVenta) {

        Node nodoExistente = formularioVenta.lookup("#lineaHorizontal");

        if (nodoExistente == null) {
            Pane lineaHorizontal = new Pane();
            lineaHorizontal.setId("lineaHorizontal");
            lineaHorizontal.setMinHeight(1);
            lineaHorizontal.setMaxHeight(1);
            lineaHorizontal.setPrefWidth(Region.USE_COMPUTED_SIZE);
            lineaHorizontal.setStyle("-fx-background-color: black;");
            formularioVenta.add(lineaHorizontal, 0, cantLineas2+1);
            GridPane.setColumnSpan(lineaHorizontal, 4);
            GridPane.setHalignment(lineaHorizontal, HPos.CENTER);
            GridPane.setMargin(lineaHorizontal, new Insets(0, 5, 0, 5));
            Label total = new Label("0");
            total.setId("total");

            Label totalTexto = new Label("Total ($):");
            totalTexto.setId("totalTexto");
            formularioVenta.add(totalTexto, 0, cantLineas2+2);
            formularioVenta.add(total, 3, cantLineas2+2);
            GridPane.setMargin(formularioVenta.lookup("#totalTexto"), new Insets(0, 0, 10, 0));
            GridPane.setMargin(formularioVenta.lookup("#total"), new Insets(0, 0, 10, 0));
        } else {
            formularioVenta.getChildren().remove(nodoExistente);
            formularioVenta.add(nodoExistente, 0, cantLineas2+1);
            GridPane.setColumnSpan(nodoExistente, 4);
            GridPane.setHalignment(nodoExistente, HPos.CENTER);
            GridPane.setValignment(nodoExistente, VPos.BOTTOM);
            GridPane.setRowIndex(formularioVenta.lookup("#totalTexto"), cantLineas2+2);
            GridPane.setRowIndex(formularioVenta.lookup("#total"), cantLineas2+2);
        }
    }

    private void aplicarSubtotalLinea(TextField cantidadVendida, TextField precioVenta, ComboBox<String> codArt, HBox lineaField, GridPane formularioVenta, int rowIndex) {
        Integer indice = (Integer) cantidadVendida.getUserData();

        Runnable accion = () -> {
            if (!cantidadVendida.getText().trim().isEmpty() &&
                    !precioVenta.getText().trim().isEmpty() &&
                    codArt.getValue() != null) {
                cantLineasLlenas = cantLineasLlenas+1;
                if(cantLineas == cantLineasLlenas) {
                    ultimaLineaLLena = true;
                }
                if(rowIndex > 1) {
                    quitarSubTotalLinea(formularioVenta, rowIndex);
                }
                TextField copiaCantidad = new TextField(cantidadVendida.getText());
                TextField copiaPrecio = new TextField(precioVenta.getText());
                TextField copiaCodArt = new TextField(codArt.getValue());
                HBox caja = new HBox(10);
                caja.getChildren().addAll(copiaCantidad, copiaPrecio, copiaCodArt);
                if(indice == cajaCalculos.size()){
                    cajaCalculos.add(caja);
                } else{
                    cajaCalculos.set(indice, caja);
                }
                mostrarSubTotalLinea(caja, formularioVenta, rowIndex);
                mostrarTotalLinea(formularioVenta);
            }
        };
        ChangeListener<String> listener = (obs, oldVal, newVal) -> accion.run();

        cantidadVendida.textProperty().addListener(listener);
        precioVenta.textProperty().addListener(listener);
        codArt.valueProperty().addListener(listener);

        cantidadVendida.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.isEmpty() && newValue.isEmpty()) {
                quitarSubTotalLinea(formularioVenta, rowIndex);
                cajaCalculos.remove(indice-0);
                mostrarTotalLinea(formularioVenta);
                cantLineasLlenas = cantLineasLlenas-1;
                ultimaLineaLLena = false;
            }
        });
        precioVenta.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.isEmpty() && newValue.isEmpty()) {
                quitarSubTotalLinea(formularioVenta, rowIndex);
                cajaCalculos.remove(indice-0);
                mostrarTotalLinea(formularioVenta);
                cantLineasLlenas = cantLineasLlenas-1;
                ultimaLineaLLena = false;
            }
        });
        codArt.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue == null) {
                quitarSubTotalLinea(formularioVenta, rowIndex);
                cajaCalculos.remove(indice-0);
                mostrarTotalLinea(formularioVenta);
                cantLineasLlenas = cantLineasLlenas-1;
                ultimaLineaLLena = false;
                precioVenta.clear();
            }
        });

        codArt.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null && newValue != null) {
                ArticuloDTO artDTO = maestroArticuloController.buscarArtPorNombre(newValue);
                BigDecimal precio = artDTO.getPrecioArticulo();
                precioVenta.setText(precio.toString());
            }
        });
    }

    private void mostrarSubTotalLinea(HBox caja, GridPane formularioVenta, int rowIndex) {
        TextField cantidadVendida = (TextField) caja.getChildren().get(0);
        TextField precioVenta = (TextField) caja.getChildren().get(1);
       /* ComboBox<String> codArt = null;
        Node nodo = caja.getChildren().get(2);
        if (nodo instanceof ComboBox) {
            ComboBox<?> combo = (ComboBox<?>) nodo;
            codArt = (ComboBox<String>) combo;
        }*/
        VentaArticuloDTO v = new VentaArticuloDTO();
        v.setCantidadVendida(Integer.parseInt(cantidadVendida.getText()));
        v.setPrecioVenta(new BigDecimal(precioVenta.getText()));

        BigDecimal subTotal = controller.calcularSubTotalVenta(v);
        DecimalFormat df = new DecimalFormat("0.00");
        Label subTotalLabel = new Label(df.format(subTotal));
        formularioVenta.add(subTotalLabel, 3, rowIndex);
        GridPane.setValignment(subTotalLabel, VPos.BOTTOM);
        GridPane.setHalignment(subTotalLabel, HPos.CENTER);
    }

    private void quitarSubTotalLinea(GridPane formularioVenta, int rowIndex) {
        int filaObjetivo = rowIndex;
        int columnaObjetivo = 3;
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
    }

    private void mostrarTotalLinea(GridPane formularioVenta) {
        Label campoTotal = (Label) formularioVenta.lookup("#total");
        List<VentaArticuloDTO> vList = new ArrayList<>();
        for(HBox h: cajaCalculos){
            TextField cantidadVendida = (TextField) h.getChildren().get(0);
            TextField precioVenta = (TextField) h.getChildren().get(1);
            VentaArticuloDTO v = new VentaArticuloDTO();
            v.setCantidadVendida(Integer.parseInt(cantidadVendida.getText()));
            v.setPrecioVenta(new BigDecimal(precioVenta.getText()));
            v.setSubTotalVenta(controller.calcularSubTotalVenta(v));
            vList.add(v);
        }
        BigDecimal nuevoValor = controller.calcularTotalVenta(vList);
        DecimalFormat df = new DecimalFormat("0.00");
        campoTotal.setText(df.format(nuevoValor));
    }

    private void quitarLineaVenta(GridPane formularioVenta, List<LineaVentaFormulario> lineaVentaList) {
        int fila = 1;
        if(listaLineasVenta.size() > 1) {
            VBox vBox = listaLineasVenta.getLast();
            fila = GridPane.getRowIndex(vBox);
            formularioVenta.getChildren().remove(vBox);
            lineaVentaList.removeLast();
            listaLineasVenta.removeLast();
            cantLineas2 = cantLineas2-1;
            filasRemovidas = filasRemovidas+1;
            GridPane.setRowIndex(formularioVenta.lookup("#lineaHorizontal"), cantLineas2+1);
            GridPane.setRowIndex(formularioVenta.lookup("#totalTexto"), cantLineas2+2);
            GridPane.setRowIndex(formularioVenta.lookup("#total"), cantLineas2+2);
            GridPane.setRowSpan(formularioVenta.lookup("#lineaVertical"), cantLineas2+3);
            cantLineas = cantLineas-1;
        }

        if(fila != 1) {
            quitarSubTotalLinea(formularioVenta, fila);
            if(cantLineas > cantLineasLlenas) {
                cajaCalculos.removeLast();
                mostrarTotalLinea(formularioVenta);
                cantLineasLlenas = cantLineasLlenas-1;
                indiceCaja = indiceCaja-1;
            }
            if(!ultimaLineaLLena) {
                indiceCaja = indiceCaja-1;
            }

            if(ultimaLineaLLena) {
                cajaCalculos.removeLast();
                mostrarTotalLinea(formularioVenta);
                cantLineasLlenas = cantLineasLlenas-1;
                ultimaLineaLLena = false;
                indiceCaja = indiceCaja-1;
            }

        }
        comprobarUltimaLineaLlena();
    }

    private void comprobarUltimaLineaLlena() {
        VBox vBox = listaLineasVenta.getLast();
        HBox lineaField = (HBox) vBox.getChildren().get(1);
        TextField cantVendida = (TextField) lineaField.getChildren().get(0);
        TextField precio = (TextField) lineaField.getChildren().get(1);
        TextField codArt = (TextField) lineaField.getChildren().get(2);
        if(!cantVendida.getText().isEmpty() && !precio.getText().isEmpty() && !codArt.getText().isEmpty()) {
            ultimaLineaLLena = true;
        }
    }

    private void registrarVenta(HBox cajaFecha) {
        DatePicker fecha = (DatePicker) cajaFecha.getChildren().get(0);
        ComboBox<String> hora = (ComboBox<String>) cajaFecha.getChildren().get(1);
        ComboBox<String> minuto = (ComboBox<String>) cajaFecha.getChildren().get(2);
        if (fecha == null &&
                hora.getValue() == null &&
                minuto.getValue() == null
        ) {
            mostrarAlerta(CAMPOS_VACIOS, 2, null);
        } else {
            try {
                LocalDate fechaStr = fecha.getValue();
                String horaStr = hora.getValue();
                String minutoStr = minuto.getValue();
                int horaInt = Integer.parseInt(horaStr);
                int minutoInt = Integer.parseInt(minutoStr);
                LocalDateTime fechaHora = fechaStr.atTime(horaInt, minutoInt);
                ventaDTO = null;
                ventaDTO = new VentaDTO();
                ventaDTO.setFechaHoraVenta(fechaHora);

                if (ventaArticuloDTOList.isEmpty()) {
                    throw new Exception("No hay artículos cargados para registrar la venta.");
                }

                for (VentaArticuloDTO ventaArticuloDTO : ventaArticuloDTOList) {
                    if (ventaArticuloDTO.getNombreArticulo() == null ||
                            ventaArticuloDTO.getCantidadVendida() == 0 ||
                            ventaArticuloDTO.getPrecioVenta() == null) {
                        throw new Exception("Uno de los artículos es inválido o tiene campos vacíos.");
                    }
                    ventaDTO.addVentaArticuloDTO(ventaArticuloDTO);
                }

                if (controller != null) {
                    System.out.println("si se registroo");
                    controller.AltaVenta(ventaDTO);
                    mostrarAlerta(VENTA_REGISTRADA, 4, () -> {
                        cajaFecha.getChildren().clear();
                        cargarTablaVentas();
                        Label header = (Label) this.lookup("#tituloHeader");
                        header.setText("Historial Ventas");
                    });
                    botonNuevaVenta.setDisable(false);
                    botonNuevaVenta.setVisible(true);
                    Label header = (Label) this.lookup("#tituloHeader");
                    header.setText("Historial Ventas");
                } else {
                    mostrarAlerta("Error: El controlador no está inicializado.", 2, null);
                }
            } catch (Exception ex) {
                mostrarAlerta(ERROR_REGISTRAR_VENTA + ex.getMessage(), 2, null);
                ex.printStackTrace();
                mostrarFormularioAlta();
            }
        }
    }

    private void registrarVentaArticulo(TextField cantidad, TextField precio, ComboBox<String> codArt) {
        if (cantidad.getText().isEmpty() || precio.getText().isEmpty() || codArt.getValue() == null) {
            mostrarAlerta(CAMPOS_VACIOS, 2, null);
        } else {

                ventaArticuloDTO = new VentaArticuloDTO();
                ventaArticuloDTO.setCantidadVendida(Integer.parseInt(cantidad.getText()));
                ventaArticuloDTO.setPrecioVenta(new BigDecimal(precio.getText()));
                ventaArticuloDTO.setNombreArticulo(codArt.getValue());
                ventaArticuloDTOList.add(ventaArticuloDTO);
            }
        }


    private void cargarTablaVentas() {
        contenido.getChildren().clear();

        tablaVentas = new TableView<>();
        tablaVentas.getStyleClass().add("tablaProveedor");
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

            {
                btnVerDetalle.setOnAction(e -> {
                    ventaDTO = getTableView().getItems().get(getIndex());
                    mostrarPopupDetalle(ventaDTO);
                });
                btnVerDetalle.setStyle(" -fx-max-width: 80px;\n" +
                        "    -fx-max-height: 15px;\n" +
                        "    -fx-background-color: #13c268;\n" +
                        "    -fx-text-fill: white;\n" +
                        "    -fx-font-weight: bold;\n" +
                        "    -fx-background-radius: 5px;\n" +
                        "    -fx-cursor: hand;\n" +
                        "    -fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.2), 4, 0.0, 0, 2);\n" +
                        "    -fx-transition: background-color 0.3s ease;");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
        return colVerDetalle;
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
            Label lblMensaje = new Label("Código Artículo: "+ventaArticuloDTO.getCodArticulo().toString());
            Label lb2Mensaje = new Label("Nombre Artículo: "+ventaArticuloDTO.getNombreArticulo().toString());
            Label lb3Mensaje = new Label("Órden de Linea: "+ventaArticuloDTO.getOrdenVentaArticulo().toString());
            Label lb4Mensaje = new Label("Cantidad: "+ventaArticuloDTO.getCantidadVendida());
            Label lb5Mensaje = new Label("Precio de Venta: $"+ventaArticuloDTO.getPrecioVenta().toString());
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
        btnAceptar.setStyle(" -fx-min-width: 90px;\n" +
                "    -fx-background-color: #3498db;\n" +
                "    -fx-text-fill: white;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-background-radius: 5px;\n" +
                "    -fx-padding: 10 20;\n" +
                "    -fx-cursor: hand;\n" +
                "    -fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.2), 4, 0.0, 0, 2);\n" +
                "    -fx-transition: background-color 0.3s ease;");
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

    public class LineaVentaFormulario {


        public void setCantidadVendida(TextField cantidadVendida) {
            this.cantidadVendida = cantidadVendida;
        }

        public void setPrecioVenta(TextField precioVenta) {
            this.precioVenta = precioVenta;
        }

        public void setCodArt(ComboBox<String> codArt) {
            this.codArt = codArt;
        }

        private TextField cantidadVendida;
        private TextField precioVenta;
        private ComboBox<String> codArt;

        public LineaVentaFormulario() {}

        public TextField getCantidadVendida() {
            return cantidadVendida;
        }

        public TextField getPrecioVenta() {
            return precioVenta;
        }

        public ComboBox<String> getCodArt() {
            return codArt;
        }
    }
}
