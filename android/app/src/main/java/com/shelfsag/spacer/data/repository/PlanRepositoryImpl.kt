package com.shelfsag.spacer.data.repository

import com.shelfsag.spacer.data.local.ShelfPreferences
import com.shelfsag.spacer.data.sample.SampleData
import com.shelfsag.spacer.domain.model.ShelfPlan
import com.shelfsag.spacer.domain.repository.PlanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class PlanRepositoryImpl(private val preferences: ShelfPreferences) : PlanRepository {

    private val mutex = Mutex()

    override suspend fun plans(): List<ShelfPlan> = withContext(Dispatchers.IO) {
        mutex.withLock { loadAll() }
    }

    override suspend fun plan(id: String): ShelfPlan? = withContext(Dispatchers.IO) {
        mutex.withLock { loadAll().firstOrNull { it.id == id } }
    }

    override suspend fun save(plan: ShelfPlan): ShelfPlan = withContext(Dispatchers.IO) {
        mutex.withLock {
            val current = loadAll().toMutableList()
            val stamped = plan.copy(updatedAt = System.currentTimeMillis())
            val index = current.indexOfFirst { it.id == stamped.id }
            if (index >= 0) current[index] = stamped else current.add(stamped)
            persist(current)
            stamped
        }
    }

    override suspend fun delete(id: String) {
        withContext(Dispatchers.IO) {
            mutex.withLock {
                val current = loadAll().filterNot { it.id == id }
                persist(current)
            }
        }
    }

    private fun loadAll(): List<ShelfPlan> {
        if (!preferences.isSeeded()) {
            persist(SampleData.starterPlans())
            preferences.markSeeded()
        }
        val payload = preferences.readPlans()
        if (payload.isEmpty()) return emptyList()
        return runCatching {
            val array = JSONArray(payload)
            val result = mutableListOf<ShelfPlan>()
            for (i in 0 until array.length()) {
                result.add(decode(array.getJSONObject(i)))
            }
            result.sortedByDescending { it.updatedAt }
        }.getOrElse { emptyList() }
    }

    private fun persist(plans: List<ShelfPlan>) {
        val array = JSONArray()
        plans.forEach { array.put(encode(it)) }
        preferences.writePlans(array.toString())
    }

    private fun encode(plan: ShelfPlan): JSONObject {
        val positions = JSONArray()
        plan.bracketPositions.forEach { positions.put(it) }
        val bought = JSONArray()
        plan.boughtItemIds.forEach { bought.put(it) }
        return JSONObject()
            .put("id", plan.id)
            .put("name", plan.name)
            .put("room", plan.room)
            .put("materialId", plan.materialId)
            .put("thicknessMm", plan.thicknessMm)
            .put("depthMm", plan.depthMm)
            .put("lengthMm", plan.lengthMm)
            .put("loadId", plan.loadId)
            .put("customKg", plan.customKg)
            .put("bracketPositions", positions)
            .put("studSpacingMm", plan.studSpacingMm)
            .put("boughtItemIds", bought)
            .put("updatedAt", plan.updatedAt)
    }

    private fun decode(json: JSONObject): ShelfPlan {
        val positionsJson = json.optJSONArray("bracketPositions")
        val positions = mutableListOf<Double>()
        if (positionsJson != null) {
            for (i in 0 until positionsJson.length()) positions.add(positionsJson.optDouble(i, 0.0))
        }
        val boughtJson = json.optJSONArray("boughtItemIds")
        val bought = mutableSetOf<String>()
        if (boughtJson != null) {
            for (i in 0 until boughtJson.length()) bought.add(boughtJson.optString(i, ""))
        }
        return ShelfPlan(
            id = json.optString("id", ""),
            name = json.optString("name", ""),
            room = json.optString("room", SampleData.DEFAULT_ROOM),
            materialId = json.optString("materialId", "OAK"),
            thicknessMm = json.optInt("thicknessMm", 18),
            depthMm = json.optInt("depthMm", 250),
            lengthMm = json.optInt("lengthMm", 1200),
            loadId = json.optString("loadId", "PAPERBACKS"),
            customKg = json.optDouble("customKg", 20.0),
            bracketPositions = positions,
            studSpacingMm = json.optDouble("studSpacingMm", 406.0),
            boughtItemIds = bought.filter { it.isNotEmpty() }.toSet(),
            updatedAt = json.optLong("updatedAt", 0L)
        )
    }
}
