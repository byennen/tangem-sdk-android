package com.tangem.tangemtest._arch.structure.abstraction

import com.tangem.tangemtest._arch.structure.Id

/**
 * Created by Anton Zhilenkov on 22/03/2020.
 */

interface UpdateBy<B>{
    fun update(value: B)
}

interface Item: UpdateBy<Item> {
    val id: Id
    var parent: Item?
    var viewModel: ItemViewModel

    fun added(parent: Item) {
        this.parent = parent
    }

    fun removed(parent: Item) {
        this.parent = null
    }

    fun <D> getData(): D? = viewModel.data as? D

    fun setData(value: Any?) {
        viewModel.data = value
    }

    fun restoreDefaultData() {
        setData(viewModel.defaultData)
    }
}

open class BaseItem(
        override val id: Id,
        override var viewModel: ItemViewModel
) : Item {

    override var parent: Item? = null

    override fun update(value: Item) {
        viewModel.update(value.viewModel)
    }
}