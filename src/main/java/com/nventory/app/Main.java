package com.nventory.app;
import com.nventory.controller.DatosInicialesController;
import com.nventory.service.DatosEstadosTiposScript;
import com.nventory.userInterfaces.MainViewPanel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        MainViewPanel root = new MainViewPanel();

        //Tamaño de la pantalla inicial
        Scene scene = new Scene(root, 854, 504);
        scene.getStylesheets().add(getClass().getResource("/styles/estilosMainView.css").toExternalForm());

        //Título de la pantalla
        primaryStage.setTitle("Menú Principal");

        //Mostrar pantalla
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {

        DatosEstadosTiposScript estadosIniciales = new DatosEstadosTiposScript();
        estadosIniciales.ejecutar();

        DatosInicialesController datosInicialesController = new DatosInicialesController();
        datosInicialesController.ejecutar();

        launch(args);
    }
}