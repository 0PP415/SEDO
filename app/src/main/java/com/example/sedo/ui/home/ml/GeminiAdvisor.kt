package com.example.sedo.ui.home.ml

import com.example.sedo.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

class GeminiAdvisor {

    // ⭐️ 정답 획득! 가성비와 속도가 가장 뛰어난 최신 Flash 모델로 확정
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun generateWashGuide(symbols: List<Detection>, ocrText: String): String {
        return try {
            val symbolNames = if (symbols.isEmpty()) "탐지된 기호 없음"
            else symbols.joinToString(", ") { it.label }

            val prompt = """
                아래 데이터를 바탕으로 한국어 세탁 가이드를 작성하세요.
                
                입력 데이터:
                - 세탁 기호: $symbolNames
                - 라벨 텍스트: $ocrText
                
                <출력 규칙>
                반드시 아래의 [양식]을 100% 똑같이 복사해서, 괄호 안의 내용을 채우고 괄호는 빼고 출력하세요. 다른 인사말이나 부연 설명은 절대 금지합니다.
                
                [양식]
                [소재 요약]
                (주요 소재를 딱 1줄로 요약)

                [세탁 기호]
                (탐지된 기호의 의미를 한글로만 설명. ER_ 같은 영문 ID는 절대 출력 금지)

                [세탁 가이드]
                (실제 세탁/건조 주의사항을 딱 3줄 이내의 글머리기호로 핵심만 요약)

                [검색 키워드]
                (소재와 주의사항 기반 유튜브 검색용 키워드 딱 2개를 쉼표로 구분해서 작성. 예: 비스코스 세탁, 건조기 금지)
            """.trimIndent()

            val response = generativeModel.generateContent(content { text(prompt) })

            response.text ?: "세탁 가이드를 생성하지 못했습니다."

        } catch (e: Exception) {
            e.printStackTrace()
            "AI 서버 연결에 실패했습니다: ${e.localizedMessage}"
        }
    }
}