package com.nventory.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
@Entity
public class TipoStockMovimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codTipoStockMov;
    private LocalDateTime fechaHoraBajaTSM;
    private String nombreTipoStockMovimiento;

}
