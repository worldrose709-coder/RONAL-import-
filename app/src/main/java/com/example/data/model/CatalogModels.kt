package com.example.data.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.R

enum class ProductCategory(val title: String, val badge: String) {
    CARS("TRJ Cars", "Bespoke Motors"),
    HOTELS("UK Hotels", "Estates & Suites"),
    TEA("Ceylon Tea", "Reserve Leaf"),
    PERFUME("Cinnamon Perfume", "Royal Elixir")
}

enum class VarietyVisualType {
    CAR_TRJ_VIP,
    CAR_TRJ_VVIP_PHOTO,
    CAR_TRJ_GR8,
    CAR_TRJ_CP2,
    CAR_TRJ_540N,
    CAR_GR8_189M,
    HOTEL_UK_ULTRA_PHOTO,
    HOTEL_GRAND_VILLA,
    HOTEL_GRAND_VIP,
    HOTEL_RONAL_VVIP,
    TEA_100G,
    TEA_250G,
    TEA_500G,
    TEA_1KG_PHOTO,
    PERFUME_100ML,
    PERFUME_250ML,
    PERFUME_500ML
}

data class LuxuryProduct(
    val id: String,
    val name: String,
    val varietyLabel: String,
    val category: ProductCategory,
    val priceLkr: Long,
    val priceDisplay: String,
    val unitSuffix: String,
    val ukLocation: String,
    val badge: String,
    val description: String,
    val specs: List<String>,
    val visualType: VarietyVisualType,
    @DrawableRes val photoRes: Int? = null,
    val primaryTint: Color,
    val secondaryTint: Color
)

data class ThemeSwatch(
    val id: String,
    val family: String,
    val name: String,
    val accentColor: Color,
    val glowColor: Color
)

enum class WallpaperStyle(val id: String, val label: String, val subtitle: String) {
    PEARL_SILK("pearl_silk", "Pearl Silk Wave", "Animated alabaster champagne gradient"),
    AURORA_PRISM("aurora_prism", "White Aurora Prism", "Iridescent pastel prism shimmer"),
    STARLIGHT_DUST("starlight_dust", "Mayfair Starlight", "Floating golden dust particles"),
    ROYAL_GRID("royal_grid", "Atelier 3D Grid", "Perspective white marble depth"),
    CUSTOM_PHOTO("custom_photo", "Custom Photo", "Your uploaded gallery artwork")
}

enum class ProfileFrameStyle(val id: String, val label: String) {
    GOLDEN_HALO("golden_halo", "Golden Halo"),
    NEON_PULSE("neon_pulse", "Neon Prism Pulse"),
    ROYAL_CREST("royal_crest", "Royal Diamond Orbit"),
    PEARL_AURA("pearl_aura", "Alabaster Pearl Ring")
}

enum class VipLevel(
    val level: Int,
    val title: String,
    val minPoints: Int,
    val perkSummary: String
) {
    SILVER_ATELIER(1, "Silver Atelier", 0, "Priority UK Concierge & Welcome Credit"),
    GOLD_SOVEREIGN(2, "Gold Sovereign", 500, "Complimentary Ceylon Tea Tasting & Chauffeur"),
    PLATINUM_REGENT(3, "Platinum Regent", 1500, "Private Mayfair Showroom & Perfume Engraving"),
    RONAL_VVIP_ROYAL(4, "Ronal VVIP Royal", 3500, "24/7 UK Estate Helicopter & Bespoke Allocation");

    companion object {
        fun forPoints(points: Int): VipLevel =
            entries.lastOrNull { points >= it.minPoints } ?: SILVER_ATELIER

        fun nextLevel(current: VipLevel): VipLevel? =
            entries.firstOrNull { it.level == current.level + 1 }
    }
}

data class DigitalGift(
    val id: String,
    val title: String,
    val subtitle: String,
    val valueDisplay: String,
    val codePrefix: String
)

