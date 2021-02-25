package top.intkilow.architecture.utils

import android.view.View

object Debounce {
  @Volatile
  private var enabled: Boolean = true
  private val enableAgain = Runnable { enabled = true }

  fun canPerform(view: View): Boolean {
    if (enabled) {
      enabled = false
      view.post(enableAgain)
      return true
    }
    return false
  }
}

fun <T : View> T.setOnClickDebounced(click: (view: T) -> Unit) {
  setOnClickListener {
    if (Debounce.canPerform(it)) {
      @Suppress("UNCHECKED_CAST")
      click(it as T)
    }
  }
}
