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
}