package ipt.pt.dam2025.teste_002.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import ipt.pt.dam2025.teste_002.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationFragment : Fragment() {

    private lateinit var txtLocation: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_location, container, false)
        txtLocation = view.findViewById(R.id.txtLocation)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        getLocation()
        return view
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                txtLocation.text = "Lat: ${it.latitude}, Lon: ${it.longitude}"
            } ?: run {
                txtLocation.text = "Localização não disponível"
            }
        }
    }
}
