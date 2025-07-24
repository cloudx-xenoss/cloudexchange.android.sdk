package io.cloudx.sdk.internal.tracking.dtoconverters

import io.cloudx.sdk.internal.tracking.InitOperationStatus

internal fun InitOperationStatus.toDB(): io.cloudx.sdk.internal.db.tracking_legacy.InitOperationStatus =
    io.cloudx.sdk.internal.db.tracking_legacy.InitOperationStatus(
        success = success,
        startedAtUnixMillis = startedAtUnixMillis,
        endedAtUnixMillis = endedAtUnixMillis,
        appKey = appKey,
        sessionId = sessionId
    )