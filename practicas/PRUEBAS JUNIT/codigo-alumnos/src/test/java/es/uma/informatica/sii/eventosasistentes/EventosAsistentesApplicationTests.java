package es.uma.informatica.sii.eventosasistentes;

import org.hibernate.Hibernate;
import es.uma.informatica.sii.eventosasistentes.entities.Asistente;
import es.uma.informatica.sii.eventosasistentes.entities.Evento;
import es.uma.informatica.sii.eventosasistentes.repositories.AsistenteRepo;
import es.uma.informatica.sii.eventosasistentes.repositories.EventoRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import jakarta.transaction.Transactional;



import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("En el servicio de eventos y asistentes")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EventosAsistentesApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private EventoRepo eventoRepository;

    @Autowired
    private AsistenteRepo asistenteRepository;

    @BeforeEach
    public void initializeDatabase() {
        eventoRepository.deleteAll();
        asistenteRepository.deleteAll();
    }

    private URI uri(String scheme, String host, int port, String... paths) {
        UriBuilderFactory ubf = new DefaultUriBuilderFactory();
        UriBuilder ub = ubf.builder()
            .scheme(scheme)
            .host(host).port(port);
        for (String path : paths) {
            ub = ub.path(path);
        }
        return ub.build();
    }

    private RequestEntity<Void> get(String scheme, String host, int port, String path) {
        URI uri = uri(scheme, host, port, path);
        var peticion = RequestEntity.get(uri)
            .accept(MediaType.APPLICATION_JSON)
            .build();
        return peticion;
    }

    private RequestEntity<Void> delete(String scheme, String host, int port, String path) {
        URI uri = uri(scheme, host, port, path);
        var peticion = RequestEntity.delete(uri)
            .build();
        return peticion;
    }

    private <T> RequestEntity<T> post(String scheme, String host, int port, String path, T object) {
        URI uri = uri(scheme, host, port, path);
        var peticion = RequestEntity.post(uri)
            .contentType(MediaType.APPLICATION_JSON)
            .body(object);
        return peticion;
    }

    private <T> RequestEntity<T> put(String scheme, String host, int port, String path, T object) {
        URI uri = uri(scheme, host, port, path);
        var peticion = RequestEntity.put(uri)
            .contentType(MediaType.APPLICATION_JSON)
            .body(object);
        return peticion;
    }


    @Nested
    @DisplayName("cuando no hay eventos")
    public class EventosVacios {

        @Test
        @DisplayName("devuelve la lista de eventos vacía")
        public void devuelveEventos() {
            var peticion = get("http", "localhost", port, "/eventos");

            var respuesta = restTemplate.exchange(peticion,
                new ParameterizedTypeReference<Set<Evento>>() {
                });

            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertThat(respuesta.getBody()).isEmpty();
        }
    }

    @Nested
    @DisplayName("operaciones completas sobre eventos")
    public class OperacionesEventos {

        @Test
        @DisplayName("lanza error al actualizar evento inexistente")
        public void errorActualizarEventoInexistente() {
            Evento fantasma = new Evento();
            fantasma.setId(9999L);
            fantasma.setNombre("GhostEvent");
            fantasma.setInicio(Timestamp.valueOf(LocalDateTime.now()));

            var peticion = put("http", "localhost", port, "/eventos/" + fantasma.getId(), fantasma);
            var respuesta = restTemplate.exchange(peticion, String.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
        }


        @Test
        @DisplayName("puede añadir un evento nuevo correctamente")
        public void puedeAñadirEvento() {
            Evento nuevo = new Evento();
            nuevo.setNombre("Concierto UMA");
            nuevo.setInicio(Timestamp.valueOf(LocalDateTime.of(2025, 6, 10, 20, 0)));

            var peticion = post("http", "localhost", port, "/eventos", nuevo);
            var respuesta = restTemplate.exchange(peticion, Evento.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
            assertThat(respuesta.getBody().getNombre()).isEqualTo("Concierto UMA");
        }

        @Test
        @DisplayName("no permite añadir un evento con nombre duplicado")
        public void errorNombreDuplicadoEvento() {
            Evento evento = new Evento();
            evento.setNombre("Jornada puertas abiertas");
            evento.setInicio(Timestamp.valueOf(LocalDateTime.of(2025, 5, 5, 12, 0)));
            eventoRepository.save(evento);

            Evento duplicado = new Evento();
            duplicado.setNombre("Jornada puertas abiertas");
            duplicado.setInicio(Timestamp.valueOf(LocalDateTime.of(2025, 6, 10, 20, 0)));

            var peticion = post("http", "localhost", port, "/eventos", duplicado);
            var respuesta = restTemplate.exchange(peticion, String.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
        }

        @Test
        @DisplayName("devuelve un evento por ID correctamente")
        public void obtenerEventoPorId() {
            Evento evento = new Evento();
            evento.setNombre("Seminario AI");
            evento.setInicio(Timestamp.valueOf(LocalDateTime.of(2025, 4, 25, 9, 0)));
            Evento guardado = eventoRepository.save(evento);

            var peticion = get("http", "localhost", port, "/eventos/" + guardado.getId());
            var respuesta = restTemplate.exchange(peticion, Evento.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertThat(respuesta.getBody().getNombre()).isEqualTo("Seminario AI");
        }

        @Test
        @DisplayName("devuelve error al buscar un evento inexistente")
        public void errorEventoNoExiste() {
            var peticion = get("http", "localhost", port, "/eventos/9999");
            var respuesta = restTemplate.exchange(peticion, String.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
        }

        @Test
        @DisplayName("modifica el nombre de un evento correctamente")
        public void modificaEventoCorrectamente() {
            Evento evento = new Evento();
            evento.setNombre("Taller Web");
            evento.setInicio(Timestamp.valueOf(LocalDateTime.of(2025, 5, 10, 10, 0)));
            Evento guardado = eventoRepository.save(evento);

            guardado.setNombre("Taller Java");

            var peticion = put("http", "localhost", port, "/eventos/" + guardado.getId(), guardado);
            var respuesta = restTemplate.exchange(peticion, Evento.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertThat(respuesta.getBody().getNombre()).isEqualTo("Taller Java");
        }

        @Test
        @DisplayName("devuelve error al modificar un evento inexistente")
        public void errorModificarEventoNoExiste() {
            Evento evento = new Evento();
            evento.setId(1234L);
            evento.setNombre("Fantasma");
            evento.setInicio(Timestamp.valueOf(LocalDateTime.now()));

            var peticion = put("http", "localhost", port, "/eventos/1234", evento);
            var respuesta = restTemplate.exchange(peticion, String.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
        }

        @Test
        @DisplayName("devuelve error si se modifica un evento con nombre ya usado")
        public void errorModificarConNombreDuplicado() {
            Evento evento1 = new Evento();
            evento1.setNombre("NombreOriginal");
            evento1.setInicio(Timestamp.valueOf(LocalDateTime.now()));
            evento1 = eventoRepository.save(evento1);

            Evento evento2 = new Evento();
            evento2.setNombre("NombreDuplicado");
            evento2.setInicio(Timestamp.valueOf(LocalDateTime.now()));
            evento2 = eventoRepository.save(evento2);

            evento2.setNombre("NombreOriginal");

            var peticion = put("http", "localhost", port, "/eventos/" + evento2.getId(), evento2);
            var respuesta = restTemplate.exchange(peticion, String.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
        }

        @Test
        @DisplayName("elimina un evento correctamente")
        public void eliminarEventoCorrectamente() {

            Evento evento = new Evento();
            evento.setNombre("Eliminarme");
            evento.setInicio(Timestamp.valueOf(LocalDateTime.now()));

            Evento guardado = eventoRepository.saveAndFlush(evento);

            // QUIERO VER QUE SE GUARDA

            System.out.println("Evento guardado con ID: " + guardado.getId());

            var peticion = delete("http", "localhost", port, "/eventos/" + guardado.getId());
            var respuesta = restTemplate.exchange(peticion, String.class);


// BUSCANDO ERRORES
            System.out.println(" DELETE respuesta: " + respuesta.getStatusCode());

            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertThat(eventoRepository.findById(guardado.getId())).isEmpty();
        }


        @Test
        @DisplayName("devuelve error al eliminar un evento inexistente")
        public void errorEliminarEventoNoExiste() {
            var peticion = delete("http", "localhost", port, "/eventos/12345");
            var respuesta = restTemplate.exchange(peticion, String.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
        }


    }

    @Nested
    @DisplayName("asignación de asistentes a eventos")
    public class AsistentesEventos {

        @Test
        @DisplayName("asigna asistentes válidos a un evento correctamente")
        public void asignaAsistentesEvento() {
            Evento evento = new Evento();
            evento.setNombre("Bootcamp");
            evento.setInicio(Timestamp.valueOf(LocalDateTime.now()));
            evento = eventoRepository.save(evento);

            Asistente asistente1 = new Asistente();
            asistente1.setNombre("Juan");
            asistente1.setDni("12345678A");
            asistente1 = asistenteRepository.save(asistente1);

            Asistente asistente2 = new Asistente();
            asistente2.setNombre("Ana");
            asistente2.setDni("87654321B");
            asistente2 = asistenteRepository.save(asistente2);

            var uri = UriComponentsBuilder
                    .fromHttpUrl("http://localhost:" + port + "/eventos/" + evento.getId() + "/asistentes")
                    .queryParam("idAsistentes", asistente1.getId())
                    .queryParam("idAsistentes", asistente2.getId())
                    .build().toUri();

            var peticion = RequestEntity.put(uri).build();
            var respuesta = restTemplate.exchange(peticion, new ParameterizedTypeReference<List<Asistente>>() {});

            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertThat(respuesta.getBody()).extracting("dni")
                    .containsExactlyInAnyOrder("12345678A", "87654321B");
        }



        @Test
        @DisplayName("lanza error si se asignan asistentes a un evento inexistente")
        public void errorAsignarAsistentesEventoInexistente() {
            Asistente asistente = new Asistente();
            asistente.setNombre("Pepe");
            asistente.setDni("12345678Z");
            asistente = asistenteRepository.save(asistente);

            var uri = uri("http", "localhost", port, "eventos", "9999", "asistentes") + "?idAsistentes=" + asistente.getId();
            var peticion = RequestEntity.put(URI.create(uri)).build();
            var respuesta = restTemplate.exchange(peticion, String.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
        }

        @Test
        @DisplayName("lanza error si se asigna un asistente inexistente")
        public void errorAsignarAsistenteInexistente() {
            Evento evento = new Evento();
            evento.setNombre("Workshop");
            evento.setInicio(Timestamp.valueOf(LocalDateTime.now()));
            evento = eventoRepository.save(evento);

            var uri = uri("http", "localhost", port, "eventos", evento.getId().toString(), "asistentes") + "?idAsistentes=9999";
            var peticion = RequestEntity.put(URI.create(uri)).build();
            var respuesta = restTemplate.exchange(peticion, String.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
        }

        @Test
        @DisplayName("lanza error si se eliminan asistentes de un evento inexistente")
        public void errorEliminarAsistentesEventoInexistente() {
            var uri = uri("http", "localhost", port, "eventos", "8888", "asistentes") + "?idAsistentes=1";
            var peticion = RequestEntity.delete(URI.create(uri)).build();
            var respuesta = restTemplate.exchange(peticion, String.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
        }

        @Test
        @DisplayName("lanza error si se elimina un asistente inexistente")
        public void errorEliminarAsistenteInexistente() {
            Evento evento = new Evento();
            evento.setNombre("EventoPrueba");
            evento.setInicio(Timestamp.valueOf(LocalDateTime.now()));
            evento = eventoRepository.save(evento);

            var uri = uri("http", "localhost", port, "eventos", evento.getId().toString(), "asistentes") + "?idAsistentes=9999";
            var peticion = RequestEntity.delete(URI.create(uri)).build();
            var respuesta = restTemplate.exchange(peticion, String.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
        }

    }

    @Nested
    @DisplayName("gestión de asistentes")
    public class GestionAsistentes {

        @Test
        @DisplayName("lanza error al eliminar dos veces el mismo asistente")
        public void errorEliminarAsistenteDosVeces() {
            Asistente asistente = new Asistente();
            asistente.setNombre("BorrarDosVeces");
            asistente.setDni("BORX123");
            asistente = asistenteRepository.save(asistente);

            var peticion1 = delete("http", "localhost", port, "/asistentes/" + asistente.getId());
            var respuesta1 = restTemplate.exchange(peticion1, String.class);
            assertThat(respuesta1.getStatusCode().value()).isEqualTo(200);

            var peticion2 = delete("http", "localhost", port, "/asistentes/" + asistente.getId());
            var respuesta2 = restTemplate.exchange(peticion2, String.class);
            assertThat(respuesta2.getStatusCode().value()).isEqualTo(404); // ya no existe
        }

        @Test
        @DisplayName("lanza error al actualizar asistente inexistente")
        public void errorActualizarAsistenteInexistente() {
            Asistente fantasma = new Asistente();
            fantasma.setId(9999L); // ID que no existe
            fantasma.setNombre("Ghost");
            fantasma.setDni("NO_EXISTE");

            var peticion = put("http", "localhost", port, "/asistentes/" + fantasma.getId(), fantasma);
            var respuesta = restTemplate.exchange(peticion, String.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
        }


        @Test
        @DisplayName("lanza error al modificar asistente con DNI duplicado")
        public void errorModificarAsistenteDuplicado() {
            var a1 = new Asistente();
            a1.setNombre("Uno");
            a1.setDni("123A");
            a1 = asistenteRepository.save(a1);

            var a2 = new Asistente();
            a2.setNombre("Dos");
            a2.setDni("456B");
            a2 = asistenteRepository.save(a2);

            a2.setDni("123A");

            var peticion = put("http", "localhost", port, "/asistentes/" + a2.getId(), a2);
            var respuesta = restTemplate.exchange(peticion, String.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
        }

        @Test
        @DisplayName("lanza error si se asignan asistentes a un evento inexistente")
        public void errorAsignarAsistentesEventoInexistente() {
            Asistente asistente = new Asistente();
            asistente.setNombre("Test");
            asistente.setDni("99999999A");
            asistente = asistenteRepository.save(asistente);

            var uri = UriComponentsBuilder
                    .fromHttpUrl("http://localhost:" + port + "/eventos/9999/asistentes")
                    .queryParam("idAsistentes", asistente.getId())
                    .build().toUri();

            var peticion = RequestEntity.put(uri).build();
            var respuesta = restTemplate.exchange(peticion, String.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
        }

        @Test
        @DisplayName("lanza error al crear un asistente con DNI duplicado")
        public void errorCrearAsistenteDuplicado() {
            Asistente original = new Asistente();
            original.setNombre("Ana");
            original.setDni("AABB1122");
            asistenteRepository.save(original);

            Asistente duplicado = new Asistente();
            duplicado.setNombre("Copia");
            duplicado.setDni("AABB1122");

            var peticion = post("http", "localhost", port, "/asistentes", duplicado);
            var respuesta = restTemplate.exchange(peticion, String.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
        }

        @Test
        @DisplayName("puede crear un nuevo asistente correctamente")
        public void crearAsistenteCorrectamente() {
            Asistente nuevo = new Asistente();
            nuevo.setNombre("Carlos");
            nuevo.setDni("11223344X");

            var peticion = post("http", "localhost", port, "/asistentes", nuevo);
            var respuesta = restTemplate.exchange(peticion, Asistente.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
            assertThat(respuesta.getBody().getDni()).isEqualTo("11223344X");
        }

        @Test
        @DisplayName("lanza error al crear un asistente con DNI duplicado")
        public void errorCrearAsistenteDniDuplicado() {
            Asistente asistente = new Asistente();
            asistente.setNombre("Luis");
            asistente.setDni("99887766Y");
            asistenteRepository.save(asistente);

            Asistente duplicado = new Asistente();
            duplicado.setNombre("Mario");
            duplicado.setDni("99887766Y");

            var peticion = post("http", "localhost", port, "/asistentes", duplicado);
            var respuesta = restTemplate.exchange(peticion, String.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
        }

        @Test
        @DisplayName("puede recuperar un asistente por ID")
        public void obtenerAsistentePorId() {
            Asistente asistente = new Asistente();
            asistente.setNombre("Lucía");
            asistente.setDni("11224455M");
            Asistente guardado = asistenteRepository.save(asistente);

            var peticion = get("http", "localhost", port, "/asistentes/" + guardado.getId());
            var respuesta = restTemplate.exchange(peticion, Asistente.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertThat(respuesta.getBody().getDni()).isEqualTo("11224455M");
        }

        @Test
        @DisplayName("lanza error si el asistente no existe")
        public void errorAsistenteNoExiste() {
            var peticion = get("http", "localhost", port, "/asistentes/9999");
            var respuesta = restTemplate.exchange(peticion, String.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
        }

        @Test
        @DisplayName("puede modificar un asistente correctamente")
        public void modificarAsistenteCorrectamente() {
            Asistente asistente = new Asistente();
            asistente.setNombre("Pedro");
            asistente.setDni("12312312P");
            asistente = asistenteRepository.save(asistente);

            asistente.setDni("32132132Q");

            var peticion = put("http", "localhost", port, "/asistentes/" + asistente.getId(), asistente);
            var respuesta = restTemplate.exchange(peticion, Asistente.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertThat(respuesta.getBody().getDni()).isEqualTo("32132132Q");
        }

        @Test
        @DisplayName("lanza error al modificar asistente inexistente")
        public void errorModificarAsistenteInexistente() {
            Asistente asistente = new Asistente();
            asistente.setId(1234L);
            asistente.setNombre("Fantasma");
            asistente.setDni("99999999Z");

            var peticion = put("http", "localhost", port, "/asistentes/1234", asistente);
            var respuesta = restTemplate.exchange(peticion, String.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
        }

        @Test
        @DisplayName("lanza error si el nuevo DNI ya está en uso")
        public void errorModificarAsistenteDniDuplicado() {
            Asistente a1 = new Asistente();
            a1.setNombre("Uno");
            a1.setDni("DNI1");
            a1 = asistenteRepository.save(a1);

            Asistente a2 = new Asistente();
            a2.setNombre("Dos");
            a2.setDni("DNI2");
            a2 = asistenteRepository.save(a2);

            a2.setDni("DNI1");

            var peticion = put("http", "localhost", port, "/asistentes/" + a2.getId(), a2);
            var respuesta = restTemplate.exchange(peticion, String.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
        }

        @Test
        @DisplayName("puede eliminar un asistente correctamente")
        public void eliminarAsistenteCorrectamente() {
            Asistente asistente = new Asistente();
            asistente.setNombre("Borrar");
            asistente.setDni("BOR1234");
            asistente = asistenteRepository.save(asistente);

            var peticion = delete("http", "localhost", port, "/asistentes/" + asistente.getId());
            var respuesta = restTemplate.exchange(peticion, String.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertThat(asistenteRepository.findById(asistente.getId())).isEmpty();
        }

        @Test
        @DisplayName("lanza error al eliminar asistente inexistente")
        public void errorEliminarAsistenteInexistente() {
            var peticion = delete("http", "localhost", port, "/asistentes/9999");
            var respuesta = restTemplate.exchange(peticion, String.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
        }
    }
}