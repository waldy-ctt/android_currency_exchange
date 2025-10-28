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
                if (offset >= originalIntegerLength) {
                    val integerCommas = (originalIntegerLength - 1).coerceAtLeast(0) / 3
                    return offset + integerCommas
                }
                val commasBefore = (offset - 1).coerceAtLeast(0) / 3
                return offset + commasBefore
            }

            override fun transformedToOriginal(offset: Int): Int {
                val formattedIntegerLength = formattedInteger.length
                if (offset >= formattedIntegerLength) {
                    val integerCommas = formattedInteger.count { it == ',' }
                    return offset - integerCommas
                }
                val commasBefore = newText.substring(0, offset).count { it == ',' }
                return offset - commasBefore
            }
        }

        return TransformedText(
            AnnotatedString(newText),
            offsetMapping
        )
    }
}
