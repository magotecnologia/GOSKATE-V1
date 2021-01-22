package go.goskate.goskate.ui.maps


import android.content.res.Resources
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import go.goskate.goskate.R
import go.goskate.goskate.ui.viewmodel.MapsViewModel
import kotlinx.android.synthetic.main.maps_fragment.*

class Maps : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private var mapView: MapView? = null
    private var navController: NavController? = null
    private lateinit var viewModel: MapsViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.maps_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MapsViewModel::class.java)
        navController = Navigation.findNavController(view)


        mapView = view.findViewById(R.id.mapFrameLayout) as MapView
        mapView!!.onCreate(savedInstanceState)
        mapView!!.onResume()
        mapView!!.getMapAsync(this)

        floatingActionButton.setOnClickListener {
            navController!!.navigate(R.id.action_maps_to_newSpot2)
        }

    }


    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap.let {
            mMap = it!!
        }
        mMap.setOnMarkerClickListener(this)

        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this.context, R.raw.mapstyle)
            )
            if (!success) {
                Log.e("oo", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("oo", "Can't find style. Error: ", e)
        }

        val tercerMilenio = LatLng(4.648671, -74.120623)
        val tunal = LatLng(4.571306, -74.136941)
        val movistar = LatLng(4.649959, -74.077532)
        val bowl72 = LatLng(4.663218, -74.066717)
        val toberin = LatLng(4.743526, -74.039515)
        val fontanar = LatLng(4.756818, -74.111592)
        val flores = LatLng(4.753979, -74.101135)
        val tibabuyes = LatLng(4.734838, -74.107224)
        val margaritas = LatLng(4.734838, -74.107224)
        val nuevoMilenio = LatLng(4.529037, -74.115449)
        val palermo = LatLng(4.541913, -74.109892)
        val francia = LatLng(4.622971, -74.111631)

        val spots = listOf(
            tercerMilenio,
            tunal,
            movistar,
            bowl72,
            toberin,
            fontanar,
            flores,
            tibabuyes,
            margaritas,
            nuevoMilenio,
            palermo,
            francia
        )
        val zoomLevel = 11.7f
        val cameraPlace = LatLng(4.622971, -74.111631)

        spots.forEach { place ->
            mMap.addMarker(MarkerOptions().position(place).title("loveu"))
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPlace, zoomLevel))
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        TODO("Not yet implemented")
    }

}