package com.nventory.service;

import com.nventory.DTO.ArticuloDTO;
import com.nventory.DTO.ConfigInvDTO;
import com.nventory.model.Articulo;
import com.nventory.model.ArticuloProveedor;
import com.nventory.model.ConfiguracionInventario;
import com.nventory.model.TipoModeloInventario;
import com.nventory.repository.ArticuloProveedorRepository;
import com.nventory.repository.ConfiguracionInventarioRepository;
import com.nventory.repository.TipoModeloInventarioRepository;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ConfiguracionInventarioService {

    ArticuloProveedorRepository articuloProveedorRepository = new ArticuloProveedorRepository();
    TipoModeloInventarioRepository tipoModeloInventarioRepository = new TipoModeloInventarioRepository();
    ConfiguracionInventarioRepository configuracionInventarioRepository;

    public ConfiguracionInventarioService() {
        this.configuracionInventarioRepository = new ConfiguracionInventarioRepository();
    }

    public void guardarConfigInventario(ArticuloProveedor articuloProveedor) {
        configuracionInventarioRepository.guardar(articuloProveedor.getConfiguracionInventario());
    }

    public void recalcularFormulasArticuloProveedor(ArticuloProveedor articuloProveedor) {
        ConfiguracionInventario configuracionInventario = articuloProveedor.getConfiguracionInventario();
        String nombreModeloInventario = configuracionInventario.getTipoModeloInventario().getNombreModeloInventario();

        ArticuloDTO articuloDTO = new ArticuloDTO(articuloProveedor.getArticulo().getCodArticulo(),
                articuloProveedor.getArticulo().getCostoAlmacenamiento(),
                articuloProveedor.getArticulo().getNivelServicioArticulo(),
                articuloProveedor.getArticulo().getPrecioArticulo(),
                articuloProveedor.getArticulo().getDemandaArt(),
                articuloProveedor.getArticulo().getNombreArticulo(),
                articuloProveedor.getArticulo().getDescripcionArticulo(),
                articuloProveedor.getArticulo().getFechaHoraBajaArticulo(),
                articuloProveedor.getArticulo().getStockActual(),
                articuloProveedor.getArticulo().getDiasEntreRevisiones(),
                articuloProveedor.getArticulo().getDesviacionEstandarArticulo(),
                articuloProveedor.getArticulo().getArticuloProveedor() == null ? "" : articuloProveedor.getArticulo().getArticuloProveedor().getProveedor().getNombreProveedor());

        if (nombreModeloInventario.compareTo("Modelo Lote Fijo") == 0){
            configuracionInventario.setLoteOptimo(calcularLoteOptimo(articuloDTO, articuloProveedor));
            configuracionInventario.setStockSeguridad(calcularStockSeguridadLoteFijo(articuloDTO, articuloProveedor));
            configuracionInventario.setPuntoPedido(calcularPuntoPedido(articuloDTO, articuloProveedor));
        }else {
            configuracionInventario.setStockSeguridad(calcularStockSeguridadPeriodoFijo(articuloDTO, articuloProveedor));
            configuracionInventario.setInventarioMaximo(calcularInventarioMax(articuloDTO, articuloProveedor));
            configuracionInventario.setCantidadPedir(calcularCantidadPedir(articuloDTO, articuloProveedor));
        }

        configuracionInventarioRepository.guardar(configuracionInventario);
    }

    public ConfiguracionInventario crearConfiguracionInventario(boolean isLoteFijo) {
        ConfiguracionInventario config = new ConfiguracionInventario();
        TipoModeloInventario tipoModelo;
        Long idCI;
        config.setCantidadPedir(0);
        config.setLoteOptimo(0);
        config.setPuntoPedido(0);
        config.setStockSeguridad(0);
        config.setInventarioMaximo(0);

        if (isLoteFijo) {
            tipoModelo = tipoModeloInventarioRepository.buscarPorNombre("Modelo Lote Fijo");
            config.setTipoModeloInventario(tipoModelo);
        } else {
            tipoModelo = tipoModeloInventarioRepository.buscarPorNombre("Modelo Periodo Fijo");
            config.setTipoModeloInventario(tipoModelo);
        }
        idCI = configuracionInventarioRepository.GuardarYRetornarID(config);
        return configuracionInventarioRepository.buscarPorId(idCI);
    }

    public void recalcularFormulasArticulo(Articulo articuloExistente, ArticuloDTO articuloNuevo){
        List<ArticuloProveedor> articuloProveedorList = articuloProveedorRepository.buscarArticuloProveedorPorArticulo(articuloExistente);

        for (ArticuloProveedor articuloProveedor : articuloProveedorList) {
            ConfiguracionInventario configuracionInventarioActualizada = articuloProveedor.getConfiguracionInventario();
            String nombreModeloInventario = configuracionInventarioActualizada.getTipoModeloInventario().getNombreModeloInventario();

            if (nombreModeloInventario.compareTo("Modelo Lote Fijo") == 0){
                configuracionInventarioActualizada.setLoteOptimo(calcularLoteOptimo(articuloNuevo, articuloProveedor));
                configuracionInventarioActualizada.setStockSeguridad(calcularStockSeguridadLoteFijo(articuloNuevo, articuloProveedor));
                configuracionInventarioActualizada.setPuntoPedido(calcularPuntoPedido(articuloNuevo, articuloProveedor));
            }else {
                configuracionInventarioActualizada.setStockSeguridad(calcularStockSeguridadPeriodoFijo(articuloNuevo, articuloProveedor));
                configuracionInventarioActualizada.setInventarioMaximo(calcularInventarioMax(articuloNuevo, articuloProveedor));
                configuracionInventarioActualizada.setCantidadPedir(calcularCantidadPedir(articuloNuevo, articuloProveedor));
            }

            configuracionInventarioRepository.guardar(configuracionInventarioActualizada);
        }
    }

    public ConfigInvDTO convertirAConfigInvDTO(ConfiguracionInventario configuracionInventario) {
        return new ConfigInvDTO(
                configuracionInventario.getInventarioMaximo(),
                configuracionInventario.getLoteOptimo(),
                configuracionInventario.getPuntoPedido(),
                configuracionInventario.getStockSeguridad(),
                configuracionInventario.getTipoModeloInventario().getNombreModeloInventario()
        );
    }

    public int calcularLoteOptimo(ArticuloDTO articuloNuevo, ArticuloProveedor articuloProveedor){
        BigDecimal dos = BigDecimal.valueOf(2);
        BigDecimal D = BigDecimal.valueOf(articuloNuevo.getDemandaArt());
        BigDecimal S = articuloProveedor.getCostoPedido();
        BigDecimal H = articuloNuevo.getCostoAlmacenamiento();

        BigDecimal numerador = dos.multiply(D).multiply(S);
        BigDecimal division = numerador.divide(H, 10, RoundingMode.HALF_UP);
        double raiz = Math.sqrt(division.doubleValue());

        return (int) Math.floor(raiz);
    }

    public int calcularStockSeguridadLoteFijo (ArticuloDTO articuloNuevo, ArticuloProveedor articuloProveedor){
        int L = articuloProveedor.getDemoraEntregaDias();
        double Z = calcularZ(articuloNuevo.getNivelServicioArticulo());
        double desviacionEstandar = Math.sqrt(L);

        return (int) Math.round(Z * desviacionEstandar);
    }

    public int calcularPuntoPedido(ArticuloDTO articuloNuevo, ArticuloProveedor articuloProveedor){
        int D = articuloNuevo.getDemandaArt();
        int d = (int) Math.round((double) D / 365);
        int L = articuloProveedor.getDemoraEntregaDias();
        int stockSeguridad = calcularStockSeguridadLoteFijo(articuloNuevo, articuloProveedor);

        return d * L + stockSeguridad;
    }

    public int calcularStockSeguridadPeriodoFijo (ArticuloDTO articuloNuevo, ArticuloProveedor articuloProveedor){
        int L = articuloProveedor.getDemoraEntregaDias();
        int T = articuloNuevo.getDiasEntreRevisiones();
        double Z = calcularZ(articuloNuevo.getNivelServicioArticulo());
        double desviacionEstandar = Math.sqrt(L + T);

        return (int) Math.round(Z * desviacionEstandar);
    }

    public int calcularInventarioMax(ArticuloDTO articuloNuevo, ArticuloProveedor articuloProveedor){
        int D = articuloNuevo.getDemandaArt();
        int d = (int) Math.round((double) D / 365);
        int L = articuloProveedor.getDemoraEntregaDias();
        int T = articuloNuevo.getDiasEntreRevisiones();
        int stockSeguridad = calcularStockSeguridadLoteFijo(articuloNuevo, articuloProveedor);

        return d * (L + T) + stockSeguridad;
    }

    public int calcularCantidadPedir(ArticuloDTO articuloNuevo, ArticuloProveedor articuloProveedor){
        int I = articuloNuevo.getStockActual();
        int InventarioMax = calcularInventarioMax(articuloNuevo, articuloProveedor);

        return InventarioMax - I;
    }

    private double calcularZ(BigDecimal nivelServicio) {
        NormalDistribution normal = new NormalDistribution();

        return normal.inverseCumulativeProbability(nivelServicio.doubleValue());
    }
}
