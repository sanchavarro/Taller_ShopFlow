# Rol C · Proxy y Factory Method

Responsable: Santiago Luna. Este archivo reúne lo que el rol C aporta a cada sección del README
y sirve como bitácora de las decisiones de diseño.

Ramas y commits:

| Rama | Contenido |
|---|---|
| `rol-c-proxy` | `CatalogoCacheProxy`, `CatalogoProteccionProxy`, `ProductoController`, `ConfiguracionCatalogo` y pruebas de proxy |
| `rol-c-factory-method` | `Notificacion`, `Notificador`, productos/creadores Email y SMS, `NotificacionController` y cambios de integración en `PedidoService` |
| `reto-c-whatsapp` | `NotificacionWhatsApp`, `NotificadorWhatsApp`, `NotificadorWhatsAppTest` y evidencia del reto |
| `rol-c-integracion` | Unión de las tres ramas anteriores con la base del rol A/B |

## 1. Proxy

### Problema

`CatalogoRemoto` simula el catálogo de otro equipo y tarda 1,5 s por llamada. Además, el precio
solo lo debe cambiar un usuario con rol `ADMIN`. Sin patrón, cada consulta paga la latencia, la
caché se dispersa por los servicios y la validación del rol se repite en cada controlador.

### Participantes GoF

| Participante | Clase |
|---|---|
| Subject | `catalogo.ServicioCatalogo` |
| RealSubject | `catalogo.CatalogoRemoto` (no se modifica) |
| Proxy de caché | `catalogo.CatalogoCacheProxy` |
| Proxy de protección | `catalogo.CatalogoProteccionProxy` |
| Client | `catalogo.ProductoController` y `pedidos.PedidoService` |
| Composition root | `catalogo.ConfiguracionCatalogo` |

### Implementación

`CatalogoCacheProxy` guarda un `ConcurrentHashMap<String, Producto>` y solo llama al remoto
cuando el SKU no está en caché:

```java
@Override
public Producto buscar(String sku) {
    if (sku == null || sku.isBlank()) {
        throw new IllegalArgumentException("El SKU es obligatorio");
    }

    String clave = sku.trim();
    Producto enCache = cache.get(clave);
    if (enCache != null) {
        return enCache;
    }

    Producto producto = catalogoReal.buscar(clave);
    cache.put(producto.sku(), producto);
    return producto;
}
```

`actualizarPrecio` actualiza la caché en la misma operación para no servir un precio viejo. Si el
cambio se hace por fuera del proxy, la caché no se entera; es la limitación que se discute en la
pregunta 5 del README.

`CatalogoProteccionProxy` solo permite `actualizarPrecio` al rol `ADMIN`; los demás métodos
delegan:

```java
@Override
public Producto actualizarPrecio(String sku, BigDecimal nuevoPrecio) {
    if (!esAdmin()) {
        throw new AccesoDenegadoException("Solo el rol ADMIN puede cambiar precios");
    }
    return catalogo.actualizarPrecio(sku, nuevoPrecio);
}
```

`ProductoController` expone los tres endpoints y crea el proxy de protección por petición con el
valor de `X-Rol`:

```java
@PutMapping("/{sku}/precio")
public Producto actualizarPrecio(@PathVariable String sku,
        @RequestHeader(value = "X-Rol", required = false) String rol,
        @RequestBody PrecioRequest request) {
    if (request == null || request.precio() == null) {
        throw new IllegalArgumentException("El precio es obligatorio");
    }
    if (request.precio().signum() <= 0) {
        throw new IllegalArgumentException("El precio debe ser mayor que cero");
    }

    ServicioCatalogo conProteccion = new CatalogoProteccionProxy(catalogo, rol);
    return conProteccion.actualizarPrecio(sku, request.precio());
}
```

`ConfiguracionCatalogo` entrega el proxy de caché como único `ServicioCatalogo` de la aplicación:

```java
@Bean
public ServicioCatalogo servicioCatalogo() {
    return new CatalogoCacheProxy(new CatalogoRemoto());
}
```

### Endpoints

