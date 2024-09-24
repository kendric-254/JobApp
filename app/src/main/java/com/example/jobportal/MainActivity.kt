package com.example.jobportal

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jobportal.ui.theme.JobPortalTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private var isUserLoggedIn by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = FirebaseAuth.getInstance()
        setContent {
            JobPortalTheme {
                MainScreen()
            }
        }
    }

    @Composable
    fun MainScreen() {
        var isLoginScreen by remember { mutableStateOf(true) }

        if (isUserLoggedIn) {
            HomeScreen()
        } else {
            if (isLoginScreen) {
                LoginScreen(
                    onSignupClick = { isLoginScreen = false },
                    onLoginSuccess = { isUserLoggedIn = true }
                )
            } else {
                SignupScreen(onSignupSuccess = { isLoginScreen = true })
            }
        }
    }

    @Composable
    fun HomeScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Welcome to Job Portal", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(20.dp))
            // Additional components such as job listings can be added here
        }
    }

    @Composable
    fun LoginScreen(
        onSignupClick: () -> Unit,
        onLoginSuccess: () -> Unit
    ) {
        var username by remember { mutableStateOf(TextFieldValue("")) }
        var password by remember { mutableStateOf(TextFieldValue("")) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Login", style = MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Enter Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = { login(username.text, password.text, onLoginSuccess) }) {
                    Text(text = "Login")
                }
                Button(onClick = { onSignupClick() }) {
                    Text(text = "Sign Up")
                }
            }
        }
    }

    private fun login(email: String, password: String, onLoginSuccess: () -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Login successful.", Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                } else {
                    Toast.makeText(baseContext, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    @Composable
    fun SignupScreen(onSignupSuccess: () -> Unit) {
        var username by remember { mutableStateOf(TextFieldValue("")) }
        var password by remember { mutableStateOf(TextFieldValue("")) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Sign Up", style = MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Enter Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(onClick = { signup(username.text, password.text, onSignupSuccess) }) {
                Text(text = "Sign Up")
            }
        }
    }

    private fun signup(email: String, password: String, onSignupSuccess: () -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Signup successful. A verification email has been sent.", Toast.LENGTH_SHORT).show()
                    auth.currentUser?.sendEmailVerification()
                    onSignupSuccess()
                } else {
                    Toast.makeText(baseContext, "Signup failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        JobPortalTheme {
            MainScreen()
        }
    }
}
