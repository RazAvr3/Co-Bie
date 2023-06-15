package com.example.co_bie;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.co_bie.LoginAndRegistration.RegistrationFragment;

import com.example.co_bie.Event.CreateEventFragment;
import com.example.co_bie.Event.CreateEventFragment.CreateEventListener;
import com.example.co_bie.Event.Utils.EventType;
import com.example.co_bie.Event.Utils.Platform;
import com.example.co_bie.Hobby.Hobby;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class UnitTests {

    private RegistrationFragment registrationFragment;
    private CreateEventFragment createEventFragment;

    @Mock
    private RegistrationFragment.ContinueListener mockListenerReg;

    private CreateEventListener mockListenerCreateEvent;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        registrationFragment = new RegistrationFragment();
        registrationFragment.contListener = mockListenerReg;
        createEventFragment = new CreateEventFragment();
        createEventFragment.createEventListener = mockListenerCreateEvent;
    }

    @Test
    public void checkAllFields_ValidFields_ReturnsFalse() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        String fullName = "John Doe";
        String username = "johndoe";
        String birthDate = "01/01/2000";

        // Act
        boolean result = registrationFragment.checkAllFields(email, password, fullName, username, birthDate);

        // Assert
        assertFalse(result);
    }

    @Test
    public void checkAllFields_EmptyFields_ReturnsTrue() {
        // Arrange
        String email = "";
        String password = "";
        String fullName = "";
        String username = "";
        String birthDate = "";

        // Act
        boolean result = registrationFragment.checkAllFields(email, password, fullName, username, birthDate);

        // Assert
        assertTrue(result);
    }

    @Test
    public void onClickContinue_ValidFields_CallsListener() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        String fullName = "John Doe";
        String username = "johndoe";
        String birthDate = "01/01/2000";
        String gender = "Male";

        // Act
        registrationFragment.contListener.onClickContinue(email, fullName, username, password, birthDate, gender);

        // Assert
        verify(mockListenerReg).onClickContinue(email, fullName, username, password, birthDate, gender);
    }

    @Test
    public void checkAllFields_NullFields_ReturnsTrue() {
        // Arrange
        String email = null;
        String password = null;
        String fullName = null;
        String username = null;
        String birthDate = null;

        // Act
        boolean result = registrationFragment.checkAllFields(email, password, fullName, username, birthDate);

        // Assert
        assertTrue(result);
    }

    @Test
    public void onClickContinue_InvalidFields_DoesNotCallListener() {
        // Arrange
        String email = "";
        String password = "pass"; // Invalid password
        String fullName = "John Doe";
        String username = "johndoe";
        String birthDate = "01/01/2000";
        String gender = "Male";

        // Act
        registrationFragment.contListener.onClickContinue(email, fullName, username, password, birthDate, gender);

        // Assert
        verify(mockListenerReg, never()).onClickContinue(email, fullName, username, password, birthDate, gender);
    }

    @Test
    public void onClickContinue_ValidFields_CallsListenerWithCorrectParameters() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        String fullName = "John Doe";
        String username = "johndoe";
        String birthDate = "01/01/2000";
        String gender = "Male";

        // Act
        registrationFragment.contListener.onClickContinue(email, fullName, username, password, birthDate, gender);

        // Assert
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> fullNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> birthDateCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> genderCaptor = ArgumentCaptor.forClass(String.class);

        verify(mockListenerReg, times(1)).onClickContinue(
                emailCaptor.capture(),
                fullNameCaptor.capture(),
                usernameCaptor.capture(),
                passwordCaptor.capture(),
                birthDateCaptor.capture(),
                genderCaptor.capture()
        );

        assertEquals(email, emailCaptor.getValue());
        assertEquals(fullName, fullNameCaptor.getValue());
        assertEquals(username, usernameCaptor.getValue());
        assertEquals(password, passwordCaptor.getValue());
        assertEquals(birthDate, birthDateCaptor.getValue());
        assertEquals(gender, genderCaptor.getValue());
    }

    @Test
    public void onCreateView_PhysicalEventType_ViewInitialization() {
        // Arrange
        when(createEventFragment.handleArgsEventType()).thenReturn(EventType.PHYSICAL);

        // Act
        createEventFragment.onCreateView(null, null, null);

        // Assert
        assertNotNull(createEventFragment.tvLocation);
        assertEquals(View.VISIBLE, createEventFragment.tvLocation.getVisibility());
        assertEquals("Physical Event", createEventFragment.pageTitle.getText().toString());
    }

    @Test
    public void onCreateView_VirtualEventType_ViewInitialization() {
        // Arrange
        when(createEventFragment.handleArgsEventType()).thenReturn(EventType.VIRTUAL);

        // Act
        createEventFragment.onCreateView(null, null, null);

        // Assert
        assertNotNull(createEventFragment.tvPlatform);
        assertNotNull(createEventFragment.tvLink);
        assertEquals(View.VISIBLE, createEventFragment.tvPlatform.getVisibility());
        assertEquals(View.VISIBLE, createEventFragment.tvLink.getVisibility());
    }

    @Test
    public void handleArgsEventType_PhysicalEventType_ReturnsPhysical() {
        // Arrange
        Bundle args = new Bundle();
        args.putString("EventType", "Physical");
        createEventFragment.setArguments(args);

        // Act
        EventType eventType = createEventFragment.handleArgsEventType();

        // Assert
        assertEquals(EventType.PHYSICAL, eventType);
    }

    @Test
    public void handleArgsEventType_VirtualEventType_ReturnsVirtual() {
        // Arrange
        Bundle args = new Bundle();
        args.putString("EventType", "Virtual");
        createEventFragment.setArguments(args);

        // Act
        EventType eventType = createEventFragment.handleArgsEventType();

        // Assert
        assertEquals(EventType.VIRTUAL, eventType);
    }

    @Test
    public void locationPicker_Clicked_CallsHandleCreateEventFragment() {
        // Act
        createEventFragment.locationPicker();

        // Assert
        verify(mockListenerCreateEvent).handleCrateEventFragment(1);
    }

    @Test
    public void platformPicker_SelectedPlatform_CorrectlyMapsPlatform() {
        // Act
        createEventFragment.mapSelectedPlatform("ZOOM");

        // Assert
        assertEquals(Platform.ZOOM, createEventFragment.eventPlatform);
    }

    @Test
    public void createEvent_PhysicalEventType_AllFieldsFilled_CallsAddManagerToEventAndLoadingBarMethods() {
        // Arrange
        createEventFragment.eventType = EventType.PHYSICAL;
        createEventFragment.tvLocation = new TextView(null);
        createEventFragment.etName = new EditText(null);
        createEventFragment.tvHobby = new TextView(null);
        createEventFragment.tvDate = new TextView(null);
        createEventFragment.tvTime = new TextView(null);
        createEventFragment.tvDuration = new TextView(null);
        createEventFragment.etDescription = new EditText(null);
        createEventFragment.mAuth = Mockito.mock(FirebaseAuth.class);
        createEventFragment.refDatabase = Mockito.mock(DatabaseReference.class);

        // Act
        createEventFragment.createEvent();

        // Assert
        verify(createEventFragment).addManagerToEvent(Mockito.any(), Mockito.eq("PHYSICAL"));
        verify(createEventFragment).loadingBarAndGoToMainActivity();
    }

    @Test
    public void createEvent_VirtualEventType_AllFieldsFilled_CallsAddManagerToEventAndLoadingBarMethods() {
        // Arrange
        createEventFragment.eventType = EventType.VIRTUAL;
        createEventFragment.etName = new EditText(null);
        createEventFragment.tvHobby = new TextView(null);
        createEventFragment.tvPlatform = new TextView(null);
        createEventFragment.tvLink = new EditText(null);
        createEventFragment.tvDate = new TextView(null);
        createEventFragment.tvTime = new TextView(null);
        createEventFragment.tvDuration = new TextView(null);
        createEventFragment.etDescription = new EditText(null);
        createEventFragment.mAuth = Mockito.mock(FirebaseAuth.class);
        createEventFragment.refDatabase = Mockito.mock(DatabaseReference.class);

        // Act
        createEventFragment.createEvent();

        // Assert
        verify(createEventFragment).addManagerToEvent(Mockito.any(), Mockito.eq("VIRTUAL"));
        verify(createEventFragment).loadingBarAndGoToMainActivity();
    }
}