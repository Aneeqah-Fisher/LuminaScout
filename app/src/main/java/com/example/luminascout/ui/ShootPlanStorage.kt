package com.example.luminascout.ui

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class ShootChecklistEntry(val name: String, val checked: Boolean)

data class SavedShootPlan(
    val id: String,
    val name: String,
    val date: String, // yyyy-MM-dd
    val time: String, // HH:mm, 24-hour
    val location: String,
    val notes: String,
    val gear: List<ShootChecklistEntry>,
    val shots: List<ShootChecklistEntry>
)

/**
 * Stores shoot plans on this device. The JSON structure can later be used
 * by the Reminders screen. This does not schedule Android notifications.
 */
object ShootPlanStorage {
    private const val PREFS = "LuminaScoutShootPlans"
    private const val KEY = "saved_shoot_plans"

    fun load(context: Context): List<SavedShootPlan> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, "[]") ?: "[]"
        return try {
            val array = JSONArray(raw)
            (0 until array.length()).map { index ->
                val obj = array.getJSONObject(index)
                SavedShootPlan(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    date = obj.getString("date"),
                    time = obj.getString("time"),
                    location = obj.getString("location"),
                    notes = obj.optString("notes", ""),
                    gear = readChecklist(obj.optJSONArray("gear")),
                    shots = readChecklist(obj.optJSONArray("shots"))
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun save(context: Context, plan: SavedShootPlan): Boolean {
        val existing = load(context).toMutableList()
        val index = existing.indexOfFirst { it.id == plan.id }
        if (index >= 0) existing[index] = plan else existing.add(plan)
        val array = JSONArray()
        existing.forEach { item ->
            array.put(JSONObject().apply {
                put("id", item.id)
                put("name", item.name)
                put("date", item.date)
                put("time", item.time)
                put("location", item.location)
                put("notes", item.notes)
                put("gear", writeChecklist(item.gear))
                put("shots", writeChecklist(item.shots))
            })
        }
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY, array.toString()).commit()
    }

    private fun writeChecklist(entries: List<ShootChecklistEntry>): JSONArray =
        JSONArray().apply {
            entries.forEach { entry ->
                put(JSONObject().apply {
                    put("name", entry.name)
                    put("checked", entry.checked)
                })
            }
        }

    private fun readChecklist(array: JSONArray?): List<ShootChecklistEntry> {
        if (array == null) return emptyList()
        return (0 until array.length()).map { i ->
            val item = array.getJSONObject(i)
            ShootChecklistEntry(item.optString("name"), item.optBoolean("checked"))
        }
    }
}
