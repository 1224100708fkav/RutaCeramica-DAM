package com.utng.rutaceramica.data.model

/**
 * Modelo de datos para un ítem dentro del carrito de compras/apartados.
 *
 * @property producto El producto que se desea apartar.
 * @property cantidad La cantidad de unidades seleccionadas.
 */
data class CarritoItem(
    val producto: Producto,
    val cantidad: Int
)
