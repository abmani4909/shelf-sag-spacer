package com.shelfsag.spacer.domain.usecase

import com.shelfsag.spacer.domain.model.SagRating

class RateSagUseCase {

    operator fun invoke(
        sagMm: Double,
        spanMm: Double,
        limitK: Int,
        hardLimitMm: Double
    ): SagRating {
        val byRatio = when {
            spanMm <= 0.0 || limitK <= 0 -> SagRating.INVISIBLE
            sagMm <= spanMm / limitK -> SagRating.INVISIBLE
            sagMm <= spanMm / 180.0 -> SagRating.NOTICEABLE
            else -> SagRating.SAGGING
        }
        val byAbsolute = when {
            hardLimitMm <= 0.0 -> SagRating.INVISIBLE
            sagMm <= hardLimitMm * 0.4 -> SagRating.INVISIBLE
            sagMm <= hardLimitMm -> SagRating.NOTICEABLE
            else -> SagRating.SAGGING
        }
        return if (byRatio.ordinal >= byAbsolute.ordinal) byRatio else byAbsolute
    }
}
