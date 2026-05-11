package org.fichaje.provider.db.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entidad que representa un día laborable dentro de un calendario.
 */
@Entity
@Table(name = "dias_laborables")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaLaborable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dia;

    @NotNull
    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaInicio;

    @NotNull
    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaFin;

    @JsonIgnoreProperties(value = { "dias", "handler", "hibernateLazyInitializer" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendarioId")
    private Calendario calendario;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiaLaborable that = (DiaLaborable) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
