package io.cloudx.sdk

/**
 * CloudX targeting object, passed into [CloudX.setTargeting] API
 *
 * @property userID App/game identifier of the user, for example, game nickname, alias etc.
 * @property age user's age.
 * @property yob year of birth as a 4-digit integer
 * @property gender [Gender] of the user
 * @property keywords list of keywords, interests, or intent
 * @property data additional user data
 */
data class CloudXTargeting(
    val userID: String? = null,
    val age: Int? = null,
    val yob: Int? = null,
    val gender: Gender? = null,
    val keywords: List<String>? = null,
    val data: Map<String, String>? = null,
) {

    enum class Gender {
        Male, Female, Other
    }
}