package com.nventory.service;

import com.nventory.DTO.ArticuloDTO;
import com.nventory.DTO.ArticuloProveedorDTO;
import com.nventory.DTO.CGIDTO;
import com.nventory.DTO.StockMovimientoDTO;
import com.nventory.model.*;
import com.nventory.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ArticuloService {

    private final OrdenDeCompraArticuloRepository ordenDeCompraArticuloRepository = new OrdenDeCompraArticuloRepository();
    private final ArticuloProveedorRepository articuloProveedorRepository = new ArticuloProveedorRepository();

    private final ConfiguracionInventarioService configuracionInventarioService = new ConfiguracionInventarioService();

    private StockMovimientoRepository stockMovimientoRepository;
    private TipoStockMovimientoRepository tipoStockMovimientoRepository;

    ArticuloRepository articuloRepository;

    public ArticuloService(ArticuloRepository articuloRepository) {this.articuloRepository = articuloRepository;}
    public ArticuloService() {this.articuloRepository = new ArticuloRepository();}

    //--------------------Obtener todos los articulos--------------------------

    public List<ArticuloDTO> obtenerTodosArticulos(){
        List<ArticuloDTO> articulosDisponibles = new ArrayList<>();
        List<Articulo> articulos = articuloRepository.buscarTodos();

        for (Articulo articulo : articulos) {
            if (articulo.getFechaHoraBajaArticulo() == null) {
                ArticuloDTO articuloDTO = new ArticuloDTO();
                articuloDTO.setCodArticulo(articulo.getCodArticulo());
                articuloDTO.setNombreArticulo(articulo.getNombreArticulo());
                articuloDTO.setStockActual(articulo.getStockActual());
                articuloDTO.setPrecioArticulo(articulo.getPrecioArticulo());
                articulosDisponibles.add(articuloDTO);
            }
        }
        return articulosDisponibles;
    }

    //------------------Obtener el articulo por ID--------------------------------
    public ArticuloDTO obtenerArticuloPorId(Long codArticulo) {
        Articulo articulo = articuloRepository.buscarPorId(codArticulo);
        ArticuloDTO articuloDTO = new ArticuloDTO();

        articuloDTO.setNombreArticulo(articulo.getNombreArticulo());
        articuloDTO.setDescripcionArticulo(articulo.getDescripcionArticulo());
        articuloDTO.setStockActual(articulo.getStockActual());
        articuloDTO.setCostoAlmacenamiento(articulo.getCostoAlmacenamiento());
        articuloDTO.setPrecioArticulo(articulo.getPrecioArticulo());
        articuloDTO.setNivelServicioArticulo(articulo.getNivelServicioArticulo());
        articuloDTO.setDiasEntreRevisiones(articulo.getDiasEntreRevisiones());
        articuloDTO.setDemandaArt(articulo.getDemandaArt());

        return articuloDTO;
    }

    public void actualizarStock(Long idArticulo, Integer cantidad) {
       Articulo articulo = articuloRepository.buscarPorId(idArticulo);
       Integer cantidadVieja = articulo.getStockActual();
       Integer cantidadNueva = cantidadVieja + cantidad;
        articulo.setStockActual(cantidadNueva);
        articuloRepository.guardar(articulo);
    }

    //-----------------Metodo del service para dar de baja un articulo------------------------------------------
    public void darDeBajaArticulo(ArticuloDTO articuloDTO){
        Articulo articulo = articuloRepository.buscarPorId(articuloDTO.getCodArticulo());

        //Se valida que el articulo no tenga stock para darle de baja
        if(articulo.getStockActual() > 0){
            throw new IllegalStateException("No se puede dar de baja: el artículo aún tiene stock.");
        }

        //Se valida que el articulo no tenga ordenes de compra pendiente o enviada para poder dar de baja
        ArticuloProveedor proveedorPredeterminado = articulo.getArticuloProveedor();

        //Si no hay proveedor predeterminado, permitir la baja directamente
        if(proveedorPredeterminado != null ){
            Long codArticuloProveedor = proveedorPredeterminado.getCodArticuloProveedor();

            //Buscar ordenesCompraArticulo relacionadas a ese codArticuloProveedor
            List<OrdenDeCompraArticulo> ordenesCompraArticulo = ordenDeCompraArticuloRepository.buscarOrdenCompraArticuloDeArticulo(codArticuloProveedor);

            for(OrdenDeCompraArticulo oca : ordenesCompraArticulo){
                OrdenDeCompra orden = oca.getOrdenDeCompra();
                EstadoOrdenDeCompra estado = orden.getEstadoOrdenDeCompra();
                String nombreEstado = estado.getNombreEstadoOC();

                //Si es Pendiente o Enviada, no se puede hacer la baja
                if("Pendiente".equalsIgnoreCase(nombreEstado) || "Enviada".equalsIgnoreCase(nombreEstado)){

                    throw new IllegalStateException("No se puede dar de baja un artículo con órdenes de compra pendientes o enviadas.");
                }
            }
        }

        // Si pasa las validaciones, dar de baja
        articulo.setFechaHoraBajaArticulo(articuloDTO.getFechaHoraBajaArticulo());
        articuloRepository.guardar(articulo);


        //Dar de baja todos los articulosProveedor asociado a ese articulo
        List<ArticuloProveedor> articuloProveedores = articuloProveedorRepository.buscarTodosArticuloProveedor(articulo.getCodArticulo());
        for (ArticuloProveedor ap : articuloProveedores) {
            ap.setFechaHoraBajaArticuloProveedor(articuloDTO.getFechaHoraBajaArticulo());
            articuloProveedorRepository.guardar(ap);
        }
    }

    //-----------------Metodo del service para obtener Proveedores del articulo seleccionado------------------------
    public List<ArticuloProveedorDTO> obtenerProveedoresDeEseArticulo(Long codArticulo){
        List<ArticuloProveedorDTO> articulosProDTO = new ArrayList<>();
        List<ArticuloProveedor> articulosPro = articuloProveedorRepository.buscarTodosArticuloProveedor(codArticulo);

        for(ArticuloProveedor articulosProv : articulosPro){
            ArticuloProveedorDTO articuloProveedorDTO = new ArticuloProveedorDTO();
            articuloProveedorDTO.setId(articulosProv.getCodArticuloProveedor());
            articuloProveedorDTO.setNombre(articulosProv.getProveedor().getNombreProveedor());

            articulosProDTO.add(articuloProveedorDTO);
        }
        return articulosProDTO;
    }

    //----------------Metodo del service para asignar el proveedor predeterminado de un articulo-------------
    public void asignarProveedorPredeterminado(Long codArticuloProveedor){
        ArticuloProveedor articuloProveedorTraido = articuloProveedorRepository.buscarPorId(codArticuloProveedor);
        if (articuloProveedorTraido != null) {
            Articulo articulo = articuloProveedorTraido.getArticulo();
            articulo.setArticuloProveedor(articuloProveedorTraido);
            articuloRepository.guardar(articulo);
        }
    }

    //----------------Metodo del service para obtener el predeterminado------------------------
    public ArticuloProveedorDTO obtenerProveedorPredeterminado(Long codArticulo){
        Articulo articulo = articuloRepository.buscarPorId(codArticulo);
        ArticuloProveedor articuloProveedor = articulo.getArticuloProveedor();
        if(articuloProveedor != null){
            ArticuloProveedorDTO articuloProveedorDTO = new ArticuloProveedorDTO();
            articuloProveedorDTO.setId(articuloProveedor.getCodArticuloProveedor());
            articuloProveedorDTO.setNombre(articuloProveedor.getProveedor().getNombreProveedor());

            return articuloProveedorDTO;
        }
        return null;

    }

    public List<Articulo> listarArticulos() {
        return articuloRepository.buscarTodos();
    }

    //----------------------------Metodo para traerme todos los articulos a reponer---------------------------------
    public List<ArticuloDTO> obtenerArticulosReponer() {
        List<Articulo> articulos = articuloRepository.buscarTodos();
        List<ArticuloDTO> articulosAReponer = new ArrayList<>();
        for (Articulo articulo : articulos) {
            if (articulo.getFechaHoraBajaArticulo() == null) {

                //Leemos el proveedor predeterminado
                ArticuloProveedor provPredeterminado = articulo.getArticuloProveedor();

                if (provPredeterminado != null) {

                    int puntoPedido = articulo.getArticuloProveedor().getConfiguracionInventario().getPuntoPedido();
                    int stockActual = articulo.getStockActual();

                    if (stockActual <= puntoPedido) {
                        Long codArticuloProveedor = provPredeterminado.getCodArticuloProveedor();
                        //Buscar ordenesCompraArticulo relacionadas a ese codArticuloProveedor
                        List<OrdenDeCompraArticulo> ordenesCompraArticulo = ordenDeCompraArticuloRepository.buscarOrdenCompraArticuloDeArticulo(codArticuloProveedor);

                        boolean tieneOrdenPendienteOEnviada = false;
                        for (OrdenDeCompraArticulo oca : ordenesCompraArticulo) {
                            OrdenDeCompra orden = oca.getOrdenDeCompra();
                            EstadoOrdenDeCompra estado = orden.getEstadoOrdenDeCompra();
                            String nombreEstado = estado.getNombreEstadoOC();

                            if ("Pendiente".equalsIgnoreCase(nombreEstado) || "Enviada".equalsIgnoreCase(nombreEstado)) {
                                tieneOrdenPendienteOEnviada = true;
                                break;
                            }
                        }
                        if (tieneOrdenPendienteOEnviada == false) {
                            ArticuloDTO articuloDTO = new ArticuloDTO();
                            articuloDTO.setCodArticulo(articulo.getCodArticulo());
                            articuloDTO.setNombreArticulo(articulo.getNombreArticulo());
                            articulosAReponer.add(articuloDTO);
                        }
                    }
                }
            }
        }
        return articulosAReponer;
    }

    //-----------------Metodo del service para buscar articulo por ID---------------------------------
    public Articulo buscarArticuloPorId(Long id) {
        Articulo articulo = articuloRepository.buscarPorId(id);
        if (articulo == null) {
            throw new IllegalArgumentException("El articulo con ID " + id + " no existe.");
        }
        return articulo;
    }

    //-----------------Metodo para buscar art por nombre, lo uso en ventas--------------------------------
    public ArticuloDTO buscarArtPorNombre(String nombre) {
        ArticuloDTO artDTO = new ArticuloDTO();
        Articulo art = articuloRepository.buscarArticuloPorNombre(nombre);
        artDTO.setNombreArticulo(art.getNombreArticulo());
        artDTO.setCodArticulo(art.getCodArticulo());
        artDTO.setDemandaArt(art.getDemandaArt());
        artDTO.setStockActual(art.getStockActual());
        artDTO.setDescripcionArticulo(art.getDescripcionArticulo());
        artDTO.setCostoAlmacenamiento(art.getCostoAlmacenamiento());
        artDTO.setPrecioArticulo(art.getPrecioArticulo());
        return artDTO;
    }

    //-----------------Metodo para buscar art por nombre, lo uso en ventas--------------------------------
    public Articulo buscarArticuloPorNombre(String nombre) {
        Articulo art = articuloRepository.buscarArticuloPorNombre(nombre);
        return art;
    }

    //-----------------Metodo para buscar art que no estén dados de baja------------------------------------------
    public List<ArticuloDTO> listarArticulosDisponibles () {
        List<Articulo> articulos = articuloRepository.buscarTodos();
        List<ArticuloDTO> articulosDisponibles = new ArrayList<>();
        for (Articulo articulo : articulos) {
            if (articulo.getFechaHoraBajaArticulo() == null) {
                ArticuloDTO articuloDTO = new ArticuloDTO();
                articuloDTO.setNombreArticulo(articulo.getNombreArticulo());
                articulosDisponibles.add(articuloDTO);
            }
        }
        return articulosDisponibles;
    }

    //-----------------Metodo para buscar art que no estén dados de baja y que estén asociados a artProv------------------------------------------
    public List<ArticuloDTO> listarArticulosConfigurados () {
        List<Articulo> articulos = articuloRepository.buscarTodos();
        List<ArticuloDTO> articulosDisponibles = new ArrayList<>();
        for (Articulo articulo : articulos) {
            if (articulo.getFechaHoraBajaArticulo() == null && articulo.getStockActual() > 0) {
                ArticuloDTO articuloDTO = new ArticuloDTO();
                articuloDTO.setNombreArticulo(articulo.getNombreArticulo());
                articulosDisponibles.add(articuloDTO);
            }
        }
        return articulosDisponibles;
    }

    //-----------------Metodo del service crear un artículo-----------------------------
    public void crearArticulo(ArticuloDTO articuloDTO) {

        validarArticulo(articuloDTO);

        BigDecimal costoAlmacenamiento = articuloDTO.getCostoAlmacenamiento();
        BigDecimal nivelServicioArticulo = articuloDTO.getNivelServicioArticulo();
        BigDecimal precioArticulo = articuloDTO.getPrecioArticulo();
        String nombreArticulo = articuloDTO.getNombreArticulo();
        String descripcionArticulo = articuloDTO.getDescripcionArticulo();
        int demandaArt = articuloDTO.getDemandaArt();
        LocalDateTime fechaHoraBajaArticulo = articuloDTO.getFechaHoraBajaArticulo();
        Integer stockActual = articuloDTO.getStockActual();
        int diasEntreRevisiones = articuloDTO.getDiasEntreRevisiones();

        Articulo articulo = Articulo.builder()
                .costoAlmacenamiento(costoAlmacenamiento)
                .nivelServicioArticulo(nivelServicioArticulo)
                .precioArticulo(precioArticulo)
                .nombreArticulo(nombreArticulo)
                .descripcionArticulo(descripcionArticulo)
                .demandaArt(demandaArt)
                .fechaHoraBajaArticulo(fechaHoraBajaArticulo)
                .stockActual(stockActual)
                .diasEntreRevisiones(diasEntreRevisiones)
                .build();
        articuloRepository.guardar(articulo);
    }

    //-----------------Metodo del service modificar un artículo-----------------------------
    public void modificarArticulo(ArticuloDTO articuloDTO) {

        Long codArticulo = articuloDTO.getCodArticulo();
        Articulo articuloExistente = articuloRepository.buscarPorId(codArticulo);

        if (articuloExistente == null) {
            throw new IllegalArgumentException("El articulo con ID " + codArticulo + " no existe.");
        }

        if (articuloExistente.getFechaHoraBajaArticulo() != null) {
            throw new IllegalArgumentException("El articulo con ID " + codArticulo + " no puede ser modificado porque está dado de baja.");
        }

        validarArticulo(articuloDTO);

        if (articuloExistente.getCostoAlmacenamiento().compareTo(articuloDTO.getCostoAlmacenamiento()) != 0
        || articuloExistente.getNivelServicioArticulo().compareTo(articuloDTO.getNivelServicioArticulo()) != 0
        || articuloExistente.getDemandaArt() != articuloDTO.getDemandaArt()
        || articuloExistente.getDiasEntreRevisiones() != articuloDTO.getDiasEntreRevisiones()
        || articuloExistente.getStockActual().compareTo(articuloDTO.getStockActual()) != 0){
            configuracionInventarioService.recalcularFormulasArticulo(articuloExistente, articuloDTO);
        }

        articuloExistente.setCostoAlmacenamiento(articuloDTO.getCostoAlmacenamiento());
        articuloExistente.setNivelServicioArticulo(articuloDTO.getNivelServicioArticulo());
        articuloExistente.setPrecioArticulo(articuloDTO.getPrecioArticulo());
        articuloExistente.setNombreArticulo(articuloDTO.getNombreArticulo());
        articuloExistente.setDescripcionArticulo(articuloDTO.getDescripcionArticulo());
        articuloExistente.setDemandaArt(articuloDTO.getDemandaArt());
        articuloExistente.setFechaHoraBajaArticulo(articuloDTO.getFechaHoraBajaArticulo());
        articuloExistente.setStockActual(articuloDTO.getStockActual());
        articuloExistente.setDiasEntreRevisiones(articuloDTO.getDiasEntreRevisiones());

        articuloRepository.guardar(articuloExistente);
    }

    //-----------------Metodo del service para articulos en stock de seguridad-----------------------------
    public List<Articulo> listarArticulosEnStockSeg () {
        List<Articulo> articulos = articuloRepository.buscarTodos();
        List<Articulo> articulosEnStockSeg = new ArrayList<>();
        for (Articulo articulo : articulos) {
            if (articulo.getFechaHoraBajaArticulo() == null) {
                if(articulo.getArticuloProveedor() != null){
                    ArticuloProveedor articuloProveedor = articulo.getArticuloProveedor();
                    ConfiguracionInventario configuracionInventario = articuloProveedor.getConfiguracionInventario();
                    Integer stockSeg = configuracionInventario.getStockSeguridad();
                    Integer stockActual = articulo.getStockActual();
                    if(stockActual <= stockSeg){
                        articulosEnStockSeg.add(articulo);
                    }
                }
            }
        }
        return articulosEnStockSeg;
    }

    //-----------------Metodo del service para calcular CGI-----------------------------
    public List<CGIDTO> calcularCGI () {
        List<Articulo> articulos = articuloRepository.buscarTodos();
        List<CGIDTO> cgiDtoList = new ArrayList<>();
        for (Articulo articulo : articulos) {
            if (articulo.getFechaHoraBajaArticulo() == null && articulo.getArticuloProveedor() != null) {
                ArticuloProveedor artProv = articulo.getArticuloProveedor();
                ConfiguracionInventario confInv = artProv.getConfiguracionInventario();
                TipoModeloInventario modelo = confInv.getTipoModeloInventario();

                BigDecimal D = new BigDecimal(articulo.getDemandaArt());
                BigDecimal C = artProv.getPrecioUnitario();
                BigDecimal S = artProv.getCostoPedido();
                BigDecimal H = articulo.getCostoAlmacenamiento();
                BigDecimal Q = new BigDecimal(confInv.getLoteOptimo());

                if (modelo.getCodTipoModeloI() == 1) {
                    BigDecimal parte1 = C.multiply(D);
                    BigDecimal parte2 = S.multiply(D.divide(Q, 2, RoundingMode.HALF_UP));
                    BigDecimal parte3 = H.multiply(Q.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP));

                    BigDecimal cgi = parte1.add(parte2).add(parte3).setScale(2, RoundingMode.HALF_UP);

                    CGIDTO cgiDto = new CGIDTO();
                    cgiDto.setNombreArticulo(articulo.getNombreArticulo());
                    cgiDto.setCgi(cgi);

                    cgiDtoList.add(cgiDto);

                } else if (modelo.getCodTipoModeloI() == 2) {
                    ArticuloDTO artDto = new ArticuloDTO();
                    artDto.setStockActual(articulo.getStockActual());
                    artDto.setDemandaArt(articulo.getDemandaArt());
                    artDto.setDiasEntreRevisiones(articulo.getDiasEntreRevisiones());
                    artDto.setNivelServicioArticulo(articulo.getNivelServicioArticulo());

                    BigDecimal T = new BigDecimal(articulo.getDiasEntreRevisiones());
                    BigDecimal SS = new BigDecimal(configuracionInventarioService.calcularStockSeguridadPeriodoFijo(artDto, artProv));
                    BigDecimal d = new BigDecimal(articulo.getDemandaArt()).divide(new BigDecimal("365"), 4, RoundingMode.HALF_UP);
                    BigDecimal L = new BigDecimal(artProv.getDemoraEntregaDias());
                    BigDecimal parte1 = C.multiply(D);
                    BigDecimal parte2 = S.multiply(D.divide(T, 2, RoundingMode.HALF_UP));
                    BigDecimal parte3 = H.multiply(SS.add(d.multiply((T.add(L))).divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP)));

                    BigDecimal cgi = parte1.add(parte2).add(parte3).setScale(2, RoundingMode.HALF_UP);

                    CGIDTO cgiDto = new CGIDTO();
                    cgiDto.setNombreArticulo(articulo.getNombreArticulo());
                    cgiDto.setCgi(cgi);

                    cgiDtoList.add(cgiDto);
                }
            }
        }
        return cgiDtoList;
    }

    //-----------------Metodo del service para obtener stock actual de un articulo-----------------------------
    public Integer obtenerStockActual (String nombreArticulo) {
        ArticuloDTO articuloDTO = buscarArtPorNombre(nombreArticulo);
        Integer stockActual = articuloDTO.getStockActual();

        return stockActual;
    }

    //-----------------Metodo del service para ajuste de inventario-----------------------------
    public void realizarAjusteInventario (StockMovimientoDTO stockMovimientoDTO) {
        Articulo articulo = buscarArticuloPorId(stockMovimientoDTO.getArticuloID());

        Integer inventarioMaximo = null;

        if(articulo.getArticuloProveedor() != null) {
            inventarioMaximo  = articulo.getArticuloProveedor()
                    .getConfiguracionInventario()
                    .getInventarioMaximo();
        }

        int nuevaCantidad = stockMovimientoDTO.getCantidad();

        if(inventarioMaximo != null) {
            if (nuevaCantidad < 0 || nuevaCantidad > inventarioMaximo) {
                throw new IllegalArgumentException("La cantidad debe estar entre 0 y " + inventarioMaximo);
            }
        }

        articulo.setStockActual(nuevaCantidad);
        articuloRepository.guardar(articulo);

        tipoStockMovimientoRepository = new TipoStockMovimientoRepository();
        stockMovimientoRepository = new StockMovimientoRepository();

        TipoStockMovimiento tipoStockMovimiento = tipoStockMovimientoRepository.buscarPorId(3L);

        StockMovimiento stockMovimiento = new StockMovimiento();
        stockMovimiento.setCantidad(nuevaCantidad);
        stockMovimiento.setComentario(stockMovimientoDTO.getComentario());
        stockMovimiento.setFechaHoraMovimiento(stockMovimientoDTO.getFechaHoraMovimiento());
        stockMovimiento.setTipoStockMovimiento(tipoStockMovimiento);

        stockMovimientoRepository.guardar(stockMovimiento);
    }

    public int obtenerInventarioMax (Long idArticulo) {
        Articulo articulo = buscarArticuloPorId(idArticulo);
        return articulo.getArticuloProveedor().getConfiguracionInventario().getInventarioMaximo();
    }

    //-----------------Metodo del service para Validar el Articulo-----------------------------
    public void validarArticulo(ArticuloDTO articuloDTO) {

        BigDecimal nivelServicio = articuloDTO.getNivelServicioArticulo();
        if (nivelServicio == null || nivelServicio.compareTo(BigDecimal.ZERO) < 0 || nivelServicio.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("El nivel de servicio debe estar entre 0 y 1.");
        }

        BigDecimal precio = articuloDTO.getPrecioArticulo();
        if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio del artículo debe ser mayor que 0.");
        }

        Integer diasRev = articuloDTO.getDiasEntreRevisiones();
        if (diasRev == null || diasRev <= 0) {
            throw new IllegalArgumentException("Los días entre revisiones deben ser mayores a 0.");
        }

        Integer stock = articuloDTO.getStockActual();
        if (stock == null || stock < 0) {
            throw new IllegalArgumentException("El stock actual no puede ser negativo.");
        }

        if (articuloDTO.getFechaHoraBajaArticulo() != null) {
            throw new IllegalStateException("Este Artículo no debe tener fecha de baja para la acción que intenta realizar.");
        }
    }

}



