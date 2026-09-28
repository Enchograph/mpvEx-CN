package app.marlboroadvance.mpvex.preferences

enum class SeekbarStyle {
  Standard,
  Wavy,
  Thick,
  ;

  val displayName: String
    get() =
      when (this) {
        Standard -> "标准"
        Wavy -> "波浪"
        Thick -> "加粗"
      }
}
