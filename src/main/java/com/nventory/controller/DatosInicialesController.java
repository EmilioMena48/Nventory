package com.nventory.controller;

import com.nventory.DTO.ProveedorDTO;
import com.nventory.model.Articulo;
import com.nventory.model.Proveedor;
import com.nventory.repository.ArticuloRepository;
import com.nventory.repository.ProveedorRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DatosInicialesController {
    ProveedorRepository proveedorRepository;
    ArticuloRepository articuloRepository;
    ProveedorController proveedorController;
    ArticuloController articuloController;

    public DatosInicialesController() {
        proveedorRepository = new ProveedorRepository();
        articuloRepository = new ArticuloRepository();
        proveedorController = new ProveedorController();
        articuloController = new ArticuloController();
    }

    public void ejecutar() {
        if (ExisteDatosIniciales()) {
            System.out.println("[-] Ya existen datos iniciales en la base de datos, no se cargaran nuevamente.");
        } else {
            init();
        }
    }

    private void init() {
        /* *
         * Cargar datos iniciales de proveedores
         * Utilizar los DTOs y Controllers para guardar los datos en la base de datos.
         */
        List<ProveedorDTO> proveedoresIniciale = cargarProveedoresIniciales();
        List<Proveedor> proveedores = new ArrayList<Proveedor>();
        for (ProveedorDTO proveedorDTO : proveedoresIniciale) {
            Proveedor proveedor = proveedorController.GuardarYRetornar(proveedorDTO);
            proveedores.add(proveedor);
        }

        //* Cargar datos iniciales de artículos

        //* Cargar artículos con proveedores

        //* Cargar configuración de inventario

        //* Cargar ordenes de compra con artículos y proveedores

        //* cargar venta con artículos y proveedores

        System.out.println("[+] Datos iniciales cargados correctamente.");
    }

    private boolean ExisteDatosIniciales() {
        /* *
         * verificar si ya existen datos iniciales en la base de datos o esta vacía.
         */
        Long cantidadProveedores = proveedorRepository.contarProveedores();
        Long cantidadArticulos = articuloRepository.contarArticulos();
        System.out.println("Cantidad de proveedores: " + cantidadProveedores);
        System.out.println("Cantidad de artículos: " + cantidadArticulos);
        return cantidadProveedores > 0 || cantidadArticulos > 0;
    }

    private List<ProveedorDTO> cargarProveedoresIniciales() {
        /* *
         * Cargar datos iniciales de proveedores, son ejemplos xd
         */
        return List.of(
                ProveedorDTO.builder()
                        .nombreProveedor("Pollos Hermanos S.A.")
                        .descripcionProveedor("Proveedor de pollos frescos y algo mas.")
                        .build()
                , ProveedorDTO.builder()
                        .nombreProveedor("Don Cables S.R.L.")
                        .descripcionProveedor("Líder en venta de cables que no sabías que necesitabas.")
                        .build()
                , ProveedorDTO.builder()
                        .nombreProveedor("Bits & Empanadas")
                        .descripcionProveedor("Venta de hardware y delivery de empanadas.")
                        .build()
        );
    }

    private List<Articulo> cargarArticulosIniciales() {
        /* *
         * Cargar datos iniciales de artículos, son ejemplos xd
         */
        return List.of(
                Articulo.builder()
                        .nombreArticulo("Pollo Entero")
                        .descripcionArticulo("Pollo fresco de alta calidad.")
                        .precioArticulo(BigDecimal.valueOf(12.99))
                        .stockActual(12)
                        .build()
                , Articulo.builder()
                        .nombreArticulo("Cable HDMI")
                        .descripcionArticulo("Cable HDMI de 2 metros, alta velocidad.")
                        .precioArticulo(BigDecimal.valueOf(5.49))
                        .stockActual(50)
                        .build()
                , Articulo.builder()
                        .nombreArticulo("Disco Duro Externo")
                        .descripcionArticulo("Disco duro externo de 1TB, USB 3.0.")
                        .precioArticulo(BigDecimal.valueOf(59.99))
                        .stockActual(20)
                        .build()
                , Articulo.builder()
                        .nombreArticulo("Empanada de Pollo")
                        .descripcionArticulo("Empanada de pollo casera, perfecta para el almuerzo.")
                        .precioArticulo(BigDecimal.valueOf(2.50))
                        .stockActual(100)
                        .build()
        );
    }
}