#!/usr/bin/env bash
# Rol C (Proxy y Factory Method): escenarios 1, 2 y 8 del Anexo B, más el reto C.
# Uso: con la aplicación recién iniciada (mvn spring-boot:run), desde shopflow-base:
#   bash evidencias/rol-c-escenarios.sh
# Deja la salida en evidencias/escenario-1.txt, escenario-2.txt, escenario-8.txt,
# reto-C-whatsapp.txt y reto-C-canal-invalido.txt.
# Nota: ejecute el escenario 1 antes de consultar SKU-003 por otro medio, para que
# la primera medición sea con la caché fría.
set -u
API=${API:-http://localhost:8080}
DIR=$(cd "$(dirname "$0")" && pwd)
JSON='Content-Type: application/json'

{
  echo "# Escenario 1 - Caché del proxy"
  echo "# Primera consulta de SKU-003 (caché fría, esperado ~1.5 s)"
  curl -s -o /dev/null -w "%{time_total}\n" "$API/api/productos/SKU-003"
  echo "# Segunda consulta de SKU-003 (esperado < 0.05 s)"
  curl -s -o /dev/null -w "%{time_total}\n" "$API/api/productos/SKU-003"
} > "$DIR/escenario-1.txt"

{
  echo "# Escenario 2 - Proxy de protección"
  echo "# PUT con X-Rol: CLIENTE (esperado 403)"
  curl -s -i -X PUT "$API/api/productos/SKU-003/precio" -H "X-Rol: CLIENTE" -H "$JSON" \
    -d '{"precio": 299.90}'
  echo; echo
  echo "# PUT con X-Rol: ADMIN (esperado 200)"
  curl -s -i -X PUT "$API/api/productos/SKU-003/precio" -H "X-Rol: ADMIN" -H "$JSON" \
    -d '{"precio": 299.90}'
  echo; echo
  echo "# GET /api/productos/SKU-003 (debe mostrar 299.90)"
  curl -s -i "$API/api/productos/SKU-003"
  echo
} > "$DIR/escenario-2.txt"

PEDIDO_EMAIL=$(curl -s -X POST "$API/api/pedidos" -H "$JSON" -d '{
  "clienteId": "CLI-C-01", "documentoCliente": "1020304050", "pais": "CO",
  "contacto": "carlos.luna@correo.com",
  "items": [ { "sku": "SKU-001", "cantidad": 1 } ],
  "direccionEnvio": "Calle 134 # 9-51, Bogota",
  "canalNotificacion": "EMAIL" }')
ID_EMAIL=$(printf '%s' "$PEDIDO_EMAIL" | sed -n 's/.*"id":"\(PED-[0-9]*\)".*/\1/p')

curl -s -X POST "$API/api/pedidos/$ID_EMAIL/pago" -H "$JSON" \
  -d '{"proveedor": "QUICKPAY"}' > /dev/null

PEDIDO_SMS=$(curl -s -X POST "$API/api/pedidos" -H "$JSON" -d '{
  "clienteId": "CLI-C-02", "documentoCliente": "1020304050", "pais": "CO",
  "contacto": "+573001234567",
  "items": [ { "sku": "SKU-005", "cantidad": 1 } ],
  "direccionEnvio": "Calle 134 # 9-51, Bogota",
  "canalNotificacion": "SMS" }')
ID_SMS=$(printf '%s' "$PEDIDO_SMS" | sed -n 's/.*"id":"\(PED-[0-9]*\)".*/\1/p')

curl -s -X POST "$API/api/pedidos/$ID_SMS/pago" -H "$JSON" \
  -d '{"proveedor": "QUICKPAY"}' > /dev/null

PEDIDO_WHATSAPP=$(curl -s -X POST "$API/api/pedidos" -H "$JSON" -d '{
  "clienteId": "CLI-C-03", "documentoCliente": "1020304050", "pais": "CO",
  "contacto": "+573001234567",
  "items": [ { "sku": "SKU-002", "cantidad": 1 } ],
  "direccionEnvio": "Calle 134 # 9-51, Bogota",
  "canalNotificacion": "WHATSAPP" }')
ID_WHATSAPP=$(printf '%s' "$PEDIDO_WHATSAPP" | sed -n 's/.*"id":"\(PED-[0-9]*\)".*/\1/p')

curl -s -X POST "$API/api/pedidos/$ID_WHATSAPP/pago" -H "$JSON" \
  -d '{"proveedor": "QUICKPAY"}' > /dev/null

{
  echo "# Escenario 8 - Auditoría y notificaciones"
  echo "# Pedidos pagados con QuickPay: $ID_EMAIL (EMAIL), $ID_SMS (SMS), $ID_WHATSAPP (WHATSAPP)"
  echo
  echo "Auditoría:"
  curl -s "$API/api/pagos/auditoria"
  echo; echo
  echo "Notificaciones:"
  curl -s "$API/api/notificaciones"
  echo
} > "$DIR/escenario-8.txt"

{
  echo "# Reto C - Notificación por WhatsApp"
  echo "# Pedido pagado con QuickPay: $ID_WHATSAPP"
  echo
  echo "Notificaciones:"
  curl -s "$API/api/notificaciones"
  echo
} > "$DIR/reto-C-whatsapp.txt"

{
  echo "# Reto C - Canal no soportado"
  echo "# Esperado: 400 con los canales disponibles"
  curl -s -i -X POST "$API/api/pedidos" -H "$JSON" -d '{
    "clienteId": "CLI-C-04", "documentoCliente": "1020304050", "pais": "CO",
    "contacto": "carlos.luna@correo.com",
    "items": [ { "sku": "SKU-005", "cantidad": 1 } ],
    "direccionEnvio": "Calle 134 # 9-51, Bogota",
    "canalNotificacion": "PALOMA" }'
  echo
} > "$DIR/reto-C-canal-invalido.txt"

echo "Listo. Revise escenario-1.txt, escenario-2.txt, escenario-8.txt y reto-C-*.txt"
