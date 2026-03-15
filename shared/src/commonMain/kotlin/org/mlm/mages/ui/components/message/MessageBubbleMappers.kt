package org.mlm.mages.ui.components.message

import org.mlm.mages.MessageEvent

fun MessageEvent.toBubbleModel(
    isMine: Boolean,
    isDm: Boolean,
    avatarPath: String?,
    groupedWithPrev: Boolean,
    groupedWithNext: Boolean,
    threadCount: Int? = null,
    variant: MessageBubbleVariant = MessageBubbleVariant.Timeline,
): MessageBubbleModel {
    return MessageBubbleModel(
        eventId = eventId,
        isMine = isMine,
        body = body,
        formattedBody = formattedBody,
        sender = MessageSenderUi(
            id = sender,
            displayName = senderDisplayName,
            avatarPath = avatarPath,
        ),
        timestamp = timestampMs,
        isDm = isDm,
        grouping = MessageGroupingUi(
            groupedWithPrev = groupedWithPrev,
            groupedWithNext = groupedWithNext,
        ),
        reactions = reactions,
        reply = MessageReplyUi(
            sender = replyToSenderDisplayName,
            body = replyToBody,
        ),
        sendState = sendState,
        attachment = attachment?.let {
            MessageAttachmentUi(
                thumbPath = it.thumbnailMxcUri,
                kind = it.kind,
                width = it.width,
                height = it.height,
                durationMs = it.durationMs,
            )
        },
        isEdited = isEdited,
        poll = pollData,
        thread = threadCount?.let { count -> MessageThreadUi(count) },
        variant = variant,
    )
}
