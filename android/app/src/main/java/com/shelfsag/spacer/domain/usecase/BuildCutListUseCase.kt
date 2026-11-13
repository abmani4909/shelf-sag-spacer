package com.shelfsag.spacer.domain.usecase

import com.shelfsag.spacer.core.config.GameConfig
import com.shelfsag.spacer.domain.model.CutListItem
import com.shelfsag.spacer.domain.model.CutListKind
import com.shelfsag.spacer.domain.model.CutListSection
import com.shelfsag.spacer.domain.model.ShelfPlan
import kotlin.math.floor
import kotlin.math.max

class BuildCutListUseCase {

    operator fun invoke(plan: ShelfPlan, bracketCount: Int): List<CutListItem> {
        val brackets = max(bracketCount, GameConfig.MIN_BRACKETS)
        val arm = floor(
            max(plan.depthMm - GameConfig.BRACKET_ARM_GAP_MM, 50.0) / 10.0
        ) * 10.0
        val items = mutableListOf(
            CutListItem(
                id = "board",
                section = CutListSection.BOARDS,
                kind = CutListKind.BOARD,
                quantity = 1,
                material = plan.material,
                lengthMm = plan.lengthMm.toDouble(),
                depthMm = plan.depthMm.toDouble(),
                thicknessMm = plan.thicknessMm.toDouble(),
                buyLengthMm = plan.lengthMm * GameConfig.TRIM_MARGIN
            ),
            CutListItem(
                id = "bracket",
                section = CutListSection.HARDWARE,
                kind = CutListKind.BRACKET,
                quantity = brackets,
                material = plan.material,
                armMm = arm
            ),
            CutListItem(
                id = "wall_screw",
                section = CutListSection.FASTENERS,
                kind = CutListKind.WALL_SCREW,
                quantity = brackets * 2,
                material = plan.material
            ),
            CutListItem(
                id = "shelf_screw",
                section = CutListSection.FASTENERS,
                kind = CutListKind.SHELF_SCREW,
                quantity = brackets * 2,
                material = plan.material
            )
        )
        if (plan.studSpacingMm <= 0.0) {
            items.add(
                CutListItem(
                    id = "wall_plug",
                    section = CutListSection.FASTENERS,
                    kind = CutListKind.WALL_PLUG,
                    quantity = brackets * 2,
                    material = plan.material
                )
            )
        }
        return items
    }
}
