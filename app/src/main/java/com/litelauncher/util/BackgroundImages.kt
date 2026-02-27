package com.litelauncher.util

object BackgroundImages {

    /**
     * Returns the drawable resource ID for the given background index.
     * Index -1 means no background. Indices 0-9 map to bg_wall_01..10.
     * Returns 0 if index is invalid.
     */
    fun getResourceId(index: Int): Int {
        if (index < 0 || index >= IMAGE_COUNT) return 0
        return resourceIds.getOrElse(index) { 0 }
    }

    const val IMAGE_COUNT = 10

    // These will resolve to 0 if the drawables don't exist yet—
    // safe to reference by name using reflection-free approach.
    private val resourceIds: List<Int> by lazy {
        try {
            val clazz = com.litelauncher.R.drawable::class.java
            (1..IMAGE_COUNT).map { i ->
                val name = "bg_wall_%02d".format(i)
                val field = clazz.getField(name)
                field.getInt(null)
            }
        } catch (_: Exception) {
            List(IMAGE_COUNT) { 0 }
        }
    }
}
