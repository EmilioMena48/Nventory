package com.nventory.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@AllArgsConstructor
@Data
@NoArgsConstructor
public class CGIDTO {
    private String nombreArticulo;
    private BigDecimal cgi;
}
