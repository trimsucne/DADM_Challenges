package com.example.reto8.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.reto8.data.PosconsumoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

val ESRI_WORLD_STREET_TILE_SOURCE = object : OnlineTileSourceBase(
    "EsriWorldStreetMap",
    0,
    19,
    256,
    ".png",
    arrayOf(
        "https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/",
        "https://services.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/"
    )
) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        return baseUrl + MapTileIndex.getZoom(pMapTileIndex) + "/" +
                MapTileIndex.getY(pMapTileIndex) + "/" +
                MapTileIndex.getX(pMapTileIndex)
    }
}

@Composable
fun OsmMapView(
    puntos: List<PosconsumoPoint>,
    onPointSelected: (PosconsumoPoint) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val mapView = remember {
        val appUserAgent = "Reto8DatosGovApp/1.0 (com.example.reto8; Android)"
        Configuration.getInstance().userAgentValue = appUserAgent
        Configuration.getInstance().additionalHttpRequestProperties["User-Agent"] = appUserAgent
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))

        MapView(context).apply {
            setTileSource(ESRI_WORLD_STREET_TILE_SOURCE)
            setMultiTouchControls(true)
            controller.setZoom(6.0)
            controller.setCenter(GeoPoint(4.5709, -74.2973)) // Colombia center
        }
    }

    AndroidView(
        factory = { mapView },
        update = { map ->
            map.overlays.clear()

            val validPoints = puntos.filter { it.latitud != 0.0 && it.longitud != 0.0 }

            if (validPoints.isNotEmpty()) {
                val firstPoint = validPoints.first()
                map.controller.setCenter(GeoPoint(firstPoint.latitud, firstPoint.longitud))
                if (validPoints.size == 1) {
                    map.controller.setZoom(14.0)
                } else {
                    map.controller.setZoom(7.5)
                }
            }

            validPoints.forEach { pt ->
                val pointGeo = GeoPoint(pt.latitud, pt.longitud)
                val marker = Marker(map).apply {
                    position = pointGeo
                    title = pt.nombrePunto
                    snippet = "${pt.tipoResiduo} • ${pt.municipio}, ${pt.departamento}"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    setOnMarkerClickListener { _, _ ->
                        onPointSelected(pt)
                        showInfoWindow()
                        true
                    }
                }
                map.overlays.add(marker)
            }

            map.invalidate()
        },
        modifier = modifier.fillMaxSize()
    )
}
