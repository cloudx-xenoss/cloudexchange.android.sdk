package io.cloudx.sdk.internal.privacy

import android.content.SharedPreferences
import android.preference.PreferenceManager
import io.cloudx.sdk.RoboMockkTest
import io.cloudx.sdk.internal.ApplicationContext
import kotlinx.coroutines.test.runTest
import org.junit.Test

class USPrivacyProviderImplTest : RoboMockkTest() {

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var subject: USPrivacyProviderImpl

    override fun before() {
        super.before()
        val ctx = ApplicationContext()

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx)
        sharedPrefs.edit().clear()

        subject = USPrivacyProviderImpl(ctx)
    }

    @Test
    fun shouldReturnCorrectUsPrivacyStringWhenSet() = runTest {
        val usPrivacyString1 = "1YYY"
        val usPrivacyString2 = "1---"

        sharedPrefs.edit().putString(IABUSPrivacy_String, usPrivacyString1).commit()
        val result1 = subject.usPrivacyString()

        sharedPrefs.edit().putString(IABUSPrivacy_String, usPrivacyString2).commit()
        val result2 = subject.usPrivacyString()

        assert(result1 == usPrivacyString1 && result2 == usPrivacyString2)
    }

    @Test
    fun shouldReturnNullWhenSharedPrefsFieldNotSet() = runTest {
        val result = subject.usPrivacyString()
        assert(result == null)
    }

    @Test
    fun shouldReturnNullWhenSharedPrefsFieldIsBlank() = runTest {
        sharedPrefs.edit().putString(IABUSPrivacy_String, "     ").commit()
        val result = subject.usPrivacyString()
        assert(result == null)
    }

    @Test
    fun shouldReturnNullWhenSharedPrefsFieldIsEmpty() = runTest {
        sharedPrefs.edit().putString(IABUSPrivacy_String, "").commit()
        val result = subject.usPrivacyString()
        assert(result == null)
    }

    @Test
    fun shouldReturnNullWhenSharedPrefsFieldIsOfDifferentType() = runTest {
        sharedPrefs.edit().putInt(IABUSPrivacy_String, 5).commit()
        val result = subject.usPrivacyString()
        assert(result == null)
    }
}