| Método | Ruta | Efecto |
|---|---|---|
| GET | `/api/productos` | Lista y llena la caché |
| GET | `/api/productos/{sku}` | Primera consulta ~1,5 s; siguientes < 50 ms |
| PUT | `/api/productos/{sku}/precio` | Requiere `X-Rol: ADMIN`; si no, 403; precio <= 0, 400 |

### Pruebas

- `CatalogoCacheProxyTest`: dos búsquedas del mismo SKU solo llaman una vez al remoto y un
  `actualizarPrecio` deja la caché con el precio nuevo.
- `CatalogoProteccionProxyTest`: `CLIENTE` y sin rol lanzan `AccesoDenegadoException`; `ADMIN`
  actualiza.
- `CatalogoFalso`: catálogo falso que cuenta llamadas, para no depender de la latencia real.

### Evidencia

`evidencias/escenario-1.txt`:

```text
# Primera consulta de SKU-003 (caché fría, esperado ~1.5 s)
1.564754
# Segunda consulta de SKU-003 (esperado < 0.05 s)
0.004809
```

`evidencias/escenario-2.txt` muestra `403` con `X-Rol: CLIENTE`, `200` con `X-Rol: ADMIN` y el
`GET` posterior con `"precio":299.90`.

## 2. Factory Method

### Problema

Al aprobar un pago hay que notificar por el canal elegido. El proceso es siempre el mismo (armar
el mensaje, crear la notificación y enviarla), pero cada canal valida el destino y formatea
distinto. Si `PedidoService` decide con `if` qué clase instanciar, cada canal nuevo obliga a
modificarlo.

### Participantes GoF

| Participante | Clase |
|---|---|
| Product | `notificaciones.Notificacion` |
| ConcreteProduct | `NotificacionEmail`, `NotificacionSms` (y `NotificacionWhatsApp` en el reto C) |
| Creator | `notificaciones.Notificador` |
| ConcreteCreator | `NotificadorEmail`, `NotificadorSms` (y `NotificadorWhatsApp`) |
| Client | `pedidos.PedidoService` |
| Endpoint | `notificaciones.NotificacionController` |

### Implementación

El creador fija el proceso y delega la creación:

```java
public abstract class Notificador {

    public abstract String canal();

    protected abstract Notificacion crearNotificacion();

    public final void notificarPagoAprobado(String pedidoId, String destino, BigDecimal total) {
        String monto = total == null
                ? "0.00"
                : total.setScale(2, RoundingMode.HALF_UP).toPlainString();
        String mensaje = "Pago aprobado del pedido " + pedidoId + " por USD " + monto;
        crearNotificacion().enviar(destino, mensaje);
    }
}
```

`NotificadorEmail` crea `NotificacionEmail`, que exige `@` y antepone un asunto; `NotificadorSms`
crea `NotificacionSms`, que recorta a 160 caracteres. Ambos usan `BandejaNotificaciones`.

`PedidoService` recibe la lista de creadores y los indexa por canal:

```java
public PedidoService(ServicioCatalogo catalogo, ServicioFacturacion facturacion,
        ConfiguracionPagos configuracionPagos, List<Notificador> notificadores) {
    this.catalogo = catalogo;
    this.facturacion = facturacion;
    this.configuracionPagos = configuracionPagos;
    this.notificadores = notificadores.stream()
            .collect(Collectors.toUnmodifiableMap(Notificador::canal, Function.identity()));
}
```

En `crear` valida el canal antes de facturar (400 si no hay creador). En `pagar`, después de
guardar el pedido pagado, envía la notificación. El servicio no contiene `new Notificacion...` ni
un `if` por canal; solo consulta el mapa. Una notificación fallida no cambia el resultado del pago
ya aprobado: se registra en el log.

### Endpoints

| Método | Ruta | Efecto |
|---|---|---|
| POST | `/api/pedidos` | Acepta `canalNotificacion`; un canal sin creador responde 400 |
| POST | `/api/pedidos/{id}/pago` | Si se aprueba, dispara la notificación del canal |
| GET | `/api/notificaciones` | Devuelve canal, destino, mensaje y fecha |

### Pruebas

- `NotificadorEmailTest`: un contacto sin `@` lanza `IllegalArgumentException` y no registra
  nada; un correo válido registra `EMAIL` con asunto y total.
