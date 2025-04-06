package es.uma.informatica.sii.pr2025rest.servicios;


import es.uma.informatica.sii.pr2025rest.entidades.Equipo;
import es.uma.informatica.sii.pr2025rest.entidades.Expedicion;
import es.uma.informatica.sii.pr2025rest.excepciones.EntidadNoExisteException;
import es.uma.informatica.sii.pr2025rest.repositorios.EquipoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.net.URI;

@Service
public class EquipoService {
    private final EquipoRepository equipoRepository;

    public EquipoService(EquipoRepository equipoRepository) {
        this.equipoRepository = equipoRepository;
    }

    public List<Equipo> obtenerTodos() {
        return equipoRepository.findAll();
    }

    public Equipo obtenerEquipoPorId(Long id) throws EntidadNoExisteException {
        return equipoRepository.findById(id)
                .orElseThrow(EntidadNoExisteException::new);
    }

    public Equipo crearEquipo(Equipo equipo) {
        return equipoRepository.save(equipo);
    }

    public Equipo actualizarEquipo(Long id, Equipo datosActualizados) throws EntidadNoExisteException {
        Equipo existente = obtenerEquipoPorId(id);
        existente.setNombre(datosActualizados.getNombre());
        existente.setEspecialidad(datosActualizados.getEspecialidad());
        return equipoRepository.save(existente);
    }

    public void eliminarEquipo(Long id){
        Equipo e = equipoRepository.findById(id)
                .orElseThrow(EntidadNoExisteException::new);

        for (Expedicion expedicion : e.getExpediciones()) {
            expedicion.getEquipos().remove(e);
        }
        e.getExpediciones().clear();
        equipoRepository.save(e);
        equipoRepository.delete(e);
    }

}