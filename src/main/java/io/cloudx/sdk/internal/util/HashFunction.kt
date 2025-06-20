package io.cloudx.sdk.internal.util

fun normalizeAndHash(dataToHash: String, algo: String = "md5"): String {
    val normalized = dataToHash.trim().lowercase()
    val digest = when (algo) {
        "md5" -> "MD5"
        "sha1" -> "SHA-1"
        "sha256" -> "SHA-256"
        else -> throw IllegalArgumentException("Unsupported hash algorithm: $algo")
    }
    return java.math.BigInteger(
        1, java.security.MessageDigest.getInstance(digest)
            .digest(normalized.toByteArray())
    ).toString(16).padStart(
        when (digest) {
            "MD5" -> 32
            "SHA-1" -> 40
            else -> 64 // SHA-256
        }, '0'
    )
}