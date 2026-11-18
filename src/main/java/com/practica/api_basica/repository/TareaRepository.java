package com.practica.api_basica.repository;

import com.practica.api_basica.model.Tarea;

import java.util.List;
import java.util.Optional;

/**
 * Abstraccion de persistencia de tareas.
 *
 * La implementacion por defecto guarda las tareas en memoria, pero el servicio
 * solo depende de esta interfaz para poder probarse de forma aislada.
 */
public interface TareaRepository {

    List<Tarea> findAll();

    Optional<Tarea> findById(Long id);

    Tarea save(Tarea tarea);

    boolean existsById(Long id);

    boolean deleteById(Long id);
}
