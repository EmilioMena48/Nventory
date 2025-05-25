package com.nventory.interfaces;

import com.nventory.DTO.ArticuloProveedorGuardadoDTO;
import com.nventory.DTO.ProveedorDTO;
import com.nventory.DTO.ProveedorEliminadoDTO;
import com.nventory.model.Articulo;
import com.nventory.model.Proveedor;
import lombok.NonNull;

import java.util.List;

public interface ModuloProveedores  {
    /*
     * Modulo Proveedores
     *
     * Este modulo permite gestionar los proveedores de la aplicacion.
     *
     * @author Juan Pablo
     * @version 1.0
     */
    public void GuardarProveedor(@NonNull ProveedorDTO proveedor);
    public void EliminarProveedor(@NonNull Long codProveedor);
    public List<ProveedorDTO> ListarProveedores();
    public List<ProveedorEliminadoDTO> ListarProveedoresEliminados();
    public Proveedor guardarYRetornar(ProveedorDTO proveedorDto);
    public Proveedor buscarProveedorPorId(Long idProveedor);
    void ListarArticulosxProveedor();
    public void AsociarArticuloProveedor(Articulo articulo, Proveedor proveedor, ArticuloProveedorGuardadoDTO articuloProveedor);
}