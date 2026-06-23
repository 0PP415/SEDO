package com.example.sedo.ui.home.ml

object SymbolMapper {
    // Pair(한글 설명 텍스트, 경고 여부)
    private val mapper = mapOf(

        "ER_wash_normal" to Pair("물세탁 가능", false),
        "ER_wash_machine" to Pair("세탁기 사용 가능", false),
        "ER_wash_hand" to Pair("손세탁 권장", false),
        "ER_wash_30" to Pair("30℃ 물세탁", false),
        "ER_wash_40" to Pair("40℃ 물세탁", false),
        "ER_wash_60" to Pair("60℃ 물세탁", false),
        "ER_wash_95" to Pair("95℃ 삶음 세탁 가능", false),
        "ER_wash_gentle" to Pair("약한 수류로 세탁", false),
        "ER_wash_no" to Pair("물세탁 절대 금지", true),
        "KR_wash_neutral_30" to Pair("30℃ 중성세제 세탁", false),
        "KR_hand_wash_30" to Pair("30℃ 약하게 손세탁", false),

        "ER_bleach_all" to Pair("모든 표백제 사용 가능", false),
        "ER_bleach_chlorine" to Pair("염소계 표백제 가능", false),
        "ER_bleach_oxygen" to Pair("산소계 표백제 가능", false),
        "ER_no_bleach" to Pair("표백제 사용 금지", true),
        "KR_bleach_oxygen_only" to Pair("산소계 표백제만 사용", false),

        "ER_iron_high_temp" to Pair("200℃ 고온 다림질", false),
        "ER_iron_medium_temp" to Pair("150℃ 중온 다림질", false),
        "ER_iron_low_temp" to Pair("110℃ 저온 다림질", false),
        "ER_no_iron" to Pair("다림질 금지", true),
        "ER_iron_no_steam" to Pair("스팀 다림질 금지", true),
        "KR_iron_cloth" to Pair("원단 덮고 다림질", false),

        "ER_tumble_dry_normal" to Pair("건조기 가능 (표준)", false),
        "ER_tumble_dry_low" to Pair("건조기 가능 (저온)", false),
        "ER_no_tumble_dry" to Pair("건조기 절대 금지", true),

        "ER_line_dry" to Pair("옷걸이에 걸어서 건조", false),
        "ER_line_dry_shade" to Pair("그늘에 걸어서 건조", false),
        "ER_dry_flat" to Pair("뉘어서 건조", false),
        "ER_dry_flat_shade" to Pair("그늘에 뉘어서 건조", false),
        "ER_drip_dry" to Pair("물기 있는 채로 건조", false),
        "ER_drip_dry_shade" to Pair("그늘에서 물기 있는 채로 건조", false),
        "KR_wring_soft" to Pair("약하게 짜기", false),
        "KR_wring_no" to Pair("비틀어 짜기 금지", true),

        "ER_dry_clean" to Pair("드라이클리닝", false),
        "ER_dry_clean_petroleum" to Pair("석유계 드라이클리닝", false),
        "ER_dry_clean_chemical" to Pair("전문 드라이클리닝", false),
        "ER_no_dry_clean" to Pair("드라이클리닝 금지", true),
        "ER_wet_clean" to Pair("전문 웻클리닝", false),
        "ER_no_wet_clean" to Pair("웻클리닝 금지", true),

        "KR_hand_wash_30_neutral" to Pair("중성세제, 30도 손세탁", false),
        "KR_gentle_wring" to Pair("약하게 짜기", false),
    )

    fun getKoreanName(label: String): String = mapper[label]?.first ?: label.replace("ER_", "").replace("_", " ")

    fun isWarning(label: String): Boolean = mapper[label]?.second ?: false
}