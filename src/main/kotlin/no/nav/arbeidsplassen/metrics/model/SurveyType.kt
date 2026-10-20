package no.nav.arbeidsplassen.metrics.model

// https://github.com/navikt/pam-survey-api/blob/main/src/main/kotlin/no/nav/arbeidsplassen/survey/survey/SurveyType.kt
enum class SurveyType {
    RECRUITMENT_OUTCOME;

    companion object {
        private val surveyTypes: Set<String> = entries.map { it.name }.toSet()
        fun isValid(type: String): Boolean = type in surveyTypes
    }
}
