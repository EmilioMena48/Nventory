package com.nventory.userInterfaces;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainViewPanel  extends VBox {

    //Controles Header
    Label titulo = new Label("Nventory");
    Label subtitulo = new Label("Prototipo");

    Image imagenTienda = new Image(getClass().getResource("/images/icono-principal.png").toExternalForm());
    ImageView imagenTiendaView = new ImageView(imagenTienda);

    //Controles Body
    Label textoArticulo = new Label("Maestro de Artículos");
    Label textoProveedor = new Label("Proveedores");
    Label textoOrdenCompra = new Label("Orden de Compra");
    Label textoVenta = new Label("Ventas");

    Image imagenArticulo = new Image(getClass().getResource("/images/icono-articulo.png").toExternalForm());
    ImageView imagenArticuloView = new ImageView(imagenArticulo);

    Image imagenProveedor = new Image(getClass().getResource("/images/icono-proveedor.png").toExternalForm());
    ImageView imagenProveedorView = new ImageView(imagenProveedor);

    Image imagenOrdenCompra = new Image(getClass().getResource("/images/icono-ordenCompra.png").toExternalForm());
    ImageView imagenOrdenCompraView = new ImageView(imagenOrdenCompra);

    Image imagenVenta = new Image(getClass().getResource("/images/icono-venta.png").toExternalForm());
    ImageView imagenVentaView = new ImageView(imagenVenta);

    private final Button botonArticulo = new Button();
    private final Button botonProveedor = new Button();
    private final Button botonOrdenCompra= new Button();
    private final Button botonVentas = new Button();


    public MainViewPanel() {

        //----------Espaciado y relleno de la pantalla-----------------------------------------------------------------------
        setSpacing(20);
        setPadding(new Insets(20));


        //----------Header-----------------------------------------------------------------------
        HBox cajaHeader = new HBox(10);

        VBox textoHeader = new VBox(10);
        textoHeader.getChildren().addAll(titulo, subtitulo);

        cajaHeader.getChildren().addAll(imagenTiendaView, textoHeader);


        //----------Estilos Header-----------------------------------------------------------------------
        titulo.getStyleClass().add("tituloHeader");
        subtitulo.getStyleClass().add("subtituloHeader");
        textoHeader.getStyleClass().add("textoHeaderEstilo");
        cajaHeader.getStyleClass().add("sombreadoHeader");
        cajaHeader.getStyleClass().add("cajaHeaderEstilo");


        //----------Body-----------------------------------------------------------------------
        VBox cajaArticulo = new VBox(5);
        cajaArticulo.getChildren().addAll(imagenArticuloView, textoArticulo);

        VBox cajaProveedor = new VBox(5);
        cajaProveedor.getChildren().addAll(imagenProveedorView, textoProveedor);

        VBox cajaOrdenCompra = new VBox(5);
        cajaOrdenCompra.getChildren().addAll(imagenOrdenCompraView, textoOrdenCompra);

        VBox cajaVenta = new VBox(5);
        cajaVenta.getChildren().addAll(imagenVentaView, textoVenta);

        botonArticulo.setGraphic(cajaArticulo);
        botonProveedor.setGraphic(cajaProveedor);
        botonOrdenCompra.setGraphic(cajaOrdenCompra);
        botonVentas.setGraphic(cajaVenta);

        HBox cajaBody = new HBox(10);
        cajaBody.getChildren().addAll(botonArticulo, botonProveedor, botonOrdenCompra, botonVentas);



        //----------Estilos Body-----------------------------------------------------------------------
        textoArticulo.getStyleClass().add("textoModulo");
        textoProveedor.getStyleClass().add("textoModulo");
        textoOrdenCompra.getStyleClass().add("textoModulo");
        textoVenta.getStyleClass().add("textoModulo");

        textoArticulo.setWrapText(true); // Para hacer salto de línea
        textoProveedor.setWrapText(true);
        textoOrdenCompra.setWrapText(true);
        textoVenta.setWrapText(true);

        botonArticulo.getStyleClass().add("botonModulo");
        botonProveedor.getStyleClass().add("botonModulo");
        botonOrdenCompra.getStyleClass().add("botonModulo");
        botonVentas.getStyleClass().add("botonModulo");

        cajaArticulo.getStyleClass().add("cajaModulo");
        cajaProveedor.getStyleClass().add("cajaModulo");
        cajaOrdenCompra.getStyleClass().add("cajaModulo");
        cajaVenta.getStyleClass().add("cajaModulo");

        cajaBody.getStyleClass().add("cajaPrincipal");

        //----------Poner funcionalidad de los botones-----------------------------------------------------------------------
        botonArticulo.setOnAction(e -> {
            mostrarAlerta("hola");
        });
        botonProveedor.setOnAction(e -> {
            mostrarAlerta("hola");
        });
        botonOrdenCompra.setOnAction(e -> {
            mostrarAlerta("hola");
        });
        botonVentas.setOnAction(e -> {
            mostrarAlerta("hola");
        });


        //----------Integrar las dos cajas a la pantalla-----------------------------------------------------------------------
        getChildren().addAll(cajaHeader, cajaBody);
    }



    //----------Método para probar los botones-----------------------------------------------------------------------
    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Advertencia");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

}
