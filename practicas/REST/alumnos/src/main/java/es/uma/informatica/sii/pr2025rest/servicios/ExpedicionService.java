package es.uma.informatica.sii.pr2025rest.servicios;

import es.uma.informatica.sii.pr2025rest.dtos.ExpedicionEntradaDTO;
import es.uma.informatica.sii.pr2025rest.entidades.Equipo;
import es.uma.informatica.sii.pr2025rest.entidades.Expedicion;
import es.uma.informatica.sii.pr2025rest.excepciones.EntidadNoExisteException;
import es.uma.informatica.sii.pr2025rest.excepciones.ExpedicionConEquiposException;
import es.uma.informatica.sii.pr2025rest.repositorios.EquipoRepository;
import es.uma.informatica.sii.pr2025rest.repositorios.ExpedicionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ExpedicionService {

    private final ExpedicionRepository expedicionRepository;
    private final EquipoRepository equipoRepository;

    public ExpedicionService(ExpedicionRepository expedicionRepository, EquipoRepository equipoRepository) {
        this.expedicionRepository = expedicionRepository;
        this.equipoRepository = equipoRepository;
    }

    public List<Expedicion> obtenerTodas() {
        return expedicionRepository.findAll();
    }

    public Expedicion obtenerPorId(Long id) {
        return expedicionRepository.findById(id)
                .orElseThrow(EntidadNoExisteException::new);
    }

    public Expedicion crearExpedicion(ExpedicionEntradaDTO datos) {
        List<Equipo> equipos = buscarEquiposPorIds(datos.getEquipos());
        Expedicion nueva = new Expedicion();
        nueva.setNombre(datos.getNombre());
        nueva.setFechaInicio(datos.getFechaInicio());
        nueva.setFechaFin(datos.getFechaFin());
        nueva.setEquipos(new ArrayList<>(equipos));
        nueva.setEquipos(equipos);
        return expedicionRepository.save(nueva);
    }

    public Expedicion actualizarExpedicion(Long id, ExpedicionEntradaDTO datos) {
        Expedicion expedicion = expedicionRepository.findById(id)
                .orElseThrow(EntidadNoExisteException::new);
        List<Equipo> equipos = buscarEquiposPorIds(datos.getEquipos());
        expedicion.setNombre(datos.getNombre());
        expedicion.setFechaInicio(datos.getFechaInicio());
        expedicion.setFechaFin(datos.getFechaFin());
        expedicion.setEquipos(new ArrayList<>(equipos));

        return expedicionRepository.save(expedicion);
    }

    public void borrarExpedicion(Long id) {
        Expedicion expedicion = expedicionRepository.findById(id)
                .orElseThrow(EntidadNoExisteException::new);
        if (!expedicion.getEquipos().isEmpty()) {
            throw new ExpedicionConEquiposException();
        }
        expedicionRepository.delete(expedicion);
    }

    private List<Equipo> buscarEquiposPorIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        List<Equipo> equipos = equipoRepository.findAllById(ids);

        if (equipos.size() != ids.size()) {
            throw new EntidadNoExisteException();
        }

        return equipos;
    }
}



