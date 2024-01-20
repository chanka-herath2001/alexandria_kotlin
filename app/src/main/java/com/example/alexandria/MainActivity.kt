package com.example.alexandria
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.os.PowerManager;



class BookDetailActivity : AppCompatActivity() {

    private lateinit var bookImage: ImageView
    private lateinit var bookTitle: TextView
    private lateinit var bookAuthor: TextView
    private lateinit var bookDescription: TextView
    private lateinit var bookGenre: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)

        bookImage = findViewById(R.id.bookImage)
        bookTitle = findViewById(R.id.bookTitle)
        bookAuthor = findViewById(R.id.bookAuthor)
        bookDescription = findViewById(R.id.bookDescription)
        bookGenre = findViewById(R.id.bookGenre)


        val book = intent.getSerializableExtra("Book") as? Book

        if (book != null) {
            Glide.with(this)
                .load(book.image)
                .into(bookImage)

            bookTitle.text = book.title
            bookAuthor.text = book.author
            bookDescription.text = book.description
            bookGenre.text = book.genre
        } else {
            Toast.makeText(this, "Error loading book details", Toast.LENGTH_SHORT).show()
        }
    }
}

class ProfileActivity: AppCompatActivity() {

    companion object {
        const val GALLERY_REQUEST_CODE = 100
        const val CAMERA_REQUEST_CODE = 101
    }

    private lateinit var profile: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        profile = findViewById(R.id.profile)

        profile.clipToOutline = true

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        profile.setOnClickListener {
            val options = arrayOf<CharSequence>("Gallery", "Camera")
            val builder = AlertDialog.Builder(this@ProfileActivity)
            builder.setTitle("Choose Image Source")
            builder.setItems(options) { dialog, item ->
                when (item) {
                    0 -> {
                        // Choose from gallery
                        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(pickPhoto, GALLERY_REQUEST_CODE)
                    }
                    1 -> {
                        // Capture from camera
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
                        } else {
                            val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            if (takePicture.resolveActivity(packageManager) != null) {
                                startActivityForResult(takePicture, CAMERA_REQUEST_CODE)
                            }
                        }
                    }
                }
            }
            builder.show()
        }


        bottomNavigationView.selectedItemId = R.id.navigation_profile

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))

                    true
                }
                R.id.navigation_collection -> {
                    startActivity(Intent(this, CollectionActivity::class.java))

                    true
                }
                R.id.navigation_profile -> {

                    true
                }
                R.id.navigation_notification -> {
                    startActivity(Intent(this, NotificationActivity::class.java))

                    true
                }

                else -> false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    // Handle the image chosen from the gallery
                    val selectedImage = data?.data
                    profile.setImageURI(selectedImage)
                }
                CAMERA_REQUEST_CODE -> {
                    // Handle the image captured from the camera
                    val photo = data?.extras?.get("data")
                    if (photo is Bitmap) {
                        profile.setImageBitmap(photo)
                    }
                }
            }
        }
    }
}

class CollectionActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)


        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.selectedItemId = R.id.navigation_collection

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))

                    true
                }
                R.id.navigation_collection -> {

                    true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))

                    true
                }
                R.id.navigation_notification -> {
                    startActivity(Intent(this, NotificationActivity::class.java))

                    true
                }

                else -> false
            }
        }
    }
}

class NotificationActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.selectedItemId = R.id.navigation_notification

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))

                    true
                }
                R.id.navigation_collection -> {
                    startActivity(Intent(this, CollectionActivity::class.java))

                    true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))

                    true
                }
                R.id.navigation_notification -> {

                    true
                }

                else -> false
            }
        }
    }
}

class SignupActivity: AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firstNameField: EditText
    private lateinit var lastNameField: EditText
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance()

        // Initialize UI components
        firstNameField = findViewById(R.id.firstNameField)
        lastNameField = findViewById(R.id.lastNameField)
        emailField = findViewById(R.id.emailField)
        passwordField = findViewById(R.id.passwordField)
        signupButton = findViewById(R.id.signup)

        // Set click listener for signup button
        signupButton.setOnClickListener { signupUser() }
    }
    private fun signupUser() {
        val firstName = firstNameField.text.toString()
        val lastName = lastNameField.text.toString()
        val email = emailField.text.toString()
        val password = passwordField.text.toString()

        // Firebase authentication to create a user with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up successful
                    Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
                    // You can navigate to another activity or perform additional actions here
                    startActivity(Intent(this@SignupActivity, HomeActivity::class.java))
                } else {
                    // If sign up fails, display a message to the user.
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

}


class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookList: ArrayList<Book>
    private lateinit var bookAdapter: BookAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activiy_home)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)



        bookList = ArrayList()
        bookAdapter = BookAdapter(this, bookList)
        recyclerView.adapter = bookAdapter

        val databaseReference = FirebaseDatabase.getInstance().getReference("Books")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookList.clear()
                for (dataSnapshot in snapshot.children) {
                    val book = dataSnapshot.getValue(Book::class.java)
                    if (book != null) {
                        bookList.add(book)
                    }
                }
                bookAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, "Error fetching data", Toast.LENGTH_SHORT).show()
            }
        })
        bookAdapter.listener = object : BookAdapter.OnItemClickListener {
            override fun onItemClick(book: Book) {
                val intent = Intent(this@HomeActivity, BookDetailActivity::class.java)
                intent.putExtra("Book", book)
                startActivity(intent)
            }
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.selectedItemId = R.id.navigation_home

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {

                    true
                }
                R.id.navigation_collection -> {
                    startActivity(Intent(this, CollectionActivity::class.java))


                    true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))

                    true
                }
                R.id.navigation_notification -> {
                    startActivity(Intent(this, NotificationActivity::class.java))

                    true
                }

                else -> false
            }
        }
    }



}



class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var signupButton: TextView
    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null
    private lateinit var powerManager: PowerManager
    private lateinit var wakeLock: PowerManager.WakeLock


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance()

        // Initialize UI components
        emailField = findViewById(R.id.emailField)
        passwordField = findViewById(R.id.passwordField)
        loginButton = findViewById(R.id.loginButton)
        signupButton = findViewById(R.id.signupButton)

        // Initialize Sensor Manager and Proximity Sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK,
            "example:tag"
        )

        if (proximitySensor == null) {
            Toast.makeText(this, "No Proximity Sensor Found!", Toast.LENGTH_SHORT).show()
        }

        // Set click listener for login button
        loginButton.setOnClickListener { loginUser() }

        signupButton.setOnClickListener {
            try {
                val intent = Intent(this@MainActivity, SignupActivity::class.java)
                Log.d("IntentCreation", "Intent created successfully")
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("IntentCreation", "Error creating intent: ${e.message}")
            }
        }
    }

    private fun loginUser() {
        val email = emailField.text.toString()
        val password = passwordField.text.toString()

        // Firebase authentication
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login successful
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    // You can navigate to another activity or perform additional actions here
                    startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun logoutUser() {
        mAuth.signOut()
        // Redirect or update UI as needed
    }

    override fun onSensorChanged(event: SensorEvent) {
        proximitySensor?.let {
            if (event.values[0] < it.maximumRange) {
                // Detected something nearby
                Toast.makeText(this, "Near", Toast.LENGTH_SHORT).show()
                turnOffScreen()
            } else {
                // Nothing is nearby
                Toast.makeText(this, "Far", Toast.LENGTH_SHORT).show()
                turnOnScreen()
            }
        }
    }
    private fun turnOffScreen() {
        if (!wakeLock.isHeld) {
            wakeLock.acquire()
        }
    }

    private fun turnOnScreen() {
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}
