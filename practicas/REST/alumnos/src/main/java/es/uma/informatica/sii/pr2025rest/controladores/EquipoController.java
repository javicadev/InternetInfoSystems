package es.uma.informatica.sii.pr2025rest.controladores;

import es.uma.informatica.sii.pr2025rest.dtos.EquipoDTO;
import es.uma.informatica.sii.pr2025rest.entidades.Equipo;
import es.uma.informatica.sii.pr2025rest.excepciones.EntidadNoExisteException;
import es.uma.informatica.sii.pr2025rest.servicios.EquipoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import java.net.URI;

@RestController
@RequestMapping("/equipos")
public class EquipoController {

    private final EquipoService equipoService;

    public EquipoController(EquipoService equipoService) {
        this.equipoService = equipoService;
    }

    @GetMapping
    public List<EquipoDTO> obtenerTodos() {
        return equipoService.obtenerTodos().stream()
            .map(Mapper::dto)
            .collect(Collectors.toList());
    }

    // TODO

    @GetMapping("/{id}")
    public ResponseEntity<EquipoDTO> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(Mapper.dto(equipoService.obtenerEquipoPorId(id)));
        } catch (EntidadNoExisteException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<EquipoDTO> crearEquipo(@RequestBody EquipoDTO equipoDTO) {
        Equipo creado = equipoService.crearEquipo(Mapper.entidad(equipoDTO));

        EquipoDTO dto = new EquipoDTO();
        dto.setId(creado.getId());
        dto.setNombre(creado.getNombre());
        dto.setEspecialidad(creado.getEspecialidad());

        return ResponseEntity.created(URI.create("/equipos/" + dto.getId())).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipoDTO> actualizar(@PathVariable Long id, @RequestBody EquipoDTO equipoDTO) {
        try {
            Equipo actualizado = equipoService.actualizarEquipo(id, Mapper.entidad(equipoDTO));
            return ResponseEntity.ok(Mapper.dto(actualizado));
        } catch (EntidadNoExisteException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            equipoService.eliminarEquipo(id);
            return ResponseEntity.noContent().build();
        } catch (EntidadNoExisteException e) {
            return ResponseEntity.notFound().build();
        }
    }

}