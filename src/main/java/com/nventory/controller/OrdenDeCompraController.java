package com.nventory.controller;

import com.nventory.DTO.OrdenDeCompraDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrdenDeCompraController {

    public List<OrdenDeCompraDTO> obtenerTodasOrdenesDeCompra() {
        List<OrdenDeCompraDTO> ordenDeCompraDTOs = new ArrayList<OrdenDeCompraDTO>();
        OrdenDeCompraDTO ordenDeCompraDTO1 = new OrdenDeCompraDTO(123L, "Pendiente", "Coca-Cola", "19.99");
        ordenDeCompraDTOs.add(ordenDeCompraDTO1);

        OrdenDeCompraDTO ordComp2 = new OrdenDeCompraDTO(233L, "Enviada", "Arcos-SA", "1202330.54");
        ordenDeCompraDTOs.add(ordComp2);
        return ordenDeCompraDTOs;
    }


}
