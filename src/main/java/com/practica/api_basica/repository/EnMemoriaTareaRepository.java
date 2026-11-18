package com.practica.api_basica.repository;

import com.practica.api_basica.model.Tarea;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementacion en memoria del repositorio de tareas.
 */
@Repository
public class EnMemoriaTareaRepository implements TareaRepository {

    private final List<Tarea> tareas = new CopyOnWriteArrayList<>();
    private final AtomicLong contador = new AtomicLong(1);

    @Override
    public List<Tarea> findAll() {
        return List.copyOf(tareas);
    }

    @Override
    public Optional<Tarea> findById(Long id) {
        return tareas.stream()
                .filter(t -> t.id().equals(id))
                .findFirst();
    }

    @Override
    public Tarea save(Tarea tarea) {
        Long id = tarea.id() == null ? contador.getAndIncrement() : tarea.id();
        Tarea almacenada = new Tarea(id, tarea.titulo(), tarea.completada());

        tareas.removeIf(t -> t.id().equals(id));
        tareas.add(almacenada);

        return almacenada;
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    @Override
    public boolean deleteById(Long id) {
        return tareas.removeIf(t -> t.id().equals(id));
    }
}
