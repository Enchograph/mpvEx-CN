package app.marlboroadvance.mpvex.utils.media

import app.marlboroadvance.mpvex.preferences.SubtitleAutoSelectMode
import java.util.Locale

/**
 * Shared ordering rules for automatic subtitle selection.
 *
 * Used by both SubtitleOps (external file autoload) and TrackSelector (mpv track list)
 * so the selected subtitle converges regardless of which callback runs first.
 * Higher [rankKey] wins; candidates with equal rank keep their original order.
 */
object SubtitleSelection {
  private val langAliases =
    mapOf(
      "chs" to "zh-hans",
      "sc" to "zh-hans",
      "zh-cn" to "zh-hans",
      "zh-sg" to "zh-hans",
      "cht" to "zh-hant",
      "tc" to "zh-hant",
      "zh-tw" to "zh-hant",
      "zh-hk" to "zh-hant",
      "jp" to "ja",
      "jpn" to "ja",
    )

  fun rankKey(
    mode: SubtitleAutoSelectMode,
    sameName: Boolean,
    langHit: Boolean,
  ): Int =
    when (mode) {
      SubtitleAutoSelectMode.SameName -> if (sameName) 2 else if (langHit) 1 else 0
      SubtitleAutoSelectMode.PreferredLanguage -> if (langHit) 2 else if (sameName) 1 else 0
      SubtitleAutoSelectMode.Off -> 0
    }

  fun isSameName(subtitleStem: String, videoStem: String): Boolean =
    subtitleStem.equals(videoStem, ignoreCase = true)

  /** Language tag embedded after the video name (e.g. "zh-hans" in "X.zh-hans.ass"); null if absent. */
  fun extractLangTag(subtitleStem: String, videoStem: String): String? {
    if (!subtitleStem.startsWith(videoStem, ignoreCase = true)) return null
    val suffix = subtitleStem.substring(videoStem.length).trimStart('.', '-', '_', ' ')
    if (suffix.isEmpty()) return null
    return suffix.substringBefore('.').lowercase(Locale.ROOT).ifEmpty { null }
  }

  fun langMatches(
    subtitleStem: String,
    videoStem: String,
    preferredLangs: List<String>,
  ): Boolean {
    val tag = extractLangTag(subtitleStem, videoStem) ?: return false
    val normalized = langAliases[tag] ?: tag
    return preferredLangs.any { normalized == it || normalized.startsWith(it) }
  }
}
