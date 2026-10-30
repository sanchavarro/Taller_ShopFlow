#!/usr/bin/env bash
# Rol A (Builder y Abstract Factory): escenarios 3, 4 y 5 del Anexo B y verificación del reto A (Perú).
# Uso: con la aplicación recién iniciada (mvn spring-boot:run), desde la carpeta shopflow-base:
#   bash evidencias/rol-a-escenarios.sh
# Deja la salida en evidencias/escenario-3.txt, escenario-4.txt, escenario-5.txt y reto-A-peru.txt.
# Nota: ejecútelo primero, para que el pedido de Colombia quede como PED-1 (lo usa el escenario 6).
set -u
API=${API:-http://localhost:8080}
DIR=$(cd "$(dirname "$0")" && pwd)
JSON='Content-Type: application/json'

{
  echo "# Escenario 3 · Pedido de Colombia válido (ejemplo B.2)"
  echo "# Esperado: 201, factura DIAN, IVA 19 %, descuento del cupón BOSQUE10"
  curl -s -i -X POST "$API/api/pedidos" -H "$JSON" -d @"$DIR/pedido-co.json"
  echo; echo
  echo "# GET /api/pedidos/PED-1"
  curl -s -i "$API/api/pedidos/PED-1"
  echo
} > "$DIR/escenario-3.txt"

{
  echo "# Escenario 4a · Pedido sin ítems"
  echo "# Esperado: 400 «El pedido debe tener al menos un ítem»"
  curl -s -i -X POST "$API/api/pedidos" -H "$JSON" -d '{
    "clienteId": "CLI-002", "documentoCliente": "1020304050", "pais": "CO",
    "contacto": "ana.perez@correo.com", "items": [], "direccionEnvio": "Calle 134 # 9-51, Bogotá" }'
  echo; echo
  echo "# Escenario 4b · Recoger en tienda con envío express"
  echo "# Esperado: 400 «Un pedido para recoger en tienda no puede tener envío express»"
  curl -s -i -X POST "$API/api/pedidos" -H "$JSON" -d '{
    "clienteId": "CLI-003", "documentoCliente": "1020304050", "pais": "CO",
    "contacto": "ana.perez@correo.com", "items": [ { "sku": "SKU-005", "cantidad": 1 } ],
    "recogerEnTienda": true, "envioExpress": true }'
  echo; echo
  echo "# Escenario 4c · Domicilio sin dirección"
  echo "# Esperado: 400 «La dirección de envío es obligatoria si el pedido no se recoge en tienda»"
  curl -s -i -X POST "$API/api/pedidos" -H "$JSON" -d '{
    "clienteId": "CLI-004", "documentoCliente": "1020304050", "pais": "CO",
    "contacto": "ana.perez@correo.com", "items": [ { "sku": "SKU-005", "cantidad": 1 } ] }'
  echo
} > "$DIR/escenario-4.txt"

{
  echo "# Escenario 5a · Pedido de México con RFC GODE561231GR8"
  echo "# Esperado: 201 con CFDI (SAT) e IVA 16 %"
  curl -s -i -X POST "$API/api/pedidos" -H "$JSON" -d '{
    "clienteId": "CLI-MX-01", "documentoCliente": "GODE561231GR8", "pais": "MX",
    "contacto": "+525512345678", "items": [ { "sku": "SKU-001", "cantidad": 1 } ],
    "direccionEnvio": "Av. Paseo de la Reforma 222, CDMX", "canalNotificacion": "SMS" }'
  echo; echo
  echo "# Escenario 5b · Pedido de México con la cédula colombiana 1020304050"
  echo "# Esperado: 400 (documento inválido para México)"
  curl -s -i -X POST "$API/api/pedidos" -H "$JSON" -d '{
    "clienteId": "CLI-MX-02", "documentoCliente": "1020304050", "pais": "MX",
    "contacto": "+525512345678", "items": [ { "sku": "SKU-001", "cantidad": 1 } ],
    "direccionEnvio": "Av. Paseo de la Reforma 222, CDMX" }'
  echo
} > "$DIR/escenario-5.txt"

{
  echo "# Reto A · Pedido de Perú con RUC 20123456789"
  echo "# Esperado: 201 con «Factura electrónica (SUNAT)» e IGV 18 %"
  curl -s -i -X POST "$API/api/pedidos" -H "$JSON" -d '{
    "clienteId": "CLI-PE-01", "documentoCliente": "20123456789", "pais": "PE",
    "contacto": "luis.quispe@correo.pe", "items": [ { "sku": "SKU-003", "cantidad": 1 } ],
    "direccionEnvio": "Av. Larco 1301, Miraflores, Lima" }'
  echo; echo
  echo "# Reto A · Pedido de Perú con cédula colombiana (esperado: 400)"
  curl -s -i -X POST "$API/api/pedidos" -H "$JSON" -d '{
    "clienteId": "CLI-PE-02", "documentoCliente": "1020304050", "pais": "PE",
    "contacto": "luis.quispe@correo.pe", "items": [ { "sku": "SKU-003", "cantidad": 1 } ],
    "direccionEnvio": "Av. Larco 1301, Miraflores, Lima" }'
  echo
} > "$DIR/reto-A-peru.txt"

echo "Listo. Revise evidencias/escenario-3.txt, escenario-4.txt, escenario-5.txt y reto-A-peru.txt"
