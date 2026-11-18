# Pruebas del proyecto

Este documento explica cómo funcionan las pruebas implementadas para la eliminación de tareas y qué garantiza cada una.

## 1. Pruebas unitarias puras de `TareaService`

**Archivo:** `src/test/java/com/practica/api_basica/service/TareaServiceTest.java`

Estas pruebas verifican únicamente la lógica de negocio de `TareaService`, sin levantar el contexto de Spring ni el de Spring MVC. Para lograrlo, el repositorio real se reemplaza por un mock de Mockito.

### Anotaciones utilizadas

```java
@ExtendWith(MockitoExtension.class)
class TareaServiceTest {
```

- `@ExtendWith(MockitoExtension.class)`: activa la integración de Mockito con JUnit 5. Gracias a esto, Mockito crea los mocks y los inyecta automáticamente antes de cada prueba.
- `@Mock`: crea una implementación falsa de `TareaRepository`.
- `@InjectMocks`: crea una instancia real de `TareaService` e inyecta en ella el mock anterior mediante su constructor.

```java
@Mock
private TareaRepository tareaRepository;

@InjectMocks
private TareaService tareaService;
```

De esta forma, el servicio se ejecuta con su código real, pero la persistencia está controlada por la prueba.

### Método bajo prueba

```java
public boolean eliminar(Long id) {
    if (id == null || !tareaRepository.existsById(id)) {
        return false;
    }

    tareaRepository.deleteById(id);
    return true;
}
```

El servicio devuelve `true` solo cuando la tarea existe y fue eliminada. Devuelve `false` cuando el id es nulo o cuando la tarea no existe.

### Casos de prueba

| Caso | Preparación del mock | Resultado esperado | Verificación |
|---|---|---|---|
| La tarea existe | `existsById(id)` devuelve `true` | `eliminar(id)` devuelve `true` | Se llama `existsById` y después `deleteById`, en ese orden |
| La tarea no existe | `existsById(id)` devuelve `false` | `eliminar(id)` devuelve `false` | Se consulta `existsById`, pero nunca se llama `deleteById` |
| El id es nulo | No se configura el mock | `eliminar(null)` devuelve `false` | No hay ninguna interacción con el repositorio |

### Explicación de cada caso

#### Tarea existente

```java
when(tareaRepository.existsById(id)).thenReturn(true);

boolean resultado = tareaService.eliminar(id);
```

`when(...).thenReturn(...)` indica qué debe responder el mock. Luego se comprueba que el servicio haya consultado la existencia y haya eliminado la tarea:

```java
InOrder orden = inOrder(tareaRepository);
orden.verify(tareaRepository).existsById(id);
orden.verify(tareaRepository).deleteById(id);
```

`InOrder` valida el orden de las interacciones: primero se verifica la existencia y solo después se elimina.

#### Tarea inexistente

```java
when(tareaRepository.existsById(id)).thenReturn(false);
```

El servicio debe cortar el flujo y no intentar eliminar nada. Esto se comprueba con:

```java
verify(tareaRepository, never()).deleteById(anyLong());
```

`never()` confirma que el método no fue invocado.

#### Id nulo

```java
boolean resultado = tareaService.eliminar(null);
```

El servicio valida el id antes de consultar el repositorio. Por eso la prueba verifica que no hubo ninguna interacción:

```java
verifyNoInteractions(tareaRepository);
```

### ¿Por qué son pruebas unitarias puras?

- No usan `@SpringBootTest`.
- No usan `@WebMvcTest`.
- No inician un servidor ni una base de datos.
- No dependen del contexto de Spring.
- Solo prueban la clase `TareaService` y su colaboración con `TareaRepository`.

Esto hace que las pruebas sean rápidas, deterministas y aisladas: si falla la lógica del servicio, la prueba falla; si falla la base de datos o el controlador, estas pruebas no se ven afectadas.

## 2. Pruebas del endpoint DELETE

**Archivo:** `src/test/java/com/practica/api_basica/controller/TareaControllerTest.java`

Estas pruebas validan el comportamiento HTTP del controlador mediante `@WebMvcTest`, es decir, levantan únicamente la capa web de Spring MVC con `MockMvc`.

Se importan explícitamente el servicio y el repositorio en memoria:

```java
@WebMvcTest(TareaController.class)
@Import({TareaService.class, EnMemoriaTareaRepository.class})
```

Casos cubiertos:

1. Eliminar una tarea existente responde `204 No Content` y la tarea deja de aparecer en el listado.
2. Eliminar una tarea inexistente responde `404 Not Found` y no modifica el listado.
3. Eliminar dos veces la misma tarea responde `204` la primera vez y `404` la segunda.

Estas pruebas complementan a las unitarias: las de servicio verifican la lógica interna y las del controlador verifican el contrato HTTP.

## 3. Ejecución

Desde la raíz del proyecto:

```bash
./mvnw test
```

En Windows:

```powershell
.\mvnw.cmd test
```

Resultado esperado:

```text
Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Distribución actual:

- `ApiBasicaApplicationTests`: 1 prueba de carga del contexto.
- `TareaControllerTest`: 3 pruebas del endpoint HTTP.
- `TareaServiceTest`: 3 pruebas unitarias puras del servicio.
