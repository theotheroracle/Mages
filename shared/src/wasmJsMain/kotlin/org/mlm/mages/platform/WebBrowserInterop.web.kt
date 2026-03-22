package org.mlm.mages.platform

import kotlin.js.JsAny
import kotlin.js.Promise
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event
import org.w3c.files.File

internal fun clipboardDataOf(event: Event): JsAny? =
    js("event.clipboardData ? event.clipboardData : null")

internal fun dataTransferOf(event: Event): JsAny? =
    js("event.dataTransfer ? event.dataTransfer : null")

internal fun dataTransferItemsOf(transfer: JsAny): JsAny? =
    js("transfer.items ? transfer.items : null")

internal fun dataTransferFilesOf(transfer: JsAny): JsAny? =
    js("transfer.files ? transfer.files : null")

internal fun arrayLikeLength(value: JsAny): Int =
    js("value.length | 0")

internal fun arrayLikeItem(value: JsAny, index: Int): JsAny? =
    js("(value[index] !== undefined) ? value[index] : null")

internal fun dataTransferItemKind(item: JsAny): String? =
    js("item.kind ? item.kind : null")

internal fun dataTransferItemGetAsFile(item: JsAny): File? =
    js("item.getAsFile ? item.getAsFile() : null")

internal fun fileListItem(files: JsAny, index: Int): File? =
    js("files.item ? files.item(index) : ((files[index] !== undefined) ? files[index] : null)")

internal fun inputSelectedFile(input: HTMLInputElement): File? =
    js("input.files ? input.files.item(0) : null")

internal fun windowIsSecureContext(): Boolean =
    js("window.isSecureContext === true")

internal fun navigatorShareSupported(): Boolean =
    js("typeof navigator !== 'undefined' && typeof navigator.share === 'function'")

internal fun navigatorCanShareSupported(): Boolean =
    js("typeof navigator !== 'undefined' && typeof navigator.canShare === 'function'")

internal fun shareDataCreate(): JsAny =
    js("({})")

internal fun shareDataSetTitle(data: JsAny, title: String): Unit =
    js("{ data.title = title; }")

internal fun shareDataSetText(data: JsAny, text: String): Unit =
    js("{ data.text = text; }")

internal fun shareDataSetUrl(data: JsAny, url: String): Unit =
    js("{ data.url = url; }")

internal fun navigatorCanShare(data: JsAny): Boolean =
    js("navigator.canShare(data)")

internal fun navigatorShare(data: JsAny): Promise<JsAny?> =
    js("navigator.share(data)")

internal fun navigatorClipboardWriteTextSupported(): Boolean =
    js("typeof navigator !== 'undefined' && !!navigator.clipboard && typeof navigator.clipboard.writeText === 'function'")

internal fun navigatorClipboardWriteText(text: String): Promise<JsAny?> =
    js("navigator.clipboard.writeText(text)")

internal fun documentExecCopy(): Boolean =
    js("document.execCommand('copy')")

internal fun textareaSelect(textarea: HTMLTextAreaElement): Unit =
    js("textarea.select()")

internal fun notificationSupported(): Boolean =
    js("typeof Notification !== 'undefined'")

internal fun notificationPermission(): String =
    js("typeof Notification !== 'undefined' ? Notification.permission : 'denied'")

internal fun notificationRequestPermission(): Promise<JsAny?> =
    js("typeof Notification !== 'undefined' ? Notification.requestPermission() : Promise.resolve('denied')")

internal fun createNotification(title: String, body: String?, icon: String?): JsAny? =
    js("typeof Notification !== 'undefined' ? new Notification(title, { body: body || undefined, icon: icon || undefined }) : null")

internal fun navigatorOnLine(): Boolean =
    js("typeof navigator !== 'undefined' ? navigator.onLine === true : true")

internal fun documentHasFocus(): Boolean =
    js("typeof document !== 'undefined' && document.hasFocus() === true")

internal fun dataTransferHasFiles(transfer: JsAny?): Boolean {
    if (transfer == null) return false

    val items = dataTransferItemsOf(transfer)
    if (items != null) {
        val count = arrayLikeLength(items)
        for (i in 0 until count) {
            val item = arrayLikeItem(items, i) ?: continue
            if (dataTransferItemKind(item) == "file") return true
        }
    }

    val files = dataTransferFilesOf(transfer) ?: return false
    return arrayLikeLength(files) > 0
}

internal fun extractFilesFromDataTransfer(transfer: JsAny?): List<File> {
    if (transfer == null) return emptyList()

    val items = dataTransferItemsOf(transfer)
    if (items != null) {
        val fromItems = mutableListOf<File>()
        val count = arrayLikeLength(items)
        for (i in 0 until count) {
            val item = arrayLikeItem(items, i) ?: continue
            if (dataTransferItemKind(item) != "file") continue
            dataTransferItemGetAsFile(item)?.let(fromItems::add)
        }
        if (fromItems.isNotEmpty()) return fromItems
    }

    val files = dataTransferFilesOf(transfer) ?: return emptyList()
    val out = mutableListOf<File>()
    val count = arrayLikeLength(files)
    for (i in 0 until count) {
        fileListItem(files, i)?.let(out::add)
    }
    return out
}