- `NotificadorSmsTest`: el mensaje se recorta a 160 caracteres.
- `NotificadorWhatsAppTest` (reto C): sin indicativo lanza excepción; con `+573001234567` registra
  `WHATSAPP`, el check y el total.

### Evidencia

`evidencias/escenario-8.txt` muestra una entrada de auditoría por cada cobro y una notificación
por cada pago aprobado, con el canal y el total. El extracto de la notificación EMAIL es:

```json
[{"canal":"EMAIL","destino":"carlos.luna@correo.com",
  "mensaje":"Asunto: Pago aprobado | Pago aprobado del pedido PED-1 por USD 101.15",
  "fecha":"..."}]
```

`evidencias/reto-C-canal-invalido.txt` muestra el 400:

```json
{"error":"Canal de notificación no soportado: PALOMA. Canales disponibles: [EMAIL, SMS, WHATSAPP]"}
```

## 3. Reto C · WhatsApp

Se agregaron únicamente archivos nuevos:

- `NotificacionWhatsApp`: exige `^\+[1-9]\d{7,14}$` y antepone `✅` al mensaje.
- `NotificadorWhatsApp`: creador `@Component` que devuelve `NotificacionWhatsApp`.
- `NotificadorWhatsAppTest`: prueba el número inválido y el envío con check y total.

`PedidoService` no se modificó para el reto: Spring inyecta el nuevo `Notificador` en la lista y el
mapa por canal lo encuentra. La evidencia `evidencias/reto-C-git-diff-stat.txt` muestra solo tres
archivos nuevos:

```text
 .../notificaciones/NotificacionWhatsApp.java       | 27 ++++++++++++++++
 .../notificaciones/NotificadorWhatsApp.java        | 24 +++++++++++++
 .../notificaciones/NotificadorWhatsAppTest.java    | 36 ++++++++++++++++++++++
 3 files changed, 87 insertions(+)
```

`evidencias/reto-C-whatsapp.txt` muestra la notificación `WHATSAPP` con el mensaje que empieza por
`✅` y el total del pedido.

## 4. Integración

- `catalogo/ConfiguracionCatalogo`: reemplaza el bean provisional por
  `new CatalogoCacheProxy(new CatalogoRemoto())`. Es la única modificación de configuración del
  proxy.
- `pedidos/PedidoService`: recibe `List<Notificador>`, valida el canal y notifica tras el pago.
  Es la única modificación de un servicio existente.
- No se modificaron `externos.*` ni `catalogo/CatalogoRemoto`.
- El orden de las capas de pago queda a cargo del rol B; el rol C solo consume
  `ConfiguracionPagos.obtener(...)`.

## 5. Preguntas de reflexión del rol C

**Pregunta 4: Proxy vs. Decorator.** `CatalogoCacheProxy` controla si se llega o no al sujeto real
(evita la llamada si está en caché) y `CatalogoProteccionProxy` controla quién puede llegar a
`actualizarPrecio`. `AuditoriaPago` no evita el cobro: agrega una responsabilidad y siempre delega.
La estructura es la misma; la intención es la que cambia.

**Pregunta 5: caché desactualizada.** Si otro sistema cambia el precio en el remoto, la caché
sigue sirviendo el valor anterior. Se propone TTL corto, versión/ETag o invalidación por evento;
para precios exactos, no cachear la lectura o validar la versión antes de usarla.

**Pregunta 8: SOLID.** Proxy: SRP (caché y permisos separados) y LSP (el cliente usa
`ServicioCatalogo` sin distinguir el proxy). Factory Method: OCP (WhatsApp son archivos nuevos) y
DIP (`PedidoService` depende de `Notificador`, no de los productos concretos).

## 6. Cómo reproducir las evidencias

Con la aplicación recién iniciada:

```bash
cd shopflow-base
bash evidencias/rol-c-escenarios.sh
```

El script deja `escenario-1.txt`, `escenario-2.txt`, `escenario-8.txt`,
`reto-C-whatsapp.txt` y `reto-C-canal-invalido.txt`. Ejecútelo antes de cualquier consulta a
`SKU-003` para que la primera medición del escenario 1 tenga la caché fría.
