package org.fichaje.provider.db.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad para gestionar API Keys de autenticación.
 * Permite autenticación sin JWT para aplicaciones externas.
 */
@Entity
@Table(name = "api_keys")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false, length = 128) // Aumentado para soportar hashes más largos si es necesario
    private String keyHash;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY) // Cambiado a LAZY para mejor rendimiento
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull
    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @Column
    private LocalDateTime expiresAt;

    @Column
    private LocalDateTime lastUsedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(length = 100)
    private String createdBy;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY) // Cambiado a LAZY
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    public boolean isValid() {
        return Boolean.TRUE.equals(active) && (expiresAt == null || LocalDateTime.now().isBefore(expiresAt));
    }

    public void updateLastUsed() {
        this.lastUsedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiKey apiKey = (ApiKey) o;
        return id != null && Objects.equals(id, apiKey.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
