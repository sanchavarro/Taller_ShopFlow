package co.edu.unbosque.shopflow.catalogo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Catálogo falso que cuenta las llamadas, para probar los proxies sin latencia. */
final class CatalogoFalso implements ServicioCatalogo {

    private final Map<String, Producto> productos = new LinkedHashMap<>();
    private int busquedas;
    private int actualizaciones;

    CatalogoFalso() {
        productos.put("SKU-003", new Producto("SKU-003", "Monitor 27 pulgadas", new BigDecimal("320.00")));
    }

    @Override
    public Producto buscar(String sku) {
        busquedas++;
        Producto producto = productos.get(sku);
        if (producto == null) {
            throw new java.util.NoSuchElementException("No existe el producto " + sku);
        }
        return producto;
    }

    @Override
    public List<Producto> listar() {
        return new ArrayList<>(productos.values());
    }

    @Override
    public Producto actualizarPrecio(String sku, BigDecimal nuevoPrecio) {
        actualizaciones++;
        Producto actualizado = buscarSinContar(sku).conPrecio(nuevoPrecio);
        productos.put(sku, actualizado);
        return actualizado;
    }

    int busquedas() {
        return busquedas;
    }

    int actualizaciones() {
        return actualizaciones;
    }

    private Producto buscarSinContar(String sku) {
        Producto producto = productos.get(sku);
        if (producto == null) {
            throw new java.util.NoSuchElementException("No existe el producto " + sku);
        }
        return producto;
    }
}
