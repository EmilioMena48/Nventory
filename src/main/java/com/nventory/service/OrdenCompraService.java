package com.nventory.service;

import com.nventory.DTO.OrdenDeCompraDTO;
import com.nventory.model.OrdenDeCompra;
import com.nventory.repository.OrdenDeCompraRepository;

import java.util.ArrayList;
import java.util.List;

public class OrdenCompraService {

    private final OrdenDeCompraRepository ordenCompraRepo = new OrdenDeCompraRepository();

    public List<OrdenDeCompraDTO> obtenerTodasOrdenesDeCompra() {

        List<OrdenDeCompraDTO> ordenesDto = new ArrayList<>();
        List<OrdenDeCompra> ordenesCompra = ordenCompraRepo.buscarTodos();

        for (OrdenDeCompra ordenCompra : ordenesCompra) {
            OrdenDeCompraDTO ordenCompraDTO = new OrdenDeCompraDTO();

            Long codOrd = ordenCompra.getCodOrdenDeCompra();
            ordenCompraDTO.setCodOrdenDeCompra(codOrd);

            String total = ordenCompra.getTotalOrdenDeCompra().toString();
            ordenCompraDTO.setTotalOrden(total);

            String prov = ordenCompra.getProveedor().getNombreProveedor();
            ordenCompraDTO.setProveedor(prov);

            String estado = ordenCompra.getEstadoOrdenDeCompra().getNombreEstadoOC();
            ordenCompraDTO.setEstadoOrdenDeCompra(estado);

            ordenesDto.add(ordenCompraDTO);
        }

        return ordenesDto;
    }
}
