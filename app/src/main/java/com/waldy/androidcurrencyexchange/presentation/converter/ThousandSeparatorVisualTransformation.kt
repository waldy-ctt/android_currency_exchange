package com.waldy.androidcurrencyexchange.presentation.converter

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat
import kotlin.math.max

/**
 * A [VisualTransformation] that applies thousand separators to a numeric input.
 *
 * This transformation formats the integer part of a number with commas as thousand separators.
 * For example, `1234567` will be transformed to `1,234,567`.
 */
class ThousandSeparatorVisualTransformation : VisualTransformation {

    private val formatter = DecimalFormat("#,###")

    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val integerPart = originalText.split(".").getOrNull(0) ?: ""

        // To avoid Long overflow, we only format if the integer part is within a reasonable length.
        if (integerPart.length > 15) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val formattedInteger = try {
            if (integerPart.isNotEmpty()) formatter.format(integerPart.toLong()) else ""
        } catch (e: NumberFormatException) {
            // Revert to original text if formatting fails (e.g., during intermediate input)
            return TransformedText(text, OffsetMapping.Identity)
        }

        val decimalPart = originalText.split(".").getOrNull(1)

        val newText = when {
            decimalPart != null && originalText.endsWith(".") -> "$formattedInteger."
            decimalPart != null -> "$formattedInteger.$decimalPart"
            else -> formattedInteger
        }

        // cai vu offset mapping nay hoi bi kho. de mat toi may truong hop la
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val commas = formattedInteger.count { it == ',' }
                return offset + commas
            }

            override fun transformedToOriginal(offset: Int): Int {
                val commas = newText.take(offset).count { it == ',' }
                return offset - commas
            }
        }

        return TransformedText(AnnotatedString(newText), offsetMapping)
    }
}
