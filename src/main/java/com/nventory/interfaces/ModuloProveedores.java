package com.nventory.interfaces;

import com.nventory.DTO.ArticuloProveedorGuardadoDTO;
import com.nventory.DTO.ConfigInvDTO;
import com.nventory.DTO.ProveedorDTO;
import com.nventory.DTO.ProveedorEliminadoDTO;
import com.nventory.model.Articulo;
import com.nventory.model.Proveedor;
import lombok.NonNull;

import java.util.List;

public interface ModuloProveedores  {
    /*
     * Módulo Proveedores
     *
     * Este módulo permite gestionar los proveedores de la application.
     *
     * @author Juan Pablo
     * @version 1.0
     */
    void GuardarProveedor(@NonNull ProveedorDTO proveedor);
    void EliminarProveedor(@NonNull Long codProveedor);
    List<ProveedorDTO> ListarProveedores();
    List<ProveedorEliminadoDTO> ListarProveedoresEliminados();
    Proveedor GuardarYRetornar(ProveedorDTO proveedorDto);
    Proveedor BuscarProveedorPorId(Long idProveedor);
    List<Articulo> ListarArticulos(Long codProveedor);
    List<Articulo> ListarArticulos();
    ArticuloProveedorGuardadoDTO BuscarArticuloProveedor(Long articuloId, Long proveedorId);
    void AsociarArticuloProveedor(Articulo articulo, Proveedor proveedor, ArticuloProveedorGuardadoDTO articuloProveedor);
    void AsociarArticuloProveedor(Articulo articulo, Proveedor proveedor, ArticuloProveedorGuardadoDTO articuloProveedorDto, Boolean tipoModelo);
    void EliminarArticuloProveedor(Long articuloId, Long proveedorId);
    boolean EstaEliminadoArticuloProveedor(Long articuloId, Long proveedorId);
    ConfigInvDTO BuscarConfigInventario(Long articuloId, Long proveedorId);
    Proveedor BuscarProveedorPorNombre(String nombreProveedor);
}