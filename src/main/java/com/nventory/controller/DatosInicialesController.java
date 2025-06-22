package com.nventory.controller;

import com.nventory.DTO.ArticuloDTO;
import com.nventory.DTO.ArticuloProveedorGuardadoDTO;
import com.nventory.DTO.ProveedorDTO;
import com.nventory.model.Articulo;
import com.nventory.model.Proveedor;
import com.nventory.repository.ArticuloRepository;
import com.nventory.repository.ProveedorRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatosInicialesController {
    ProveedorRepository proveedorRepository;
    ArticuloRepository articuloRepository;
    ProveedorController proveedorController;
    MaestroArticuloController maestroArticuloController;

    public DatosInicialesController() {
        proveedorRepository = new ProveedorRepository();
        articuloRepository = new ArticuloRepository();
        proveedorController = new ProveedorController();
        maestroArticuloController = new MaestroArticuloController();
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
        for (ProveedorDTO proveedorDTO : proveedoresIniciale) {
            Proveedor proveedor = proveedorController.GuardarYRetornar(proveedorDTO);
        }

        //* Cargar datos iniciales de artículos
        List<ArticuloDTO> articulosIniciale = cargarArticulosIniciales();
        for (ArticuloDTO articuloDTO : articulosIniciale) {
            maestroArticuloController.darDeAltaArticulo(articuloDTO);
        }

        //* Cargar artículos con proveedores
        List<Articulo> articulos = articuloRepository.buscarTodos();
        List<Proveedor> proveedores = proveedorRepository.buscarTodos();
        List<ArticuloProveedorGuardadoDTO> articuloProveedorGuardadoDTOs = cargarArticuloProveedorInicial();
        proveedorController.AsociarArticuloProveedor(articulos.get(3),proveedores.get(0), articuloProveedorGuardadoDTOs.get(0),false);
        proveedorController.AsociarArticuloProveedor(articulos.get(3),proveedores.get(2), articuloProveedorGuardadoDTOs.get(1),true);
        proveedorController.AsociarArticuloProveedor(articulos.get(3),proveedores.get(1), articuloProveedorGuardadoDTOs.get(2),false);
        proveedorController.AsociarArticuloProveedor(articulos.get(4),proveedores.get(2), articuloProveedorGuardadoDTOs.get(3),true);
        proveedorController.AsociarArticuloProveedor(articulos.get(4),proveedores.get(0), articuloProveedorGuardadoDTOs.get(4),false);
        proveedorController.AsociarArticuloProveedor(articulos.get(2),proveedores.get(1), articuloProveedorGuardadoDTOs.get(5),true);
        proveedorController.AsociarArticuloProveedor(articulos.get(5),proveedores.get(3), articuloProveedorGuardadoDTOs.get(6),true);
        proveedorController.AsociarArticuloProveedor(articulos.get(5),proveedores.get(2), articuloProveedorGuardadoDTOs.get(7),false);
        proveedorController.AsociarArticuloProveedor(articulos.get(0),proveedores.get(0), articuloProveedorGuardadoDTOs.get(8),false);
        proveedorController.AsociarArticuloProveedor(articulos.get(0),proveedores.get(1), articuloProveedorGuardadoDTOs.get(9),true);
        proveedorController.AsociarArticuloProveedor(articulos.get(1),proveedores.get(2), articuloProveedorGuardadoDTOs.get(10),true);
        proveedorController.AsociarArticuloProveedor(articulos.get(1),proveedores.get(0), articuloProveedorGuardadoDTOs.get(11),false);

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
        return List.of(
                ProveedorDTO.builder()//0
                        .nombreProveedor("MSI Argentina")
                        .descripcionProveedor("Distribuidor oficial MSI: placas madre, gráficas y notebooks gamer.")
                        .build(),
                ProveedorDTO.builder()//1
                        .nombreProveedor("Gigabyte Argentina")
                        .descripcionProveedor("Proveedor de motherboards ys tarjetas de video Gigabyte/AORUS.")
                        .build(),
                ProveedorDTO.builder()//2
                        .nombreProveedor("Corsair Import")
                        .descripcionProveedor("Memorias RAM, SSD, fuentes y periféricos Corsair. Importación directa.")
                        .build(),
                ProveedorDTO.builder()//3
                        .nombreProveedor("Kingston Tech")
                        .descripcionProveedor("Especialistas en memorias RAM y almacenamiento SSD Kingston.")
                        .build()
        );
    }

    private List<ArticuloDTO> cargarArticulosIniciales() {
        return List.of(
                ArticuloDTO.builder()//0
                        .nombreArticulo("AMD Ryzen 9 9950X")
                        .descripcionArticulo("Procesador 16 C/32 T Zen 5 con boost hasta 5.7 GHz.")
                        .costoAlmacenamiento(new BigDecimal("400.00"))
                        .nivelServicioArticulo(new BigDecimal("0.90"))
                        .precioArticulo(new BigDecimal("699.00"))
                        .demandaArt(30)
                        .diasEntreRevisiones(60)
                        .stockActual(10)
                        .build(),

                ArticuloDTO.builder()//1
                        .nombreArticulo("Intel Core i7-14700K")
                        .descripcionArticulo("Procesador 20 C/28 T Raptor Lake con boost hasta 5.6 GHz.")
                        .costoAlmacenamiento(new BigDecimal("300.00"))
                        .nivelServicioArticulo(new BigDecimal("0.92"))
                        .precioArticulo(new BigDecimal("450.00"))
                        .demandaArt(45)
                        .diasEntreRevisiones(60)
                        .stockActual(15)
                        .build(),

                ArticuloDTO.builder()//2
                        .nombreArticulo("AMD Ryzen 7 9800X3D")
                        .descripcionArticulo("CPU 8 C/16 T Zen 5 con cache 3D.")
                        .costoAlmacenamiento(new BigDecimal("250.00"))
                        .nivelServicioArticulo(new BigDecimal("0.95"))
                        .precioArticulo(new BigDecimal("479.00"))
                        .demandaArt(60)
                        .diasEntreRevisiones(45)
                        .stockActual(20)
                        .build(),

                ArticuloDTO.builder()//3
                        .nombreArticulo("MSI RTX 5070 Ti 16GB")
                        .descripcionArticulo("Tarjeta gráfica Ada Lovelace, 16 GB, DLSS 3.")
                        .costoAlmacenamiento(new BigDecimal("600.00"))
                        .nivelServicioArticulo(new BigDecimal("0.88"))
                        .precioArticulo(new BigDecimal("799.00"))
                        .demandaArt(25)
                        .diasEntreRevisiones(60)
                        .stockActual(5)
                        .build(),

                ArticuloDTO.builder()//4
                        .nombreArticulo("Gigabyte Radeon RX 9060 XT")
                        .descripcionArticulo("GPU 1080/1440p, 8 GB VRAM, triple fan.")
                        .costoAlmacenamiento(new BigDecimal("300.00"))
                        .nivelServicioArticulo(new BigDecimal("0.90"))
                        .precioArticulo(new BigDecimal("389.00"))
                        .demandaArt(40)
                        .diasEntreRevisiones(45)
                        .stockActual(10)
                        .build(),

                ArticuloDTO.builder()//5
                        .nombreArticulo("Corsair Vengeance DDR5 32GB (2x16GB) 6000MHz")
                        .descripcionArticulo("Kit de memoria RAM DDR5 de alta velocidad.")
                        .costoAlmacenamiento(new BigDecimal("120.00"))
                        .nivelServicioArticulo(new BigDecimal("0.94"))
                        .precioArticulo(new BigDecimal("179.00"))
                        .demandaArt(70)
                        .diasEntreRevisiones(30)
                        .stockActual(25)
                        .build()
        );
    }

    private List<ArticuloProveedorGuardadoDTO> cargarArticuloProveedorInicial() {
        return List.of(
                ArticuloProveedorGuardadoDTO.builder()//0
                        .costoPedido(new BigDecimal("650.00"))
                        .precioUnitario(new BigDecimal("780.00"))
                        .demoraEntregaDias(7)
                        .fechaProxRevisionAP(LocalDate.of(2025, 6, 23))
                        .build(),

                ArticuloProveedorGuardadoDTO.builder()//1
                        .costoPedido(new BigDecimal("640.00"))
                        .precioUnitario(new BigDecimal("770.00"))
                        .demoraEntregaDias(14)
                        .fechaProxRevisionAP(null)
                        .build(),

                ArticuloProveedorGuardadoDTO.builder()//2
                        .costoPedido(new BigDecimal("645.00"))
                        .precioUnitario(new BigDecimal("775.00"))
                        .demoraEntregaDias(10)
                        .fechaProxRevisionAP(LocalDate.of(2025, 6, 27))
                        .build(),

                ArticuloProveedorGuardadoDTO.builder()//3
                        .costoPedido(new BigDecimal("280.00"))
                        .precioUnitario(new BigDecimal("365.00"))
                        .demoraEntregaDias(5)
                        .fechaProxRevisionAP(null)
                        .build(),

                ArticuloProveedorGuardadoDTO.builder()//4
                        .costoPedido(new BigDecimal("290.00"))
                        .precioUnitario(new BigDecimal("375.00"))
                        .demoraEntregaDias(12)
                        .fechaProxRevisionAP(LocalDate.of(2025, 7, 2))
                        .build(),

                ArticuloProveedorGuardadoDTO.builder()//5
                        .costoPedido(new BigDecimal("345.00"))
                        .precioUnitario(new BigDecimal("459.00"))
                        .demoraEntregaDias(10)
                        .fechaProxRevisionAP(null)
                        .build(),

                ArticuloProveedorGuardadoDTO.builder()//6
                        .costoPedido(new BigDecimal("110.00"))
                        .precioUnitario(new BigDecimal("145.00"))
                        .demoraEntregaDias(10)
                        .fechaProxRevisionAP(null)
                        .build(),

                ArticuloProveedorGuardadoDTO.builder()//7
                        .costoPedido(new BigDecimal("115.00"))
                        .precioUnitario(new BigDecimal("150.00"))
                        .demoraEntregaDias(8)
                        .fechaProxRevisionAP(LocalDate.of(2025, 7, 2))
                        .build(),

                ArticuloProveedorGuardadoDTO.builder()//8
                        .costoPedido(new BigDecimal("370.00"))
                        .precioUnitario(new BigDecimal("685.00"))
                        .demoraEntregaDias(14)
                        .fechaProxRevisionAP(LocalDate.of(2025, 7, 7))
                        .build(),

                ArticuloProveedorGuardadoDTO.builder()//9
                        .costoPedido(new BigDecimal("375.00"))
                        .precioUnitario(new BigDecimal("690.00"))
                        .demoraEntregaDias(14)
                        .fechaProxRevisionAP(null)
                        .build(),

                ArticuloProveedorGuardadoDTO.builder()//10
                        .costoPedido(new BigDecimal("290.00"))
                        .precioUnitario(new BigDecimal("455.00"))
                        .demoraEntregaDias(12)
                        .fechaProxRevisionAP(null)
                        .build(),

                ArticuloProveedorGuardadoDTO.builder()//11
                        .costoPedido(new BigDecimal("295.00"))
                        .precioUnitario(new BigDecimal("460.00"))
                        .demoraEntregaDias(10)
                        .fechaProxRevisionAP(LocalDate.of(2025, 6, 25))
                        .build()
        );
    }
}