data class UkLocationPin(
    val id: String,
    val name: String,
    val district: String,
    val specialty: String,
    val xFraction: Float,
    val yFraction: Float
)

object RonalCatalog {
    val products: List<LuxuryProduct> = listOf(
        // TRJ CARS (6 distinct varieties)
        LuxuryProduct(
            id = "car_trj_vip",
            name = "TRJ VIP",
            varietyLabel = "450M Sovereign Edition",
            category = ProductCategory.CARS,
            priceLkr = 450_000_000L,
            priceDisplay = "Rs. 450M",
            unitSuffix = "Ex-Mayfair",
            ukLocation = "Mayfair Showroom, London UK",
            badge = "VIP Flagship",
            description = "Hand-assembled Pearl Ivory executive limousine featuring acoustic glass, champagne chiller, and bespoke British wool cabin.",
            specs = listOf("V12 Twin-Turbo", "Pearl Ivory Finish", "Mayfair Chauffeur Spec", "Starlight Headliner"),
            visualType = VarietyVisualType.CAR_TRJ_VIP,
            primaryTint = Color(0xFFC59B27),
            secondaryTint = Color(0xFF1E293B)
        ),
        LuxuryProduct(
            id = "car_trj_vvip",
            name = "TRJ VVIP",
            varietyLabel = "550M Royal Commission",
            category = ProductCategory.CARS,
            priceLkr = 550_000_000L,
            priceDisplay = "Rs. 550M",
            unitSuffix = "Bespoke UK",
            ukLocation = "Grosvenor Square, London UK",
            badge = "VVIP 1 of 1",
            description = "Our pinnacle two-tone alabaster & sapphire grand tourer with diamond-quilted hides and private security telemetry.",
            specs = listOf("Armored B6 Option", "Two-Tone Sapphire", "24K Gold Badging", "Rear Lounge Suites"),
            visualType = VarietyVisualType.CAR_TRJ_VVIP_PHOTO,
            photoRes = R.drawable.img_car_trj_vvip,
            primaryTint = Color(0xFF1D4ED8),
            secondaryTint = Color(0xFFC59B27)
        ),
        LuxuryProduct(
            id = "car_trj_gr8",
            name = "TRJ GR8",
            varietyLabel = "27M Alpine GT",
            category = ProductCategory.CARS,
            priceLkr = 27_000_000L,
            priceDisplay = "Rs. 27M",
            unitSuffix = "UK Spec",
            ukLocation = "Knightsbridge Salon, London UK",
            badge = "Track & Grand Tour",
            description = "Sculpted forged-carbon coupe tuned on British circuits for crisp weekend escapes across the Cotswolds.",
            specs = listOf("0-60 in 2.8s", "Forged Carbon Aero", "Ceramic Brakes", "Alcantara Cockpit"),
            visualType = VarietyVisualType.CAR_TRJ_GR8,
            primaryTint = Color(0xFF0EA5E9),
            secondaryTint = Color(0xFF0F172A)
        ),
        LuxuryProduct(
            id = "car_trj_cp2",
            name = "TRJ CP2",
            varietyLabel = "9M Urban Atelier",
            category = ProductCategory.CARS,
            priceLkr = 9_000_000L,
            priceDisplay = "Rs. 9M",
            unitSuffix = "City Edition",
            ukLocation = "Chelsea Gallery, London UK",
            badge = "City Coupe",
            description = "Agile porcelain-silver compact luxury coupe tailored for effortless West End evenings and boutique arrivals.",
            specs = listOf("Hybrid Whisper Drive", "Porcelain Silver", "Panoramic Glass", "Atelier Sound"),
            visualType = VarietyVisualType.CAR_TRJ_CP2,
            primaryTint = Color(0xFF6366F1),
            secondaryTint = Color(0xFF64748B)
        ),
        LuxuryProduct(
            id = "car_trj_540n",
            name = "TRJ 540N",
            varietyLabel = "90M Neon Apex",
            category = ProductCategory.CARS,
            priceLkr = 90_000_000L,
            priceDisplay = "Rs. 90M",
            unitSuffix = "Limited 90!",
            ukLocation = "Silverstone Atelier, UK",
            badge = "Limited 540N",
            description = "Aerodynamic obsidian-white hyper-roadster with active rear wing, titanium exhaust, and laser-etched serial plaque.",
            specs = listOf("900 HP Hybrid", "Active Titanium Wing", "Carbon Monocoque", "Track Telemetry"),
            visualType = VarietyVisualType.CAR_TRJ_540N,
            primaryTint = Color(0xFFEC4899),
            secondaryTint = Color(0xFF1E293B)
        ),
        LuxuryProduct(
            id = "car_gr8_189m",
            name = "GR8 189M",
            varietyLabel = "90M Heritage Prototype",
            category = ProductCategory.CARS,
            priceLkr = 90_000_000L,
            priceDisplay = "Rs. 90M",
            unitSuffix = "Collector Spec",
            ukLocation = "Edinburgh Private Vault, UK",
            badge = "Heritage",
            description = "Collector-grade British racing emerald & brushed platinum grand tourer engineered for transcontinental poise.",
            specs = listOf("British Emerald Trim", "Bespoke Luggage Set", "Adaptive Air Ride", "Numbered Chassis"),
            visualType = VarietyVisualType.CAR_GR8_189M,
            primaryTint = Color(0xFF059669),
            secondaryTint = Color(0xFFD97706)
        ),

        // TRJ HOTELS UK (4 distinct varieties)
        LuxuryProduct(
            id = "hotel_uk_ultra",
            name = "UK Ultra Luxury",
            varietyLabel = "Hyde Park Sky Penthouse",
            category = ProductCategory.HOTELS,
            priceLkr = 850_000L,
            priceDisplay = "Rs. 850,000",
            unitSuffix = "per day",
            ukLocation = "Park Lane, Mayfair, London UK",
            badge = "Sky Penthouse",
            description = "Full-floor Carrara marble penthouse overlooking Hyde Park with private butler, terrace dining, and helipad access.",
            specs = listOf("24h Private Butler", "Hyde Park Panorama", "Marble Spa Suite", "TRJ Chauffeur Included"),
            visualType = VarietyVisualType.HOTEL_UK_ULTRA_PHOTO,
            photoRes = R.drawable.img_hotel_uk_ultra,
            primaryTint = Color(0xFFC59B27),
            secondaryTint = Color(0xFF0F766E)
        ),
        LuxuryProduct(
            id = "hotel_grand_villa",
            name = "Grand Villa",
            varietyLabel = "Cotswolds Manor Estate",
            category = ProductCategory.HOTELS,
            priceLkr = 150_000L,
            priceDisplay = "Rs. 150,000",
            unitSuffix = "per day",
            ukLocation = "Cotswolds, Oxfordshire, UK",
            badge = "Countryside",
            description = "Sunlit limestone country villa surrounded by lavender gardens, heated infinity pool, and private tea conservatory.",
            specs = listOf("4 Acre Gardens", "Heated Stone Pool", "Private Chef Kitchen", "Equestrian Access"),
            visualType = VarietyVisualType.HOTEL_GRAND_VILLA,
            primaryTint = Color(0xFF10B981),
            secondaryTint = Color(0xFFB45309)
        ),
        LuxuryProduct(
            id = "hotel_grand_vip",
            name = "Grand VIP",
            varietyLabel = "Knightsbridge Townhouse",
            category = ProductCategory.HOTELS,
            priceLkr = 500_000L,
            priceDisplay = "Rs. 500,000",
            unitSuffix = "per day",
            ukLocation = "Knightsbridge, London UK",
            badge = "VIP Residence",
            description = "Five-storey Georgian townhouse steps from Harrods featuring private cinema, wine vault, and courtyard conservatory.",
            specs = listOf("Private Cinema", "Georgian Courtyard", "Concierge Desk", "Security Escort"),
            visualType = VarietyVisualType.HOTEL_GRAND_VIP,
            primaryTint = Color(0xFF3B82F6),
            secondaryTint = Color(0xFF1E3A8A)
        ),
        LuxuryProduct(
            id = "hotel_ronal_vvip",
            name = "Ronal VVIP",
            varietyLabel = "Royal Sovereign Wing",
            category = ProductCategory.HOTELS,
            priceLkr = 1_000_000L,
            priceDisplay = "Rs. 1,000,000",
            unitSuffix = "per day",
            ukLocation = "Belgravia Royal Enclave, UK",
            badge = "Ronal Crown",
            description = "The ultimate private Belgravia palace wing with golden salon, Michelin chef residency, and dual TRJ fleet standby.",
            specs = listOf("Michelin Private Chef", "Dual TRJ Fleet", "Private Art Salon", "Diplomatic Privacy"),
            visualType = VarietyVisualType.HOTEL_RONAL_VVIP,
            primaryTint = Color(0xFF9333EA),
            secondaryTint = Color(0xFFC59B27)
        ),

        // TRJ BLACK CEYLON TEA (4 distinct varieties)
        LuxuryProduct(
            id = "tea_100g",
            name = "TRJ Black Ceylon Tea",
            varietyLabel = "100g Velvet Tin",
            category = ProductCategory.TEA,
            priceLkr = 100_000L,
            priceDisplay = "Rs. 100,000",
            unitSuffix = "100g Caddy",
            ukLocation = "London & Nuwara Eliya Reserve",
            badge = "Single Estate",
            description = "Hand-plucked silver-tipped Ceylon black leaves from 6,000ft estates, sealed in a numbered ivory travel tin.",
            specs = listOf("100g Loose Leaf", "Highland Single Estate", "Brisk Floral Honey", "Ivory Travel Tin"),
            visualType = VarietyVisualType.TEA_100G,
            primaryTint = Color(0xFFD97706),
            secondaryTint = Color(0xFF78350F)
        ),
        LuxuryProduct(
            id = "tea_250g",
            name = "TRJ Black Ceylon Tea",
            varietyLabel = "250g Heritage Decanter",
            category = ProductCategory.TEA,
            priceLkr = 250_000L,
            priceDisplay = "Rs. 250,000",
            unitSuffix = "250g Edition",
            ukLocation = "Mayfair Tea Salon, UK",
            badge = "Gold Leaf",
            description = "Aromatic autumn-flush BOPF reserve packaged in double-walled porcelain with brass measuring scoop.",
            specs = listOf("250g Porcelain Jar", "Gold Brass Scoop", "Autumn Flush Harvest", "Gift Sealed"),
            visualType = VarietyVisualType.TEA_250G,
            primaryTint = Color(0xFF047857),
            secondaryTint = Color(0xFFC59B27)
        ),
        LuxuryProduct(
            id = "tea_500g",
            name = "TRJ Black Ceylon Tea",
            varietyLabel = "500g Imperial Chest",
            category = ProductCategory.TEA,
            priceLkr = 490_000L,
            priceDisplay = "Rs. 490,000",
            unitSuffix = "500g Chest",
            ukLocation = "Mayfair Tea Salon, UK",
            badge = "Imperial",
            description = "Lacquered presentation chest housing twin 250g chambers of full-bodied golden-liquor Ceylon reserve.",
            specs = listOf("500g Twin Chamber", "Lacquered White Box", "Certificate of Origin", "Sommelier Notes"),
            visualType = VarietyVisualType.TEA_500G,
            primaryTint = Color(0xFF7C3AED),
            secondaryTint = Color(0xFFB45309)
        ),
        LuxuryProduct(
            id = "tea_1kg",
            name = "TRJ Black Ceylon Tea",
            varietyLabel = "1KG Sovereign Vault",
            category = ProductCategory.TEA,
            priceLkr = 950_000L,
            priceDisplay = "Rs. 950,000",
            unitSuffix = "1KG Reserve",
            ukLocation = "Mayfair Flagship Vault, UK",
            badge = "1KG Collector",
            description = "Our rarest micro-lot 1KG royal porcelain amphora & crystal teapot pairing crafted for grand hospitality.",
            specs = listOf("1KG Royal Amphora", "Crystal Infuser Teapot", "24K Rim Teacup", "Micro-Lot 2026"),
            visualType = VarietyVisualType.TEA_1KG_PHOTO,
            photoRes = R.drawable.img_tea_ceylon,
            primaryTint = Color(0xFFB45309),
            secondaryTint = Color(0xFFC59B27)
        ),

        // TRJ CINNAMON PERFUME (3 distinct varieties)
        LuxuryProduct(
            id = "perfume_100ml",
            name = "TRJ Cinnamon Perfume",
            varietyLabel = "100ml Extrait de Parfum",
            category = ProductCategory.PERFUME,
            priceLkr = 250_000L,
            priceDisplay = "Rs. 250,000",
            unitSuffix = "100ml Flacon",
            ukLocation = "Bond Street Atelier, London UK",
            badge = "Extrait 32%",
            description = "Warm Ceylon Alba cinnamon bark fused with bergamot, white oud, and Madagascar vanilla in a crystal flacon.",
            specs = listOf("100ml Crystal Spray", "32% Pure Oil", "Ceylon Alba Cinnamon", "Monogram Cap"),
            visualType = VarietyVisualType.PERFUME_100ML,
            primaryTint = Color(0xFFEA580C),
            secondaryTint = Color(0xFFF59E0B)
        ),
        LuxuryProduct(
            id = "perfume_250ml",
            name = "TRJ Cinnamon Perfume",
            varietyLabel = "250ml Royal Decanter",
            category = ProductCategory.PERFUME,
            priceLkr = 500_000L,
            priceDisplay = "Rs. 500,000",
            unitSuffix = "250ml Decanter",
            ukLocation = "Bond Street Atelier, London UK",
            badge = "Atelier 250ml",
            description = "Hand-blown octagonal decanter with gilded atomizer bulb, macerated for 9 months in oak & amber.",
            specs = listOf("250ml Octagonal Glass", "Gold Bulb Atomizer", "9-Month Maceration", "Refillable Seal"),
            visualType = VarietyVisualType.PERFUME_250ML,
            primaryTint = Color(0xFFDB2777),
            secondaryTint = Color(0xFFC59B27)
        ),
        LuxuryProduct(
            id = "perfume_500ml",
            name = "TRJ Cinnamon Perfume",
            varietyLabel = "500ml Sovereign Amphora",
            category = ProductCategory.PERFUME,
            priceLkr = 1_000_000L,
            priceDisplay = "Rs. 1,000,000",
            unitSuffix = "500ml Masterwork",
            ukLocation = "Bond Street Vault, London UK",
            badge = "500ml Vault",
            description = "Bespoke half-litre crystal masterwork with 24K gold collar, custom engraved nameplate, and travel phial.",
            specs = listOf("500ml Crystal Amphora", "24K Gold Collar", "Engraved Nameplate", "15ml Travel Atomizer"),
            visualType = VarietyVisualType.PERFUME_500ML,
            primaryTint = Color(0xFFC59B27),
            secondaryTint = Color(0xFF9A3412)
        )
    )

