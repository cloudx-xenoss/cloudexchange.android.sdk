package io.cloudx.sdk.internal.tracking.dtoconverters

import io.cloudx.sdk.internal.tracking.InitOperationStatus

internal fun InitOperationStatus.toDB(): io.cloudx.sdk.internal.db.tracking.InitOperationStatus =
    io.cloudx.sdk.internal.db.tracking.InitOperationStatus(
        success = success,
        startedAtUnixMillis = startedAtUnixMillis,
        endedAtUnixMillis = endedAtUnixMillis,
        appKey = appKey,
        sessionId = sessionId
    )