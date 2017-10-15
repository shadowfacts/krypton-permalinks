package net.shadowfacts.krypton.permalinks

import net.shadowfacts.krypton.Page
import net.shadowfacts.krypton.permalinks.config.locale
import net.shadowfacts.krypton.permalinks.config.permalinkIndex
import net.shadowfacts.krypton.permalinks.config.timeZone
import net.shadowfacts.krypton.pipeline.stage.Stage
import java.io.File
import java.time.format.TextStyle
import java.util.*

/**
 * @author shadowfacts
 */
class StagePermalinks: Stage() {

	override val id = "permalinks"

	override fun scan(page: Page) {
	}

	override fun apply(page: Page, input: String): String {
		if ("permalink" in page.metadata) {
			val permalinkData = createPermalinkData(page)

			var permalink = page.metadata["permalink"] as String // e.g. /%year%/%month%/%title%/
			val parts = permalink.split("%").toMutableList()
			for (i in parts.indices.filter { it % 2 == 1}) {
				parts[i] = permalinkData[parts[i]] ?: throw RuntimeException("No permalink substitution for %${parts[i]}%")
			}
			permalink = parts.joinToString("")
			page.metadata["permalink"] = permalink
			if (permalink.endsWith("/")) permalink += page.krypton.config.permalinkIndex
			val destination = File(page.krypton.config.output, permalink)
			page.output = destination
		}
		return input
	}

	private fun createPermalinkData(page: Page): Map<String, String> {
		val data = mutableMapOf<String, String>()

		page.metadata.forEach { k, v ->
			if (v is String) data[k] = v
		}

		page.metadata["date"]?.also {
			if (it is Date) {
				val date = it.toInstant().atZone(page.krypton.config.timeZone)
				data["year"] = date.year.toString()
				data["monthName"] = date.month.getDisplayName(TextStyle.FULL, page.krypton.config.locale).toLowerCase()
				data["month"] = date.monthValue.toString()
				data["day"] = date.dayOfMonth.toString()
			}
		}

		data["filename"] = page.source.name
		data["filenameWithoutExt"] = page.source.nameWithoutExtension

		return data
	}

}