package com.tangem.tangemtest._arch.widget.abstraction

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import com.tangem.tangemtest.R
import com.tangem.tangemtest._arch.structure.StringId
import com.tangem.tangemtest._arch.structure.StringResId
import com.tangem.tangemtest._arch.structure.abstraction.Item
import com.tangem.tangemtest._arch.structure.abstraction.ViewState
import ru.dev.gbixahue.eu4d.lib.android._android.views.colorFrom
import ru.dev.gbixahue.eu4d.lib.kotlin.common.LayoutHolder

/**
 * Created by Anton Zhilenkov on 19/03/2020.
 */
interface ViewWidget : LayoutHolder {
    val view: View
    var item: Item

    fun getName(): String
    fun setBackgroundColor(@ColorRes colorId: Int?)
}

abstract class BaseViewWidget(
        parent: ViewGroup,
        override var item: Item
) : ViewWidget {

    override val view: View = inflate(getLayoutId(), parent)

    private var defaultBackground: Drawable? = view.background

    init {
        subscribeToViewStateChanges(item.viewModel.viewState)
        initViewState(item.viewModel.viewState)
    }

    protected open fun subscribeToViewStateChanges(viewState: ViewState) {
        viewState.isVisibleState.onValueChanged = { state ->
            state?.let { view.visibility = if (it) View.VISIBLE else View.GONE }
        }
        viewState.backgroundColor.onValueChanged = { setBackgroundColor(it) }
    }

    protected open fun initViewState(viewState: ViewState) {
        viewState.preventSameChanges(false)
        viewState.isVisibleState.value = viewState.isVisibleState.value
        if (viewState.backgroundColor.value != -1) setBackgroundColor(viewState.backgroundColor.value)
        viewState.preventSameChanges(true)
    }

    override fun getName(): String {
        return when (val id = item.id) {
            is StringId -> id.value
            is StringResId -> view.resources.getString(id.value)
            else -> view.resources.getString(R.string.unknown)
        }
    }

    override fun setBackgroundColor(colorId: Int?) {
        val background = when {
            colorId == null -> null
            colorId == -1 -> defaultBackground
            else -> ColorDrawable(view.colorFrom(colorId))
        }
        view.background = background
    }
}

internal fun inflate(id: Int, parent: ViewGroup): View {
    val layoutId = if (id <= 0) R.layout.w_empty else id
    val inflatedView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
    parent.addView(inflatedView)
    return inflatedView
}
