package org.thoughtcrime.securesms.keyboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.thoughtcrime.securesms.dependencies.ApplicationDependencies
import org.thoughtcrime.securesms.keyvalue.SignalStore
import org.thoughtcrime.securesms.stickers.StickerSearchRepository
import org.thoughtcrime.securesms.util.DefaultValueLiveData

class KeyboardPagerViewModel : ViewModel() {

  private val page: DefaultValueLiveData<KeyboardPage>
  private val pages: DefaultValueLiveData<Set<KeyboardPage>>

  init {
    val startingPages: MutableSet<KeyboardPage> = KeyboardPage.values().toMutableSet()
    if (SignalStore.settings().isPreferSystemEmoji) {
      startingPages.remove(KeyboardPage.EMOJI)
    }
    pages = DefaultValueLiveData(startingPages)
    page = DefaultValueLiveData(startingPages.first())

    StickerSearchRepository(ApplicationDependencies.getApplication()).getStickerFeatureAvailability { available ->
      if (!available) {
        val updatedPages = pages.value.toMutableSet().apply { remove(KeyboardPage.STICKER) }
        pages.postValue(updatedPages)
        if (page.value == KeyboardPage.STICKER) {
          switchToPage(KeyboardPage.GIF)
          switchToPage(KeyboardPage.EMOJI)
        }
      }
    }
  }

  fun page(): LiveData<KeyboardPage> = page
  fun pages(): LiveData<Set<KeyboardPage>> = pages

  fun setOnlyPage(page: KeyboardPage) {
    pages.postValue(setOf(page))
    switchToPage(page)
  }

  fun switchToPage(page: KeyboardPage) {
    if (this.pages.value.contains(page) && this.page.value != page) {
      this.page.postValue(page)
    }
  }
}
