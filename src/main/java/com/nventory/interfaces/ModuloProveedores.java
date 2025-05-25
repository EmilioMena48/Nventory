package com.nventory.interfaces;

import com.nventory.DTO.ProveedorDTO;
import com.nventory.DTO.ProveedorEliminadoDTO;
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
    public ProveedorDTO BuscarProveedor(@NonNull Long codProveedor);
    void ListarArticulosxProveedor();
    void AsociarArticuloProveedor();
}