package com.sncf.reports.domain.model

import org.junit.Test

import org.junit.Assert.*
import java.time.LocalDateTime

class SignalementTest {

    private val fixedDateTime = LocalDateTime.of(2024, 5, 14, 4, 32, 58)

    @Test
    fun `toRequest should generate correct URL with all parameters`() {
        // Given
        val signalement = Signalement(
            vehicleType = VehicleType.R,
            vehicleRank = 3,
            level = Level.H,
            trainsetNumber = "285",
            courseId = "1187-F-6728-2024-04-22",
            signalementClass = SignalementClass.CONFORT,
            codePanne = "P00626",
            lcnCode = "D",
            reportDate = fixedDateTime,
            location = "Place",
            categoryLabel = "Equipement SECURITE",
            organLabel = "Vitre latérale",
            physicalAndFunctionalFailure = "Verre extérieur fissuré, impact, mosaïque",
            svsiComment = "WC HS",
            precisionN1 = "83, 85"
        )

        // When
        val result = signalement.toRequest("https://api.example.com/report")

        // Then
        val expected = "https://api.example.com/report?" +
                "vehicleType=R&" +
                "vehicleRank=3&" +
                "level=H&" +
                "trainsetNumber=285&" +
                "courseId=1187-F-6728-2024-04-22&" +
                "signalementClass=2&" +
                "codePanne=P00626&" +
                "lcnCode=D&" +
                "reportDate=2024-05-14T04%3A32%3A58&" +
                "location=Place&" +
                "categoryLabel=Equipement+SECURITE&" +
                "organLabel=Vitre+lat%C3%A9rale&" +
                "physicalAndFunctionalFailure=Verre+ext%C3%A9rieur+fissur%C3%A9%2C+impact%2C+mosa%C3%AFque&" +
                "svsiComment=WC+HS&" +
                "precisionN1=83%2C+85"

        assertEquals(expected, result)
    }

    @Test
    fun `toRequest should handle null optional fields correctly`() {
        // Given
        val signalement = Signalement(
            vehicleType = VehicleType.M,
            vehicleRank = 1,
            level = null,
            trainsetNumber = "123",
            courseId = "test-course",
            signalementClass = SignalementClass.SECURITE,
            codePanne = "TEST123",
            lcnCode = "X",
            reportDate = fixedDateTime,
            location = "Test Location",
            categoryLabel = "Test Category",
            organLabel = "Test Organ",
            physicalAndFunctionalFailure = "Test Failure",
            // All optional fields left null
            svsiComment = null,
            precisionN1 = null,
            precisionN2 = null,
            precisionN3 = null,
            subOrganLabel = null
        )

        // When
        val result = signalement.toRequest("https://api.test.com/submit")

        // Then
        val expected = "https://api.test.com/submit?" +
                "vehicleType=M&" +
                "vehicleRank=1&" +
                "trainsetNumber=123&" +
                "courseId=test-course&" +
                "signalementClass=1&" +
                "codePanne=TEST123&" +
                "lcnCode=X&" +
                "reportDate=2024-05-14T04%3A32%3A58&" +
                "location=Test+Location&" +
                "categoryLabel=Test+Category&" +
                "organLabel=Test+Organ&" +
                "physicalAndFunctionalFailure=Test+Failure"

        assertEquals(expected, result)
        assertFalse(result.contains("level="))
        assertFalse(result.contains("svsiComment="))
    }

    @Test
    fun `toRequest should handle special characters in fields`() {
        // Given
        val signalement = Signalement(
            vehicleType = VehicleType.R,
            vehicleRank = 2,
            level = Level.B,
            trainsetNumber = "286",
            courseId = "test & course",
            signalementClass = SignalementClass.PROPRETE,
            codePanne = "P#123",
            lcnCode = "A&B",
            reportDate = fixedDateTime,
            location = "Paris/Gare",
            categoryLabel = "Équipement",
            organLabel = "Porte/Toilette",
            physicalAndFunctionalFailure = "Défaillance #1",
            svsiComment = "Comment?",
            precisionN1 = "1/2"
        )

        // When
        val result = signalement.toRequest("https://api.special.com")

        // Then
        assertTrue(result.contains("courseId=test+%26+course"))
        assertTrue(result.contains("codePanne=P%23123"))
        assertTrue(result.contains("lcnCode=A%26B"))
        assertTrue(result.contains("location=Paris%2FGare"))
        assertTrue(result.contains("categoryLabel=%C3%89quipement"))
        assertTrue(result.contains("organLabel=Porte%2FToilette"))
        assertTrue(result.contains("physicalAndFunctionalFailure=D%C3%A9faillance+%231"))
        assertTrue(result.contains("svsiComment=Comment%3F"))
        assertTrue(result.contains("precisionN1=1%2F2"))
    }
}