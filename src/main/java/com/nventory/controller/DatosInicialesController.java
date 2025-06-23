package com.nventory.controller;

import com.nventory.DTO.ArticuloDTO;
import com.nventory.DTO.ArticuloProveedorGuardadoDTO;
import com.nventory.DTO.ProveedorDTO;
import com.nventory.model.*;
import com.nventory.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class DatosInicialesController {
    ProveedorRepository proveedorRepository;
    ArticuloRepository articuloRepository;
    ProveedorController proveedorController;
    MaestroArticuloController maestroArticuloController;
    ArticuloProveedorRepository articuloProveedorRepository;
    OrdenDeCompraController ordenDeCompraController;

    public DatosInicialesController() {
        proveedorRepository = new ProveedorRepository();
        articuloRepository = new ArticuloRepository();
        proveedorController = new ProveedorController();
        maestroArticuloController = new MaestroArticuloController();
        articuloProveedorRepository = new ArticuloProveedorRepository();
        ordenDeCompraController = new OrdenDeCompraController();
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

        //* Cargar proveedores predeterminados en artículos
        List<ArticuloProveedor> articuloProveedores = articuloProveedorRepository.buscarTodos();
        maestroArticuloController.asignarProveedorPredeterminado(articuloProveedores.get(11).getCodArticuloProveedor());
        maestroArticuloController.asignarProveedorPredeterminado(articuloProveedores.get(9).getCodArticuloProveedor());
        maestroArticuloController.asignarProveedorPredeterminado(articuloProveedores.get(5).getCodArticuloProveedor());
        maestroArticuloController.asignarProveedorPredeterminado(articuloProveedores.get(1).getCodArticuloProveedor());
        maestroArticuloController.asignarProveedorPredeterminado(articuloProveedores.get(4).getCodArticuloProveedor());
        maestroArticuloController.asignarProveedorPredeterminado(articuloProveedores.get(6).getCodArticuloProveedor());

        //* Cargar ordenes de compra con artículos y proveedores
        Set<Long> codigosProveedoresConOrden = new HashSet<>();
        codigosProveedoresConOrden.add(proveedores.get(0).getCodProveedor()); // MSI
        codigosProveedoresConOrden.add(proveedores.get(1).getCodProveedor()); // Gigabyte
        codigosProveedoresConOrden.add(proveedores.get(2).getCodProveedor()); // Corsair

        // Mapeo simple de proveedor → idArticuloProveedor (según asociaciones anteriores)
        Map<Long, Long> proveedorToArticuloProveedorId = new HashMap<>();
        proveedorToArticuloProveedorId.put(proveedores.get(0).getCodProveedor(), 1L);  // MSI
        proveedorToArticuloProveedorId.put(proveedores.get(1).getCodProveedor(), 3L);  // Gigabyte
        proveedorToArticuloProveedorId.put(proveedores.get(2).getCodProveedor(), 4L);  // Corsair

        for (Long codProveedor : codigosProveedoresConOrden) {
            Long codOrden = ordenDeCompraController.crearOrdenDeCompra(codProveedor);
            System.out.println("Orden de compra creada para proveedor " + codProveedor + " con código de orden: " + codOrden);

            // Agregar artículo asociado a esa orden
            Long idArticuloProveedor = proveedorToArticuloProveedorId.get(codProveedor);
            int cantidad = 10;
            ordenDeCompraController.agregarArticuloAOrden(codOrden, idArticuloProveedor, cantidad);
            System.out.println("Artículo-Proveedor con ID " + idArticuloProveedor + " agregado a la orden con cantidad " + cantidad);
        }


        //* cargar venta con artículos y proveedores
        realizarVentaDeEjemplo();

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
                        .nombreProveedor("MSI")
                        .descripcionProveedor("Distribuidor oficial MSI: placas madre, gráficas y notebooks gamer.")
                        .build(),
                ProveedorDTO.builder()//1
                        .nombreProveedor("Gigabyte")
                        .descripcionProveedor("Proveedor de motherboards ys tarjetas de video Gigabyte/AORUS.")
                        .build(),
                ProveedorDTO.builder()//2
                        .nombreProveedor("Corsair")
                        .descripcionProveedor("Memorias RAM, SSD, fuentes y periféricos Corsair. Importación directa.")
                        .build(),
                ProveedorDTO.builder()//3
                        .nombreProveedor("Kingston")
                        .descripcionProveedor("Especialistas en memorias RAM y almacenamiento SSD Kingston.")
                        .build()
        );
    }

    private List<ArticuloDTO> cargarArticulosIniciales() {
        return List.of(
                ArticuloDTO.builder()//0
                        .nombreArticulo("CPU AMD")
                        .descripcionArticulo("Procesador 16 C/32 T Zen 5 con boost hasta 5.7 GHz.")
                        .costoAlmacenamiento(new BigDecimal("400.00"))
                        .nivelServicioArticulo(new BigDecimal("0.90"))
                        .precioArticulo(new BigDecimal("699.00"))
                        .demandaArt(30)
                        .diasEntreRevisiones(60)
                        .stockActual(20)
                        .build(),

                ArticuloDTO.builder()//1
                        .nombreArticulo("CPU Intel")
                        .descripcionArticulo("Procesador 20 C/28 T Raptor Lake con boost hasta 5.6 GHz.")
                        .costoAlmacenamiento(new BigDecimal("300.00"))
                        .nivelServicioArticulo(new BigDecimal("0.92"))
                        .precioArticulo(new BigDecimal("450.00"))
                        .demandaArt(45)
                        .diasEntreRevisiones(60)
                        .stockActual(25)
                        .build(),

                ArticuloDTO.builder()//2
                        .nombreArticulo("Disco Duro NVMe")
                        .descripcionArticulo("SSD NVMe M.2 1TB, velocidad de lectura 3500 MB/s.")
                        .costoAlmacenamiento(new BigDecimal("250.00"))
                        .nivelServicioArticulo(new BigDecimal("0.95"))
                        .precioArticulo(new BigDecimal("479.00"))
                        .demandaArt(60)
                        .diasEntreRevisiones(45)
                        .stockActual(20)
                        .build(),

                ArticuloDTO.builder()//3
                        .nombreArticulo("GPU RTX")
                        .descripcionArticulo("Tarjeta gráfica Ada Lovelace, 16 GB, DLSS 3.")
                        .costoAlmacenamiento(new BigDecimal("600.00"))
                        .nivelServicioArticulo(new BigDecimal("0.88"))
                        .precioArticulo(new BigDecimal("799.00"))
                        .demandaArt(25)
                        .diasEntreRevisiones(60)
                        .stockActual(25)
                        .build(),

                ArticuloDTO.builder()//4
                        .nombreArticulo("GPU RADEON")
                        .descripcionArticulo("GPU 1080/1440p, 8 GB VRAM, triple fan.")
                        .costoAlmacenamiento(new BigDecimal("300.00"))
                        .nivelServicioArticulo(new BigDecimal("0.90"))
                        .precioArticulo(new BigDecimal("389.00"))
                        .demandaArt(40)
                        .diasEntreRevisiones(45)
                        .stockActual(20)
                        .build(),

                ArticuloDTO.builder()//5
                        .nombreArticulo("Memoria RAM")
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

    public void realizarVentaDeEjemplo() {
        // Repositorios
        ArticuloRepository articuloRepository = new ArticuloRepository();
        TipoStockMovimientoRepository tipoStockMovimientoRepository = new TipoStockMovimientoRepository();
        VentaRepository ventaRepository = new VentaRepository();
        StockMovimientoRepository stockMovimientoRepository = new StockMovimientoRepository();

        // Tipo de movimiento (salida)
        TipoStockMovimiento salida = tipoStockMovimientoRepository.buscarPorId(2L);

        // === VENTA 1 ===
        Articulo articulo1 = articuloRepository.buscarPorId(1L); // AMD Ryzen 9 9950X
        Articulo articulo2 = articuloRepository.buscarPorId(2L); // Intel Core i7-14700K

        Venta venta1 = Venta.builder()
                .fechaHoraVenta(LocalDateTime.now())
                .montoTotalVenta(BigDecimal.ZERO)
                .ventaArticulo(new ArrayList<>())
                .build();

        VentaArticulo va1_1 = VentaArticulo.builder()
                .articulo(articulo1)
                .cantidadVendida(2)
                .precioVenta(articulo1.getPrecioArticulo())
                .subTotalVenta(articulo1.getPrecioArticulo().multiply(BigDecimal.valueOf(2)))
                .build();

        VentaArticulo va1_2 = VentaArticulo.builder()
                .articulo(articulo2)
                .cantidadVendida(1)
                .precioVenta(articulo2.getPrecioArticulo())
                .subTotalVenta(articulo2.getPrecioArticulo())
                .build();

        venta1.addVentaArticulo(va1_1);
        venta1.addVentaArticulo(va1_2);
        venta1.setMontoTotalVenta(va1_1.getSubTotalVenta().add(va1_2.getSubTotalVenta()));
        ventaRepository.guardar(venta1);

        Venta venta1Persistida = ventaRepository.buscarVentaConArticulos(1L);
        crearMovimientosYActualizarStock(venta1Persistida, stockMovimientoRepository, articuloRepository, salida);

        // === VENTA 2 ===
        Articulo articulo3 = articuloRepository.buscarPorId(3L); // AMD Ryzen 7 9800X3D
        Articulo articulo4 = articuloRepository.buscarPorId(4L); // MSI RTX 5070 Ti 16GB

        Venta venta2 = Venta.builder()
                .fechaHoraVenta(LocalDateTime.now())
                .montoTotalVenta(BigDecimal.ZERO)
                .ventaArticulo(new ArrayList<>())
                .build();

        VentaArticulo va2_1 = VentaArticulo.builder()
                .articulo(articulo3)
                .cantidadVendida(3)
                .precioVenta(articulo3.getPrecioArticulo())
                .subTotalVenta(articulo3.getPrecioArticulo().multiply(BigDecimal.valueOf(3)))
                .build();

        VentaArticulo va2_2 = VentaArticulo.builder()
                .articulo(articulo4)
                .cantidadVendida(2)
                .precioVenta(articulo4.getPrecioArticulo())
                .subTotalVenta(articulo4.getPrecioArticulo().multiply(BigDecimal.valueOf(2)))
                .build();

        venta2.addVentaArticulo(va2_1);
        venta2.addVentaArticulo(va2_2);
        venta2.setMontoTotalVenta(va2_1.getSubTotalVenta().add(va2_2.getSubTotalVenta()));
        ventaRepository.guardar(venta2);

        Venta venta2Persistida = ventaRepository.buscarVentaConArticulos(2L);
        crearMovimientosYActualizarStock(venta2Persistida, stockMovimientoRepository, articuloRepository, salida);

        // === VENTA 3 ===
        Articulo articulo5 = articuloRepository.buscarPorId(5L); // Gigabyte Radeon RX 9060 XT
        Articulo articulo6 = articuloRepository.buscarPorId(6L); // Corsair Vengeance DDR5

        Venta venta3 = Venta.builder()
                .fechaHoraVenta(LocalDateTime.now())
                .montoTotalVenta(BigDecimal.ZERO)
                .ventaArticulo(new ArrayList<>())
                .build();

        VentaArticulo va3_1 = VentaArticulo.builder()
                .articulo(articulo5)
                .cantidadVendida(1)
                .precioVenta(articulo5.getPrecioArticulo())
                .subTotalVenta(articulo5.getPrecioArticulo())
                .build();

        VentaArticulo va3_2 = VentaArticulo.builder()
                .articulo(articulo6)
                .cantidadVendida(4)
                .precioVenta(articulo6.getPrecioArticulo())
                .subTotalVenta(articulo6.getPrecioArticulo().multiply(BigDecimal.valueOf(4)))
                .build();

        venta3.addVentaArticulo(va3_1);
        venta3.addVentaArticulo(va3_2);
        venta3.setMontoTotalVenta(va3_1.getSubTotalVenta().add(va3_2.getSubTotalVenta()));
        ventaRepository.guardar(venta3);

        Venta venta3Persistida = ventaRepository.buscarVentaConArticulos(3L);
        crearMovimientosYActualizarStock(venta3Persistida, stockMovimientoRepository, articuloRepository, salida);

        System.out.println("[+] Ventas creadas exitosamente.");
    }

    private void crearMovimientosYActualizarStock(Venta venta, StockMovimientoRepository stockRepo, ArticuloRepository articuloRepo, TipoStockMovimiento salida) {
        for (VentaArticulo va : venta.getVentaArticulo()) {
            StockMovimiento sm = StockMovimiento.builder()
                    .articulo(va.getArticulo())
                    .cantidad(va.getCantidadVendida())
                    .comentario("Venta de artículo")
                    .fechaHoraMovimiento(LocalDateTime.now())
                    .tipoStockMovimiento(salida)
                    .ordenDeCompraArticulo(null)
                    .ventaArticulo(va)
                    .build();

            stockRepo.guardar(sm);

            Articulo articulo = va.getArticulo();
            articulo.setStockActual(articulo.getStockActual() - va.getCantidadVendida());
            articuloRepo.guardar(articulo);
        }
    }





}