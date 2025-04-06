package es.uma.informatica.sii.pr2025rest.controladores;

import es.uma.informatica.sii.pr2025rest.servicios.ExpedicionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.uma.informatica.sii.pr2025rest.dtos.ExpedicionDTO;
import es.uma.informatica.sii.pr2025rest.dtos.ExpedicionEntradaDTO;
import es.uma.informatica.sii.pr2025rest.entidades.Expedicion;
import es.uma.informatica.sii.pr2025rest.excepciones.EntidadNoExisteException;
import es.uma.informatica.sii.pr2025rest.excepciones.ExpedicionConEquiposException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/expediciones")
public class ExpedicionController {

    private final ExpedicionService expedicionService;

    public ExpedicionController(ExpedicionService expedicionService) {
        this.expedicionService = expedicionService;
    }

    @GetMapping
    public List<ExpedicionDTO> obtenerTodas() {
        return expedicionService.obtenerTodas().stream()
                .map(Mapper::dto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpedicionDTO> obtener(@PathVariable Long id) {
        try {
            Expedicion entidad = expedicionService.obtenerPorId(id);
            return ResponseEntity.ok(Mapper.dto(entidad));
        } catch (EntidadNoExisteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ExpedicionEntradaDTO entrada) {
        try {
            Expedicion creada = expedicionService.crearExpedicion(entrada);
            ExpedicionDTO dto = Mapper.dto(creada);
            return ResponseEntity.created(URI.create("/expediciones/" + dto.getId())).body(dto);
        } catch (EntidadNoExisteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ExpedicionEntradaDTO entrada) {
        try {
            Expedicion actualizada = expedicionService.actualizarExpedicion(id, entrada);
            return ResponseEntity.ok(Mapper.dto(actualizada));
        } catch (EntidadNoExisteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            expedicionService.borrarExpedicion(id);
            return ResponseEntity.noContent().build();
        } catch (EntidadNoExisteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExpedicionConEquiposException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
