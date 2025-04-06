package es.uma.informatica.sii.restunidades.servicios;


import es.uma.informatica.sii.restunidades.controladores.Mapper;
import es.uma.informatica.sii.restunidades.dtos.UnidadDTO;
import es.uma.informatica.sii.restunidades.entidades.UnidadDocente;
import es.uma.informatica.sii.restunidades.excepciones.UnidadExistenteException;
import es.uma.informatica.sii.restunidades.excepciones.UnidadNoEncontrada;
import es.uma.informatica.sii.restunidades.repositorios.UnidadRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LogicaUnidad {
	
	private UnidadRepo repo;
	
	@Autowired
	public LogicaUnidad(UnidadRepo repo) {
		this.repo=repo;
	}
	
	public List<UnidadDocente> getTodasUnidades() {
		return repo.findAll();
	}

    //Para convertir cada UnidadDocente a DTO
    public List<UnidadDTO> obtenerUnidades (){
		List<UnidadDocente> entidades = getTodasUnidades();
		return entidades.stream()
				.map(Mapper::toUnidadDTO)
				.collect(Collectors.toList());
    }



}
