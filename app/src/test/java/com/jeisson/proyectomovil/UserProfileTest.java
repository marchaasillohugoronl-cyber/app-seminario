package com.jeisson.proyectomovil;

import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class UserProfileTest {
    @Test public void accountWithoutNamesMustCompleteRegistration() {
        assertFalse(UserProfile.isComplete(null, null));
        assertFalse(UserProfile.isComplete("Ana", "  "));
        assertFalse(UserProfile.isComplete("", "Pérez"));
        assertTrue(UserProfile.isComplete("Ana", "Pérez"));
    }

    @Test public void registrationProducesFieldsConsumedByDashboardAndMyData() {
        Map<String, Object> profile = UserProfile.registration("uid-1", "ana@example.com", " Ana ", " Pérez ");
        assertEquals("uid-1", profile.get("uid"));
        assertEquals("ana@example.com", profile.get("mail"));
        assertEquals("Ana", profile.get("nombres"));
        assertEquals("Pérez", profile.get("apellidos"));
        assertFalse(profile.containsKey("password"));
    }

    @Test public void retryPreservesOptionalProfileData() {
        Map<String, Object> existing = new HashMap<>();
        existing.put("imagenBase64", "photo");
        existing.put("telefono", "999111222");
        existing.put("fechaNacimiento", "2000-01-01");
        existing.putAll(UserProfile.registration("uid-1", "ana@example.com", "Ana", "Pérez"));
        assertEquals("photo", existing.get("imagenBase64"));
        assertEquals("999111222", existing.get("telefono"));
        assertEquals("2000-01-01", existing.get("fechaNacimiento"));
    }
}
