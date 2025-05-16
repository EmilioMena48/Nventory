package com.nventory.userInterfaces;

import com.nventory.pruebasEmilio.DetallePrueba;
import com.nventory.pruebasEmilio.VentaPrueba;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class VentaPanel extends BorderPane {

    /*Label l1 = new Label("Historial de Ventas");
    VBox cajaHistorial = new VBox();
    TableView<VentaPrueba> tabla = new TableView<>();

    TableColumn<VentaPrueba, Integer> colNumero = new TableColumn<>("Numero");
    TableColumn<VentaPrueba, Integer> colMontoTotal = new TableColumn<>("MontoTotal");
    TableColumn<VentaPrueba, Date> colFecha = new TableColumn<>("Fecha");
    TableColumn<VentaPrueba, DetallePrueba> colDetalle = new TableColumn<>("Fecha");

    private final Button verDetalle = new Button("Ver Detalle");

    HBox tablaCaja = new HBox(10);
    //Ejemplo
    LocalDate localDate = LocalDate.of(2025, 5, 15);
    Date fecha = java.sql.Date.valueOf(localDate);

    Long id = 1L;
    BigDecimal monto = BigDecimal.valueOf(123L);

    ObservableList<VentaPrueba> ventas = FXCollections.observableArrayList(
            new VentaPrueba(id, monto, fecha, 1L, 2, BigDecimal.valueOf(10L), BigDecimal.valueOf(20L))
           // new VentaPrueba(id, monto, fecha),
            //new VentaPrueba(id, monto, fecha)
    );

    private final Button botonAgregar = new Button("Nueva Venta");
*/
    public VentaPanel() {
/*
        colNumero.setCellValueFactory(new PropertyValueFactory<>("NumeroVenta"));
        colMontoTotal.setCellValueFactory(new PropertyValueFactory<>("montoTotal"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaHoraVenta"));
        colDetalle.setCellValueFactory(new PropertyValueFactory<>("DetallePrueba"));

        l1.getStyleClass().add("l1estilo");
        cajaHistorial.getChildren().add(l1);
        cajaHistorial.getStyleClass().add("cajaHistorialEstilo");
        this.setMargin(cajaHistorial, new Insets(10, 10, 10, 10));
        tabla.getColumns().addAll(colNumero, colMontoTotal, colFecha, colDetalle);
        tabla.setItems(ventas);
        //tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //tabla.getStyleClass().add("table-row-cell");
        //tabla.setFixedCellSize(25);
        //tabla.prefHeightProperty().bind(tabla.fixedCellSizeProperty().multiply(ventas.size() + 1.00) // +1 para el encabezado
        //);
        Region separador = new Region();
        HBox.setHgrow(separador, Priority.ALWAYS);
        tablaCaja.getChildren().addAll(tabla, separador, botonAgregar);
        setTop(cajaHistorial);
        setCenter(tablaCaja);
        this.getStylesheets().add(getClass().getResource("/styles/estilosVenta.css").toExternalForm());
*/
    }
}
