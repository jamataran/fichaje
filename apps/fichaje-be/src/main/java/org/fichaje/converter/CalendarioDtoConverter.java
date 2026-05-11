package org.fichaje.converter;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.fichaje.dto.entity.CalendarioDto;
import org.fichaje.provider.db.entity.Calendario;

@Component
@RequiredArgsConstructor
public class CalendarioDtoConverter {

    private final DiaDtoConverter diaConverter;

    public Calendario transform(CalendarioDto dto) {
        Calendario c = Calendario.builder()
                .id(dto.id())
                .nombre(dto.nombre())
                .active(dto.active())
                .minutosMasEntrada(dto.minutosMasEntrada())
                .minutosMenosEntrada(dto.minutosMenosEntrada())
                .year(dto.year())
                .build();
        
        if (dto.dias() != null) {
            c.setDias(dto.dias().stream()
                    .map(diaDto -> diaConverter.transformWithCalendario(diaDto, c))
                    .collect(Collectors.toList()));
        }
        return c;
    }

    public CalendarioDto inverseTransform(Calendario c) {
        return new CalendarioDto(
            c.getId(),
            c.getNombre(),
            c.getYear(),
            c.isActive(),
            c.getMinutosMasEntrada(),
            c.getMinutosMenosEntrada(),
            c.getDias() != null ? c.getDias().stream()
                    .map(diaConverter::inverseTransform)
                    .collect(Collectors.toList()) : null
        );
    }
}
