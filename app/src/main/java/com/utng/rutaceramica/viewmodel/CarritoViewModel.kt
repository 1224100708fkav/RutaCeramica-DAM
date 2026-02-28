package com.utng.rutaceramica.viewmodel

import androidx.lifecycle.ViewModel
import com.utng.rutaceramica.data.model.CarritoItem
import com.utng.rutaceramica.data.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel que gestiona el estado global del carrito de compras/apartados.
 * Permite agregar, eliminar y modificar la cantidad de productos seleccionados por el turista.
 */
class CarritoViewModel : ViewModel() {

    // Estado interno del carrito
    private val _items = MutableStateFlow<List<CarritoItem>>(emptyList())
    
    /**
     * Lista de ítems actualmente en el carrito.
     */
    val items: StateFlow<List<CarritoItem>> = _items.asStateFlow()

    /**
     * Agrega un producto al carrito con la cantidad especificada.
     * Si el producto ya existe, incrementa su cantidad.
     * 
     * @param producto El producto a agregar.
     * @param cantidad Cantidad seleccionada.
     */
    fun agregarAlCarrito(producto: Producto, cantidad: Int) {
        _items.update { currentItems ->
            val existingItemIndex = currentItems.indexOfFirst { it.producto.idProducto == producto.idProducto }
            
            if (existingItemIndex != -1) {
                // El producto ya está, actualizamos la cantidad
                currentItems.toMutableList().apply {
                    val currentItem = this[existingItemIndex]
                    this[existingItemIndex] = currentItem.copy(cantidad = currentItem.cantidad + cantidad)
                }
            } else {
                // Producto nuevo en el carrito
                currentItems + CarritoItem(producto, cantidad)
            }
        }
    }

    /**
     * Elimina un ítem completo del carrito.
     * 
     * @param idProducto ID del producto a eliminar.
     */
    fun eliminarDelCarrito(idProducto: String) {
        _items.update { currentItems ->
            currentItems.filter { it.producto.idProducto != idProducto }
        }
    }

    /**
     * Actualiza la cantidad de un producto en el carrito.
     * 
     * @param idProducto ID del producto.
     * @param nuevaCantidad Nueva cantidad deseada.
     */
    fun actualizarCantidad(idProducto: String, nuevaCantidad: Int) {
        if (nuevaCantidad <= 0) {
            eliminarDelCarrito(idProducto)
            return
        }
        
        _items.update { currentItems ->
            currentItems.map { 
                if (it.producto.idProducto == idProducto) it.copy(cantidad = nuevaCantidad) else it 
            }
        }
    }

    /**
     * Vacía el carrito por completo.
     */
    fun vaciarCarrito() {
        _items.value = emptyList()
    }

    /**
     * Calcula el costo total de todos los productos apartados.
     */
    fun calcularTotal(): Double {
        return _items.value.sumOf { it.producto.precio * it.cantidad }
    }
}
