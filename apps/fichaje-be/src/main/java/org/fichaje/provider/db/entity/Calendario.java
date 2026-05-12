package org.fichaje.provider.db.entity;

import java.util.List;
import java.util.Objects;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entidad que representa un calendario laboral.
 */
@Entity
@Table(name = "calendarios")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Calendario implements TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String nombre;

    @Builder.Default
    @Column(columnDefinition = "boolean default false")
    private boolean active = false;

    @NotNull
    @Column(nullable = false)
    private int year;

    @NotNull
    @Column(nullable = false)
    private int minutosMasEntrada;

    @NotNull
    @Column(nullable = false)
    private int minutosMenosEntrada;

    @JsonIgnoreProperties(value = { "calendario" })
    @OneToMany(mappedBy = "calendario", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaLaborable> dias;

    @JsonIgnoreProperties(value = { "calendarios" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_id")
    private Sede sede;

    @Override
    public Empresa getEmpresa() {
        return (sede != null) ? sede.getEmpresa() : null;
    }

    @Override
    public void setEmpresa(Empresa empresa) {
        // La empresa se establece a través de la sede.
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Calendario that = (Calendario) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
