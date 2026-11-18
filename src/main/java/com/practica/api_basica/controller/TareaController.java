package com.practica.api_basica.controller;

import com.practica.api_basica.model.Tarea;
import com.practica.api_basica.service.TareaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    private final TareaService tareaService;

    public TareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    // GET /api/tareas
    @GetMapping
    public List<Tarea> listar() {
        return tareaService.listar();
    }

    // GET /api/tareas/1
    @GetMapping("/{id}")
    public ResponseEntity<Tarea> obtener(@PathVariable Long id) {
        return tareaService.obtener(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/tareas
    @PostMapping
    public Tarea crear(@RequestBody Tarea nueva) {
        return tareaService.crear(nueva);
    }

    // DELETE /api/tareas/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return tareaService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
