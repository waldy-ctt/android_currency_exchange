package com.waldy.androidcurrencyexchange.presentation.converter

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat

class ThousandSeparatorVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val integerPart = originalText.split(".").getOrNull(0) ?: ""

        // Prevent formatting for very long numbers to avoid Long overflow
        if (integerPart.length > 15) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val formattedInteger = if (integerPart.isNotEmpty()) {
            try {
                DecimalFormat("#,###").format(integerPart.toLong())
            } catch (e: NumberFormatException) {
                return TransformedText(text, OffsetMapping.Identity)
            }
        } else {
            ""
        }

        val decimalPart = originalText.split(".").getOrNull(1)
        val newText = when {
            decimalPart != null && originalText.endsWith(".") -> "$formattedInteger."
            decimalPart != null -> "$formattedInteger.$decimalPart"
            else -> formattedInteger
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val originalIntegerLength = integerPart.length

                // Handle offsets beyond the integer part (in decimal area)
                if (offset > originalIntegerLength) {
                    val integerCommas = (originalIntegerLength - 1).coerceAtLeast(0) / 3
                    return offset + integerCommas
                }

                // Calculate commas before this position in the integer part
                val commasBefore = if (offset > 0) {
                    val digitsFromRight = originalIntegerLength - offset
                    val commasAfter = (digitsFromRight - 1).coerceAtLeast(0) / 3
                    val totalCommas = (originalIntegerLength - 1).coerceAtLeast(0) / 3
                    totalCommas - commasAfter
                } else {
                    0
                }

                return (offset + commasBefore).coerceIn(0, newText.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                // Ensure we don't go out of bounds
                val safeOffset = offset.coerceIn(0, newText.length)

                val formattedIntegerLength = formattedInteger.length

                // Handle offsets beyond the integer part (in decimal area)
                if (safeOffset > formattedIntegerLength) {
                    val integerCommas = formattedInteger.count { it == ',' }
                    return (safeOffset - integerCommas).coerceIn(0, originalText.length)
                }

                // Count commas before this position
                val commasBefore = newText.substring(0, safeOffset).count { it == ',' }
                return (safeOffset - commasBefore).coerceIn(0, originalText.length)
            }
        }

        return TransformedText(
            AnnotatedString(newText),
            offsetMapping
        )
    }
}