package io.cloudx.sdk.internal.privacy

import android.content.SharedPreferences
import android.preference.PreferenceManager
import io.cloudx.sdk.RoboMockkTest
import io.cloudx.sdk.internal.ApplicationContext
import kotlinx.coroutines.test.runTest
import org.junit.Test

class TCFProviderImplTest : RoboMockkTest() {

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var subject: TCFProviderImpl

    override fun before() {
        super.before()
        val ctx = ApplicationContext()

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx)
        sharedPrefs.edit().clear()

        subject = TCFProviderImpl(ctx)
    }

    @Test
    fun tcStringShouldReturnCorrectTCStringWhenSet() = runTest {
        val tcString1 = "asdasda.asdasdasdasdas"
        val tcString2 = "kljlw;ljflsj.qdwwqdqwdq"

        sharedPrefs.edit().putString(IABTCF_TCString, tcString1).commit()
        val result1 = subject.tcString()

        sharedPrefs.edit().putString(IABTCF_TCString, tcString2).commit()
        val result2 = subject.tcString()

        assert(result1 == tcString1 && result2 == tcString2)
    }

    @Test
    fun tcStringShouldReturnNullWhenSharedPrefsFieldNotSet() = runTest {
        val result = subject.tcString()
        assert(result == null)
    }

    @Test
    fun tcStringShouldReturnNullWhenSharedPrefsFieldIsBlank() = runTest {
        sharedPrefs.edit().putString(IABTCF_TCString, "     ").commit()
        val result = subject.tcString()
        assert(result == null)
    }

    @Test
    fun tcStringShouldReturnNullWhenSharedPrefsFieldIsEmpty() = runTest {
        sharedPrefs.edit().putString(IABTCF_TCString, "").commit()
        val result = subject.tcString()
        assert(result == null)
    }

    @Test
    fun tcStringShouldReturnNullWhenSharedPrefsFieldIsOfDifferentType() = runTest {
        sharedPrefs.edit().putInt(IABTCF_TCString, 5).commit()
        val result = subject.tcString()
        assert(result == null)
    }

    @Test
    fun gdprAppliesReturnNullWhenShardPrefsFieldNotSet() = runTest {
        val result = subject.gdprApplies()
        assert(result == null)
    }

    @Test
    fun gdprAppliesShouldReturnNullWhenSharedPrefsFieldIsNotZeroOrOne() = runTest {
        sharedPrefs.edit().putInt(IABTCF_gdprApplies, 5).commit()
        val result = subject.gdprApplies()
        assert(result == null)
    }

    @Test
    fun gdprAppliesShouldReturnNullWhenSharedPrefsFieldIsOfDifferentType() = runTest {
        sharedPrefs.edit().putString(IABTCF_gdprApplies, "tomtom").commit()
        val result = subject.gdprApplies()
        assert(result == null)
    }

    @Test
    fun gdprAppliesShouldReturnTrueWhenSharedPrefsFieldIsOne() = runTest {
        sharedPrefs.edit().putInt(IABTCF_gdprApplies, 1).commit()
        val result = subject.gdprApplies()
        assert(result == true)
    }

    @Test
    fun gdprAppliesShouldReturnFalseWhenSharedPrefsFieldIsZero() = runTest {
        sharedPrefs.edit().putInt(IABTCF_gdprApplies, 0).commit()
        val result = subject.gdprApplies()
        assert(result == false)
    }
}