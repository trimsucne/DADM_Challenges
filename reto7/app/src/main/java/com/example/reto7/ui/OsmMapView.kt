package com.example.reto7.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.reto7.data.PoiItem
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun OsmMapView(
    userLat: Double,
    userLon: Double,
    nearbyPois: List<Pair<PoiItem, Double>>,
    onPoiSelected: (Pair<PoiItem, Double>) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
        Configuration.getInstance().userAgentValue = context.packageName
        onDispose { }
    }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(14.0)
        }
    }

    AndroidView(
        factory = { mapView },
        update = { map ->
            map.overlays.clear()

            // User Location Marker
            val userPoint = GeoPoint(userLat, userLon)
            map.controller.setCenter(userPoint)

            val userMarker = Marker(map).apply {
                position = userPoint
                title = "Tu Ubicación GPS"
                snippet = "Lat: %.4f, Lon: %.4f".format(userLat, userLon)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            map.overlays.add(userMarker)

            // POI Markers
            nearbyPois.forEach { pair ->
                val poi = pair.first
                val distance = pair.second
                val poiPoint = GeoPoint(poi.latitude, poi.longitude)

                val poiMarker = Marker(map).apply {
                    position = poiPoint
                    title = poi.name
                    snippet = "${poi.category} • %.2f km".format(distance)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    setOnMarkerClickListener { _, _ ->
                        onPoiSelected(pair)
                        showInfoWindow()
                        true
                    }
                }
                map.overlays.add(poiMarker)
            }

            map.invalidate()
        },
        modifier = modifier.fillMaxSize()
    )
}