    // 108 Curated Luxury Theme Colors (9 families x 12 shades = 108 > 100+ Theme Colors)
    val themeSwatches: List<ThemeSwatch> = run {
        val families = listOf(
            "Champagne Gold" to Pair(42f, 0.72f),
            "Royal Sapphire" to Pair(222f, 0.80f),
            "British Emerald" to Pair(158f, 0.78f),
            "Neon Cyan" to Pair(190f, 0.85f),
            "Rose Quartz" to Pair(336f, 0.75f),
            "Amethyst Prism" to Pair(272f, 0.74f),
            "Ceylon Amber" to Pair(26f, 0.82f),
            "Crimson Velvet" to Pair(354f, 0.78f),
            "Platinum Slate" to Pair(215f, 0.28f)
        )
        val result = mutableListOf<ThemeSwatch>()
        families.forEach { (familyName, hs) ->
            val (baseHue, baseSat) = hs
            for (step in 0 until 12) {
                val hue = (baseHue + (step - 5) * 3.5f + 360f) % 360f
                val sat = (baseSat - (step % 4) * 0.08f).coerceIn(0.18f, 0.95f)
                val light = (0.34f + (step / 11f) * 0.28f).coerceIn(0.30f, 0.65f)
                val glowHue = (hue + 28f) % 360f
                val accent = Color.hsl(hue, sat, light)
                val glow = Color.hsl(glowHue, (sat + 0.1f).coerceAtMost(0.95f), (light + 0.1f).coerceAtMost(0.72f))
                result.add(
                    ThemeSwatch(
                        id = "${familyName.lowercase().replace(" ", "_")}_$step",
                        family = familyName,
                        name = "$familyName #${step + 1}",
                        accentColor = accent,
                        glowColor = glow
                    )
                )
            }
        }
        result
    }

