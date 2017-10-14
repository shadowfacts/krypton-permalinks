package net.shadowfacts.krypton.permalinks.config

import net.shadowfacts.krypton.config.Configuration
import net.shadowfacts.krypton.config.config
import java.time.ZoneId
import java.util.*

/**
 * @author shadowfacts
 */
var Configuration.permalinkIndex: String by config({ it }, fallback = { "index.html" })
var Configuration.timeZone: ZoneId by config(ZoneId::of, fallback = ZoneId::systemDefault)
var Configuration.locale: Locale by config({
	val parts = it.split("_")
	when (parts.size) {
		1 -> Locale(parts[0])
		2 -> Locale(parts[0], parts[1])
		3 -> Locale(parts[0], parts[1], parts[2])
		else -> throw RuntimeException("Invalid locale string $it")
	}
}, fallback = Locale::getDefault)