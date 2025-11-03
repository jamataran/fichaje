package org.fichaje.provider.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entidad para gestionar API Keys de autenticación
 * Permite autenticación sin JWT para aplicaciones externas
 */
@Entity
@Table(name = "api_keys")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false, length = 64)
    private String keyHash; // Hash de la API Key (nunca almacenar en plano)

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name; // Nombre descriptivo de la API Key (ej: "App Mobile Producción")

    @Column(length = 255)
    private String description; // Descripción adicional

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // Usuario asociado a la API Key

    @NotNull
    @Column(nullable = false)
    private Boolean active = true; // Estado de la API Key

    @Column
    private LocalDateTime expiresAt; // Fecha de expiración (opcional, null = sin expiración)

    @Column
    private LocalDateTime lastUsedAt; // Última vez que se usó la API Key

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(length = 100)
    private String createdBy; // Usuario que creó la API Key

    /**
     * Verifica si la API Key está activa y no ha expirado
     */
    public boolean isValid() {
        if (!active) {
            return false;
        }
        if (expiresAt != null && LocalDateTime.now().isAfter(expiresAt)) {
            return false;
        }
        return true;
    }

    /**
     * Actualiza el timestamp de último uso
     */
    public void updateLastUsed() {
        this.lastUsedAt = LocalDateTime.now();
    }
}
