package com.nventory.service;

import com.nventory.model.Articulo;
import com.nventory.model.EstadoOrdenDeCompra;
import com.nventory.model.TipoModeloInventario;
import com.nventory.model.TipoStockMovimiento;
import com.nventory.repository.ArticuloRepository;
import com.nventory.repository.EstadoOrdenDeCompraRepository;
import com.nventory.repository.TipoModeloInventarioRepository;
import com.nventory.repository.TipoStockMovimientoRepository;

public class DatosEstadosTiposScript {

    private final EstadoOrdenDeCompraRepository estadoOrdenDeCompraRepository = new EstadoOrdenDeCompraRepository();
    private final TipoModeloInventarioRepository tipoModeloInventarioRepository = new TipoModeloInventarioRepository();
    private final TipoStockMovimientoRepository tipoStockMovimientoRepository = new TipoStockMovimientoRepository();
    private final ArticuloRepository articuloRepository = new ArticuloRepository();

    public DatosEstadosTiposScript() {

    }

    public void ejecutar() {
        crearEstadosOrdenCompraIniciales();
        crearTipoModeloInventarioIniciales();
        crearTipoStockMovimientoIniciales();
        //crearArticulosIniciales();
    }

    private void crearEstadosOrdenCompraIniciales() {
        crearEstadoSiNoExiste("Pendiente");
        crearEstadoSiNoExiste("Enviada");
        crearEstadoSiNoExiste("Finalizada");
        crearEstadoSiNoExiste("Cancelada");
    }

    private void crearEstadoSiNoExiste(String nombreEstado) {
        if (!estadoOrdenDeCompraRepository.existeEstadoPorNombre(nombreEstado)) {
            EstadoOrdenDeCompra estadoOrdenDeCompra = new EstadoOrdenDeCompra();
            estadoOrdenDeCompra.setNombreEstadoOC(nombreEstado);
            estadoOrdenDeCompraRepository.guardar(estadoOrdenDeCompra);
            System.out.println("✔ Estado creado: " + nombreEstado);
        }
    }

    private void crearTipoModeloInventarioIniciales() {
        creatTipoModeloSiNoExiste("Modelo Lote Fijo");
        creatTipoModeloSiNoExiste("Modelo Periodo Fijo");
    }

    private void creatTipoModeloSiNoExiste(String modelo) {
        if (!tipoModeloInventarioRepository.existeTipoModeloPorNombre(modelo)) {
            TipoModeloInventario tipoModeloInventario = new TipoModeloInventario();
            tipoModeloInventario.setNombreModeloInventario(modelo);
            tipoModeloInventarioRepository.guardar(tipoModeloInventario);
            System.out.println("✔ Tipo de Modelo de Inventario creado: " + modelo);
        }
    }

    private void crearTipoStockMovimientoIniciales() {
        crearTipoMovimientoSiNoExiste("Entrada");
        crearTipoMovimientoSiNoExiste("Salida");
        crearTipoMovimientoSiNoExiste("Ajuste");
    }

    private void crearTipoMovimientoSiNoExiste(String movimiento) {
        if (!tipoStockMovimientoRepository.existeTipoStockPorNombre(movimiento)) {
            TipoStockMovimiento tipoStockMovimiento = new TipoStockMovimiento();
            tipoStockMovimiento.setNombreTipoStockMovimiento(movimiento);
            tipoStockMovimientoRepository.guardar(tipoStockMovimiento);
            System.out.println("✔ Tipo de Stock Movimiento creado: " + movimiento);
        }
    }


    private void crearArticulosIniciales() {
        if ((articuloRepository.buscarTodos()).isEmpty()) {
            // Aquí puedes agregar la lógica para crear artículos iniciales si es necesario.
            System.out.println("[-] No hay articulos, agregando algunos iniciales.");
            // Ejemplo de creación de un artículo inicial
            Articulo articulo1 = Articulo.builder()
                    .nombreArticulo("Coca-Cola")
                    .descripcionArticulo("Bebida gaseosa 2.5L")
                    .stockActual(100)
                    .build();
            articuloRepository.guardar(articulo1);

            Articulo articulo2 = Articulo.builder()
                    .nombreArticulo("Pepsi")
                    .descripcionArticulo("Bebida gaseosa 3L")
                    .stockActual(50)
                    .build();
            articuloRepository.guardar(articulo2);

            Articulo articulo3 = Articulo.builder()
                    .nombreArticulo("Fanta")
                    .descripcionArticulo("Bebida gaseosa 1.5L")
                    .stockActual(75)
                    .build();
            articuloRepository.guardar(articulo3);
        }
    }

}