    val dailyQuotes: List<Pair<String, String>> = listOf(
        "True luxury is quiet precision—crafted in Ceylon, curated in Mayfair." to "Ronal Atelier Manifesto",
        "Every mile in a TRJ flagship turns an ordinary London evening into theatre." to "Mayfair Motor Club",
        "Single-estate black Ceylon tea carries the mist of Nuwara Eliya in every cup." to "Ceylon Tea Guild",
        "Cinnamon and amber linger long after the doors of the grand villa close." to "Bond Street Parfumerie",
        "Excellence is not an occasion; it is the daily standard of the Ronal VVIP circle." to "Sovereign Concierge UK"
    )

    val digitalGifts: List<DigitalGift> = listOf(
        DigitalGift(
            id = "gift_tea_salon",
            title = "Mayfair Tea Salon Pass",
            subtitle = "Private Ceylon tasting for two in London",
            valueDisplay = "Rs. 45,000 Value",
            codePrefix = "TRJ-TEA"
        ),
        DigitalGift(
            id = "gift_chauffeur",
            title = "TRJ Chauffeur Transfer",
            subtitle = "Complimentary Heathrow to Mayfair VIP arrival",
            valueDisplay = "Rs. 85,000 Value",
            codePrefix = "TRJ-RIDE"
        ),
        DigitalGift(
            id = "gift_engraving",
            title = "24K Perfume Engraving",
            subtitle = "Bespoke monogramming on any Cinnamon flacon",
            valueDisplay = "Rs. 30,000 Value",
            codePrefix = "TRJ-SCENT"
        ),
        DigitalGift(
            id = "gift_villa_upgrade",
            title = "Grand Villa Late Checkout",
            subtitle = "Extended 6 PM Cotswolds estate departure",
            valueDisplay = "Rs. 60,000 Value",
            codePrefix = "TRJ-STAY"
        )
    )

    val ukLocations: List<UkLocationPin> = listOf(
        UkLocationPin("mayfair", "Mayfair Flagship", "London W1K", "TRJ Cars & Tea Vault", 0.64f, 0.72f),
        UkLocationPin("knightsbridge", "Knightsbridge Suite", "London SW1X", "Grand VIP & Perfume", 0.59f, 0.76f),
        UkLocationPin("cotswolds", "Grand Villa Estate", "Oxfordshire", "Countryside Villas", 0.44f, 0.65f),
        UkLocationPin("silverstone", "TRJ Track Studio", "Northamptonshire", "GR8 & 540N Testing", 0.53f, 0.56f),
        UkLocationPin("edinburgh", "Royal Scottish Vault", "Edinburgh EH2", "GR8 189M Private Collection", 0.45f, 0.22f)
    )
}
