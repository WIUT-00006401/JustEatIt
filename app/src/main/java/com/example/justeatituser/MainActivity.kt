package com.example.justeatituser

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.justeatituser.Common.Common
import com.example.justeatituser.Model.UserModel
import com.example.justeatituser.Remote.ICloudFunctions
import com.example.justeatituser.Remote.RetrofitCloudClient
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import dmax.dialog.SpotsDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class MainActivity : AppCompatActivity() {

    private var placeSelected: Place?=null
    private var places_fragment:AutocompleteSupportFragment?=null
    private lateinit var placeClient: PlacesClient
    private val placeFields = Arrays.asList(Place.Field.ID,
        Place.Field.NAME,
        Place.Field.ADDRESS,
        Place.Field.LAT_LNG)

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var listener: FirebaseAuth.AuthStateListener
    private lateinit var dialog: AlertDialog
    private val compositeDisposable = CompositeDisposable()
    private lateinit var cloudFunctions: ICloudFunctions

    private lateinit var userRef: DatabaseReference
    private var providers:List<AuthUI.IdpConfig>? = null

    companion object {
        private val APP_REQUEST_CODE = 7171
    }



    override fun onStop() {
        if(listener!=null)
            firebaseAuth.removeAuthStateListener (listener)
        compositeDisposable.clear()
        super.onStop()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {

        //Places.initialize(this,getString(com.google.firebase.database.R.string.google_maps_key))
        Places.initialize(this,getString(R.string.google_maps_key))
        placeClient = Places.createClient(this)

        providers = Arrays.asList<AuthUI.IdpConfig>(
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        userRef = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCE)
        firebaseAuth = FirebaseAuth.getInstance()
        dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()


        listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            Dexter.withActivity(this@MainActivity)
                .withPermissions(Arrays.asList(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA
                ))
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted())
                        {
                            val user = firebaseAuth.currentUser
                            if (user != null){
                                checkUserFromFirebase(user!!)
                            }else{
                                phoneLogin()
                            }
                        }else
                            Toast.makeText(this@MainActivity, "Please accept all permissions", Toast.LENGTH_SHORT).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        
                    }

                }

                    ).check()
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener (listener )
    }

    private fun checkUserFromFirebase(user: FirebaseUser){
        dialog!!.show()
        userRef!!.child(user!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Toast.makeText(this@MainActivity, ""+p0.message, Toast.LENGTH_SHORT).show()
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists()){

                        FirebaseAuth.getInstance().currentUser!!
                            .getIdToken(true)
                            .addOnFailureListener{t->
                                Toast.makeText(this@MainActivity, ""+t.message,Toast.LENGTH_SHORT).show()
                            }
                            .addOnCompleteListener{
                                Common.authorizeToken = it.result!!.token


                                dialog.dismiss()
                                val userModel = p0.getValue(UserModel::class.java)
                                goToHomeActivity(userModel)
                            }
                    }else{
                        dialog!!.dismiss()
                        showRegisterDialog(user!!)
                    }

                }
            })
    }

    private fun showRegisterDialog(user: FirebaseUser){
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Register")
        builder.setMessage("Please fill Information")

        val itemView = LayoutInflater.from(this@MainActivity)
            .inflate(R.layout.layout_register, null)


        val phone_input_layout = itemView.findViewById<TextInputLayout>(R.id.phone_input_layout)
        val edt_name = itemView.findViewById<EditText>(R.id.edt_name)
        val txt_address = itemView.findViewById<TextView>(R.id.txt_address_detail)
        val edt_phone = itemView.findViewById<EditText>(R.id.edt_phone)

        //places_fragment = supportFragmentManager.findFragmentById(com.google.firebase.database.R.id.places_autocomplete_fragment)
        places_fragment = supportFragmentManager.findFragmentById(R.id.places_autocomplete_fragment)
                as AutocompleteSupportFragment
        places_fragment!!.setPlaceFields(placeFields)
        places_fragment!!.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place) {
                placeSelected = p0
                txt_address.text = placeSelected!!.address
            }

            override fun onError(p0: Status) {
                Toast.makeText(this@MainActivity,""+p0.statusMessage,Toast.LENGTH_SHORT).show()
            }

        })

        //Set
        if (user.phoneNumber == null || TextUtils.isEmpty(user.phoneNumber))
        {
            phone_input_layout.hint = "Email"
            edt_phone.setText(user.email)
            edt_name.setText(user.displayName)
        }
        else
            edt_phone.setText(user!!.phoneNumber)


        builder.setView(itemView)
        builder.setNegativeButton("Cancel"){dialogInterface, i -> dialogInterface.dismiss()}
        builder.setPositiveButton("Register"){dialogInterface, i->

            if (placeSelected != null) {
                if (TextUtils.isDigitsOnly(edt_name.text.toString())) {
                    Toast.makeText(this@MainActivity, "Please, enter your name", Toast.LENGTH_SHORT)
                        .show()
                    return@setPositiveButton
                }

                val userModel = UserModel()
                userModel.uid = user!!.uid
                userModel.name = edt_name.text.toString()
                userModel.address = txt_address.text.toString()
                userModel.phone = edt_phone.text.toString()
                userModel.lat = placeSelected!!.latLng!!.latitude
                userModel.lng = placeSelected!!.latLng!!.longitude

                userRef!!.child(user!!.uid)
                    .setValue(userModel)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            FirebaseAuth.getInstance().currentUser!!
                                .getIdToken(true)
                                .addOnFailureListener{t->
                                    Toast.makeText(this@MainActivity,""+t.message,Toast.LENGTH_SHORT).show()
                                }
                                .addOnCompleteListener {
                                    Common.authorizeToken = it!!.result!!.token

                                    //val headers = HashMap<String, String>()
                                    //headers.put("Authorization",Common.buildToken(Common.authorizeToken!!))

                                    dialogInterface.dismiss()
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Congratulation! Register success",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    goToHomeActivity(userModel)

                                }
                        }
                    }
            }
            else
            {
                Toast.makeText(this@MainActivity,"Please select address", Toast.LENGTH_SHORT).show()
            }
        }

        val dialog = builder.create()
        dialog.setOnDismissListener{
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.remove(places_fragment!!)
            fragmentTransaction.commit()
        }
        dialog.show()
    }

    private fun goToHomeActivity(userModel: UserModel?){

        FirebaseInstanceId.getInstance()
            .instanceId
            .addOnFailureListener{e->Toast.makeText(this@MainActivity, ""+e.message,Toast.LENGTH_SHORT).show()
                Common.currentUser = userModel!!

                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                finish()
            }
            .addOnCompleteListener{task ->
                if (task.isSuccessful)
                {
                    Common.currentUser = userModel!!
                    Common.updateToken(this@MainActivity,task.result!!.token)
                    startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                    finish()
                }
            }


    }

    private fun phoneLogin(){
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
            .setTheme(R.style.LoginTheme)
            .setLogo(R.drawable.logo)
            .setAvailableProviders(providers!!).build(), APP_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_REQUEST_CODE){
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK){
                val user = FirebaseAuth.getInstance().currentUser
            }
            else{
                Toast.makeText(this, "Failed to sign in", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
