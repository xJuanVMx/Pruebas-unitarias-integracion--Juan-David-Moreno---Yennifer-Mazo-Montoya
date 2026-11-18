package com.practica.api_basica.service;

import com.practica.api_basica.repository.TareaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias puras de la logica de eliminacion de TareaService.
 *
 * No se levanta el contexto de Spring ni el de Spring MVC: el repositorio se
 * sustituye por un mock de Mockito para verificar unicamente la interaccion
 * entre el servicio y la persistencia.
 */
@ExtendWith(MockitoExtension.class)
class TareaServiceTest {

    @Mock
    private TareaRepository tareaRepository;

    @InjectMocks
    private TareaService tareaService;

    @Test
    @DisplayName("eliminar() borra la tarea existente y devuelve true")
    void eliminarTareaExistenteDevuelveTrue() {
        Long id = 1L;
        when(tareaRepository.existsById(id)).thenReturn(true);

        boolean resultado = tareaService.eliminar(id);

        assertTrue(resultado);

        // Primero comprueba que existe y solo despues la elimina.
        InOrder orden = inOrder(tareaRepository);
        orden.verify(tareaRepository).existsById(id);
        orden.verify(tareaRepository).deleteById(id);
        verifyNoMoreInteractions(tareaRepository);
    }

    @Test
    @DisplayName("eliminar() no borra nada y devuelve false cuando la tarea no existe")
    void eliminarTareaInexistenteDevuelveFalse() {
        Long id = 99L;
        when(tareaRepository.existsById(id)).thenReturn(false);

        boolean resultado = tareaService.eliminar(id);

        assertFalse(resultado);

        verify(tareaRepository).existsById(id);
        verify(tareaRepository, never()).deleteById(anyLong());
        verifyNoMoreInteractions(tareaRepository);
    }

    @Test
    @DisplayName("eliminar() con id nulo devuelve false sin tocar el repositorio")
    void eliminarConIdNuloDevuelveFalseSinConsultarRepositorio() {
        boolean resultado = tareaService.eliminar(null);

        assertFalse(resultado);
        verifyNoInteractions(tareaRepository);
    }
}
