package com.example

import com.example.core.WhatsAppService
import com.example.data.model.RonalCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun catalog_has_all_17_distinct_varieties_and_over_100_colors() {
        assertEquals(17, RonalCatalog.products.size)
        assertEquals(17, RonalCatalog.products.map { it.visualType }.distinct().size)
        assertTrue(RonalCatalog.themeSwatches.size >= 100)
    }

    @Test
    fun whatsapp_number_formats_for_0719362947() {
        assertEquals("94719362947", WhatsAppService.formatInternationalPhone("0719362947"))
    }
}
