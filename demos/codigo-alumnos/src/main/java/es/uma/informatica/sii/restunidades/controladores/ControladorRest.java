package es.uma.informatica.sii.restunidades.controladores;


import es.uma.informatica.sii.restunidades.dtos.UnidadDTO;
import es.uma.informatica.sii.restunidades.dtos.UnidadNuevaDTO;
import es.uma.informatica.sii.restunidades.entidades.UnidadDocente;
import es.uma.informatica.sii.restunidades.excepciones.UnidadExistenteException;
import es.uma.informatica.sii.restunidades.excepciones.UnidadNoEncontrada;
import es.uma.informatica.sii.restunidades.servicios.LogicaUnidad;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/unidad")
public class ControladorRest {
	private LogicaUnidad servicio;

	public ControladorRest(LogicaUnidad servicioUnidad) {
		servicio = servicioUnidad;
	}

	@GetMapping
	public ResponseEntity<List<UnidadDTO>> listaDeUnidades() {
		return ResponseEntity.ok(servicio.getTodasUnidades().stream()
			.map(Mapper::toUnidadDTO)
			.toList());
	}

	@PostMapping
	public ResponseEntity<UnidadDTO> aniadirUnidad(@RequestBody UnidadNuevaDTO unidad, UriComponentsBuilder builder) {
		// TODO
		UnidadDTO unidadDTO = null;
		URI uri = builder
				.path("/api")
				.path("/v1")
				.path("/unidad")
				.path(String.format("/%d", unidadDTO.getId()))
				.build()
				.toUri();
		return null;
	}

	// TODO
}
