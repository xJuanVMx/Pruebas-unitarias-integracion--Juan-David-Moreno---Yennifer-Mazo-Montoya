package com.practica.api_basica.service;

import com.practica.api_basica.model.Tarea;
import com.practica.api_basica.repository.TareaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Logica de negocio de las tareas.
 */
@Service
public class TareaService {

    private final TareaRepository tareaRepository;

    public TareaService(TareaRepository tareaRepository) {
        this.tareaRepository = tareaRepository;
    }

    public List<Tarea> listar() {
        return tareaRepository.findAll();
    }

    public Optional<Tarea> obtener(Long id) {
        return tareaRepository.findById(id);
    }

    public Tarea crear(Tarea nueva) {
        return tareaRepository.save(new Tarea(null, nueva.titulo(), nueva.completada()));
    }

    /**
     * Elimina una tarea por su id.
     *
     * @return {@code true} si la tarea existia y fue eliminada; {@code false}
     *         si el id es nulo o la tarea no existe.
     */
    public boolean eliminar(Long id) {
        if (id == null || !tareaRepository.existsById(id)) {
            return false;
        }

        tareaRepository.deleteById(id);
        return true;
    }
}
