package com.nventory.userInterfaces;

import com.nventory.controller.MaestroArticuloController;
import com.nventory.controller.OrdenDeCompraController;
import com.nventory.controller.ProveedorController;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainViewPanel  extends BorderPane {

    //Elementos Header
    Label titulo = new Label("Nventory");
    Label subtitulo = new Label("Prototipo");

    Image imagenTienda = new Image(getClass().getResource("/images/icono-principal.png").toExternalForm());
    ImageView imagenTiendaView = new ImageView(imagenTienda);

    HBox cajaHeader = new HBox(10);
    VBox textoHeader = new VBox(10);

    //Elementos Menú
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

    VBox cajaArticulo = new VBox(5);
    VBox cajaProveedor = new VBox(5);
    VBox cajaOrdenCompra = new VBox(5);
    VBox cajaVenta = new VBox(5);
    HBox cajaMenu = new HBox(10);

    //Elementos body
    VBox contenidoBody = new VBox();

    Image imagenVolver = new Image(getClass().getResource("/images/icono-volver.png").toExternalForm());
    ImageView imagenVolverView = new ImageView(imagenVolver);

    Label textoVolver = new Label("Volver");

    HBox cajaVolver = new HBox(2);

    private final Button botonVolver= new Button();

    public MainViewPanel() {

        //----------Header-------------------------------------------------------------
        textoHeader.getChildren().addAll(titulo, subtitulo);
        cajaHeader.getChildren().addAll(imagenTiendaView, textoHeader);

        //----------Estilos Header-----------------------------------------------------------------------
        titulo.getStyleClass().add("tituloHeader");
        subtitulo.getStyleClass().add("subtituloHeader");
        textoHeader.getStyleClass().add("textoHeaderEstilo");
        cajaHeader.getStyleClass().add("sombreadoHeader");
        cajaHeader.getStyleClass().add("cajaHeaderEstilo");

        //----------Menú-----------------------------------------------------------------------
        cajaArticulo.getChildren().addAll(imagenArticuloView, textoArticulo);
        cajaProveedor.getChildren().addAll(imagenProveedorView, textoProveedor);
        cajaOrdenCompra.getChildren().addAll(imagenOrdenCompraView, textoOrdenCompra);
        cajaVenta.getChildren().addAll(imagenVentaView, textoVenta);
        botonArticulo.setGraphic(cajaArticulo);
        botonProveedor.setGraphic(cajaProveedor);
        botonOrdenCompra.setGraphic(cajaOrdenCompra);
        botonVentas.setGraphic(cajaVenta);
        cajaMenu.getChildren().addAll(botonArticulo, botonProveedor, botonOrdenCompra, botonVentas);

        //----------Estilos Menú-----------------------------------------------------------------------
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

        cajaMenu.getStyleClass().add("cajaMenu");

        //----------Poner funcionalidad de los botones del menu-----------------------------------------------------------------------
        botonArticulo.setOnAction(e -> {
            mostrarArticulos();
        });
        botonProveedor.setOnAction(e -> {
            mostrarProveedores();
        });
        botonOrdenCompra.setOnAction(e -> {
            mostrarOrdenCompra();
        });
        botonVentas.setOnAction(e -> {
            mostrarVentas();
        });

        //----------Botón volver-------------------------------------------------------------
        textoVolver.getStyleClass().add("textoVolverEstilo");
        cajaVolver.getStyleClass().add("cajaVolverEstilo");
        imagenVolverView.setFitWidth(20);
        imagenVolverView.setFitHeight(20);
        cajaVolver.getChildren().addAll(imagenVolverView, textoVolver);
        botonVolver.setGraphic(cajaVolver);
        botonVolver.getStyleClass().add("botonVolverEstilo");

        botonVolver.setOnAction(e -> {
            mostrarMenu();
        });

        //----------Header y body-------------------------------------------------------------
        contenidoBody.setStyle("-fx-padding: 10px 0 10px 0;");

        setTop(cajaHeader);
        setCenter(contenidoBody);

        //----------Se muestra el menú en el body por defecto-------------------------------------------------------------
        mostrarMenu();
    }

    private void mostrarMenu() {
        contenidoBody.getChildren().setAll(cajaMenu);
    }

    private void mostrarArticulos(){
        MaestroArticuloController maestroArticuloController = new MaestroArticuloController();
        contenidoBody.getChildren().setAll(botonVolver, new MaestroArticuloPanel(maestroArticuloController));
        botonVolver.setAlignment(Pos.TOP_LEFT);
    }
    private void mostrarVentas() {
        contenidoBody.getChildren().setAll(botonVolver, new VentaPanel());
        botonVolver.setAlignment(Pos.TOP_LEFT);
    }

    private void mostrarProveedores() {
        ProveedorController proveedorController = new ProveedorController();
        contenidoBody.getChildren().setAll(botonVolver, new ProveedorPanel(proveedorController));
        botonVolver.setAlignment(Pos.TOP_LEFT);
    }

    private void mostrarOrdenCompra() {
        OrdenDeCompraController controller = new OrdenDeCompraController();
        contenidoBody.getChildren().setAll(botonVolver, new OrdenCompraPanel(controller));
        botonVolver.setAlignment(Pos.TOP_LEFT);
    }

    //----------Método para probar los botones-----------------------------------------------------------------------
    private void mostrarAlerta (String mensaje){
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Advertencia");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }


}
