package org.fichaje.converter;

import org.springframework.stereotype.Component;
import org.fichaje.dto.entity.DiaDto;
import org.fichaje.provider.db.entity.Calendario;
import org.fichaje.provider.db.entity.DiaLaborable;

@Component
public class DiaDtoConverter {

    public DiaLaborable transform(DiaDto dto) {
        return DiaLaborable.builder()
                .id(dto.id())
                .dia(dto.dia())
                .horaInicio(dto.horaInicio())
                .horaFin(dto.horaFin())
                .build();
    }

    public DiaLaborable transformWithCalendario(DiaDto dto, Calendario calendario) {
        DiaLaborable dia = transform(dto);
        dia.setCalendario(calendario);
        return dia;
    }

    public DiaDto inverseTransform(DiaLaborable dia) {
        return new DiaDto(
            dia.getId(),
            dia.getDia(),
            dia.getHoraInicio(),
            dia.getHoraFin(),
            dia.getCalendario() != null ? dia.getCalendario().getNombre() : null
        );
    }
}
