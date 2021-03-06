package com.tangem.tangemtest.ucase.domain.paramsManager.triggers.afterAction

import com.tangem.commands.Card
import com.tangem.commands.CardStatus
import com.tangem.common.CompletionResult
import com.tangem.tangemtest._arch.structure.PayloadHolder
import com.tangem.tangemtest._arch.structure.abstraction.Item
import com.tangem.tangemtest._arch.structure.abstraction.findItem
import com.tangem.tangemtest.ucase.domain.paramsManager.PayloadKey
import com.tangem.tangemtest.ucase.tunnel.ActionView
import com.tangem.tangemtest.ucase.tunnel.CardError
import com.tangem.tangemtest.ucase.variants.TlvId

import ru.dev.gbixahue.eu4d.lib.android.global.threading.postUI

/**
 * Created by Anton Zhilenkov on 19/03/2020.
 */
class AfterScanModifier : AfterActionModification {
    override fun modify(payload: PayloadHolder, commandResult: CompletionResult<*>, itemList: List<Item>): List<Item> {
        val foundItem = itemList.findItem(TlvId.CardId) ?: return listOf()
        val card = smartCast(commandResult) ?: return listOf()
        val actionView = payload.get(PayloadKey.actionView) as? ActionView ?: return listOf()

        return if (isNotPersonalized(card)) {
            actionView.showSnackbar(CardError.NotPersonalized)
            postUI { actionView.enableActionFab(false) }
            listOf()
        } else {
            payload.set(PayloadKey.card, card)
            foundItem.setData(card.cardId)
            postUI { actionView.enableActionFab(true) }
            listOf(foundItem)
        }
    }

    private fun smartCast(commandResult: CompletionResult<*>): Card? {
        return (commandResult as? CompletionResult.Success<Card>)?.data
    }

    private fun isNotPersonalized(card: Card): Boolean = card.status == CardStatus.NotPersonalized
}