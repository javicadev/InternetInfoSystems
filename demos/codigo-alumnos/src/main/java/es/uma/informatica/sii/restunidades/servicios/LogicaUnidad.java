package es.uma.informatica.sii.restunidades.servicios;

import es.uma.informatica.sii.restunidades.entidades.UnidadDocente;
import es.uma.informatica.sii.restunidades.excepciones.UnidadExistenteException;
import es.uma.informatica.sii.restunidades.excepciones.UnidadNoEncontrada;
import es.uma.informatica.sii.restunidades.repositorios.UnidadRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
	
	// TODO
	
}
