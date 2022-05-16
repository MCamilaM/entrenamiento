package co.com.sofkau.entrenamento.curso;

import co.com.sofka.business.generic.UseCaseHandler;
import co.com.sofka.business.repository.DomainEventRepository;
import co.com.sofka.business.support.RequestCommand;
import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofkau.entrenamiento.curso.commands.AgregarDirectrizAMentoria;
import co.com.sofkau.entrenamiento.curso.events.CursoCreado;
import co.com.sofkau.entrenamiento.curso.events.DirectrizAgregadaAMentoria;
import co.com.sofkau.entrenamiento.curso.events.MentoriaCreada;
import co.com.sofkau.entrenamiento.curso.values.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarDirectrizAMentoriaUseCaseTest {

    @InjectMocks
    private AgregarDirectrizAMentoriaUseCase useCase;

    @Mock
    private DomainEventRepository repository;

    @Test
    public void agregarDirectrizAMentoriaHappyPass() {
        //arrange
        CursoId cursoId = CursoId.of("curso1");
        MentoriaId mentoriaId = MentoriaId.of("mentoria1");
        Directriz directriz = new Directriz("directriz 1");
        var command = new AgregarDirectrizAMentoria(cursoId,mentoriaId,directriz);

        when(repository.getEventsBy("curso1")).thenReturn(history());
        useCase.addRepository(repository);

        //act
        var events = UseCaseHandler.getInstance()
                .setIdentifyExecutor(command.getCursoId().value())
                .syncExecutor(useCase, new RequestCommand<>(command))
                .orElseThrow()
                .getDomainEvents();

        //assert
        var event = (DirectrizAgregadaAMentoria)events.get(0);
        Assertions.assertEquals("directriz 1", event.getDirectiz().value());

    }


    private List<DomainEvent> history() {
        Nombre nombreCurso = new Nombre("DDD");
        Descripcion descripcion = new Descripcion("directriz caso de uso");
        var event = new CursoCreado(
                nombreCurso,
                descripcion
        );

        MentoriaId mentoriaId = MentoriaId.of("mentoria1");
        Nombre nombreMentoria  = new Nombre("nombre mentoria");
        Fecha fecha = new Fecha(LocalDateTime.now(), LocalDate.now());
        var eventMentoria = new MentoriaCreada(mentoriaId,nombreMentoria,fecha);

        event.setAggregateRootId("3D");
        return List.of(event, eventMentoria);
    }
}